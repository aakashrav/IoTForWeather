package com.ibm.IoTForWeather.WeatherFunctions;

import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.impl.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.*;
import java.io.IOException;
import java.net.URLEncoder;

import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

import com.ibm.IoTForWeather.Utilities.*;

public abstract class InsightsForWeather {
	
	private static String WEATHER_API_URL = "https://39e101f9-3097-4c54-bb88-4dbb89a11f74:Hh0xOQlQGT@twcservice.mybluemix.net";
	private static String WEATHER_API_USERNAME = "39e101f9-3097-4c54-bb88-4dbb89a11f74";
	private static String WEATHER_API_PASSWORD = "Hh0xOQlQGT";
	private static final String API_VERSION_NUMBER = "v2";

	/**
	 * Sets the static class variables depending on pre-configured environment variables.
	 * This function need only be called once per application instance at the very
	 * beginning in order to set environment variables.
	 *
	 * @exception JSONException 
	 *				Will throw an exception if no environment variables for the 
	 *				Insights for Weather service have been set up. Make sure you have
	 *				set up this service on your application instsance.
     */
	public static void setVCAPServices() throws JSONException {

		String services = System.getenv("VCAP_SERVICES");
		JSONObject servicesObject = new JSONObject(services);

		JSONObject weatherCredentials = null;
		weatherCredentials = servicesObject.getJSONArray("weatherinsights")
			.getJSONObject(0).getJSONObject("credentials");
		
		InsightsForWeather.WEATHER_API_URL = weatherCredentials.getString("url");
		InsightsForWeather.WEATHER_API_USERNAME = weatherCredentials.getString("username");
		InsightsForWeather.WEATHER_API_PASSWORD = weatherCredentials.getString("password");
	}

	public static JSONObject getCurrentObservations(double latitude, double longitude) throws IllegalStateException, IOException {
		
		String currentObservationsURL = InsightsForWeather.WEATHER_API_URL + ":443"
                + "/api/weather/" + InsightsForWeather.API_VERSION_NUMBER
                + "/observations/current?units=" + URLEncoder.encode("e", "UTF-8")
                + "&geocode=" + URLEncoder.encode(String.valueOf(latitude) + "," + String.valueOf(longitude), "UTF-8")
                + "&language=" + URLEncoder.encode("en-US", "UTF-8");

        System.out.println(currentObservationsURL);

        String baseURI = "https://twcservice.mybluemix.net:443/api/weather/v2";
        baseURI += "/observations/current?units=" + URLEncoder.encode("e", "UTF-8")
        + "&geocode=" + URLEncoder.encode(String.valueOf(latitude) + "," + String.valueOf(longitude), "UTF-8")
        + "&language=" + URLEncoder.encode("en-US", "UTF-8");

        String credentials = InsightsForWeather.WEATHER_API_USERNAME+":"+InsightsForWeather.WEATHER_API_PASSWORD;

        DefaultHttpClient httpclient=new DefaultHttpClient();
        HttpGet httpget = new HttpGet(baseURI);
        HttpResponse response = null;
        httpget.addHeader("Authorization", "Basic " + new BASE64Encoder().encode(credentials.getBytes()));
       
        response = httpclient.execute(httpget);
        return JSONUtils.getObjectFromStream(response.getEntity()
                        .getContent());
		
	}
}