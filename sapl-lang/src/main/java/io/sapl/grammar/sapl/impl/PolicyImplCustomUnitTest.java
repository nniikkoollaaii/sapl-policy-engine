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
		//additionally check for Policy Id to only match the unit tested policy
		if (!getSaplName().equals(this.policyIdUnderUnitTest)) {
			log.trace("Overriding match result with false due to wrong unit test policy id");
			return Mono.just(Val.FALSE);
		} else {
			return super.matches(ctx);
		}
	}
}
