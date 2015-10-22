/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;

/**
 *
 * @author jan
 */
public class FloatValue extends CalcValue {
    
    public static final char GROUPING_SEPARATOR = '_';
    public static final String GROUPING_SEPARATOR_STR = Character.toString(GROUPING_SEPARATOR);
    
    
    private final BigDecimal value;
    
    
    public FloatValue(float value)
    {
        this.value = BigDecimal.valueOf((double)value);
    }
    
    public FloatValue(double value)
    {
        this.value = BigDecimal.valueOf(value);
    }
    
    public FloatValue(BigDecimal value)
    {
        this.value = value;
    }
    
    public FloatValue(BigInteger value) {
        this.value = new BigDecimal(value);
    }
    
    
    public static FloatValue parse(String value) {
        return new FloatValue(new BigDecimal(
                value.replace(GROUPING_SEPARATOR_STR, "")));
    }
    
    public static FloatValue parseHex(String value) {
        String clean = value.substring(2).replace(GROUPING_SEPARATOR_STR, "");
        return new FloatValue(new BigDecimal(new BigInteger(clean, 16)));
    }
    
    public static FloatValue parseOct(String value) {
        String clean = value.substring(1).replace(GROUPING_SEPARATOR_STR, "");
        return new FloatValue(new BigDecimal(new BigInteger(clean, 8)));
    }
    
    public static FloatValue parseBin(String value) {
        String clean = value.substring(0, value.length() - 1).replace(GROUPING_SEPARATOR_STR, "");
        return new FloatValue(new BigDecimal(new BigInteger(clean, 2)));
    }
    
    
    @Override
    public String toString() {
        return value.toString();
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + value.hashCode();
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FloatValue other = (FloatValue) obj;
        return value.compareTo(other.value) == 0;
    }
    
    public boolean isInteger() {
        return (value.scale() <= 0) || (value.stripTrailingZeros().scale() <= 0);
    }
    
    @Override
    public String getRepr(ReprContext ctx) {
        return ctx.formatFloat(this);
    }
    
    @Override
    public boolean getBooleanValue() {
        return value.compareTo(BigDecimal.ZERO) != 0;
    }
    
    @Override
    public BigDecimal getDecimalValue() {
        return value;
    }
    
    @Override
    public ICalcValue toFloat(Context ctx) {
        return this;
    }

    
    @Override
    public boolean isValueEqual(ICalcValue other, Context ctx) {
        ICalcValue otherFloat = other.toFloat(ctx);
        if (otherFloat.isError()) {
            return false;
        }
        return value.compareTo(otherFloat.getDecimalValue()) == 0;
    }
    
    @Override
    public int compareValue(ICalcValue other, Context ctx) {
        ICalcValue otherFloat = other.toFloat(ctx);
        return value.compareTo(otherFloat.getDecimalValue());
    }
    
    
    @Override
    public ICalcValue negate(Context ctx) {
        return new FloatValue(value.negate());
    }
    
    protected ICalcValue binaryOp(ICalcValue other, Context ctx, Function<BigDecimal, BigDecimal> fn) {
        ICalcValue casted = other.toFloat(ctx);
        if (casted.isError()) {
            return casted;
        }
        return new FloatValue(fn.apply(casted.getDecimalValue()));
    }
    
    @Override
    public ICalcValue add(ICalcValue other, Context ctx) {
        return binaryOp(other, ctx, decimal -> value.add(decimal));
    }
    
    @Override
    public ICalcValue subtract(ICalcValue other, Context ctx) {
        return binaryOp(other, ctx, decimal -> value.subtract(decimal));
    }
    
    @Override
    public ICalcValue divide(ICalcValue other, Context ctx) {
        return binaryOp(other, ctx, decimal -> {
            return value.divide(decimal, ctx.getMathContext());
            //BigDecimal result[] = value.divideAndRemainder(decimal);
            //return result[0];
        });
        //return binaryOp(other, ctx, decimal -> value.divide(decimal));
    }
    
    @Override
    public ICalcValue multiply(ICalcValue other, Context ctx) {
        return binaryOp(other, ctx, decimal -> value.multiply(decimal));
    }
    
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        return visitor.visitFloat(this, context);
    }
    
}
