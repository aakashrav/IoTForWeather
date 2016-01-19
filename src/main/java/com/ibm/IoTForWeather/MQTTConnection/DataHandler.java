package com.ibm.IoTForWeather.MQTTConnection;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.IoTForWeather.Utilities.DeviceCommand;
import com.ibm.IoTForWeather.Utilities.JSONUtils;
import com.ibm.IoTForWeather.WeatherFunctions.*;

public class DataHandler implements WeatherMQTTHandler {
	
	private MqttClient client = null;
	private final static String DEFAULT_TCP_PORT = "1883";
	private final static String DEFAULT_SSL_PORT = "8883";
	
	public void run() {
		
		this.connect(MQTTUtil.getOrg() + MQTTUtil.getServerSuffix(), 
				"a:" + MQTTUtil.getOrg() + ":" + MQTTUtil.getAppid(),
				MQTTUtil.getKey(), MQTTUtil.getToken(), true);
		
		/* process requests indefinitely, therefore need to thread this */
		this.subscribe("iot-2/type/" + MQTTUtil.getDefaultDeviceType()
				+ "/id/+/evt/+" + "/fmt/json", 0);
	}

	/**
	 * When new data from the device is recieved, use it to call
	 * the weather services API and gain insight.
	 */
	public void messageArrivedCallback(String payload) {
		
		System.out.println(payload);
		System.out.println("got new data object! " + payload);
		
		/*
		 * Get latitude, longitude, other data from device
		 */
		JSONObject wrapper_obj = new JSONObject(payload);
		JSONObject obj = wrapper_obj.getJSONObject("d");
		
		double latitude = obj.getDouble("lat");
		double longitude = obj.getDouble("lng");
		String deviceMAC = obj.getString("name");
		
		/*
		 * Store this data for display and monitoring
		 */
		JSONUtils.addNewUpdateToJSONArray("device_data.json",obj);

		/*
		 * Pass this data to insights for weather to pass for analysis
		 */
		JSONObject weatherData = null;
		try {
			weatherData = InsightsForWeather.getCurrentObservations(latitude, longitude);
		} catch (Exception e) {
			System.out.println("Error making weather API call with new data");
			e.printStackTrace();
		}
		/*
		 * Take the received insight from weather analytics and
		 * send it as a command to the device. First we preprocess
		 * and obtain the important parts of the insight.
		 */
		
		//The 3 pieces of insight we require: rain, snow, and temp
		double rain = weatherData.getJSONObject("observation")
				.getJSONObject("imperial")
				.getDouble("precip_1hour");
		double snow = weatherData.getJSONObject("observation")
				.getJSONObject("imperial")
				.getDouble("snow_1hour");
		double temp = weatherData.getJSONObject("observation")
				.getJSONObject("imperial")
				.getDouble("temp");
		
		DeviceCommand newUpdate = new DeviceCommand(rain, snow, temp);
		
		ObjectMapper mapper = new ObjectMapper();
		String rawJSONCommand = "";
		try {
			rawJSONCommand = mapper.writeValueAsString(newUpdate);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		System.out.println(rawJSONCommand);
		
		//iot-2/type/<type-id>/id/<device-id>/cmd/<cmd-id>/fmt/<format-id>
		publish("iot-2/type/" + MQTTUtil.getDefaultDeviceType()
				+ "/id/" + deviceMAC + "/cmd/" + MQTTUtil.getDefaultCmdId()
				+ "/fmt/json", rawJSONCommand, false, 0);
		System.out.println("iot-2/type/" + MQTTUtil.getDefaultDeviceType()
				+ "/id/" + deviceMAC + "/cmd/" + MQTTUtil.getDefaultCmdId()
				+ "/fmt/json");
//		//iot-2/type/<type-id>/id/<device-id>/cmd/<cmd-id>/fmt/<format-id>
//		publish("iot-2/cmd/" + MQTTUtil.getDefaultCmdId()
//				+ "/fmt/json", rawJSONCommand, false, 0);
		
		System.out.println("Finished sending command!");
	}
	
	
	/**
	 * Received one subscribed message
	 */
	@Override
	public void messageArrived (String topic, MqttMessage mqttMessage)
			throws Exception, JSONException, RuntimeException {
		
		Pattern pattern = Pattern.compile("iot-2/type/"
				+ MQTTUtil.getDefaultDeviceType() + "/id/(.+)/evt/(.+)"
				+ "/fmt/json");

		Matcher matcher = pattern.matcher(topic);
		if (matcher.matches()) {
			String payload = new String(mqttMessage.getPayload());
			System.out.println(payload);
		
			messageArrivedCallback(payload);
		}
	}
	
	public void connect(String serverHost, String clientId, String authmethod,
			String authtoken, boolean isSSL) {
		// check if client is already connected
		if (!isMqttConnected()) {
			String connectionUri = null;
			
			connectionUri = "ssl://" + serverHost + ":" + DataHandler.DEFAULT_SSL_PORT;

			if (client != null) {
				try {
					client.disconnect();
				} catch (MqttException e) {
					System.out.println("not able to disconnect: mqtthandler");
					e.printStackTrace();
				}
				client = null;
			}

			try {
				client = new MqttClient(connectionUri, clientId);
			} catch (MqttException e) {
				e.printStackTrace();
			}

			client.setCallback(this);

			// create MqttConnectOptions and set the clean session flag
			MqttConnectOptions options = new MqttConnectOptions();
			options.setCleanSession(true);

			options.setUserName(authmethod);
			options.setPassword(authtoken.toCharArray());
			
			java.util.Properties sslClientProps = new java.util.Properties();
			sslClientProps.setProperty("com.ibm.ssl.protocol", "TLSv1.2");
			options.setSSLProperties(sslClientProps);

			try {
				// connect
				client.connect(options);
				System.out.println("Connected to " + connectionUri);
			} catch (MqttException e) {
				System.out.println("ERROR CONNECTING!");
				e.printStackTrace();
			}

		}

	}
	
	/**
	 * Subscribe MqttClient to a topic
	 * 
	 * @param topic
	 *            to subscribe to
	 * @param qos
	 *            to subscribe with
	 */
	public void subscribe(String topic, int qos) {

		// check if client is connected
		if (isMqttConnected()) {
			try {
				client.subscribe(topic, qos);
				System.out.println("Subscribed: " + topic);

			} catch (MqttException e) {
				e.printStackTrace();
			}
		} else {
			connectionLost(null);
		}
	}
	
	@Override
	public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
		System.out.println(".deliveryComplete() entered");
	}
	
	/**
	 * Unsubscribe MqttClient from a topic
	 * 
	 * @param topic
	 *            to unsubscribe from
	 */
	public void unsubscribe(String topic) {
		// check if client is connected
		if (isMqttConnected()) {
			try {

				client.unsubscribe(topic);
			} catch (MqttException e) {
				e.printStackTrace();
			}
		} else {
			connectionLost(null);
		}
	}

	@Override
	public void connectionLost(Throwable throwable) {
		if (throwable != null) {
			throwable.printStackTrace();
		}
	}

	/**
	 * Publish message to a topic
	 * 
	 * @param topic
	 *            to publish the message to
	 * @param message
	 *            JSON object representation as a string
	 * @param retained
	 *            true if retained flag is requred
	 * @param qos
	 *            quality of service (0, 1, 2)
	 */
	public void publish(String topic, String message, boolean retained, int qos) {
		// check if client is connected
		if (isMqttConnected()) {
			// create a new MqttMessage from the message string
			MqttMessage mqttMsg = new MqttMessage(message.getBytes());
			// set retained flag
			mqttMsg.setRetained(retained);
			// set quality of service
			mqttMsg.setQos(qos);
			try {
				System.out.println("About to send!");
				client.publish(topic, mqttMsg);
				System.out.println("Finished sending!");
			} catch (MqttPersistenceException e) {
				e.printStackTrace();
			} catch (MqttException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Connection lost!");
			connectionLost(null);
		}
	}

	/**
	 * Checks if the MQTT client has an active connection
	 * 
	 * @return True if client is connected, false if not.
	 */
	private boolean isMqttConnected() {
		boolean connected = false;
		try {
			if ((client != null) && (client.isConnected())) {
				connected = true;
			}
		} catch (Exception e) {
			// swallowing the exception as it means the client is not connected
		}
		return connected;
	}



}
