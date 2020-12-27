package io.sapl.grammar.sapl.impl;

import io.sapl.grammar.sapl.Policy;
import io.sapl.grammar.sapl.PolicyBody;
import io.sapl.grammar.sapl.PolicySet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SaplFactoryImplCustomUnitTest extends SaplFactoryImpl {

	private final String policyIdUnderUnitTest;
	private final boolean shouldCollectCoverageHits;
	
	public SaplFactoryImplCustomUnitTest(String policyIdUnderUnitTest, boolean shouldCollectCoverageHits) {
		this.policyIdUnderUnitTest = policyIdUnderUnitTest;
		this.shouldCollectCoverageHits = shouldCollectCoverageHits;
	}
	
	@Override
	public PolicySet createPolicySet()
	{
		log.trace("Creating PolicySet Subclass for test mode");
		PolicySetImplCustomTestMode policySet = new PolicySetImplCustomTestMode(this.shouldCollectCoverageHits);
		return policySet;
	}
	
	@Override
	public Policy createPolicy()
	{
		log.trace("Creating Policy Subclass for test mode");
		PolicyImplCustomTestMode policy = new PolicyImplCustomTestMode(this.policyIdUnderUnitTest, this.shouldCollectCoverageHits);
		return policy;
	}
	
	@Override
	public PolicyBody createPolicyBody()
	{
		log.trace("Creating PolicyBody Subclass for test mode");
		PolicyBodyImplCustomTestMode body = new PolicyBodyImplCustomTestMode(this.shouldCollectCoverageHits);
		return body;
	}
		
}
