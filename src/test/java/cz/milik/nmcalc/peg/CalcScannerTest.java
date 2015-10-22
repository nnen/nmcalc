/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.peg;

import cz.milik.nmcalc.parser.Token;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jan
 */
public class CalcScannerTest {
    
    private CalcScanner scanner;
    
    public CalcScannerTest() {
    }
    
    @Before
    public void setUp() {
        scanner = new CalcScanner();
    }
    
    @After
    public void tearDown() {
        scanner = null;
    }

    protected void assertTokens(List<Token> tokens, Token.Types... types) {
        System.err.println(tokens.toString());
        assertEquals(types.length, tokens.size());
        for (int i = 0; i < types.length; i++) {
            assertEquals(types[i], tokens.get(i).getType());
        }
    }
    
    protected void assertTokens(String input, Token.Types... types) {
        scanner.reset(input);
        List<Token> tokens = scanner.readTokens();
        assertTokens(tokens, types);
    }
    
    @Test
    public void testIdentifier00() {
        assertTokens("abc", Token.Types.IDENTIFIER);
    }
    
    @Test
    public void testIdentifier01() {
        assertTokens("first second third", Token.Types.IDENTIFIER, Token.Types.IDENTIFIER, Token.Types.IDENTIFIER);
    }
    
    @Test
    public void testFloat() {
        assertTokens("123", Token.Types.FLOAT);
        assertTokens("123.", Token.Types.FLOAT);
        assertTokens("123.456", Token.Types.FLOAT);
        assertTokens("0.456", Token.Types.FLOAT);
    }
    
    @Test
    public void testHex() {
        assertTokens("0x0", Token.Types.HEX_LITERAL);
        assertTokens("0x123456789abcdef", Token.Types.HEX_LITERAL);
        
        //assertTokens("0x", Token.Types.UNKNOWN);
        //assertTokens("0x123456789abcdefg", Token.Types.UNKNOWN);
    }
    
    @Test
    public void testBin() {
        assertTokens("0b", Token.Types.BIN_LITERAL);
        assertTokens("1b", Token.Types.BIN_LITERAL);
        assertTokens("2b", Token.Types.FLOAT, Token.Types.IDENTIFIER);
        assertTokens("010101b", Token.Types.BIN_LITERAL);
        
        //assertTokens("0x", Token.Types.UNKNOWN);
        //assertTokens("0x123456789abcdefg", Token.Types.UNKNOWN);
    }
}

