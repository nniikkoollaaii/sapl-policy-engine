package io.sapl.grammar.sapl.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.interpreter.PolicyEvaluationException;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.grammar.sapl.ValueDefinition;
import io.sapl.interpreter.DependentStreamsUtil;
import io.sapl.interpreter.EvaluationContext;
import io.sapl.interpreter.FluxProvider;
import io.sapl.interpreter.Void;
import io.sapl.interpreter.combinators.DenyOverridesCombinator;
import io.sapl.interpreter.combinators.DenyUnlessPermitCombinator;
import io.sapl.interpreter.combinators.FirstApplicableCombinator;
import io.sapl.interpreter.combinators.OnlyOneApplicableCombinator;
import io.sapl.interpreter.combinators.PermitOverridesCombinator;
import io.sapl.interpreter.combinators.PermitUnlessDenyCombinator;
import io.sapl.interpreter.combinators.PolicyCombinator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class PolicySetImplCustom extends PolicySetImpl {

	/**
	 * Evaluates the body of the policy set within the given evaluation context and
	 * returns a {@link Flux} of {@link AuthorizationDecision} objects.
	 * @param ctx the evaluation context in which the policy set's body is evaluated. It
	 * must contain
	 * <ul>
	 * <li>the attribute context</li>
	 * <li>the function context</li>
	 * <li>the variable context holding the four authorization subscription variables
	 * 'subject', 'action', 'resource' and 'environment' combined with system variables
	 * from the PDP configuration</li>
	 * <li>the import mapping for functions and attribute finders</li>
	 * </ul>
	 * @return A {@link Flux} of {@link AuthorizationDecision} objects.
	 */
	@Override
	public Flux<AuthorizationDecision> evaluate(EvaluationContext ctx) {
		final Map<String, JsonNode> variables = new HashMap<>();
		final List<FluxProvider<Void>> fluxProviders = new ArrayList<>(getValueDefinitions().size());
		for (ValueDefinition valueDefinition : getValueDefinitions()) {
			fluxProviders.add(voiD -> evaluateValueDefinition(valueDefinition, ctx, variables));
		}
		final Flux<Void> variablesFlux = DependentStreamsUtil.nestedSwitchMap(Void.INSTANCE, fluxProviders);

		final PolicyCombinator combinator;
		switch (getAlgorithm()) {
		case "deny-unless-permit":
			combinator = new DenyUnlessPermitCombinator();
			break;
		case "permit-unless-deny":
			combinator = new PermitUnlessDenyCombinator();
			break;
		case "deny-overrides":
			combinator = new DenyOverridesCombinator();
			break;
		case "permit-overrides":
			combinator = new PermitOverridesCombinator();
			break;
		case "only-one-applicable":
			combinator = new OnlyOneApplicableCombinator();
			break;
		default: // "first-applicable":
			combinator = new FirstApplicableCombinator();
			break;
		}

		return variablesFlux.switchMap(voiD -> combinator.combinePolicies(getPolicies(), ctx))
				.onErrorReturn(INDETERMINATE);
	}

	private Flux<Void> evaluateValueDefinition(ValueDefinition valueDefinition, EvaluationContext evaluationCtx,
			Map<String, JsonNode> variables) {
		return valueDefinition.getEval().evaluate(evaluationCtx, true, Optional.empty()).flatMap(evaluatedValue -> {
			try {
				if (evaluatedValue.isPresent()) {
					evaluationCtx.getVariableCtx().put(valueDefinition.getName(), evaluatedValue.get());
					variables.put(valueDefinition.getName(), evaluatedValue.get());
					return Flux.just(Void.INSTANCE);
				}
				else {
					return Flux.error(new PolicyEvaluationException(CANNOT_ASSIGN_UNDEFINED_TO_A_VAL));
				}
			}
			catch (PolicyEvaluationException e) {
				LOGGER.error("Value definition evaluation failed: {}", e.getMessage());
				return Flux.error(e);
			}
		});
	}

}
