/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.peg;

import cz.milik.nmcalc.parser.Token;
import cz.milik.nmcalc.utils.IThrowsFunction;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author jan
 */
public interface IPegContext {
    
    public boolean isDebugModeOn();

    public IPegContext getParent();
    
    public PegScope getScope();
    
    public void startNewScope();
    
    public void endScope();
    
    public PegParser<?> getParser();
    
    public ParseResult<?> getResult();
    
    public void setResult(ParseResult<?> value);
    
    public int getDepth();
    
    public int getDepthLimit();
    
    public IPegContext createChild(PegParser<?> parser) throws PegException;
    
    public void setNamedValue(String name, Class<?> aClass, Object value);
    
    public <T, U> U withNamedValue(String name, Class<T> aClass, Function<T, U> withFn, Supplier<U> elseFn);
    
    public <T> List<T> getNamedValues(String name, Class<T> aClass);
    
    public <T> T getNamedValue(String name, Class<T> aClass);
    
    public Token getNamedToken(String name);
    
    public <R> R executeWithChild(final PegParser<?> parser, final IThrowsFunction<IPegContext, R, PegException> body) throws PegException;
    
    public void printStack(Appendable output);
    
    
    public abstract static class AbstractPegContext implements IPegContext {
        
        private boolean debug = false;
        
        @Override
        public boolean isDebugModeOn() { return debug; }
        
        
        private final IPegContext parent;
        
        @Override
        public IPegContext getParent() { return parent; }
        
        
        private PegScope scope;
        
        @Override
        public PegScope getScope() {
            if (scope == null) {
                if (getParent() != null) {
                    scope = getParent().getScope();
                } else {
                    scope = new PegScope(null);
                }
            }
            return scope;
        }
        
        public void setScope(PegScope scope) {
            this.scope = scope;
        }
        
        @Override
        public void startNewScope() {
            PegScope newScope = new PegScope(getScope());
            scope = newScope;
        }
        
        @Override
        public void endScope() {
            PegScope oldScope = getScope();
            scope = oldScope.getParent();
            scope.update(oldScope);
        }
        
        private final PegParser<?> parser;
        
        @Override
        public PegParser<?> getParser() { return parser; }
        
        
        private ParseResult<?> result;
        
        @Override
        public ParseResult<?> getResult() { return result; }
        
        @Override
        public void setResult(ParseResult<?> value) { result = value; }
        
        
        private final int depth;

        @Override
        public int getDepth() {
            return depth;
        }
        
        @Override
        public int getDepthLimit() {
            return 1000;
        }
        
        public AbstractPegContext(IPegContext aParent, PegParser<?> aParser, int depth) {
            this.parent = aParent;
            this.parser = aParser;
            this.depth = depth;
        }
        
        @Override
        public IPegContext createChild(PegParser<?> parser) throws PegException {
            int newDepth = getDepth() + 1;
            if (newDepth > getDepthLimit()) {
                throw new PegException("Stack depth limit exceeded.");
            }
            return new PegContext(this, parser, newDepth);
        }
        
        @Override
        public <T, U> U withNamedValue(String name, Class<T> aClass, Function<T, U> withFn, Supplier<U> elseFn) {
            T value = getNamedValue(name, aClass);
            if (value == null) {
                if (elseFn == null) {
                    return null;
                }
                return elseFn.get();
            } else {
                if (withFn == null) {
                    return null;
                }
                return withFn.apply(value);
            }
        }
        
        @Override
        public <T> List<T> getNamedValues(String aName, Class<T> aClass) {
            return getScope().getAll(aName, aClass);
        }
        
        @Override
        public <T> T getNamedValue(String name, Class<T> aClass) {
            return getScope().get(name, aClass).unwrap();
        }

        @Override
        public Token getNamedToken(String name) {
            return getNamedValue(name, Token.class);
        }
        
        @Override
        public void setNamedValue(String name, Class<?> aClass, Object value) {
            getScope().add(name, value);
        }
        
        @Override
        public <R> R executeWithChild(PegParser<?> parser,
                IThrowsFunction<IPegContext, R, PegException> body) throws PegException {
            return body.apply(createChild(parser));
        }
        
        @Override
        public void printStack(Appendable output) {
            IPegContext current = this;
            int index = 0;
            
            // output.append(String.format("[%d parser frames]", ));
            
            try {
                for (; current != null; current = current.getParent(), index++) {
                    String parserName = "<root>";
                    if (current.getParser() != null) {
                        parserName = current.getParser().getShortDescription();
                    }
                    output.append(String.format(
                            "[%d] %s ",
                            index,
                            parserName));
                    if (current.getResult() != null) {
                        ParseResult<?> r = current.getResult();
                        if (r.isSuccess()) {
                            output.append(String.format(
                                    "[success: %s] ",
                                    Objects.toString(r.getValue())
                            ));
                        } else if (r.isError()) {
                            output.append(String.format("[error: %s] ", r.getErrorMessage()));
                        } else {
                            output.append(String.format("[failure: %s] ", r.getErrorMessage()));
                        }
                    }
                    output.append("\n");
                }
            } catch (IOException e) {
            }
        }
        
    }
    
    
    public static class PegContext extends AbstractPegContext {    
        
        public PegContext(IPegContext aParent, PegParser<?> aParser, int depth) {
            super(aParent, aParser, depth);
        }
    
    }
    

}
