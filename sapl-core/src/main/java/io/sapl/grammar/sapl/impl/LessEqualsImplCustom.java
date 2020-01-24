/**
 * Copyright © 2019 Dominic Heutelbeck (dominic.heutelbeck@gmail.com)
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

import java.math.BigDecimal;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.interpreter.EvaluationContext;
import reactor.core.publisher.Flux;

/**
 * Checks for a left value being less than or equal to a right value.
 *
 * Grammar: Comparison returns Expression: Prefixed (({LessEquals.left=current} '<=')
 * right=Prefixed)? ;
 */
public class LessEqualsImplCustom extends LessEqualsImpl {

	@Override
	public Flux<Optional<JsonNode>> evaluate(EvaluationContext ctx, boolean isBody, Optional<JsonNode> relativeNode) {
		final Flux<BigDecimal> left = getLeft().evaluate(ctx, isBody, relativeNode).flatMap(Value::toBigDecimal);
		final Flux<BigDecimal> right = getRight().evaluate(ctx, isBody, relativeNode).flatMap(Value::toBigDecimal);
		return Flux.combineLatest(left, right, this::lessOrEqual).map(Value::of).distinctUntilChanged();
	}

	private Boolean lessOrEqual(BigDecimal left, BigDecimal right) {
		return left.compareTo(right) <= 0;
	}

}
