/**
 * Copyright © 2017 Dominic Heutelbeck (dheutelbeck@ftk.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.sapl.functions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.sapl.api.functions.Function;
import io.sapl.api.functions.FunctionException;
import io.sapl.api.functions.FunctionLibrary;

@FunctionLibrary(name = FilterFunctionLibrary.NAME, description = FilterFunctionLibrary.DESCRIPTION)
public class FilterFunctionLibrary {

	public static final String NAME = "filter";

	public static final String DESCRIPTION = "Essential functions for content filtering.";

	private static final String BLACKEN_DEFAULT_REPLACEMENT = "X";

	private static final int BLACKEN_DEFAULT_SHOW_LEFT = 0;

	private static final int BLACKEN_DEFAULT_SHOW_RIGHT = 0;

	private static final String BLACKEN_DOC = "blacken(STRING, DISCLOSE_LEFT, DISCLOSE_RIGHT, REPLACEMENT): Assumes that DISCLOSE_LEFT and DISCLOSE_RIGHT are positive integers and STRING and REPLACEMENT are strings."
			+ " Replaces each character in STRING by REPLACEMENT, leaving DISCLOSE_LEFT characters from the beginning and DISCLOSE_RIGHT characters from the end unchanged."
			+ " Except for STRING, all parameters are optional."
			+ " DISCLOSE_LEFT defaults to 0, DISCLOSE_RIGHT defaults to 0 and REPLACEMENT defaults to 'X'"
			+ " Returns the modified STRING.";

	private static final String REPLACE_DOC = "replace(ORIGINAL, REPLACEMENT): Assumes that ORIGINAL and REPLACEMENT are JSON nodes. Returns REPLACEMENT.";

	private static final String ILLEGAL_PARAMETERS_COUNT = "Illegal number of parameters provided.";

	private static final String ILLEGAL_PARAMETER_DISCLOSE_LEFT = "Illegal parameter for DISCLOSE_LEFT. Expecting a positive integer.";

	private static final String ILLEGAL_PARAMETER_DISCLOSE_RIGHT = "Illegal parameter for DISCLOSE_RIGHT. Expecting a positive integer.";

	private static final String ILLEGAL_PARAMETER_REPLACEMENT = "Illegal parameter for REPLACEMENT. Expecting a string.";

	private static final String ILLEGAL_PARAMETER_STRING = "Illegal parameter for STRING. Expecting a string.";

	private static final int ZERO = 0;

	private static final int ONE = 1;

	private static final int TWO = 2;

	private static final int THREE = 3;

	private static final int PARAMETERS_MAX = 4;

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	@Function(docs = BLACKEN_DOC)
	public static JsonNode blacken(JsonNode... parameters) throws FunctionException {
		if (parameters.length > PARAMETERS_MAX) {
			throw new FunctionException(ILLEGAL_PARAMETERS_COUNT);
		}
		if (!parameters[0].isTextual()) {
			throw new FunctionException(ILLEGAL_PARAMETER_STRING);
		}

		String replacement;
		if (parameters.length == PARAMETERS_MAX) {
			if (!parameters[THREE].isTextual()) {
				throw new FunctionException(ILLEGAL_PARAMETER_REPLACEMENT);
			}
			replacement = parameters[THREE].asText();
		}
		else {
			replacement = BLACKEN_DEFAULT_REPLACEMENT;
		}

		int discloseRight;
		if (parameters.length >= THREE) {
			if (!parameters[TWO].isNumber() || parameters[TWO].asInt() < 0) {
				throw new FunctionException(ILLEGAL_PARAMETER_DISCLOSE_RIGHT);
			}
			discloseRight = parameters[TWO].asInt();
		}
		else {
			discloseRight = BLACKEN_DEFAULT_SHOW_RIGHT;
		}

		int discloseLeft;
		if (parameters.length >= TWO) {
			if (!parameters[ONE].isNumber() || parameters[ONE].asInt() < 0) {
				throw new FunctionException(ILLEGAL_PARAMETER_DISCLOSE_LEFT);
			}
			discloseLeft = parameters[ONE].asInt();
		}
		else {
			discloseLeft = BLACKEN_DEFAULT_SHOW_LEFT;
		}

		String string = parameters[ZERO].asText();
		StringBuilder result = new StringBuilder();
		if (discloseLeft + discloseRight < string.length()) {
			if (discloseLeft > 0) {
				result.append(string.substring(0, discloseLeft));
			}
			int replacedChars = string.length() - discloseLeft - discloseRight;
			for (int i = 0; i < replacedChars; i++) {
				result.append(replacement);
			}
			if (discloseRight > 0) {
				result.append(string.substring(discloseLeft + replacedChars));
			}
		}
		else {
			result.append(string);
		}

		return JSON.textNode(result.toString());
	}

	@Function(docs = REPLACE_DOC)
	public static JsonNode replace(JsonNode original, JsonNode replacement) {
		return replacement;
	}

}
