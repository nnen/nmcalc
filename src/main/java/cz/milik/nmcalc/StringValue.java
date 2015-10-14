/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.parser.Token;
import java.math.BigDecimal;
import java.util.Objects;

/**
 *
 * @author jan
 */
public class StringValue extends PrimitiveValueBase<String> {

    public StringValue(String value) {
        super(value);
    }

    
    @Override
    public boolean isHelp() {
        return getAnnotation(CalcAnnotation.IsHelp.class).isPresent();
    }
    
    
    @Override
    public String getRepr(ReprContext ctx) {
        if (ctx.hasPrettyPrintHelp() && isHelp()) {
            return getValue();
        }
        return "\"" + getValue() + "\"";
    }
    
    
    @Override
    public boolean getBooleanValue() {
        return !getValue().isEmpty();
    }
    
    
    @Override
    public BigDecimal getDecimalValue() {
        try {
            return new BigDecimal(getValue());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
    

    @Override
    public ICalcValue toFloat(Context ctx) {
        try {
            return CalcValue.make(new BigDecimal(getValue()));
        } catch (NumberFormatException e) {
            return CalcValue.error(
                    ctx,
                    e,
                    "Cannot convert %s to float, invalid format.",
                    getRepr(ctx.getReprContext())
            );
        }
    }
    
    
    @Override
    public String getStringValue(Context ctx) {
        return getValue();
    }
    
    @Override
    public ICalcValue toStringValue(Context ctx) {
        return this;
    }
    

    @Override
    public boolean isValueEqual(ICalcValue other, Context ctx) {
        ICalcValue otherStr = other.toStringValue(ctx);
        return Objects.equals(getValue(), otherStr.getStringValue(ctx));
    }
    
    @Override
    public int compareValue(ICalcValue other, Context ctx) {
        ICalcValue otherStr = other.toStringValue(ctx);
        return getValue().compareTo(otherStr.getStringValue(ctx));
    }
    
    
    @Override
    public ICalcValue getItem(int index) {
        if ((index < 0) || (index >= getValue().length())) {
            return new ErrorValue();
        }
        return new StringValue(Character.toString(getValue().charAt(index)));
    }
    
    @Override
    public int length() {
        return getValue().length();
    }
    
    @Override
    public boolean hasLength() {
        return true;
    }
    
    
    @Override
    public ICalcValue add(ICalcValue other, Context ctx) {
        if (other.isError()) {
            return other;
        }
        
        return new StringValue(getValue() + other.getStringValue(ctx));
    }

    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        return visitor.visitString(this, context);
    }
    
    
    public boolean isKeyword() {
        for (Token.Types tt : Token.Types.values()) {
            if (tt.isKeyword && Objects.equals(getValue(), tt.keyword)) {
                return true;
            }
        }
        return false;
    }
    
}
