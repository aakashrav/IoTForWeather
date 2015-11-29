package com.ibm.IoTForWeather.Controllers;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.ibm.IoTForWeather.WeatherFunctions.*;

@Controller
public class InitialController {

	@RequestMapping(value={"/","NewData"}, method=RequestMethod.GET)
	public String processData() {
		try {
			InsightsForWeather.getCurrentObservations(23,23);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "index";
	}
}