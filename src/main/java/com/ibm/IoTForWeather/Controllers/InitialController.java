package com.ibm.IoTForWeather.Controllers;


import java.io.File;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ibm.IoTForWeather.MQTTConnection.DataHandler;
import com.ibm.IoTForWeather.Utilities.DeviceData;
import com.ibm.IoTForWeather.Utilities.JSONUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Controller
public class InitialController {
	
	@RequestMapping(value={"/","NewData"}, method=RequestMethod.GET)
	public String processData(Model model) {
		//Get data from JSON file, and display it
		JSONObject weatherInfo = null;
		ArrayList<DeviceData> deviceDataArray = new ArrayList<DeviceData>();
		try {
			weatherInfo = JSONUtils.
					getJSONFromFile("device_data.json");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (weatherInfo!=null) {
			JSONArray device_data_array = 
					weatherInfo.getJSONArray("device_data_array");
			System.out.println(device_data_array.length());
			for (int i =0; i < device_data_array.length(); i++) {
				double lat = device_data_array.getJSONObject(i)
						.getDouble("lat");
				double lon = device_data_array.getJSONObject(i)
						.getDouble("lng");
				String MAC = device_data_array.getJSONObject(i)
						.getString("name");
				
				DeviceData data = new DeviceData(lat, lon, MAC);
				deviceDataArray.add(data);
			}
		}
		
		//Add data to model and display it.
		model.addAttribute("deviceDataArray", deviceDataArray);
		return "index";
	}
	
	@PostConstruct
	public static void init() throws Exception {
		
		// Create new file for storage of device data
		File f = new File("device_data.json");
	    if (f.exists())
	    {
	    	f.delete();
	    	f.createNewFile();
	    }
	    else
	    	f.createNewFile();
	    
		//Add an empty array for incoming device data storage
		JSONArray device_data_array = new JSONArray();
		JSONObject device_data_object = new JSONObject();
		//Append empty JSONArray to JSONObject
		device_data_object.put("device_data_array", device_data_array);
		
		JSONUtils.writeJSONToFile("device_data.json",
				device_data_object);
		
		//TODO:Initialize the monitoring
		(new Thread(new DataHandler())).start();
		System.out.println("I got initialized!");
	}
	
}