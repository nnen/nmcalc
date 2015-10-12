/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 
    
    public static void closeSilently(Object obj, Closeable stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        } catch (IOException e) {
            Logger.getLogger(obj.getClass().getName()).log(Level.WARNING, "Error occured while closing a stream.", e);
        }
    }
    
    public static void closeSilently(Closeable stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        } catch (IOException e) {
            Logger.getLogger("Utils").log(Level.WARNING, "Error occured while closing a stream.", e);
        }
    }
    
    public static <T extends Closeable> void doAndClose(Object obj, T argument, IThrowsAction<T, IOException> action) throws IOException {
        try {
            action.execute(argument);
        } finally {
            closeSilently(obj, argument);
        }
    }
    
    public static <T extends Closeable> void createAndClose(Object obj, IThrowsSupplier<T, IOException> supplier, IThrowsAction<T, IOException> action) throws IOException {
        T closeable = null;
        try {
            closeable = supplier.supply();
            action.execute(closeable);
        } finally {
            closeSilently(obj, closeable);
        }
    }
    
}
