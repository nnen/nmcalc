/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.values.SomeValue;
import cz.milik.nmcalc.values.FloatValue;
import cz.milik.nmcalc.values.ErrorValue;
import cz.milik.nmcalc.values.StringValue;
import cz.milik.nmcalc.values.ListValue;
import cz.milik.nmcalc.values.FunctionValue;
import cz.milik.nmcalc.values.NothingValue;
import cz.milik.nmcalc.values.SymbolValue;
import cz.milik.nmcalc.values.BoolValue;
import cz.milik.nmcalc.values.ICalcValue;

/**
 *
 * @author jan
 */
public interface ICalcValueVisitor<T, U> {
    
    public T visitBool(BoolValue value, U context);
    public T visitFloat(FloatValue value, U context);
    public T visitString(StringValue value, U context);
    public T visitList(ListValue value, U context);
    
    public T visitError(ErrorValue value, U context);
    public T visitSymbol(SymbolValue value, U context);
    
    public T visitSome(SomeValue value, U context);
    public T visitNothing(NothingValue value, U context);
    
    public T visitBuiltin(BuiltinCalcValue value, U context);
    public T visitFunction(FunctionValue value, U context);
    
    public T visitUnknown(ICalcValue value, U context);
    
}
