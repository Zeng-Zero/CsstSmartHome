package com.csst.smarthome.bean;

import java.io.Serializable;

public class CsstDeviceTypeBean implements Serializable {
	private static final long serialVersionUID = 4098675060187321075L;
	private int key;
	private String name;
    public CsstDeviceTypeBean(int key,String name){
    	this.key=key;
    	this.name=name;
    }
	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


}
