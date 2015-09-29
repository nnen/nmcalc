/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.ast;

import cz.milik.nmcalc.parser.Token;
import cz.milik.nmcalc.utils.ObjectUtils;

/**
 *
 * @author jan
 */
public class ASTBuilder {
 
    public ASTNode assignment(Token operator, ASTNode lhs, ASTNode rhs) {
        return new ASTNode(ASTNodeTypes.ASSIGNMENT, operator, lhs, rhs);
    }
    
    public ASTNode assignment(Token operator, Token lhs, ASTNode rhs) {
        return assignment(operator, new ASTNode(ASTNodeTypes.VARIABLE, lhs), rhs);
    }
    
    public ASTNode realLiteral(Token literal) {
        return new ASTNode(ASTNodeTypes.REAL_LITERAL, literal);
    }
    
    public ASTNode variable(Token variable) {
        return new ASTNode(ASTNodeTypes.VARIABLE, variable);
    }
    
    public ASTNode binaryOp(Token operator, ASTNode left, ASTNode right) {
        ObjectUtils.argNonNull("left", left);
        ObjectUtils.argNonNull("right", right);
        return new ASTNode(ASTNodeTypes.forOperator(operator.getType()), operator, left, right);
    }
    
    public ASTNode addition(Token plusSign, ASTNode left, ASTNode right) {
        ObjectUtils.argNonNull("left", left);
        ObjectUtils.argNonNull("right", right);
        return new ASTNode(ASTNodeTypes.ADDITION, plusSign, left, right);
    }
    
    public ASTNode subtraction(Token minusSign, ASTNode left, ASTNode right) {
        ObjectUtils.argNonNull("left", left);
        ObjectUtils.argNonNull("right", right);
        return new ASTNode(ASTNodeTypes.SUBTRACTION, minusSign, left, right);
    }
    
}
