/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.peg;

import cz.milik.nmcalc.parser.Token;

/**
 *
 * @author jan
 */
public class CalcScanner extends RegExScanner<Token.Types, Token> implements IScanner {

    public CalcScanner() {
        addLiteral(Token.Types.KW_TRUE, "true");
        addLiteral(Token.Types.KW_FALSE, "false");
        addLiteral(Token.Types.KW_NOTHING, "nothing");
        
        addPattern(Token.Types.IDENTIFIER, "[a-zA-Z_][a-zA-Z0-9_]*");
        addPattern(Token.Types.SYMBOL, "\\$[a-zA-Z0-9_]");
        addPattern(Token.Types.BIN_LITERAL, "[01]+b");
        addPattern(Token.Types.HEX_LITERAL, "0[xX][0-9a-fA-F]+");
        addPattern(Token.Types.OCT_LITERAL, "0[0-7]+");
        addPattern(Token.Types.FLOAT, "[1-9][0-9_]*(\\.[0-9_]*)?|0(\\.[0-9_]*)?", 2);
        addPattern(Token.Types.STRING, "\\\"(\\\\\\\"|[^\"])*\\\"", 1);
        addPattern(Token.Types.BUILTIN, "#<\\s*[0-9]+\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*>");
        
        addPattern(Token.Types.EQUALS_COMP, "==");
        addPattern(Token.Types.EQUALS, "=");
        addPattern(Token.Types.LT_COMP, "<<?", s -> (s.length() == 2) ? Token.Types.LSHIFT : Token.Types.LT_COMP);
        addPattern(Token.Types.GT_COMP, ">>?", s -> (s.length() == 2) ? Token.Types.RSHIFT : Token.Types.GT_COMP);
        
        addPattern(Token.Types.ARROW, "->");
        
        addPattern(Token.Types.PLUS, "\\+");
        addPattern(Token.Types.MINUS, "-");
        addPattern(Token.Types.DOUBLE_ASTERISK, "\\*\\*");
        addPattern(Token.Types.ASTERISK, "\\*");
        addPattern(Token.Types.SLASH, "/");
        
        addPattern(Token.Types.LPAR, "\\(");
        addPattern(Token.Types.RPAR, "\\)");
        addPattern(Token.Types.LBRA, "\\[");
        addPattern(Token.Types.RBRA, "\\]");
        addPattern(Token.Types.LBRACE, "\\{");
        addPattern(Token.Types.RBRACE, "\\}");
        addPattern(Token.Types.QUOTE, "\\'");
        addPattern(Token.Types.COMMA, ",");
        addPattern(Token.Types.CONS, "::");
        addPattern(Token.Types.COLON, ":");
        addPattern(Token.Types.SEMICOLON, ";");
        
        //addLiteral(Token.Types.KW_DEF, "def");
        
        addPattern(Token.Types.CONS, "\\:\\:?", s -> s.length() == 2 ? Token.Types.CONS : Token.Types.COLON);
        //addPattern(Token.Types.COLON, ":");
    }
    
    @Override
    protected Token makeToken(Token.Types tokenType, int offset, String value) {
        return new Token(tokenType, offset, value);
    }
    
    @Override
    protected Token makeUnknownToken(int offset, char value) {
        return new Token(Token.Types.UNKNOWN, offset, Character.toString(value));
    }

    @Override
    protected Token makeEOFToken(int offset) {
        return new Token(Token.Types.EOF, offset, "");
    }

    @Override
    protected boolean isEOFToken(Token token) {
        return token.getType() == Token.Types.EOF;
    }
    
}
