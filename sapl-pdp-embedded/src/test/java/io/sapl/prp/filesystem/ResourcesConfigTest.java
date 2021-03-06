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
package io.sapl.prp.filesystem;

import java.util.logging.Level;

import org.junit.Test;

import io.sapl.pdp.embedded.config.resources.ResourcesVariablesAndCombinatorSource;
import reactor.core.publisher.SignalType;

public class ResourcesConfigTest {
	@Test
	public void doTest() throws InterruptedException {
		var configProvider = new ResourcesVariablesAndCombinatorSource("/policies");
		configProvider.getDocumentsCombinator().log(null, Level.INFO, SignalType.ON_NEXT).blockFirst();
		configProvider.getVariables().log(null, Level.INFO, SignalType.ON_NEXT).blockFirst();
		configProvider.dispose();
	}
}
