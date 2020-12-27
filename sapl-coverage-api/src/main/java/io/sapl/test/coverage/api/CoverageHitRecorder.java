package io.sapl.test.coverage.api;

import io.sapl.test.coverage.api.model.*;

public interface CoverageHitRecorder {
	
	/**
	 * Internal method used by SAPL Coverage Recording to record a hit of a {@link io.sapl.grammar.sapl.PolicySet}
	 * @param policySetId PolicySetId of the PolicySet
	 */
	void recordPolicySetHit(PolicySetHit hit);
	
	/**
	 * Internal method used by SAPL Coverage Recording to record a hit of a {@link io.sapl.grammar.sapl.Policy}
	 * @param policySetId PolicySetId of the PolicySet
	 * @param policyId PolicyId of the Policy
	 */
	void recordPolicyHit(PolicyHit hit);
	
	/**
	 * Internal method used by SAPL Coverage Recording to record a hit of a {@link io.sapl.grammar.sapl.Condition}
	 * @param policySetId PolicySetId of the PolicySet
	 * @param policyId PolicyId of surrounding Policy
	 * @param lineNumber LineNumber of this Condition
	 */
	void recordPolicyConditionHit(PolicyConditionHit hit);
	
	/**
	 * Deletes all files used for coverage recording
	 */
	void cleanCoverageHitFiles();
	
}
