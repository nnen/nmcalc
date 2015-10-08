/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import java.math.BigDecimal;

/**
 *
 * @author jan
 */
public class ReprContext {
    
    public String formatFloat(BigDecimal value) {
        //return value.stripTrailingZeros().toString();
        return value.toString();
    }
    
    public static ReprContext getDefault() {
        return new ReprContext();
    }
    
}
