package com.ondc.client.utils;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.data.BytesCloudEventData;

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
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(BytesCloudEventData.class, new JsonSerializer<BytesCloudEventData>() {
					@Override
					public JsonElement serialize(BytesCloudEventData src, Type typeOfSrc,
							JsonSerializationContext context) {
						return new JsonPrimitive(new String(src.toBytes()));
					}

				}).create();
		
		return gson.toJson(event);
	}
}
