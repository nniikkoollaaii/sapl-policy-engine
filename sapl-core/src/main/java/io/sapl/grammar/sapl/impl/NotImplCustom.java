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

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.interpreter.EvaluationContext;
import reactor.core.publisher.Flux;

public class NotImplCustom extends NotImpl {

	@Override
	public Flux<Optional<JsonNode>> evaluate(EvaluationContext ctx, boolean isBody, Optional<JsonNode> relativeNode) {
		return expression.evaluate(ctx, isBody, relativeNode).flatMap(Value::toBoolean).map(bool -> !bool)
				.map(Value::of).distinctUntilChanged();
	}

}
