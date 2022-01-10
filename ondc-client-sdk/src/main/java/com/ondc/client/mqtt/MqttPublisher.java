package com.ondc.client.mqtt;

import java.net.URI;
import java.util.logging.Logger;

import com.ondc.client.utils.JSONUtils;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;

/**
 * The Class MqttPublisher.
 * @author karthik
 * 
 */
public class MqttPublisher {

	/** The logger. */
	static Logger logger = Logger.getLogger(MqttPublisher.class.getName());


	/** The mqtt client. */
	private MqttClient mqttClient;

	/**
	 * Instantiates a new mqtt publisher.
	 */
	public MqttPublisher() {
		mqttClient = new MqttClient();
	}

	/**
	 * Publish an event to the topic. This will be done in async.
	 *
	 * @param topic the topic
	 * @param event the event
	 */
	public void publishAsync(String topic, CloudEvent event) {
		mqttClient.publish(topic, JSONUtils.getJson(event));
	}

	/**
	 * Disconnect the mqtt client.
	 */
	public void disconnect() {
		mqttClient.disconnect();
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
    /*
	public static void main(String[] args) {
		String json = "{\n" + "  \"context\": {\n"
				+ "    \"transaction_id\": \"f2c3398a-5e18-40fa-85eb-a580c6c73b2e\",\n"
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

		final CloudEvent event = CloudEventBuilder.v1().withId("f2c3398a-5e18-40fa-85eb-a580c6c73b2e") // this can be
																										// beckn
																										// transaction
																										// id
				.withType(EventType.CONFIRM.name()) // type of event
				.withSource(URI.create("http://humbhionline.in")) // event source
				.withDataSchema(URI.create("http://beckn.org/schemas/confirm.json")) // "Identifies the schema that data
																						// adheres to."
				.withDataContentType("application/json").withData(json.getBytes()).build();

		MqttPublisher a = new MqttPublisher();
		a.publishAsync("testTopic1", event);
		a.disconnect();
		
	}
    */


}
