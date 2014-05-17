package com.example.struct;

public class Seed {
	String url="";
	Data data=new Data();
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
	
	public Seed(){
		
	}
	
	public Seed(String address,Data tmpData){
		this.url=address;
		this.data=tmpData;
	}
}
