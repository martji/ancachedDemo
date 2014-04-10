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

	private String getTime(){
		String stime = "";
		Time t=new Time();
		t.setToNow();
		String[] date = new String[5];
		String year = Integer.toString(t.year);
		date[0] = Integer.toString(t.month+1);
		date[1] = Integer.toString(t.monthDay);
		date[2] = Integer.toString(t.hour);
		date[3] = Integer.toString(t.minute);
		date[4] = Integer.toString(t.second);
		for (int i = 0; i < 5; i++){
			if (date[i].length() == 1){
				date[i] = "0" + date[i];
			}
		}
		stime += year + "-" + date[0] + "-" + date[1] + " " +
				date[2] + ":" + date[3] + ":" + date[4];
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
