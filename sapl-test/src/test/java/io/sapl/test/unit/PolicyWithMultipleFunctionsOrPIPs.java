package io.sapl.test.unit;

import org.junit.Before;
import org.junit.Test;

import io.sapl.api.interpreter.Val;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.functions.TemporalFunctionLibrary;
import io.sapl.test.SaplTestFixture;
import io.sapl.test.SaplUnitTestFixture;
import reactor.core.publisher.Flux;

public class PolicyWithMultipleFunctionsOrPIPs {
private SaplTestFixture fixture;
	
	@Before
	public void setUp() {
		fixture = new SaplUnitTestFixture("policyWithMultipleFunctionsOrPIPs")
			//Registration of Functions or PIPs for every test case
			.registerFunction(new TemporalFunctionLibrary());
	}

	
	@Test
	public void test_policyWithMultipleMocks() {
		
		fixture.constructTestCaseWithMocks()
			.givenPIP("test.upper", Flux.just(Val.of("WILLI")))
			.givenFunction("time.dayOfWeekFrom", Val.of("SATURDAY"))
			.when(AuthorizationSubscription.of("willi", "read", "something"))
			.expectPermit()
			.verify();
	
	}
}
