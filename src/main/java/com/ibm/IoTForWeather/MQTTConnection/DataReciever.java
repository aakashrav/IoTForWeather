package com.ibm.IoTForWeather.MQTTConnection;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.IoTForWeather.WeatherFunctions.*;

public class DataReciever implements WeatherMQTTHandler {
	
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
		JSONObject obj = new JSONObject(payload);
//		double latitude = Double.parseDouble(obj.getString("lat"));
//		double longitude = Double.parseDouble(obj.getString("lon"));
		double latitude = obj.getDouble("lat");
		double longitude = obj.getDouble("lon");
		String deviceMAC = obj.getString("mac");
		
		//TODO
		
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
		 * send it as a command to the device
		 */
		//TODO:
		//Publish command to one specific device
		//iot-2/type/<type-id>/id/<device-id>/cmd/<cmd-id>/fmt/<format-id>
		publish("iot-2/type/" + MQTTUtil.getDefaultDeviceType()
				+ "/id/" + deviceMAC + "/cmd/" + MQTTUtil.getDefaultCmdId()
				+ "/fmt/json", weatherData.toString(), false, 0);
		
		System.out.println("I'm Done!");
		System.out.println(weatherData);
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
		
		//TODO v2 implement callback for system events

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
			
			connectionUri = "ssl://" + serverHost + ":" + DataReciever.DEFAULT_SSL_PORT;

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
				client.publish(topic, mqttMsg);
			} catch (MqttPersistenceException e) {
				e.printStackTrace();
			} catch (MqttException e) {
				e.printStackTrace();
			}
		} else {
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
