/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 *
 * @author jan
 */
public class Scanner {
    
    private char decimalPoint = '.';
    private final Map<Character, Token.Types> singleCharacterTokens = new HashMap();
    private final Map<String, Token.Types> keywords = new HashMap();
    
    private IInput input;
    private int offset;
    private int line;
    
    private int tokenOffset;
    private final StringBuilder value = new StringBuilder();
    
    private final Queue<Token> tokenBuffer = new ArrayDeque<>();
    
    
    public Scanner() {
        singleCharacterTokens.put('<', Token.Types.LT_COMP);
        singleCharacterTokens.put('>', Token.Types.GT_COMP);
        singleCharacterTokens.put('\'', Token.Types.QUOTE);
        
        keywords.put("def", Token.Types.KW_DEF);
        keywords.put("if", Token.Types.KW_IF);
        keywords.put("then", Token.Types.KW_THEN);
        keywords.put("else", Token.Types.KW_ELSE);
    }
    
    
    private char peek()
    {
        return input.peek();
    }
    
    private char next()
    {
        char c = input.next();
        offset++;
        if (c == '\n')
        {
            line++;
        }
        return c;
    }
    
    private boolean hasNext()
    {
        return input.hasNext();
    }
    
    private boolean isDelimiter(char c)
    {
        if (Character.isWhitespace(c))
        {
            return true;
        }
        
        switch (c)
        {
            case '=':
            case '(':
            case ')':
            case '[':
            case ']':
            case ',':
            case ';':
                return true;
        }
        
        return false;
    }
    
    private boolean isSymbolChar(char c) {
        if (Character.isWhitespace(c)) {
            return false;
        }
        switch (c) {
            case ',':
            case '(':
            case ')':
                return false;
        }
        return true;
    }
    
    private Token finishUnknownToken()
    {
        while (hasNext() && !isDelimiter(peek()))
        {
            value.append(next());
        }
        return new Token(
                Token.Types.UNKNOWN,
                tokenOffset,
                value.toString()
        );
    }
    
    public void reset(IInput input)
    {
        this.input = input;
        this.offset = 0;
        this.line = 1;
    }
    
    public void reset(Reader reader, String fileName)
    {
        reset(new ReaderInput(reader, fileName));
    }
    
    public void reset(String input) {
        reset(new StringReader(input), "<string>");
    }
    
    public Token nextToken()
    {
        if (!tokenBuffer.isEmpty())
        {
            return tokenBuffer.remove();
        }
        
        value.delete(0, value.length());
        
        while (hasNext() && Character.isWhitespace(peek()))
        {
            next();
        }
        
        if (!hasNext())
        {
            return new Token(Token.Types.EOF, tokenOffset, "");
        }
        
        tokenOffset = offset;
        
        // Symbols
        if (peek() == '$') {
            value.append(next());
            while (hasNext() && isSymbolChar(peek())) {
                value.append(next());
            }
            return new Token(
                    Token.Types.SYMBOL,
                    tokenOffset,
                    value.toString()
            );
        }
        
        // Identifiers
        if (Character.isJavaIdentifierStart(peek())) {
            value.append(next());
            while (hasNext() && Character.isJavaIdentifierPart(peek())) {
                value.append(next());
            }
            String identifier = value.toString();
            if (keywords.containsKey(identifier)) {
                return new Token(
                        keywords.get(identifier),
                        tokenOffset,
                        identifier
                );
            }
            return new Token(
                    Token.Types.IDENTIFIER,
                    tokenOffset,
                    identifier
            );
        }
        
        // Strings
        if (peek() == '"') {
            value.append(next());
            boolean escaped = false;
            while (hasNext() && (escaped || (peek() != '"'))) {
                escaped = false;
                if (peek() == '\\') {
                    escaped = true;
                }
                value.append(next());
            }
            if (hasNext() && peek() == '"') {
                value.append(next());
                return new Token(
                        Token.Types.STRING,
                        tokenOffset,
                        value.toString()
                );
            } else {
                return new Token(
                        Token.Types.UNKNOWN,
                        tokenOffset,
                        value.toString()
                );
            }
        }
        
        // Real numbers in forms d+, d+.d*
        if (Character.isDigit(peek()))
        {
            while (hasNext() && Character.isDigit(peek()))
            {
                value.append(next());
            }
            if (hasNext() && (peek() == decimalPoint))
            {
                value.append(next());
            }
            while (hasNext() && Character.isDigit(peek()))
            {
                value.append(next());
            }
            return new Token(Token.Types.FLOAT, tokenOffset, value.toString());
        }
        
        // Real numbers in form .d+
        if (peek() == decimalPoint)
        {
            value.append(next());
            if (!hasNext() || !Character.isDigit(peek()))
            {
                return finishUnknownToken();
            }
            while (hasNext() && Character.isDigit(peek()))
            {
                value.append(next());
            }
            return new Token(Token.Types.FLOAT, tokenOffset, value.toString());
        }
        
        switch (peek())
        {
            case '=':
            {
                value.append(next());
                if (peek() == '=') {
                    value.append(next());
                    return new Token(
                            Token.Types.EQUALS_COMP,
                            tokenOffset,
                            value.toString()
                    );
                }
                return new Token(
                        Token.Types.EQUALS,
                        tokenOffset,
                        value.toString()
                );
            }
                
            case '+':
                return new Token(Token.Types.PLUS, tokenOffset, next());
            case '-':
                return new Token(Token.Types.MINUS, tokenOffset, next());
            case '*':
                return new Token(Token.Types.ASTERISK, tokenOffset, next());
            case '/':
                return new Token(Token.Types.SLASH, tokenOffset, next());
            
            case '(':
                return new Token(Token.Types.LPAR, tokenOffset, next());
            case ')':
                return new Token(Token.Types.RPAR, tokenOffset, next());
            case '[':
                return new Token(Token.Types.LBRA, tokenOffset, next());
            case ']':
                return new Token(Token.Types.RBRA, tokenOffset, next());
            case ',':
                return new Token(Token.Types.COMMA, tokenOffset, next());
        }
        
        if (singleCharacterTokens.containsKey(peek())) {
            char c = next();
            return new Token(
                    singleCharacterTokens.get(c),
                    tokenOffset,
                    c
            );
        }
        
        return finishUnknownToken();
    }

    public List<Token> readTokens() {
        List<Token> result = new ArrayList();
        
        while (true) {
            Token token = nextToken();
            if (token.getType() == Token.Types.EOF) {
                return result;
            }
            result.add(token);
        }
    }
    
}
