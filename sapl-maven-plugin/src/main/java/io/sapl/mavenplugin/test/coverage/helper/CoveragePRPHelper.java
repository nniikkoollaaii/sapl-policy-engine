package io.sapl.mavenplugin.test.coverage.helper;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.sapl.grammar.sapl.Condition;
import io.sapl.grammar.sapl.Policy;
import io.sapl.grammar.sapl.PolicyElement;
import io.sapl.grammar.sapl.PolicySet;
import io.sapl.grammar.sapl.SAPL;
import io.sapl.grammar.sapl.Statement;
import io.sapl.interpreter.DefaultSAPLInterpreter;
import io.sapl.mavenplugin.test.coverage.model.CoverageHitSummary;
import io.sapl.prp.filesystem.FileSystemPrpUpdateEventSource;
import io.sapl.test.coverage.api.model.*;

public class CoveragePRPHelper {


	public static CoverageHitSummary getCoverageTargets(String policyPath) throws Exception {
		//Initialize
		Collection<SAPL> documents = retrievePolicyDocuments(policyPath);
		List<PolicySetHit> availablePolicySetHitTargets = new LinkedList<>();
		List<PolicyHit> availablePolicyHitTargets = new LinkedList<>();
		List<PolicyConditionHit> availablePolicyConditionHitTargets = new LinkedList<>();
		
		//Evaluate
		for (SAPL sapl : documents) {
			PolicyElement element = sapl.getPolicyElement();
			
			if(element instanceof PolicySet) {
				addPolicySetToResult(
						(PolicySet) element, 
						availablePolicySetHitTargets, 
						availablePolicyHitTargets, 
						availablePolicyConditionHitTargets
					);
			} else if (element instanceof Policy) {
				addPolicyToResult(
						(Policy) element, 
						"", 
						availablePolicyHitTargets, 
						availablePolicyConditionHitTargets
					);
			} else {
				throw new Exception("Error: Unknown Subtype of " + PolicyElement.class);
			}
		}
				
		return new CoverageHitSummary(List.copyOf(availablePolicySetHitTargets), List.copyOf(availablePolicyHitTargets), List.copyOf(availablePolicyConditionHitTargets));
	}
	
	/*private static Collection<? extends AuthorizationDecisionEvaluable> retrievePolicyDocuments(String policyPath) {
		var seedIndex = new NaiveImmutableParsedDocumentIndex();
		var source = new FileSystemPrpUpdateEventSource(policyPath, new DefaultSAPLInterpreter());
		PolicyRetrievalPoint prp = new GenericInMemoryIndexedPolicyRetrievalPoint(seedIndex, source);
		
		AttributeContext attributeCtx = new AnnotationAttributeContext();
		AnnotationFunctionContext functionCtx = new AnnotationFunctionContext();
		Map<String, JsonNode> environmentVariables = new HashMap<String, JsonNode>();
		
		return prp.retrievePolicies(new EvaluationContext(attributeCtx, functionCtx, environmentVariables)).blockFirst().getMatchingDocuments();
	}*/
	
	
	private static Collection<SAPL> retrievePolicyDocuments(String policyPath) {
		var eventSource = new FileSystemPrpUpdateEventSource(policyPath, new DefaultSAPLInterpreter());
		return Arrays.stream(eventSource.getUpdates().blockFirst().getUpdates()).map(update -> update.getDocument()).collect(Collectors.toList());
	}
	
	private static void addPolicySetToResult(PolicySet policySet, List<PolicySetHit> availablePolicySetHitTargets, List<PolicyHit> availablePolicyHitTargets, List<PolicyConditionHit> availablePolicyConditionHitTargets) throws Exception {
		availablePolicySetHitTargets.add(new PolicySetHit(policySet.getSaplName()));
		for(Policy policy : policySet.getPolicies()) {
			addPolicyToResult(
					policy, 
					policySet.getSaplName(), 
					availablePolicyHitTargets, 
					availablePolicyConditionHitTargets);
		}
	}
	
	private static void addPolicyToResult(Policy policy, String policySetId, List<PolicyHit> availablePolicyHitTargets, List<PolicyConditionHit> availablePolicyConditionHitTargets) throws Exception {
		availablePolicyHitTargets.add(new PolicyHit(policySetId, policy.getSaplName()));
		if(policy.getBody() == null)
			return;
		for(int i = 0; i < policy.getBody().getStatements().size(); i++) {
			addPolicyConditionToResult(
					policy.getBody().getStatements().get(i), 
					i, 
					policySetId, 
					policy.getSaplName(), 
					availablePolicyConditionHitTargets
				);
		}
	}
	
	private static void addPolicyConditionToResult(Statement statement, int position, String policySetId, String policyId, List<PolicyConditionHit> availablePolicyConditionHitTargets) throws Exception {
		if (statement instanceof Condition) {
			availablePolicyConditionHitTargets.add(new PolicyConditionHit(policySetId, policyId, position, true));
			availablePolicyConditionHitTargets.add(new PolicyConditionHit(policySetId, policyId, position, false));
		} else {
			throw new Exception("Error: Unknown Subtype of " + Statement.class);
		}
	}
	
	
}
