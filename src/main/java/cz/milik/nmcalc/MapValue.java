/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author jan
 */
public class MapValue extends CalcValue {

    private final Map<ICalcValue, ICalcValue> values = new HashMap();
    
    
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
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
