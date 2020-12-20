package io.sapl.test.unit.runner;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import org.junit.runners.model.TestClass;
import io.sapl.api.interpreter.InitializationException;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint;
import io.sapl.pdp.embedded.PolicyDecisionPointFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class SetupTestDependencies {


    private static String defaultPath = "~/sapl/policies";
    
	/**
	 * Setup up an embedded PDP for evaluating policies.
	 * @param TestClass Test Class
	 * @return EmbeddedPDP to use in helper methods of this class
	 * @throws Exception
	 */
	//TODO Custom SetupSaplTestRunnerException for related exceptions
	static EmbeddedPolicyDecisionPoint setupEmbeddedPDP(TestClass testClass) throws InitializationException {
		return PolicyDecisionPointFactory.filesystemUnitTestPolicyDecisionPoint(
					readPathFromAnnotation(testClass), 
					readPIPs(testClass), 
					readFunctions(testClass),
					readPolicyIdFromAnnotation(testClass));
	}
	
	private static String readPathFromAnnotation(TestClass testClass) {
		PolicyPath annotationPolicyPath = testClass.getAnnotation(PolicyPath.class);
		if(annotationPolicyPath == null) {
			// log.info(PolicyPath.class.getName()  + " is missing on Class " + testClass.getName() + " using default path: " + defaultPath);
			return defaultPath;
		} else {
			return annotationPolicyPath.value();			
		}
	}

	//TODO Custom SetupSaplTestRunnerException for related exceptions
	private static String readPolicyIdFromAnnotation(TestClass testClass) throws InitializationException  {
		PolicyId annotationPolicyId = testClass.getAnnotation(PolicyId.class);
		if(annotationPolicyId == null) {
			throw new InitializationException("Annotation " + PolicyId.class.getName() + " is missing on Class " + testClass.getName());
		} else {
			return annotationPolicyId.value();			
		}
	}

	//TODO Custom SetupSaplTestRunnerException for related exceptions
	private static List<Object> readPIPs(TestClass testClass) throws InitializationException {
		PolicyPIP policyPIP = testClass.getAnnotation(PolicyPIP.class);
		List<Object> pips = new LinkedList<>();
		if(policyPIP != null) {
			for(Class<?> pip : policyPIP.value()) {
				if(pip.getConstructors().length == 1 && pip.getConstructors()[0].getParameterCount() == 0) {
					try {
						pips.add(pip.getConstructors()[0].newInstance());
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | SecurityException e) {
						throw new InitializationException("Exception initializing Policy Function " + pip.getName() + " referenced in annotation " + PolicyPIP.class.getName() + "on Class " + testClass.getName(), e);
					}
				} else {
					throw new InitializationException("Expecting only a ZeroArgsConstructor for Policy Information Point " + pip.getName() + " referenced in annotation " + PolicyPIP.class.getName() + "on Class " + testClass.getName());
				}
			}
		}
		return pips;
	}
	
	//TODO Custom SetupSaplTestRunnerException for related exceptions
	private static List<Object> readFunctions(TestClass testClass) throws InitializationException {
		PolicyFunction policyFunctions = testClass.getAnnotation(PolicyFunction.class);
		List<Object> functions = new LinkedList<>();
		if(policyFunctions != null) {
			for(Class<?> function : policyFunctions.value()) {
				if(function.getConstructors().length == 1 && function.getConstructors()[0].getParameterCount() == 0) {
					try {
						functions.add(function.getConstructors()[0].newInstance());
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | SecurityException e) {
						throw new InitializationException("Exception initializing Policy Function " + function.getName() + " referenced in annotation " + PolicyFunction.class.getName() + "on Class " + testClass.getName(), e);
					}
				} else {
					throw new InitializationException("Expecting only a ZeroArgsConstructor for Policy Function " + function.getName() + " referenced in annotation " + PolicyFunction.class.getName() + "on Class " + testClass.getName());
				}
			}
		}
		return functions;
	}
	
}
