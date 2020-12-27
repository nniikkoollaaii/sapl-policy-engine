package io.sapl.interpreter;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;

import io.sapl.api.interpreter.PolicyEvaluationException;
import io.sapl.grammar.sapl.SAPL;
import io.sapl.grammar.sapl.SaplPackage;
import io.sapl.grammar.sapl.impl.SaplFactoryImplCustomUnitTest;

public class UnitTestSAPLInterpreter extends DefaultSAPLInterpreter {
	
	private final String policyId;
	private final boolean shouldCollectCoverageHits;
	
	/**
	 * Constructor for {@link io.sapl.interpreter.UnitTestSAPLInterpreter}
	 * @param policyId PolicyId of Policy under Unit Test. Null if no specific policy is under test.
	 * @param shouldCollectCoverageHits boolean
	 */
	public UnitTestSAPLInterpreter(String policyId, boolean shouldCollectCoverageHits) {
		this.policyId = policyId;
		this.shouldCollectCoverageHits = shouldCollectCoverageHits;
	}
	
	@Override
	protected SAPL loadAsResource(InputStream policyInputStream) {
		final XtextResourceSet resourceSet = INJECTOR.getInstance(XtextResourceSet.class);
		//hier kann eigene SaplFactory eingeschleust werden
		resourceSet.getPackageRegistry().getEPackage(SaplPackage.eNS_URI)
			.setEFactoryInstance(new SaplFactoryImplCustomUnitTest(this.policyId, this.shouldCollectCoverageHits));
		final Resource resource = resourceSet.createResource(URI.createFileURI(DUMMY_RESOURCE_URI));

		try {
			resource.load(policyInputStream, resourceSet.getLoadOptions());
		} catch (IOException | WrappedException e) {
			throw new PolicyEvaluationException(e, PARSING_ERRORS, resource.getErrors());
		}

		if (!resource.getErrors().isEmpty()) {
			throw new PolicyEvaluationException(PARSING_ERRORS, resource.getErrors());
		}
		return (SAPL) resource.getContents().get(0);
	}
	
}
