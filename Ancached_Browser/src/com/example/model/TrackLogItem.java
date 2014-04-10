package com.example.model;

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
	private STime vTime;
	private int netState;
	private String location;
	
	public TrackLogItem(String url, String title, String vTime, int netState, String location){
		this.url = url;
		this.title = title;
		this.vTime = new STime(vTime);
		this.netState = netState;
		this.setLocation(location);
	}

	public TrackLogItem(String url, String title, String vTime, int netState) {
		// TODO Auto-generated constructor stub
		this.url = url;
		this.title = title;
		this.vTime = new STime(vTime);
		this.netState = netState;
		this.location = "";
	}

	public TrackLogItem() {
		// TODO Auto-generated constructor stub
		this.url = "----";
		this.title = "New Launch";
		this.vTime = new STime(getTime());
		this.netState = 0;
		this.location = "";
	}

	private String getTime() {
		// TODO Auto-generated method stub
		String stime = "";
		Time t=new Time();
		t.setToNow();
		String year = Integer.toString(t.year);
		String month = Integer.toString(t.month+1);
		String date = Integer.toString(t.monthDay);
		String hour = Integer.toString(t.hour);
		String minute = Integer.toString(t.minute);
		String second = Integer.toString(t.second);
		stime += year + "-" + month + "-" + date + "-" +
				hour + "-" + minute + "-" + second;
		return stime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		if (title == null){
			return "";
		}
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public STime getvTime() {
		return vTime;
	}

	public void setvTime(STime vTime) {
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
