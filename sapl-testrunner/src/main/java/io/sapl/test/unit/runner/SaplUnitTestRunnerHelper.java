package io.sapl.test.unit.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.api.interpreter.InitializationException;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.grammar.sapl.Policy;
import io.sapl.interpreter.EvaluationContext;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint;

public class SaplUnitTestRunnerHelper {
	
	private Policy policy;
	private EvaluationContext ctx;
    
    /**
     * Constructor to instantiate SaplUnitTestRunnerHelper
     * @param policyId Id of Policy to test
     * @throws Exception
     */
	public SaplUnitTestRunnerHelper(Policy policy, EvaluationContext ctx) throws Exception {
		this.policy = policy;
		this.ctx = ctx;
	}	
	
	/**
	 * Evaluating policy under test with an AuthorizationSubscription object. Abstracting reactive behavior of policy engine in unit tests.
	 * @param authorizationSubscription 
	 * @return An AuthorizationDecision object
	 */
	public AuthorizationDecision decide(AuthorizationSubscription authorizationSubscription) {
		return this.policy.evaluate(ctx.forAuthorizationSubscription(authorizationSubscription)).blockFirst();
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
			return this.policy.evaluate(ctx.forAuthorizationSubscription(authSub)).blockFirst();
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
			return this.policy.evaluate(ctx.forAuthorizationSubscription(authSub)).blockFirst();
		}
		return null;
	}
}
