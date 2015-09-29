/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.parser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
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
public class ScannerTest {
    
    public ScannerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    public Scanner prepareScanner(String input)
    {
        Scanner result = new Scanner();
        result.reset(new ReaderInput(new StringReader(input), "<string>"));
        return result;
    }
    
    public List<Token> readTokens(Scanner scanner)
    {
        List<Token> result = new ArrayList<>();
        Token token;
        while (true)
        {
            token = scanner.nextToken();
            if (token.getType() == Token.Types.EOF)
            {
                break;
            }
            result.add(token);
        }
        return result;
    }
    
    public List<Token> readTokens(String input)
    {
        return readTokens(prepareScanner(input));
    }
    
    public void assertToken(Token token, Token.Types type, String literalValue)
    {
        assertNotNull(token);
        assertEquals(token.getType(), type);
        assertEquals(token.getValue(), literalValue);
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of nextToken method, of class Scanner.
     */
    @Test
    public void testNextToken() {
    }
    
    @Test
    public void testFloatToken() {
        List<Token> tokens = readTokens("123");
        assertEquals(tokens.size(), 1);
        assertEquals(tokens.get(0).getType(), Token.Types.FLOAT);
        assertEquals(tokens.get(0).getValue(), "123");
        
        tokens = readTokens("123.");
        assertEquals(tokens.size(), 1);
        assertEquals(tokens.get(0).getType(), Token.Types.FLOAT);
        assertEquals(tokens.get(0).getValue(), "123.");
        
        tokens = readTokens("123.456");
        assertEquals(tokens.size(), 1);
        assertEquals(tokens.get(0).getType(), Token.Types.FLOAT);
        assertEquals(tokens.get(0).getValue(), "123.456");
        
        tokens = readTokens(".456");
        assertEquals(tokens.size(), 1);
        assertEquals(tokens.get(0).getType(), Token.Types.FLOAT);
        assertEquals(tokens.get(0).getValue(), ".456");
        
        tokens = readTokens("1 2 3");
        assertEquals(tokens.size(), 3);
        assertToken(tokens.get(0), Token.Types.FLOAT, "1");
        assertToken(tokens.get(1), Token.Types.FLOAT, "2");
        assertToken(tokens.get(2), Token.Types.FLOAT, "3");
    }
    
    @Test
    public void testExpression() {
        List<Token> tokens = readTokens("1 + (2.34 * 3) / 4");
        assertEquals(tokens.size(), 9);
        assertToken(tokens.get(0), Token.Types.FLOAT, "1");
        assertToken(tokens.get(1), Token.Types.PLUS, "+");
        assertToken(tokens.get(2), Token.Types.LPAR, "(");
        assertToken(tokens.get(3), Token.Types.FLOAT, "2.34");
        assertToken(tokens.get(4), Token.Types.ASTERISK, "*");
        assertToken(tokens.get(5), Token.Types.FLOAT, "3");
        assertToken(tokens.get(6), Token.Types.RPAR, ")");
        assertToken(tokens.get(7), Token.Types.SLASH, "/");
        assertToken(tokens.get(8), Token.Types.FLOAT, "4");
        
        tokens = readTokens("134 + 456");
        assertEquals(tokens.size(), 3);
        assertToken(tokens.get(0), Token.Types.FLOAT, "134");
        assertToken(tokens.get(1), Token.Types.PLUS, "+");
        assertToken(tokens.get(2), Token.Types.FLOAT, "456");
    }
}
