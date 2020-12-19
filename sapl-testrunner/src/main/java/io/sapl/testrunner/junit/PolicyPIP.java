package io.sapl.testrunner.junit;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PolicyPIP {

	Class<?>[] value();

}
