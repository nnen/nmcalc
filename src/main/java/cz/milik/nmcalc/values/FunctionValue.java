/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.values;

import cz.milik.nmcalc.Context;
import cz.milik.nmcalc.Environment;
import cz.milik.nmcalc.ExecResult;
import cz.milik.nmcalc.ICalcValueVisitor;
import cz.milik.nmcalc.Process;
import cz.milik.nmcalc.ReprContext;
import cz.milik.nmcalc.text.TextWriter;
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
    
    private final Environment staticContext;
    private final SymbolValue functionName;
    private final List<SymbolValue> argumentNames = new ArrayList();
    private final ICalcValue functionBody;

    public Environment getStaticContext() {
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
    
    
    public FunctionValue(SymbolValue aName, ICalcValue aFunctionBody, Environment aStaticContext) {
        functionName = aName;
        functionBody = aFunctionBody;
        staticContext = aStaticContext;
    }
    
    public FunctionValue(SymbolValue aName, ICalcValue aFunctionBody, Environment aStaticContext, Collection<? extends SymbolValue> someArgumentNames) {
        functionName = aName;
        functionBody = aFunctionBody;
        argumentNames.addAll(someArgumentNames);
        staticContext = aStaticContext;
    }

    
    @Override
    public void print(TextWriter out, ReprContext ctx) {
        out.span("keyword", "def");
        out.plain(" ");
        if (functionName != null) {
            out.span("name", functionName.getValue());
        }
        out.plain("(");
        out.plain(") ");
        if (getHelp().isPresent()) {
            out.span("literal", "\"" + getHelp().get() + "\" ");
        }
        functionBody.print(out, ctx);
    }
    
    @Override
    public String getRepr(ReprContext ctx) {
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
        if (getHelp().isPresent()) {
            sb.append("\n  \"");
            sb.append(getHelp().get());
            sb.append("\"\n  ");
        }
        sb.append(functionBody.getExprRepr(ctx));
        return sb.toString();
    }
    
    
    @Override
    public Context apply(Context ctx, List<? extends ICalcValue> arguments) {
        final List<SymbolValue> argNames = getArgumentNames();
        
        if (!checkArguments(ctx, arguments, argNames.size())) {
            return ctx;
        }
        
        return new Context(ctx, getStaticContext().createChild(), getFunctionBody()) {    
            @Override
            public ExecResult execute(Process process) {
                int pc = getPC();
                switch (pc) {
                    case 0:
                        Environment env = this.getEnvironment();
                        for (int i = 0; i < argNames.size(); i++) {
                            env.setVariable(argNames.get(i).getValue(), arguments.get(i));
                        }
                        setPC(pc + 1);
                        return ctxContinue(getFunctionBody().eval(this));
                    case 1:
                        setPC(pc + 1);
                        return ctxReturn(getReturnedValue());
                    default:
                        return invalidPC(pc);
                }
            }
            
            @Override
            protected void printDescription(TextWriter out, ReprContext ctx) {
                SymbolValue name = FunctionValue.this.getFunctionName();
                List<SymbolValue> argNames = FunctionValue.this.getArgumentNames();
                if (name == null) {
                    out.plain("executing anonymous function");
                } else {
                    out.plain("executing function ");
                    out.monospace(
                            "%s(%s)",
                            name.getValue(),
                            StringUtils.join(", ", argNames.stream().map(s -> s.getValue()))
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
