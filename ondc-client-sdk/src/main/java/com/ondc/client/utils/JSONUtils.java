package com.ondc.client.utils;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;

/**
 * The Class JSONUtils.
 */
public class JSONUtils {

	/**
	 * Gets the json.
	 *
	 * @param event the event
	 * @return the json
	 */
	public static String getJson(CloudEvent event) {
		EventFormat format = EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE);
		byte[] serialized = format.serialize(event);
		return new String(serialized);
	}

	/**
	 * Gets the cloud event.
	 *
	 * @param eventJson the event json
	 * @return the cloud event
	 */
	public static CloudEvent getCloudEvent(String eventJson) {
		EventFormat format = EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE);
		CloudEvent event = format.deserialize(eventJson.getBytes());
		return event;
	}
}
