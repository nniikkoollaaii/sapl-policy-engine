package io.sapl.testrunner.junit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.api.interpreter.InitializationException;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint;

public class SaplUnitTestRunnerHelper {
	
	private EmbeddedPolicyDecisionPoint embeddedPDP;	
    
    /**
     * Constructor to instantiate SaplUnitTestRunnerHelper
     * @param policyId Id of Policy to test
     * @throws Exception
     */
	public SaplUnitTestRunnerHelper(EmbeddedPolicyDecisionPoint embeddedPDP) throws Exception {
		this.embeddedPDP = embeddedPDP;
	}	
	
	/**
	 * Evaluating policy under test with an AuthorizationSubscription object. Abstracting reactive behavior of policy engine in unit tests.
	 * @param authorizationSubscription 
	 * @return An AuthorizationDecision object
	 */
	public AuthorizationDecision decide(AuthorizationSubscription authorizationSubscription) {
		return this.embeddedPDP.decide(authorizationSubscription).blockFirst();
	}
		
	/**
	 * Evaluating policy under test with an AuthorizationSubscription object. Abstracting reactive behavior of policy engine in unit tests.
	 * @param authorizationSubscription 
	 * @return An AuthorizationDecision object
	 * @throws InitializationException
	 */
	// TODO custom Exception
	public AuthorizationDecision decide(String jsonAuthSub) throws InitializationException {
		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode authSubJsonNode;
		try {
			authSubJsonNode = objectMapper.readTree(jsonAuthSub);
		} catch (JsonProcessingException e) {
			throw new InitializationException("Could not read Json Tree!", e);
		}
		if(authSubJsonNode != null) {
			AuthorizationSubscription authSub = new AuthorizationSubscription(
					authSubJsonNode.findValue("subject"), 
					authSubJsonNode.findValue("action"), 
					authSubJsonNode.findValue("resource"), 
					authSubJsonNode.findValue("environment")
					);	
			return this.embeddedPDP.decide(authSub).blockFirst();
		}
		return null;
	}	
	
	/**
	 * Evaluating policy under test. Abstracting reactive behavior of policy engine in unit tests.
	 * @param jsonNode 
	 * @return An AuthorizationDecision object
	 * @throws Exception 
	 */
	public AuthorizationDecision decide(JsonNode jsonNode) {
		if(jsonNode != null) {
			AuthorizationSubscription authSub = new AuthorizationSubscription(
					jsonNode.findValue("subject"), 
					jsonNode.findValue("action"), 
					jsonNode.findValue("resource"), 
					jsonNode.findValue("environment")
					);	
			return this.embeddedPDP.decide(authSub).blockFirst();
		}
		return null;
	}
		
	/**
	 * Disposing all resources
	 */
	protected void disposeResources() {
		this.embeddedPDP.dispose();
	}

}
