/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jan
 * @param <TTokenType>
 * @param <TToken>
 */
public abstract class RegExScanner<TTokenType, TToken> {
    
    private final List<TokenPattern> patterns = new ArrayList<>();
    private CharSequence input;
    private int offset;
    
    protected void addPattern(TTokenType tokenType, Pattern pattern)
    {
        patterns.add(new TokenPattern(tokenType, pattern));
    }
    
    protected void addPattern(TTokenType tokenType, String pattern)
    {
        addPattern(tokenType, Pattern.compile(pattern));
    }
    
    protected void addLiteral(TTokenType tokenType, String literal)
    {
        addPattern(tokenType, Pattern.quote(literal));
    }
    
    public void reset(CharSequence input)
    {
        this.input = input;
        this.offset = 0;
        
        for (TokenPattern pattern : patterns) {
            pattern.reset(input);
        }
    }
    
    protected TToken internalNext()
    {
        for (TokenPattern pattern : patterns) {
            Matcher m = pattern.getMatcher();
            m.region(offset, input.length());
        }
        
        return null;
    }
    
    public TToken next()
    {
        return null;
    }
    
    protected abstract TToken makeToken(TTokenType tokenType, int offset, String value);
    
    private class TokenPattern {
        private final TTokenType tokenType;
        
        public TTokenType getTokenType() { return tokenType; }
        
        private final Pattern pattern;
        
        public Pattern getPattern() { return pattern; }
        
        private Matcher matcher;
        
        public Matcher getMatcher() {
            return matcher;
        }
        
        public void reset(CharSequence input) {
            matcher = getPattern().matcher(input);
        }
        
        public TokenPattern(TTokenType tokenType, Pattern pattern) {
            this.tokenType = tokenType;
            this.pattern = pattern;
        }
    }
    
}
