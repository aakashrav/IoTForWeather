package com.ibm.IoTForWeather.WeatherFunctions;

import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.impl.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.*;
import java.io.IOException;
import com.ibm.IoTForWeather.Utilities.*;

public abstract class InsightsForWeather {
	
	// private static String WEATHER_API_URL;
	// private static String WEATHER_API_USERNAME;
	// private static String WEATHER_API_PASSWORD;
	// private static String WEATHER_HOST_URL;
	private static String WEATHER_API_URL = "https://39e101f9-3097-4c54-bb88-4dbb89a11f74:Hh0xOQlQGT@twcservice.mybluemix.net";
	private static String WEATHER_API_USERNAME = "39e101f9-3097-4c54-bb88-4dbb89a11f74";
	private static String WEATHER_API_PASSWORD = "Hh0xOQlQGT";
	private static String WEATHER_HOST_URL = "twcservice.mybluemix.net:443";
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
		InsightsForWeather.WEATHER_HOST_URL = weatherCredentials.getString("host");
	}

	public static JSONObject getCurrentObservations(double latitude, double longitude) throws IllegalStateException, IOException {
		
		String currentObservationsURL = InsightsForWeather.WEATHER_API_URL + ":443"
			+ "/api/weather/" + InsightsForWeather.API_VERSION_NUMBER + 
			"/observations/current?units=m&geocode=" + latitude + "%2C" + longitude
			+ "&language=en-US";

		System.out.println(currentObservationsURL);


		DefaultHttpClient httpclient=new DefaultHttpClient();
		HttpGet httpget = new HttpGet(currentObservationsURL);
		HttpResponse response = null;
		response = httpclient.execute(httpget);
		
		System.out.println(response.getStatusLine());
		
		return JSONUtils.getObjectFromStream(response.getEntity()
				.getContent());
		
		
	}
}