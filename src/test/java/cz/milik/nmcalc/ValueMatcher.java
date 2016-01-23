/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.utils.Pair;
import cz.milik.nmcalc.values.CalcValue;
import cz.milik.nmcalc.values.ICalcValue;
import cz.milik.nmcalc.values.MapValue;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 * @author jan
 */
public abstract class ValueMatcher extends MatcherCombinator<ICalcValue> {

    public static ValueMatcher isList() { return new IsList(false); }
    public static ValueMatcher isListWithItems(Matcher<ICalcValue>... items) {
        return new IsList(true, items);
    }
    public static ValueMatcher isDict(Pair<ICalcValue, Matcher<ICalcValue>>... pairs) {
        return new IsDict(Arrays.asList(pairs));
    }
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
        private final List<Matcher<ICalcValue>> items;
        
        public IsList(boolean withItems, Matcher<ICalcValue>... items) {
            if (!withItems) {
                this.items = null;
            } else {
                this.items = Arrays.asList(items);
            }
        }
        
        @Override
        protected boolean matchesValue(ICalcValue value) {
            if (!value.isList()) {
                return false;
            }
            
            if (this.items == null) {
                return true;
            }
            
            if (value.length() != items.size()) {
                return false;
            }
            
            for (int i = 0; i < items.size(); i++) {
                ICalcValue item = value.getItem(i);
                if (!items.get(i).matches(item)) {
                    return false;
                }
            }
             
            return true;
        }
    }
    
    public static class IsDict extends ValueMatcher {

        private final List<Pair<ICalcValue, Matcher<ICalcValue>>> items;

        public IsDict(List<Pair<ICalcValue, Matcher<ICalcValue>>> items) {
            this.items = items;
        }
        
        @Override
        protected boolean matchesValue(ICalcValue value) {
            if (!(value instanceof MapValue)) {
                return false;
            }
            MapValue mapValue = (MapValue)value;
            if (items != null) {
                if (items.size() != value.length()) {
                    return false;
                }
                for (Pair<ICalcValue, Matcher<ICalcValue>> item : items) {
                    Matcher<ICalcValue> matcher = item.getSecond();
                    ICalcValue itemValue = mapValue.getItem(item.getFirst());
                    if (!matcher.matches(itemValue)) {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public void describeMismatch(Object item, Description desc) {
            if (!(item instanceof MapValue)) {
                desc.appendValue(item);
                desc.appendText(" is not a dict");
                return;
            }
            MapValue mapValue = (MapValue)item;
            if (items != null) {
                if (items.size() != mapValue.length()) {
                    desc.appendValue(mapValue);
                    desc.appendText(String.format(" doesn't have %d items", items.size()));
                }
                for (Pair<ICalcValue, Matcher<ICalcValue>> pair : items) {
                    Matcher<ICalcValue> matcher = pair.getSecond();
                    ICalcValue itemValue = mapValue.getItem(pair.getFirst());
                    if (!matcher.matches(itemValue)) {
                        desc.appendText("for key ");
                        desc.appendValue(pair.getFirst());
                        desc.appendText(": ");
                        matcher.describeMismatch(itemValue, desc);
                        return;
                    }
                }
            }
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
                if (!items.get(i).matches(value.getItem(i))) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public void describeMismatch(Object item, Description description) {
            if (!(item instanceof ICalcValue)) {
                description.appendValue(item);
                description.appendText(" is not ICalcValue");
                return;
            }
            ICalcValue value = (ICalcValue)item;
            if (!value.hasLength()) {
                description.appendValue(item);
                description.appendText(" doesn't have a length");
                return;
            }
            if (value.length() != items.size()) {
                description.appendValue(item);
                description.appendText(String.format(
                        " doesn't have %d items",
                        items.size()));
                return;
            }
            for (int i = 0; i < items.size(); i++) {
                ICalcValue element = value.getItem(i);
                if (!items.get(0).matches(element)) {
                    items.get(0).describeMismatch(element, description);
                    return;
                }
            }
        }
        
    }
    
}
