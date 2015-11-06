/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.utils.Utils;
import cz.milik.nmcalc.values.FloatValue;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.EnumSet;
import java.util.Optional;

/**
 *
 * @author jan
 */
public class ReprContext {    
    
    private ReprContext parent;
    
    public ReprContext getParent() {
        return parent;
    }
    
    public void setParent(ReprContext parent) {
        this.parent = parent;
    }
    
    
    public static enum Flags {
        EXPRESSION,
        APPLY_EXPRESSION,
        
        HEX,
        HEX_INTEGER_ONLY,
        
        OCTAL,
        OCTAL_INTEGER_ONLY,
        
        BINARY,
        BINARY_INTEGER_ONLY,
        
        PRETTY_PRINT_HELP,
    }
    
    
    private Optional<EnumSet<Flags>> flags = Optional.empty();
    
    private Optional<EnumSet<Flags>> disabledFlags = Optional.empty();
    
    public boolean hasFlag(Flags flag) {
        if (disabledFlags.isPresent() && disabledFlags.get().contains(flag)) {
            return false;
        }
        
        if (flags.isPresent() && flags.get().contains(flag)) {
            return true;
        }
        
        if (parent != null) {
            return parent.hasFlag(flag);
        }
        
        return false;
    }
    
    public void setFlags(EnumSet<Flags> flags) {
        this.flags = Optional.ofNullable(flags);
    }
    
    public ReprContext addFlags(Flags first, Flags... rest) {
        if (!flags.isPresent()) {
            flags = Optional.of(EnumSet.noneOf(Flags.class));
        }
        flags.get().addAll(EnumSet.of(first, rest));
        return this;
    }
    
    public ReprContext removeFlags(Flags first, Flags... rest) {
        if (!disabledFlags.isPresent()) {
            disabledFlags = Optional.of(EnumSet.noneOf(Flags.class));
        }
        disabledFlags.get().addAll(EnumSet.of(first, rest));
        return this;
    }
    
    
    public static final DecimalFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat();
    
    static {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator('_');
        dfs.setDecimalSeparator('.');
        
        //decimalFormat = new DecimalFormat();
        DEFAULT_DECIMAL_FORMAT.setGroupingUsed(true);
        DEFAULT_DECIMAL_FORMAT.setGroupingSize(3);
        DEFAULT_DECIMAL_FORMAT.setDecimalFormatSymbols(dfs);
        DEFAULT_DECIMAL_FORMAT.setMaximumFractionDigits(340);
    }
    
    private Optional<DecimalFormat> decimalFormat = Optional.empty();

    public DecimalFormat getDecimalFormat() {
        if (decimalFormat.isPresent()) {
            return decimalFormat.get();
        }
        if (parent != null) {
            return parent.getDecimalFormat();
        }
        return DEFAULT_DECIMAL_FORMAT;
    }

    public void setDecimalFormat(DecimalFormat decimalFormat) {
        this.decimalFormat = Optional.ofNullable(decimalFormat);
    }
    
    
    public boolean isExpression() {
        return hasFlag(Flags.EXPRESSION);
    }
    
    public ReprContext withIsExpression() {
        ReprContext ctx = new ReprContext(this);
        ctx.addFlags(Flags.EXPRESSION);
        return ctx;
    }
    
    public boolean isApplyExpression() {
        return hasFlag(Flags.APPLY_EXPRESSION);
    }
    
    public ReprContext withIsApplyExpression() {
        ReprContext ctx = new ReprContext(this);
        ctx.addFlags(Flags.APPLY_EXPRESSION);
        return ctx;
    }
    
    
    public boolean hasPrettyPrintHelp() {
        return hasFlag(Flags.PRETTY_PRINT_HELP);
    }
    
    
    public ReprContext() {
    }
    
    public ReprContext(ReprContext parent) {
        this.parent = parent;
    }
    
    public ReprContext(EnumSet<Flags> flags) {
        setFlags(flags.clone());
    }
    
    public ReprContext(ReprContext parent, Optional<EnumSet<Flags>> flags, Optional<EnumSet<Flags>> disabledFlags) {
        this.parent = parent;
        if (Utils.isPresent(flags)) {
            this.flags = Optional.of(EnumSet.copyOf(flags.get()));
        }
        if (Utils.isPresent(disabledFlags)) {
            this.disabledFlags = Optional.of(EnumSet.copyOf(disabledFlags.get()));
        }
    }
    
    public ReprContext copy() {
        return new ReprContext(parent, flags, disabledFlags);
    }
    
    public String formatFloat(BigDecimal value) {
        //return value.stripTrailingZeros().toString();
        return getDecimalFormat().format(value);
    }
    
    public String formatFloat(FloatValue value) {
        if (hasFlag(Flags.HEX)) {
            return formatHexInt(value.getDecimalValue());
        } else if (hasFlag(Flags.OCTAL)) {
            return formatOctInt(value.getDecimalValue());
        } else if (hasFlag(Flags.BINARY)) {
            return formatBinInt(value.getDecimalValue());
        } else if (value.isInteger()) {
            if (hasFlag(Flags.HEX_INTEGER_ONLY)) {
                return formatHexInt(value.getDecimalValue());
            } else if (hasFlag(Flags.OCTAL_INTEGER_ONLY)) {
                return formatOctInt(value.getDecimalValue());
            } else if (hasFlag(Flags.BINARY_INTEGER_ONLY)) {
                return formatBinInt(value.getDecimalValue());
            }
            //return value.getDecimalValue().setScale(0).toString();
            return formatFloat(value.getDecimalValue().setScale(0));
        }
        return formatFloat(value.getDecimalValue());
        //return value.getDecimalValue().toString();
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
