package io.sapl.interpreter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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
	
	public UnitTestSAPLInterpreter(String policyId) {
		this.policyId = policyId;
	}
	
	@Override
	public SAPL parse(String saplDefinition) {
		return parse(new ByteArrayInputStream(saplDefinition.getBytes(StandardCharsets.UTF_8)));
	}

	@Override
	public SAPL parse(InputStream saplInputStream) {
		return loadAsResource(saplInputStream);
	}


	@Override
	protected SAPL loadAsResource(InputStream policyInputStream) {
		final XtextResourceSet resourceSet = INJECTOR.getInstance(XtextResourceSet.class);
		//hier kann eigene SaplFactory eingeschleust werden
		resourceSet.getPackageRegistry().getEPackage(SaplPackage.eNS_URI).setEFactoryInstance(new SaplFactoryImplCustomUnitTest(this.policyId));
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
