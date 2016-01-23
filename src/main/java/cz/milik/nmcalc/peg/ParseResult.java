/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.peg;

import cz.milik.nmcalc.parser.Token;
import cz.milik.nmcalc.utils.LinkedList;
import cz.milik.nmcalc.utils.Pair;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author jan
 * @param <T> type of the resulting value
 */
public class ParseResult<T> {
    
    private final boolean success;
    
    public boolean isSuccess() { return success; }
        
    
    private final boolean error;
    
    public boolean isError() { return error; }
    
    
    private final boolean ignored;
    
    public boolean isIgnored() {
        return ignored;
    }
    
    
    private final boolean cut;
    
    public boolean isCut() {
        return cut;
    }
    
    
    private final T value;
    
    public T getValue() { return value; }
    
    
    private final ITokenSequence rest;
    
    public ITokenSequence getRest() { return rest; }
    
    
    private final Token errorToken;
    
    public Token getErrorToken() { return errorToken; }
    
    
    private final String errorMessage;
    
    public String getErrorMessage() { 
        if ((errorMessage == null) && (this.getCause() != null)) {
            StackTraceElement top = getCause().getStackTrace()[0];
            return String.format(
                    "[%d:%s:%d] %s",
                    getCause().getStackTrace().length,
                    top.getFileName(),
                    top.getLineNumber(),
                    getCause().getMessage());
        }
        return errorMessage; 
    }
    
    
    private final Exception cause;
    
    public Exception getCause() { return cause; }
    
    
    private final IPegContext context;
    
    public IPegContext getContext() { return context; }
    
    
    public ParseResult(IPegContext aContext, T aValue, ITokenSequence aRest)
    {
        success = true;
        error = false;
        ignored = false;
        cut = false;
        value = aValue;
        rest = aRest;
        errorMessage = "Ok.";
        errorToken = aRest.get(0);
        cause = null;
        context = aContext;
    }
    
    public ParseResult(IPegContext aContext, ITokenSequence aRest)
    {
        success = true;
        error = false;
        ignored = true;
        cut = false;
        value = null;
        rest = aRest;
        errorMessage = "Ok.";
        errorToken = aRest.get(0);
        cause = null;
        context = aContext;
    }
    
    public ParseResult(IPegContext ctx, boolean anError, boolean aCut, ITokenSequence aRest, String anErrorMessage, Token anErrorToken)
    {
        success = false;
        error = anError;
        value = null;
        ignored = false;
        cut = aCut;
        rest = aRest;
        errorToken = anErrorToken;
        if (anErrorMessage == null) {
            errorMessage = String.format(
                    "Unexpected error at token %s.",
                    anErrorToken);
        } else {
            errorMessage = anErrorMessage;
        }
        cause = null;
        context = ctx;
    }
    
    public ParseResult(IPegContext ctx, boolean anError, boolean aCut, ITokenSequence aRest, Exception anException, Token anErrorToken)
    {
        success = false;
        error = anError;
        ignored = false;
        cut = aCut;
        value = null;
        rest = aRest;
        errorToken = anErrorToken;
        errorMessage = null;
        cause = anException;
        context = ctx;
    }
    
    public ParseResult(IPegContext ctx, boolean anError, boolean aCut, ITokenSequence aRest, Exception anException, String anErrorMessage, Token anErrorToken)
    {
        success = false;
        error = anError;
        ignored = false;
        cut = aCut;
        value = null;
        rest = aRest;
        errorToken = anErrorToken;
        errorMessage = anErrorMessage;
        cause = anException;
        context = ctx;
    }
    
    public ParseResult(boolean success,
            boolean error,
            boolean ignored,
            boolean cut,
            T value,
            ITokenSequence rest,
            Token errorToken,
            String errorMessage,
            Exception exception,
            IPegContext ctx) {
        this.success = success;
        this.error = error;
        this.ignored = ignored;
        this.cut = cut;
        
        this.value = value;
        this.rest = rest;
        this.errorToken = errorToken;
        this.errorMessage = errorMessage;
        this.cause = exception;
        this.context = ctx;
    }
    
    
    @Override
    public String toString() {
        if (!isSuccess()) {
            return String.format("ParseResult{isError=%s, errorMessage=%s}",
                    Boolean.toString(isError()),
                    getErrorMessage()
            );
        }
        return "ParseResult{" + "value=" + value + '}';
    }
    
    public String format() {
        StringBuilder sb = new StringBuilder();
        
        if (isSuccess()) {
            sb.append("Success: ");
            sb.append(Objects.toString(value));
            sb.toString();
        } else if (isError()) {
            sb.append("Error ");
        } else { 
            sb.append("Failure ");
        }
        if (getErrorToken() != null) {
            sb.append("at ");
            sb.append(getErrorToken().toString());
        }
        sb.append("\n");
        
        if (getCause() != null) {
            sb.append("Cause: ");
            sb.append(getCause().getMessage());
            sb.append("\n");
            
            StackTraceElement[] stack = getCause().getStackTrace();
            for (int i = 0; i < stack.length; i++) {
                StackTraceElement e = stack[i];
                sb.append(String.format(
                        "[%d] [%s:%d] %s\n",
                        i,
                        e.getFileName(),
                        e.getLineNumber(),
                        e.getMethodName()
                ));
            }
        }
        
        sb.append("Context: ");
        getContext().printStack(sb);
        
        return sb.toString();
    }
    
    
    public <U> ParseResult<U> map(Function<T, U> fn) {
        if (!isSuccess()) {
            return castFailure();
        }
        return mapSuccess(fn);
    }
    
    public <U> ParseResult<U> mapSuccess(Function<T, U> fn) {
        return new ParseResult(
                getContext(),
                fn.apply(getValue()),
                getRest()
        );
    }
    
    public <U> ParseResult<U> ignore() {
        if (!isSuccess()) {
            return castFailure();
        }
        return new ParseResult<>(
                getContext(),
                getRest()
        );
    }
    
    public <U> ParseResult<U> castFailure() {
        return castFailure(getRest());
    }
    
    public <U> ParseResult<U> castFailure(ITokenSequence rest, boolean cut) {
        return new ParseResult<>(
                getContext(),
                isError(),
                cut,
                rest,
                getCause(),
                getErrorMessage(),
                getErrorToken()
        );
    }
    
    public <U> ParseResult<U> castFailure(ITokenSequence rest) {
        return castFailure(rest, isCut());
    }
    
    public <U> ParseResult<U> makeError() {
        return new ParseResult<>(
                getContext(),
                true,
                isCut(),
                getRest(),
                getCause(),
                getErrorMessage(),
                getErrorToken()
        );
    }
    
    
    public ParseResult<T> toNonCut() {
        return new ParseResult<>(
                success,
                error,
                ignored,
                false,
                value,
                rest,
                errorToken,
                errorMessage,
                cause,
                context
        );
    }
    
    public ParseResult<T> toCut() {
        return new ParseResult<>(
                success,
                error,
                ignored,
                true,
                value,
                rest,
                errorToken,
                errorMessage,
                cause,
                context
        );
    }
    
    
    public <U> ParseResult<U> bind(PegParser<U> parser) throws PegException {
        if (!isSuccess()) {
            return castFailure();
        }
        return parser.parse(this);
    }
    
    public <U> ParseResult<U> bind(PegParser<U> parser, IPegContext ctx) throws PegException
    {
        if (!isSuccess()) {
            return castFailure();
        }
        return parser.parse(getRest(), ctx);
    }
    
    public ParseResult<T> or(PegParser<T> other) throws PegException {
        if (isError()) {
            return this;
        }
        if (!isSuccess()) {
            return bind(other);
        }
        return this;
    }
    
    public <U> ParseResult<Pair<T, U>> and(PegParser<U> other) throws PegException {
        if (!isSuccess()) {
            return castFailure();
        }
        return bind(other).map(otherVal -> Pair.of(value, otherVal));
    }
    
    public ParseResult<LinkedList<T>> prepend(PegParser<LinkedList<T>> other) throws PegException {
        if (!isSuccess()) {
            return castFailure();
        }
        return bind(other).map(list -> {
            return list.prepend(value);
        });
    }
    
    public ParseResult<T> or(ParseResult<T> other)
    {
        if (isError()) {
            return this;
        }
        if (!isSuccess()) {
            return other;
        }
        return this;
    }
    
    public ParseResult<T> or(Supplier<T> supplier) {
        if (isError()) {
            return this;
        }
        if (!isSuccess()) {
            return new ParseResult(getContext(), supplier.get(), getRest());
        }
        return this;
    }
    
    public <U, V> ParseResult<V> and(ParseResult<U> other, BiFunction<T, U, V> fn)
    {
        if (isError()) {
            return castFailure();
        }
        if (other.isError()) {
            return other.castFailure();
        }
        return new ParseResult(other.getContext(), fn.apply(getValue(), other.getValue()), other.getRest());
    }

    
    public static <U> ParseResult<LinkedList<U>> flatten(ParseResult<LinkedList<LinkedList<U>>> nested) {
        return nested.map(v -> {
            return nested.getValue().reduceRight((inner, rest) -> {
                return inner.reduceRight((item, rest2) -> {
                    return rest2.prepend(item);
                }, rest);
            }, LinkedList.empty());
        });
    }
}
