/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.ast;

import cz.milik.nmcalc.parser.Token;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author jan
 */
public class ASTNode implements Iterable<ASTNode> {
    
    private ASTNodeTypes nodeType = ASTNodeTypes.UNKNOWN;
    
    public ASTNodeTypes getType()
    {
        return nodeType;
    }
    
    private final List<ASTNode> children = new ArrayList<>();
    
    public List<ASTNode> getChildren() { return Collections.unmodifiableList(children); }
    
    private Token literalValue;
    
    public Token getLiteralValue()
    {
        return literalValue;
    }
    
    public float getFloatValue()
    {
        return Float.parseFloat(getLiteralValue().getValue());
    }
    
    @Override
    public Iterator<ASTNode> iterator() {
        return children.iterator();
    }
    
    public ASTNode(ASTNodeTypes nodeType)
    {
        this.nodeType = nodeType;
    }
    
    public ASTNode(ASTNodeTypes nodeType, Token literalValue, ASTNode... children)
    {
        this.nodeType = nodeType;
        this.literalValue = literalValue;
        this.children.addAll(Arrays.asList(children));
    }
    
    public ASTNode(ASTNodeTypes nodeType, Token literalValue, Collection<ASTNode> children)
    {
        this.nodeType = nodeType;
        this.literalValue = literalValue;
        this.children.addAll(children);
    }
    
    @Override
    public String toString() {
        return "ASTNode{" + "nodeType=" + nodeType + ", children=" + children + ", literalValue=" + literalValue + '}';
    }
    
    public <T, U> U visit(ASTNodeVisitor<T, U> visitor, T context)
    {
        switch (getType())
        {
            case UNKNOWN:
                return visitor.visitUnknown(this, context);
            case REAL_LITERAL:
                return visitor.visitRealLiteral(this, context);
            case ADDITION:
                return visitor.visitAddition(this, context);
            case SUBTRACTION:
                return visitor.visitSubtraction(this, context);
            case MULTIPLICATION:
                return visitor.visitMultiplication(this, context);
            case DIVISION:
                return visitor.visitDivision(this, context);
            default:
                throw new AssertionError(getType().name());   
        }
        
        //throw new AssertionError(getType().name());   
    }
    
}
