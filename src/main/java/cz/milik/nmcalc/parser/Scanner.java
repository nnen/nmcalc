/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.parser;

import cz.milik.nmcalc.FloatValue;
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
    //private final StringBuilder value = new StringBuilder();
    
    private final Queue<Token> tokenBuffer = new ArrayDeque<>();
    
    
    public Scanner() {
        singleCharacterTokens.put('<', Token.Types.LT_COMP);
        singleCharacterTokens.put('>', Token.Types.GT_COMP);
        singleCharacterTokens.put('\'', Token.Types.QUOTE);
        
        for (Token.Types tt : Token.Types.values()) {
            if (tt.isKeyword) {
                keywords.put(tt.keyword, tt);
            }
            if (tt.isSingleCharacter) {
                singleCharacterTokens.put(tt.singleCharacter, tt);
            }
        }
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
            case '[':
            case ']':
                return false;
        }
        return true;
    }
    
    private boolean isInnerDigit(char c) {
        return Character.isDigit(c) || (c == FloatValue.GROUPING_SEPARATOR);
    }
    
    private boolean isHexChar(char c) {
        if (Character.isDigit(c)) {
            return true;
        }
        switch (c) {
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case '_':
                return true;
        }
        return false;
    }
    
    private boolean isOctChar(char c) {
        return Character.digit(c, 8) >= 0;
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
        if (peek() == '0') {
            value.append(next());
            
            if (peek() == 'x') {
                value.append(next());
                if (isHexChar(peek())) {
                    while (hasNext() && isHexChar(peek())) {
                        value.append(next());
                    }
                    return new Token(
                            Token.Types.HEX_LITERAL,
                            tokenOffset,
                            value.toString()
                    );
                } else {
                    return finishUnknownToken();
                }
            } else if (peek() == decimalPoint) {
                value.append(next());
                while (hasNext() && isInnerDigit(peek()))
                {
                    value.append(next());
                }
                return new Token(Token.Types.FLOAT, tokenOffset, value.toString());
            } else {
                while (hasNext() && isOctChar(peek())) {
                    value.append(next());
                }
                if (peek() == 'b') {
                    if (value.toString().matches("[01]+")) {
                        value.append(next());
                        return new Token(
                                Token.Types.BIN_LITERAL,
                                tokenOffset,
                                value.toString());
                    } else {
                        value.append(next());
                        return new Token(
                                Token.Types.UNKNOWN,
                                tokenOffset,
                                value.toString());
                    }
                }
                if (value.length() == 1) {
                    return new Token(
                        Token.Types.FLOAT,
                        tokenOffset,
                        value.toString()
                    );
                }
                return new Token(
                        Token.Types.OCT_LITERAL,
                        tokenOffset,
                        value.toString()
                );
            }
        }
        if (Character.isDigit(peek()))
        {
            while (hasNext() && isInnerDigit(peek()))
            {
                value.append(next());
            }
            if (peek() == 'b') {
                if (value.toString().matches("[01_]+")) {
                    value.append(next());
                    return new Token(
                            Token.Types.BIN_LITERAL,
                            tokenOffset,
                            value.toString());
                } else {
                    value.append(next());
                    return new Token(
                            Token.Types.UNKNOWN,
                            tokenOffset,
                            value.toString());
                }
            }
            if (hasNext() && (peek() == decimalPoint))
            {
                value.append(next());
            }
            while (hasNext() && isInnerDigit(peek()))
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
                value.append(next());
                if (peek() == '>') {
                    value.append(next());
                    return new Token(Token.Types.ARROW, tokenOffset, value.toString());
                }
                return new Token(Token.Types.MINUS, tokenOffset, value.toString());
            case '*':
                value.append(next());
                if (peek() == '*') {
                    value.append(next());
                    return new Token(Token.Types.DOUBLE_ASTERISK, tokenOffset, value.toString());
                } else {
                    return new Token(Token.Types.ASTERISK, tokenOffset, value.toString());
                }
            case '/':
                return new Token(Token.Types.SLASH, tokenOffset, next());
            
            case ':':
            {
                value.append(next());
                if (peek() != ':') {
                    return new Token(Token.Types.COLON, tokenOffset, value.toString());
                }
                value.append(next());
                return new Token(Token.Types.CONS, tokenOffset, value.toString());
            }
            
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
