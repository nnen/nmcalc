/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.peg;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
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
    private Pattern ignorePattern = Pattern.compile("[ \\t\\n]*");
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
    
    protected void setIgnore(String pattern) {
        ignorePattern = Pattern.compile(pattern);
    }
    
    public void reset(CharSequence input)
    {
        this.input = input;
        this.offset = 0;
    }
    
    protected TToken internalNext() {
        Matcher m = ignorePattern.matcher(input).region(offset, input.length());
        if (m.lookingAt()) {
            offset += m.group().length();
        }
        
        if (offset >= input.length()) {
            return makeEOFToken(offset);
        }
        
        for (TokenPattern pattern : patterns) {
            TToken token = pattern.match(input, offset);
            if (token != null) {
                offset = pattern.getEnd();
                return token;
            }
        }
        
        offset += 1;
        return makeUnknownToken(offset - 1, input.charAt(offset - 1));
    }
    
    public TToken next() {
        return internalNext();
    }
    
    public List<TToken> readTokens() {
        List<TToken> result = new ArrayList();
        TToken token;
        
        while (true) {
            token = next();
            if (isEOFToken(token)) {
                break;
            }
            result.add(token);
        }
        
        return result;
    }
    
    protected abstract TToken makeToken(TTokenType tokenType, int offset, String value);
    
    protected abstract TToken makeUnknownToken(int offset, char value);
    
    protected abstract TToken makeEOFToken(int offset);
    
    protected abstract boolean isEOFToken(TToken token);
    
    private class TokenPattern {
        private final TTokenType tokenType;
        
        public TTokenType getTokenType() { return tokenType; }
        
        private final Pattern pattern;
        
        public Pattern getPattern() { return pattern; }
        
        private final ITokenFunction<TTokenType, TToken> transform;

        public ITokenFunction<TTokenType, TToken> getTransform() {
            return transform;
        }
        
        private int groupIndex;

        public int getGroupIndex() {
            return groupIndex;
        }

        public void setGroupIndex(int groupIndex) {
            this.groupIndex = groupIndex;
        }
        
        private int end;

        public int getEnd() {
            return end;
        }
        
        public TokenPattern(TTokenType tokenType, Pattern pattern) {
            this.tokenType = tokenType;
            this.pattern = pattern;
            this.transform = null;
        }
        
        public TToken match(CharSequence input, int offset) {
            Matcher m = pattern.matcher(input).region(offset, input.length());
            if (!m.lookingAt()) {
                return null;
            }
            String value = m.group();
            end = offset + value.length();
            if (transform != null) {
                return transform.make(getTokenType(), offset, getGroupIndex(), m);
            }
            return makeToken(getTokenType(), offset, value);
        }
    }
    
    
    @FunctionalInterface
    public static interface ITokenFunction<TTokenType, TToken> {
        public TToken make(TTokenType type, int offset, int group, Matcher m);
    }
    
}
