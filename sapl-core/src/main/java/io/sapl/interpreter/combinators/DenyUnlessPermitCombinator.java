package io.sapl.interpreter.combinators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.interpreter.PolicyEvaluationException;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.Decision;
import io.sapl.grammar.sapl.Policy;
import io.sapl.grammar.sapl.SAPL;
import io.sapl.interpreter.EvaluationContext;
import io.sapl.interpreter.combinators.ObligationAdviceCollector.Type;
import io.sapl.interpreter.functions.FunctionContext;
import io.sapl.interpreter.pip.AttributeContext;
import io.sapl.interpreter.variables.VariableContext;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class DenyUnlessPermitCombinator implements DocumentsCombinator, PolicyCombinator {

	@Override
	public Flux<AuthorizationDecision> combineMatchingDocuments(Collection<SAPL> matchingSaplDocuments,
			boolean errorsInTarget, AuthorizationSubscription authzSubscription, AttributeContext attributeCtx,
			FunctionContext functionCtx, Map<String, JsonNode> systemVariables) {
		LOGGER.trace("|-- Combining matching documents");
		if (matchingSaplDocuments == null || matchingSaplDocuments.isEmpty()) {
			LOGGER.trace("| |-- No matches. Default to DENY");
			return Flux.just(AuthorizationDecision.DENY);
		}

		final VariableContext variableCtx;
		try {
			variableCtx = new VariableContext(authzSubscription, systemVariables);
		}
		catch (PolicyEvaluationException e) {
			return Flux.just(AuthorizationDecision.INDETERMINATE);
		}
		final EvaluationContext evaluationCtx = new EvaluationContext(attributeCtx, functionCtx, variableCtx);

		final List<Flux<AuthorizationDecision>> authzDecisionFluxes = new ArrayList<>(matchingSaplDocuments.size());
		for (SAPL document : matchingSaplDocuments) {
			LOGGER.trace("| |-- Evaluate: {} ({})", document.getPolicyElement().getSaplName(),
					document.getPolicyElement().getClass().getName());
			// do not first check match again. directly evaluate the rules
			authzDecisionFluxes.add(document.evaluate(evaluationCtx));
		}

		final AuthorizationDecisionAccumulator accumulator = new AuthorizationDecisionAccumulator();
		return Flux.combineLatest(authzDecisionFluxes, authzDecisions -> {
			accumulator.addSingleDecisions(authzDecisions);
			AuthorizationDecision result = accumulator.getCombinedAuthorizationDecision();
			LOGGER.trace("| |-- {} Combined AuthorizationDecision: {}", result.getDecision(), result);
			return result;
		}).distinctUntilChanged();
	}

	@Override
	public Flux<AuthorizationDecision> combinePolicies(List<Policy> policies, EvaluationContext ctx) {
		final List<Policy> matchingPolicies = new ArrayList<>();
		for (Policy policy : policies) {
			try {
				if (policy.matches(ctx)) {
					matchingPolicies.add(policy);
				}
			}
			catch (PolicyEvaluationException e) {
				// we won't further evaluate this policy
			}
		}

		if (matchingPolicies.isEmpty()) {
			return Flux.just(AuthorizationDecision.DENY);
		}

		final List<Flux<AuthorizationDecision>> authzDecisionFluxes = new ArrayList<>(matchingPolicies.size());
		for (Policy policy : matchingPolicies) {
			authzDecisionFluxes.add(policy.evaluate(ctx));
		}
		final AuthorizationDecisionAccumulator accumulator = new AuthorizationDecisionAccumulator();
		return Flux.combineLatest(authzDecisionFluxes, authzDecisions -> {
			accumulator.addSingleDecisions(authzDecisions);
			return accumulator.getCombinedAuthorizationDecision();
		}).distinctUntilChanged();
	}

	private static class AuthorizationDecisionAccumulator {

		private AuthorizationDecision authzDecision;

		private int permitCount;

		private boolean transformation;

		private ObligationAdviceCollector obligationAdvice;

		AuthorizationDecisionAccumulator() {
			init();
		}

		private void init() {
			permitCount = 0;
			transformation = false;
			obligationAdvice = new ObligationAdviceCollector();
			authzDecision = AuthorizationDecision.DENY;
		}

		void addSingleDecisions(Object... authzDecisions) {
			init();
			for (Object decision : authzDecisions) {
				addSingleDecision((AuthorizationDecision) decision);
			}
		}

		private void addSingleDecision(AuthorizationDecision newAuthzDecision) {
			if (newAuthzDecision.getDecision() == Decision.PERMIT) {
				permitCount += 1;
				if (newAuthzDecision.getResource().isPresent()) {
					transformation = true;
				}
				obligationAdvice.add(Decision.PERMIT, newAuthzDecision);
				authzDecision = newAuthzDecision;
			}
			else if (newAuthzDecision.getDecision() == Decision.DENY
					&& authzDecision.getDecision() != Decision.PERMIT) {
				obligationAdvice.add(Decision.DENY, newAuthzDecision);
			}
		}

		AuthorizationDecision getCombinedAuthorizationDecision() {
			if (authzDecision.getDecision() == Decision.PERMIT) {
				if (permitCount > 1 && transformation) {
					// Multiple applicable permit policies with at least one
					// transformation not
					// allowed.
					return AuthorizationDecision.DENY;
				}

				return new AuthorizationDecision(Decision.PERMIT, authzDecision.getResource(),
						obligationAdvice.get(Type.OBLIGATION, Decision.PERMIT),
						obligationAdvice.get(Type.ADVICE, Decision.PERMIT));
			}
			else {
				return new AuthorizationDecision(Decision.DENY, authzDecision.getResource(),
						obligationAdvice.get(Type.OBLIGATION, Decision.DENY),
						obligationAdvice.get(Type.ADVICE, Decision.DENY));
			}
		}

	}

}
