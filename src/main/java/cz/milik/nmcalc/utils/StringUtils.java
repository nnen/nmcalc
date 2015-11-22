/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

import java.util.Iterator;
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
