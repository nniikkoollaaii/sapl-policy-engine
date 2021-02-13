package io.sapl.test;

import io.sapl.test.StepBuilder.GivenStep;
import io.sapl.test.StepBuilder.WhenStep;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.interpreter.InitializationException;
import io.sapl.grammar.sapl.SAPL;
import io.sapl.interpreter.DefaultSAPLInterpreter;
import io.sapl.interpreter.EvaluationContext;
import io.sapl.interpreter.functions.AnnotationFunctionContext;
import io.sapl.interpreter.pip.AnnotationAttributeContext;
import lombok.AllArgsConstructor;
import lombok.NonNull;

public class SaplUnitTestFixture implements SaplTestFixture {

	private static final String ERROR_MESSAGE_MISSING_SAPL_DOCUMENT_NAME = "Bevor constructing a test case you have to specifiy the filename where to find your SAPL policy!" 
			+ "\n\nProbably you forgot to call \".setSaplDocumentName(\"\")\"";
	private static final String ERROR_MESSAGE_POLICY_FILE_NOT_FOUND = "We could not find your specified file with your policy on the classpath. We looked at \"%s\" and \"%s\"";
	private static final String DEFAULT_PATH = "policies/";
	
	
	private String saplDocumentName;	
	private AnnotationAttributeContext attributeCtx = new AnnotationAttributeContext();
	private AnnotationFunctionContext functionCtx = new AnnotationFunctionContext();
	private Map<String, JsonNode> variables = new HashMap<String, JsonNode>(1);
	
	public SaplUnitTestFixture(String saplDocumentName) {
		this.saplDocumentName = saplDocumentName;
	}

	@Override
	public GivenStep constructTestCaseWithMocks() {
		if(this.saplDocumentName == null || this.saplDocumentName.isEmpty()) {
			throw new SaplTextException(ERROR_MESSAGE_MISSING_SAPL_DOCUMENT_NAME);
		}
        return StepBuilder.newBuilderAtGivenStep(readSaplDocument(), this.attributeCtx, this.functionCtx, this.variables);
	}
	
	@Override
	public GivenStep constructTestCaseWithMocks(String saplDocumentName) {
		this.saplDocumentName = saplDocumentName;
        return StepBuilder.newBuilderAtGivenStep(readSaplDocument(), this.attributeCtx, this.functionCtx, this.variables);
	}
	
	@Override
	public WhenStep constructTestCase() {		
		if(this.saplDocumentName == null || this.saplDocumentName.isEmpty()) {
		throw new SaplTextException(ERROR_MESSAGE_MISSING_SAPL_DOCUMENT_NAME);
	}
        return StepBuilder.newBuilderAtWhenStep(readSaplDocument(), this.attributeCtx, this.functionCtx, this.variables);
	}

	@Override
	public WhenStep constructTestCase(String saplDocumentName) {
		this.saplDocumentName = saplDocumentName;
        return StepBuilder.newBuilderAtWhenStep(readSaplDocument(), this.attributeCtx, this.functionCtx, this.variables);
	}
	
	@Override
	public SaplTestFixture registerPIP(Object pip) {
		try {
			this.attributeCtx.loadPolicyInformationPoint(pip);
		} catch (InitializationException e) {
			throw new SaplTextException("Error loading your specified PIP", e);
		}
		return this;
	}

	@Override
	public SaplTestFixture registerFunction(Object library) {
		try {
			this.functionCtx.loadLibrary(library);
		} catch (InitializationException e) {
			throw new SaplTextException("Error loading your specified Function-Library", e);
		}
		return this;
	}
	
	@Override
	public SaplTestFixture registerVariable(String key, JsonNode value) {
		if(this.variables.containsKey(key)) {
			throw new SaplTextException("The VariableContext already contains a key \"" + key + "\"");
		}
			this.variables.put(key, value);
		return this;
	}
	
	@Override
	public SaplTestFixture setSaplDocumentName(String documentName) {
		this.saplDocumentName = documentName;
		return this;
	}
	
	private SAPL readSaplDocument() {
		String filename = constructFileEnding(this.saplDocumentName);
		DefaultSAPLInterpreter interpreter = new DefaultSAPLInterpreter();
		return interpreter.parse(findFileOnClasspath(filename));
	}
	
	private String constructFileEnding(String filename) {
		if(this.saplDocumentName.endsWith(".sapl")) {
			return filename;
		} else {
			return filename + ".sapl";
		}
	}

	private InputStream findFileOnClasspath(String filename) {
		if(getClass().getClassLoader().getResourceAsStream(filename) != null) {
			return getClass().getClassLoader().getResourceAsStream(filename);
		} else if(getClass().getClassLoader().getResourceAsStream(DEFAULT_PATH + filename) != null) {
			return getClass().getClassLoader().getResourceAsStream(DEFAULT_PATH + filename);
		} else {
			throw new SaplTextException(String.format(ERROR_MESSAGE_POLICY_FILE_NOT_FOUND, filename, DEFAULT_PATH + filename));
		}
	}


	
}
