package io.sapl.mavenplugin.test.coverage.helper;

import java.util.Collection;

import io.sapl.test.coverage.api.model.*;

public class CoverageResult {
	
	public static float calculatePolicySetHitRatio( Collection<PolicySetHit> availableCoverageTargets, Collection<PolicySetHit> hitTargets) {
		float targets = availableCoverageTargets.size();
		float hits = hitTargets.size();
		return (hits / targets) * 100;
	}
	
	public static float calculatePolicyHitRatio( Collection<PolicyHit> availableCoverageTargets, Collection<PolicyHit> hitTargets) {
		float targets = availableCoverageTargets.size();
		float hits = hitTargets.size();
		return (hits / targets) * 100;
	}
	
	public static float calculatePolicyConditionHitRatio( Collection<PolicyConditionHit> availableCoverageTargets, Collection<PolicyConditionHit> hitTargets) {
		float targets = availableCoverageTargets.size();
		float hits = hitTargets.size();
		return (hits / targets) * 100;
	}

}
