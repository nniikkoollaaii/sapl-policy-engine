package io.sapl.test.coverage.api.model;

import io.sapl.test.coverage.api.CoverageHitConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
/**
 * Containing all necessary information of a Policy Condition Hit
 * @author Nikolai Seip
 *
 */
public class PolicyConditionHit {
	
	/**
	 * Id of {@link io.sapl.grammar.sapl.PolicySet} of hit {@link io.sapl.grammar.sapl.Policy}. Empty if {@link io.sapl.grammar.sapl.Policy} isn't in a {@link io.sapl.grammar.sapl.PolicySet}.
	 */
	String policySetId;
	
	/**
	 * Id of hit {@link io.sapl.grammar.sapl.Policy}
	 */
	String policyId;
	

	/**
	 * StatementId of {@link io.sapl.grammar.sapl.Condition} hit in {@link io.sapl.grammar.sapl.Policy}
	 */
	int conditionStatementId;
	
	/**
	 * Result of evaluation {@link io.sapl.grammar.sapl.Condition}
	 */
	boolean conditionResult;
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(policySetId);
		stringBuilder.append(CoverageHitConfig.DELIMITER);
		stringBuilder.append(policyId);
		stringBuilder.append(CoverageHitConfig.DELIMITER);
		stringBuilder.append(conditionStatementId);
		stringBuilder.append(CoverageHitConfig.DELIMITER);
		stringBuilder.append(conditionResult);
		return stringBuilder.toString();
	}
	
	@Override
	public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        
        if (!(o instanceof PolicyConditionHit)) {
            return false;
        }
        
        PolicyConditionHit hit = (PolicyConditionHit) o;
        
        return policySetId.equals(hit.getPolicySetId()) 
        		&& policyId.equals(hit.getPolicyId())
        		&& conditionStatementId == hit.getConditionStatementId()
        		&& conditionResult == hit.isConditionResult();
	}
	
	public static PolicyConditionHit fromString(String policyConditionToStringResult) {
		String[] splitted = policyConditionToStringResult.split(CoverageHitConfig.DELIMITER_MATCH_REGEX);
		return new PolicyConditionHit(splitted[0], splitted[1], Integer.valueOf(splitted[2]), Boolean.valueOf(splitted[3]));
	}
	
}
