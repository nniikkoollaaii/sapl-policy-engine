package io.sapl.test.unit;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.Decision;
import io.sapl.test.SaplTestFixture;
import io.sapl.test.SaplUnitTestFixture;

public class PolicyWithObligationTest {
	
	private SaplTestFixture fixture;
	
	private ObjectMapper mapper;

	@Before
	public void setUp() {
		fixture = new SaplUnitTestFixture("policyWithObligation");
		mapper = new ObjectMapper();
	}

	
	@Test
	public void test_policyWithObligation() {
		ObjectNode obligation = mapper.createObjectNode();
		obligation.put("type", "logAccess");
		obligation.put("message", "willi has accessed patient data 56 as an administrator.");
		ArrayNode obligations = mapper.createArrayNode();
		obligations.set(0, obligation);
		AuthorizationDecision decision = new AuthorizationDecision(Decision.PERMIT).withObligations(obligations);
			
		fixture.constructTestCase()
			.when(AuthorizationSubscription.of("willi", "read", "something"))
			.expect(decision)
			.verify();
		
	}
}
