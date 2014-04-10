package com.ancached.db;

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
		this.vTime = new STime("2014-1-1-1-1-1");
		this.netState = 0;
		this.location = "";
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
