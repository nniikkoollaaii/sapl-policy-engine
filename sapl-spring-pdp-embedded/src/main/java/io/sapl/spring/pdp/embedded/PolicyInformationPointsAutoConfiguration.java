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
package io.sapl.spring.pdp.embedded;

import io.sapl.pip.ClockPolicyInformationPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.web3j.protocol.Web3j;

import io.netty.handler.ssl.SslContext;
import io.sapl.interpreter.pip.EthereumPolicyInformationPoint;
import io.sapl.interpreter.pip.GeoPolicyInformationPoint;
import io.sapl.pip.http.HttpPolicyInformationPoint;
import io.sapl.pip.http.WebClientRequestExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class PolicyInformationPointsAutoConfiguration {

	@Configuration
	@ConditionalOnClass(io.sapl.pip.http.HttpPolicyInformationPoint.class)
	public static class HTTPConfiguration {
		@Nullable
		@Autowired
		SslContext sslContext;

		@Bean
		public HttpPolicyInformationPoint httpPolicyInformationPoint() {
			if (sslContext == null) {
				log.info("HTTP PIP present. No SslContext bean. Loading with default SslContext...");
				return new HttpPolicyInformationPoint(new WebClientRequestExecutor());
			} else {
				log.info("HTTP PIP present. Loading with custom SslContext bean...");
				return new HttpPolicyInformationPoint(new WebClientRequestExecutor(sslContext));
			}
		}
	}

	@Configuration
	@ConditionalOnClass(io.sapl.interpreter.pip.GeoPolicyInformationPoint.class)
	public static class GeoConfiguration {
		@Bean
		public GeoPolicyInformationPoint geoPolicyInformationPoint() {
			log.info("GEO PIP present. Loading.");
			return new GeoPolicyInformationPoint();
		}
	}

	@Configuration
	@ConditionalOnClass(io.sapl.interpreter.pip.EthereumPolicyInformationPoint.class)
	public static class EthereumConfiguration {
		@Bean
		public EthereumPolicyInformationPoint ethereumPolicyInformationPoint(Web3j web3j) {
			log.info("Ethereum PIP present. Loading.");
			if (web3j != null) {
				log.info("Web3j found. Using Web3j present in application.");
				return new EthereumPolicyInformationPoint(web3j);
			}
			log.info("No Web3j present in application. Using default Web3j");
			return new EthereumPolicyInformationPoint();
		}
	}

	@Bean
	public ClockPolicyInformationPoint clockPolicyInformationPoint() {
		return new ClockPolicyInformationPoint();
	}
}
