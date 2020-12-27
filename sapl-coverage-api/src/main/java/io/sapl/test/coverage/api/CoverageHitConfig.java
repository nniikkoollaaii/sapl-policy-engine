package io.sapl.test.coverage.api;

import java.io.File;

public interface CoverageHitConfig {
	static final String ROOT_FILE_PATH = "target" + File.separator + "sapl-coverage";
	
	static final String FILE_PATH_POLICY_SET_HITS = ROOT_FILE_PATH + File.separator + "_policySetHits.txt";
	
	static final String FILE_PATH_POLICY_HITS = ROOT_FILE_PATH + File.separator + "_policyHits.txt";
	
	static final String FILE_PATH_POLICY_CONDITION_HITS = ROOT_FILE_PATH + File.separator + "_policyConditionHits.txt";

	static final String DELIMITER = "||";
	
	static final String DELIMITER_MATCH_REGEX = "\\|\\|";
}
