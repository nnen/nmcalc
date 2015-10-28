/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.peg;

import com.numericalmethod.suanshu.misc.StringUtils;
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
    private String fileName;
    
    private boolean isCompiled = false;
    private Pattern compiledPattern;
    
    
    private ITokenFunction<TTokenType, TToken> defaultTransform = new ITokenFunction<TTokenType, TToken>() {
        @Override
        public TToken make(TTokenType type, int offset, int group, Matcher m) {
            return makeToken(type, offset, m.group(group));
        }  
    };

    public ITokenFunction<TTokenType, TToken> getDefaultTransform() {
        return defaultTransform;
    }

    public void setDefaultTransform(ITokenFunction<TTokenType, TToken> defaultTransform) {
        this.defaultTransform = defaultTransform;
    }
    
    
    protected void addPattern(TTokenType tokenType, String pattern, int groupCount, ITokenFunction<TTokenType, TToken> transform)
    {
        patterns.add(new TokenPattern(tokenType, pattern, groupCount, transform));
    }
    
    protected void addPattern(TTokenType tokenType, String pattern, Function<String, TTokenType> tokenTypeFn)
    {
        addPattern(tokenType, pattern, 0, (tt, o, g, m) -> getDefaultTransform().make(tokenTypeFn.apply(m.group(g)), o, g, m));
    }
    
    protected void addPattern(TTokenType tokenType, String pattern, int groupCount)
    {
        addPattern(tokenType, pattern, groupCount, defaultTransform);
    }
    
    protected void addPattern(TTokenType tokenType, String pattern)
    {
        addPattern(tokenType, pattern, 0);
    }
    
    protected void addLiteral(TTokenType tokenType, String literal)
    {
        addPattern(tokenType, Pattern.quote(literal));
    }
    
    protected void setIgnore(String pattern) {
        ignorePattern = Pattern.compile(pattern);
    }
    
    public void compile()
    {
        StringBuilder sb = new StringBuilder();
        
        int groupIndex = 1;
        
        for (TokenPattern p : patterns) {
            if (groupIndex > 1) {
                sb.append("|");
            }
            sb.append("(");
            sb.append(p.getPattern());
            sb.append(")");
            
            p.setGroupIndex(groupIndex);
            groupIndex += 1 + p.getGroupCount();
        }
        
        compiledPattern = Pattern.compile(sb.toString());
        
        isCompiled = true;
        
        dump();
    }
    
    public void dump()
    {
        System.err.println("Patterns:");
        for (int i = 0; i < patterns.size(); i++) {
            TokenPattern p = patterns.get(i);
            System.err.println(String.format("   [%d] %d - %s", i, p.getGroupIndex(), p.getPattern()));
        }
        System.err.println(String.format("Compiled pattern: %s", compiledPattern.pattern()));
    }
    
    
    public void reset(CharSequence input, String fileName)
    {
        this.input = input;
        this.offset = 0;
        this.fileName = fileName;
        
        compile();
    }
    
    protected TToken internalNext() {
        Matcher m = ignorePattern.matcher(input).region(offset, input.length());
        if (m.lookingAt()) {
            offset += m.group().length();
        }
        
        if (offset >= input.length()) {
            return makeEOFToken(offset);
        }
        
        m = compiledPattern.matcher(input).region(offset, input.length());
        if (!m.lookingAt()) {
            offset += 1;
            return makeUnknownToken(offset - 1, input.charAt(offset - 1));
        }
        
        for (TokenPattern pattern : patterns) {
            String group = m.group(pattern.getGroupIndex());
            
            if (StringUtils.isNullOrEmpty(group)) {
                continue;
            }
            
            TToken token = pattern.getTransform().make(
                    pattern.getTokenType(),
                    offset,
                    pattern.getGroupIndex(),
                    m);
            offset += group.length();
            return token;
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
        
        
        private final String pattern;

        public String getPattern() {
            return pattern;
        }
        
        
        private int groupCount = 0;

        public int getGroupCount() {
            return groupCount;
        }
        
        
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
        
        
        public TokenPattern(TTokenType tokenType, String pattern, int groupCount, ITokenFunction<TTokenType, TToken> transform) {
            this.tokenType = tokenType;
            this.pattern = pattern;
            this.groupCount = groupCount;
            this.transform = transform;
        }
        
        /*
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
        */
    
    }
    
    
    @FunctionalInterface
    public static interface ITokenFunction<TTokenType, TToken> {
        public TToken make(TTokenType type, int offset, int group, Matcher m);
    }
    
}
