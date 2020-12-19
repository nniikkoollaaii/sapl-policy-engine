package io.sapl.grammar.sapl.impl;

import io.sapl.grammar.sapl.Policy;
import io.sapl.grammar.sapl.PolicyElement;
import io.sapl.grammar.sapl.PolicySet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SaplFactoryImplCustomUnitTest extends SaplFactoryImpl {

	private final String policyIdUnderUnitTest;
	
	public SaplFactoryImplCustomUnitTest(String policyIdUnderUnitTest) {
		this.policyIdUnderUnitTest = policyIdUnderUnitTest;
	}
	
	@Override
	public Policy createPolicy()
	{
		log.trace("Creating PolicyElement Subclass used for Unit-Testing Policy");
		PolicyImplCustomUnitTest policy = new PolicyImplCustomUnitTest(this.policyIdUnderUnitTest);
		return policy;
	}
		
}
