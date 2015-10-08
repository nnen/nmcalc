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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author jan
 */
public abstract class BuiltinCalcValue extends CalcValue {
    
    public static void initialize(Environment env) {
        env.setVariable("let", LET);
        env.setVariable(DEF);
        
        env.setVariable(IF_ELSE);
        env.setVariable(SEQUENCE);
        
        env.setVariable(GETATTR);
        env.setVariable(SETATTR);
        
        env.setVariable(LEN);
        
        env.setVariable(LIST);
        env.setVariable(CONS);
        
        env.setVariable(APPLY);
        env.setVariable(UNAPPLY);
        env.setVariable(EVAL);
        
        env.setVariable(EQUALS);
        env.setVariable(LT);
        env.setVariable(LTE);
        env.setVariable(GT);
        env.setVariable(GTE);
        
        env.setVariable(SQRT);
    }
    
    
    public abstract String getName();
    
    @Override
    public String getRepr(ReprContext ctx) {
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
        public String getRepr(ReprContext ctx) {
            return "'(" + quotedValue.getRepr(ctx) + ")";
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
            
            String symbol = arguments.get(0).getStringValue(ctx);
            ICalcValue value = arguments.get(1);
            
            ctx.getEnvironment().setVariable(symbol, value);
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
        public String getApplyRepr(List<? extends ICalcValue> arguments, ReprContext ctx) {
            ListValue argNames = (ListValue)arguments.get(1);
            return String.format(
                    "def %s(%s) %s",
                    arguments.get(0).getExprRepr(ctx),
                    StringUtils.join(", ", argNames.getValues().stream().map(arg -> arg.getExprRepr(ctx))),
                    arguments.get(2).getExprRepr(ctx)
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
            
            SymbolValue symbol = asSymbol(arguments.get(0), ctx);
            Collection<? extends SymbolValue> argumentNames = asSymbolList(arguments.get(1), ctx);
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
    
    public static final BuiltinCalcValue CONS = new BinaryOperator("cons", "::") {
        @Override
        protected ICalcValue applyInner(ICalcValue a, ICalcValue b, Context ctx) throws NMCalcException {
            if (!b.hasLength()) {
                return CalcValue.error(
                        ctx,
                        "%s expected the second argument to be a list.",
                        getRepr(ctx.getReprContext())
                );
            }
            List<ICalcValue> items = new ArrayList();
            items.add(a);
            for (int i = 0; i < b.length(); i++) {
                items.add(b.getItem(i));
            }
            return CalcValue.list(items);
        }
        
        @Override
        protected Context unapplyInner(Context ctx, ICalcValue value) throws NMCalcException {
            if (!value.hasLength()) {
                ctx.setReturnedValue(CalcValue.error(
                        ctx,
                        "%s expected the unapplied value to be a list.",
                        getRepr(ctx.getReprContext())
                ));
                return ctx;
            }
            if (value.length() < 1) {
                ctx.setReturnedValue(CalcValue.error(
                        ctx,
                        "%s expected the unapplied value to be a non-empty list.",
                        getRepr(ctx.getReprContext())
                ));
                return ctx;
            }
            ICalcValue head = value.getItem(0);
            List<ICalcValue> tail = new ArrayList();
            for (int i = 1; i < value.length(); i++) {
                tail.add(value.getItem(i));
            }
            ctx.setReturnedValue(CalcValue.list(head, CalcValue.list(tail)));
            return ctx;
            //return super.unapplyInner(ctx, value); //To change body of generated methods, choose Tools | Templates.
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
                        getRepr(ctx.getReprContext()),
                        arguments.size()
                ));
                return ctx;
            }
            ICalcValue head = arguments.get(0);
            List<? extends ICalcValue> tail = arguments.subList(1, arguments.size());
            return head.apply(ctx, tail);
        }
        
        @Override
        protected Context unapplyInner(Context ctx, ICalcValue value) throws NMCalcException {
            return new Context(ctx, ctx.getEnvironment(), this) {
                @Override
                public ExecResult execute(Interpreter interpreter) {
                    int pc = getPC();
                    
                    switch (pc) {
                        case 0:
                        default:
                            return invalidPC(pc);
                    }
                }
            };
        }
    };
    
    public static final BuiltinCalcValue UNAPPLY = new BuiltinCalcValue() {
        @Override
        public String getName() { return "unapply"; }
        
        @Override
        public boolean isSpecialForm() { return true; }
        
        @Override
        protected Context applySpecialInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 2)) {
                return ctx;
            }
            ICalcValue value = arguments.get(0);
            ICalcValue argument = arguments.get(1);
            return value.unapply(ctx, argument);
            
            /*
            return new Context(ctx, ctx.getEnvironment(), this) {
                @Override
                public ExecResult execute(Interpreter interpreter) {
                    int pc = getPC();
                    
                    switch (pc) {
                        case 0:
                            setPC(pc + 1);
                            return ctxContinue(value.unapply(this, argument));
                        case 1:
                            setPC(pc + 1);
                        default:
                            return invalidPC(pc);
                    }
                }
            };
            */
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
                ctx.setReturnedValue(ErrorValue.formatted(
                        "%s cannot be applied to %d arguments. Exactly one argument expected",
                        getRepr(ctx.getReprContext()),
                        arguments.size()));
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
        public String getApplyRepr(List<? extends ICalcValue> arguments, ReprContext ctx) {
            ICalcValue cond = arguments.get(0);
            ICalcValue trueExpr = arguments.get(1);
            ICalcValue falseExpr = arguments.get(2);
            return String.format("if %s then %s else %s", cond.getExprRepr(ctx), trueExpr.getExprRepr(ctx), falseExpr.getExprRepr(ctx));
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
    
    
    public static final BuiltinCalcValue SEQUENCE = new BuiltinCalcValue() {
        
        @Override
        public String getName() { return "sequence"; }
        
        @Override
        public boolean isSpecialForm() { return true; }
        
        @Override
        protected Context applySpecialInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            return new Context(ctx, ctx.getEnvironment(), this) {
                @Override
                public ExecResult execute(Interpreter interpreter) {
                    int pc = getPC();
                    
                    if (pc < arguments.size()) {
                        setPC(pc + 1);
                        if ((pc > 0) && getReturnedValue().isError()) {
                            return ctxReturn(getReturnedValue());
                        }
                        return ctxContinue(arguments.get(pc).eval(this));
                    } else if (pc == arguments.size()) {
                        setPC(pc + 1);
                        return ctxReturn(getReturnedValue());
                    } else {
                        return invalidPC(pc);
                    }
                }
            };
        }
        
    };
    
    
    public static final BuiltinCalcValue GETATTR = new BuiltinCalcValue() {
        @Override
        public String getName() { return "getattr"; }

        @Override
        protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 2)) {
                return ctx;
            }
            ICalcValue obj = arguments.get(0);
            ICalcValue name = arguments.get(1);
            String nameStr = name.getStringValue(ctx);
            if (nameStr == null) {
                ctx.setReturnedValue(ErrorValue.formatted(
                        ctx,
                        "%s expected a string as second argument.",
                        getName()));
                return ctx;
            }
            
            return new Context(ctx, ctx.getEnvironment(), this) {
                @Override
                public ExecResult execute(Interpreter interpreter) {
                    int pc = getPC();
                    
                    switch (pc) {
                        case 0:
                            setPC(pc + 1);
                            return ctxContinue(obj.getAttribute(name.toString(), this));
                        case 1:
                            setPC(pc + 1);
                            return ctxReturn(getReturnedValue());
                        default:
                            return invalidPC(pc);
                    }
                }
            };
        }
    };
    
    public static final BuiltinCalcValue SETATTR = new BuiltinCalcValue() {
        @Override
        public String getName() { return "setattr"; }

        @Override
        protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 3)) {
                return ctx;
            }
            ICalcValue obj = arguments.get(0);
            ICalcValue name = arguments.get(1);
            ICalcValue value = arguments.get(2);
            String nameStr = name.getStringValue(ctx);
            if (nameStr == null) {
                ctx.setReturnedValue(ErrorValue.formatted(
                        ctx,
                        "%s expected a string as second argument.",
                        getName()));
                return ctx;
            }
            
            return new Context(ctx, ctx.getEnvironment(), this) {
                @Override
                public ExecResult execute(Interpreter interpreter) {
                    int pc = getPC();
                    
                    switch (pc) {
                        case 0:
                            setPC(pc + 1);
                            return ctxContinue(obj.setAttribute(nameStr, value, this));
                        case 1:
                            setPC(pc + 1);
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
                ctx.setReturnedValue(ErrorValue.formatted(
                        ctx,
                        "%s doesn't have length.",
                        val.getRepr(ctx.getReprContext())));
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
        public String getApplyRepr(List<? extends ICalcValue> arguments, ReprContext ctx) {
            return StringUtils.join(
                    " " + operator + " ",
                    arguments.stream().map(arg -> arg.getExprRepr(ctx))).toString();
        }
        
        @Override
        public Context apply(Context ctx, List<? extends ICalcValue> arguments) {
            if (arguments.size() < 1) {
                ctx.setReturnedValue(ErrorValue.formatted(
                        "%s cannot be applied to %d arguments. At least 1 arguments required.",
                        getRepr(ctx.getReprContext()),
                        arguments.size()
                ));
                return ctx;
            }
            
            ICalcValue result = arguments.get(0);
            for (int i = 1; i < arguments.size(); i++) {
                result = collect(result, arguments.get(i), ctx);
            }
            
            ctx.setReturnedValue(result);
            return ctx;
        }
        
        protected abstract ICalcValue collect(ICalcValue lhs, ICalcValue rhs, Context ctx);
        
    }
    
    
    public static abstract class BinaryOperator extends BuiltinCalcValue {
        private final String name;
        private final String operator;
        
        public BinaryOperator(String name, String operator) {
            this.name = name;
            this.operator = operator;
        }
        
        @Override
        public String getName() { return name; }
        
        @Override
        protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 2)) {
                return ctx;
            }
            ICalcValue a = arguments.get(0);
            ICalcValue b = arguments.get(1);
            if (a.isError()) {
                ctx.setReturnedValue(a);
            } else if (b.isError()) {
                ctx.setReturnedValue(b);
            } else {
                ctx.setReturnedValue(applyInner(a, b, ctx));
            }
            return ctx;
        }
        
        protected abstract ICalcValue applyInner(ICalcValue a, ICalcValue b, Context ctx)throws NMCalcException;
        
        @Override
        public String getApplyRepr(List<? extends ICalcValue> arguments, ReprContext ctx) {
            return StringUtils.join(
                    " " + operator + " ",
                    arguments.stream().map(arg -> arg.getExprRepr(ctx))).toString();
        }
    }
    
    
    public static final BuiltinCalcValue EQUALS = new CollectBuiltin("==") {
        @Override
        public String getName() { return "equals"; }
        
        @Override
        protected ICalcValue collect(ICalcValue lhs, ICalcValue rhs, Context ctx) {
            return CalcValue.make(lhs.isValueEqual(rhs, ctx));
        }
    };

    
    public static final BuiltinCalcValue LT = new BinaryOperator("lt", "<") {
        @Override
        protected ICalcValue applyInner(ICalcValue a, ICalcValue b, Context ctx) {
            if (a.compareValue(b, ctx) < 0) {
                return CalcValue.make(true);
            }
            return CalcValue.make(false);
        }
    };
    
    public static final BuiltinCalcValue LTE = new BinaryOperator("lte", "<=") {
        @Override
        protected ICalcValue applyInner(ICalcValue a, ICalcValue b, Context ctx) {
            if (a.compareValue(b, ctx) <= 0) {
                return CalcValue.make(true);
            }
            return CalcValue.make(false);
        }
    };
    
    public static final BuiltinCalcValue GT = new BinaryOperator("gt", ">") {
        @Override
        protected ICalcValue applyInner(ICalcValue a, ICalcValue b, Context ctx) {
            if (a.compareValue(b, ctx) > 0) {
                return CalcValue.make(true);
            }
            return CalcValue.make(false);
        }
    };
    
    public static final BuiltinCalcValue GTE = new BinaryOperator("gte", ">=") {
        @Override
        protected ICalcValue applyInner(ICalcValue a, ICalcValue b, Context ctx) {
            if (a.compareValue(b, ctx) >= 0) {
                return CalcValue.make(true);
            }
            return CalcValue.make(false);
        }
    };
    
            
    public static final ICalcValue ADD = new CollectBuiltin("+") {
        
        @Override
        public String getName() { return "+"; }
        
        @Override
        protected ICalcValue collect(ICalcValue lhs, ICalcValue rhs, Context ctx) {
            return lhs.add(rhs, ctx);
        }
        
    };
    
    
    public static final ICalcValue SUB = new CollectBuiltin("-") {
        
        @Override
        public String getName() { return "-"; }
        
        @Override
        protected ICalcValue collect(ICalcValue lhs, ICalcValue rhs, Context ctx) {
            return lhs.subtract(rhs, ctx);
        }
        
    };
    
    
    public static final ICalcValue MULT = new CollectBuiltin("*") {
        
        @Override
        public String getName() { return "*"; }

        @Override
        protected ICalcValue collect(ICalcValue lhs, ICalcValue rhs, Context ctx) {
            return lhs.multiply(rhs, ctx);
        }
        
    };
    
    
    public static final ICalcValue DIV = new CollectBuiltin("/") {
        
        @Override
        public String getName() { return "/"; }

        @Override
        protected ICalcValue collect(ICalcValue lhs, ICalcValue rhs, Context ctx) {
            return lhs.divide(rhs, ctx);
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
