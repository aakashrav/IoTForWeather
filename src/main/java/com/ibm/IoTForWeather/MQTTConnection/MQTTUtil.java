package com.ibm.IoTForWeather.MQTTConnection;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public abstract class MQTTUtil {
	
		private final static String SERVER_SUFFIX = ".messaging.internetofthings.ibmcloud.com";
		private final static String DEFAULT_DEVICE_TYPE="ARM";
		private final static String DEFAULT_EVENT_ID = "status";
		private final static String DEFAULT_CMD_ID = "weather";
		
		private static final String org="2uwqwc";
		
		private static final String subscribe_appid="WeatherIoT";
		private static final String subscribe_key="a-2uwqwc-gnumvxzrqf";
		private static final String subscribe_token="6@T191EadC&1i&Xu*0";
		
		private static final String publish_appid = "WeatherIoT";
		private static final String publish_key = "a-2uwqwc-wuatrq2bo0";
		private static final String publish_token = "7IMZreCaUq+ogp1XNw";
		
				
		
		
		public static String getServerSuffix() {
			return SERVER_SUFFIX;
		}

		public static String getDefaultDeviceType() {
			return DEFAULT_DEVICE_TYPE;
		}

		public static String getDefaultEventId() {
			return DEFAULT_EVENT_ID;
		}

		public static String getDefaultCmdId() {
			return DEFAULT_CMD_ID;
		}

		public static String getOrg() {
			return org;
		}

		public static String getSubscribeAppid() {
			return subscribe_appid;
		}
		
		public static String getPublishAppid() {
			return publish_appid;
		}

		public static String getSubscribeKey() {
			return subscribe_key;
		}
		
		public static String getPublishKey() {
			return publish_key;
		}

		public static String getSubscribeToken() {
			return subscribe_token;
		}
		
		public static String getPublishToken() {
			return publish_token;
		}
		
		
		//private static final String isSSL="T"; assumed to be true
	
//		private static Properties configurationProperties;
//		
//		/**
//		 * This method sets the properties from the config file
//		 * @param filePath
//		 * @return
//		 * @exception IOException
//		 */
//		public static void setMQTTProperties(String filePath) {
//			
//			configurationProperties = new Properties();
//			
//			try {
//				InputStream in = new BufferedInputStream(new FileInputStream(
//						filePath));
//				MQTTUtil.configurationProperties.load(in);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//		}
//
//		public static String getServerSuffix() {
//			return MQTTUtil.configurationProperties
//					.getProperty("SERVER_SUFFIX");
//		}
//
//		public static String getDefaultDeviceType() {
//			return MQTTUtil.configurationProperties
//					.getProperty("DEFAULT_DEVICE_TYPE");
//		}
//
//		public static String getDefaultEventId() {
//			return MQTTUtil.configurationProperties
//					.getProperty("DEFAULT_EVENT_ID");
//		}
//
//		public static String getDefaultCmdId() {
//			return MQTTUtil.configurationProperties
//					.getProperty("DEFAULT_CMD_ID");
//		}
//
//		public static String getOrg() {
//			return MQTTUtil.configurationProperties
//					.getProperty("ORG");
//		}
//
//		public static String getAppid() {
//			return MQTTUtil.configurationProperties
//					.getProperty("APP_ID");
//		}
//
//		public static String getKey() {
//			return MQTTUtil.configurationProperties
//					.getProperty("KEY");
//		}
//
//		public static String getToken() {
//			return MQTTUtil.configurationProperties
//					.getProperty("TOKEN");
//		}
	}


