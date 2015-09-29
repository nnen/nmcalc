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
        
        EQUALS,
        
        PLUS,
        MINUS,
        ASTERISK,
        SLASH,
        
        LPAR,
        RPAR,
        LBRA,
        RBRA,
        COMMA,
        
        KW_DEF,
        
        EOF,
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
}
