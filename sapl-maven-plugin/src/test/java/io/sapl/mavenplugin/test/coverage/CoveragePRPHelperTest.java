package io.sapl.mavenplugin.test.coverage;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import io.sapl.mavenplugin.test.coverage.helper.CoveragePRPHelper;
import io.sapl.mavenplugin.test.coverage.model.CoverageHitSummary;

public class CoveragePRPHelperTest {
	
	public static final String policyPath = "src\\test\\resources\\policies";
	
	@Test
    public void testPolicyRetrieval() throws Exception {
		CoverageHitSummary targets = CoveragePRPHelper.getCoverageTargets(policyPath);
		Assertions.assertThat(targets.getPolicySets().size()).isEqualTo(1);
		Assertions.assertThat(targets.getPolicySets().get(0).getPolicySetId()).isEqualTo("testPolicies");
		Assertions.assertThat(targets.getPolicys().size()).isEqualTo(2);
		Assertions.assertThat(targets.getPolicys().get(0).getPolicySetId()).isEqualTo("testPolicies");
		Assertions.assertThat(targets.getPolicys().get(0).getPolicyId()).isEqualTo("policy 1");
		Assertions.assertThat(targets.getPolicys().get(1).getPolicySetId()).isEqualTo("");
		Assertions.assertThat(targets.getPolicys().get(1).getPolicyId()).isEqualTo("policy 2");
		Assertions.assertThat(targets.getPolicyConditions().size()).isEqualTo(4);
		Assertions.assertThat(targets.getPolicyConditions().get(0).getConditionStatementId()).isEqualTo(0);
		Assertions.assertThat(targets.getPolicyConditions().get(0).getPolicySetId()).isEqualTo("testPolicies");
		Assertions.assertThat(targets.getPolicyConditions().get(0).getPolicyId()).isEqualTo("policy 1");
		Assertions.assertThat(targets.getPolicyConditions().get(0).isConditionResult()).isEqualTo(true);
		Assertions.assertThat(targets.getPolicyConditions().get(1).getConditionStatementId()).isEqualTo(0);
		Assertions.assertThat(targets.getPolicyConditions().get(1).getPolicySetId()).isEqualTo("testPolicies");
		Assertions.assertThat(targets.getPolicyConditions().get(1).getPolicyId()).isEqualTo("policy 1");
		Assertions.assertThat(targets.getPolicyConditions().get(1).isConditionResult()).isEqualTo(false);
		Assertions.assertThat(targets.getPolicyConditions().get(2).getConditionStatementId()).isEqualTo(1);
		Assertions.assertThat(targets.getPolicyConditions().get(2).getPolicySetId()).isEqualTo("testPolicies");
		Assertions.assertThat(targets.getPolicyConditions().get(2).getPolicyId()).isEqualTo("policy 1");
		Assertions.assertThat(targets.getPolicyConditions().get(2).isConditionResult()).isEqualTo(true);
		Assertions.assertThat(targets.getPolicyConditions().get(3).getConditionStatementId()).isEqualTo(1);
		Assertions.assertThat(targets.getPolicyConditions().get(3).getPolicySetId()).isEqualTo("testPolicies");
		Assertions.assertThat(targets.getPolicyConditions().get(3).getPolicyId()).isEqualTo("policy 1");
		Assertions.assertThat(targets.getPolicyConditions().get(3).isConditionResult()).isEqualTo(false);
	}
}
