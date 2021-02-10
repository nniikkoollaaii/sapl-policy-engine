package io.sapl.test.unit;

import org.junit.Before;
import org.junit.Test;

import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.test.SaplTestFixture;
import io.sapl.test.SaplUnitTestFixture;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PolicyWithSimplePIPTest {

	private SaplTestFixture fixture;
	
	@Before
	public void setUp() {
		fixture = new SaplUnitTestFixture("policyWithSimplePIP");
	}
	
	@Test
	public void test_policyWithSimpleMockedPIP() {
		
		fixture.constructTestCaseWithMocks()
			.given("test.upper", Mono.just("WILLI"))
			//.given(TestPIP.class,  Mono.just("WILLI"))
			//.given(new TestPIP(),  Mono.just("WILLI"))
			.when(AuthorizationSubscription.of("willi", "read", "something"))
			.expectPermit()
			.verify();
		
	}
}
