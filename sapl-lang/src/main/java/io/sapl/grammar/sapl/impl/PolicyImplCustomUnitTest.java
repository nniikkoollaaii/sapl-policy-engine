package io.sapl.grammar.sapl.impl;

import io.sapl.api.interpreter.Val;
import io.sapl.grammar.sapl.Expression;
import io.sapl.interpreter.EvaluationContext;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class PolicyImplCustomUnitTest extends PolicyImplCustom {
	
	private final String policyIdUnderUnitTest;
	
	PolicyImplCustomUnitTest(String policyIdUnderUnitTest) {
		this.policyIdUnderUnitTest = policyIdUnderUnitTest;
	}
	
	@Override
	public Mono<Val> matches(EvaluationContext ctx) {
		log.trace("| | |-- PolicyElement test match '{}'", getSaplName());
		
		//additionally check for Policy Id to only match the unit tested policy
		if (!getSaplName().equals(this.policyIdUnderUnitTest)) {
			return Mono.just(Val.FALSE);
		}
		
		
		final Expression targetExpression = getTargetExpression();
		if (targetExpression == null) {
			log.trace("| | | |-- MATCH (no target expression, matches all)");
			log.trace("| | |");
			return Mono.just(Val.TRUE);
		}
		return targetExpression.evaluate(ctx, Val.UNDEFINED).next().defaultIfEmpty(Val.FALSE).flatMap(result -> {
			if (result.isError() || !result.isBoolean()) {
				log.trace("| | | |-- ERROR in target expression did not evaluate to boolean. Was: {}", result);
				log.trace("| | |");
				return Val.errorMono(CONDITION_NOT_BOOLEAN, result);
			}
			log.trace("| | | |-- {}", result.get().asBoolean() ? "MATCH" : "NO MATCH");
			log.trace("| | |");
			return Mono.just(result);
		});
	}

}
