package io.sapl.mavenplugin.test.coverage.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

import io.sapl.test.coverage.api.model.*;

@Getter
@AllArgsConstructor
public class CoverageHitSummary {

	List<PolicySetHit> policySets;

	List<PolicyHit> policys;

	List<PolicyConditionHit> policyConditions;
}
