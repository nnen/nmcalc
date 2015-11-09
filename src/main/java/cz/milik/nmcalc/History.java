/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.utils.ListenerCollection;
import cz.milik.nmcalc.utils.Pair;
import cz.milik.nmcalc.values.CalcValue;
import cz.milik.nmcalc.values.ICalcValue;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jan
 */
public class History {
    
    private List<Pair<String, ICalcValue>> values = new ArrayList();
    
    public int add(String expr, ICalcValue value) {
        int index = values.size();
        values.add(Pair.of(expr, value));
        return index;
    }
    
    public ICalcValue get(int index) {
        if ((index < 0) || (index >= values.size())) {
            return CalcValue.nothing();
        }
        return values.get(index).getSecond();
    }
    
    public void clear() {
        values.clear();
        listeners.handleEvent(l -> l.onHistoryCleared(this));
    }
    
    
    private ListenerCollection<IListener> listeners = new ListenerCollection(new IListener[] {});

    public boolean add(IListener e) {
        return listeners.add(e);
    }
    
    public boolean remove(IListener o) {
        return listeners.remove(o);
    }
    
    
    public interface IListener {
        public void onHistoryCleared(History history);
    }
    
    public class Adapter implements IListener {
        @Override
        public void onHistoryCleared(History history) {
            // Do nothing.
        }
    }
    
}
