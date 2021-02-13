package io.sapl.test.unit;

import org.junit.Before;
import org.junit.Test;

import io.sapl.api.interpreter.Val;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.test.SaplTestFixture;
import io.sapl.test.SaplUnitTestFixture;
import reactor.core.publisher.Flux;

public class PolicyWithSimplePIPTest {

	private SaplTestFixture fixture;
	
	@Before
	public void setUp() {
		fixture = new SaplUnitTestFixture("policyWithSimplePIP");
	}
	
	@Test
	public void test_policyWithSimpleMockedPIP() {
		
		fixture.constructTestCaseWithMocks()
			.givenPIP("test.upper", Flux.just(Val.of("WILLI")))
			.when(AuthorizationSubscription.of("willi", "read", "something"))
			.expectPermit()
			.verify();
		
	}
	
	@Test
	public void test_policyWithSimplePIP() {
		
		fixture.registerPIP(new TestPIP())
			.constructTestCase()
			.when(AuthorizationSubscription.of("willi", "read", "something"))
			.expectPermit()
			.verify();
		
	}
}
