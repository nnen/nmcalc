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
    
    private final Context staticContext;
    private final SymbolValue functionName;
    private final List<SymbolValue> argumentNames = new ArrayList();
    private final ICalcValue functionBody;

    public Context getStaticContext() {
        return staticContext;
    }
    
    public SymbolValue getFunctionName() {
        return functionName;
    }
    
    public ICalcValue getFunctionBody() {
        return functionBody;
    }
    
    public List<SymbolValue> getArgumentNames() {
        return Collections.unmodifiableList(argumentNames);
    }
    
    
    public FunctionValue(SymbolValue aName, ICalcValue aFunctionBody, Context aStaticContext) {
        functionName = aName;
        functionBody = aFunctionBody;
        staticContext = aStaticContext;
    }
    
    public FunctionValue(SymbolValue aName, ICalcValue aFunctionBody, Context aStaticContext, Collection<? extends SymbolValue> someArgumentNames) {
        functionName = aName;
        functionBody = aFunctionBody;
        argumentNames.addAll(someArgumentNames);
        staticContext = aStaticContext;
    }
    
    
    @Override
    public String getRepr() {
        StringBuilder sb = new StringBuilder();
        
        /*
        if (functionName == null) {
            sb.append("def ");
            sb.append(StringUtils.join(" ", argumentNames.stream().map(arg -> "\"" + arg.getValue())));
            sb.append(" -> ");
            sb.append(functionBody.getRepr());
            return sb.toString();
        }
        */
        
        sb.append("def ");
        if (functionName != null) {
            sb.append(functionName.getValue());
        }
        sb.append("(");
        sb.append(StringUtils.join(", ", argumentNames.stream().map(arg -> arg.getValue())));
        sb.append(") ");
        sb.append(functionBody.getExprRepr());
        return sb.toString();
    }
    
    @Override
    public Context apply(Context ctx, List<? extends ICalcValue> arguments) {
        final List<SymbolValue> argNames = getArgumentNames();
        return new Context(ctx, getStaticContext().getEnvironment().createChild(), getFunctionBody()) {
            @Override
            public ExecResult execute(Interpreter interpreter) {
                int pc = getPC();
                switch (pc) {
                    case 0:
                        Environment env = this.getEnvironment();
                        for (int i = 0; i < argNames.size(); i++) {
                            env.setVariable(argNames.get(i).getValue(), arguments.get(i));
                        }
                        setPC(pc + 1);
                        return new ExecResult(
                                ExecResult.ExitCodes.CONTINUE,
                                getFunctionBody().eval(this),
                                null
                        );
                    case 1:
                        setPC(pc + 1);
                        return new ExecResult(
                                ExecResult.ExitCodes.RETURN,
                                this,
                                getReturnedValue()
                        );
                    default:
                        return new ExecResult(
                                ExecResult.ExitCodes.ERROR,
                                this,
                                ErrorValue.formatted("Invalid value of PC counter (%d) in %s.", pc, toString())
                        );
                }
            }  
        };
    }
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        return visitor.visitFunction(this, context);
    }
    
}
