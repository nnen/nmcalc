/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.parser;

/**
 *
 * @author jan
 */
public class Token {
    
    public enum Types
    {
        UNKNOWN,
        
        FLOAT,
        IDENTIFIER,
        SYMBOL,
        STRING,
        
        EQUALS,
        
        PLUS,
        MINUS,
        ASTERISK,
        SLASH,
        
        LPAR,
        RPAR,
        LBRA,
        RBRA,
        QUOTE,
        COMMA,
        
        KW_DEF(true),
        KW_IF(true),
        KW_THEN(true),
        KW_ELSE(true),
        
        EOF;
        
        public final boolean isKeyword;

        private Types(boolean isKeyword) {
            this.isKeyword = isKeyword;
        }
        
        private Types() {
            isKeyword = false;
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
        return "Token{" + "type=" + type + ", offset=" + offset + ", value=" + value + '}';
    }

    public String parseStringLiteral() {
        StringBuilder sb = new StringBuilder();
        boolean escaped = false;
        
        for (int i = 0; i < value.length() - 2; i++) {
            char c = value.charAt(1 + i);
            if (escaped) {
                sb.append(c);
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
