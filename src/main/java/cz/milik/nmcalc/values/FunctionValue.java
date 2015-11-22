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
import cz.milik.nmcalc.utils.Utils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author jan
 */
public class FunctionValue extends CalcValue {
    
    private final Environment staticContext;
    private final SymbolValue functionName;
    //private final List<SymbolValue> argumentNames = new ArrayList();
    private final ICalcValue functionBody;
    
    private final List<ArgumentInfo> arguments = new ArrayList();

    public Environment getStaticContext() {
        return staticContext;
    }
    
    public SymbolValue getFunctionName() {
        return functionName;
    }
    
    public ICalcValue getFunctionBody() {
        return functionBody;
    }
    
    /*
    public List<SymbolValue> getArgumentNames() {
        return Collections.unmodifiableList(argumentNames);
    }
    */
    
    public List<ArgumentInfo> getArguments() {
        return Collections.unmodifiableList(arguments);
    }
    
    
    public FunctionValue(SymbolValue aName, ICalcValue aFunctionBody, Environment aStaticContext) {
        functionName = aName;
        functionBody = aFunctionBody;
        staticContext = aStaticContext;
    }
    
    /*
    public FunctionValue(SymbolValue aName, ICalcValue aFunctionBody, Environment aStaticContext, Collection<? extends SymbolValue> someArgumentNames) {
        functionName = aName;
        functionBody = aFunctionBody;
        argumentNames.addAll(someArgumentNames);
        staticContext = aStaticContext;
    }
    */
    
    public FunctionValue(SymbolValue aName, ICalcValue aFunctionBody, Environment aStaticContext, Collection<ArgumentInfo> someArguments) {
        functionName = aName;
        functionBody = aFunctionBody;
        arguments.addAll(someArguments);
        staticContext = aStaticContext;
    }
    
    
    public String getSignatureMarkup() {
        StringBuilder sb = new StringBuilder();
        sb.append("`").append(functionName.getValue()).append("(");
        Utils.forEach(arguments, arg -> {
            if (arg.isVarArg()) {
                sb.append("*").append(arg.getName());
            } else if (arg.isOptional()) {
                sb.append("[").append(arg.getName()).append("]");
            } else {
                sb.append("*").append(arg.getName()).append("*");
            }
        }, () -> {
            sb.append(", ");
        });
        sb.append(")").append("`");
        return sb.toString();
    }
    
    public void printSignature(TextWriter out, ReprContext ctx) {
        out.plain(functionName.getValue() + "(");
        Utils.forEach(arguments, arg -> {
            if (arg.isVarArg()) {
                out.plain("*");
                out.italic(arg.getName());
            } else if (arg.isOptional()) {
                out.plain("[");
                out.italic(arg.getName());
                out.plain("]");
            } else {
                out.italic(arg.getName());
            }
        }, () -> {
            out.plain(", ");
        });
        out.plain(")");
    }
    
    @Override
    public void print(TextWriter out, ReprContext ctx) {
        out.span("keyword", "def");
        out.plain(" ");
        if (functionName != null) {
            out.span("func_name", functionName.getValue());
        }
        out.plain("(");
        Utils.forEach(arguments, arg -> {
            if (arg.isVarArg()) {
                out.plain("*");
            }
            out.span("arg_name", arg.getName());
        }, () -> {
            out.plain(", ");
        });
        out.plain(") ");
        if (getHelp().isPresent()) {
            out.span("literal", "\"" + getHelp().get() + "\" ");
        }
        ReprContext bodyCtx = ctx.withIsExpression();
        functionBody.print(out, bodyCtx);
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
        sb.append(StringUtils.join(", ", arguments.stream().map(arg -> arg.getName())));
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
    protected Optional<String> getHelpInner() {
        Optional<String> help = super.getHelpInner();
        if (help.isPresent()) {
            return help;
        }
        return Optional.of(getSignatureMarkup());
    }
    
    
    @Override
    public Context applyInner(Context ctx, List<? extends ICalcValue> arguments) {
        //final List<SymbolValue> argNames = getArgumentNames();
        final List<ArgumentInfo> args = getArguments();
        
        if (!checkArguments(ctx, arguments, args)) {
            return ctx;
        }
        
        return new Context(ctx, getStaticContext().createChild(), getFunctionBody()) {    
            @Override
            public ExecResult execute(Process process) {
                int pc = getPC();
                switch (pc) {
                    case 0:
                        Environment env = this.getEnvironment();
                        if (args.size() > 0 && args.get(args.size() - 1).isVarArg()) {
                            for (int i = 0; i < args.size() - 1; i++) {
                                env.setVariable(args.get(i).getName(), arguments.get(i));
                            }
                            ICalcValue varArgs = CalcValue.list(arguments.subList(args.size() - 1, arguments.size()));
                            env.setVariable(args.get(args.size() - 1).getName(), varArgs);
                        } else {
                            for (int i = 0; i < args.size(); i++) {
                                env.setVariable(args.get(i).getName(), arguments.get(i));
                            }
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
                List<ArgumentInfo> args = FunctionValue.this.getArguments();
                if (name == null) {
                    out.plain("executing anonymous function");
                } else {
                    out.plain("executing function ");
                    out.monospace(
                            "%s(%s)",
                            name.getValue(),
                            StringUtils.join(", ", args.stream().map(s -> s.getName()))
                    );
                }
            }   
        };
    }
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        return visitor.visitFunction(this, context);
    }
 
    
    public static class ArgumentInfo implements Serializable {
        private String name;

        public String getName() {
            return name;
        }
        
        
        private int position;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
        
        
        private boolean optional = false;

        public boolean isOptional() {
            return optional;
        }
        
        
        private boolean varArg = false;

        public boolean isVarArg() {
            return varArg;
        }
        
        
        public ArgumentInfo(String name, int position, boolean isOptional, boolean isVarArg) {
            this.name = name;
            this.position = position;
            this.optional = isOptional;
            this.varArg = isVarArg;
        }
    }
}
