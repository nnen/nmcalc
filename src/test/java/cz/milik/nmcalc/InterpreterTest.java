/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

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
        testEvaluate("1 + 1", CalcValue.make(2));
        testEvaluate("2 * 3", CalcValue.make(6));
        testEvaluate("1 + 2 + 3", CalcValue.make(6));
        testEvaluate("3 - 2 - 1", CalcValue.make(0));
        testEvaluate("40 / 4 / 2", CalcValue.make(5));
    }
 
    @Test
    public void testEvaluateDef() {
        ICalcValue result = interpreter.evaluate("def fn(x) x * x");
        assertNotNull(result);
        System.err.println(result.getClass().getSimpleName());
        System.err.println(result.getRepr(ReprContext.getDefault()));
        assertTrue(result instanceof FunctionValue);
        
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
}
