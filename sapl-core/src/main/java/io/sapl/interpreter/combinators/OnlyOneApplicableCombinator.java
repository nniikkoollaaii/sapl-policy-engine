package io.sapl.interpreter.combinators;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.interpreter.PolicyEvaluationException;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.grammar.sapl.Policy;
import io.sapl.grammar.sapl.SAPL;
import io.sapl.interpreter.EvaluationContext;
import io.sapl.interpreter.functions.FunctionContext;
import io.sapl.interpreter.pip.AttributeContext;
import io.sapl.interpreter.variables.VariableContext;
import reactor.core.publisher.Flux;

public class OnlyOneApplicableCombinator implements DocumentsCombinator, PolicyCombinator {

	@Override
	public Flux<AuthorizationDecision> combineMatchingDocuments(Collection<SAPL> matchingSaplDocuments,
			boolean errorsInTarget, AuthorizationSubscription authzSubscription, AttributeContext attributeCtx,
			FunctionContext functionCtx, Map<String, JsonNode> systemVariables) {

		if (errorsInTarget || matchingSaplDocuments.size() > 1) {
			return Flux.just(AuthorizationDecision.INDETERMINATE);
		}
		else if (matchingSaplDocuments.size() == 1) {
			final VariableContext variableCtx;
			try {
				variableCtx = new VariableContext(authzSubscription, systemVariables);
			}
			catch (PolicyEvaluationException e) {
				return Flux.just(AuthorizationDecision.INDETERMINATE);
			}
			final EvaluationContext evaluationCtx = new EvaluationContext(attributeCtx, functionCtx, variableCtx);

			final SAPL matchingDocument = matchingSaplDocuments.iterator().next();
			return matchingDocument.evaluate(evaluationCtx);
		}
		else {
			return Flux.just(AuthorizationDecision.NOT_APPLICABLE);
		}
	}

	@Override
	public Flux<AuthorizationDecision> combinePolicies(List<Policy> policies, EvaluationContext ctx) {
		Policy matchingPolicy = null;
		for (Policy policy : policies) {
			try {
				if (policy.matches(ctx)) {
					if (matchingPolicy != null) {
						return Flux.just(AuthorizationDecision.INDETERMINATE);
					}
					matchingPolicy = policy;
				}
			}
			catch (PolicyEvaluationException e) {
				return Flux.just(AuthorizationDecision.INDETERMINATE);
			}
		}

		if (matchingPolicy == null) {
			return Flux.just(AuthorizationDecision.NOT_APPLICABLE);
		}

		return matchingPolicy.evaluate(ctx);
	}

}
