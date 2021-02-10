package io.sapl.test;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.Decision;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.grammar.sapl.SAPL;
import io.sapl.interpreter.EvaluationContext;
import reactor.core.CorePublisher;

/**
 * Implementing a Step Builder Pattern to construct test cases.
 *
 */
class StepBuilder {

	/**
	 * Create Builder starting at the Given-Step. Only for internal usage.
	 * @param document containing the {@link SAPL} policy to evaluate
	 * @return {@link GivenStep} to start constructing the test case.
	 */
	static GivenStep newBuilderAtGivenStep(SAPL document) {
        return new Steps(document);
	}
	
	/**
	 * Create Builder starting at the When-Step. Only for internal usage.
	 * @param document containing the {@link SAPL} policy to evaluate
	 * @return {@link WhenStep} to start constructing the test case.
	 */
	static WhenStep newBuilderAtWhenStep(SAPL document) {
        return new Steps(document);
	}
	
	//disable default constructor
    StepBuilder() {}
    
     /**
     * First Step in charge of registering mock values, ... . 
     * Next Step available : {@link WhenStep} or again {@link GivenStep} -> therefore returning composite {@link GivenOrWhenStep}
     */
    public static interface GivenStep {
    	
    		/**
    		 * Mock the return value of a PIP or Function in the SAPL policy
    		 * @param importName the reference in the SAPL policy to the PIP or Function
    		 * @param publisher the mocked return value
	         * @return {@link GivenOrWhenStep} to define another {@link GivenStep} or {@link WhenStep}
    		 */
            GivenOrWhenStep given(String importName, CorePublisher<?> publisher);
    		/**
    		 * Mock the return value of a PIP or Function in the SAPL policy
    		 * @param obj Object of the PIP or Function
    		 * @param publisher the mocked return value
	         * @return {@link GivenOrWhenStep} to define another {@link GivenStep} or {@link WhenStep}
    		 */
            GivenOrWhenStep given(Object obj, CorePublisher<?> publisher);
    		/**
    		 * Mock the return value of a PIP or Function in the SAPL policy
    		 * @param clazz Class reference of the PIP or Function
    		 * @param publisher the mocked return value
	         * @return {@link GivenOrWhenStep} to define another {@link GivenStep} or {@link WhenStep}
    		 */
            GivenOrWhenStep given(Class<?> clazz, CorePublisher<?> publisher);
            
            /**
             * Allows control of virtual time for time-based streams
	         * @return {@link GivenOrWhenStep} to define another {@link GivenStep} or {@link WhenStep}
             */
            GivenOrWhenStep withVirtualTime();
    }
    
    /**
    * When Step in charge of setting the {@link AuthorizationSubscription} for the test case. 
    * Next Step available : {@link ExpectStep}
    */
   public static interface WhenStep {
	   /**
	    * Sets the {@link AuthorizationSubscription} for the test case.
	    * @param authSubscription the {@link AuthorizationSubscription}
	    * @return next available Step {@link ExpectStep}
	    */
       ExpectStep when(AuthorizationSubscription  authSubscription);
	   /**
	    * Sets the {@link AuthorizationSubscription} for the test case.
	    * @param authSubscription {@link String} containing JSON defining a {@link AuthorizationSubscription}
	    * @return next available Step {@link ExpectStep}
	    */
       ExpectStep when(String jsonAuthSub) throws SaplTextException;	   
       /**
	    * Sets the {@link AuthorizationSubscription} for the test case.
	    * @param authSubscription {@link ObjectNode} defining a {@link AuthorizationSubscription}
	    * @return next available Step {@link ExpectStep}
	    */
       ExpectStep when(ObjectNode jsonNode) throws SaplTextException;
   }
    
    /**
     * Composite Step to allow repeating of {@link WhenStep} or go over to a {@link GivenStep}
     */
    public static interface GivenOrWhenStep extends GivenStep, WhenStep {
    }
    
    /**
     * This step is in charge of defining the expected results. 
     * Next Step available : {@link VerifyStep} or again an {@link ExpectStep} -> therefore returning composite {@link ExpectOrVerifyStep}
     */
    public static interface ExpectStep {
    	
    		//Sync expect methods
    	    	
	    	/**
	         * Allow custom validation of {@link AuthorizationDecision}
	         * @param An {@link AuthorizationDecision} object which has to be equal to the first emitted {@link AuthorizationDecision}
             * @return {@link VerifyStep} to verify your test case.
	         */
            VerifyStep expect(AuthorizationDecision authDec);
            /**
             * Allow custom validation of {@link AuthorizationDecision}
             * @param func Lambda-Expression to validate the first emitted {@link AuthorizationDecision}
             * @return {@link VerifyStep} to verify your test case.
             */
            VerifyStep expect(Consumer<AuthorizationDecision> func);
            /**
             * Allow custom validation of {@link AuthorizationDecision}
             * @param pred {@link Predicate<AuthorizationDecision>} to validate the first emitted {@link AuthorizationDecision}
             * @return {@link VerifyStep} to verify your test case.
             */
            VerifyStep expect(Predicate<AuthorizationDecision> pred);
            /**
             * Asserts that the first emitted {@link AuthorizationDecision} of the policy evaluation is a {@link Decision#PERMIT}
             * @return {@link VerifyStep} to verify your test case.
             */
            VerifyStep expectPermit();
            /**
             * Asserts that the first emitted {@link AuthorizationDecision} of the policy evaluation is a {@link Decision#DENY}
             * @return {@link VerifyStep} to verify your test case.
             */
            VerifyStep expectDeny();
            /**
             * Asserts that the first emitted {@link AuthorizationDecision} of the policy evaluation is a {@link Decision#INDETERMINATE}
             * @return {@link VerifyStep} to verify your test case.
             */
            VerifyStep expectIndeterminate();
            /**
             * Asserts that the first emitted {@link AuthorizationDecision} of the policy evaluation is a {@link Decision#NOT_APPLICABLE}
             * @return {@link VerifyStep} to verify your test case.
             */
            VerifyStep expectNotApplicable();
    	
            
    		//Async expect methods
            
	    	/**
	         * Allow custom validation of {@link AuthorizationDecision}
	         * @param authDec An {@link AuthorizationDecision} object which has to be equal to the current emitted {@link AuthorizationDecision}
	         * @return {@link ExpectOrVerifyStep} to define another {@link ExpectStep} or {@link VerifyStep}
	         */
            ExpectOrVerifyStep expectNext(AuthorizationDecision authDec);
            /**
             * Allow custom validation of {@link AuthorizationDecision}
             * @param func Lambda-Expression to validate the current emitted {@link AuthorizationDecision}
             * @return {@link ExpectOrVerifyStep} to define another {@link ExpectStep} or {@link VerifyStep}
             */
            ExpectOrVerifyStep expectNext(Consumer<AuthorizationDecision> func);
            /**
             * Asserts that the current emitted {@link AuthorizationDecision} of the policy evaluation is a {@link Decision#PERMIT}
             * @return {@link ExpectOrVerifyStep} to define another {@link ExpectStep} or {@link VerifyStep}
             */
            ExpectOrVerifyStep expectNextPermit();
            /**
             * Asserts that the next @param emitted values of {@link AuthorizationDecision} of the policy evaluation is a {@link Decision#PERMIT}
             * @return {@link ExpectOrVerifyStep} to define another {@link ExpectStep} or {@link VerifyStep}
             */
            ExpectOrVerifyStep expectNextPermit(Integer count);
            /**
             * Asserts that the current emitted {@link AuthorizationDecision} of the policy evaluation is a {@link Decision#DENY}
             * @return {@link ExpectOrVerifyStep} to define another {@link ExpectStep} or {@link VerifyStep}
             */
            ExpectOrVerifyStep expectNextDeny();
            /**
             * Asserts that the next @param emitted values of {@link AuthorizationDecision} of the policy evaluation is a {@link Decision#DENY}
             * @return {@link ExpectOrVerifyStep} to define another {@link ExpectStep} or {@link VerifyStep}
             */
            ExpectOrVerifyStep expectNextDeny(Integer count);
            /**
             * Asserts that the current emitted {@link AuthorizationDecision} of the policy evaluation is a {@link Decision#INDETERMINATE}
             * @return {@link ExpectOrVerifyStep} to define another {@link ExpectStep} or {@link VerifyStep}
             */
            ExpectOrVerifyStep expectNextIndeterminate();
            /**
             * Asserts that the next @param emitted values of {@link AuthorizationDecision} of the policy evaluation is a {@link Decision#INDETERMINATE}
             * @return {@link ExpectOrVerifyStep} to define another {@link ExpectStep} or {@link VerifyStep}
             */
            ExpectOrVerifyStep expectNextIndeterminate(Integer count);
            /**
             * Asserts that the first emitted {@link AuthorizationDecision} of the policy evaluation is a {@link Decision#NOT_APPLICABLE}
             * @return {@link ExpectOrVerifyStep} to define another {@link ExpectStep} or {@link VerifyStep}
             */
            ExpectOrVerifyStep expectNextNotApplicable();            
            /**
             * Asserts that the next @param emitted values of {@link AuthorizationDecision} of the policy evaluation is a {@link Decision#NOT_APPLICABLE}
             * @return {@link ExpectOrVerifyStep} to define another {@link ExpectStep} or {@link VerifyStep}
             */
            ExpectOrVerifyStep expectNextNotApplicable(Integer count);
            
            /**
             * Pauses the evaluation of steps
             * @param duration Pause for this {#link Duration}
             * @return {@link ExpectOrVerifyStep} to define another {@link ExpectStep} or {@link VerifyStep}
             */
            ExpectOrVerifyStep thenAwait(Duration duration);
            
            /**
             * Lets the stream play out for a given {#link Duration} but fails the test if any signal occurs during that time
             * @param duration Wait for this {#link Duration}
             * @return {@link ExpectOrVerifyStep} to define another {@link ExpectStep} or {@link VerifyStep}
             */
            ExpectOrVerifyStep expectNoEvent(Duration duration);
    }
    
    
    /**
     * This is the final step in charge of executing the test case and verifying the results.
     */
    public static interface VerifyStep {
    	/**
    	 * Executes the test case and verifies expectations.
    	 */
        void verify();
    }
    
    /**
     * Composite Step to allow repeating of {@link ExpectStep} or go over to a {@link VerifyStep}
     */
    public static interface ExpectOrVerifyStep extends ExpectStep, VerifyStep {
    }
    
    
    /**
     * Implementing all step interfaces. Always returning this to enable Builder-Pattern but as a step interface
     */
    private static class Steps implements GivenStep, WhenStep, GivenOrWhenStep, ExpectStep, ExpectOrVerifyStep {

    	AuthorizationSubscription authSub;
    	AuthorizationDecision authDec;
    	SAPL document;
    	EvaluationContext ctx;
    	
    	Steps(SAPL document) {
    		this.document = document;
    		
    	}

		

		@Override
		public GivenOrWhenStep given(String importName, CorePublisher<?> publisher) {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public GivenOrWhenStep given(Object obj, CorePublisher<?> publisher) {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public GivenOrWhenStep given(Class<?> clazz, CorePublisher<?> publisher) {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public GivenOrWhenStep withVirtualTime() {
			// TODO Auto-generated method stub
			
			
			//https://projectreactor.io/docs/test/release/api/reactor/test/scheduler/VirtualTimeScheduler.html#getOrSet--
			//hierüber sollte globaler VirtualTimeSchedular Registrations möglich sein siehe 
			//https://dzone.com/articles/testing-time-based-reactor-core-streams-with-virtual-time
			
			
			
			return null;
		}

    	
    	
		
		@Override
		public ExpectStep when(AuthorizationSubscription authSub) {
			this.authSub = authSub;
			return this;
		}

		@Override
		public ExpectStep when(String jsonAuthSub) throws SaplTextException {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode authSubJsonNode;
			try {
				authSubJsonNode = objectMapper.readTree(jsonAuthSub);
			} catch (JsonProcessingException e) {
				throw new SaplTextException("Could not read Json Tree!", e);
			}
			if(authSubJsonNode != null) {
				AuthorizationSubscription authSub = new AuthorizationSubscription(
						authSubJsonNode.findValue("subject"), 
						authSubJsonNode.findValue("action"), 
						authSubJsonNode.findValue("resource"), 
						authSubJsonNode.findValue("environment")
						);
				this.authSub = authSub;
				return this;
			}
			throw new SaplTextException("Could not read Json Tree!");
		}

		@Override
		public ExpectStep when(ObjectNode jsonNode) throws SaplTextException {
			if(jsonNode != null) {
				AuthorizationSubscription authSub = new AuthorizationSubscription(
					jsonNode.findValue("subject"), 
					jsonNode.findValue("action"), 
					jsonNode.findValue("resource"), 
					jsonNode.findValue("environment")
					);
				this.authSub = authSub;
				return this;
			}
			throw new SaplTextException("Could not read AuthorizationSubscription");
		}

		@Override
		public ExpectOrVerifyStep expect(AuthorizationDecision authDec) {
			this.authDec = authDec;
			return this;
		}

		@Override
		public ExpectOrVerifyStep expectPermit() {
			this.authDec = AuthorizationDecision.PERMIT;
			return this;
		}

		@Override
		public ExpectOrVerifyStep expectDeny() {
			this.authDec = AuthorizationDecision.DENY;
			return this;
		}

		@Override
		public VerifyStep expect(Consumer<AuthorizationDecision> func) {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public VerifyStep expect(Predicate<AuthorizationDecision> pred) {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public VerifyStep expectIndeterminate() {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public VerifyStep expectNotApplicable() {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public ExpectOrVerifyStep expectNext(AuthorizationDecision authDec) {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public ExpectOrVerifyStep expectNext(Consumer<AuthorizationDecision> func) {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public ExpectOrVerifyStep expectNextPermit() {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public ExpectOrVerifyStep expectNextPermit(Integer count) {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public ExpectOrVerifyStep expectNextDeny() {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public ExpectOrVerifyStep expectNextDeny(Integer count) {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public ExpectOrVerifyStep expectNextIndeterminate() {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public ExpectOrVerifyStep expectNextIndeterminate(Integer count) {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public ExpectOrVerifyStep expectNextNotApplicable() {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public ExpectOrVerifyStep expectNextNotApplicable(Integer count) {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public ExpectOrVerifyStep thenAwait(Duration duration) {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public ExpectOrVerifyStep expectNoEvent(Duration duration) {
			// TODO Auto-generated method stub
			return null;
		}

		
		
		@Override
		public void verify() {
			this.document.evaluate(this.ctx);
			// TODO Auto-generated method stub
			
		}
    }
}
