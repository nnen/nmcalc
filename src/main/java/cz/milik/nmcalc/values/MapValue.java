/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.values;

import cz.milik.nmcalc.Context;
import cz.milik.nmcalc.ICalcValueVisitor;
import cz.milik.nmcalc.NMCalcException;
import cz.milik.nmcalc.ReprContext;
import cz.milik.nmcalc.text.Text;
import cz.milik.nmcalc.text.TextWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author jan
 */
public class MapValue extends CalcValue {

    private final Map<ICalcValue, ICalcValue> values = new HashMap();
    
    
    /*
    @Override
    public boolean isValueEqual(ICalcValue other, Context ctx) {
        if (!(other instanceof MapValue)) {
            return false;
        }
        if (length() != other.length()) {
            return false;
        }
        try {
            for (ICalcValue key : values.keySet()) {
                //ICalcValue otherValue = other.get
            }
        } catch (NMCalcException e) {
            return false;
        }
    }
    */
    
    
    @Override
    public boolean hasLength() { return true; }
    
    @Override
    public int length() { return values.size(); 
    }
    
    @Override
    public Context getItem(Context ctx, ICalcValue index) {
        ICalcValue item = values.get(index);
        if (item == null) {
            ctx.setReturnedError(
                    "Map doesn't have entry with key '%s'.",
                    index.getRepr(ctx.getReprContext())
            );
            return ctx;
        }
        ctx.setReturnedValue(item);
        return ctx;
    }
    
    @Override
    public void setItem(ICalcValue index, ICalcValue value) throws NMCalcException {
        values.put(index, value);
    }
    
    @Override
    public Context setItem(Context ctx, ICalcValue index, ICalcValue value) {
        values.put(index, value);
        ctx.setReturnedValue(value);
        return ctx;
    }
    
    public ICalcValue getItem(ICalcValue index) {
        return values.get(index);
    }
    
    public Set<ICalcValue> getKeys() {
        return values.keySet();
    }
    
    
    @Override
    public String getRepr(ReprContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        
        boolean isFirst = true;
        for (Entry<ICalcValue, ICalcValue> entry : values.entrySet()) {
            if (isFirst) {
                isFirst = false;
                sb.append(" ");
            } else {
                sb.append(", ");
            }
            sb.append(entry.getKey().getRepr(ctx));
            sb.append(": ");
            sb.append(entry.getValue().getRepr(ctx));
        }
        
        sb.append(" }");
        return sb.toString();
    }
    
    
    @Override
    public void printDebug(TextWriter out, ReprContext ctx) {
        super.print(out, ctx);
    }
    
    @Override
    public void print(TextWriter out, ReprContext ctx) {
        if (ctx.isHyperTextPrint()) {
            out.startTable();
            out.startTableRow();
            out.tableCell(true, 2, "Map");
            out.end();
            for (Entry<ICalcValue, ICalcValue> entry : values.entrySet()) {
                out.startTableRow();
                out.startTableCell(true, 1, Text.HAlignment.NONE);
                entry.getKey().print(out, ctx);
                out.end();
                out.startTableCell();
                entry.getValue().print(out, ctx);
                out.end();
                out.end();
            }
            out.end();
            return;
        }
        
        out.monospace("{");
        boolean isFirst = true;
        for (Entry<ICalcValue, ICalcValue> entry : values.entrySet()) {
            if (isFirst) {
                isFirst = false;
                out.monospace(" ");
            } else {
                out.monospace(", ");
            }
            entry.getKey().print(out, ctx);
            out.monospace(": ");
            entry.getValue().print(out, ctx);
        }
        out.monospace(" }");
    }
    
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
