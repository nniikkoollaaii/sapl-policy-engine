/**
 * Copyright © 2017 Dominic Heutelbeck (dheutelbeck@ftk.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.sapl.grammar.sapl.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.interpreter.PolicyEvaluationException;
import io.sapl.interpreter.EvaluationContext;
import io.sapl.interpreter.selection.AbstractAnnotatedJsonNode;
import io.sapl.interpreter.selection.ArrayResultNode;
import io.sapl.interpreter.selection.JsonNodeWithParentArray;
import io.sapl.interpreter.selection.JsonNodeWithParentObject;
import io.sapl.interpreter.selection.ResultNode;
import reactor.core.publisher.Flux;

/**
 * Implements the expression subscript of an array (or object), written as
 * '[(Expression)]'.
 *
 * Returns the value of an attribute with a key or an array item with an index specified
 * by an expression. Expression must evaluate to a string or a number. If Expression
 * evaluates to a string, the selection can only be applied to an object. If Expression
 * evaluates to a number, the selection can only be applied to an array.
 *
 * Example: The expression step can be used to refer to custom variables
 * (object.array[(anIndex+2)]) or apply custom functions
 * (object.array[(max_value(object.array))].
 *
 * Grammar: Step: ... | '[' Subscript ']' | ... Subscript returns Step: ... |
 * {ExpressionStep} '(' expression=Expression ')' | ...
 */
public class ExpressionStepImplCustom extends ExpressionStepImpl {

	private static final String EXPRESSION_ACCESS_INDEX_NOT_FOUND = "Index not found. Failed to access item with index '%s' after expression evaluation.";

	private static final String EXPRESSION_ACCESS_TYPE_MISMATCH = "Type mismatch. Expression evaluates to '%s' which can not be used.";

	/**
	 * Applies the expression subscript to an abstract annotated JsonNode, which can
	 * either be an array or an object. If it is an array, the expression must evaluate to
	 * an integer which is used as the index to access the required array element. If it
	 * is an object, the expression must evaluate to a string which is used as the name of
	 * the attribute to be returned.
	 * @param previousResult the array or object
	 * @param ctx the evaluation context
	 * @param isBody a flag indicating whether the expression is part of a policy body
	 * @param relativeNode the relative node (not needed here)
	 * @return a flux of ArrayResultNodes containing the element/attribute value of the
	 * original array/object corresponding to the integer/string result (index/attribute
	 * name) retrieved by evaluating the expression
	 */
	@Override
	public Flux<ResultNode> apply(AbstractAnnotatedJsonNode previousResult, EvaluationContext ctx, boolean isBody,
			Optional<JsonNode> relativeNode) {
		return getExpression().evaluate(ctx, isBody, relativeNode).flatMap(expressionResult -> {
			try {
				return Flux.just(handleExpressionResultFor(previousResult, expressionResult));
			}
			catch (PolicyEvaluationException e) {
				return Flux.error(e);
			}
		});
	}

	private ResultNode handleExpressionResultFor(AbstractAnnotatedJsonNode previousResult, Optional<JsonNode> optResult)
			throws PolicyEvaluationException {
		JsonNode result = optResult.orElseThrow(() -> new PolicyEvaluationException("undefined value"));
		final Optional<JsonNode> previousResultNode = previousResult.getNode();
		if (result.isNumber()) {
			if (previousResultNode.isPresent() && previousResultNode.get().isArray()) {
				return handleArrayIndex(previousResultNode.get(), result);
			}
			else {
				// FIXME:
				// Maybe another message would be helpful. The problem here is not that
				// the
				// result type does not match, but the node the subscript is applied to is
				// either undefined or not an array.
				throw new PolicyEvaluationException(
						String.format(EXPRESSION_ACCESS_TYPE_MISMATCH, result.getNodeType()));
			}
		}
		else if (result.isTextual()) {
			if (previousResultNode.isPresent()) {
				return handleAttributeName(previousResultNode.get(), result);
			}
			else {
				// FIXME:
				// Maybe another message would be helpful. The problem here is not that
				// the
				// result type does not match, but the node the subscript is applied to is
				// undefined.
				throw new PolicyEvaluationException(
						String.format(EXPRESSION_ACCESS_TYPE_MISMATCH, result.getNodeType()));
			}
		}
		else {
			throw new PolicyEvaluationException(String.format(EXPRESSION_ACCESS_TYPE_MISMATCH, result.getNodeType()));
		}
	}

	private ResultNode handleArrayIndex(JsonNode previousResult, JsonNode result) throws PolicyEvaluationException {
		final int index = result.asInt();
		if (!previousResult.has(index)) {
			throw new PolicyEvaluationException(String.format(EXPRESSION_ACCESS_INDEX_NOT_FOUND, index));
		}
		return new JsonNodeWithParentArray(Optional.of(previousResult.get(index)), Optional.of(previousResult), index);
	}

	private ResultNode handleAttributeName(JsonNode previousResult, JsonNode result) {
		final String attribute = result.asText();
		if (!previousResult.has(attribute)) {
			return new JsonNodeWithParentObject(Optional.empty(), Optional.of(previousResult), attribute);
		}
		return new JsonNodeWithParentObject(Optional.of(previousResult.get(attribute)), Optional.of(previousResult),
				attribute);
	}

	/**
	 * Applies the expression subscript to an array. The expression must evaluate to an
	 * integer. This integer is used as the index to access the required array element.
	 * @param previousResult the array
	 * @param ctx the evaluation context
	 * @param isBody a flag indicating whether the expression is part of a policy body
	 * @param relativeNode the relative node (not needed here)
	 * @return a flux of ArrayResultNodes containing the element of the original array at
	 * the position corresponding to the integer result retrieved by evaluating the
	 * expression
	 */
	@Override
	public Flux<ResultNode> apply(ArrayResultNode previousResult, EvaluationContext ctx, boolean isBody,
			Optional<JsonNode> relativeNode) {
		return getExpression().evaluate(ctx, isBody, previousResult.asJsonWithoutAnnotations())
				.flatMap(Value::toJsonNode)
				.flatMap(expressionResult -> handleExpressionResultFor(previousResult, expressionResult));
	}

	private Flux<ResultNode> handleExpressionResultFor(ArrayResultNode previousResult, JsonNode result) {
		if (result.isNumber()) {
			int index = result.asInt();
			List<AbstractAnnotatedJsonNode> nodes = previousResult.getNodes();
			if (index < 0 || index >= nodes.size()) {
				return Flux
						.error(new PolicyEvaluationException(String.format(EXPRESSION_ACCESS_INDEX_NOT_FOUND, index)));
			}
			return Flux.just(nodes.get(index));
		}
		else {
			return Flux.error(new PolicyEvaluationException(
					String.format(EXPRESSION_ACCESS_TYPE_MISMATCH, result.getNodeType())));
		}
	}

	@Override
	public int hash(Map<String, String> imports) {
		int hash = 17;
		hash = 37 * hash + Objects.hashCode(getClass().getTypeName());
		hash = 37 * hash + ((getExpression() == null) ? 0 : getExpression().hash(imports));
		return hash;
	}

	@Override
	public boolean isEqualTo(EObject other, Map<String, String> otherImports, Map<String, String> imports) {
		if (this == other)
			return true;
		if (other == null || getClass() != other.getClass())
			return false;
		final ExpressionStepImplCustom otherImpl = (ExpressionStepImplCustom) other;
		return (getExpression() == null) ? (getExpression() == otherImpl.getExpression())
				: getExpression().isEqualTo(otherImpl.getExpression(), otherImports, imports);
	}

}
