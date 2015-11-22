/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.values;

import cz.milik.nmcalc.Context;
import cz.milik.nmcalc.ICalcValueVisitor;
import cz.milik.nmcalc.ReprContext;
import cz.milik.nmcalc.text.IPrintable;
import cz.milik.nmcalc.text.TextWriter;
import cz.milik.nmcalc.utils.StringUtils;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author jan
 */
public class JavaObjectValue extends CalcValue {

    private final Object obj;

    public Object getObj() {
        return obj;
    }
    
    
    public JavaObjectValue(Object obj) {
        this.obj = obj;
    }

    
    @Override
    public boolean isNothing() {
        return (obj == null);
    }
    
    
    @Override
    public String getStringValue(Context ctx) {
        return Objects.toString(obj);
    }
    
    @Override
    public void print(TextWriter out, ReprContext ctx) {
        if (obj == null) {
            out.plain("null");
            return;
        }
        if (obj instanceof IPrintable) {
            IPrintable printable = (IPrintable)obj;
            printable.print(out, ctx);
            return;
        }
        String clsName = obj.getClass().getSimpleName();
        if (clsName.length() > 0) {
            out.plain(clsName);
        } else {
            out.plain(obj.getClass().getName());
        }
    }
    
    @Override
    public void printDebug(TextWriter out, ReprContext ctx) {
        print(out, ctx);
    }
    
    
    @Override
    public boolean hasLength() {
        if (obj == null) {
            return false;
        }
        if ((obj instanceof List) || (obj instanceof Array)) {
            return true;
        }
        return false;
    }
    
    @Override
    public int length() {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof List) {
            return ((List)obj).size();
        }
        if (obj instanceof Array) {
            return Array.getLength(obj);
        }
        return 0;
    }
    
    @Override
    public ICalcValue getItem(int index) {
        if (obj == null) {
            return super.getItem(index);
        }
        if (obj instanceof List) {
            //List list = (List)obj;
            return CalcValue.wrap(((List)obj).get(index));
        }
        if (obj instanceof Array) {
            return CalcValue.wrap(Array.get(obj, index));
        }
        return super.getItem(index);
    }
    
    
    protected Method getMethod(String name) {
        if (obj == null) {
            return null;
        }
        try {
            return obj.getClass().getMethod(name);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
    
    @Override
    public Context getAttribute(SymbolValue attrName, Context ctx) {
        try {
            String getterName = "get" + StringUtils.capitalize(attrName.getValue());
            Method getter = getMethod(getterName);
            if (getter != null) {
                ctx.setReturnedValue(CalcValue.wrap(getter.invoke(obj)));
                return ctx;
            }
            
            getterName = "is" + StringUtils.capitalize(attrName.getValue());
            getter = getMethod(getterName);
            if (getter != null) {
                ctx.setReturnedValue(CalcValue.wrap(getter.invoke(obj)));
                return ctx;
            }
        } catch (Exception e) {
            ctx.setReturnedValue(ErrorValue.error(ctx, e));
            return ctx;
        }
        
        return super.getAttribute(attrName, ctx);
    }
    
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
