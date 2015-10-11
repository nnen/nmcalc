/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *
 * @author jan
 */
public final class MathBuiltins {
    
    public static void initialize(Environment env) {
        env.setVariable("pi", CalcValue.make(Math.PI));
        env.setVariable("e", CalcValue.make(Math.E));
        
        env.setVariable(makeFloatFunction("abs", x -> x.abs()));
        env.setVariable(makeFloatFunction("ceil", x -> x.setScale(0, RoundingMode.CEILING)));
        env.setVariable(makeFloatFunction("floor", x -> x.setScale(0, RoundingMode.FLOOR)));
        
        env.setVariable(makeDoubleFunction("sin", x -> Math.sin(x.doubleValue())));
        env.setVariable(makeDoubleFunction("cos", x -> Math.cos(x.doubleValue())));
        env.setVariable(makeDoubleFunction("tan", x -> Math.tan(x.doubleValue())));
        
        env.setVariable(makeDoubleFunction("ln", x -> Math.log(x.doubleValue())));
    }
    
    
    public static BuiltinCalcValue makeFloatFunction(String name, Function<BigDecimal, BigDecimal> fn) {
        return new FloatFunction1(name) {
            @Override
            protected BigDecimal apply(BigDecimal value) throws NMCalcException {
                return fn.apply(value);
            }
        };
    }
    
    public static BuiltinCalcValue makeDoubleFunction(String name, Function<BigDecimal, Double> fn) {
        return new FloatFunction1(name) {
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

        public FloatFunction1(String name) {
            super(name);
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
    
}
