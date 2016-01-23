/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.parser;

import java.util.function.Function;

/**
 *
 * @author jan
 */
public class Token {
    
    public enum Types
    {
        UNKNOWN(null, "unknown token"),
        
        FLOAT(null, "float literal"),
        HEX_LITERAL(null, "hex literal"),
        OCT_LITERAL(null, "octal literal"),
        BIN_LITERAL(null, "binary literal"),
        IDENTIFIER(null, "identifier"),
        SYMBOL(null, "symbol literal"),
        STRING(null, "string literal"),
        BYTES(null, "byte array literal"),
        BUILTIN(null, "builtin literal"),
        
        EQUALS,
        EQUALS_COMP(null, "== operator"),
        LT_COMP(null, "< operator"),
        GT_COMP(null, "> operator"),
        
        CONS(null, ":: operator"),
        ARROW(null, "-> operator"),
        
        PLUS('+'),
        MINUS('-'),
        ASTERISK('*'),
        DOUBLE_ASTERISK,
        SLASH('/'),
        LSHIFT(null, "<< operator"),
        RSHIFT(null, ">> operator"),
        
        LPAR('('),
        RPAR(')'),
        LBRA('['),
        RBRA(']'),
        LBRACE('{'),
        RBRACE('}'),
        QUOTE('\''),
        DOT('.'),
        COMMA(','),
        COLON(':'),
        SEMICOLON(';'),
        
        KW_DEF("def"),
        KW_IF("if"),
        KW_THEN("then"),
        KW_ELSE("else"),
        KW_MATCH("match"),
        KW_CASE("case"),
        KW_TRUE("true"),
        KW_FALSE("false"),
        KW_NOTHING("nothing"),
        
        EOF(null, "end of input");
        
        public final boolean isKeyword;
        public final String keyword;
        public final boolean isSingleCharacter;
        public final char singleCharacter;
        
        private final String description;
        private final Function<Token, String> descriptionFn;
        
        private Types(char c) {
            this.keyword = null;
            this.isKeyword = false;
            isSingleCharacter = true;
            singleCharacter = c;
            description = "'" + Character.toString(c) + "'";
            descriptionFn = null;
        }
        
        private Types(String keyword, String description) {
            this.keyword = keyword;
            this.isKeyword = (keyword != null);
            isSingleCharacter = false;
            singleCharacter = ' ';
            if (description != null) {
                this.description = description;
            } else if (keyword != null) {
                this.description = "keyword '" + keyword + "'";
            } else {
                this.description = null;
            }
            descriptionFn = null;
        }
        
        private Types(String keyword) {
            this(keyword, null);
        }
        
        private Types() {
            isKeyword = false;
            keyword = null;
            isSingleCharacter = false;
            singleCharacter = ' ';
            description = null;
            descriptionFn = null;
        }
        
        public String getDescription() {
            if (description == null) {
                return name();
            }
            return description;
        }
        
        public String getDescription(Token token) {
            if (descriptionFn != null) {
                return descriptionFn.apply(token);
            }
            return getDescription();
        }
        
    }
    
    private Types type;
    private int offset;
    private String value;
    
    public Types getType() { return type; }
    public int getOffset() { return offset; }
    public String getValue() { return value; }
    
    public Token(Types type, int offset, String value)
    {
        this.type = type;
        this.offset = offset;
        this.value = value;
    }
    
    public Token(Types type, int offset, char value)
    {
        this.type = type;
        this.offset = offset;
        this.value = Character.toString(value);
    }
    
    @Override
    public String toString() {
        return "\"" + value + "\" at offset " + Integer.toString(offset);
        //return "Token{" + "type=" + type + ", offset=" + offset + ", value=\"" + value + "\"}";
    }
    
    public String getDescription() {
        return getType().getDescription(this);
    }
    
    public String parseStringLiteral() {
        StringBuilder sb = new StringBuilder();
        boolean escaped = false;
        
        for (int i = 0; i < value.length() - 2; i++) {
            char c = value.charAt(1 + i);
            if (escaped) {
                switch (c) {
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    default:
                        sb.append(c);
                        break;
                }
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else {
                sb.append(c);
            }
        }
        
        return sb.toString();
    }
    
}
