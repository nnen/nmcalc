/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.ast;

/**
 *
 * @author jan
 * @param <T>
 */
public interface ASTNodeVisitor<T, U> {
    
    public U visitUnknown(ASTNode node, T context);
    
    public U visitAssignment(ASTNode node, T context);
    
    public U visitRealLiteral(ASTNode node, T context);
    
    public U visitAddition(ASTNode node, T context);
    public U visitSubtraction(ASTNode node, T context);    
    public U visitMultiplication(ASTNode node, T context);
    public U visitDivision(ASTNode node, T context);
}

