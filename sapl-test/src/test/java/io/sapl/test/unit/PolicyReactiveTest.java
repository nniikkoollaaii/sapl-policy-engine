package io.sapl.test.unit;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.Test;

import io.sapl.api.interpreter.Val;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.functions.TemporalFunctionLibrary;
import io.sapl.test.SaplTestFixture;
import io.sapl.test.SaplUnitTestFixture;
import reactor.core.publisher.Flux;

public class PolicyReactiveTest {

	private SaplTestFixture fixture;
	
	@Before
	public void setUp() {
		fixture = new SaplUnitTestFixture("policyReactive")
				//.registerPIP(null)
				.registerFunction(new TemporalFunctionLibrary());
	}

	
	@Test
	public void test_reactivePolicy() {
		var timestamp0 = Val.of(ZonedDateTime.of(2021, 1, 7, 18, 24, 0, 0, ZoneId.of("UTC")).toString());
		var timestamp1 = Val.of(ZonedDateTime.of(2021, 1, 7, 18, 24, 1, 0, ZoneId.of("UTC")).toString());
		var timestamp2 = Val.of(ZonedDateTime.of(2021, 1, 7, 18, 24, 2, 0, ZoneId.of("UTC")).toString());
		var timestamp3 = Val.of(ZonedDateTime.of(2021, 1, 7, 18, 24, 3, 0, ZoneId.of("UTC")).toString());
		var timestamp4 = Val.of(ZonedDateTime.of(2021, 1, 7, 18, 24, 4, 0, ZoneId.of("UTC")).toString());
		var timestamp5 = Val.of(ZonedDateTime.of(2021, 1, 7, 18, 24, 5, 0, ZoneId.of("UTC")).toString());
		Flux<Val> mockValue = Flux.just(timestamp0, timestamp1, timestamp2, timestamp3, timestamp4, timestamp5);
			
		fixture.constructTestCaseWithMocks()
			.givenPIP("clock.ticker", mockValue)
			.when(AuthorizationSubscription.of("ROLE_DOCTOR", "read", "heartBeatData"))
			.expectNextDeny()
			.expectNextDeny()
			.expectNextDeny()
			.expectNextDeny()
			.expectNextDeny()
			//.expectNextDeny(5)
			.expectNextPermit()
			.verify();
	}
	
	@Test
	public void test_reactivePolicyWithVirtualTime() {
					
		fixture.constructTestCaseWithMocks()
			.withVirtualTime()
			.when(AuthorizationSubscription.of("ROLE_DOCTOR", "read", "heartBeatData"))
			.thenAwait(Duration.ofSeconds(3))
			.expectNextDeny()
			.thenAwait(Duration.ofSeconds(3))
			.expectNextDeny()
			.thenAwait(Duration.ofSeconds(3))
			.expectNextDeny()
			.thenAwait(Duration.ofSeconds(3))
			.expectNextDeny()
			.thenAwait(Duration.ofSeconds(3))
			.expectNextDeny()
			.thenAwait(Duration.ofSeconds(3))
			.expectNextPermit()
			.verify();
	}
}
