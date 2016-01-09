package com.ibm.IoTForWeather.Utilities;

public class DeviceData {
	
	private double lat;
	private double lon;
	private String mac;
	
	public DeviceData(double lt, double ln, String mac) {
		this.lat = lt;
		this.lon = ln;
		this.mac = mac;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	
	

}
