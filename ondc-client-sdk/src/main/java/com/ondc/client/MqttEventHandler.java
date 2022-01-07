package com.ondc.client;

import java.net.URI;

import com.ondc.client.mqtt.MqttClient;
import com.ondc.client.utils.JSONUtils;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;

/**
 * The EventHandler class.
 */
public class MqttEventHandler {

	/** The instance. */
	private static MqttEventHandler instance;
	
	/** The mqtt client. */
	private MqttClient mqttClient;

	/**
	 * Instantiates a new event handler.
	 */
	private MqttEventHandler() {
		mqttClient = MqttClient.getInstance();
	}

	/**
	 * Gets the single instance of EventHandler.
	 *
	 * @return single instance of EventHandler
	 */
	public static synchronized MqttEventHandler instance() {
		if (instance == null)
			instance = new MqttEventHandler();

		return instance;
	}

	/**
	 * Publish an event to the topic.
	 *
	 * @param topic the topic
	 * @param event the event
	 */
	public void publish(String topic, CloudEvent event) {
		mqttClient.publish(topic, JSONUtils.getJson(event));
	}

	/**
	 * Subscribe for an event on a topic.
	 *
	 * @param topic the topic
	 * @return the event
	 */
	public String subscribe(String topic) {
		return mqttClient.subscribe(topic);
	}

	/**
	 * Disconnect the mqtt client
	 */
	public void disconnect() {
		mqttClient.disconnect();
	}
	
	public static void main(String[] args) {
		String json = "{\n" + "  \"context\": {\n"
				+ "    \"transaction_id\": \"c2c3398a-5e18-40fa-85eb-a580c6c73b2e\",\n"
				+ "    \"bpp_id\": \"mandi.succinct.in\",\n" + "    \"domain\": \"local-retail\",\n"
				+ "    \"bpp_uri\": \"https://mandi.succinct.in/bpp\",\n" + "    \"action\": \"confirm\",\n"
				+ "    \"message_id\": \"012283c6-31f2-4b74-bbb3-3cf634ed2cc4\",\n" + "    \"ttl\": \"PT1M\",\n"
				+ "    \"core_version\": \"0.9.1\",\n"
				+ "    \"bap_uri\": \"https://beckn-one.succinct.in/local_retail_bap\",\n"
				+ "    \"bap_id\": \"beckn-one.succinct.in.local-retail.bap\",\n"
				+ "    \"timestamp\": \"2021-07-09T21:05:16+05:30\"\n" + "  },\n" + "  \"message\": {\n"
				+ "    \"order\": {\n" + "      \"payment\": {\n" + "        \"params\": {\n"
				+ "          \"amount\": 80\n" + "        },\n" + "        \"status\": \"PAID\"\n" + "      }\n"
				+ "    }\n" + "  }\n" + "}";


		final CloudEvent event = CloudEventBuilder.v1().withId("c2c3398a-5e18-40fa-85eb-a580c6c73b2e") // this can be
																										// beckn
																										// transaction
																										// id
				.withType(EventType.CONFIRM.name()) // type of event
				.withSource(URI.create("http://humbhionline.in")) // event source
				.withDataSchema(URI.create("http://beckn.org/schemas/confirm.json")) // "Identifies the schema that data
																						// adheres to."
				.withDataContentType("application/json").withData(json.getBytes()).build();
				
		MqttEventHandler a= MqttEventHandler.instance();
		a.publish("testTopic", event);
		System.out.println(a.subscribe("testTopic"));
		a.disconnect();
	}

}
