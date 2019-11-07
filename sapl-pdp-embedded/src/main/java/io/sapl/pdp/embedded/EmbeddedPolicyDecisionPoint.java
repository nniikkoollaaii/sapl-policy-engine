package io.sapl.pdp.embedded;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.functions.FunctionException;
import io.sapl.api.interpreter.PolicyEvaluationException;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.PDPConfigurationException;
import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.api.pdp.multisubscription.IdentifiableAuthorizationSubscription;
import io.sapl.api.pdp.multisubscription.IdentifiableAuthorizationDecision;
import io.sapl.api.pdp.multisubscription.MultiAuthorizationDecision;
import io.sapl.api.pdp.multisubscription.MultiAuthorizationSubscription;
import io.sapl.api.pip.AttributeException;
import io.sapl.api.prp.ParsedDocumentIndex;
import io.sapl.api.prp.PolicyRetrievalPoint;
import io.sapl.functions.FilterFunctionLibrary;
import io.sapl.functions.SelectionFunctionLibrary;
import io.sapl.functions.StandardFunctionLibrary;
import io.sapl.functions.TemporalFunctionLibrary;
import io.sapl.grammar.sapl.SAPL;
import io.sapl.interpreter.combinators.DocumentsCombinator;
import io.sapl.interpreter.functions.AnnotationFunctionContext;
import io.sapl.interpreter.functions.FunctionContext;
import io.sapl.interpreter.pip.AnnotationAttributeContext;
import io.sapl.interpreter.pip.AttributeContext;
import io.sapl.pdp.embedded.config.PDPConfigurationProvider;
import io.sapl.pdp.embedded.config.filesystem.FilesystemPDPConfigurationProvider;
import io.sapl.pdp.embedded.config.resources.ResourcesPDPConfigurationProvider;
import io.sapl.pip.ClockPolicyInformationPoint;
import io.sapl.prp.filesystem.FilesystemPolicyRetrievalPoint;
import io.sapl.prp.inmemory.indexed.FastParsedDocumentIndex;
import io.sapl.prp.inmemory.simple.SimpleParsedDocumentIndex;
import io.sapl.prp.resources.ResourcesPolicyRetrievalPoint;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class EmbeddedPolicyDecisionPoint implements PolicyDecisionPoint {

	private final FunctionContext functionCtx = new AnnotationFunctionContext();

	private final AttributeContext attributeCtx = new AnnotationAttributeContext();

	private PDPConfigurationProvider configurationProvider;

	private PolicyRetrievalPoint prp;

	private EmbeddedPolicyDecisionPoint() {
		// use Builder to create new instances
	}

	@Override
	public Flux<AuthorizationDecision> decide(AuthorizationSubscription authzSubscription) {
		LOGGER.trace("|---------------------------");
		LOGGER.trace("|-- PDP AuthorizationSubscription: {}", authzSubscription);

		final Flux<Map<String, JsonNode>> variablesFlux = configurationProvider.getVariables();
		final Flux<DocumentsCombinator> combinatorFlux = configurationProvider.getDocumentsCombinator();

		return Flux.combineLatest(variablesFlux, combinatorFlux, (variables, combinator) -> prp
				.retrievePolicies(authzSubscription, functionCtx, variables).switchMap(result -> {
					final Collection<SAPL> matchingDocuments = result.getMatchingDocuments();
					final boolean errorsInTarget = result.isErrorsInTarget();
					LOGGER.trace("|-- Combine documents of authzSubscription: {}", authzSubscription);
					return (Flux<AuthorizationDecision>) combinator.combineMatchingDocuments(matchingDocuments,
							errorsInTarget, authzSubscription, attributeCtx, functionCtx, variables);
				})).flatMap(Function.identity()).distinctUntilChanged();
	}

	@Override
	public Flux<IdentifiableAuthorizationDecision> decide(MultiAuthorizationSubscription multiAuthzSubscription) {
		if (multiAuthzSubscription.hasAuthorizationSubscriptions()) {
			final List<Flux<IdentifiableAuthorizationDecision>> identifiableAuthzDecisionFluxes = createIdentifiableAuthzDecisionFluxes(
					multiAuthzSubscription, true);
			return Flux.merge(identifiableAuthzDecisionFluxes);
		}
		return Flux.just(IdentifiableAuthorizationDecision.INDETERMINATE);
	}

	@Override
	public Flux<MultiAuthorizationDecision> decideAll(MultiAuthorizationSubscription multiAuthzSubscription) {
		if (multiAuthzSubscription.hasAuthorizationSubscriptions()) {
			final List<Flux<IdentifiableAuthorizationDecision>> identifiableAuthzDecisionFluxes = createIdentifiableAuthzDecisionFluxes(
					multiAuthzSubscription, false);
			return Flux.combineLatest(identifiableAuthzDecisionFluxes, this::collectAuthorizationDecisions);
		}
		return Flux.just(MultiAuthorizationDecision.indeterminate());
	}

	private List<Flux<IdentifiableAuthorizationDecision>> createIdentifiableAuthzDecisionFluxes(
			Iterable<IdentifiableAuthorizationSubscription> multiDecision, boolean useSeparateSchedulers) {
		final Scheduler schedulerForMerge = useSeparateSchedulers ? Schedulers.newElastic("pdp") : null;
		final List<Flux<IdentifiableAuthorizationDecision>> identifiableAuthzDecisionFluxes = new ArrayList<>();
		for (IdentifiableAuthorizationSubscription identifiableAuthzSubscription : multiDecision) {
			final String subscriptionId = identifiableAuthzSubscription.getAuthorizationSubscriptionId();
			final AuthorizationSubscription authzSubscription = identifiableAuthzSubscription
					.getAuthorizationSubscription();
			final Flux<IdentifiableAuthorizationDecision> identifiableAuthzDecisionFlux = decide(authzSubscription)
					.map(authzDecision -> new IdentifiableAuthorizationDecision(subscriptionId, authzDecision));
			if (useSeparateSchedulers) {
				identifiableAuthzDecisionFluxes.add(identifiableAuthzDecisionFlux.subscribeOn(schedulerForMerge));
			}
			else {
				identifiableAuthzDecisionFluxes.add(identifiableAuthzDecisionFlux);
			}
		}
		return identifiableAuthzDecisionFluxes;
	}

	private MultiAuthorizationDecision collectAuthorizationDecisions(Object[] values) {
		final MultiAuthorizationDecision multiAuthzDecision = new MultiAuthorizationDecision();
		for (Object value : values) {
			IdentifiableAuthorizationDecision ir = (IdentifiableAuthorizationDecision) value;
			multiAuthzDecision.setAuthorizationDecisionForSubscriptionWithId(ir.getAuthorizationSubscriptionId(),
					ir.getAuthorizationDecision());
		}
		return multiAuthzDecision;
	}

	public static Builder builder() throws FunctionException, AttributeException {
		return new Builder();
	}

	public static class Builder {

		public enum IndexType {

			SIMPLE, FAST

		}

		private EmbeddedPolicyDecisionPoint pdp = new EmbeddedPolicyDecisionPoint();

		private Builder() throws FunctionException, AttributeException {
			pdp.functionCtx.loadLibrary(new FilterFunctionLibrary());
			pdp.functionCtx.loadLibrary(new SelectionFunctionLibrary());
			pdp.functionCtx.loadLibrary(new StandardFunctionLibrary());
			pdp.functionCtx.loadLibrary(new TemporalFunctionLibrary());

			pdp.attributeCtx.loadPolicyInformationPoint(new ClockPolicyInformationPoint());
		}

		public Builder withResourcePDPConfigurationProvider()
				throws PDPConfigurationException, IOException, URISyntaxException {
			pdp.configurationProvider = new ResourcesPDPConfigurationProvider();
			return this;
		}

		public Builder withResourcePDPConfigurationProvider(String resourcePath)
				throws PDPConfigurationException, IOException, URISyntaxException {
			return withResourcePDPConfigurationProvider(ResourcesPDPConfigurationProvider.class, resourcePath);
		}

		public Builder withResourcePDPConfigurationProvider(Class<?> clazz, String resourcePath)
				throws PDPConfigurationException, IOException, URISyntaxException {
			pdp.configurationProvider = new ResourcesPDPConfigurationProvider(clazz, resourcePath);
			return this;
		}

		public Builder withFilesystemPDPConfigurationProvider(String configFolder) {
			pdp.configurationProvider = new FilesystemPDPConfigurationProvider(configFolder);
			return this;
		}

		public Builder withFunctionLibrary(Object lib) throws FunctionException {
			pdp.functionCtx.loadLibrary(lib);
			return this;
		}

		public Builder withPolicyInformationPoint(Object pip) throws AttributeException {
			pdp.attributeCtx.loadPolicyInformationPoint(pip);
			return this;
		}

		public Builder withResourcePolicyRetrievalPoint()
				throws IOException, URISyntaxException, PolicyEvaluationException {
			pdp.prp = new ResourcesPolicyRetrievalPoint();
			return this;
		}

		public Builder withResourcePolicyRetrievalPoint(String resourcePath, IndexType indexType)
				throws IOException, URISyntaxException, PolicyEvaluationException {
			return withResourcePolicyRetrievalPoint(ResourcesPolicyRetrievalPoint.class, resourcePath, indexType);
		}

		public Builder withResourcePolicyRetrievalPoint(Class<?> clazz, String resourcePath, IndexType indexType)
				throws IOException, URISyntaxException, PolicyEvaluationException {
			final ParsedDocumentIndex index = getDocumentIndex(indexType);
			pdp.prp = new ResourcesPolicyRetrievalPoint(clazz, resourcePath, index);
			return this;
		}

		public Builder withFilesystemPolicyRetrievalPoint(String policiesFolder, IndexType indexType) {
			final ParsedDocumentIndex index = getDocumentIndex(indexType);
			pdp.prp = new FilesystemPolicyRetrievalPoint(policiesFolder, index);
			return this;
		}

		private ParsedDocumentIndex getDocumentIndex(IndexType indexType) {
			switch (indexType) {
			case SIMPLE:
				return new SimpleParsedDocumentIndex();
			case FAST:
				return new FastParsedDocumentIndex(pdp.functionCtx);
			}
			return new SimpleParsedDocumentIndex();
		}

		public EmbeddedPolicyDecisionPoint build()
				throws IOException, URISyntaxException, PolicyEvaluationException, PDPConfigurationException {
			if (pdp.prp == null) {
				withResourcePolicyRetrievalPoint();
			}
			if (pdp.configurationProvider == null) {
				withResourcePDPConfigurationProvider();
			}
			return pdp;
		}

	}

}
