package io.sapl.test;

import io.sapl.api.interpreter.Val;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;

@Data
@AllArgsConstructor
class PIPMockDTO {

	private String fullname;
	private Flux<Val> mockReturnValue;
}
