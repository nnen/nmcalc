/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.loader;

import cz.milik.nmcalc.NMCalcException;
import cz.milik.nmcalc.utils.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jan
 */
public abstract class CalcLoader implements ICalcLoader {
    
    private static CalcLoader instance;
    
    public static CalcLoader getInstance() {
        if (instance == null) {
            ChainLoader loader = new ChainLoader();
            loader.append(new DirLoader(System.getProperty("user.home")));
            loader.append(new ResourceLoader("cz/milik/nmcalc"));
            instance = loader;
        }
        return instance;
    }
    
    
    public String getString(String name) throws NMCalcException {
        InputStream input = getStream(name);
        if (input == null) {
            return null;
        }
        try {
            return Utils.readAll(input);
        } catch (IOException ex) {
            Logger.getLogger(CalcLoader.class.getName()).log(Level.SEVERE, null, ex);
            throw new NMCalcException(ex);
        }
    }
    
    
    public static class ResourceLoader extends CalcLoader {
        
        private String basePath = "cz/milik/nmcalc";

        public String getBasePath() {
            return basePath;
        }

        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }
        
        
        private ClassLoader clsLoader;

        public ClassLoader getClsLoader() {
            if (clsLoader == null) {
                clsLoader = getClass().getClassLoader();
            }
            return clsLoader;
        }
        
        public void setClsLoader(ClassLoader clsLoader) {
            this.clsLoader = clsLoader;
        }

        
        public ResourceLoader(String basePath) {
            this.basePath = basePath;
        }
        
        
        @Override
        public InputStream getStream(String name) {
            ClassLoader clsLoader = getClsLoader();
            return clsLoader.getResourceAsStream(getBasePath() + "/" + name);
        }
        
    }
    
    
    public static class DirLoader extends CalcLoader {
        private String dirPath = System.getProperty("user.home");
        
        public String getDirPath() {
            return dirPath;
        }

        public void setDirPath(String dirPath) {
            this.dirPath = dirPath;
        }

        
        public DirLoader(String dirPath) {
            this.dirPath = dirPath;
        }
        
        
        @Override
        public InputStream getStream(String name) {
            File f = new File(getDirPath() + "/" + name);
            if (!f.isFile()) {
                return null;
            }
            try {
                return new FileInputStream(f);
            } catch (FileNotFoundException ex) {
                return null;
            }
        }
    }
    
    
    public static class ChainLoader extends CalcLoader {

        private List<ICalcLoader> loaders = new ArrayList();
        
        public ICalcLoader append(ICalcLoader loader) {
            loaders.add(loader);
            return loader;
        }
        
        public ICalcLoader prepend(ICalcLoader loader) {
            loaders.add(0, loader);
            return loader;
        }
        
        public List<ICalcLoader> getLoaders() {
            return Collections.unmodifiableList(loaders);
        }
        
        
        @Override
        public InputStream getStream(String name) {
            InputStream result = null;
            
            for (ICalcLoader loader : loaders) {
                result = loader.getStream(name);
                if (result != null) {
                    return result;
                }
            }
            
            return null;
        }
        
    }
    
}
