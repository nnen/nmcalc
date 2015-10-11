/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author jan
 */
public class ListBuilder {
    
    private List<ICalcValue> items = new ArrayList();
    
    public void add(ICalcValue first, ICalcValue... rest) {
        items.add(first);
        for (ICalcValue item : rest) {
            items.add(item);
        }
    }
    
    public void addAll(Collection<? extends ICalcValue> items) {
        this.items.addAll(items);
    }
    
    public ICalcValue makeList() {
        ICalcValue result = new ListValue(items, true);
        reset();
        return result;
    }
    
    public void reset() {
        items = new ArrayList();
    }
    
}
