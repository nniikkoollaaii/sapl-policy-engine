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
package io.sapl.interpreter.combinators;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.Decision;
import io.sapl.interpreter.DefaultSAPLInterpreter;
import io.sapl.interpreter.EvaluationContext;
import io.sapl.interpreter.functions.AnnotationFunctionContext;
import io.sapl.interpreter.pip.AnnotationAttributeContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DenyOverridesTest {

	private static final DefaultSAPLInterpreter INTERPRETER = new DefaultSAPLInterpreter();

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	private static final AuthorizationSubscription EMPTY_AUTH_SUBSCRIPTION = new AuthorizationSubscription(null, null,
			null, null);

	private static final AuthorizationSubscription AUTH_SUBSCRIPTION_WITH_TRUE_RESOURCE = new AuthorizationSubscription(
			null, null, JSON.booleanNode(true), null);

	private EvaluationContext evaluationCtx;

	@Before
	public void setUp() {
		var attributeCtx = new AnnotationAttributeContext();
		var functionCtx = new AnnotationFunctionContext();
		evaluationCtx = new EvaluationContext(attributeCtx, functionCtx, new HashMap<>());
	}

	@Test
	public void permit() {
		String policySet = "set \"tests\" deny-overrides" + " policy \"testp\" permit";

		assertEquals("should return permit if the only policy evaluates to permit", Decision.PERMIT,
				INTERPRETER.evaluate(EMPTY_AUTH_SUBSCRIPTION, policySet, evaluationCtx).blockFirst().getDecision());
	}

	@Test
	public void deny() {
		String policySet = "set \"tests\" deny-overrides" + " policy \"testp\" deny";

		assertEquals("should return deny if the only policy evaluates to deny", Decision.DENY,
				INTERPRETER.evaluate(EMPTY_AUTH_SUBSCRIPTION, policySet, evaluationCtx).blockFirst().getDecision());
	}

	@Test
	public void notApplicableTarget() {
		String policySet = "set \"tests\" deny-overrides" + " policy \"testp\" deny true == false";

		assertEquals("should return not applicable if the only policy target evaluates to not applicable",
				Decision.NOT_APPLICABLE,
				INTERPRETER.evaluate(EMPTY_AUTH_SUBSCRIPTION, policySet, evaluationCtx).blockFirst().getDecision());
	}

	@Test
	public void notApplicableCondition() {
		String policySet = "set \"tests\" deny-overrides" + " policy \"testp\" deny where true == false;";

		assertEquals("should return not applicable if the only policy condition evaluates to not applicable",
				Decision.NOT_APPLICABLE,
				INTERPRETER.evaluate(EMPTY_AUTH_SUBSCRIPTION, policySet, evaluationCtx).blockFirst().getDecision());
	}

	@Test
	public void indeterminateTarget() {
		String policySet = "set \"tests\" deny-overrides" + " policy \"testp\" permit \"a\" < 5";

		assertEquals("should return indeterminate if the only target is indeterminate", Decision.INDETERMINATE,
				INTERPRETER.evaluate(EMPTY_AUTH_SUBSCRIPTION, policySet, evaluationCtx).blockFirst().getDecision());
	}

	@Test
	public void indeterminateCondition() {
		String policySet = "set \"tests\" deny-overrides" + " policy \"testp\" permit where \"a\" < 5;";

		assertEquals("should return indeterminate if the only condition is indeterminate", Decision.INDETERMINATE,
				INTERPRETER.evaluate(EMPTY_AUTH_SUBSCRIPTION, policySet, evaluationCtx).blockFirst().getDecision());
	}

	@Test
	public void permitDeny() {
		String policySet = "set \"tests\" deny-overrides" + " policy \"testp1\" permit" + " policy \"testp2\" deny";

		assertEquals("should return deny if any policy evaluates to deny", Decision.DENY,
				INTERPRETER.evaluate(EMPTY_AUTH_SUBSCRIPTION, policySet, evaluationCtx).blockFirst().getDecision());
	}

	@Test
	public void denyIndeterminate() {
		String policySet = "set \"tests\" deny-overrides" + " policy \"testp1\" deny"
				+ " policy \"testp2\" deny where \"a\" > 5;";

		assertEquals("should return deny if any policy evaluates to deny", Decision.DENY,
				INTERPRETER.evaluate(EMPTY_AUTH_SUBSCRIPTION, policySet, evaluationCtx).blockFirst().getDecision());
	}

	@Test
	public void permitNotApplicableDeny() {
		String policySet = "set \"tests\" deny-overrides" + " policy \"testp1\" permit"
				+ " policy \"testp2\" permit true == false" + " policy \"testp3\" deny";

		assertEquals("should return deny if any policy evaluates to deny", Decision.DENY,
				INTERPRETER.evaluate(EMPTY_AUTH_SUBSCRIPTION, policySet, evaluationCtx).blockFirst().getDecision());
	}

	@Test
	public void permitNotApplicableIndeterminateDeny() {
		String policySet = "set \"tests\" deny-overrides" + " policy \"testp1\" permit"
				+ " policy \"testp2\" permit true == false" + " policy \"testp3\" permit \"a\" > 5"
				+ " policy \"testp4\" deny" + " policy \"testp5\" permit";

		assertEquals("should return deny if any policy evaluates to deny", Decision.DENY,
				INTERPRETER.evaluate(EMPTY_AUTH_SUBSCRIPTION, policySet, evaluationCtx).blockFirst().getDecision());
	}

	@Test
	public void permitIndeterminateNotApplicable() {
		String policySet = "set \"tests\" deny-overrides" + " policy \"testp1\" permit"
				+ " policy \"testp2\" deny \"a\" < 5" + " policy \"testp3\" deny true == false";

		assertEquals("should return indeterminate if only indeterminate, permit and not applicable present",
				Decision.INDETERMINATE,
				INTERPRETER.evaluate(EMPTY_AUTH_SUBSCRIPTION, policySet, evaluationCtx).blockFirst().getDecision());
	}

	@Test
	public void multiplePermitTransformation() {
		String policySet = "set \"tests\" deny-overrides" + " policy \"testp1\" permit transform false"
				+ " policy \"testp2\" permit transform true";

		assertEquals(
				"should return indeterminate if final decision would be permit and there is a transformation incertainty",
				Decision.INDETERMINATE,
				INTERPRETER.evaluate(EMPTY_AUTH_SUBSCRIPTION, policySet, evaluationCtx).blockFirst().getDecision());
	}

	@Test
	public void multiplePermitTransformationDeny() {
		String policySet = "set \"tests\" deny-overrides" + " policy \"testp1\" permit"
				+ " policy \"testp2\" permit transform true" + " policy \"testp3\" deny";

		assertEquals("should return deny if final decision would be deny and there is a transformation incertainty",
				Decision.DENY,
				INTERPRETER.evaluate(EMPTY_AUTH_SUBSCRIPTION, policySet, evaluationCtx).blockFirst().getDecision());
	}

	@Test
	public void singlePermitTransformation() {
		String policySet = "set \"tests\" deny-overrides" + " policy \"testp\" permit transform true";

		assertEquals("should return permit if there is no transformation incertainty", Decision.PERMIT,
				INTERPRETER.evaluate(EMPTY_AUTH_SUBSCRIPTION, policySet, evaluationCtx).blockFirst().getDecision());
	}

	@Test
	public void singlePermitTransformationResource() {
		String policySet = "set \"tests\" deny-overrides" + " policy \"testp\" permit transform true";

		assertEquals("should return resource if there is no transformation incertainty",
				Optional.of(JSON.booleanNode(true)),
				INTERPRETER.evaluate(EMPTY_AUTH_SUBSCRIPTION, policySet, evaluationCtx).blockFirst().getResource());
	}

	@Test
	public void multiplePermitNoTransformation() {
		String policySet = "set \"tests\" deny-overrides" + " policy \"testp1\" permit" + " policy \"testp2\" permit";

		assertEquals("should return permit if there is no transformation incertainty", Decision.PERMIT, INTERPRETER
				.evaluate(AUTH_SUBSCRIPTION_WITH_TRUE_RESOURCE, policySet, evaluationCtx).blockFirst().getDecision());
	}

	@Test
	public void collectObligationDeny() {
		String policySet = "set \"tests\" deny-overrides"
				+ " policy \"testp1\" deny obligation \"obligation1\" advice \"advice1\""
				+ " policy \"testp2\" deny obligation \"obligation2\" advice \"advice2\""
				+ " policy \"testp3\" permit obligation \"obligation3\" advice \"advice3\""
				+ " policy \"testp4\" deny false obligation \"obligation4\" advice \"advice4\"";

		ArrayNode obligation = JSON.arrayNode();
		obligation.add(JSON.textNode("obligation1"));
		obligation.add(JSON.textNode("obligation2"));

		assertEquals("should collect all deny obligation", Optional.of(obligation),
				INTERPRETER.evaluate(AUTH_SUBSCRIPTION_WITH_TRUE_RESOURCE, policySet, evaluationCtx).blockFirst()
						.getObligations());
	}

	@Test
	public void collectAdviceDeny() {
		String policySet = "set \"tests\" deny-overrides"
				+ " policy \"testp1\" deny obligation \"obligation1\" advice \"advice1\""
				+ " policy \"testp2\" deny obligation \"obligation2\" advice \"advice2\""
				+ " policy \"testp3\" permit obligation \"obligation3\" advice \"advice3\""
				+ " policy \"testp4\" deny false obligation \"obligation4\" advice \"advice4\"";

		ArrayNode advice = JSON.arrayNode();
		advice.add(JSON.textNode("advice1"));
		advice.add(JSON.textNode("advice2"));

		assertEquals("should collect all deny advice", Optional.of(advice), INTERPRETER
				.evaluate(AUTH_SUBSCRIPTION_WITH_TRUE_RESOURCE, policySet, evaluationCtx).blockFirst().getAdvices());
	}

	@Test
	public void collectObligationPermit() {
		String policySet = "set \"tests\" deny-overrides"
				+ " policy \"testp1\" permit obligation \"obligation1\" advice \"advice1\""
				+ " policy \"testp2\" permit obligation \"obligation2\" advice \"advice2\""
				+ " policy \"testp3\" deny false obligation \"obligation3\" advice \"advice3\""
				+ " policy \"testp4\" deny where false; obligation \"obligation4\" advice \"advice4\"";

		ArrayNode obligation = JSON.arrayNode();
		obligation.add(JSON.textNode("obligation1"));
		obligation.add(JSON.textNode("obligation2"));

		assertEquals("should collect all permit obligation", Optional.of(obligation),
				INTERPRETER.evaluate(AUTH_SUBSCRIPTION_WITH_TRUE_RESOURCE, policySet, evaluationCtx).blockFirst()
						.getObligations());
	}

	@Test
	public void collectAdvicePermit() {
		try {
			String policySet = "set \"tests\" deny-overrides"
					+ " policy \"testp1\" permit obligation \"obligation1\" advice \"advice1\""
					+ " policy \"testp2\" permit obligation \"obligation2\" advice \"advice2\""
					+ " policy \"testp3\" deny false obligation \"obligation3\" advice \"advice3\""
					+ " policy \"testp4\" deny where false; obligation \"obligation4\" advice \"advice4\"";

			ArrayNode advice = JSON.arrayNode();
			advice.add(JSON.textNode("advice1"));
			advice.add(JSON.textNode("advice2"));

			log.info("->{}",
					INTERPRETER.evaluate(AUTH_SUBSCRIPTION_WITH_TRUE_RESOURCE, policySet, evaluationCtx).blockFirst());


//			assertEquals("should collect all permit advice", Optional.of(advice),
//					INTERPRETER.evaluate(AUTH_SUBSCRIPTION_WITH_TRUE_RESOURCE, policySet, attributeCtx, functionCtx,
//							SYSTEM_VARIABLES).blockFirst().getAdvices());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
