package io.sapl.test.unit;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.junit.Before;
import org.junit.Test;

import io.sapl.api.interpreter.Val;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.functions.TemporalFunctionLibrary;
import io.sapl.test.SaplTestFixture;
import io.sapl.test.SaplUnitTestFixture;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PolicyWithSimpleFunctionTest {
	
	private SaplTestFixture fixture;
	
	@Before
	public void setUp() {
		fixture = new SaplUnitTestFixture("policyWithSimpleFunction");
			//Registration of Functions or PIPs for every test case
			//.registerFunction(new TemporalFunctionLibrary())
	}

	
	@Test
	public void test_policyWithSimpleFunction() {
		
		
		fixture
			.registerFunction(new TemporalFunctionLibrary()) //do not mock function in this unit test
			.constructTestCase()
			.when(AuthorizationSubscription.of("willi", "read", "something"))
			.expectPermit()
			.verify();
	
	}
	
	@Test
	public void test_policyWithSimpleMockedFunction() {
		
		fixture.constructTestCaseWithMocks()
			.given("time.dayOfWeekFrom", Mono.just("SATURDAY"))
			.when(AuthorizationSubscription.of("willi", "read", "something"))
			.expectPermit()
			.verify();
	
	}
}
