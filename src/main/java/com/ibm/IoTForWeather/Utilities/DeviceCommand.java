package com.ibm.IoTForWeather.Utilities;

public class DeviceCommand {
	
	private String rain;
	private String snow;
	private String temp;
	
	public DeviceCommand(String r, String s, String t)
	{
		this.setRain(r);
		this.setSnow(s);
		this.setTemp(t);
	}
	
	public DeviceCommand(double r, double s, double t)
	{
		this.setRain(String.valueOf(r));
		this.setSnow(String.valueOf(s));
		this.setTemp(String.valueOf(t));
	}

	public String getRain() {
		return rain;
	}

	public void setRain(String rain) {
		this.rain = rain;
	}

	public String getSnow() {
		return snow;
	}

	public void setSnow(String snow) {
		this.snow = snow;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

}
