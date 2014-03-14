package com.ancached.db;

import android.text.format.Time;

public class TrackLogItem {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private String url;
	private String title;
	private Time vTime;
	private int netState;
	private String location;
	
	public TrackLogItem(String url, String title, Time vTime, int netState, String location){
		this.url = url;
		this.title = title;
		this.vTime = vTime;
		this.netState = netState;
		this.setLocation(location);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Time getvTime() {
		return vTime;
	}

	public void setvTime(Time vTime) {
		this.vTime = vTime;
	}

	public int getNetState() {
		return netState;
	}

	public void setNetState(int netState) {
		this.netState = netState;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
