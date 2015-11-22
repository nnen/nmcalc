/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.values;

import cz.milik.nmcalc.ICalcValueVisitor;
import cz.milik.nmcalc.ReprContext;
import cz.milik.nmcalc.text.TextWriter;
import java.io.ByteArrayOutputStream;
import static jdk.nashorn.internal.objects.ArrayBufferView.buffer;

/**
 *
 * @author jan
 */
public class BytesValue extends CalcValue {

    private final byte[] value;
    
    public BytesValue() {
        value = new byte[] {};
    }

    public BytesValue(byte[] value) {
        this.value = value;
    }
    
    
    @Override
    public boolean hasLength() {
        return true;
    }
    
    @Override
    public int length() {
        return value.length;
    }

    
    @Override
    public void print(TextWriter out, ReprContext ctx) {
        out.span("literal", getRepr(ctx));
    }
    
    @Override
    public String getRepr(ReprContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append("b'");
        for (int i = 0; i < (value.length - 1); i++) {
            sb.append(String.format("%02X ", value[i]));
            if ((i % 4) == 3) {
                sb.append(" ");
            }
        }
        if (value.length > 0) {
            sb.append(String.format("%02X", value[value.length - 1]));
        }
        sb.append("'");
        return sb.toString();
    }
    
    
    public static BytesValue parseLiteral(String literal) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        boolean isHigh = true;
        char high = '0';
        
        for (int i = 2; i < (literal.length() - 1); i++) {
            char c = literal.charAt(i);
            if (!Character.isWhitespace(c))
            {
                if (isHigh) {
                    high = c;
                } else {
                    out.write((Character.digit(high, 16) << 4) + Character.digit(c, 16));
                }
                isHigh ^= true;
            }
        }
        
        if (!isHigh) {
            out.write(Character.digit(high, 16) << 4);
        }
        
        return new BytesValue(out.toByteArray());
    }
    
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
