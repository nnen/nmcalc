/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author jan
 */
public final class MathBuiltins {
    
    public static void initialize(Environment env) {
        env.setVariable("pi", CalcValue.make(Math.PI));
        env.setVariable("e", CalcValue.make(Math.E));
        
        env.setVariable(makeFloatFunction("abs", x -> Math.abs(x)));
        env.setVariable(makeFloatFunction("ceil", x -> Math.ceil(x)));
        env.setVariable(makeFloatFunction("floor", x -> Math.floor(x)));
        
        env.setVariable(makeFloatFunction("sin", x -> Math.sin(x)));
        env.setVariable(makeFloatFunction("cos", x -> Math.cos(x)));
        env.setVariable(makeFloatFunction("tan", x -> Math.tan(x)));
        
        env.setVariable(makeFloatFunction("ln", x -> Math.log(x)));
    }
    
    public static BuiltinCalcValue makeFloatFunction(String name, Function<Double, Double> fn) {
        return new FloatFunction1(name) {
            @Override
            protected double apply(double value) throws NMCalcException {
                return fn.apply(value);
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
                    result.add(apply(input.getItem(i)));
                }
                ctx.setReturnedValue(CalcValue.list(result));
            } else {
                ctx.setReturnedValue(apply(input));
            }
            return ctx;
        }
        
        protected abstract ICalcValue apply(ICalcValue item) throws NMCalcException;
        
    }
    
    public static abstract class FloatFunction1 extends MathFunction1 {
        public FloatFunction1(String name) {
            super(name);
        }
        
        @Override
        protected ICalcValue apply(ICalcValue item) throws NMCalcException {
            ICalcValue floatValue = item.toFloat();
            if (floatValue.isError()) {
                return floatValue;
            }
            double value = floatValue.getDoubleValue();
            return CalcValue.make(apply(value));
        }
        
        protected abstract double apply(double value) throws NMCalcException;
    }
    
    
    /*
    public static final BuiltinCalcValue SIN = new FloatFunction1("sin") {
        @Override
        protected double apply(double value) throws NMCalcException {
            return Math.sin(value);
        }
    };
    
    public static final BuiltinCalcValue COS = new FloatFunction1("cos") {
        @Override
        protected double apply(double value) throws NMCalcException {
            return Math.sin(value);
        }
    };
    
    public static final BuiltinCalcValue TAN = new FloatFunction1("tan") {
        @Override
        protected double apply(double value) throws NMCalcException {
            return Math.sin(value);
        }
    };
    */
    
}
