package com.ondc.client.mqtt;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient.Mqtt5Publishes;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

/**
 * The Class MqttClient.
 */
public class MqttClient {
	static Logger logger =  Logger.getLogger(MqttClient.class.getName()); 

	/** The instance. */
	private static MqttClient instance;

	/** The mqtt client. */
	Mqtt5BlockingClient mqttClient = null;

	// TODO externalize

	/** The topic prefix. */
	String TOPIC_PREFIX = "ondc/";
	
	/** The qos. */
	int qos = 1;
	
	/** The host. */
	static String HOST = "9a7d49f3b04d4dd98b6ce4c203cc1831.s1.eu.hivemq.cloud";
	
	/** The broker. */
	static String BROKER = "ssl://" + HOST + ":8883";
	
	/** The user. */
	static String user = "testpoc";
	
	/** The password. */
	static String password = "Q1w2e3r4";

	/**
	 * Instantiates a new mqtt client.
	 */
	private MqttClient() {
		mqttClient = com.hivemq.client.mqtt.MqttClient.builder().useMqttVersion5().serverHost(HOST).serverPort(8883)
				.sslWithDefaultConfig().buildBlocking();
		mqttClient.connectWith().simpleAuth().username(user).password(StandardCharsets.UTF_8.encode(password))
				.applySimpleAuth().send();
	}

	/**
	 * Gets the single instance of MqttClient.
	 *
	 * @return single instance of MqttClient
	 */
	public static MqttClient getInstance() {
		if (instance == null)
			instance = new MqttClient();

		return instance;
	}

	/**
	 * Disconnect.
	 */
	public void disconnect() {
		if (mqttClient != null)
			logger.log(Level.INFO,"Disconnecting....");
		mqttClient.disconnect();
	}

	/**
	 * Publish.
	 *
	 * @param topic the topic
	 * @param event the event
	 */
	public void publish(String topic, String event) {
		mqttClient.publishWith().topic(TOPIC_PREFIX + topic).payload(StandardCharsets.UTF_8.encode(event)).send();
		logger.log(Level.INFO,"published message to topic.." + event);
	}

	/**
	 * Subscribe.
	 *
	 * @param topic the topic
	 * @return the string
	 */
	public String subscribe(String topic) {

		final Mqtt5BlockingClient client = Mqtt5Client.builder().identifier(UUID.randomUUID().toString())
				.serverHost(HOST).serverPort(8883).sslWithDefaultConfig().buildBlocking();

		client.connectWith().simpleAuth().username(user).password(StandardCharsets.UTF_8.encode(password))
		.applySimpleAuth().send();

		try (final Mqtt5Publishes publishes = client.publishes(MqttGlobalPublishFilter.ALL)) {

			client.subscribeWith().topicFilter(TOPIC_PREFIX+topic).qos(MqttQos.AT_LEAST_ONCE).send();

			try {
				publishes.receive(1, TimeUnit.SECONDS).ifPresent(System.out::println);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		} finally {
			client.disconnect();
		}
		return "ok";
	}

}
