package io.sapl.test.unit;


import org.junit.Before;
import org.junit.Test;

import io.sapl.api.interpreter.Val;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.functions.TemporalFunctionLibrary;
import io.sapl.test.SaplTestFixture;
import io.sapl.test.SaplUnitTestFixture;

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
			.givenFunction("time.dayOfWeekFrom", Val.of("SATURDAY"))
			.when(AuthorizationSubscription.of("willi", "read", "something"))
			.expectPermit()
			.verify();
	
	}
}
