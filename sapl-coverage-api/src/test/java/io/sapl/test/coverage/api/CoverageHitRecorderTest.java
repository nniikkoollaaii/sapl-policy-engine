package io.sapl.test.coverage.api;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.sapl.test.coverage.api.model.*;


public class CoverageHitRecorderTest {
	
	private CoverageHitRecorder recorder;
	
	@Before
	public void setup() {
		this.recorder = new CoverageHitAPIImpl();
		this.recorder.cleanCoverageHitFiles();
	}
	
	@After
	public void cleanup() {
		this.recorder.cleanCoverageHitFiles();
	}

	@Test
    public void testCoverageRecording() throws Exception {
		// arrange & act
		this.recorder.recordPolicySetHit(new PolicySetHit("set1"));
		this.recorder.recordPolicyHit(new PolicyHit("set1", "policy11"));
		this.recorder.recordPolicyConditionHit(new PolicyConditionHit("set1", "policy11", 7, true));
		this.recorder.recordPolicyConditionHit(new PolicyConditionHit("set1", "policy11", 8, true));
		this.recorder.recordPolicyConditionHit(new PolicyConditionHit("set1", "policy11", 9, true));
		this.recorder.recordPolicyHit(new PolicyHit("set1", "policy12"));
		this.recorder.recordPolicyConditionHit(new PolicyConditionHit("set1", "policy12", 7, true));
		this.recorder.recordPolicyConditionHit(new PolicyConditionHit("set1", "policy12", 8, true));
		this.recorder.recordPolicyConditionHit(new PolicyConditionHit("set1", "policy12", 9, true));
		this.recorder.recordPolicySetHit(new PolicySetHit("set2"));
		this.recorder.recordPolicyHit(new PolicyHit("set2", "policy21"));
		this.recorder.recordPolicyConditionHit(new PolicyConditionHit("set2", "policy21", 7, true));
		this.recorder.recordPolicyConditionHit(new PolicyConditionHit("set2", "policy21", 8, true));
		this.recorder.recordPolicyConditionHit(new PolicyConditionHit("set2", "policy21", 9, true));
		this.recorder.recordPolicyHit(new PolicyHit("set2", "policy22"));
		this.recorder.recordPolicyConditionHit(new PolicyConditionHit("set2", "policy22", 7, true));
		this.recorder.recordPolicyConditionHit(new PolicyConditionHit("set2", "policy22", 8, true));
		this.recorder.recordPolicyConditionHit(new PolicyConditionHit("set2", "policy22", 9, true));
		this.recorder.recordPolicySetHit(new PolicySetHit("set2"));
		this.recorder.recordPolicyHit(new PolicyHit("set2", "policy21"));
		this.recorder.recordPolicyConditionHit(new PolicyConditionHit("set2", "policy21", 7, true));
		this.recorder.recordPolicyConditionHit(new PolicyConditionHit("set2", "policy21", 8, true));
		this.recorder.recordPolicyConditionHit(new PolicyConditionHit("set2", "policy21", 9, true));
		this.recorder.recordPolicyHit(new PolicyHit("set2", "policy22"));
		this.recorder.recordPolicyConditionHit(new PolicyConditionHit("set2", "policy22", 7, true));
		this.recorder.recordPolicyConditionHit(new PolicyConditionHit("set2", "policy22", 8, true));
		this.recorder.recordPolicyConditionHit(new PolicyConditionHit("set2", "policy22", 9, true));
		
		
		//assert
	    List<String> resultPolicySetHits = Files.readAllLines(Paths.get(CoverageHitAPIImpl.FILE_PATH_POLICY_SET_HITS));
	    Assertions.assertThat(resultPolicySetHits.size()).isEqualTo(2);
	    Assertions.assertThat(resultPolicySetHits.get(0)).isEqualTo("set1");
	    Assertions.assertThat(resultPolicySetHits.get(1)).isEqualTo("set2");
	    
	    List<String> resultPolicyHits = Files.readAllLines(Paths.get(CoverageHitAPIImpl.FILE_PATH_POLICY_HITS));
	    Assertions.assertThat(resultPolicyHits.size()).isEqualTo(4);
	    Assertions.assertThat(resultPolicyHits.get(0)).isEqualTo("set1" + CoverageHitAPIImpl.DELIMITER + "policy11");
	    Assertions.assertThat(resultPolicyHits.get(1)).isEqualTo("set1" + CoverageHitAPIImpl.DELIMITER + "policy12");
	    Assertions.assertThat(resultPolicyHits.get(2)).isEqualTo("set2" + CoverageHitAPIImpl.DELIMITER + "policy21");
	    Assertions.assertThat(resultPolicyHits.get(3)).isEqualTo("set2" + CoverageHitAPIImpl.DELIMITER + "policy22");
	    
	    List<String> resultPolicyConditionHits = Files.readAllLines(Paths.get(CoverageHitAPIImpl.FILE_PATH_POLICY_CONDITION_HITS));
	    Assertions.assertThat(resultPolicyConditionHits.size()).isEqualTo(12);
	    Assertions.assertThat(resultPolicyConditionHits.get(0)).isEqualTo("set1" + CoverageHitAPIImpl.DELIMITER + "policy11" + CoverageHitAPIImpl.DELIMITER + "7" + CoverageHitAPIImpl.DELIMITER + true);
	    Assertions.assertThat(resultPolicyConditionHits.get(1)).isEqualTo("set1" + CoverageHitAPIImpl.DELIMITER + "policy11" + CoverageHitAPIImpl.DELIMITER + "8" + CoverageHitAPIImpl.DELIMITER + true);
	    Assertions.assertThat(resultPolicyConditionHits.get(2)).isEqualTo("set1" + CoverageHitAPIImpl.DELIMITER + "policy11" + CoverageHitAPIImpl.DELIMITER + "9" + CoverageHitAPIImpl.DELIMITER + true);
	    Assertions.assertThat(resultPolicyConditionHits.get(3)).isEqualTo("set1" + CoverageHitAPIImpl.DELIMITER + "policy12" + CoverageHitAPIImpl.DELIMITER + "7" + CoverageHitAPIImpl.DELIMITER + true);
	    Assertions.assertThat(resultPolicyConditionHits.get(4)).isEqualTo("set1" + CoverageHitAPIImpl.DELIMITER + "policy12" + CoverageHitAPIImpl.DELIMITER + "8" + CoverageHitAPIImpl.DELIMITER + true);
	    Assertions.assertThat(resultPolicyConditionHits.get(5)).isEqualTo("set1" + CoverageHitAPIImpl.DELIMITER + "policy12" + CoverageHitAPIImpl.DELIMITER + "9" + CoverageHitAPIImpl.DELIMITER + true);
	    Assertions.assertThat(resultPolicyConditionHits.get(6)).isEqualTo("set2" + CoverageHitAPIImpl.DELIMITER + "policy21" + CoverageHitAPIImpl.DELIMITER + "7" + CoverageHitAPIImpl.DELIMITER + true);
	    Assertions.assertThat(resultPolicyConditionHits.get(7)).isEqualTo("set2" + CoverageHitAPIImpl.DELIMITER + "policy21" + CoverageHitAPIImpl.DELIMITER + "8" + CoverageHitAPIImpl.DELIMITER + true);
	    Assertions.assertThat(resultPolicyConditionHits.get(8)).isEqualTo("set2" + CoverageHitAPIImpl.DELIMITER + "policy21" + CoverageHitAPIImpl.DELIMITER + "9" + CoverageHitAPIImpl.DELIMITER + true);
	    Assertions.assertThat(resultPolicyConditionHits.get(9)).isEqualTo("set2" + CoverageHitAPIImpl.DELIMITER + "policy22" + CoverageHitAPIImpl.DELIMITER + "7" + CoverageHitAPIImpl.DELIMITER + true);
	    Assertions.assertThat(resultPolicyConditionHits.get(10)).isEqualTo("set2" + CoverageHitAPIImpl.DELIMITER + "policy22" + CoverageHitAPIImpl.DELIMITER + "8" + CoverageHitAPIImpl.DELIMITER + true);
	    Assertions.assertThat(resultPolicyConditionHits.get(11)).isEqualTo("set2" + CoverageHitAPIImpl.DELIMITER + "policy22" + CoverageHitAPIImpl.DELIMITER + "9" + CoverageHitAPIImpl.DELIMITER + true);
	}
}
