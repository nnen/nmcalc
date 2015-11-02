/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.loader;

import cz.milik.nmcalc.Context;
import cz.milik.nmcalc.ISource;
import cz.milik.nmcalc.NMCalcException;
import cz.milik.nmcalc.Source;
import java.io.File;
import java.net.URL;
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
    
    private static final Logger LOGGER = Logger.getLogger(CalcLoader.class.getName());
    
    private static ICalcLoader instance;
    
    public static ICalcLoader getInstance() {
        if (instance == null) {
            ChainLoader loader = new ChainLoader();
            loader.append(new DirLoader(System.getProperty("user.home")));
            loader.append(new ResourceLoader("cz/milik/nmcalc"));
            instance = loader;
        }
        return instance;
    }

    
    /*
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
    */
    
    
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
        public ISource getSource(String name, Context ctx) throws NMCalcException {
            String path = getBasePath() + "/" + name;
            LOGGER.info(String.format(
                    "Searching for source %s in resources in %s...",
                    name, path
            ));
            
            URL url = getClsLoader().getResource(path);
            if (url == null) {
                return null;
            }
            
            LOGGER.info(String.format(
                    "Source %s found in %s.",
                    name, path
            ));
            return Source.fromResource(getClsLoader(), path);
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
        public ISource getSource(String name, Context ctx) throws NMCalcException {
            File f = new File(getDirPath() + "/" + name);
            LOGGER.info(String.format(
                    "Searching for source %s in file '%s'...",
                    name, f.getAbsolutePath()
            ));
            
            File tf = new File("C:\\Users\\jan\\utils.nmcalc");
            LOGGER.info(String.format("%s exists: %s", tf.getAbsolutePath(), Boolean.toString(tf.isFile())));
            
            if (!f.isFile()) {
                return null;
            }
            
            LOGGER.info(String.format(
                    "Source %s found in %s.",
                    name, f.getAbsolutePath()
            ));
            return Source.fromFile(f.getPath());
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
        public ISource getSource(String name, Context ctx) throws NMCalcException {
            LOGGER.info(String.format(
                    "Searching for source %s...", 
                    name
            ));
            
            for (ICalcLoader loader : loaders) {
                ISource source = loader.getSource(name, ctx);
                if (source != null) {
                    return source;
                }
            }
            
            LOGGER.warning(String.format(
                    "Source %s was not found!",
                    name
            ));
            
            return null;
        }
        
    }
    
}
