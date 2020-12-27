package io.sapl.test.coverage.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
/**
 * Containing all neccessary information of a {@link io.sapl.grammar.sapl.PolicySet} hit
 * @author Nikolai Seip
 *
 */
public class PolicySetHit {
	/**
	 * PolicySetId of hit {@link io.sapl.grammar.sapl.PolicySet}
	 */
	private String policySetId;
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(policySetId);
		return stringBuilder.toString();
	}
	
	@Override
	public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        
        if (!(o instanceof PolicySetHit)) {
            return false;
        }
        
        PolicySetHit hit = (PolicySetHit) o;
        
        return policySetId.equals(hit.getPolicySetId());
	}
	
	public static PolicySetHit fromString(String policySetToStringResult) {
		return new PolicySetHit(policySetToStringResult);
	}
}
