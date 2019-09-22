package io.sapl.pdp.remote;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.api.pdp.Request;
import io.sapl.api.pdp.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemotePolicyDecisionPoint implements PolicyDecisionPoint {

	public static final String APPLICATION_JSON_VALUE = "application/json;charset=UTF-8";
	public static final String AUTHORIZATION_REQUEST = "/api/authorizationRequests";
	public static final String HTTPS = "https";

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private HttpHost httpHost;
	private HttpClientContext clientContext;

	public RemotePolicyDecisionPoint(final String hostName, final int port, final String applicationKey,
			final String applicationSecret) {
		MAPPER.registerModule(new Jdk8Module());

		httpHost = new HttpHost(hostName, port, HTTPS);
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(httpHost.getHostName(), httpHost.getPort()),
				new UsernamePasswordCredentials(applicationKey, applicationSecret));

		AuthCache authCache = new BasicAuthCache();
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(httpHost, basicAuth);

		clientContext = HttpClientContext.create();
		clientContext.setCredentialsProvider(credsProvider);
		clientContext.setAuthCache(authCache);
	}

	@Override
	public Response decide(Request request) {
		HttpPost post = new HttpPost(AUTHORIZATION_REQUEST);
		post.addHeader("content-type", APPLICATION_JSON_VALUE);
		String body;
		try {
			body = MAPPER.writeValueAsString(request);
		} catch (JsonProcessingException e) {
			LOGGER.error("Marshalling request failed: {}", request, e);
			return new Response(Decision.INDETERMINATE, null, null, null);
		}
		HttpEntity entity = new ByteArrayEntity(body.getBytes(StandardCharsets.UTF_8));
		post.setEntity(entity);
		Response response = null;
		try {
			response = executeHttpRequest(post);
		} catch (IOException e) {
			LOGGER.error("Request failed: {}", post, e);
			return new Response(Decision.INDETERMINATE, null, null, null);
		}
		return response;
	}

	@Override
	public Response decide(Object subject, Object action, Object resource, Object environment) {
		Request request = new Request(MAPPER.convertValue(subject, JsonNode.class),
				MAPPER.convertValue(action, JsonNode.class), MAPPER.convertValue(resource, JsonNode.class),
				MAPPER.convertValue(environment, JsonNode.class));
		return decide(request);
	}

	@Override
	public Response decide(Object subject, Object action, Object resource) {
		return decide(subject, action, resource, null);
	}

	private Response executeHttpRequest(HttpRequest request) throws IOException {
		Response response = null;
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse webResponse = httpClient.execute(httpHost, request, clientContext);) {

			int resultCode = webResponse.getStatusLine().getStatusCode();
			if (resultCode != HttpStatus.SC_OK) {
				throw new IOException("Error " + resultCode + ": " + webResponse.getStatusLine().getReasonPhrase());
			}

			HttpEntity responseEntity = webResponse.getEntity();
			if (responseEntity != null) {
				String responseEntityText = EntityUtils.toString(responseEntity);
				if (!responseEntityText.isEmpty()) {
					/*
					 * JsonNode result = MAPPER.readValue(responseEntityText, JsonNode.class);
					 * response = new Response( Decision.valueOf(result.get("decision").asText()),
					 * result.get("resource"), result.has("obligation") ? (ArrayNode)
					 * result.get("obligation") : null, result.has("advice") ? (ArrayNode)
					 * result.get("advice") : null);
					 */
					response = MAPPER.readValue(responseEntityText, Response.class);
				}
				EntityUtils.consume(responseEntity);
			}
		}
		return response;
	}

}