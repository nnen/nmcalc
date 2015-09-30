/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.parser.Token;
import cz.milik.nmcalc.utils.IMonad;
import cz.milik.nmcalc.utils.Monad;
import java.util.List;

/**
 *
 * @author jan
 */
public abstract class BuiltinCalcValue extends CalcValue {
    
    public abstract String getName();
    
    @Override
    public String getRepr() {
        return "#" + getName();
    }
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        return visitor.visitBuiltin(this, context);
    }
    
    
    public static IMonad<ICalcValue> forOperator(Token.Types operator) {
        switch (operator) {
            case PLUS:
                return Monad.just(ADD);
            case MINUS:
                return Monad.just(SUB);
            case ASTERISK:
                return Monad.just(MULT);
            case SLASH:
                return Monad.just(DIV);
            default:
                return Monad.nothing();
        }
    }
    
    
    public static final ICalcValue LET = new BuiltinCalcValue() {
        @Override
        public String getName() { return "let"; }
    };
    
    
    public static final BuiltinCalcValue LIST = new BuiltinCalcValue() {
        
        @Override
        public String getName() {
            return "list";
        }
        
        @Override
        public Context apply(Context ctx, List<? extends ICalcValue> arguments) {
            ctx.setReturnedValue(CalcValue.list(arguments));
            return ctx;
        }
        
    };
    
    
    public static final BuiltinCalcValue APPLY = new BuiltinCalcValue() {

        @Override
        public String getName() {
            return "apply";
        }
        
        @Override
        public Context apply(Context ctx, List<? extends ICalcValue> arguments) {
            if (arguments.size() < 1) {
                ctx.setReturnedValue(ErrorValue.formatted(
                        "%s cannot be applied to %d arguments. At least one argument expected.",
                        getRepr(),
                        arguments.size()
                ));
                return ctx;
            }
            ICalcValue head = arguments.get(0);
            List<? extends ICalcValue> tail = arguments.subList(1, arguments.size());
            return head.apply(ctx, tail);
        }
        
    };
            
    
    public static final BuiltinCalcValue EVAL = new BuiltinCalcValue() {
        
        @Override
        public String getName() {
            return "eval";
        }
        
        @Override
        public Context apply(Context ctx, List<? extends ICalcValue> arguments) {
            if (arguments.size() != 1) {
                ctx.setReturnedValue(ErrorValue.formatted("%s cannot be applied to %d arguments. Exactly one argument expected", getRepr(), arguments.size()));
                return ctx;
            }
            ICalcValue value = arguments.get(0);
            return value.eval(ctx);
        }
        
    };
    
    public static abstract class CollectBuiltin extends BuiltinCalcValue {

        @Override
        public Context apply(Context ctx, List<? extends ICalcValue> arguments) {
            if (arguments.size() < 1) {
                ctx.setReturnedValue(ErrorValue.formatted(
                        "%s cannot be applied to %d arguments. At least 1 arguments required.",
                        getRepr(),
                        arguments.size()
                ));
                return ctx;
            }
            
            ICalcValue result = arguments.get(0);
            for (int i = 1; i < arguments.size(); i++) {
                result = collect(result, arguments.get(i));
            }
            
            ctx.setReturnedValue(result);
            return ctx;
        }
        
        protected abstract ICalcValue collect(ICalcValue lhs, ICalcValue rhs);
        
    }
    
    
    public static final ICalcValue ADD = new CollectBuiltin() {
        
        @Override
        public String getName() { return "+"; }

        @Override
        protected ICalcValue collect(ICalcValue lhs, ICalcValue rhs) {
            return lhs.add(rhs);
        }
        
    };
    
    
    public static final ICalcValue SUB = new CollectBuiltin() {
        
        @Override
        public String getName() { return "-"; }
        
        @Override
        protected ICalcValue collect(ICalcValue lhs, ICalcValue rhs) {
            return lhs.subtract(rhs);
        }
        
    };
    
    
    public static final ICalcValue MULT = new CollectBuiltin() {
        
        @Override
        public String getName() { return "*"; }

        @Override
        protected ICalcValue collect(ICalcValue lhs, ICalcValue rhs) {
            return lhs.multiply(rhs);
        }
        
    };
    
    
    public static final ICalcValue DIV = new CollectBuiltin() {
        
        @Override
        public String getName() { return "/"; }

        @Override
        protected ICalcValue collect(ICalcValue lhs, ICalcValue rhs) {
            return lhs.divide(rhs);
        }
        
    };
    
}
