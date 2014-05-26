package com.example.struct;

import java.util.ArrayList;

public class ItemList {
	ArrayList<Item> list =new ArrayList<Item>();
	
	public ArrayList<Item> getList() {
		return list;
	}

	public void setList(ArrayList<Item> list) {
		this.list = list;
	}

	public ItemList(){
		
	}

	public ItemList(ArrayList<Item> items) {
		// TODO Auto-generated constructor stub
		this.list = items;
	}
	
}
