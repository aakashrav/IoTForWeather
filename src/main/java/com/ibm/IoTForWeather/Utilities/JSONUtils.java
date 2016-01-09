package com.ibm.IoTForWeather.Utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
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
	
	/*
	 * {
	"device_data_array": [{
		"lat": 12,
		"lon": 43,
		"mac": "adsfa"
	}]
}
	 */
	public static JSONObject getJSONFromFile(String pathname) throws Exception {
		File f = new File(pathname);
		JSONObject newObj = null;
        if (f.exists()){
            InputStream is = new FileInputStream(pathname);
            String json = IOUtils.toString(is);
            System.out.println(json);
            newObj = new JSONObject(json);      
        }
        return newObj;
	}
	
	public static void writeJSONToFile(String pathname, JSONObject obj) {
		
		Writer output = null;
		String json = obj.toString();
		File file = new File(pathname);
		try {
			output = new BufferedWriter(new FileWriter(file));
			output.write(json);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void addNewUpdateToJSONArray(String pathname, JSONObject obj) {
		
		JSONObject device_data_object = null;
		try {
			device_data_object = JSONUtils.getJSONFromFile(pathname);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//First create a copy of the current array
		JSONArray device_data_array = new JSONArray();
		device_data_array = device_data_object.getJSONArray("device_data_array");
		//Then append the new value
		device_data_array.put(obj);
		//Create a new Object
		JSONObject new_device_data_obj = new JSONObject();
		new_device_data_obj.put("device_data_array", device_data_array);
		System.out.println(new_device_data_obj.toString());
		
		//Then write it back to the file for storage
		JSONUtils.writeJSONToFile(pathname,new_device_data_obj);
	}
}

