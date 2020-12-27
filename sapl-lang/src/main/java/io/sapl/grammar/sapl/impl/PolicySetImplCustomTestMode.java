package io.sapl.grammar.sapl.impl;

import io.sapl.api.interpreter.Val;
import io.sapl.interpreter.EvaluationContext;
import io.sapl.test.coverage.api.CoverageAPIFactory;
import io.sapl.test.coverage.api.CoverageHitRecorder;
import io.sapl.test.coverage.api.model.PolicySetHit;
import reactor.core.publisher.Mono;

public class PolicySetImplCustomTestMode extends PolicySetImplCustom {
	private final boolean shouldCollectCoverageHits;
	private final CoverageHitRecorder hitRecorder;
	
	PolicySetImplCustomTestMode(boolean shouldCollectCoverageHits) {
		this.shouldCollectCoverageHits = shouldCollectCoverageHits;
		this.hitRecorder = CoverageAPIFactory.constructCoverageHitRecorder();
	}
	
	@Override
	public Mono<Val> matches(EvaluationContext ctx) {
		return super.matches(ctx).doOnNext(matches -> {
			//record policySet hit if policySet matches
			if(matches.isBoolean() && matches.getBoolean() && this.shouldCollectCoverageHits) {
				this.hitRecorder.recordPolicySetHit(new PolicySetHit(getSaplName()));
			}
		});
	}
}
