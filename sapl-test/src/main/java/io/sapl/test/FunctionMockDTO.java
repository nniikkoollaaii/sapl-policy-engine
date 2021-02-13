package io.sapl.test;

import io.sapl.api.interpreter.Val;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;

@Data
@AllArgsConstructor
public class FunctionMockDTO {
	private String fullname;
	private Val mockReturnValue;
}
