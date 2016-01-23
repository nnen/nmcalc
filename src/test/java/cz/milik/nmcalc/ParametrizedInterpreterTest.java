/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import static cz.milik.nmcalc.ValueMatcher.*;
import cz.milik.nmcalc.utils.Pair;
import cz.milik.nmcalc.values.CalcValue;
import cz.milik.nmcalc.values.ICalcValue;
import java.util.Arrays;
import org.junit.After;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author jan
 */
@RunWith(Parameterized.class)
public class ParametrizedInterpreterTest {
    
    private Interpreter interpreter;
    private final TestData testData;
    
    public ParametrizedInterpreterTest(TestData testData) {
        this.testData = testData;
    }
    
    @Before
    public void setUp() {
        interpreter = new Interpreter();
    }
    
    @After
    public void tearDown() {
        interpreter = null;
    }
    
    @Test
    public void testEvaluate() {
        ICalcValue result = interpreter.evaluate(testData.getInput());
        assertThat(result, testData.getEvalResult());
    }
    
    @Test
    public void testParse() {
        ICalcValue result = interpreter.parse(testData.getInput());
        assertThat(result, testData.getParseResult());
    }
    
    @Parameterized.Parameters(name = "{index}: {0}")
    public static Iterable data() {
        return Arrays.asList(
                new TestData("float literal 0", "0",
                        equalsTo(CalcValue.make(0)),
                        CalcValue.make(0)),
                new TestData("float literal 123", "123",
                        equalsTo(CalcValue.make(123)),
                        CalcValue.make(123)),
                new TestData("float literal 123.456", "123.456",
                        equalsTo(CalcValue.make(123.456)),
                        CalcValue.make(123.456)),
                new TestData("float literal 0.456", "0.456",
                        equalsTo(CalcValue.make(0.456)),
                        CalcValue.make(0.456)),
                
                new TestData("string literal \"abc\"", "\"abc\"",
                        equalsTo(CalcValue.make("abc")),
                        CalcValue.make("abc")),
                new TestData("string literal with quotes", "\"\\\"abc\\\"\"",
                        equalsTo(CalcValue.make("\"abc\"")),
                        CalcValue.make("\"abc\"")),
                new TestData("string literal with new lines", "\"hello\\nworld\"",
                        equalsTo(CalcValue.make("hello\nworld")),
                        CalcValue.make("hello\nworld")),
                
                new TestData("list literal",
                        "[1, 2, 3, 4]",
                        and(
                                isList(),
                                items(
                                    equalsTo(BuiltinCalcValue.LIST),
                                    isFloat(1),
                                    isFloat(2),
                                    isFloat(3),
                                    isFloat(4)
                                )
                        ),
                        CalcValue.list(
                                CalcValue.make(1),
                                CalcValue.make(2),
                                CalcValue.make(3),
                                CalcValue.make(4)
                        )
                ),
                
                new TestData("list literal",
                        "{1: 2}",
                        and(
                                isList(),
                                items(
                                    equalsTo(BuiltinCalcValue.DICT),
                                    and(
                                            isList(),
                                            items(
                                                    equalsTo(BuiltinCalcValue.LIST),
                                                    items(
                                                        equalsTo(BuiltinCalcValue.LIST),
                                                        isFloat(1),
                                                        isFloat(2)
                                                    )
                                            )
                                    )
                                )
                        ),
                        isDict(
                                Pair.of(CalcValue.makeFloat("1"), isFloat(2))
                        )
                ),
                
                new TestData("do block simple",
                        "1; 2; 3",
                        isListWithItems(
                                equalsTo(BuiltinCalcValue.DO),
                                isFloat(1),
                                isFloat(2),
                                isFloat(3)
                        ),
                        CalcValue.makeFloat("3")
                ),
                new TestData("do block",
                        "do { 1; 2; 3 }",
                        isListWithItems(
                                equalsTo(BuiltinCalcValue.DO),
                                isFloat(1),
                                isFloat(2),
                                isFloat(3)
                        ),
                        CalcValue.makeFloat("3")
                )
        );
    }
    
}
