/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.ast;

import cz.milik.nmcalc.ast.ASTNode;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 *
 * @author jan
 */
public abstract class ASTNodePredicate implements Predicate<ASTNode> {
    
    public static ASTNodePredicate make(String value, ASTNodeTypes first, ASTNodeTypes... rest) {
        return new LiteralValuePredicate(value).and(type(first, rest));
    }
    
    public static ASTNodePredicate type(ASTNodeTypes first, ASTNodeTypes... rest) {
        return new ASTTypePredicate(first, rest);
    }
    
    public ASTNodePredicate and(ASTNodePredicate other) {
        return new AndPredicate(this, other);
    }
    
    public ASTNodePredicate children(ASTNodePredicate... children) {
        return and(new ChildrenPredicate(children));
    }
    
    
    public static class LiteralValuePredicate extends ASTNodePredicate {
        
        private final String expectedValue;
        
        public LiteralValuePredicate(String value) {
            expectedValue = value;
        }
        
        @Override
        public boolean test(ASTNode t) {
            return Objects.equals(t.getLiteralValue().getValue(), expectedValue);
        }
        
    }
    
    public abstract static class NaryPredicate extends ASTNodePredicate {
        
        protected ASTNodePredicate[] children;
        
        public NaryPredicate(ASTNodePredicate... children) {
            this.children = children;
        }
        
    }
    
    public static class AndPredicate extends NaryPredicate {

        public AndPredicate(ASTNodePredicate... children) {
            super(children);
        }
        
        @Override
        public boolean test(ASTNode t) {
            for (ASTNodePredicate p : children) {
                if (!p.test(t)) {
                    return false;
                }
            }
            return true;
        }
        
    }
    
    public static class ASTTypePredicate extends ASTNodePredicate {
        
        private final EnumSet<ASTNodeTypes> expectedTypes;
        
        @Override
        public boolean test(ASTNode t) {
            return expectedTypes.contains(t.getType());
        }
        
        public ASTTypePredicate(ASTNodeTypes type, ASTNodeTypes... rest) {
            expectedTypes = EnumSet.of(type, rest);
        }
        
    }
    
    public static class ChildrenPredicate extends NaryPredicate {

        public ChildrenPredicate(ASTNodePredicate... children) {
            super(children);
        }
        
        @Override
        public boolean test(ASTNode t) {
            List<ASTNode> nodes = t.getChildren();
            if (nodes.size() != children.length) {
                return false;
            }
            for (int i = 0; i < nodes.size(); i++) {
                if (!children[i].test(nodes.get(i))) {
                    return false;
                }
            }
            return true;
        }
        
    }
}
