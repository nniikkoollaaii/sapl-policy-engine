/*
 * Copyright © 2017-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sapl.server.ce.service.pdpconfiguration;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.server.ce.model.pdpconfiguration.Variable;
import io.sapl.server.ce.pdp.PDPConfigurationPublisher;
import io.sapl.server.ce.persistence.VariablesRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for {@link Variable}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VariablesService {
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final String DEFAULT_JSON_VALUE = "{\n  \"property\" : \"value\"\n}";

	private final VariablesRepository variableRepository;
	private final PDPConfigurationPublisher pdpConfigurationPublisher;

	@PostConstruct
	public void init() {
		pdpConfigurationPublisher.publishVariables(getAll());
	}

	/**
	 * Gets all available {@link Variable} instances.
	 * 
	 * @return the available {@link Variable} instances
	 */
	public Collection<Variable> getAll() {
		return variableRepository.findAll();
	}

	/**
	 * Gets the amount of available {@link Variable} instances.
	 * 
	 * @return the amount
	 */
	public long getAmount() {
		return variableRepository.count();
	}

	/**
	 * Gets a specific {@link Variable} by its id.
	 * 
	 * @param id the id of the {@link Variable}
	 * @return the {@link Variable}
	 */
	public Variable getById(long id) {
		Optional<Variable> optionalEntity = variableRepository.findById(id);
		if (optionalEntity.isEmpty()) {
			throw new IllegalArgumentException(String.format("entity with id %d is not existing", id));
		}

		return optionalEntity.get();
	}

	/**
	 * Creates a {@link Variable} with default values.
	 * 
	 * @return the created {@link Variable}
	 * @throws DuplicatedVariableNameException thrown if the name is already used by
	 *                                         another variable
	 */
	public Variable createDefault() throws DuplicatedVariableNameException {
		String name = UUID.randomUUID().toString().replace("-", "");
		String jsonValue = DEFAULT_JSON_VALUE;

		checkForDuplicatedName(name, null);

		Variable variable = new Variable(null, name, jsonValue);
		variableRepository.save(variable);

		log.info("created variable {}: {}", name, jsonValue);

		publishVariables();

		return variable;
	}

	/**
	 * Creates a {@link Variable}.
	 * 
	 * @param name      the name
	 * @param jsonValue the JSON value
	 * @return the created {@link Variable}
	 * @throws InvalidJsonException            thrown if the provided JSON value is
	 *                                         invalid
	 * @throws DuplicatedVariableNameException thrown if the name is used by another
	 *                                         variable
	 */
	public Variable edit(long id, @NonNull String name, @NonNull String jsonValue)
			throws InvalidJsonException, DuplicatedVariableNameException {
		VariablesService.checkIsJsonValue(jsonValue);
		checkForDuplicatedName(name, id);

		Optional<Variable> optionalEntity = variableRepository.findById(id);
		if (optionalEntity.isEmpty()) {
			throw new IllegalArgumentException(String.format("entity with id %d is not existing", id));
		}

		Variable oldVariable = optionalEntity.get();

		Variable editedVariable = new Variable();
		editedVariable.setId(id);
		editedVariable.setName(name);
		editedVariable.setJsonValue(jsonValue);

		variableRepository.save(editedVariable);

		log.info("edited variable: {} -> {}", oldVariable, editedVariable);

		publishVariables();

		return oldVariable;
	}

	/**
	 * Deletes a {@link Variable}.
	 * 
	 * @param id the id of the {@link Variable}
	 */
	public void delete(Long id) {
		Optional<Variable> variableToDelete = variableRepository.findById(id);
		variableRepository.deleteById(id);
		log.info("deleted variable {}: {}", variableToDelete.get().getName(), variableToDelete.get().getJsonValue());
		publishVariables();
	}

	private static void checkIsJsonValue(@NonNull String jsonValue) throws InvalidJsonException {
		if (jsonValue.isBlank()) {
			throw new InvalidJsonException(jsonValue);
		}

		try {
			VariablesService.objectMapper.readTree(jsonValue);
		} catch (JsonProcessingException ex) {
			throw new InvalidJsonException(jsonValue, ex);
		}
	}

	/**
	 * Checks a for a duplicated name of a variable.
	 * 
	 * @param name the name to check
	 * @param id   the id of the variable to check
	 * @throws DuplicatedVariableNameException thrown if the name is already used by
	 *                                         another variable
	 */
	private void checkForDuplicatedName(@NonNull String name, Long id) throws DuplicatedVariableNameException {
		Collection<Variable> variablesWithName = variableRepository.findByName(name);

		boolean isAnyVariableWithNameExisting = variablesWithName.stream()
				.anyMatch(variable -> variable.getName().equals(name) && !variable.getId().equals(id));

		if (isAnyVariableWithNameExisting) {
			throw new DuplicatedVariableNameException(name);
		}
	}

	private void publishVariables() {
		Collection<Variable> variables = getAll();
		pdpConfigurationPublisher.publishVariables(variables);
	}
}
