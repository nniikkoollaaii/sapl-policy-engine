package io.sapl.mavenplugin.test.coverage;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.LifecyclePhase;

import io.sapl.mavenplugin.test.coverage.helper.CoverageAPIHelper;
import io.sapl.mavenplugin.test.coverage.helper.CoveragePRPHelper;
import io.sapl.mavenplugin.test.coverage.helper.CoverageResult;
import io.sapl.mavenplugin.test.coverage.model.CoverageHitSummary;

@Mojo(name = "report-coverage-information", defaultPhase = LifecyclePhase.VERIFY)
public class ReportCoverageInformationMojo extends AbstractMojo {
	
    @Parameter(property = "policyPath", required = true)
    private String policyPath;
    
    @Parameter(defaultValue = "0")
    private float policySetHitRatio;    
    
    @Parameter(defaultValue = "0")
    private float policyHitRatio;
    
    @Parameter(defaultValue = "0")
    private float policyConditionHitRatio;
	
    public void execute() throws MojoExecutionException, MojoFailureException
    {
    	//read available targets & hits
    	CoverageHitSummary availableCoverageTargets = null;
    	try {
    		getLog().debug("Loading coverage targets");
			availableCoverageTargets = CoveragePRPHelper.getCoverageTargets(policyPath);
			getLog().info("Successful read coverage targets");
		} catch (Exception e) {
			getLog().error("Error reading coverage targets: " + e.getMessage());
			e.printStackTrace();
			throw new MojoExecutionException("Error reading coverage targets"); 
		}
		CoverageHitSummary hitTargets;
		try {
			getLog().debug("Loading coverage hits");
			hitTargets = CoverageAPIHelper.readHits();
			getLog().info("Successful read coverage hits");
		} catch (Exception e) {
			getLog().error("Error reading coverage hits: " + e.getMessage());
			e.printStackTrace();

			throw new MojoExecutionException("Error reading coverage hits"); 
		}
		
		//calculate ratios
		float actualPolicySetHitRatio = CoverageResult.calculatePolicySetHitRatio(availableCoverageTargets.getPolicySets(), hitTargets.getPolicySets());
		getLog().info("Your PolicySet Hit Ratio is: " + actualPolicySetHitRatio);
		float actualPolicyHitRatio = CoverageResult.calculatePolicyHitRatio(availableCoverageTargets.getPolicys(), hitTargets.getPolicys());
		getLog().info("Your Policy Hit Ratio is: " + actualPolicyHitRatio);
		float actualPolicyConditionHitRatio = CoverageResult.calculatePolicyConditionHitRatio(availableCoverageTargets.getPolicyConditions(), hitTargets.getPolicyConditions());
		getLog().info("Your PolicyCondition Hit Ratio is: " + actualPolicyConditionHitRatio);
		
		//log if ratio is not fulfilled
		boolean isRatioNotFulfilled = false;
		if(actualPolicySetHitRatio < policySetHitRatio) {
			isRatioNotFulfilled = true;
			getLog().error("PolicySet Hit Ratio not fulfilled - Expected greater or equal " + policySetHitRatio + " but got " + actualPolicySetHitRatio);
		}
		if(actualPolicyHitRatio < policyHitRatio) {
			isRatioNotFulfilled = true;
			getLog().error("Policy Hit Ratio not fulfilled - Expected greater or equal " + policyHitRatio + " but got " + actualPolicyHitRatio);
		}
		if(actualPolicyConditionHitRatio < policyConditionHitRatio) {
			isRatioNotFulfilled = true;
			getLog().error("PolicyCondition Hit Ratio not fulfilled - Expected greater or equal " + policyConditionHitRatio + " but got " + actualPolicyConditionHitRatio);
		}
		
		//break lifecycle if ratio is not fulfilled
		if(isRatioNotFulfilled) {
			throw new MojoFailureException("One or more SAPL Coverage Ratios aren't fulfilled! Find further information above.");
		}
    }
} 