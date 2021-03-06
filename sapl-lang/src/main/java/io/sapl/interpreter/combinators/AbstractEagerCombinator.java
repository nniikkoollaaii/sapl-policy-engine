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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.sapl.api.interpreter.Val;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.prp.PolicyRetrievalResult;
import io.sapl.grammar.sapl.AuthorizationDecisionEvaluable;
import io.sapl.grammar.sapl.Policy;
import io.sapl.interpreter.EvaluationContext;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Slf4j
public abstract class AbstractEagerCombinator implements DocumentsCombinator {


	@Override
	public Flux<AuthorizationDecision> combineMatchingDocuments(PolicyRetrievalResult policyRetrievalResult,
			EvaluationContext evaluationCtx) {
		log.debug("|-- Combining matching documents");
		var matchingSaplDocuments = policyRetrievalResult.getMatchingDocuments();
		final List<Flux<AuthorizationDecision>> authzDecisionFluxes = new ArrayList<>(matchingSaplDocuments.size());
		for (AuthorizationDecisionEvaluable document : matchingSaplDocuments) {
			log.debug("| |-- Evaluate: {} ", document);
			authzDecisionFluxes.add(document.evaluate(evaluationCtx));
		}
		if (matchingSaplDocuments.isEmpty()) {
			return Flux.just(combineDecisions(new AuthorizationDecision[0], policyRetrievalResult.isErrorsInTarget()));
		}
		return Flux
				.combineLatest(authzDecisionFluxes,
						decisions -> Arrays.copyOf(decisions, decisions.length, AuthorizationDecision[].class))
				.map(decisions -> combineDecisions(decisions, policyRetrievalResult.isErrorsInTarget()));
	}

	protected Flux<AuthorizationDecision> doCombinePolicies(List<Policy> policies, EvaluationContext ctx) {
		return Flux.fromIterable(policies)
				.concatMap(policy -> policy.matches(ctx).map(matches -> Tuples.of(matches, policy)))
				.reduce(new PolicyRetrievalResult(), (state, matchAndDocument) -> {
					PolicyRetrievalResult newState = state;
					if (isMatch(matchAndDocument))
						newState = state.withMatch(matchAndDocument.getT2());
					if (isError(matchAndDocument))
						newState = state.withError();
					return newState;
				}).flux().flatMap(policyRetrievalResult -> combineMatchingDocuments(policyRetrievalResult, ctx));
	}

	private boolean isMatch(Tuple2<Val, Policy> matchAndDocument) {
		return matchAndDocument.getT1().isBoolean() && matchAndDocument.getT1().getBoolean();
	}

	private boolean isError(Tuple2<Val, Policy> matchAndDocument) {
		return matchAndDocument.getT1().isError();
	}

	protected abstract AuthorizationDecision combineDecisions(AuthorizationDecision[] decisions,
			boolean errorsInTarget);
	
}
