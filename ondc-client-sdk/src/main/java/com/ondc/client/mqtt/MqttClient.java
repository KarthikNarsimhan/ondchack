package com.ondc.client.mqtt;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient.Mqtt5Publishes;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.ondc.client.utils.ConfigConstants;
import com.ondc.client.utils.JSONUtils;

import io.cloudevents.CloudEvent;

/**
 * The Class MqttClient.
 * @author karthik
 * 
 * This is an internal class. Use the MqttPublisher and MqttSubscriber classes
 * for publish/subscribe.
 */
class MqttClient {

	/** The logger. */
	static Logger logger = Logger.getLogger(MqttClient.class.getName());

	/** The mqtt client. */
	Mqtt5BlockingClient mqttClient = null;

	/**
	 * Instantiates a new mqtt client.
	 */
	MqttClient() {
		mqttClient = Mqtt5Client.builder().serverHost(ConfigConstants.MQTT_HOST).serverPort(ConfigConstants.MQTT_PORT).identifier(UUID.randomUUID().toString())
				.sslWithDefaultConfig().buildBlocking();
		mqttClient.connectWith().simpleAuth().username(ConfigConstants.MQTT_USER).password(StandardCharsets.UTF_8.encode(ConfigConstants.MQTT_PASSWORD))
				.applySimpleAuth().send();
		logger.log(Level.INFO, "Connected to broker");
	}

	/**
	 * Disconnect.
	 */
	public void disconnect() {
		if (mqttClient != null)
			logger.log(Level.INFO, "Disconnecting Mqtt Broker.");
		mqttClient.disconnect();
	}

	/**
	 * Publish.
	 *
	 * @param topic the topic
	 * @param event the event
	 */
	public void publish(String topic, String event) {
		mqttClient.toAsync().publishWith().topic(topic).payload(StandardCharsets.UTF_8.encode(event)).send()
				.whenComplete((publish, throwable) -> {
					if (throwable != null) {
						logger.log(Level.SEVERE, "Error publishing", throwable);
					} else {
						logger.log(Level.INFO, "published message to topic.." + topic);
					}
				});

	}

	/**
	 * Subscribe async.
	 *
	 * @param topic    the topic
	 * @param callback the callback
	 */
	public void subscribeAsync(String topic, MqttCallback callback) {
		mqttClient.toAsync().subscribeWith().topicFilter(topic).qos(MqttQos.AT_LEAST_ONCE)
				.callback(new Consumer<Mqtt5Publish>() {
					@Override
					public void accept(Mqtt5Publish t) {
						callback.receive(topic, JSONUtils.getCloudEvent(new String(t.getPayloadAsBytes())));
					}
				}).send().whenComplete((suback, throwable) -> {
					if (throwable != null) {
						logger.log(Level.SEVERE, "Error subscribing", throwable);
					} else {
						logger.log(Level.INFO, "subscribed message from topic.." + topic);
					}
				});
	}

	/**
	 * Subscribe blocking.
	 *
	 * @param topic the topic
	 * @return the cloud event
	 * @throws InterruptedException the interrupted exception
	 */
	public CloudEvent subscribeBlocking(String topic) throws MqttException {
		try (final Mqtt5Publishes publishes = mqttClient.publishes(MqttGlobalPublishFilter.ALL)) {
			mqttClient.subscribeWith().topicFilter(topic).qos(MqttQos.AT_LEAST_ONCE).send();
			Mqtt5Publish message = publishes.receive();
			return JSONUtils.getCloudEvent(new String(message.getPayloadAsBytes()));
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new MqttException(e);
		}

	}

	/**
	 * The Interface MqttCallback.
	 */
	public interface MqttCallback {

		/**
		 * Receive.
		 *
		 * @param topic   the topic
		 * @param event the event
		 */
		public void receive(String topic, CloudEvent event);
	}

}
