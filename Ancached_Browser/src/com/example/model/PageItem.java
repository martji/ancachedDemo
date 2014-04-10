package com.example.model;

public class PageItem {

	private String url;
	private String title;
	private String type;
	
	public PageItem(String url, String title) {
		// TODO Auto-generated constructor stub
		this.url = url;
		this.title = title;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
