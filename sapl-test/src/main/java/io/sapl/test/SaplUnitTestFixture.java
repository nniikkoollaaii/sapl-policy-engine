package io.sapl.test;

import io.sapl.test.StepBuilder.GivenStep;
import io.sapl.test.StepBuilder.WhenStep;
import io.sapl.grammar.sapl.SAPL;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class SaplUnitTestFixture implements SaplTestFixture {

	@NonNull
	private String saplDocumentName;

	@Override
	public GivenStep constructTestCaseWithMocks() {
        return StepBuilder.newBuilderAtGivenStep(readSaplDocument());
	}
	
	@Override
	public GivenStep constructTestCaseWithMocks(String saplDocumentName) {
		this.saplDocumentName = saplDocumentName;
        return StepBuilder.newBuilderAtGivenStep(readSaplDocument());
	}
	
	@Override
	public WhenStep constructTestCase() {
        return StepBuilder.newBuilderAtWhenStep(readSaplDocument());
	}

	@Override
	public WhenStep constructTestCase(String saplDocumentName) {
		this.saplDocumentName = saplDocumentName;
        return StepBuilder.newBuilderAtWhenStep(readSaplDocument());
	}
	
	@Override
	public SaplTestFixture registerPIP(Object pip) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public SaplTestFixture registerFunction(Object function) {
		// TODO Auto-generated method stub
		return this;
	}
	
	private SAPL readSaplDocument() {
		//TODO: ...
		return null;
	}

	@Override
	public SaplTestFixture setSaplDocumentName(String documentName) {
		this.saplDocumentName = documentName;
		return this;
	}

	
}
