/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
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
    
    public static boolean isPresent(Optional<?> value) {
        if (value == null) {
            return false;
        }
        return value.isPresent();
    }
    
    public static <T> void forEach(Iterable<T> iterable, Consumer<T> fn, Runnable between) {
        boolean first = true;
        for (T item : iterable) {
            if (first) {
                first = false;
            } else {
                between.run();
            }
            fn.accept(item);
        }
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
    

    public static String[] toArray(Collection<String> strings) {
        String[] result = new String[strings.size()];
        int i = 0;
        for (String str : strings) {
            result[i] = str;
            i++;
        }
        return result;
    }
    
    
    public static int copy(InputStream source, OutputStream target) throws IOException
    {
        byte buffer[] = new byte[1024];
        int length = 0;
        int read = 0;
        
        if ((read = source.read(buffer)) >= 0) {
            target.write(buffer, 0, read);
            length += read;
        }
        
        return length;
    }
    
    
    public static void readAll(Reader reader, Writer writer) throws IOException
    {
        char buffer[] = new char[1024];
        int count = 0;
        
        do {
            count = reader.read(buffer);
            if (count > 0) {
                writer.write(buffer, 0, count);
            }
        } while (count > 0);
    }
    
    public static String toString(Reader reader) throws IOException {
        StringWriter sw = new StringWriter();
        readAll(reader, sw);
        return sw.toString();
    }
    
    public static void readAll(InputStream input, Writer writer) throws IOException
    {
        Reader reader = new InputStreamReader(input);
        
        try {
            readAll(reader, writer);
        } finally {
            closeSilently(reader);
        }
    }
    
    public static String readAll(InputStream input) throws IOException
    {
        StringWriter writer = new StringWriter();
        readAll(input, writer);
        return writer.getBuffer().toString();
    }
}
