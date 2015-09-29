/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

/**
 *
 * @author jan
 */
public interface ICalcValueVisitor<T, U> {
    
    public T visitFloat(FloatValue value, U context);
    public T visitString(StringValue value, U context);
    public T visitList(ListValue value, U context);
    
    public T visitError(ErrorValue value, U context);
    public T visitSymbol(SymbolValue value, U context);
    
    public T visitBuiltin(BuiltinCalcValue value, U context);
    public T visitFunction(FunctionValue value, U context);
    
    public T visitUnknown(ICalcValue value, U context);
    
}
