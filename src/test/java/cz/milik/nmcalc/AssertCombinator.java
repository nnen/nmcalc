/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.hamcrest.CoreMatchers;

/**
 *
 * @author jan
 */
public abstract class AssertCombinator<T> {
    
    public boolean apply(T value) {
        return applyInner(value);
    }
    
    protected abstract boolean applyInner(T value);
    
    protected String getMessage(T value) {
        return String.format(
                "%s failed with %s",
                Objects.toString(this),
                Objects.toString(value)
        );
    }
    
    public void assertTrue(T value) {
        org.junit.Assert.assertTrue(apply(value));
        //assertTrue(apply(value));
    }
    
    
    public static <T> AssertCombinator<T> and(AssertCombinator<T>... children) {
        return new AndAssert(children);
    }
    
    public static <T> AssertCombinator<T> or(AssertCombinator<T>... children) {
        return new OrAssert(children);
    }
    
    public static <T> AssertCombinator<T> instanceOf(Class<?> cls) {
        return new InstanceOfAssert(cls);
    }
    
    public static <T> AssertCombinator<T> equalsTo(T value) {
        return new EqualsAssert(value);
    }
    
    public static class EqualsAssert<T> extends AssertCombinator<T> {
        private final T value;
        
        public EqualsAssert(T value) {
            this.value = value;
        }
        
        @Override
        protected boolean applyInner(T value) {
            org.junit.Assert.assertEquals(this.value, value);
            return Objects.equals(this.value, value);
        }
    }
    
    
    public abstract static class UnaryAssert<T> extends AssertCombinator<T> {
        protected final AssertCombinator<T> inner;
        
        public UnaryAssert(AssertCombinator<T> inner) {
            this.inner = inner;
        }
    }
    
    
    public abstract static class NaryAssert<T> extends AssertCombinator<T> {
        protected final List<AssertCombinator<T>> children = new ArrayList();
        
        public NaryAssert(Collection<? extends AssertCombinator<T>> children) {
            this.children.addAll(children);
        }
        
        public NaryAssert(AssertCombinator<T>... children) {
            for (AssertCombinator<T> ac : children) {
                this.children.add(ac);
            }
        }
    }
    
    
    public static class AndAssert<T> extends NaryAssert<T> {
        public AndAssert(Collection<? extends AssertCombinator<T>> children) {
            super(children);
        }

        public AndAssert(AssertCombinator<T>... children) {
            super(children);
        }
        
        @Override
        protected boolean applyInner(T value) {
            for (AssertCombinator<T> child : children) {
                if (!child.apply(value)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    
    public static class OrAssert<T> extends NaryAssert<T> {
        public OrAssert(Collection<? extends AssertCombinator<T>> children) {
            super(children);
        }

        public OrAssert(AssertCombinator<T>... children) {
            super(children);
        }
        
        @Override
        protected boolean applyInner(T value) {
            List<AssertionError> errors = new ArrayList();
            for (AssertCombinator<T> child : children) {
                try {
                    if (child.apply(value)) {
                        return true;
                    }
                } catch (AssertionError e) {
                    errors.add(e);
                }
            }
            return false;
        }
    }
    
    
    public static class InstanceOfAssert<T> extends AssertCombinator<T> {
        private final Class<?> cls;
        
        public InstanceOfAssert(Class<?> cls) {
            this.cls = cls;
        }
        
        @Override
        protected boolean applyInner(T value) {
            org.junit.Assert.assertThat(value, CoreMatchers.instanceOf(cls));
            return this.cls.isInstance(value);
        }
    }
}
