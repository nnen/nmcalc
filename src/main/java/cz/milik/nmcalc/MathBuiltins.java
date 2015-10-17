/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.nevec.rjm.BigDecimalMath;
import org.nevec.rjm.BigIntegerMath;

/**
 *
 * @author jan
 */
public final class MathBuiltins {
    
    public static void initialize(Environment env) {
        env.setVariable(
                "pi",
                CalcValue.make(BigDecimalMath.pi(MathContext.UNLIMITED)));
        env.setVariable(
                "e",
                CalcValue.make(BigDecimalMath.exp(MathContext.UNLIMITED)));
        
        env.setVariable(makeFloatFunction(
                "abs",
                "**`abs(x)`**\n\nReturns the absolute value of `x`.",
                x -> x.abs()));
        env.setVariable(makeFloatFunction(
                "ceil",
                "**`ceil(x)`**\n\nReturns `x` rounded up.",
                x -> x.setScale(0, RoundingMode.CEILING)));
        env.setVariable(makeFloatFunction(
                "floor",
                "**`floor(x)`**\n\nReturns `x` rounded down.",
                x -> x.setScale(0, RoundingMode.FLOOR)));
        
        env.setVariable(makeFloatFunction(
                "sin",
                "**`sin(x)`**\n\nReturns the sine of `x`.",
                x -> BigDecimalMath.sin(x)));
        env.setVariable(makeFloatFunction(
                "asin",
                "**`asin(x)`**\n\nReturns the arc sine of `x`.",
                x -> BigDecimalMath.asin(x)));
        env.setVariable(makeFloatFunction(
                "cos",
                "**`cos(x)`**\n\nReturns the cosine of `x`.",
                x -> BigDecimalMath.cos(x)));
        env.setVariable(makeFloatFunction(
                "acos",
                "**`acos(x)`**\n\nReturns the arc cosine of `x`.",
                x -> BigDecimalMath.acos(x)));
        env.setVariable(makeFloatFunction(
                "tan",
                "**`tan(x)`**\n\nReturns the tangent of `x`.",
                x -> BigDecimalMath.tan(x)));
        env.setVariable(makeFloatFunction(
                "atan",
                "**`atan(x)`**\n\nReturns the arc tangent of `x`.",
                x -> BigDecimalMath.atan(x)));
        
        env.setVariable(POW);
        env.setVariable(makeFloatFunction(
                "sqrt",
                "**`sqrt(x)`**\n\nReturns the square root of `x`.",
                x -> BigDecimalMath.sqrt(x)));
        env.setVariable(makeFloatFunction(
                "ln",
                "**`ln(x)`**\n\nReturns the natural logarithm of `x`.",
                x -> BigDecimalMath.log(x)));
        
        env.setVariable(makeIntegerFunction(
                "lcm",
                "**`lcm(a, b)`**\n\nReturns least common denominator of `a` and `b`.",
                (a, b) -> BigIntegerMath.lcm(a, b)));
    }
    
    
    public static final BuiltinCalcValue POW = makeFloatFunction(
            "pow",
            (b, e) -> BigDecimalMath.pow(b, e));
    
    
    public static BuiltinCalcValue makeFloatFunction(String name, String help, Function<BigDecimal, BigDecimal> fn) {
        return new FloatFunction1(name, help) {
            @Override
            protected BigDecimal apply(BigDecimal value) throws NMCalcException {
                return fn.apply(value);
            }
        };
    }
    
    public static BuiltinCalcValue makeDoubleFunction(String name, String help, Function<BigDecimal, Double> fn) {
        return new FloatFunction1(name, help) {
            @Override
            protected BigDecimal apply(BigDecimal value) throws NMCalcException {
                return new BigDecimal(fn.apply(value));
            }
        };
    }

    public static BuiltinCalcValue makeFloatFunction(String name, BiFunction<BigDecimal, BigDecimal, BigDecimal> fn) {
        return new FloatFunction2(name) {
            @Override
            protected BigDecimal apply(Context ctx, BigDecimal a, BigDecimal b) throws NMCalcException {
                return fn.apply(a, b);
            }
        };
    }
    
    public static BuiltinCalcValue makeIntegerFunction(String name, String help, BiFunction<BigInteger, BigInteger, BigInteger> fn) {
        return new IntegerFunction2(name, help) {
            @Override
            protected BigInteger apply(Context ctx, BigInteger a, BigInteger b) throws NMCalcException {
                return fn.apply(a, b);
            }
        };
    }
    
    
    public abstract static class MathFunction1 extends BuiltinCalcValue {

        private String name;
        
        @Override
        public String getName() { return name; }
        
        public MathFunction1(String name) {
            this.name = name;
        }
        
        @Override
        protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!this.checkArguments(ctx, arguments, 1)) {
                return ctx;
            }
            ICalcValue input = arguments.get(0);
            if (input.hasLength()) {
                List<ICalcValue> result = new ArrayList();
                for (int i = 0; i < input.length(); i++) {
                    result.add(apply(ctx, input.getItem(i)));
                }
                ctx.setReturnedValue(CalcValue.list(result));
            } else {
                ctx.setReturnedValue(apply(ctx, input));
            }
            return ctx;
        }
        
        protected abstract ICalcValue apply(Context ctx, ICalcValue item) throws NMCalcException;
        
    }
    
    public static abstract class FloatFunction1 extends BuiltinCalcValue.UnaryFunction {

        private final String help;

        @Override
        protected Optional<String> getHelpInner() {
            return Optional.ofNullable(help);
        }
        
        public FloatFunction1(String name, String help) {
            super(name);
            this.help = help;
        }
        
        @Override
        protected Context applyInner(Context ctx, ICalcValue argument) throws NMCalcException {
            ctx.setReturnedValue(
                    CalcValue.make(apply(argument.getDecimalValue())));
            return ctx;
        }
        
        protected abstract BigDecimal apply(BigDecimal value) throws NMCalcException;
    }
    
    public static abstract class FloatFunction2 extends BuiltinCalcValue {
        
        private final String name;

        public String getName() {
            return name;
        }

        public FloatFunction2(String name) {
            this.name = name;
        }
        
        @Override
        protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 2)) {
                return ctx;
            }
            ICalcValue a = arguments.get(0);
            ICalcValue b = arguments.get(1);
            if (a.isError()) {
                ctx.setReturnedValue(a);
                return ctx;
            }
            if (b.isError()) {
                ctx.setReturnedValue(b);
                return ctx;
            }
            ctx.setReturnedValue(CalcValue.make(apply(ctx, a.getDecimalValue(), b.getDecimalValue())));
            return ctx;
        }
        
        protected abstract BigDecimal apply(Context ctx, BigDecimal a, BigDecimal b) throws NMCalcException;
        
    }
    
    public static abstract class IntegerFunction2 extends BuiltinCalcValue {
        
        private String name;
        private String help;
        
        public String getName() {
            return name;
        }
        
        @Override
        protected Optional<String> getHelpInner() {
            return Optional.ofNullable(help);
        }
        
        public IntegerFunction2(String name, String help) {
            this.name = name;
            this.help = help;
        }

        @Override
        protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
            if (!checkArguments(ctx, arguments, 2)) {
                return ctx;
            }
            ICalcValue a = arguments.get(0);
            ICalcValue b = arguments.get(1);
            if (a.isError()) {
                ctx.setReturnedValue(a);
                return ctx;
            }
            if (b.isError()) {
                ctx.setReturnedValue(b);
                return ctx;
            }
            ctx.setReturnedValue(CalcValue.make(apply(ctx, a.getDecimalValue().toBigInteger(), b.getDecimalValue().toBigInteger())));
            return ctx;
        }
        
        protected abstract BigInteger apply(Context ctx, BigInteger a, BigInteger b) throws NMCalcException;
        
    }
}
