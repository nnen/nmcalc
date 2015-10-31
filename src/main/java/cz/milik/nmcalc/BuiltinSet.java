/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.values.ICalcValue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author jan
 */
public class BuiltinSet implements Iterable<BuiltinCalcValue> {
    
    private final List<BuiltinCalcValue> values =
            new ArrayList();
    
    public <T extends BuiltinCalcValue> T register(T value) {
        int id = values.size();
        values.add(value);
        value.setBuiltinId(id);
        return value;
    }
    
    public ICalcValue get(int id) {
        if ((id >= 0) && (id < values.size())) {
            return values.get(id);
        }
        return null;
    }

    
    @Override
    public Iterator<BuiltinCalcValue> iterator() {
        return values.iterator();
    }
    
}
