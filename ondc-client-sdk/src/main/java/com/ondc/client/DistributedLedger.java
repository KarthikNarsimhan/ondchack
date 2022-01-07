package com.ondc.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.KeyPair;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bigchaindb.api.TransactionsApi;
import com.bigchaindb.builders.BigchainDbConfigBuilder;
import com.bigchaindb.builders.BigchainDbTransactionBuilder;
import com.bigchaindb.constants.Operations;
import com.bigchaindb.model.GenericCallback;
import com.bigchaindb.model.MetaData;
import com.bigchaindb.model.Transaction;
import com.google.gson.internal.LinkedTreeMap;
import com.ondc.client.utils.JSONUtils;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import okhttp3.Response;

/**
 * The Class DistributedLedger.
 */
public class DistributedLedger {
	static Logger logger = Logger.getLogger(DistributedLedger.class.getName());

	/** The url. */
	// TODO externalize. In multinode deployment, get this information from a
	// registry
	private static String URL = "https://test.ipdb.io";

	/** The instance. */
	private static DistributedLedger instance;

	/**
	 * Instantiates a new distributed ledger.
	 */
	private DistributedLedger() {
		BigchainDbConfigBuilder.baseUrl(URL).addToken("app_id", "").addToken("app_key", "").setup();
	}

	/**
	 * Gets the single instance of DistributedLedger.
	 *
	 * @return single instance of DistributedLedger
	 */
	public static synchronized DistributedLedger instance() {
		if (instance == null)
			instance = new DistributedLedger();

		return instance;
	}

	/**
	 * Write event.
	 *
	 * @param event     the event
	 * @param eventType the event type
	 * @param keyPair   the key pair
	 * @param callback  the callback
	 * @return the string
	 */
	public String writeEvent(CloudEvent event, EventType eventType, KeyPair keyPair, GenericCallback callback) {
		MetaData metaData = new MetaData();
		metaData.setMetaData("Event", eventType.name());
		metaData.setMetaData("ID", event.getId());
		try {
			metaData.setMetaData("Source", event.getSource().toURL().toString());
		} catch (MalformedURLException e1) {
		}

		Map<String, Object> assetData = new TreeMap<String, Object>();
		assetData.put("asset_id", event.getId());
		assetData.put("event", JSONUtils.getJson(event));

		try {
			Transaction createTransaction = BigchainDbTransactionBuilder.init().addAssets(assetData, TreeMap.class)
					.addMetaData(metaData).operation(Operations.CREATE)
					.buildAndSign((EdDSAPublicKey) keyPair.getPublic(), (EdDSAPrivateKey) keyPair.getPrivate())
					.sendTransaction(callback);

			return createTransaction.getId();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets the event by id.
	 *
	 * @param eventId the event id
	 * @return the event by id
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public CloudEvent getEventById(String eventId) throws IOException {
		Transaction transaction = TransactionsApi.getTransactionById(eventId);
		LinkedTreeMap<String, Object> data = (LinkedTreeMap<String, Object>) transaction.getAsset().getData();
		String eventJson = (String) data.get("event");
		return JSONUtils.getCloudEvent(eventJson);
	}

	private static void writeTransaction() throws Exception {
		net.i2p.crypto.eddsa.KeyPairGenerator edDsaKpg = new net.i2p.crypto.eddsa.KeyPairGenerator();
		KeyPair keyPair = edDsaKpg.generateKeyPair();
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

		GenericCallback callback = new GenericCallback() {
			@Override
			public void transactionMalformed(Response response) {
				logger.log(Level.INFO, "transactionMalformed");
			}

			@Override
			public void pushedSuccessfully(Response response) {
				try {
					logger.log(Level.INFO, "Ledger Write Successful." + new String(response.body().string()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void otherError(Response response) {
				logger.log(Level.SEVERE, "Ledger Write Failed.");
			}
		};

		String transactionId = DistributedLedger.instance().writeEvent(event, EventType.CONFIRM, keyPair, callback);
		logger.log(Level.INFO, "Transaction Id: " + transactionId);
	}

	public static void main(String[] args) {
		try {
			logger.log(Level.INFO, DistributedLedger.instance()
					.getEventById("91e03402c49f1b297793b29ba1c1ee8697ca881ac6026ed7a2525b1cdcc3409d").toString());
//			writeTransaction();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
