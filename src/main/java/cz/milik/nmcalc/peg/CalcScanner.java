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
public class CalcScanner extends RegExScanner<Token.Types, Token> {

    public CalcScanner() {
        addPattern(Token.Types.IDENTIFIER, "[a-zA-Z_][a-zA-Z0-9_]*");
        addPattern(Token.Types.SYMBOL, "\\$[a-zA-Z0-9_]");
        addPattern(Token.Types.BIN_LITERAL, "[01]+b");
        addPattern(Token.Types.HEX_LITERAL, "0[xX][0-9a-fA-F]+");
        addPattern(Token.Types.OCT_LITERAL, "0[0-7]+");
        addPattern(Token.Types.FLOAT, "[1-9][0-9]*(\\.[0-9]*)?|0(\\.[0-9]*)?");
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
