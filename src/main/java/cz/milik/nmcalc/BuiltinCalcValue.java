/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.parser.Token;
import cz.milik.nmcalc.utils.IMonad;
import cz.milik.nmcalc.utils.Monad;
import cz.milik.nmcalc.utils.StringUtils;
import cz.milik.nmcalc.utils.Utils;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author jan
 */
public abstract class BuiltinCalcValue extends CalcValue {
    
    public abstract String getName();
    
    @Override
    public String getRepr() {
        //return "$" + getName();
        return "<builtin " + getName() + ">";
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
    
    
    public static class QuoteValue extends BuiltinCalcValue {

        private final ICalcValue quotedValue;
        
        public QuoteValue(ICalcValue quotedValue) {
            this.quotedValue = quotedValue;
        }
        
        @Override
        public String getName() {
            return "quote";
        }
        
        @Override
        public String getRepr() {
            return "'(" + quotedValue.getRepr() + ")";
        }
        
        @Override
        public Context eval(Context ctx) {
            ctx.setReturnedValue(quotedValue);
            return ctx;
        }
        
    }
    
    
    public static final ICalcValue LET = new BuiltinCalcValue() {
        @Override
        public String getName() { return "let"; }

        @Override
        public Context apply(Context ctx, List<? extends ICalcValue> arguments) {
            if (!checkArguments(ctx, arguments, 2)) {
                return ctx;
            }
            
            IMonad<String> symbol = arguments.get(0).getStringValue();
            ICalcValue value = arguments.get(1);
            
            symbol.bind(symbolValue -> {
                System.err.printf("Symbol value: %s\n", symbolValue);
                ctx.getEnvironment().setVariable(symbolValue, value);
                /*
                if (ctx.getParent() == null) {
                    ctx.getEnvironment().setVariable(symbolValue, value);
                } else {
                    ctx.getParent().setVariable(symbolValue, value);
                }
                */
                //ctx.setVariable(symbolValue, value);
            });
            
            ctx.getEnvironment().dump(System.err);
            
            ctx.setReturnedValue(value);
            
            return ctx;
        }
    };
    
    
    public static final BuiltinCalcValue DEF = new BuiltinCalcValue() {
        @Override
        public String getName() {
            return "def";
        }

        @Override
        public String getApplyRepr(List<? extends ICalcValue> arguments) {
            ListValue argNames = (ListValue)arguments.get(1);
            return String.format(
                    "def %s(%s) %s",
                    arguments.get(0).getExprRepr(),
                    StringUtils.join(", ", argNames.getValues().stream().map(arg -> arg.getExprRepr())),
                    arguments.get(2).getExprRepr()
            );
        }
        
        @Override
        public boolean isSpecialForm() {
            return true;
        }
        
        @Override
        public Context applySpecialInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 3)) {
                return ctx;
            }
            
            SymbolValue symbol = asSymbol(arguments.get(0));
            Collection<? extends SymbolValue> argumentNames = asSymbolList(arguments.get(1));
            ICalcValue body = arguments.get(2);
            
            FunctionValue fn = new FunctionValue(symbol, body, ctx, argumentNames);
            ctx.setVariable(symbol.getValue(), fn);
            ctx.setReturnedValue(fn);
            
            return ctx;
        }
        
        /*
        @Override
        public Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 3)) {
                return ctx;
            }
            
            SymbolValue symbol = asSymbol(arguments.get(0));
            Collection<? extends SymbolValue> argumentNames = asSymbolList(arguments.get(1));
            ICalcValue body = arguments.get(2);
            
            FunctionValue fn = new FunctionValue(symbol, body, ctx, argumentNames);
            ctx.setVariable(symbol.getValue(), fn);
            ctx.setReturnedValue(fn);
            
            return ctx;
        }
        */
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
    
    
    public static final BuiltinCalcValue IF_ELSE = new BuiltinCalcValue() {

        @Override
        public String getName() {
            return "if_else";
        }
        
        @Override
        public String getApplyRepr(List<? extends ICalcValue> arguments) {
            ICalcValue cond = arguments.get(0);
            ICalcValue trueExpr = arguments.get(1);
            ICalcValue falseExpr = arguments.get(2);
            return String.format("if %s then %s else %s", cond.getExprRepr(), trueExpr.getExprRepr(), falseExpr.getExprRepr());
        }
        
        @Override
        public boolean isSpecialForm() { return true; }
        
        @Override
        public Context applySpecial(Context ctx, List<? extends ICalcValue> arguments) {
            if (!checkArguments(ctx, arguments, 3)) {
                return ctx;
            }
            
            final ICalcValue condition = arguments.get(0);
            final ICalcValue trueExpr = arguments.get(1);
            final ICalcValue falseExpr = arguments.get(2);
            
            return new Context(ctx, ctx.getEnvironment(), this) {
                @Override
                public ExecResult execute(Interpreter interpreter) {
                    int pc = getPC();
                    
                    switch (pc) {
                        case 0:
                            setPC(pc + 1);
                            return eval(interpreter, condition);
                        case 1:
                            setPC(pc + 1);
                            if (getReturnedValue().getBooleanValue()) {
                                return eval(interpreter, trueExpr);
                            } else {
                                return eval(interpreter, falseExpr);
                            }
                        case 2:
                            return ctxReturn(getReturnedValue());
                        default:
                            return invalidPC(pc);
                    }
                }
            };
        }
        
    };
    
    
    
    public static final BuiltinCalcValue LEN = new BuiltinCalcValue() {
        @Override
        public String getName() { return "len"; }

        @Override
        protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 1)) {
                return ctx;
            }
            ICalcValue val = arguments.get(0);
            if (val.hasLength()) {
                ctx.setReturnedValue(CalcValue.make(val.length()));
            } else {
                ctx.setReturnedValue(ErrorValue.formatted(ctx, "%s doesn't have length.", val.getRepr()));
            }
            return ctx;
        }
    };
    
    
    
    public static abstract class CollectBuiltin extends BuiltinCalcValue {
        
        private final String operator;

        public CollectBuiltin(String operator) {
            this.operator = operator;
        }
        
        @Override
        public String getApplyRepr(List<? extends ICalcValue> arguments) {
            return StringUtils.join(" " + operator + " ", arguments.stream().map(arg -> arg.getExprRepr())).toString();
        }
        
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
    
    
    public static final ICalcValue ADD = new CollectBuiltin("+") {
        
        @Override
        public String getName() { return "+"; }

        @Override
        protected ICalcValue collect(ICalcValue lhs, ICalcValue rhs) {
            return lhs.add(rhs);
        }
        
    };
    
    
    public static final ICalcValue SUB = new CollectBuiltin("-") {
        
        @Override
        public String getName() { return "-"; }
        
        @Override
        protected ICalcValue collect(ICalcValue lhs, ICalcValue rhs) {
            return lhs.subtract(rhs);
        }
        
    };
    
    
    public static final ICalcValue MULT = new CollectBuiltin("*") {
        
        @Override
        public String getName() { return "*"; }

        @Override
        protected ICalcValue collect(ICalcValue lhs, ICalcValue rhs) {
            return lhs.multiply(rhs);
        }
        
    };
    
    
    public static final ICalcValue DIV = new CollectBuiltin("/") {
        
        @Override
        public String getName() { return "/"; }

        @Override
        protected ICalcValue collect(ICalcValue lhs, ICalcValue rhs) {
            return lhs.divide(rhs);
        }
        
    };
    
    
    public static final BuiltinCalcValue SQRT = new BuiltinCalcValue() {
        @Override
        public String getName() {
            return "sqrt";
        }
        
        @Override
        public Context apply(Context ctx, List<? extends ICalcValue> arguments) {
            if (!checkArguments(ctx, arguments, 1)) {
                return ctx;
            }
            
            Float value = arguments.get(0).getFloatValue().unwrap();
            
            if (value == null) {
                ctx.setReturnedValue(ErrorValue.formatted("Cannot convert %s to float.", arguments.get(0).getFloatValue()));
            } else {
                ctx.setReturnedValue(CalcValue.make((float)Math.sqrt(value)));
            }
            
            /*
            ctx.<ICalcValue>setReturnedValue(value.unwrap(fltValue -> {
                return CalcValue.make((float)Math.sqrt(fltValue));
            }, (Supplier<ICalcValue>)(() -> { 
                return ErrorValue.formatted("Cannot convert %s to float.", arguments.get(0).getFloatValue());
            }));
                    */
            
            return ctx;
        }
    };
    
}
