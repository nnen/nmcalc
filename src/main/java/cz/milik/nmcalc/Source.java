/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.utils.Utils;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jan
 */
public abstract class Source implements ISource {

    @Override
    public String getContent(Context ctx) throws NMCalcException {
        try {
            return Utils.toString(getReader(ctx));
        } catch (IOException ex) {
            Logger.getLogger(Source.class.getName()).log(Level.SEVERE, null, ex);
            if (ctx == null) {
                throw NMCalcException.format(
                        ex,
                        "Failed to read %s to string.",
                        getName()
                );
            } else {
                throw NMCalcException.format(
                        ctx, ex,
                        "Failed to read %s to string.",
                        getName()
                );
            }
        }
    }
    
    
    public static ISource fromString(String value) {
        return new StringSource(value);
    }
    
    public static ISource fromString(String value, String name) {
        return new StringSource(value, name);
    }
    
    public static ISource fromResource(ClassLoader loader, String name) {
        return new ResourceSource(loader, name);
    }
    
    public static ISource fromResource(String name) {
        return fromResource(Source.class.getClassLoader(), name);
    }
    
    public static ISource fromFile(String fileName) {
        return new FileSource(fileName);
    }
    
    
    private static class StringSource extends Source {

        private final String name;
        private final String value;

        public StringSource(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public StringSource(String value) {
            this.name = null;
            this.value = value;
        }
        
        @Override
        public String getName() {
            if (name == null) {
                return "<string>";
            }
            return name;
        }

        @Override
        public Reader getReader(Context ctx) throws NMCalcException {
            return new StringReader(value);
        }

        @Override
        public String getContent(Context ctx) throws NMCalcException {
            return value;
        }
        
    }
    
    
    private static class ResourceSource extends Source {

        private final ClassLoader clsLoader;
        private final String name;

        public ResourceSource(ClassLoader clsLoader, String name) {
            this.clsLoader = clsLoader;
            this.name = name;
        }
        
        @Override
        public String getName() { return name; }
        
        @Override
        public Reader getReader(Context ctx) throws NMCalcException {
            InputStream stream = clsLoader.getResourceAsStream(getName());
            if (stream == null) {
                return null;
            }
            return new InputStreamReader(stream);
        }
        
    }
    
    
    private static class FileSource extends Source {
        private final String fileName;

        public FileSource(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public String getName() {
            return fileName;
        }
        
        @Override
        public Reader getReader(Context ctx) throws NMCalcException {
            try {
                return new FileReader(getName());
            } catch (FileNotFoundException ex) {
                throw NMCalcException.format(
                        ctx,
                        ex,
                        "Could not find file %s.", getName()
                );
            }
        }
    }
    
}
