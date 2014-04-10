package com.example.model;

public class STime {
	
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	private String str;
	
	public STime(String stime){
		this.setStr(stime);
		String[] tmp = stime.split("-");
		this.year = Integer.parseInt(tmp[0]);
		this.month = Integer.parseInt(tmp[1]);
		this.day = Integer.parseInt(tmp[2]);
		this.hour = Integer.parseInt(tmp[3]);
		this.minute = Integer.parseInt(tmp[4]);
		this.second = Integer.parseInt(tmp[5]);
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
