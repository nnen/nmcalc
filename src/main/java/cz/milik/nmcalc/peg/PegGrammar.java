/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.peg;

import cz.milik.nmcalc.parser.Token;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jan
 */
public class PegGrammar<T> {

    private final Map<String, PegParser<T>> nonterminals = new HashMap<>();
    private final List<NonterminalProxy> proxies = new ArrayList<>();
    
    private static final PegParser<Token> TOKEN_PARSER = new PegParser.TokenParser();
    
    public PegGrammar() {
        initializeGrammar();
        resolve();
    }
    
    public PegParser<T> getSumbol(String name) {
        return nonterminals.get(name);
    }
    
    protected void initializeGrammar() {
        // Override in subclass.
    }
    
    protected PegParser<Token> s(final Token.Types first, final Token.Types... rest) {
        return token(first, rest);
    }
    
    protected PegParser<Token> s(final Token.Types first, String name) {
        return token(first).named(name);
    }
    
    protected PegParser<T> s(String name) {
        return nt(name);
    }
    
    protected PegParser<T> s(String name, String ctxName) {
        return s(name).named(ctxName);
    }
    
    protected PegParser<Token> keyword(String value) {
        return s(Token.Types.IDENTIFIER).test(
                t -> t.getValue().equals(value),
                "Expected keyword \"" + value + "\".");
    }
    
    protected PegParser<Token> token() { return TOKEN_PARSER; }
    
    protected PegParser<Token> token(final Token.Types first, final Token.Types... rest) {
        if (rest.length == 0) {
            return new PegParser.TokenTypeParser(first, TOKEN_PARSER);
        }
        return new PegParser.TokenTypesParser(TOKEN_PARSER, first, rest);
        /*
        if (rest.length == 0) {
            //return new PegParser.TokenTypeParser(first, TOKEN_PARSER);
            return TOKEN_PARSER.test(
                t -> {
                    return t.getType() == first;
                },
                String.format("Expect token %s.", first.name())
            );
        }
        final EnumSet<Token.Types> typeSet = EnumSet.of(first, rest);
        return TOKEN_PARSER.test(
                t -> typeSet.contains(t.getType()),
                String.format("Expect one of %s.", StringUtils.join(", ", typeSet.stream().map(tt -> tt.name())))
        );
        */
    }
    
    protected <U> PegParser<U> or(PegParser<U>... parsers) {
        return PegParser.or(parsers);
    }
    
    protected PegParser<T> nt(String name, PegParser<T> parser) {
        nonterminals.put(name, parser);
        return parser;
    }
    
    protected PegParser<T> nt(String name) {
        NonterminalProxy proxy = new NonterminalProxy(name);
        proxies.add(proxy);
        return proxy;
    }

    protected void resolve() {
        proxies.forEach(proxy -> proxy.resolve());
    }

    
    protected class NonterminalProxy extends PegParser<T> {

        protected final String name;

        protected PegParser<T> nonterminal;

        public NonterminalProxy(String name) {
            this.name = name;
        }
        
        @Override
        public String getName() {
            return name;
        }

        @Override
        protected String getDefaultShortDesc() {
            return "<" + getName() + ">";
        }
        
        /*
        @Override
        public ParseResult<T> parse(ITokenSequence input, IPegContext ctx) {
            if (nonterminal == null) {
                throw new IllegalStateException();
            }
            return nonterminal.parse(input, ctx);
        }
        */
        
        @Override
        public ParseResult<T> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException {
            if (nonterminal == null) {
                throw new IllegalStateException();
            }
            return nonterminal.parse(input, ctx);
            //return null; // Do nothing.
        }

        public void resolve() {
            PegParser<T> actual = nonterminals.get(name);
            if (actual == null) {
                throw new IllegalStateException(
                        String.format("There is no terminal '%s'.", name));
            }
            this.nonterminal = actual;
        }

    }

}
