/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jan
 */
public class ListMap<K, V> {
    
    private List<V> defaultList;
    private Map<K, List<V>> namedLists;
    
    public List<V> getDefaultList()
    {
        return Utils.getNonNull(defaultList);
    }
    
    public List<V> getNamedList(K key)
    {
        return Utils.getNonNull(namedLists.get(key));
    }
    
    public V add(V value)
    {
        if (defaultList == null)
        {
            defaultList = new ArrayList<>();
        }
        defaultList.add(value);
        return value;
    }
    
    public V add(K key, V value)
    {
        if (namedLists == null)
        {
            namedLists = new HashMap<>();
        }
        List<V> list = namedLists.get(key);
        if (list == null)
        {
            list = new ArrayList<>();
            namedLists.put(key, list);
        }
        list.add(value);
        return value;
    }
    
}
