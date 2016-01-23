/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jan
 */
public class IOUtils {
    
    public static void closeSilently(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException ex) {
            Logger.getLogger(IOUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean writeFileSafely(File file, IThrowsFunction<OutputStream, Boolean, IOException> callback) {
        try {
            if (!file.canWrite()) {
                return false;
            }
            
            File tempFile = File.createTempFile("nmcalc_", "");
            try {
                try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                    if (!callback.apply(outputStream)) {
                        return false;
                    }
                }
                
                if (!file.delete()) {
                    return false;
                }
                return tempFile.renameTo(file);
            } finally {
                tempFile.delete();
            }
        } catch (IOException ex) {
            Logger.getLogger(IOUtils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
 
    public static boolean writeFileSafely(String fileName, IThrowsFunction<OutputStream, Boolean, IOException> callback) {
        return writeFileSafely(new File(fileName), callback);
    }
    
}
