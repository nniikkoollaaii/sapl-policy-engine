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
package io.sapl.grammar.sapl.impl;

import static io.sapl.grammar.sapl.impl.util.TestUtil.expressionErrors;
import static io.sapl.grammar.sapl.impl.util.TestUtil.expressionEvaluatesTo;

import java.io.IOException;

import org.junit.Test;

import io.sapl.api.interpreter.Val;
import io.sapl.grammar.sapl.impl.util.ArrayUtil;
import io.sapl.grammar.sapl.impl.util.MockUtil;
import io.sapl.grammar.sapl.impl.util.ParserUtil;
import io.sapl.interpreter.EvaluationContext;
import reactor.test.StepVerifier;

public class ApplyStepsRecursiveWildcardTest {

	private static final EvaluationContext CTX = MockUtil.constructTestEnvironmentPdpScopedEvaluationContext();

	@Test
	public void stepPropagatesErrors() {
		expressionErrors(CTX, "(10/0)..*");
	}

	@Test
	public void stepOnUndefinedEmpty() {
		expressionErrors(CTX, "undefined..*");
	}

	@Test
	public void apptlyToNull() {
		expressionEvaluatesTo(CTX, "null..*", "[]");
	}

	@Test
	public void applyToArray() {
		var expression = "[1,2,[3,4,5], { \"key\" : [6,7,8], \"key2\": { \"key3\" : 9 } }]..*";
		var expected = "[1,2,[3,4,5],3,4,5,{\"key\":[6,7,8],\"key2\":{\"key3\":9}},[6,7,8],6,7,8,{\"key3\":9},9]";
		expressionEvaluatesTo(CTX, expression, expected);
	}

	@Test
	public void filterArray() {
		var expression = "[1,2,[3,4,5], { \"key\" : [6,7,8], \"key2\": { \"key3\" : 9 } }] |- { @..* : mock.nil }";
		var expected = "[null,null,null,null]";
		expressionEvaluatesTo(CTX, expression, expected);
	}

	@Test
	public void applyToObject() throws IOException {
		var expression = "{ \"key\" : \"value1\", \"array1\" : [ { \"key\" : \"value2\" }, { \"key\" : \"value3\" } ], \"array2\" : [ 1, 2, 3, 4, 5 ]}..*";
		var expected = Val.ofJson(
				"[1,2,3,4,5,\"value1\",[{\"key\":\"value2\"},{\"key\":\"value3\"}],{\"key\":\"value2\"},\"value2\",{\"key\":\"value3\"},\"value3\",[1,2,3,4,5]]");
		expressionEvaluatesTo(CTX, "null..*", "[]");
		StepVerifier.create(ParserUtil.expression(expression).evaluate(CTX, Val.UNDEFINED))
				.expectNextMatches(result -> ArrayUtil.arraysMatchWithSetSemantics(result, expected)).verifyComplete();
	}

}
