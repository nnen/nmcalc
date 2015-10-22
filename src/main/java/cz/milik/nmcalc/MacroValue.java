/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author jan
 */
public class MacroValue extends CalcValue {

    private final SymbolValue name;
    private final List<ICalcValue> argNames;
    private final ICalcValue body;
    private final String help;
    
    public MacroValue(SymbolValue name, Collection<? extends ICalcValue> arguments, String help, ICalcValue body) {
        this.name = name;
        this.argNames = new ArrayList(arguments);
        this.help = help;
        this.body = body;
    }
    
    
    @Override
    protected Optional<String> getHelpInner() {
        return Optional.ofNullable(help);
    }
    
    
    @Override
    public String getRepr(ReprContext ctx) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("defmacro ");
        sb.append(name.getRepr(ctx));
        sb.append("(");
        boolean first = true;
        for (ICalcValue arg : argNames) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(arg.getRepr(ctx));
        }
        sb.append(") ");
        sb.append(body.getExprRepr(ctx));
        
        return sb.toString();
    }

    
    @Override
    public boolean isSpecialForm() { return true; }
    
    @Override
    protected Context applySpecialInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
        if (!checkArguments(ctx, arguments, argNames.size())) {
            return ctx;
        }
        
        return new Context(ctx, ctx.getEnvironment(), this) {
            private ICalcValue substituted = body;
            
            @Override
            public ExecResult execute(Interpreter interpreter) {
                int pc = getPC();
                
                if ((pc > 0) && (pc <= arguments.size())) {
                    substituted = getReturnedValue();
                }
                
                if (pc < arguments.size()) {
                    setPC(pc + 1);
                    return ctxContinue(substituted.substitute(this, argNames.get(pc), arguments.get(pc)));
                } else if (pc == arguments.size()) {
                    setPC(pc + 1);
                    return ctxContinue(substituted.eval(this));
                } else if (pc == arguments.size() + 1) {
                    setPC(pc + 1);
                    return ctxReturn(getReturnedValue());
                }
                
                return invalidPC(pc);
            }
        };
    }
    
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
