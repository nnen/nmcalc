/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

import java.util.Objects;

/**
 *
 * @author jan
 */
public class ObjectUtils {
    
    public static String toString(Object obj) {
        return Objects.toString(obj);
    }
    
    public static void argNonNull(String name, Object value) {
        if (value == null) {
            throw new NullPointerException(String.format("Argument '%s' is null.", name));
        }
    }
    
}
