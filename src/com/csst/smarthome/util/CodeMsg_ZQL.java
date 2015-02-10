package com.csst.smarthome.util;

import java.util.Random;

import com.lishate.encryption.BaseCodeMessage;

public class CodeMsg_ZQL {
	
	private static short _seq = 0;
	private static Object seqsyn = new Object();
	public static short getSeq(){
		synchronized(seqsyn){
			_seq++;
			
			return _seq;
		}
	}
	
	private static Random random = new Random();
	
	
	
	private static void Encode(byte[] src, int srcindex, int length, byte[] dst, int dstindex){
		for(int i = srcindex; i<length; i=i+2){
			byte src_1 = src[i];
			byte src_2 = src[i+1];
			
			byte dst_1 = 0;
			byte dst_2 = 0;
			byte dst_3 = 0;
			byte dst_4 = 0;
			
			byte temp = 0;
			dst_1 = (byte) random.nextInt(128);
			temp = (byte) (dst_1 % 4);
			switch(temp){
			case 0:
				dst_2 = (byte) (src_1 ^ dst_1);
				dst_3 = (byte)random.nextInt(128);
				dst_4 = (byte)(src_2 ^ dst_3);
				break;
			case 1:
				dst_2 = (byte) random.nextInt(128);
				dst_3 = (byte) (src_1 ^ dst_1);
				dst_4 = (byte) (src_2 ^ dst_2);
				break;
			case 2:
				dst_2 = (byte) random.nextInt(128);
				dst_3 = (byte) (src_1 ^ dst_2);
				dst_4 = (byte) (src_2 ^ dst_1);
				break;
			case 3:
				dst_3 = (byte) random.nextInt(128);
				dst_2 = (byte) (src_2 ^ dst_3);
				dst_4 = (byte) (src_1 ^ dst_1);
				break;
			}
			
			dst[dstindex + 0 + (i - srcindex) * 2] = dst_1;
			dst[dstindex + 1 + (i - srcindex) * 2] = dst_2;
			dst[dstindex + 2 + (i - srcindex) * 2] = dst_3;
			dst[dstindex + 3 + (i - srcindex) * 2] = dst_4;
		}
	}
	
	private static void Decode(byte[] src, int srcindex, int length, byte[] dst, int dstindex){
		for(int i = srcindex; i <length; i=i+4){
			byte src_1 = src[i];
			byte src_2 = src[i + 1];
			byte src_3 = src[i + 2];
			byte src_4 = src[i + 3];
			
			byte dst_1 = 0;
			byte dst_2 = 0;
			
			int  temp = (src_1 & 0xFF);
			temp = (byte) (temp % 4);
			switch(temp){
			case 0:
				dst_1 = (byte) (src_1 ^ src_2);
				dst_2 = (byte) (src_3 ^ src_4);
				break;
			case 1:
				dst_1 = (byte) (src_1 ^ src_3);
				dst_2 = (byte) (src_2 ^ src_4);
				break;
			case 2:
				dst_1 = (byte) (src_2 ^ src_3);
				dst_2 = (byte) (src_1 ^ src_4);
				break;
			case 3:
				dst_1 = (byte) (src_1 ^ src_4);
				dst_2 = (byte) (src_2 ^ src_3);
				break;
			}
			
			dst[dstindex + 0 + (i-srcindex) / 2] = dst_1;
			dst[dstindex + 1 + (i-srcindex) / 2] = dst_2;
		}
	}
	
	public static byte getBaseCodeCheckSum(byte[] src){
		int result = 0;
		for( int i = 1; i<src.length - 2; i++){
			result = result + (src[i] & 0xFF);
		}
		return (byte)result;
	}
	
	public static BaseCodeMessage getDecodeInfo(byte[] src, int inlength){
		BaseCodeMessage bcm = null;
		
		if(src[0] != 0x00){
			return bcm;
		}
		int length = src[1];
		if(inlength != length){
			return bcm;
		}
		if(src[length -1] != (byte)0xFF){
			return bcm;
		}
		byte checksum = getBaseCodeCheckSum(src);
		/*
		if(src[length - 2] != checksum){
			return bcm;
		}
		*/
		bcm = new BaseCodeMessage();
		byte[] tempbuf = new byte[(length - 5) / 2];
//		Decode(src,3,length - 5,tempbuf,0);
		tempbuf = src;
		bcm.Seq = tempbuf[0] | tempbuf[1] << 8;
		bcm.FromType = (tempbuf[2] >> 5) & 0xFF;
		bcm.ToType = (tempbuf[2] >> 2 & 0x07) & 0xFF;
		bcm.Direct = (tempbuf[2] & 0x03) & 0xFF;
		bcm.FromLID = 0;
		bcm.FromHID = 0;
		bcm.ToLID = 0;
		bcm.ToHID = 0;
		for(int i=0; i<4; i++){
			bcm.FromLID = bcm.FromLID | ((tempbuf[4 + i] << (i * 8)) & 0xFF);
			bcm.FromHID = bcm.FromHID | ((tempbuf[8 + i] << (i * 8)) & 0xFF);
			bcm.ToLID = bcm.ToLID | ((tempbuf[12 + i] << (i * 8)) & 0xFF);
			bcm.ToHID = bcm.ToHID | ((tempbuf[16 + i] << (i * 8)) & 0xFF);
		}
		bcm.setMCMD(tempbuf[20]);
		bcm.setSCMD(tempbuf[21]);
		if(tempbuf.length - 22 > 0){
			byte[] content = new byte[tempbuf.length - 22];
			System.arraycopy(tempbuf, 22, content, 0, content.length);
			bcm.setContentBuf(content);
		}
		
		return bcm;
	}
	
	public static byte[] getEncodeInfo(BaseCodeMessage bcm){
		int length = 0;
		int buflength = 0;
		if(bcm.getContentBuf() != null){
			length = length + bcm.getContentBuf().length;
			buflength = bcm.getContentBuf().length;
		}
		int i = 0;
		if(length % 2 != 0){
			length = length + 1;
			buflength = buflength + 1;
			//length = length * 2;
		}
		length = length + 49;
		byte[] buf = new byte[49 + (buflength+1) * 2];
		byte[] tempbuf = new byte[length - 5];
//		byte[] tempbuf = new byte[length];
		buf[0] = 0x00;
		buf[1] = (byte)(49 + (buflength+1) * 2);
		buf[2] = 0x11;
		short seq = (short) bcm.Seq;
		tempbuf[0] = (byte)(seq & 0xFF);
		tempbuf[1] = (byte)((seq >> 8) & 0xFF);
		byte ft = 0;
		ft = (byte)(ft & 0x1F);
		ft = (byte) (ft | bcm.FromType << 5);
		ft = (byte) (ft & 0xE3);
		ft = (byte)(ft | bcm.ToType << 2);
		ft = (byte)(ft & 0xFC);
		ft = (byte)(ft | bcm.Direct);
		tempbuf[2] = ft;
		tempbuf[3] = (byte) (random.nextInt() % 128);
		int flid = bcm.FromLID;
		int fhid = bcm.FromHID;
		int tlid = bcm.ToLID;
		int thid = bcm.ToHID;
		for(i=0; i<4; i++){
			tempbuf[4 + i] = (byte) ((flid >> (i * 8)) & 0xFF);
			tempbuf[8 + i] = (byte)((fhid >> (i * 8)) & 0xFF);
			tempbuf[12 + i] = (byte)((tlid >> (i * 8)) & 0xFF);
			tempbuf[16 + i] = (byte)((thid >> (i * 8)) & 0xFF);
		}
		tempbuf[20] = bcm.getMCMD();
		tempbuf[21] = bcm.getSCMD();
		if(bcm.getContentBuf() != null){
			System.arraycopy(bcm.getContentBuf(), 0, tempbuf, 22, bcm.getContentBuf().length);

		}
			System.out.println(" 8888888888888888888888 the length is "+ length);

//		Encode(tempbuf,0,22 + buflength, buf,3);
		buf[49 + buflength * 2 - 2] = getBaseCodeCheckSum(buf);
		byte bufsend[] =new byte[49 + (buflength+1) * 2];
		bufsend[0]=buf[0];
		bufsend[1]=buf[1];
		bufsend[2]=buf[2];
		for(int m=0;m<tempbuf.length;m++){
//			bufsend[m+3]=tempbuf[m];
			bufsend[m+3]=tempbuf[m];
		}
		
		
		bufsend[49 + (buflength+1) * 2-2] = getBaseCodeCheckSum(bufsend);
		bufsend[49 + (buflength+1) * 2-1] = (byte) 0xFF;
		
		for(int j=0;j<bufsend.length;j++){
			System.out.println("the get sendmsg is buf["+j+"]"+bufsend[j]);
		}
		
		
		buf[49 + buflength * 2 - 1] = (byte) 0xFF;
//		return buf;
		return bufsend;
		
	}
}
