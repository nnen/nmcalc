/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 * @author jan
 */
public class StringUtils {
    
    public static String joinStr(CharSequence glue, Iterable<? extends CharSequence> parts) {
        return joinStr(glue, parts.iterator());
    }
    
    public static String joinStr(CharSequence glue, Iterator<? extends CharSequence> parts) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        
        if (parts.hasNext()) {
            sb.append(parts.next());
        }
        while (parts.hasNext()) {
            sb.append(glue);
            sb.append(parts.next());
        }
        
        return sb.toString();
    }
    
    public static String joinStr(CharSequence glu, String[] parts, int from) {
        StringBuilder sb = new StringBuilder();
        if ((parts.length - from) > 0) {
            sb.append(parts[from]);
            for (int i = (from + 1); i < parts.length; i++) {
                sb.append(glu);
                sb.append(parts[i]);
            }
        }
        return sb.toString();
    }
    
    public static String joinStr(CharSequence glu, String[] parts) {
        return joinStr(glu, parts, 0);
    }
    
    public static String joinStr(CharSequence glue, CharSequence lastGlue, Iterable<? extends CharSequence> parts) {
        List<CharSequence> partList = new ArrayList();
        for (CharSequence part : parts) {
            partList.add(part);
        }
        
        if (partList.isEmpty()) {
            return "";
        }
        
        if (partList.size() == 1) {
            return partList.get(0).toString();
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (partList.size() - 2); i++) {
            sb.append(partList.get(i));
            sb.append(glue);
        }
        sb.append(partList.get(partList.size() - 2));
        sb.append(lastGlue);
        sb.append(partList.get(partList.size() - 1));
        return sb.toString();
    }
    
    public static <T> String joinStr(CharSequence glue, CharSequence lastGlue, Iterable<T> parts, Function<T, String> stringMap) {
        Iterator<T> iter = parts.iterator();
        
        if (!iter.hasNext()) {
            return "";
        }
        T item1 = iter.next();
        
        if (!iter.hasNext()) {
            return stringMap.apply(item1);
        }
        T item2 = iter.next();
        
        StringBuilder sb = new StringBuilder();
        
        while (iter.hasNext()) {
            sb.append(stringMap.apply(item1));
            sb.append(glue);
            item1 = item2;
            item2 = iter.next();
        }
        
        sb.append(stringMap.apply(item1));
        sb.append(lastGlue);
        sb.append(stringMap.apply(item2));
        
        return sb.toString();
    }
    
    public static <T> String joinStrOr(Iterable<T> parts, Function<T, String> stringMap) {
        return joinStr(", ", " or ", parts, stringMap);
    }
    
    public static CharSequence join(CharSequence glue, Iterable<? extends CharSequence> parts) {
        return join(glue, parts.iterator());
    }
    
    public static CharSequence join(CharSequence glue, Iterator<? extends CharSequence> parts) {
        StringBuilder sb = new StringBuilder();
        
        if (parts.hasNext()) {
            sb.append(parts.next());
        }
        
        while (parts.hasNext()) {
            sb.append(glue);
            sb.append(parts.next());
        }
        
        return sb.toString();
    }
    
    public static CharSequence join(CharSequence glue, Stream<? extends CharSequence> partStream) {
        return join(glue, partStream.iterator());
    }
    
    
    public static String capitalize(String str) {
        if (str.length() == 0) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
