package com.example.struct;

import java.util.ArrayList;

public class FeedBack {
	private ArrayList<Seed> sortList=new ArrayList<Seed>();//sorted list
	private ArrayList<String> list=new ArrayList<String>();//resource list

	public ArrayList<Seed> getSortList() {
		return sortList;
	}
	public void setSortList(ArrayList<Seed> sortList) {
		this.sortList = sortList;
	}
	public ArrayList<String> getList() {
		return list;
	}
	public void setList(ArrayList<String> list) {
		this.list = list;
	}
	
	public FeedBack(){
		
	}
}