/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.peg;

import cz.milik.nmcalc.BuiltinCalcValue;
import cz.milik.nmcalc.parser.Token;
import cz.milik.nmcalc.values.ICalcValue;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
// import static cz.milik.nmcalc.MatcherCombinator.*;
import static cz.milik.nmcalc.ValueMatcher.*;
import cz.milik.nmcalc.values.FunctionValue;
import org.hamcrest.core.IsInstanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jan
 */
public class PegParserTest {
    
    protected class DummyTokenSequence extends TokenList {
        
        public DummyTokenSequence() {
            super(new ArrayList<>());
        }
        
        public DummyTokenSequence append(Token.Types tt, String value) {
            int offset = 0;
            
            if (getList().size() > 0) {
                Token t = getList().get(getList().size() - 1);
                offset = t.getOffset() + t.getValue().length() + 1;
            }
            
            getList().add(new Token(tt, offset, value));
            
            return this;
        }
        
    }
    
    protected DummyTokenSequence tokenSequence;
    
    public PegParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        resetTokens();
    }
    
    @After
    public void tearDown() {
        tokenSequence = null;
    }
    
    protected DummyTokenSequence resetTokens() {
        tokenSequence = new DummyTokenSequence();
        return tokenSequence;
    }
    
    protected IPegContext newContext() {
        return new IPegContext.PegContext(null, null, 0);
    }
    
    protected ParseResult<ICalcValue> parse(String input, boolean assumeSuccess) {
        CalcParser parser = new CalcParser();
        ParseResult<ICalcValue> result = parser.parseList(input);
        if (assumeSuccess) {
            if (!result.isSuccess()) {
                System.err.println("=========================================");
                result.getContext().printStack(System.err);
                System.err.println();
                System.err.println("=========================================");
                fail("Expected no parsing errors.\nParser: " + 
                        result.getContext().getParser().getShortDescription() + 
                        "\nFound: " +
                        result.getRest().get(0).toString());
            }
        }
        return result;
    }
    
    protected ParseResult<ICalcValue> parse(String input) {
        return parse(input, true);
    }
    
    @Test
    public void testTokenParser() {
        ParseResult<Token> r = PegParser.TOKEN_PARSER.parse(
                resetTokens(),
                newContext()
        );
        assertFalse(r.isSuccess());
        assertFalse(r.isError());
        
        r = PegParser.TOKEN_PARSER.parse(
                resetTokens().append(Token.Types.FLOAT, "123"),
                newContext()
        );
        assertTrue(r.isSuccess());
        assertNotNull(r.getValue());
    }
    
    @Test
    public void testMap() {
        ParseResult<Float> r = PegParser.TOKEN_PARSER.map(
                (t, ctx) -> Float.parseFloat(t.getValue())
        ).parse(
                resetTokens(),
                newContext()
        );
        assertFalse(r.isSuccess());
        assertFalse(r.isError());
        
        r = PegParser.TOKEN_PARSER.map(
                (t, ctx) -> Float.parseFloat(t.getValue())
        ).parse(
                resetTokens().append(Token.Types.FLOAT, "123"),
                newContext()
        );
        assertTrue(r.isSuccess());
        assertNotNull(r.getValue());
        assertEquals(123.0, r.getValue().floatValue(), 0.01);
    }
    
    @Test
    public void testParenthisedExpression() {
        parse("(1 + 2 + 3)");
    }
    
    @Test
    public void testCall00() {
        ParseResult<ICalcValue> r = parse("foo()");
    }
    
    @Test
    public void testCall01() {
        ParseResult<ICalcValue> r = parse("foo(1, 2, 3)");
    }
    
    @Test
    public void testCall02() {
        ParseResult<ICalcValue> r = parse("(def foo() 7)()");
        /*
        assertThat(r.getValue(), and(
                isList(),
                items(
                        and(
                                isList(),
                                items(
                                    equalsTo(BuiltinCalcValue.DEF)
                                )
                        )
                )
        ));
                */
    }
    
    @Test
    public void testDo00() {
        parse("1; 2; 3");
    }
    
}
