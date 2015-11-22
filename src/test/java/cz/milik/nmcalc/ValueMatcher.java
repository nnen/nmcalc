/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.values.CalcValue;
import cz.milik.nmcalc.values.FloatValue;
import cz.milik.nmcalc.values.ICalcValue;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matcher;

/**
 *
 * @author jan
 */
public abstract class ValueMatcher extends MatcherCombinator<ICalcValue> {

    public static ValueMatcher isList() { return new IsList(); }
    public static ValueMatcher items(Matcher<ICalcValue>... items) {
        return new Items(items);
    }
    
    public static MatcherCombinator<ICalcValue> isFloat(float value) {
        return equalsTo(CalcValue.make(value));
    }
    
    
    @Override
    public boolean matches(Object item) {
        if (item instanceof ICalcValue) {
            return matchesValue((ICalcValue)item);
        }
        return false;
    }
    
    protected abstract boolean matchesValue(ICalcValue value);
    
    
    public static class IsList extends ValueMatcher {
        @Override
        protected boolean matchesValue(ICalcValue value) {
            return value.isList();
        }
    }
    
    public static class Items extends ValueMatcher {
        private final List<Matcher<ICalcValue>> items;

        public Items(List<Matcher<ICalcValue>> items) {
            this.items = items;
        }
        
        public Items(Matcher<ICalcValue>... items) {
            this.items = Arrays.asList(items);
        }

        @Override
        protected boolean matchesValue(ICalcValue value) {
            if (!value.hasLength() || (value.length() != items.size())) {
                return false;
            }
            for (int i = 0; i < items.size(); i++) {
                if (!items.get(0).matches(value.getItem(i))) {
                    return false;
                }
            }
            return true;
        }
    }
    
}
