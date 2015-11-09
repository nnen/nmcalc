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
        //System.err.println(tokens.toString());
        if (types.length != tokens.size()) {
            for (int i = 0; i < tokens.size(); i++) {
                System.err.println(String.format("   [%d] %s", i, tokens.get(i).toString()));
            }
        }
        assertEquals(types.length, tokens.size());
        for (int i = 0; i < types.length; i++) {
            assertEquals(types[i], tokens.get(i).getType());
        }
    }
    
    protected void assertTokens(String input, Token.Types... types) {
        scanner.reset(input, "<string>");
        List<Token> tokens = scanner.readTokens();
        assertTokens(tokens, types);
    }
    
    protected Token assertToken(String input, Token.Types type) {
        scanner.reset(input, "<string>");
        List<Token> tokens = scanner.readTokens();
        assertEquals(1, tokens.size());
        assertEquals(type, tokens.get(0).getType());
        
        return tokens.get(0);
    }
    
    protected Token assertToken(String input, Token.Types type, String value) {
        Token token = assertToken(input, type);
        assertEquals(value, token.getValue());
        return token;
    }
    
    protected Token assertStringToken(String input, String strValue) {
        Token t = assertToken(input, Token.Types.STRING);
        assertEquals(strValue, t.parseStringLiteral());
        return t;
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
        
        assertTokens("1_234", Token.Types.FLOAT);
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
    
    
    @Test
    public void testString() {
        assertStringToken("\"\"", "");
        assertStringToken("\"\\\"\"", "\"");
    }

    
    @Test
    public void testColonVsCons() {
        assertTokens("::", Token.Types.CONS);
        assertTokens(":", Token.Types.COLON);
        assertTokens(": : :", Token.Types.COLON, Token.Types.COLON, Token.Types.COLON);
    }
}
