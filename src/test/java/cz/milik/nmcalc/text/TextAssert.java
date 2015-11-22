/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.text;

import cz.milik.nmcalc.AssertCombinator;

/**
 *
 * @author jan
 */
public abstract class TextAssert extends AssertCombinator<ITextElement> {
    
    public static AssertCombinator<ITextElement> childCount(int count) {
        return new ChildCountAssert(count);
    }
    
    public static AssertCombinator<ITextElement> child(int index, AssertCombinator<ITextElement> inner) {
        return new ChildAssert(index, inner);
    }
    
    
    public abstract static class Lambda extends TextAssert {
        protected abstract AssertCombinator<ITextElement> create();
        
        @Override
        protected boolean applyInner(ITextElement value) {
            return create().apply(value);
        }
    }
    
    public static class ChildCountAssert extends TextAssert {
        private final int childCount;
        
        public ChildCountAssert(int childCount) {
            this.childCount = childCount;
        }
        
        @Override
        protected boolean applyInner(ITextElement value) {
            org.junit.Assert.assertEquals(childCount, value.getChildren().size());
            return value.getChildren().size() == childCount;
        }
    }
    
    public static class ChildAssert extends UnaryAssert<ITextElement> {
        private final int childIndex;

        public ChildAssert(int childIndex, AssertCombinator<ITextElement> inner) {
            super(inner);
            this.childIndex = childIndex;
        }
        
        @Override
        protected boolean applyInner(ITextElement value) {
            if (childIndex >= value.getChildren().size()) {
                org.junit.Assert.fail(String.format(
                        "Expected %s to have at least %d children.",
                        value.toString(), childIndex + 1));
            }
            //org.junit.Assert.assertTrue(childIndex < value.getChildren().size());
            if (childIndex >= value.getChildren().size()) {
                return false;
            }
            return inner.apply(value.getChildren().get(childIndex));
        }
    }
    
}
