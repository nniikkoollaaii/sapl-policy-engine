package io.sapl.interpreter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;

import io.sapl.api.interpreter.DocumentAnalysisResult;
import io.sapl.api.interpreter.DocumentType;
import io.sapl.api.interpreter.InitializationException;
import io.sapl.api.interpreter.PolicyEvaluationException;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.prp.PolicyRetrievalResult;
import io.sapl.functions.FilterFunctionLibrary;
import io.sapl.grammar.sapl.SAPL;
import io.sapl.grammar.sapl.impl.util.EObjectUtil;
import io.sapl.interpreter.functions.AnnotationFunctionContext;
import io.sapl.interpreter.pip.AnnotationAttributeContext;
import io.sapl.interpreter.pip.TestPIP;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

public class UnitTestSAPLInterpreterTester {

	private static final String AUTHZ_SUBSCRIPTION_JSON = "{ " + "\"subject\" : { " + "\"id\" : \"1234\","
			+ "\"organizationId\" : \"5678\"," + "\"isActive\" : true," + "\"granted_authorities\" : { "
			+ "\"roles\"  : [ \"USER\", \"ACCOUNTANT\" ], " + "\"groups\" : [ \"OPERATORS\", \"DEVELOPERS\" ] " + " }"
			+ " }," + "\"action\" : { " + "\"verb\" : \"withdraw_funds\", " + "\"parameters\" : [ 200.00 ]" + "},"
			+ "\"resource\" : { " + "\"url\" : \"http://api.bank.com/accounts/12345\"," + "\"id\" : \"9012\","
			+ "\"emptyArray\" : []," + "\"textArray\" : [ \"one\", \"two\" ]," + "\"emptyObject\" : {},"
			+ "\"objectArray\" : [ {\"id\" : \"1\", \"name\" : \"one\"}, {\"id\" : \"2\", \"name\" : \"two\"} ] " + "},"
			+ "\"environment\" : { " + "\"ipAddress\" : \"10.10.10.254\"," + "\"year\" : 2016" + "}" + " }";
	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;
	private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
	private static final String POLICY_ID = "test";
	private static final String POLICY_ID_NOT_MATCHING = "not_matching";
	private static final UnitTestSAPLInterpreter INTERPRETER = new UnitTestSAPLInterpreter(POLICY_ID);

	private EvaluationContext evaluationCtx;
	private AuthorizationSubscription authzSubscription;

	@Before
	public void setUp() throws JsonProcessingException, InitializationException {
		Hooks.onOperatorDebug();
		authzSubscription = MAPPER.readValue(AUTHZ_SUBSCRIPTION_JSON, AuthorizationSubscription.class);
		var attributeCtx = new AnnotationAttributeContext();
		attributeCtx.loadPolicyInformationPoint(new TestPIP());
		var functionCtx = new AnnotationFunctionContext();
		functionCtx.loadLibrary(new SimpleFunctionLibrary());
		functionCtx.loadLibrary(new FilterFunctionLibrary());
		evaluationCtx = new EvaluationContext(attributeCtx, functionCtx, new HashMap<>());
	}

	@Test
	public void matchPolicyInUnitTestMode() {
		final String policyDocument = "policy \"" + POLICY_ID + "\" permit";
		SAPL document = INTERPRETER.parse(policyDocument);
		var match = document.matches(evaluationCtx).block();
		assertEquals(true, match.getBoolean());
	}
	
	@Test
	public void notMatchPolicyInUnitTestMode() {
		final String policyDocument = "policy \"" + POLICY_ID_NOT_MATCHING + "\" permit";
		SAPL document = INTERPRETER.parse(policyDocument);
		var match = document.matches(evaluationCtx).block();
		assertEquals(false, match.getBoolean());
	}
}
