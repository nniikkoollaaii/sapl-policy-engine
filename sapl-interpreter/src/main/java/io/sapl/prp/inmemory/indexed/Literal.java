package io.sapl.prp.inmemory.indexed;

import java.util.Objects;

import com.google.common.base.Preconditions;

import io.sapl.api.interpreter.PolicyEvaluationException;
import io.sapl.interpreter.functions.FunctionContext;
import io.sapl.interpreter.variables.VariableContext;

public class Literal {

	private final Bool bool;

	private int hash;

	private boolean hasHashCode;

	private final boolean hasNegation;

	public Literal(final Bool bool) {
		this(bool, false);
	}

	public Literal(final Bool bool, boolean negation) {
		this.bool = Preconditions.checkNotNull(bool);
		hasNegation = negation;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Literal other = (Literal) obj;
		if (hashCode() != other.hashCode()) {
			return false;
		}
		if (!Objects.equals(hasNegation, other.hasNegation)) {
			return false;
		}
		return Objects.equals(bool, other.bool);
	}

	public boolean evaluate() {
		boolean result = bool.evaluate();
		if (hasNegation) {
			return !result;
		}
		return result;
	}

	public boolean evaluate(final FunctionContext functionCtx, final VariableContext variableCtx)
			throws PolicyEvaluationException {
		boolean result = bool.evaluate(functionCtx, variableCtx);
		if (hasNegation) {
			return !result;
		}
		return result;
	}

	public Bool getBool() {
		return bool;
	}

	@Override
	public int hashCode() {
		if (!hasHashCode) {
			int h = 5;
			h = 19 * h + Objects.hashCode(bool);
			h = 19 * h + Objects.hashCode(hasNegation);
			hash = h;
			hasHashCode = true;
		}
		return hash;
	}

	public boolean isImmutable() {
		return bool.isImmutable();
	}

	public boolean isNegated() {
		return hasNegation;
	}

	public Literal negate() {
		return new Literal(bool, !hasNegation);
	}

	public boolean sharesBool(final Literal other) {
		return bool.equals(other.bool);
	}

	public boolean sharesNegation(final Literal other) {
		return hasNegation == other.hasNegation;
	}

}
