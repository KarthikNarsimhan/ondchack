package com.ondc.client.mqtt;

import java.util.logging.Logger;

import com.ondc.client.mqtt.MqttClient.MqttCallback;

import io.cloudevents.CloudEvent;

/**
 * The Class MqttSubscriber.
 * @author karthik
 */
public class MqttSubscriber {

	/** The logger. */
	static Logger logger = Logger.getLogger(MqttSubscriber.class.getName());

	/** The mqtt client. */
	private MqttClient mqttClient;

	/**
	 * Instantiates a new event handler.
	 */
	public MqttSubscriber() {
		mqttClient = new MqttClient();
	}

	/**
	 * Subscribe blocking. Used this for one time subscription.
	 * Please ensure you call disconnect at the end.
	 *
	 * @param topic the topic
	 * @return the cloud event
	 */
	public CloudEvent subscribeBlocking(String topic) throws MqttException {
		return mqttClient.subscribeBlocking(topic);
	}

	/**
	 * Subscribe async. Use this for indefinite subscription.
	 * Please ensure you call disconnect at the end.
	 *
	 * @param topic    the topic
	 * @param callback the callback
	 */
	public void subscribeAsync(String topic, MqttCallback callback) {
		mqttClient.subscribeAsync(topic, callback);
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
	public static void main(String[] args) {
		MqttSubscriber a = new MqttSubscriber();
		try {
			//test async subscription with call back.
//			a.subscribeAsync("testTopic1", new MqttCallback() {
//
//				@Override
//				public void receive(String topic, CloudEvent event) {
//					System.out.println(topic);
//					System.out.println(event);
//
//				}
//			});
			
			//test blocking subscription for one time subscribe.
			CloudEvent event = a.subscribeBlocking("testTopic1");
			System.out.println(event);
			//Sleep to try multiple publish/subscribe
			Thread.sleep(30000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			a.disconnect();
		}

	}

}
