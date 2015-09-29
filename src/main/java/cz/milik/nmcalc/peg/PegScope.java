/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.peg;

import cz.milik.nmcalc.utils.IMonad;
import cz.milik.nmcalc.utils.Monad;
import cz.milik.nmcalc.utils.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jan
 */
public class PegScope {

    private final PegScope parent;
    
    public PegScope getParent() {
        return parent;
    }
    
    
    private List<Pair<String, Object>> values;
    
    public void add(String aName, Object aValue) {
        if (values == null) {
            values = new LinkedList();
        }
        values.add(new Pair(aName, aValue));
    }
    
    public <T> IMonad<T> get(String aName, Class<T> aClass) {
        if (values == null) {
            return Monad.nothing();
        }
        for (Pair<String, Object> p : values) {
            if (p.getFirst().equals(aName)) {
                return Monad.just(aClass.cast(p.getSecond()));
            }
        }
        return Monad.nothing();
    }
    
    public <T> T expect(String aName, Class<T> aClass) throws PegException {
        if (values != null) {
            for (Pair<String, Object> p : values) {
                if (p.getFirst().equals(aName)) {
                    return aClass.cast(p.getSecond());
                }
            }
        }
        throw new PegException("Value " + aName + " is missing.");
    }
    
    public <T> List<T> getAll(String aName, Class<T> aClass) {
        if (values == null) { 
            return Collections.emptyList();
        }
        List<T> result = new ArrayList();
        for (Pair<String, Object> p : values) {
            if (p.getFirst().equals(aName)) {
                result.add(aClass.cast(p.getSecond()));
            }
        }
        return result;
    }
    
    
    public void update(PegScope other) {
        if (values == null) {
            if (other.values == null) {
                return;
            }
            values = new LinkedList();
        }
        values.addAll(other.values);
    }
    
    
    public PegScope(PegScope parent) {
        this.parent = parent;
    }
    
}
