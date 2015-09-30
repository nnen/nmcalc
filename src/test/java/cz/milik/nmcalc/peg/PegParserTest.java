/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.peg;

import cz.milik.nmcalc.parser.Token;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
    
}
