package com.ancached.params;

public class Params {
	@SuppressWarnings("unused")
	private static final String SERVER_ADDRESS="112.124.46.148:5001";//Server address
	@SuppressWarnings("unused")
	private static final String DEFAULT_AGENT = "Mozilla/5.0(Linux; Android 4.1.2; Nexus 7 Build/JZO54K) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
	
	private static String DEVICE_ID="";//Device ID
	
	/*
	 * NET_STATE 1 means wifi--2 means mobile--0 we don't know
	 */
	
	private static int NET_STATE=0;//state of the network;
	
	public static int getNET_STATE() {
		return NET_STATE;
	}

	public static void setNET_STATE(int nET_STATE) {
		NET_STATE = nET_STATE;
	}

	public static String getDEVICE_ID() {
		return DEVICE_ID;
	}

	public static void setDEVICE_ID(String dEVICE_ID) {
		DEVICE_ID = dEVICE_ID;
	}
}
