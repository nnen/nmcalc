/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.ICalcValueAnnotation;

/**
 *
 * @author jan
 */
public class TextLoc implements ICalcValueAnnotation {
    
    private final String fileName;

    public String getFileName() {
        return fileName;
    }
    
    
    private final int offset;

    public int getOffset() {
        return offset;
    }
    
    
    public TextLoc(String fileName, int offset) {
        this.fileName = fileName;
        this.offset = offset;
    }
    
}
