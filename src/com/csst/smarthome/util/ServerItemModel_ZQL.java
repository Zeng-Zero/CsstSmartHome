package com.csst.smarthome.util;


import java.io.Serializable;

import com.lishate.data.model.ServerItemModel;

public class ServerItemModel_ZQL implements Serializable{

	/**
	 * 
	 */
	
	private long id;
	String ipaddress;
	int port;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public boolean CheckIsEqual(ServerItemModel sim){
		if(ipaddress.equals(sim.getIpaddress()) && port == sim.getPort()){
			return true;
		}
		return false;
	}
}
