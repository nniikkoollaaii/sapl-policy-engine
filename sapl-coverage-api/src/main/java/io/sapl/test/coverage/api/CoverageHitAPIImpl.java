package io.sapl.test.coverage.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

import io.sapl.test.coverage.api.model.*;

@Slf4j
class CoverageHitAPIImpl implements CoverageHitRecorder, CoverageHitReader, CoverageHitConfig {
	
	@Override
	public void recordPolicySetHit(PolicySetHit hit) {
		addPossibleHit(FILE_PATH_POLICY_SET_HITS, hit.toString());
	}

	@Override
	public void recordPolicyHit(PolicyHit hit) {
		addPossibleHit(FILE_PATH_POLICY_HITS, hit.toString());
	}

	@Override
	public void recordPolicyConditionHit(PolicyConditionHit hit) {
		addPossibleHit(FILE_PATH_POLICY_CONDITION_HITS, hit.toString());
		
	}
	
	private void addPossibleHit(String filePath, String lineToAdd){
		try {

			if (doesLineExistsInFile(filePath, lineToAdd)) {
				// do nothing as already hit
			} else {
				appendLineToFile(filePath, lineToAdd);
			}
		} catch (IOException e) {
			log.error("Error using File " + filePath, e);
		}
	}
	
	private boolean doesLineExistsInFile(String filePath, String lineToAdd) throws IOException {
		if(!Files.exists(Paths.get(filePath))) {
			if(Paths.get(filePath).getParent() != null) {
				Files.createDirectories(Paths.get(filePath).getParent());
			}
			Files.createFile(Paths.get(filePath));
		}
		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
		     Optional<String> lineHavingTarget = stream.filter(l -> l.contains(lineToAdd)).findFirst();
		     if(lineHavingTarget.isPresent()) {
		    	 return true;
		     } else {
		    	 return false;
		     }
		    } catch (IOException e) {
		         throw new IOException(e);
		    }
	}
	
	private void appendLineToFile(String filePath, String lineToAdd) throws IOException {
		Files.write(
			      Paths.get(filePath), 
			      (lineToAdd + System.lineSeparator()).getBytes(), 
			      StandardOpenOption.APPEND);
	}

	@Override
	public void cleanCoverageHitFiles() {
		try {
			Files.deleteIfExists(Paths.get(FILE_PATH_POLICY_SET_HITS));
		} catch (IOException e) {
			log.error("Error deleting File " + FILE_PATH_POLICY_SET_HITS, e);
		}
		try {
			Files.deleteIfExists(Paths.get(FILE_PATH_POLICY_HITS));
		} catch (IOException e) {
			log.error("Error deleting File " + FILE_PATH_POLICY_HITS, e);
		}
		try {
			Files.deleteIfExists(Paths.get(FILE_PATH_POLICY_CONDITION_HITS));
		} catch (IOException e) {
			log.error("Error deleting File " + FILE_PATH_POLICY_CONDITION_HITS, e);
		}
		
	}

	@Override
	public List<PolicySetHit> readPolicySetHits() {
		return readFileLines(FILE_PATH_POLICY_SET_HITS).stream().map(line -> {
			return PolicySetHit.fromString(line);
		}).collect(Collectors.toList());
	}

	@Override
	public List<PolicyHit> readPolicyHits() {
		return readFileLines(FILE_PATH_POLICY_HITS).stream().map(line -> {
			return PolicyHit.fromString(line);
		}).collect(Collectors.toList());
	}

	@Override
	public List<PolicyConditionHit> readPolicyConditionHits() {
		return readFileLines(FILE_PATH_POLICY_CONDITION_HITS).stream().map(line -> {
			return PolicyConditionHit.fromString(line);
		}).collect(Collectors.toList());
	}

	
	private List<String> readFileLines(String filePath) {
		try {
			return Files.readAllLines(Paths.get(filePath));
		} catch (IOException e) {
			log.error("Error reading File " + FILE_PATH_POLICY_CONDITION_HITS, e);
		}
		return new LinkedList<>();
	}
}
