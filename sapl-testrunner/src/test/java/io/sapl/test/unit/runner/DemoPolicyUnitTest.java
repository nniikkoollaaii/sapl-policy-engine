package io.sapl.test.unit.runner;

import static org.junit.Assert.assertEquals;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.sapl.api.interpreter.InitializationException;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.Decision;
import io.sapl.test.unit.runner.PolicyId;
import io.sapl.test.unit.runner.PolicyPIP;
import io.sapl.test.unit.runner.PolicyPath;
import io.sapl.test.unit.runner.SaplTestrunnerJUnit;
import io.sapl.test.unit.runner.SaplUnitTestRunnerHelper;

@RunWith(SaplTestrunnerJUnit.class)
@PolicyId("policy 1")
@PolicyPath("C:\\Users\\Nikolai\\eclipse-sapl-workspace\\sapl-policy-engine\\sapl-testrunner\\src\\test\\resources\\policies")
@PolicyPIP({TestPIP.class})
public class DemoPolicyUnitTest {
	
	private SaplUnitTestRunnerHelper helper;
	
	public DemoPolicyUnitTest(SaplUnitTestRunnerHelper helper) {
		this.helper = helper;
	}
	

	
    @Test
    public void testPolicyViaAuthSubObject() {
    	//arrange
    	AuthorizationSubscription READ_SUB = AuthorizationSubscription.of("willi", "read", "something");
    	
    	//act
    	AuthorizationDecision decision = helper.decide(READ_SUB);
    	
    	//assert
    	Assertions.assertThat(decision.getDecision()).isEqualTo(Decision.PERMIT);
    	//assertEquals(decision.getDecision(), Decision.PERMIT);    	
    }
    
    
    @Test
    public void testPolicyViaAuthSubJSON() throws InitializationException {
    	//arrange
    	// Ab JDK 13 in Preview
    	/*
		String authSub = """
			{
				"subject"     : {
					"username"    : "alice",
					"tracking_id" : 1234321,
					"nda_signed"  : true
              	},
				"action"      : "HTTP:GET",
				"resource"    : "https://medical.org/api/patients/123",
				"environment" : null
			}
		""";
    	*/
    	String authSub = "{"
    			+ " \"subject\": \"willi\","
    			+ " \"action\"      : \"read\","
    			+ " \"resource\"    : \"something\""
    			+ "}";
    	
    	
    	//TODO: vgl. io.sapl.grammar.tests.SAPLParsingTest.xtend
    	
    	//act
    	AuthorizationDecision decision = helper.decide(authSub);
    	
    	//assert
    	Assertions.assertThat(decision.getDecision()).isEqualTo(Decision.PERMIT);
    	
    	
    }
    
    
    @Test
    public void testPolicyViaAuthSubJSONNode() {
    	//arrange
    	ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
    	jsonNode.put("subject", "willi");
    	jsonNode.put("action", "read");
    	jsonNode.put("resource", "something");
    	
    	//act
    	AuthorizationDecision decision = helper.decide(jsonNode);
    	
    	//assert
    	Assertions.assertThat(decision.getDecision()).isEqualTo(Decision.PERMIT);
    }
}
