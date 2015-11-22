/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import java.util.Arrays;
import java.util.List;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.IsNot;

/**
 *
 * @author jan
 */
public abstract class MatcherCombinator<T> extends BaseMatcher<T> {
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
    
    
    @Override
    public void describeTo(Description description) {
        description.appendText(String.format("Did not match %s.", toString()));
    }
    
    
    public static <T> MatcherCombinator<T> equalsTo(T value) {
        return new Wrapper(new IsEqual(value));
    }
    
    public static <T> MatcherCombinator<T> instanceOf(Class<?> cls) {
        return new Wrapper(new IsInstanceOf(cls));
    }
    
    public MatcherCombinator<T> not() {
        return new Wrapper(new IsNot(this));
    }
    
    public static <T> MatcherCombinator<T> and(Matcher<? extends T>... children) {
        return new AndMatcher(children);
    }
    
    
    public static abstract class TypeSafeMatcher<T> extends MatcherCombinator<T> {
        private final Class<? extends T> cls;
        
        public TypeSafeMatcher(Class<? extends T> cls) {
            this.cls = cls;
        }
        
        @Override
        public boolean matches(Object item) {
            if (item == null) {
                return false;
            }
            if (!cls.isInstance(item)) {
                return false;
            }
            if (!matchesSafe(cls.cast(item))) {
                return false;
            }
            return true;
        }
        
        protected abstract boolean matchesSafe(T item);
    }
    
    
    public abstract static class UnaryMatcher<T> extends MatcherCombinator<T> {
        protected final Matcher<T> innerMatcher;

        public UnaryMatcher(Matcher<T> innerMatcher) {
            this.innerMatcher = innerMatcher;
        }
    }
    
    public static class Wrapper<T> extends UnaryMatcher<T> {
        public Wrapper(Matcher<T> innerMatcher) {
            super(innerMatcher);
        }
        
        @Override
        public boolean matches(Object item) {
            return innerMatcher.matches(item);
        }

        @Override
        public void describeTo(Description description) {
            innerMatcher.describeTo(description);
        }
    }
    
    public abstract static class BinaryMatcher<T> extends MatcherCombinator<T> {
        protected final Matcher<T> firstMatcher;
        protected final Matcher<T> secondMatcher;

        public BinaryMatcher(Matcher<T> firstMatcher, Matcher<T> secondMatcher) {
            this.firstMatcher = firstMatcher;
            this.secondMatcher = secondMatcher;
        }
    }
    
    public abstract static class NaryMatcher<T> extends MatcherCombinator<T> {
        protected final List<Matcher<? extends T>> children;
        
        public NaryMatcher(Matcher<? extends T>... children) {
            this.children = Arrays.asList(children);
        }
    }
    
    public static class AndMatcher<T> extends NaryMatcher<T> {
        public AndMatcher(Matcher<? extends T>... children) {
            super(children);
        }
        
        @Override
        public boolean matches(Object item) {
            for (Matcher<? extends T> child : children) {
                if (!child.matches(item)) {
                    return false;
                }
            }
            return true;
        }
    }
    
}
