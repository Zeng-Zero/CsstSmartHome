package com.csst.smarthome.safe;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.util.Log;

import com.csst.smarthome.util.CsstSHDBColumnUtil;
import com.lishate.data.model.ServerItemModel;
import com.lishate.encryption.Encryption;
import com.lishate.message.baseMessage;
//自己用的没有加密的用来报警用的数据
public class UdpProcess_ZQL {
	
	public final static int SEND_SUCESS = 0;
	public final static int SEND_FAIL = 1;
	public final static int RECV_SUCESS = 2;
	public final static int RECV_FAIL = 3;
	public final static int DATA_UDP_FAIL = 4;
	private static final String TAG = "UdpProcess_ZQL_SAFE";

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
			System.out.println(" UdpProcess_ZQL_SAFE waiting for data from control in ");
			ds.receive(packet);
//			System.out.println(" UdpProcess_ZQL_SAFE get the message is  "+CsstSHDBColumnUtil.bufferToMessge(packet.getData()));
//			System.out.println(" UdpProcess_ZQL_SAFE waiting for data from new string  "+new String(packet.getData())+"zengqinglin");//数据OK
			byte[] getbuf = new byte[packet.getLength()] ;
			for(int i=0;i<packet.getLength();i++){
//				System.out.println(" UdpProcess_ZQL_SAFE get the message the [  "+i+"]"+packet.getData()[i]);
				getbuf[i]= packet.getData()[i];
			}
			
//			System.out.println(" UdpProcess_ZQL_SAFE waiting for data from new string  "+new String(getbuf)+"  ZENGQINLIN3");
			
			System.out.println(" UdpProcess_ZQL_SAFE reveive is OK  ");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			packet = null;
		}
		return packet;
		
	}
	
	public static int SendMsg(DatagramSocket ds, baseMessage msg, ServerItemModel si){
		byte[] buf = Encryption.GetMsg2Buf(msg);// Encryption.Encryption(msg);
		return UdpProcess_ZQL.Send(ds, si.getIpaddress(), si.getPort(), buf);
	}
	
	public static DatagramPacket RecvMsg(DatagramSocket ds){
		DatagramPacket packet = Recv(ds);
//		baseMessage bm = null;
		if(packet != null){
			/*
			if(Encryption.checkIsMsg(packet.getData()) == 1){
				bm = Encryption.Decryption(packet.getData());
			}
			*/
			//直接将数据返回给给safe 因为safe的发送的明码不需要加密来就行解密
//			bm = Encryption.GetMsg(packet.getData());
		}
		return packet;
	}
	
	public static DatagramPacket GetMsgReturn(baseMessage sendMsg, ServerItemModel si){
		DatagramSocket ds ;//= new DatagramSocket();
		Log.d(TAG, "GetMsgReturn set the timeout is 1S and sendcmd 3 times 3 times  " );
		//返回的数据为明码 用来返回数据用
		DatagramPacket packet = null;
		int temptimeout = 5*1000;
		try {
			ds = new DatagramSocket();
			
			ds.setSoTimeout(temptimeout/*GobalDef.Instance.getUdpReadTimeOut()*/);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		//只查询一次。要不然上面等待太久。有上层决定查询多少次
		int flag = 3;//12;
		packet = null;
		while(flag > 0){
			if(SendMsg(ds, sendMsg,si) == SEND_SUCESS){
				try {
					ds.setSoTimeout(temptimeout /*GobalDef.Instance.getUdpReadTimeOut()*/);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				packet = RecvMsg(ds);
				while(true){
					//已经接收到了数据
					if(packet != null){
						//已经接收到了数据但是不符重新接收
							//已经接收到了数据完成
							System.out.println("GetMsgReturn get msg is ok bm is not null");
							break;
						}else{
							System.out.println("GetMsgReturn bm is  null");
							break;
						}
				
				}
				
			}
			flag--;
			if(packet != null){
				System.out.println("GetMsgReturn get msg is ok send to packet ");
				break;
			}
		
		}
		return packet;
	}
	
}
