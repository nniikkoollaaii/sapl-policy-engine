package io.sapl.test.unit.runner;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class TestAspect {

    @Around("execution( * io.sapl.test.unit.runner.SetupTestDependencies.setupEmbeddedPDP(..) )")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

    	System.out.println("TEST");

        Object result = pjp.proceed();

        return result;
    }
    
}

