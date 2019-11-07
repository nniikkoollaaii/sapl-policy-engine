package io.sapl.interpreter.pip;

import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@PolicyInformationPoint(name = TestPIP.NAME, description = TestPIP.DESCRIPTION)
public class TestPIP {

	public static final String NAME = "sapl.pip.test";

	public static final String DESCRIPTION = "Policy information Point for testing";

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	public TestPIP() {
		LOGGER.trace("PIP: " + NAME);
	}

	@Attribute
	public Flux<JsonNode> echo(JsonNode value, Map<String, JsonNode> variables) {
		logVars(variables);
		return Flux.just(value);
	}

	private void logVars(Map<String, JsonNode> variables) {
		for (Entry<String, JsonNode> entry : variables.entrySet()) {
			LOGGER.trace("env: {} value: {}", entry.getKey(), entry.getValue());
		}
	}

	@Attribute
	public Flux<JsonNode> someVariableOrNull(JsonNode value, Map<String, JsonNode> variables) {
		logVars(variables);
		if (variables.containsKey(value.asText())) {
			return Flux.just(variables.get(value.asText()).deepCopy());
		}
		return Flux.just(JSON.nullNode());
	}

}
