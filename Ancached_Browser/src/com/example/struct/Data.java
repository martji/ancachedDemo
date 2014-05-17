package com.example.struct;

public class Data {
	private String description="";//description
	private float weight=0;//weight of the link
	
	public Data(){
		
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
	
	/**
	 * @param des description
	 * @param wei description
	 */
	public Data(String des,float wei){
		this.description=des;
		this.weight=wei;
	}
}
