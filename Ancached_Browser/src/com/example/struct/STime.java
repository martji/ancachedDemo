package com.example.struct;

public class STime {
	
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	private String str;
	
	public STime(String stime){
		if (stime.contains(" ")){
			this.setStr(stime);
			String[] tmps = stime.split(" ");
			String[] tmp = tmps[0].split("-");
			this.year = Integer.parseInt(tmp[0]);
			this.month = Integer.parseInt(tmp[1]);
			this.day = Integer.parseInt(tmp[2]);
			tmp = tmps[1].split(":");
			this.hour = Integer.parseInt(tmp[0]);
			this.minute = Integer.parseInt(tmp[1]);
			this.second = Integer.parseInt(tmp[2]);
		}
		else {
			String[] date = stime.split("-");
			for (int j = 1; j < 6; j++){
				if (date[j].length() == 1){
					date[j] = "0" + date[j];
				}
			}
			stime = date[0] + "-" + date[1] + "-" + date[2] + " " +
					date[3] + ":" + date[4] + ":" + date[5];
			this.setStr(stime);
			String[] tmps = stime.split(" ");
			String[] tmp = tmps[0].split("-");
			this.year = Integer.parseInt(tmp[0]);
			this.month = Integer.parseInt(tmp[1]);
			this.day = Integer.parseInt(tmp[2]);
			tmp = tmps[1].split(":");
			this.hour = Integer.parseInt(tmp[0]);
			this.minute = Integer.parseInt(tmp[1]);
			this.second = Integer.parseInt(tmp[2]);
		}
	}
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public int getSecond() {
		return second;
	}
	public void setSecond(int second) {
		this.second = second;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}
	
}
