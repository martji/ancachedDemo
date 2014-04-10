package com.example.model;

public class Featuer {
	private STime time;
	private String location;
	private int netState;
	
	public Featuer(STime time, String location, int netState){
		this.time = time;
		this.location = location;
		this.netState = netState;
	}
	public STime getTime() {
		return time;
	}
	public void setTime(STime time) {
		this.time = time;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getNetState() {
		return netState;
	}
	public void setNetState(int netState) {
		this.netState = netState;
	}
	
}
