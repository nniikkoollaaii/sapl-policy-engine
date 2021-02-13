package io.sapl.test;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.test.StepBuilder.GivenStep;
import io.sapl.test.StepBuilder.WhenStep;

public interface SaplTestFixture {
	GivenStep constructTestCaseWithMocks();
	GivenStep constructTestCaseWithMocks(String documentName);
	WhenStep constructTestCase();
	WhenStep constructTestCase(String documentName);
	

	SaplTestFixture setSaplDocumentName(String documentName);
	SaplTestFixture registerPIP(Object pip);
	SaplTestFixture registerFunction(Object function);
	SaplTestFixture registerVariable(String key, JsonNode value);
}
