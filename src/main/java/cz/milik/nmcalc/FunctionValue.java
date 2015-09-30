/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.utils.StringUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jan
 */
public class FunctionValue extends CalcValue {
    
    private final SymbolValue functionName;
    private final List<SymbolValue> argumentNames = new ArrayList();
    private final ICalcValue functionBody;

    public SymbolValue getFunctionName() {
        return functionName;
    }
    
    public ICalcValue getFunctionBody() {
        return functionBody;
    }
    
    public List<SymbolValue> getArgumentNames() {
        return Collections.unmodifiableList(argumentNames);
    }
    
    
    public FunctionValue(SymbolValue aName, ICalcValue aFunctionBody) {
        functionName = aName;
        functionBody = aFunctionBody;
    }
    
    public FunctionValue(SymbolValue aName, ICalcValue aFunctionBody, Collection<? extends SymbolValue> someArgumentNames) {
        functionName = aName;
        functionBody = aFunctionBody;
        argumentNames.addAll(someArgumentNames);
    }
    
    
    @Override
    public String getRepr() {
        StringBuilder sb = new StringBuilder();
        
        if (functionName == null) {
            sb.append(StringUtils.join(" ", argumentNames.stream().map(arg -> "\"" + arg.getValue())));
            sb.append(" -> ");
            sb.append(functionBody.getRepr());
            return sb.toString();
        }
        
        sb.append("def ");
        sb.append(functionName.getValue());
        sb.append("(");
        sb.append(StringUtils.join(", ", argumentNames.stream().map(arg -> arg.getValue())));
        sb.append(") ");
        sb.append(functionBody.getRepr());
        return sb.toString();
    }
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        return visitor.visitFunction(this, context);
    }
    
}
