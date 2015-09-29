/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.ast;

import cz.milik.nmcalc.parser.Token;

/**
 *
 * @author jan
 */
public enum ASTNodeTypes {
    
    UNKNOWN,
    
    ASSIGNMENT,
    
    REAL_LITERAL,
    VARIABLE,
    
    ADDITION,
    SUBTRACTION,
    MULTIPLICATION,
    DIVISION,
    
    ;
    
    public static ASTNodeTypes forOperator(Token.Types operator) {
        switch (operator) {
            case EQUALS: return ASSIGNMENT;
            
            case PLUS: return ADDITION;
            case MINUS: return SUBTRACTION;
            case ASTERISK: return MULTIPLICATION;
            case SLASH: return DIVISION;
        }
        return UNKNOWN;
    }
    
}
