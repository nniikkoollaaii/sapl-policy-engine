package io.sapl.testrunner.junit;


import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;


/**
 * 
 * @author Nikolai Seip
 * Extending {@link BlockJUnit4ClassRunner} AND OVERRIDING validateZeroArgConstructor() to allow injecting dependency in test class via constructor injection
 * Official support for Dependency Injection to JUnit is coming with version JUnit 5
 *
 */
public class SaplTestrunnerJUnit extends BlockJUnit4ClassRunner   {
	
	private SaplUnitTestRunnerHelper helper;
    
	public SaplTestrunnerJUnit(Class<?> testClass) throws InitializationError {
		super(testClass);
		try {
			this.helper = new SaplUnitTestRunnerHelper(SetupTestDependencies.setupEmbeddedPDP(getTestClass()));
		} catch (Exception e) {
			throw new InitializationError(e);
		}
	}
	
    @Override
    public Object createTest() throws Exception {	
	    return getTestClass().getOnlyConstructor().newInstance(helper);
    }
    
    @Override
    protected void validateZeroArgConstructor(List<Throwable> errors) {
    	if (!getTestClass().isANonStaticInnerClass()
	    		&& (getTestClass().getOnlyConstructor().getParameterTypes().length == 1)
	    		&& (getTestClass().getOnlyConstructor().getParameterTypes()[0].equals(SaplUnitTestRunnerHelper.class))) {
	    	//Do nothing
	    } else {
	        String gripe = "Test class should have exactly one public constructor with parameter of type " + SaplUnitTestRunnerHelper.class.getName();
	        errors.add(new Exception(gripe));
	    }
	}
	  
}

/*
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint;

public class SaplTestrunnerJUnit extends Runner  {

	EmbeddedPolicyDecisionPoint pdp;
    private Class<?> testClass;

    
    public SaplTestrunnerJUnit(Class<?> testClass) {
        super();
        this.testClass = testClass;
    }
    
	@Override
	public Description getDescription() {
        return Description.createTestDescription(testClass, "SAPL Policy Unit Test Runner");
	}

	@Override
	public void run(RunNotifier notifier){
		
		try {
			//Instantiate injecting dependency
			PolicyId annotationPolicyId = testClass.getAnnotation(PolicyId.class);
			if(annotationPolicyId == null) {
				throw new Exception("Annotation " + PolicyId.class.getName() + " is missing on Class " + testClass.getName());
			}
			SaplUnitTestRunnerHelper helper = new SaplUnitTestRunnerHelper(annotationPolicyId.value());
			
			//Prepare instance of test class
			Constructor<?> ctor = testClass.getDeclaredConstructor(SaplUnitTestRunnerHelper.class);	
		    ctor.setAccessible(true);
		    Object testObject = ctor.newInstance(helper);
		    
		    
		    //look out for @Test annotated methods and execute them
            for (Method method : testClass.getMethods()) {
                if (method.isAnnotationPresent(Test.class)) {
                    notifier.fireTestStarted(Description
                      .createTestDescription(testClass, method.getName()));
                    
                    try {
                    	method.invoke(testObject);                    	
                    } catch (InvocationTargetException x) {
            	 	    x.printStackTrace();
            	 	    //?
            	 	    //notifier.fireTestAssumptionFailed(null);
                	}
                    
                    
                    
                    notifier.fireTestFinished(Description
                      .createTestDescription(testClass, method.getName()));
                }
            }
            
            helper.disposeResources();
		} catch (InstantiationException x) {
		    x.printStackTrace();
	 	}  catch (IllegalAccessException x) {
		    x.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
*/
