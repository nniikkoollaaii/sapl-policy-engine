package io.sapl.test.coverage.api.model;

import io.sapl.test.coverage.api.CoverageHitConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
/**
 * Containing all neccessary information of a Policy hit
 * @author Nikolai Seip
 *
 */
public class PolicyHit {
	
	/**
	 * Id of {@link io.sapl.grammar.sapl.PolicySet} of hit policy. Empty if {@link io.sapl.grammar.sapl.Policy} isn't in a {@link io.sapl.grammar.sapl.PolicySet}.
	 */
	private String policySetId;
	
	/**
	 * Id of hit {@link io.sapl.grammar.sapl.Policy}
	 */
	private String policyId;
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(policySetId);
		stringBuilder.append(CoverageHitConfig.DELIMITER);
		stringBuilder.append(policyId);
		return stringBuilder.toString();
	}
	
	@Override
	public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        
        if (!(o instanceof PolicyHit)) {
            return false;
        }
        
        PolicyHit hit = (PolicyHit) o;
        
        return policySetId.equals(hit.getPolicySetId()) 
        		&& policyId.equals(hit.getPolicyId());
	}
	
	public static PolicyHit fromString(String policyToStringResult) {
		String[] splitted = policyToStringResult.split(CoverageHitConfig.DELIMITER_MATCH_REGEX);
		return new PolicyHit(splitted[0], splitted[1]);
	}
}