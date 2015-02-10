package com.csst.smarthome.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.util.Log;

import com.lishate.data.GobalDef;
import com.lishate.data.model.ServerItemModel;
import com.lishate.encryption.BaseCodeMessage;
import com.lishate.encryption.CodeMsg;

public class UdpJavaEncodingProcess_ZQL {

	public final static int SEND_SUCESS = 0;
	public final static int SEND_FAIL = 1;
	public final static int RECV_SUCESS = 2;
	public final static int RECV_FAIL = 3;
	public final static int DATA_UDP_FAIL = 4;
	private static final String TAG = "UdpProcess";

	public static int Send(DatagramSocket ds, String destip, int destport, byte[] buf){
		int result = SEND_SUCESS;
		InetAddress address ;
		try {
			address = InetAddress.getByName(destip);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = SEND_FAIL;
			return result;
		}
		DatagramPacket dp = new DatagramPacket(buf, buf.length, address, destport);
		try {
			Log.d(TAG, "send buf length is: " + buf.length + " address " + address.toString() + " Port is: " + destport);
			ds.send(dp);
			result = SEND_SUCESS;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			result = SEND_FAIL;
			e.printStackTrace();
		}
		catch(Throwable te){
			if(te.getMessage() != null){
				Log.d(TAG, "send te msg : " + te.getMessage());
			}
			te.printStackTrace();
			result = SEND_FAIL;
		}
		return result;
	}
	
	public static DatagramPacket Recv(DatagramSocket ds){
		
		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		try {
			System.out.println(" waiting for data from control in ");
			ds.receive(packet);
			System.out.println(" waiting for data from control out ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			packet = null;
		}
		return packet;
		
	}
	
	public static int SendMsg(DatagramSocket ds, BaseCodeMessage msg, ServerItemModel si){
		//byte[] buf = Encryption.GetMsg2Buf(msg);// Encryption.Encryption(msg);
		byte[] buf = CodeMsg_ZQL.getEncodeInfo(msg);
		return UdpJavaEncodingProcess_ZQL.Send(ds, si.getIpaddress(), si.getPort(), buf);
	}
	
	public static BaseCodeMessage RecvMsg(DatagramSocket ds){
		DatagramPacket packet = Recv(ds);
		BaseCodeMessage bm = null;
		if(packet != null){
			/*
			if(Encryption.checkIsMsg(packet.getData()) == 1){
				bm = Encryption.Decryption(packet.getData());
			}
			*/
			//bm = Encryption.GetMsg(packet.getData());
			//packet.getLength()
			for(int i=0;i<packet.getLength();i++){
				System.out.println("the get msg is buf["+i+"]"+packet.getData()[i]);
			}
			bm = CodeMsg_ZQL.getDecodeInfo(packet.getData(),packet.getLength());
		}
		return bm;
	}
	
	public static BaseCodeMessage GetMsgReturn(BaseCodeMessage sendMsg, ServerItemModel si){
		DatagramSocket ds ;//= new DatagramSocket();
		System.out.println("GetMsgReturn set the timeout 5S and sendcmd flag 3 time" );
		int squflag =3;
		int temptimeout = 5*1000;
		try {
			ds = new DatagramSocket();
			
			ds.setSoTimeout(temptimeout/*GobalDef.Instance.getUdpReadTimeOut()*/);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		int flag = 3;
		BaseCodeMessage bm = null;
		while(flag > 0){
			if(SendMsg(ds, sendMsg,si) == SEND_SUCESS){
				try {
					ds.setSoTimeout(temptimeout /*GobalDef.Instance.getUdpReadTimeOut()*/);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bm = RecvMsg(ds);
				while(true){
					//已经接收到了数据
					if(bm != null){
						//已经接收到了数据但是不符重新接收
						if(bm.Seq != sendMsg.Seq){
							squflag--;//如果序序列号不对的话 就重发squflag 次。还是不行就退出以免重新发死机
							System.out.println("GetMsgReturn set the timeout 99999999999 9999squflag-- " );
							if(squflag==0){
								bm=null;
								break;
							}
							System.out.println("GetMsgReturn get msg is ok bm is not null but bm.Seq != sendMsg.Seq");
							try {
								ds.setSoTimeout(GobalDef.Instance.getUdpReadTimeOut());
							} catch (SocketException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println("GetMsgReturn get msg is ok bm is not null bm.Seq != sendMsg.Seq repeat agian ");
							bm = RecvMsg(ds);
						}else{
							//已经接收到了数据完成
							System.out.println("GetMsgReturn get msg is ok bm is not null");
							break;
						}
					}else{
						System.out.println("GetMsgReturn bm is  null");
						break;
					}
				}
				
				
				flag--;
				Log.d(TAG, " 1 flag is: " + flag);
				System.out.println("GetMsgReturn 1 flag is: " + flag);
				
				if(bm != null){
					System.out.println("GetMsgReturn in not null" );
					Log.d(TAG, "bm is not null");
					break;
				}
			}
			/*
			while((bm != null) && (bm.getSeq() != sendMsg.getSeq())){
				try {
					ds.setSoTimeout(GobalDef.Instance.getUdpReadTimeOut());
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				bm = RecvMsg(ds);
			}
			*/
			//return bm;
		}
		return bm;
	}
}
