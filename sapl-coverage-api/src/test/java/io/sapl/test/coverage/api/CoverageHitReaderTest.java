package io.sapl.test.coverage.api;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.sapl.test.coverage.api.model.*;


public class CoverageHitReaderTest {
	
	private CoverageHitReader reader;
	
	@Before
	public void setup() {
		this.reader = new CoverageHitAPIImpl();
		this.reader.cleanCoverageHitFiles();
	}
	
	@After
	public void cleanup() {
		this.reader.cleanCoverageHitFiles();
	}

	@Test
    public void testCoverageReading_PolicySets() throws Exception {
		// arrange & act
		if(!Files.exists(Paths.get(CoverageHitConfig.FILE_PATH_POLICY_SET_HITS))) {
			if(Paths.get(CoverageHitConfig.FILE_PATH_POLICY_SET_HITS).getParent() != null) {
				Files.createDirectories(Paths.get(CoverageHitConfig.FILE_PATH_POLICY_SET_HITS).getParent());
			}
			Files.createFile(Paths.get(CoverageHitConfig.FILE_PATH_POLICY_SET_HITS));
		}
		Files.write(
			      Paths.get(CoverageHitConfig.FILE_PATH_POLICY_SET_HITS), 
			      (new PolicySetHit("set1").toString() + System.lineSeparator()).getBytes(), 
			      StandardOpenOption.APPEND);
		Files.write(
			      Paths.get(CoverageHitConfig.FILE_PATH_POLICY_SET_HITS), 
			      (new PolicySetHit("set2").toString() + System.lineSeparator()).getBytes(), 
			      StandardOpenOption.APPEND);
		
		
		
		//assert
		List<PolicySetHit> resultPolicySetHits = this.reader.readPolicySetHits();
	    Assertions.assertThat(resultPolicySetHits.size()).isEqualTo(2);
	    Assertions.assertThat(resultPolicySetHits.get(0).getPolicySetId()).isEqualTo("set1");
	    Assertions.assertThat(resultPolicySetHits.get(1).getPolicySetId()).isEqualTo("set2");
	}
	
	@Test
    public void testCoverageReading_Policys() throws Exception {
		// arrange & act
		if(!Files.exists(Paths.get(CoverageHitConfig.FILE_PATH_POLICY_HITS))) {
			if(Paths.get(CoverageHitConfig.FILE_PATH_POLICY_HITS).getParent() != null) {
				Files.createDirectories(Paths.get(CoverageHitConfig.FILE_PATH_POLICY_HITS).getParent());
			}
			Files.createFile(Paths.get(CoverageHitConfig.FILE_PATH_POLICY_HITS));
		}
		Files.write(
			      Paths.get(CoverageHitConfig.FILE_PATH_POLICY_HITS), 
			      (new PolicyHit("set1", "policy 1").toString() + System.lineSeparator()).getBytes(), 
			      StandardOpenOption.APPEND);
		Files.write(
			      Paths.get(CoverageHitConfig.FILE_PATH_POLICY_HITS), 
			      (new PolicyHit("set2", "policy 1").toString() + System.lineSeparator()).getBytes(), 
			      StandardOpenOption.APPEND);
		
		
		
		//assert
		List<PolicyHit> resultPolicyHits = this.reader.readPolicyHits();
	    Assertions.assertThat(resultPolicyHits.size()).isEqualTo(2);
	    Assertions.assertThat(resultPolicyHits.get(0).getPolicyId()).isEqualTo("policy 1");
	    Assertions.assertThat(resultPolicyHits.get(0).getPolicySetId()).isEqualTo("set1");
	    Assertions.assertThat(resultPolicyHits.get(1).getPolicyId()).isEqualTo("policy 1");
	    Assertions.assertThat(resultPolicyHits.get(1).getPolicySetId()).isEqualTo("set2");
	}
	
	
	@Test
    public void testCoverageReading_PolicyConditions() throws Exception {
		// arrange & act
		if(!Files.exists(Paths.get(CoverageHitConfig.FILE_PATH_POLICY_CONDITION_HITS))) {
			if(Paths.get(CoverageHitConfig.FILE_PATH_POLICY_CONDITION_HITS).getParent() != null) {
				Files.createDirectories(Paths.get(CoverageHitConfig.FILE_PATH_POLICY_CONDITION_HITS).getParent());
			}
			Files.createFile(Paths.get(CoverageHitConfig.FILE_PATH_POLICY_CONDITION_HITS));
		}
		Files.write(
			      Paths.get(CoverageHitConfig.FILE_PATH_POLICY_CONDITION_HITS), 
			      (new PolicyConditionHit("set1", "policy 1", 0, true).toString() + System.lineSeparator()).getBytes(), 
			      StandardOpenOption.APPEND);
		Files.write(
			      Paths.get(CoverageHitConfig.FILE_PATH_POLICY_CONDITION_HITS), 
			      (new PolicyConditionHit("set2", "policy 1", 0, true).toString() + System.lineSeparator()).getBytes(), 
			      StandardOpenOption.APPEND);
		
		
		
		//assert
		List<PolicyConditionHit> resultPolicyConditionHits = this.reader.readPolicyConditionHits();
	    Assertions.assertThat(resultPolicyConditionHits.size()).isEqualTo(2);
	    Assertions.assertThat(resultPolicyConditionHits.get(0).getConditionStatementId()).isEqualTo(0);
	    Assertions.assertThat(resultPolicyConditionHits.get(0).isConditionResult()).isEqualTo(true);
	    Assertions.assertThat(resultPolicyConditionHits.get(0).getPolicyId()).isEqualTo("policy 1");
	    Assertions.assertThat(resultPolicyConditionHits.get(0).getPolicySetId()).isEqualTo("set1");;
	    Assertions.assertThat(resultPolicyConditionHits.get(1).getConditionStatementId()).isEqualTo(0);
	    Assertions.assertThat(resultPolicyConditionHits.get(1).isConditionResult()).isEqualTo(true);
	    Assertions.assertThat(resultPolicyConditionHits.get(1).getPolicyId()).isEqualTo("policy 1");
	    Assertions.assertThat(resultPolicyConditionHits.get(1).getPolicySetId()).isEqualTo("set2");
	}
}
