package io.sapl.grammar.sapl.impl;


import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.Response;
import io.sapl.interpreter.EvaluationContext;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Slf4j
public class PolicyImplCustom extends PolicyImpl {

    private static final String OBLIGATIONS_ERROR = "Error occurred while evaluating obligations.";
    private static final String ADVICE_ERROR = "Error occurred while evaluating advice.";
    private static final String TRANSFORMATION_ERROR = "Error occurred while evaluating transformation.";

    private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

    private static final String PERMIT = "permit";


    /**
     * Evaluates the body of the policy within the given evaluation context and
     * returns a {@link Flux} of {@link Response} objects.
     *
     * @param ctx the evaluation context in which the policy's body is evaluated.
     *            It must contain
     *            <ul>
     *            <li>the attribute context</li>
     *            <li>the function context</li>
     *            <li>the variable context holding the four request variables 'subject',
     *                'action', 'resource' and 'environment' combined with system variables
     *                from the PDP configuration and other variables e.g. obtained from the
     *                containing policy set</li>
     *            <li>the import mapping for functions and attribute finders</li>
     *            </ul>
     * @return A {@link Flux} of {@link Response} objects.
     */
    @Override
    public Flux<Response> evaluate(EvaluationContext ctx) {
        final Decision entitlement = PERMIT.equals(getEntitlement()) ? Decision.PERMIT : Decision.DENY;
        final Flux<Decision> decisionFlux =
                getBody() != null
                        ? getBody().evaluate(entitlement, ctx)
                        : Flux.just(entitlement);

        return decisionFlux.flatMap(decision -> {
            if (decision == Decision.PERMIT || decision == Decision.DENY) {
                return evaluateObligationsAndAdvice(ctx)
                        .map(obligationsAndAdvice -> {
                            final Optional<ArrayNode> obligations = obligationsAndAdvice
                                    .getT1();
                            final Optional<ArrayNode> advice = obligationsAndAdvice
                                    .getT2();
                            return new Response(decision, Optional.empty(), obligations,
                                    advice);
                        });
            }
            else {
                return Flux.just(new Response(decision, Optional.empty(),
                        Optional.empty(), Optional.empty()));
            }
        }).flatMap(response -> {
            final Decision decision = response.getDecision();
            if (decision == Decision.PERMIT) {
                return evaluateTransformation(ctx)
                        .map(resource -> new Response(decision, resource,
                                response.getObligations(), response.getAdvices()));
            }
            else {
                return Flux.just(response);
            }
        }).onErrorReturn(INDETERMINATE);
    }

    private Flux<Tuple2<Optional<ArrayNode>, Optional<ArrayNode>>> evaluateObligationsAndAdvice(
            EvaluationContext evaluationCtx) {
        Flux<Optional<ArrayNode>> obligationsFlux;
        if (getObligation() != null) {
            final ArrayNode obligationArr = JSON.arrayNode();
            obligationsFlux = getObligation().evaluate(evaluationCtx, true, Optional.empty())
                    .doOnError(error -> LOGGER.error(OBLIGATIONS_ERROR, error))
                    .map(obligation -> {
                        obligation.ifPresent(obligationArr::add);
                        return obligationArr.size() > 0 ? Optional.of(obligationArr)
                                : Optional.empty();
                    });
        }
        else {
            obligationsFlux = Flux.just(Optional.empty());
        }

        Flux<Optional<ArrayNode>> adviceFlux;
        if (getAdvice() != null) {
            final ArrayNode adviceArr = JSON.arrayNode();
            adviceFlux = getAdvice().evaluate(evaluationCtx, true, Optional.empty())
                    .doOnError(error -> LOGGER.error(ADVICE_ERROR, error)).map(advice -> {
                        advice.ifPresent(adviceArr::add);
                        return adviceArr.size() > 0 ? Optional.of(adviceArr)
                                : Optional.empty();
                    });
        }
        else {
            adviceFlux = Flux.just(Optional.empty());
        }

        return Flux.combineLatest(obligationsFlux, adviceFlux, Tuples::of);
    }

    private Flux<Optional<JsonNode>> evaluateTransformation(EvaluationContext evaluationCtx) {
        if (getTransformation() != null) {
            return getTransformation().evaluate(evaluationCtx, true, Optional.empty())
                    .doOnError(error -> LOGGER.error(TRANSFORMATION_ERROR, error));
        }
        else {
            return Flux.just(Optional.empty());
        }
    }
}