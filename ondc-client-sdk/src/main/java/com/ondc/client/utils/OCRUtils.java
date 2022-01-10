package com.ondc.client.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * The Class OCRUtil.
 * 
 * @author karthik
 */
public class OCRUtils {
	/** The logger. */
	static Logger logger = Logger.getLogger(OCRUtils.class.getName());

	/**
	 * Parses the image in the url to text.
	 *
	 * @param url the url
	 * @return the parsed text
	 */
	public static String parseImage(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(ConfigConstants.OCR_URL);
		httpPost.addHeader("apikey", ConfigConstants.OCR_API_KEY);

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("base64Image", url));
		params.add(new BasicNameValuePair("language", "eng"));
		params.add(new BasicNameValuePair( "filetype","PNG"));

		params.add(new BasicNameValuePair("detectOrientation", "true"));
		params.add(new BasicNameValuePair("scale", "true"));

		params.add(new BasicNameValuePair("OCREngine", "1"));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.log(Level.SEVERE, "Error on HTTP post", e);
		}

		try {
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity respEntity = response.getEntity();

			if (respEntity != null) {
				String responseJson = EntityUtils.toString(respEntity);
				return parseResponse(responseJson);
			}
		} catch (ClientProtocolException e) {
			logger.log(Level.SEVERE, "Error on HTTP post", e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error on HTTP post", e);
		}
		return null;
	}

	/**
	 * Parses the content.
	 *
	 * @param content the content
	 * @return the string
	 */
	private static String parseResponse(String content) {
		if (content != null && !content.isEmpty()) {
			JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
			if (jsonObject.get("OCRExitCode").getAsInt() == 1) {
//				{  //response sample
//					  "ParsedResults": [
//					    {
//					      "TextOverlay": {
//					        "Lines": [],
//					        "HasOverlay": false,
//					        "Message": "No overlay requested."
//					      },
//					      "TextOrientation": "0",
//					      "FileParseExitCode": 1,
//					      "ParsedText": "Masájhai-licious\nbalcednoedle\nLETS CODK\nSWEETCORN\nodles\nMSide U99ested oarnehing\nTion\nprtionis oi\n60g B R\nan average & yoar ol chd",
//					      "ErrorMessage": "",
//					      "ErrorDetails": ""
//					    }
//					  ],
//					  "OCRExitCode": 1,
//					  "IsErroredOnProcessing": false,
//					  "ProcessingTimeInMilliseconds": "7078"
//					}
				return jsonObject.get("ParsedResults").getAsJsonArray().get(0).getAsJsonObject().get("ParsedText")
						.getAsString();
			}
		}
		return null;
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		System.out.println(parseImage("https://mandi.succinct.in/attachments/view/17592186044687.png"));
	}
}
