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
        HEX_LITERAL,
        OCT_LITERAL,
        IDENTIFIER,
        SYMBOL,
        STRING,
        
        EQUALS,
        EQUALS_COMP,
        LT_COMP,
        GT_COMP,
        
        CONS,
        ARROW,
        
        PLUS('+'),
        MINUS('-'),
        ASTERISK('*'),
        SLASH('/'),
        
        LPAR('('),
        RPAR(')'),
        LBRA('['),
        RBRA(']'),
        LBRACE('{'),
        RBRACE('}'),
        QUOTE('\''),
        COMMA(','),
        
        KW_DEF("def"),
        KW_IF("if"),
        KW_THEN("then"),
        KW_ELSE("else"),
        KW_MATCH("match"),
        KW_CASE("case"),
        KW_TRUE("true"),
        KW_FALSE("false"),
        KW_NOTHING("nothing"),
        
        EOF;
        
        public final boolean isKeyword;
        public final String keyword;
        public final boolean isSingleCharacter;
        public final char singleCharacter;
        
        private Types(char c) {
            this.keyword = null;
            this.isKeyword = false;
            isSingleCharacter = true;
            singleCharacter = c;
        }
        
        private Types(String keyword) {
            this.keyword = keyword;
            this.isKeyword = (keyword != null);
            isSingleCharacter = false;
            singleCharacter = ' ';
        }
        
        private Types() {
            isKeyword = false;
            keyword = null;
            isSingleCharacter = false;
            singleCharacter = ' ';
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
