/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author jan
 */
public class Utils {
 
    public static <T> List<T> getNonNull(List<T> list)
    {
        if (list == null)
        {
            return Collections.emptyList();
        }
        return list;
    }
    
    
    public static <T, U> List<U> mapList(Iterable<T> input, Function<T, U> mapFn) {
        List<U> result = new ArrayList();
        for (T item : input) {
            result.add(mapFn.apply(item));
        }
        return result;
    }
    
}
