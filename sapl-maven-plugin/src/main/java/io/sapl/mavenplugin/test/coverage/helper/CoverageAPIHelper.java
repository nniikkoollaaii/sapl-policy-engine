package io.sapl.mavenplugin.test.coverage.helper;

import io.sapl.mavenplugin.test.coverage.model.CoverageHitSummary;
import io.sapl.test.coverage.api.CoverageAPIFactory;
import io.sapl.test.coverage.api.CoverageHitReader;

public class CoverageAPIHelper {
	
	public static CoverageHitSummary readHits() throws Exception {
		CoverageHitReader reader = CoverageAPIFactory.constructCoverageHitReader();
		return new CoverageHitSummary(reader.readPolicySetHits(), reader.readPolicyHits(), reader.readPolicyConditionHits());
	}
}
