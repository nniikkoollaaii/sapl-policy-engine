/*
 * Copyright © 2017-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sapl.interpreter;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.sapl.api.interpreter.InitializationException;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.Decision;
import io.sapl.functions.FilterFunctionLibrary;
import io.sapl.interpreter.functions.AnnotationFunctionContext;
import io.sapl.interpreter.pip.AnnotationAttributeContext;
import reactor.core.publisher.Hooks;

public class DefaultSAPLInterpreterPolicySetTest {

	private static final DefaultSAPLInterpreter INTERPRETER = new DefaultSAPLInterpreter();

	private EvaluationContext evaluationCtx;

	private AuthorizationSubscription authzSubscription;

	@Before
	public void setUp() throws InitializationException {
		Hooks.onOperatorDebug();
		authzSubscription = new AuthorizationSubscription(null, null, null, null);
		var attributeCtx = new AnnotationAttributeContext();
		var functionCtx = new AnnotationFunctionContext();
		functionCtx.loadLibrary(new FilterFunctionLibrary());
		evaluationCtx = new EvaluationContext(attributeCtx, functionCtx, new HashMap<>());
	}

	@Test
	public void setPermit() {
		String policySet = "set \"tests\" deny-overrides " + "policy \"testp\" permit";
		AuthorizationDecision expected = AuthorizationDecision.PERMIT;
		AuthorizationDecision actual = INTERPRETER.evaluate(authzSubscription, policySet, evaluationCtx).blockFirst();
		assertEquals("simple policy set should evaluate to permit", expected, actual);
	}

	@Test
	public void setDeny() {
		String policySet = "set \"tests\" deny-overrides " + "policy \"testp\" deny";
		AuthorizationDecision expected = AuthorizationDecision.DENY;
		AuthorizationDecision actual = INTERPRETER.evaluate(authzSubscription, policySet, evaluationCtx).blockFirst();
		assertEquals("simple policy set should evaluate to deny", expected, actual);
	}

	@Test
	public void setNotApplicable() {
		String policySet = "set \"tests\" deny-overrides " + "for true == false " + "policy \"testp\" deny";
		AuthorizationDecision expected = AuthorizationDecision.NOT_APPLICABLE;
		AuthorizationDecision actual = INTERPRETER.evaluate(authzSubscription, policySet, evaluationCtx).blockFirst();
		assertEquals("simple policy set should evaluate to not applicable", expected, actual);
	}

	@Test
	public void noApplicablePolicies() {
		String policySet = "set \"tests\" deny-overrides " + "for true " + "policy \"testp\" deny true == false";
		AuthorizationDecision expected = AuthorizationDecision.NOT_APPLICABLE;
		AuthorizationDecision actual = INTERPRETER.evaluate(authzSubscription, policySet, evaluationCtx).blockFirst();
		assertEquals("set with no applicable policies should evaluate to not applicable", expected, actual);
	}

	@Test
	public void setIndeterminate() {
		String policySet = "set \"tests\" deny-overrides" + "for \"a\" > 4 " + "policy \"testp\" permit";
		AuthorizationDecision expected = AuthorizationDecision.INDETERMINATE;
		AuthorizationDecision actual = INTERPRETER.evaluate(authzSubscription, policySet, evaluationCtx).blockFirst();
		assertEquals("simple policy set should evaluate to indeterminate", expected, actual);
	}

	@Test
	public void denyOverridesPermitAndDeny() {
		String policySet = "set \"tests\" deny-overrides " + "policy \"testp1\" permit " + "policy \"testp2\" deny";
		AuthorizationDecision expected = AuthorizationDecision.DENY;
		AuthorizationDecision actual = INTERPRETER.evaluate(authzSubscription, policySet, evaluationCtx).blockFirst();
		assertEquals("algorithm should return deny if any policy evaluates to deny", expected, actual);
	}

	@Test
	public void denyOverridesPermitAndNotApplicableAndDeny() {
		String policySet = "set \"tests\" deny-overrides " + "policy \"testp1\" permit "
				+ "policy \"testp2\" permit true == false " + "policy \"testp3\" deny";
		AuthorizationDecision expected = AuthorizationDecision.DENY;
		AuthorizationDecision actual = INTERPRETER.evaluate(authzSubscription, policySet, evaluationCtx).blockFirst();
		assertEquals("algorithm should return deny if any policy evaluates to deny", expected, actual);
	}

	@Test
	public void denyOverridesPermitAndIntederminateAndDeny() {
		String policySet = "set \"tests\" deny-overrides " + //
				"policy \"testp1\" permit " + //
				"policy \"testp2\" permit \"a\" < 5 " + //
				"policy \"testp3\" deny";
		AuthorizationDecision expected = AuthorizationDecision.DENY;
		AuthorizationDecision actual = INTERPRETER.evaluate(authzSubscription, policySet, evaluationCtx).blockFirst();
		assertEquals("algorithm should return deny if any policy evaluates to deny", expected, actual);
	}

	@Test
	public void importsInSetAvailableInPolicy() {
		String policySet = "import filter.replace " + //
				"set \"tests\" deny-overrides " + //
				"policy \"testp1\" permit transform true |- replace(false)";
		Optional<BooleanNode> expected = Optional.of(JsonNodeFactory.instance.booleanNode(false));
		Optional<JsonNode> actual = INTERPRETER.evaluate(authzSubscription, policySet, evaluationCtx).blockFirst()
				.getResource();
		assertEquals("imports for policy set must be available in policies", expected, actual);
	}

	@Test
	public void importsDuplicatesByPolicySet() {
		String policySet = "import filter.replace " + //
				"import filter.replace " + //
				"set \"tests\" deny-overrides " + //
				"policy \"testp1\" permit where true;";
		AuthorizationDecision expected = AuthorizationDecision.INDETERMINATE;
		AuthorizationDecision actual = INTERPRETER.evaluate(authzSubscription, policySet, evaluationCtx).blockFirst();
		assertEquals("imports for policy set must not contain duplicates", expected, actual);
	}

	@Test
	public void variablesOnSetLevel() {
		String policySet = "set \"tests\" deny-overrides " + //
				"var var1 = true; " + //
				"policy \"testp1\" permit var1 == true";
		Decision expected = Decision.PERMIT;
		Decision actual = INTERPRETER.evaluate(authzSubscription, policySet, evaluationCtx).blockFirst().getDecision();
		assertEquals("variables defined for policy set must be available in policies", expected, actual);
	}

	@Test
	public void variablesOnSetLevelError() {
		String policySet = "set \"tests\" deny-overrides " + //
				"var var1 = true / null; " + //
				"policy \"testp1\" permit";
		AuthorizationDecision expected = AuthorizationDecision.INDETERMINATE;
		AuthorizationDecision actual = INTERPRETER.evaluate(authzSubscription, policySet, evaluationCtx).blockFirst();
		assertEquals("error in policy set variable definition should lead to indeterminate", expected, actual);
	}

	@Test
	public void variablesOverwriteInPolicy() {
		String policySet = "set \"tests\" deny-overrides " + //
				"var var1 = true; " + //
				"policy \"testp1\" permit where var var1 = 10; var1 == 10; " + //
				"policy \"testp2\" deny where !(var1 == true);";
		Decision expected = Decision.PERMIT;
		Decision actual = INTERPRETER.evaluate(authzSubscription, policySet, evaluationCtx).blockFirst().getDecision();
		assertEquals("it should be possible to overwrite a variable in a policy", expected, actual);
	}

	@Test
	public void subjectAsVariable() {
		String policySet = "set \"test\" deny-overrides " + //
				"var subject = null;  " + //
				"policy \"test\" permit";
		AuthorizationDecision expected = AuthorizationDecision.INDETERMINATE;
		AuthorizationDecision actual = INTERPRETER.evaluate(authzSubscription, policySet, evaluationCtx).blockFirst();
		assertEquals("'subject' as variable name should evaluate to indeterminate", expected, actual);
	}

	@Test
	public void variablesInPolicyMustNotLeakIntoNextPolicy() {
		String policySet = "set \"test\" deny-overrides " + "var ps1 = true; " + //
				"policy \"pol1\" permit where var p1 = 10; p1 == 10; " + //
				"policy \"pol2\" deny where p1 == undefined;";
		AuthorizationDecision expected = AuthorizationDecision.DENY;
		AuthorizationDecision actual = INTERPRETER.evaluate(authzSubscription, policySet, evaluationCtx).blockFirst();
		assertEquals("variable p1 from policy pol1 should not be defined in policy pol2", expected, actual);
	}

}
