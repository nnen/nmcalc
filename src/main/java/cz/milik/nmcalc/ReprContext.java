/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.EnumSet;

/**
 *
 * @author jan
 */
public class ReprContext { 
   
    public static enum Flags {
        HEX,
        HEX_INTEGER_ONLY,
        
        OCTAL,
        OCTAL_INTEGER_ONLY,
        
        BINARY,
        BINARY_INTEGER_ONLY
    }
    
    
    private EnumSet<Flags> flags = EnumSet.noneOf(Flags.class);
    
    public EnumSet<Flags> getFlags() {
        return flags;
    }

    public void setFlags(EnumSet<Flags> flags) {
        this.flags = flags;
    }
    
    public ReprContext addFlags(Flags first, Flags... rest) {
        flags.addAll(EnumSet.of(first, rest));
        return this;
    }
    
    
    public ReprContext() {
    }
    
    public ReprContext(EnumSet<Flags> flags) {
        setFlags(flags.clone());
    }
    
    
    public ReprContext copy() {
        return new ReprContext(getFlags());
    }
    
    public String formatFloat(BigDecimal value) {
        //return value.stripTrailingZeros().toString();
        return value.toString();
    }
    
    public String formatFloat(FloatValue value) {
        if (flags.contains(Flags.HEX)) {
            return formatHexInt(value.getDecimalValue());
        } else if (flags.contains(Flags.OCTAL)) {
            return formatOctInt(value.getDecimalValue());
        } else if (flags.contains(Flags.BINARY)) {
            return formatBinInt(value.getDecimalValue());
        } else if (value.isInteger()) {
            if (flags.contains(Flags.HEX_INTEGER_ONLY)) {
                return formatHexInt(value.getDecimalValue());
            } else if (flags.contains(Flags.OCTAL_INTEGER_ONLY)) {
                return formatOctInt(value.getDecimalValue());
            } else if (flags.contains(Flags.BINARY_INTEGER_ONLY)) {
                return formatBinInt(value.getDecimalValue());
            }
            return value.getDecimalValue().setScale(0).toString();
        }
        return value.getDecimalValue().toString();
    }
    
    public String formatHexInt(BigDecimal value) {
        return formatHexInt(value.toBigInteger());
    }
    
    public String formatOctInt(BigDecimal value) {
        return formatOctInt(value.toBigInteger());
    }
    
    public String formatBinInt(BigDecimal value) {
        return formatBinInt(value.toBigInteger());
    }
    
    public String formatHexInt(BigInteger value) {
        return "0x" + value.toString(16);
    }
    
    public String formatOctInt(BigInteger value) {
        return "0" + value.toString(8);
    }
    
    public String formatBinInt(BigInteger value) {
        return value.toString(2) + "b";
    }
    
    public static ReprContext getDefault() {
        return new ReprContext();
    }
    
}
