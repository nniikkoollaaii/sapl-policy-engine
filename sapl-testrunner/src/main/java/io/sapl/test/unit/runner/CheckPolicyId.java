package io.sapl.test.unit.runner;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import io.sapl.api.interpreter.Val;
import io.sapl.grammar.sapl.Policy;
import io.sapl.grammar.sapl.PolicyElement;
import io.sapl.grammar.sapl.PolicySet;
import io.sapl.grammar.sapl.impl.PolicyElementImplCustom;
import reactor.core.publisher.Mono;

@Aspect
public class CheckPolicyId {

	private final String policyId = "policy 1";
 
    @Around("execution( Mono<Val> io.sapl.grammar.sapl.impl.PolicyElementImplCustom.matches(..) )")
    public Mono<Val> around(ProceedingJoinPoint pjp) throws Throwable {

    	System.out.println("CheckPolicyId");

    	@SuppressWarnings("unchecked")
		Mono<Val> result = (Mono<Val>) pjp.proceed();
    	
        PolicyElementImplCustom policy = (PolicyElementImplCustom) pjp.getThis();
        if (policy instanceof Policy) {
        	if(!policy.getSaplName().equals(policyId)) {
        		return Mono.just(Val.FALSE);
        	}
        } else if (policy instanceof PolicySet) {
        	return result;
        } else {
        	return result;
        }

        return result;
    }
    
}

