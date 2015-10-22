/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.loader.CalcLoader;
import cz.milik.nmcalc.parser.Token;
import cz.milik.nmcalc.utils.IMonad;
import cz.milik.nmcalc.utils.Monad;
import cz.milik.nmcalc.utils.StringUtils;
import cz.milik.nmcalc.utils.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author jan
 */
public abstract class BuiltinCalcValue extends CalcValue {
    
    public static void initialize(Environment env) {
        env.setVariable("let", LET);
        env.setVariable(DEF);
        env.setVariable(DEFMACRO);
        
        env.setVariable(IF_ELSE);
        env.setVariable(MATCH);
        env.setVariable(SEQUENCE);
        
        env.setVariable(GETATTR);
        env.setVariable(SETATTR);
        
        env.setVariable(LEN);
        
        env.setVariable(SOME);
        
        env.setVariable(QUOTE);
        env.setVariable(LIST);
        env.setVariable(CONS);
        env.setVariable(HEAD);
        env.setVariable(TAIL);
        
        env.setVariable(DICT);
        env.setVariable(GET_ITEM);
        env.setVariable(SET_ITEM);
        
        env.setVariable(APPLY);
        env.setVariable(UNAPPLY);
        env.setVariable(EVAL);
        
        env.setVariable(SUBSTITUTE);
        
        env.setVariable(EQUALS);
        env.setVariable(LT);
        env.setVariable(LTE);
        env.setVariable(GT);
        env.setVariable(GTE);
        
        env.setVariable(HEX);
        env.setVariable(OCT);
        env.setVariable(BIN);
        
        env.setVariable(ENV);
        
        env.setVariable(HELP);
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
    
    public static final BuiltinCalcValue DEFMACRO = new BuiltinCalcValue() {
        @Override
        public String getName() { return "defmacro"; }

        @Override
        protected Context applySpecialInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 3, 4)) {
                return ctx;
            }
            
            SymbolValue name = CalcValue.asSymbol(arguments.get(0), ctx);
            List<? extends SymbolValue> argNames = CalcValue.asSymbolList(arguments.get(1), ctx);
            ICalcValue body;
            String help = null;
            
            if (arguments.size() == 3) {
                body = arguments.get(2);
            } else {
                help = arguments.get(2).getStringValue(ctx);
                body = arguments.get(3);
            }
            
            ICalcValue macro = new MacroValue(name, argNames, help, body);
            
            ctx.setVariable(name.getValue(), macro);
            ctx.setReturnedValue(macro);
            return ctx;
        }
        
        @Override
        public boolean isSpecialForm() { return true; }
    };
    
    
    public static final BuiltinCalcValue QUOTE = new BuiltinCalcValue() {
        @Override
        public String getName() { return "quote"; }

        @Override
        public String getApplyRepr(List<? extends ICalcValue> arguments, ReprContext ctx) {
            if (arguments.size() != 1) {
                return super.getApplyRepr(arguments, ctx);
            }
            return "'(" + arguments.get(0).getExprRepr(ctx) + ")";
        }
        
        @Override
        public boolean isSpecialForm() { return true; }
        
        @Override
        protected Context applySpecialInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 1)) {
                return ctx;
            }
            ctx.setReturnedValue(arguments.get(0));
            return ctx;
        }
    };
    
    public static final BuiltinCalcValue LIST = new BuiltinCalcValue() {  
        @Override
        public String getName() {
            return "list";
        }
        
        @Override
        protected Optional<String> getHelpInner() {
            return Optional.of(
                    "**`list(...) => [...]`**\n\n" +
                    "Constructs a list from the arguments passed to this " +
                    "function. Examples:\n\n" +
                    "    list(1, 2, 3) => [1, 2, 3]\n" +
                    "    list([1], [2], 3) => [[1], [2], 3]"
            );
        }
        
        @Override
        public Context apply(Context ctx, List<? extends ICalcValue> arguments) {
            ctx.setReturnedValue(CalcValue.list(arguments));
            return ctx;
        }
        
        @Override
        protected Context unapplyInner(Context ctx, ICalcValue value) throws NMCalcException {
            if (value.isList()) {
                ctx.setReturnedValue(CalcValue.some(value));
            } else {
                ctx.setReturnedValue(CalcValue.nothing());
            }
            return ctx;
        }
        
        @Override
        public String getApplyRepr(List<? extends ICalcValue> arguments, ReprContext ctx) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append(StringUtils.join(", ", arguments.stream().map(arg -> arg.getExprRepr(ctx))));
            sb.append("]");
            return sb.toString();
        }
        
    };
    
    public static final BuiltinCalcValue CONS = new BinaryOperator("cons", "::") {
        @Override
        protected Optional<String> getHelpInner() {
            return Optional.of(
                    "**`cons(head, tail) => head :: tail`**\n\n" +
                    "Constructs a new list from `head` as its first element and " +
                    "`tail` as the rest of the elements. The **`::`** operator " +
                    "is just a syntactic sugar for this function.\n\n" +
                    "Both the `cons` function and the `::` operator can be " +
                    "used as patterns in the `match` construct.\n\n" +
                    "Examples:\n\n" +
                    "    cons(1, [2, 3, 4]) => [1, 2, 3, 4]\n" +
                    "    1 :: [2, 3, 4] => [1, 2, 3, 4]\n" +
                    "    cons([1], [2, 3, 4]) => [[1], 2, 3, 4]\n" +
                    "    [1] :: [2, 3, 4] => [[1], 2, 3, 4]\n" +
                    "    cons(1, []) => [1]"
            );
        }
        
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
                ctx.setReturnedValue(CalcValue.nothing());
                return ctx;
            }
            if (value.length() < 1) {
                ctx.setReturnedValue(CalcValue.nothing());
                return ctx;
            }
            ICalcValue head = value.getItem(0);
            List<ICalcValue> tail = new ArrayList();
            for (int i = 1; i < value.length(); i++) {
                tail.add(value.getItem(i));
            }
            ctx.setReturnedValue(CalcValue.some(CalcValue.list(head, CalcValue.list(tail))));
            return ctx;
        }
    };
    
    public static final BuiltinCalcValue HEAD = new UnaryFunction("head") {
        @Override
        protected Context applyInner(Context ctx, ICalcValue argument) throws NMCalcException {
            return argument.getHead(ctx);
        }
    };
    
    public static final BuiltinCalcValue TAIL = new UnaryFunction("tail") {
        @Override
        protected Context applyInner(Context ctx, ICalcValue argument) throws NMCalcException {
            return argument.getTail(ctx);
        }
    };
    
    
    public static final BuiltinCalcValue DICT = new BuiltinCalcValue() {
        @Override
        public String getName() { return "dict"; }

        @Override
        protected Optional<String> getHelpInner() {
            return makeHelp(
                    "dict([entries])",
                    "Constructs a new mutable "
                            + "dictionary using the key-value pairs in "
                            + "`entries` if `entries` is given, otherwise "
                            + "constructs a new empty dictionary.\n\n"
                            + "Example:\n\n"
                            + "    dict([[1, \"apple\"], [16, \"banana\"]]) => { 1: \"apple\", 16: \"banana\" }"
            );
        }
        
        @Override
        protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 0, 1)) {
                return ctx;
            }
            
            if (arguments.size() == 0) {
                ctx.setReturnedValue(CalcValue.dict());
                return ctx;
            }
            
            final ICalcValue arg = arguments.get(0);
            requireLength(ctx, arg);
            
            final MapValue result = new MapValue();
            
            for (int i = 0; i < arg.length(); i++) {
                ICalcValue item = arg.getItem(i);
                requireLength(ctx, item, 2);
                ICalcValue key = item.getItem(0);
                ICalcValue value = item.getItem(1);
                result.setItem(key, value);
            }
            
            ctx.setReturnedValue(result);
            return ctx;
        }
    };
    
    public static final BuiltinCalcValue GET_ITEM = new BuiltinCalcValue() {
        @Override
        public String getName() { return "get_item"; }

        @Override
        protected Optional<String> getHelpInner() {
            return makeHelp(
                    "get_item(value, key)",
                    "Returns `value[key]`, the item of `value` indexed by "
                            + "`key`. For dictionaries, this returns the value "
                            + "mapped under `key`. For lists, this returns "
                            + "`key`-th element if `key` is a number.",
                    "If `value` doesn't have any item under index `key`, "
                            + "error is returned.",
                    "Example:",
                    "    d = { 2: \"apple\", 4: \"banana\", 8: \"citrus\" }\n"
                            + "    get_item(d, 4) => \"banana\"\n"
                            + "    get_item({}, 4) => error(...)"
            );
        }
        
        @Override
        protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 2)) {
                return ctx;
            }
            
            ICalcValue dict = arguments.get(0);
            ICalcValue key = arguments.get(1);
            
            return dict.getItem(ctx, key);
        }
    };
    
    public static final BuiltinCalcValue SET_ITEM = new BuiltinCalcValue() {
        @Override
        public String getName() { return "set_item"; }

        @Override
        protected Optional<String> getHelpInner() {
            return makeHelp(
                    "set_item(collection, key, value)",
                    "Sets an indexed item of a collection (dictionary, list, "
                            + "possibly others). Returns the assigned value.",
                    "Example:",
                    "    d = {}\n"
                            + "    set_item(d, \"pi\", 3.1415) => 3.1415\n"
                            + "    d => { \"pi\": 3.1415 }\n\n"
            );
        }
        
        @Override
        protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 3)) {
                return ctx;
            }
            
            ICalcValue dict = arguments.get(0);
            ICalcValue key = arguments.get(1);
            ICalcValue value = arguments.get(2);
            
            return dict.setItem(ctx, key, value);
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
        public boolean isSpecialForm() { return false; }
        
        @Override
        protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
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
    
    
    public static final BuiltinCalcValue SUBSTITUTE = new BuiltinCalcValue() {

        @Override
        public String getName() { return "substitute"; }

        @Override
        protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 3)) {
                return ctx;
            }
            
            ICalcValue pattern = arguments.get(0);
            ICalcValue value = arguments.get(1);
            ICalcValue replacement = arguments.get(2);
            
            return pattern.substitute(ctx, value, replacement);
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
    
    public static final BuiltinCalcValue MATCH = new BuiltinCalcValue() {

        @Override
        public String getName() { return "match"; }
        
        @Override
        protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 2)) {
                return ctx;
            }
            
            ICalcValue matchedValue = arguments.get(0);
            ICalcValue matches = arguments.get(1);
            
            if (!matches.isList()) {
                return ctx.setReturnedError(
                        "Expected the second argument to be a list, got: %s.",
                        matches.getRepr(ctx.getReprContext())
                );
            }
            
            if (matches.length() == 0) {
                ctx.setReturnedValue(CalcValue.nothing());
                return ctx;
            }
            
            for (int i = 0; i < matches.length(); i++) {
                ICalcValue item = matches.getItem(i);
                if (!item.isList() || (item.length() != 2)) {
                    return ctx.setReturnedError(
                            "Expected the %dth item of the second argument to be a 2-item list. Got: %s.",
                            item.getRepr(ctx.getReprContext())
                    );
                }
            }
            
            return new Context(ctx, ctx.getEnvironment(), this) {
                private final Environment originalEnv = getEnvironment();
                
                @Override
                public ExecResult execute(Interpreter interpreter) {
                    int pc = getPC();
                    ICalcValue returned;
                    ICalcValue body;
                    
                    if (pc == 0) {
                        setPC(pc + 1);
                        return match(matches.getItem(pc), matchedValue);
                    } else if (pc < matches.length()) {
                        returned = getReturnedValue();
                        if (returned.isSome()) {
                            body = matches.getItem(pc - 1).getItem(1);
                            setPC(matches.length() + 1);
                            return ctxContinue(interpreter.eval(body, this));
                        }
                        setPC(pc + 1);
                        return match(matches.getItem(pc), matchedValue);
                    } else if (pc == matches.length()) {
                        returned = getReturnedValue();
                        if (returned.isSome()) {
                            body = matches.getItem(pc - 1).getItem(1);
                            setPC(matches.length() + 1);
                            return ctxContinue(interpreter.eval(body, this));
                        }
                        setPC(matches.length() + 2);
                        return ctxReturn(CalcValue.nothing());
                    } else if (pc == matches.length() + 1) {
                        return ctxReturn(getReturnedValue());
                    } else {
                        return invalidPC(pc);
                    }
                }
                
                private ExecResult match(ICalcValue item, ICalcValue value) {
                    setEnvironment(originalEnv.createChild());
                    ICalcValue pattern = item.getItem(0);
                    return ctxContinue(pattern.unapply(this, value));
                }
            };
        }
        
        @Override
        public String getApplyRepr(List<? extends ICalcValue> arguments, ReprContext ctx) {
            StringBuilder sb = new StringBuilder();
            
            sb.append("match ");
            sb.append(arguments.get(0).getExprRepr(ctx));
            sb.append(" { ");
            
            ICalcValue matches = arguments.get(1).getItem(1);
            for (int i = 0; i < matches.length(); i++) {
                ICalcValue pattern = matches.getItem(i).getItem(0);
                ICalcValue body = matches.getItem(i).getItem(1);
                
                sb.append("case ");
                sb.append(pattern.getExprRepr(ctx));
                sb.append(" -> ");
                sb.append(body.getExprRepr(ctx));
                sb.append(" ");
            }
            
            sb.append("}");
            
            return sb.toString();
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
            SymbolValue name = CalcValue.asSymbol(arguments.get(1), ctx);
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
                            return ctxContinue(obj.getAttribute(name, this));
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
            SymbolValue name = CalcValue.asSymbol(arguments.get(1), ctx);
            ICalcValue value = arguments.get(2);
            
            return new Context(ctx, ctx.getEnvironment(), this) {
                @Override
                public ExecResult execute(Interpreter interpreter) {
                    int pc = getPC();
                    
                    switch (pc) {
                        case 0:
                            setPC(pc + 1);
                            return ctxContinue(obj.setAttribute(name, value, this));
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
    
    
    public static final BuiltinCalcValue SOME = new UnaryFunction("some") {
        @Override
        protected Context applyInner(Context ctx, ICalcValue argument) throws NMCalcException {
            ctx.setReturnedValue(CalcValue.some(argument));
            return ctx;
        }
        
        @Override
        protected Context unapplyInner(Context ctx, ICalcValue value) throws NMCalcException {
            if (!value.isSome()) {
                ctx.setReturnedValue(CalcValue.nothing());
                return ctx;
            }
            ctx.setReturnedValue(CalcValue.some(CalcValue.list(value.unwrap(ctx))));
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
    
    public static abstract class UnaryFunction extends BuiltinCalcValue {
        private final String name;
        
        @Override
        public String getName() {
            return name;
        }

        public UnaryFunction(String name) {
            this.name = name;
        }
        
        @Override
        protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 1)) {
                return ctx;
            }
            ICalcValue argument = arguments.get(0);
            if (argument.isError()) {
                ctx.setReturnedValue(argument);
                return ctx;
            }
            return applyInner(ctx, argument);
        }
        
        protected abstract Context applyInner(Context ctx, ICalcValue argument) throws NMCalcException;
    }
    
    public static abstract class UnaryOperator extends BuiltinCalcValue {
        private final String name;

        @Override
        public String getName() {
            return name;
        }

        public UnaryOperator(String name) {
            this.name = name;
        }
        
        @Override
        protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 1)) {
                return ctx;
            }
            ICalcValue argument = arguments.get(0);
            if (argument.isError()) {
                ctx.setReturnedValue(argument);
                return ctx;
            }
            ctx.setReturnedValue(applyInner(ctx, argument));
            return ctx;
        }
        
        protected abstract ICalcValue applyInner(Context ctx, ICalcValue argument) throws NMCalcException;
        
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
    
    
    
    public static class ReprFlagUnaryOperator extends UnaryOperator {
        private final ReprContext.Flags flag;

        public ReprFlagUnaryOperator(ReprContext.Flags flag, String name) {
            super(name);
            this.flag = flag;
        }

        @Override
        protected ICalcValue applyInner(Context ctx, ICalcValue argument) throws NMCalcException {
            ReprContext reprCtx = ctx.getReprContext().copy().addFlags(flag);
            return new ReprCtxValue(argument, reprCtx);
        }
    }
    
    public static final BuiltinCalcValue HEX = new ReprFlagUnaryOperator(ReprContext.Flags.HEX, "hex");
    public static final BuiltinCalcValue OCT = new ReprFlagUnaryOperator(ReprContext.Flags.OCTAL, "oct");
    public static final BuiltinCalcValue BIN = new ReprFlagUnaryOperator(ReprContext.Flags.BINARY, "bin");
    
    
    public static final BuiltinCalcValue ENV = new BuiltinCalcValue() {

        @Override
        public String getName() { return "env"; }
        
        @Override
        protected Optional<String> getHelpInner() {
            return Optional.of("**`env()`**\n\nReturns a dictionary of all symbols (variables) defined in the current environment.");
        }
        
        @Override
        protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 0)) {
                return ctx;
            }
            
            Environment env = ctx.getEnvironment();
            List<String> varNames = env.getVariableNames();
            
            ICalcValue result = CalcValue.dict();
            
            for (String varName : varNames) {
                result.setItem(
                        CalcValue.makeSymbol(varName),
                        env.getVariable(varName).unwrap()
                );
            }
            
            ctx.setReturnedValue(result);
            
            return ctx;
        }
        
    };
            
    
    public static final BuiltinCalcValue HELP = new BuiltinCalcValue() {
        
        public static final String HELP_FILE = "cz/milik/nmcalc/help.md";
        
        @Override
        public String getName() {
            return "help";
        }
        
        @Override
        protected Optional<String> getHelpInner() {
            return Optional.of("**`help([value])`**\n\nIf `value` is ommitted, returns the help for NMCalc."
                    + " If `value` is a string, returns a help page with that name if it exists, otherwise returns `nothing`."
                    + " If `value` is any other object (such as a builtin function), returns the help for that object, if it"
                    + " exists, otherwise returns `nothing`.\n\nExample usage:\n\n    help(lcm) => <help for the lcm function>");
        }
        
        @Override
        protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 0, 1)) {
                return ctx;
            }
            
            if (arguments.size() == 0) {
                ClassLoader loader = getClass().getClassLoader();
                InputStream stream = loader.getResourceAsStream(HELP_FILE);
                try {
                    ICalcValue result = CalcValue.make(Utils.readAll(stream));
                    result.addAnnotation(CalcAnnotation.isHelp());
                    ctx.setReturnedValue(result);
                } catch (IOException e) {
                    ctx.setReturnedValue(CalcValue.error(ctx, e));
                }
            } else {
                ICalcValue arg = arguments.get(0);
                
                Optional<String> help = Optional.empty();
                
                if (arg instanceof StringValue) {
                    help = Optional.ofNullable(CalcLoader.getInstance().getString(arg.getStringValue(ctx)));
                } else {
                    help = arguments.get(0).getHelp();
                }
                
                if (!help.isPresent()) {
                    ctx.setReturnedValue(CalcValue.nothing());
                    return ctx;
                }
                
                ICalcValue result = CalcValue.make(help.get());
                result.addAnnotation(CalcAnnotation.isHelp());
                ctx.setReturnedValue(result);
            }
            
            return ctx;
        }
        
    };
    
}
