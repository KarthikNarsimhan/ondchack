package com.ondc.client.utils;

/**
 * The Class ConfigConstants.
 */
public class ConfigConstants {
	
	/** The Constant MQTT_PORT. */
	public static final int MQTT_PORT = 1883; //8883;

	/** The host. */
	public static String MQTT_HOST = "broker.hivemq.com";

	/** The broker. */
	public static String MQTT_BROKER = "tcp://" + MQTT_HOST + ":1883";

	/** The user. */
	public static String MQTT_USER = "testpoc";

	/** The password. */
	public static String MQTT_PASSWORD = "Q1w2e3r4";
	
	
	/** The Blockchain DB endpoint. */
	public static String BLOCKCHAIN_DB_URL = "https://test.ipdb.io";
	
	
	/** The ocr url. */
	public static String OCR_URL = "https://api.ocr.space/parse/image";
	
	/** The ocr api key. */
	public static String OCR_API_KEY = "helloworld";

}
