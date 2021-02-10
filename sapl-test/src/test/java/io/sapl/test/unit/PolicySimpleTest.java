package io.sapl.test.unit;

import org.junit.Before;
import org.junit.Test;

import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.test.SaplTestFixture;
import io.sapl.test.SaplUnitTestFixture;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class PolicySimpleTest {

	private SaplTestFixture fixture;
	
	@Before
	public void setUp() {
		fixture = new SaplUnitTestFixture("policySimple");
	}
	
	@Test
	public void test_simplePolicy() {

		fixture.constructTestCase()
			.when(AuthorizationSubscription.of("willi", "read", "something"))
			.expectPermit()
			.verify();
			
	}
	
}
