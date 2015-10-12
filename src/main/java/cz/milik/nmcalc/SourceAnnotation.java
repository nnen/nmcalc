/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

/**
 *
 * @author jan
 */
public class SourceAnnotation implements ICalcValueAnnotation {
    
    private final int offset;
    private final int length;

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }
    
    public SourceAnnotation(int offset, int length) {
        this.offset = offset;
        this.length = length;
    }
    
}
