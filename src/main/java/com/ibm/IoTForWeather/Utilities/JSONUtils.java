package com.ibm.IoTForWeather.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONObject;

public class JSONUtils {
	
	public static JSONObject getObjectFromStream(InputStream returnStream) throws IOException {
		BufferedReader br = new BufferedReader(
                new InputStreamReader(returnStream));

		String line;
		String output="";
		while ((line = br.readLine()) != null) {
			output=output+line+"\n";
		}
		
		JSONObject newObj=new JSONObject(output);
		return newObj;
	
	}
}
