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
import io.sapl.interpreter.functions.FunctionContext;
import io.sapl.interpreter.functions.LibraryDocumentation;

public class MockingFunctionContext  implements FunctionContext {

	private static final String ERROR_INVALID_MOCK_INSTANCE = "Expecting an instance of %s but got %s";
	private static final String ERROR_DUPLICATE_MOCK_REGISTRATION = "Duplicate registration of mock for Function \"%s\"";
	private static final String ERROR_MOCK_INVALID_FULLNAME = "Got invalid function reference containing more than one \".\" delimiter: \"%s\"";
	private static final String NAME_DELIMITER = ".";
	
	/**
	 * Holds an FunctionContext implementation to delegate evaluations if this function is not mocked
	 */
	private final FunctionContext unmockedFunctionContext;
	/**
	 * Contains a Map of all registered mocks.
	 * Key is the String of the fullname of the function
	 * Value is the {@link Val} to be returned
	 */
	private final Map<String, Val> registeredMocks;
	private final Collection<LibraryDocumentation> functionDocumentations = new LinkedList<>();
	
	public MockingFunctionContext(FunctionContext unmockedFunctionContext) {
		this.unmockedFunctionContext = unmockedFunctionContext;
		this.registeredMocks = new HashMap<String, Val>();
	}
	
	@Override
	public Boolean isProvidedFunction(String function) {
		if(this.registeredMocks.containsKey(function)) {
			return true;
		} else if(unmockedFunctionContext.isProvidedFunction(function)) {
			return true;
		} else {
			return false;			
		}
	}

	@Override
	public Collection<String> providedFunctionsOfLibrary(String libName) {
		Set<String> set = new HashSet<>();
		//read all mocked functions for functionName 
		for(String fullName : this.registeredMocks.keySet()) {
			String[] splitted = fullName.split(Pattern.quote(NAME_DELIMITER));
			if(splitted.length != 2)
				throw new SaplTextException(String.format(ERROR_MOCK_INVALID_FULLNAME, fullName));
			
			if(splitted[0].equals(libName))
				set.add(splitted[1]);
		}
		//read all not mocked functions for pipName
		set.addAll(this.unmockedFunctionContext.providedFunctionsOfLibrary(libName));
		
		return set;
	}

	@Override
	public Val evaluate(String function, Val... parameters) {
		if(this.registeredMocks.containsKey(function)) {
			return this.registeredMocks.get(function);
		} else {
			return this.unmockedFunctionContext.evaluate(function, parameters);
		}
	}

	@Override
	public void loadLibrary(Object library) throws InitializationException {
		if(!(library instanceof FunctionMockDTO)) {
			throw new SaplTextException(String.format(ERROR_INVALID_MOCK_INSTANCE, FunctionMockDTO.class.toString(), library.toString()));
		}
		FunctionMockDTO dto = (FunctionMockDTO) library;
		String[] splitted = dto.getFullname().split(Pattern.quote(NAME_DELIMITER));
		if(splitted.length != 2) {
			throw new SaplTextException(String.format(ERROR_MOCK_INVALID_FULLNAME, dto.getFullname()));
		}
		if(this.registeredMocks.containsKey(dto.getFullname())) {
			throw new SaplTextException(String.format(ERROR_DUPLICATE_MOCK_REGISTRATION, dto.getFullname()));
		}
		this.registeredMocks.put(dto.getFullname(), dto.getMockReturnValue());
		
		
		LibraryDocumentation functionDocs = new LibraryDocumentation(splitted[0],
				"Mocked Function", library);
		this.functionDocumentations.add(functionDocs);
		
	}

	@Override
	public Collection<LibraryDocumentation> getDocumentation() {
		return Collections.unmodifiableCollection(functionDocumentations);
	}

}
