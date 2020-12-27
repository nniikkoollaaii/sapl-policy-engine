package io.sapl.grammar.sapl.impl;

import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;
import org.reactivestreams.Publisher;

import io.sapl.api.interpreter.Val;
import io.sapl.grammar.sapl.Condition;
import io.sapl.grammar.sapl.Policy;
import io.sapl.grammar.sapl.PolicySet;
import io.sapl.grammar.sapl.SaplPackage;
import io.sapl.interpreter.EvaluationContext;
import io.sapl.test.coverage.api.CoverageAPIFactory;
import io.sapl.test.coverage.api.CoverageHitRecorder;
import io.sapl.test.coverage.api.model.PolicyConditionHit;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

public class PolicyBodyImplCustomTestMode extends PolicyBodyImplCustom {

	private final boolean shouldCollectCoverageHits;
	private final CoverageHitRecorder hitRecorder;
	
	private int currentStatementId = 0;
	
	PolicyBodyImplCustomTestMode(boolean shouldCollectCoverageHits) {
		this.shouldCollectCoverageHits = shouldCollectCoverageHits;
		this.hitRecorder = CoverageAPIFactory.constructCoverageHitRecorder();
	}
	
	@Override
	protected Function<? super Tuple2<Val, EvaluationContext>, Publisher<? extends Tuple2<Val, EvaluationContext>>> evaluateStatements(
			int statementId) {
		this.currentStatementId = statementId;
		return super.evaluateStatements(statementId);
	}
	
	@Override
	protected Flux<Tuple2<Val, EvaluationContext>> evaluateCondition(Val previousResult, Condition condition,
			EvaluationContext ctx) {
		return super.evaluateCondition(previousResult, condition, ctx).doOnNext(result -> {
			//record policy condition hit
			if(result.getT1().isBoolean() && this.shouldCollectCoverageHits) {
				String policySetId = "";
				String policyId = "";		
				EObject eContainer1 = eContainer();
				if(eContainer1 instanceof Policy) {
					policyId = ((Policy) eContainer1).getSaplName();
					EObject eContainer2 = eContainer1.eContainer();
					if(eContainer2 instanceof PolicySet) {
						policySetId = ((PolicySet) eContainer2).getSaplName();
					}
				}
				this.hitRecorder.recordPolicyConditionHit(
						new PolicyConditionHit(policySetId, policyId, this.currentStatementId, result.getT1().getBoolean()));
			}
		});
	}
}
