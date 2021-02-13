package io.sapl.test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import io.sapl.api.interpreter.InitializationException;
import io.sapl.api.interpreter.Val;
import io.sapl.grammar.sapl.Arguments;
import io.sapl.interpreter.EvaluationContext;
import io.sapl.interpreter.pip.AttributeContext;
import io.sapl.interpreter.pip.PolicyInformationPointDocumentation;
import reactor.core.publisher.Flux;

public class MockingAttributeContext implements AttributeContext {

	private static final String ERROR_INVALID_MOCK_INSTANCE = "Expecting an instance of %s but got %s";
	private static final String ERROR_DUPLICATE_MOCK_REGISTRATION = "Duplicate registration of mock for PIP attribute \"%s\"";
	private static final String ERROR_MOCK_INVALID_FULLNAME = "Got invalid attribute reference containing more than one \".\" delimiter: \"%s\"";
	private static final String NAME_DELIMITER = ".";
	/**
	 * Holds an AttributeContext implementation to delegate evaluations if this attribut is not mocked
	 */
	private final AttributeContext unmockedAttributeContext;
	/**
	 * Contains a Map of all registered mocks.
	 * Key is the String of the fullname of the attribute finder
	 * Value is the {@link Flux<Val>} to be returned
	 */
	private final Map<String, Flux<Val>> registeredMocks;
	private final Collection<PolicyInformationPointDocumentation> pipDocumentations = new LinkedList<>();
	
	public MockingAttributeContext(AttributeContext unmockedAttributeContext) {
		this.unmockedAttributeContext = unmockedAttributeContext;
		this.registeredMocks = new HashMap<String, Flux<Val>>();
	}

	@Override
	public Boolean isProvidedFunction(String function) {
		if(this.registeredMocks.containsKey(function)) {
			return true;
		} else if(unmockedAttributeContext.isProvidedFunction(function)) {
			return true;
		} else {
			return false;			
		}
	}

	@Override
	public Collection<String> providedFunctionsOfLibrary(String pipName) {
		Set<String> set = new HashSet<>();
		//read all mocked functions for pipName 
		for(String fullName : this.registeredMocks.keySet()) {
			String[] splitted = fullName.split(Pattern.quote(NAME_DELIMITER));
			if(splitted.length != 2)
				throw new SaplTextException(String.format(ERROR_MOCK_INVALID_FULLNAME, fullName));
			
			if(splitted[0].equals(pipName))
				set.add(splitted[1]);
		}
		//read all not mocked functions for pipName
		set.addAll(this.unmockedAttributeContext.providedFunctionsOfLibrary(pipName));
		
		return set;
	}

	@Override
	public Flux<Val> evaluate(String attribute, Val value, EvaluationContext ctx, Arguments arguments) {
		if(this.registeredMocks.containsKey(attribute)) {
			return this.registeredMocks.get(attribute);
		} else {
			return this.unmockedAttributeContext.evaluate(attribute, value, ctx, arguments);
		}
	}

	@Override
	public void loadPolicyInformationPoint(Object pip) throws InitializationException {
		if(!(pip instanceof PIPMockDTO)) {
			throw new SaplTextException(String.format(ERROR_INVALID_MOCK_INSTANCE, PIPMockDTO.class.toString(), pip.toString()));
		}
		PIPMockDTO dto = (PIPMockDTO) pip;
		String[] splitted = dto.getFullname().split(Pattern.quote(NAME_DELIMITER));
		if(splitted.length != 2) {
			throw new SaplTextException(String.format(ERROR_MOCK_INVALID_FULLNAME, dto.getFullname()));
		}
		if(this.registeredMocks.containsKey(dto.getFullname())) {
			throw new SaplTextException(String.format(ERROR_DUPLICATE_MOCK_REGISTRATION, dto.getFullname()));
		}
		this.registeredMocks.put(dto.getFullname(), dto.getMockReturnValue());
		
		
		PolicyInformationPointDocumentation pipDocs = new PolicyInformationPointDocumentation(splitted[0],
				"Mocked PIP", pip);
		this.pipDocumentations.add(pipDocs);		
	}

	@Override
	public Collection<PolicyInformationPointDocumentation> getDocumentation() {
		return Collections.unmodifiableCollection(pipDocumentations);
	}
}
