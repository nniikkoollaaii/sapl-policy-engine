package io.sapl.test.unit;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.Decision;
import io.sapl.test.SaplTestFixture;
import io.sapl.test.SaplUnitTestFixture;

public class PolicyWithResourceTest {
	
	private SaplTestFixture fixture;

	@Before
	public void setUp() {
		fixture = new SaplUnitTestFixture("policyWithResource");
	}

	
	@Test
	public void test_policyWithResource() {
		//List<String> list = Arrays.stream({"complex", "array", "of", "objects", "from", "database"});
			
		fixture.constructTestCase()
			.when(AuthorizationSubscription.of("willi", "read", "something"))
			//.expect(decision)
			.expect((AuthorizationDecision dec) -> {
				Assertions.assertThat(dec.getDecision()).isEqualTo(Decision.PERMIT);
				Assertions.assertThat(dec.getResource().isPresent()).isTrue();
				//custom validation of JsonNode in resource
				Assertions.assertThat(dec.getResource().get()
						//get here your complex resource object
						.asBoolean()).isTrue();
			})
			.verify();
		
	}
}
