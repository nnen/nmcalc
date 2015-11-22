/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.values.CalcValue;
import cz.milik.nmcalc.values.FunctionValue;
import cz.milik.nmcalc.values.ICalcValue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jan
 */
public class InterpreterTest {
    
    private Interpreter interpreter;
    
    public InterpreterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        interpreter = new Interpreter();
    }
    
    @After
    public void tearDown() {
        interpreter = null;
    }
    
    protected void testEvaluate(String input, ICalcValue expectedResult) {
        ICalcValue actualResult = interpreter.evaluate(input);
        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
    }
    
    /**
     * Test of evaluate method, of class Interpreter.
     */
    @Test
    public void testEvaluate() {
        testEvaluate("123", CalcValue.make(123));
        testEvaluate("-123", CalcValue.make(-123));
        testEvaluate("1 + 1", CalcValue.make(2));
        testEvaluate("2 * 3", CalcValue.make(6));
        testEvaluate("1 + 2 + 3", CalcValue.make(6));
        testEvaluate("3 - 2 - 1", CalcValue.make(0));
        testEvaluate("40 / 4 / 2", CalcValue.make(5));
    }
    
    @Test
    public void testEvaluateGetItem() {
        testEvaluate("[1, 2, 3]", CalcValue.list(CalcValue.make(1), CalcValue.make(2), CalcValue.make(3)));
        testEvaluate("[1, 2, 3][0]", CalcValue.make(1));
        testEvaluate("[1, 2, 3][1]", CalcValue.make(2));
        testEvaluate("[1, 2, 3][2]", CalcValue.make(3));
    }
    
    @Test
    public void testEvaluatePower() {
        testEvaluate("2 ** 3", CalcValue.make(8));
        //testEvaluate("2 ** 8", CalcValue.make(256));
    }
 
    @Test
    public void testEvaluateComp() {
        testEvaluate("1 == 2", CalcValue.make(false));
        testEvaluate("2 == 2", CalcValue.make(true));
        
        testEvaluate("2 > 1", CalcValue.make(true));
        testEvaluate("1 > 2", CalcValue.make(false));
        testEvaluate("1 < 2", CalcValue.make(true));
        testEvaluate("2 < 1", CalcValue.make(false));
    }
    
    @Test
    public void testEvaluateMatch() {
        testEvaluate("match [] { case first :: rest -> first }", CalcValue.nothing());
        testEvaluate("match [1, 2, 3] { case first :: rest -> first }", CalcValue.make(1));
        //testEvaluate(
        //        "match [1, 2, 3] { case first :: rest -> rest }",
        //        CalcValue.list(CalcValue.make(2), CalcValue.make(3)));
        testEvaluate("match [] { case [] -> true }", CalcValue.make(true));
    }
    
    @Test
    public void testFactorialFunction() {
        testEvaluate("(def fact(x) if x > 1 then x * fact(x - 1) else 1)(5)", CalcValue.make(120));
    }
    
    @Test
    public void testEvaluateDef() {
        ICalcValue result = interpreter.evaluate("def fn(x) x * x");
        assertNotNull(result);
        System.err.println(result.getClass().getSimpleName());
        System.err.println(result.getRepr(ReprContext.getDefault()));
        assertThat(result, org.hamcrest.CoreMatchers.instanceOf(FunctionValue.class));
        //assertTrue(result instanceof FunctionValue);
        
        FunctionValue fn = (FunctionValue)result;
        assertEquals(
                CalcValue.makeSymbol("fn"),
                fn.getFunctionName());
        /*
        assertEquals(
                CalcValue.list(
                        BuiltinCalcValue.MULT,
                        CalcValue.makeSymbol("x"),
                        CalcValue.makeSymbol("x")),
                fn.getFunctionBody());
                */
    }
    
    @Test
    public void testEvaluateCall() {
        testEvaluate("(def foo() 7)()", CalcValue.make(7));
        testEvaluate("(def mkadd(x) def addr(y) x + y)(1)(2)", CalcValue.make(3));
    }

    @Test
    public void testEvaluateDo() {
        testEvaluate("1; 2; 3", CalcValue.make(3));
    }
    
    @Test
    public void textSetFreeVariable() {
        testEvaluate("x = 100; def xadd() x = x + 1; xadd(); x", CalcValue.make(101));
    }
}
