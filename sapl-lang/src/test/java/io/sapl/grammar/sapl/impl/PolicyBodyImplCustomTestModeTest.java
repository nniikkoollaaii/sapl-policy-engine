package io.sapl.grammar.sapl.impl;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.sapl.api.interpreter.InitializationException;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.functions.FilterFunctionLibrary;
import io.sapl.interpreter.EvaluationContext;
import io.sapl.interpreter.SimpleFunctionLibrary;
import io.sapl.interpreter.UnitTestSAPLInterpreter;
import io.sapl.interpreter.functions.AnnotationFunctionContext;
import io.sapl.interpreter.pip.AnnotationAttributeContext;
import io.sapl.test.coverage.api.CoverageAPIFactory;
import io.sapl.test.coverage.api.CoverageHitRecorder;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

public class PolicyBodyImplCustomTestModeTest {
	private static final UnitTestSAPLInterpreter INTERPRETER = new UnitTestSAPLInterpreter(null, true);

	private EvaluationContext ctx;
	
	private final CoverageHitRecorder recorder = CoverageAPIFactory.constructCoverageHitRecorder();

	@Before
	public void setUp() throws InitializationException {
		Hooks.onOperatorDebug();
		var attributeCtx = new AnnotationAttributeContext();
		var functionCtx = new AnnotationFunctionContext();
		functionCtx.loadLibrary(new SimpleFunctionLibrary());
		functionCtx.loadLibrary(new FilterFunctionLibrary());
		ctx = new EvaluationContext(attributeCtx, functionCtx, new HashMap<>());
	}
	
	@After
	public void cleanUp() {
		this.recorder.cleanCoverageHitFiles();
	}

	@Test
	public void trueReturnsEntitlement() {
		var policy = INTERPRETER.parse("policy \"p\" permit true where true; true; true;");
		var expected = AuthorizationDecision.PERMIT;
		StepVerifier.create(policy.evaluate(ctx)).expectNext(expected).verifyComplete();
		//TODO Nikolai: verify correct call to coverageRecorder via mockito
	}
	
	@Test
	public void trueReturnsEntitlementInSet() {
		var policy = INTERPRETER.parse("set \"set\" deny-overrides policy \"p\" permit true where true; true; true;");
		var expected = AuthorizationDecision.PERMIT;
		StepVerifier.create(policy.evaluate(ctx)).expectNext(expected).verifyComplete();
		//TODO Nikolai: Check correct CoverageRecorder call via mockito
	}

	//TODO Nikolai: more testcases
}
