package io.sapl.interpreter.combinators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.interpreter.PolicyEvaluationException;
import io.sapl.api.interpreter.SAPLInterpreter;
import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.Request;
import io.sapl.api.pdp.Response;
import io.sapl.grammar.sapl.Policy;
import io.sapl.grammar.sapl.SAPL;
import io.sapl.interpreter.combinators.ObligationAdviceCollector.Type;
import io.sapl.interpreter.functions.FunctionContext;
import io.sapl.interpreter.pip.AttributeContext;

public class PermitUnlessDenyCombinator implements DocumentsCombinator, PolicyCombinator {

	private SAPLInterpreter interpreter;

	public PermitUnlessDenyCombinator(SAPLInterpreter interpreter) {
		this.interpreter = interpreter;
	}

	@Override
	public Response combineMatchingDocuments(Collection<SAPL> matchingSaplDocuments, boolean errorsInTarget,
			Request request, AttributeContext attributeCtx, FunctionContext functionCtx,
			Map<String, JsonNode> systemVariables) {
		ResponseAccumulator responseAccumulator = new ResponseAccumulator(errorsInTarget);
		for (SAPL document : matchingSaplDocuments) {
			responseAccumulator
					.addResponse(interpreter.evaluate(request, document, attributeCtx, functionCtx, systemVariables));
		}
		return responseAccumulator.getResponse();
	}

	@Override
	public Response combinePolicies(List<Policy> policies, Request request, AttributeContext attributeCtx,
			FunctionContext functionCtx, Map<String, JsonNode> systemVariables, Map<String, JsonNode> variables,
			Map<String, String> imports) {
		boolean errorsInTarget = false;
		List<Policy> matchingPolicies = new ArrayList<>();
		for (Policy policy : policies) {
			try {
				if (interpreter.matches(request, policy, functionCtx, systemVariables, variables, imports)) {
					matchingPolicies.add(policy);
				}
			} catch (PolicyEvaluationException e) {
				errorsInTarget = true;
			}
		}

		ResponseAccumulator responseAccumulator = new ResponseAccumulator(errorsInTarget);
		for (Policy policy : matchingPolicies) {
			responseAccumulator.addResponse(interpreter.evaluateRules(request, policy, attributeCtx, functionCtx,
					systemVariables, variables, imports));
		}
		return responseAccumulator.getResponse();
	}

	private static class ResponseAccumulator {

		private Response response;
		private int permitCount;
		private boolean transformation;
		private ObligationAdviceCollector obligationAdvice;

		ResponseAccumulator(boolean errorsInTarget) {
			permitCount = 0;
			transformation = false;
			obligationAdvice = new ObligationAdviceCollector();

			response = new Response(Decision.PERMIT, null, null, null);
		}

		void addResponse(Response newResponse) {
			if (newResponse.getDecision() == Decision.DENY) {
				obligationAdvice.add(Decision.DENY, newResponse);
				response = Response.deny();
			} else if (newResponse.getDecision() == Decision.PERMIT && response.getDecision() != Decision.DENY) {
				permitCount += 1;
				if (newResponse.getResource().isPresent()) {
					transformation = true;
				}

				obligationAdvice.add(Decision.PERMIT, newResponse);
				response = newResponse;
			}
		}

		Response getResponse() {
			if (response.getDecision() == Decision.PERMIT) {
				if (permitCount > 1 && transformation) {
					return Response.deny();
				} else {
					return new Response(Decision.PERMIT, response.getResource(),
							obligationAdvice.get(Type.OBLIGATION, Decision.PERMIT),
							obligationAdvice.get(Type.ADVICE, Decision.PERMIT));
				}
			} else {
				return new Response(Decision.DENY, response.getResource(),
						obligationAdvice.get(Type.OBLIGATION, Decision.DENY),
						obligationAdvice.get(Type.ADVICE, Decision.DENY));
			}
		}
	}
}