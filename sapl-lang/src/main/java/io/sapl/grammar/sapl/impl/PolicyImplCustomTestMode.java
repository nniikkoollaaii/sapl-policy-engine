package io.sapl.grammar.sapl.impl;

import io.sapl.api.interpreter.Val;
import io.sapl.grammar.sapl.PolicySet;
import io.sapl.grammar.sapl.SaplPackage;
import io.sapl.interpreter.EvaluationContext;
import io.sapl.test.coverage.api.*;
import io.sapl.test.coverage.api.model.PolicyHit;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class PolicyImplCustomTestMode extends PolicyImplCustom {
	
	private final String policyIdUnderUnitTest;
	private final boolean shouldCollectCoverageHits;
	private final CoverageHitRecorder hitRecorder;
	
	PolicyImplCustomTestMode(String policyIdUnderUnitTest, boolean shouldCollectCoverageHits) {
		this.policyIdUnderUnitTest = policyIdUnderUnitTest;
		this.shouldCollectCoverageHits = shouldCollectCoverageHits;
		this.hitRecorder = CoverageAPIFactory.constructCoverageHitRecorder();
	}
	
	@Override
	public Mono<Val> matches(EvaluationContext ctx) {
		//check for policyId when policyIdUnderUnitTest is not null
		//then check the current Policy Id to only match the unit tested policy
		if (this.policyIdUnderUnitTest != null && !getSaplName().equals(this.policyIdUnderUnitTest)) {
			log.trace("Overriding match result with false due to wrong unit test policy id");
			return Mono.just(Val.FALSE);
		} else {
			//if this is the policy under unit test return "normal" result
			return super.matches(ctx).doOnNext(matches -> {
				//and record policy hit if policy matches
				if(matches.isBoolean() && matches.getBoolean() && this.shouldCollectCoverageHits) {
					String policySetId = "";
					if(eContainer() instanceof PolicySet) {
						policySetId = ((PolicySet) eContainer()).getSaplName();
					}
					this.hitRecorder.recordPolicyHit(new PolicyHit(policySetId, getSaplName()));
				}
			});
		}
	}
}
