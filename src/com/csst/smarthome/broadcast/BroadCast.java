package com.csst.smarthome.broadcast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import com.lishate.data.GobalDef;

public class BroadCast implements Runnable {

	private Socket deviceSocket;
	private boolean flag = false;
	
	public void SendDevice(byte[] buf, int timespan){
		DatagramSocket socket = null;  
        DatagramPacket packet;  
        
       
        	try{
		        socket = new DatagramSocket();  
		        socket.setBroadcast(true); 
		        packet = new DatagramPacket(buf,buf.length,InetAddress.getByName("255.255.255.255"),GobalDef.BROADCAST_PORT);  
		        socket.send(packet);
		        
        	}
        	catch(Exception e){
        		
        	}
        	finally{
        		if(socket != null){
        			socket.close();
        		}
        	}
        
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
