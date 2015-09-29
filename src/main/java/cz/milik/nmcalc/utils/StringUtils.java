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
    
}
