/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.peg;

import cz.milik.nmcalc.ast.ASTNode;
import cz.milik.nmcalc.ast.ASTNodePredicate;
import cz.milik.nmcalc.ast.ASTNodeTypes;
import cz.milik.nmcalc.utils.ObjectUtils;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static cz.milik.nmcalc.ast.ASTNodePredicate.*;

/**
 *
 * @author jan
 */
public class CalcParserTest {
    
    private CalcParser parser;
    
    public CalcParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        parser = new CalcParser();
    }
    
    @After
    public void tearDown() {
        parser = null;
    }

    /**
     * Test of parse method, of class CalcParser.
     */
    @Test
    public void testParse_ITokenSequence_IPegContext() {
    }

    /**
     * Test of parse method, of class CalcParser.
     */
    @Test
    public void testParse_ITokenSequence() {
    }

    /**
     * Test of parse method, of class CalcParser.
     */
    @Test
    public void testParse_Literal() {
        ParseResult<ASTNode> result = parser.parse("123");
        System.err.println("Error message: " + result.getErrorMessage());
        //System.err.println("Result: " + ObjectUtils.toString(result.getValue()));
        System.err.println(result.format());
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());
        assertEquals(ASTNodeTypes.REAL_LITERAL, result.getValue().getType());
    }
    
    protected void testParse_Failure(String input, boolean expectError) {
        ParseResult<ASTNode> result = parser.parse(input);
        assertNotNull(result);
        System.err.println("Input: " + input);
        System.err.println("Error message: " + result.getErrorMessage());
        System.err.println("Result: " + ObjectUtils.toString(result.getValue()));
        assertFalse("Expected syntax error.", result.isSuccess());
        if (expectError) {
            assertTrue("Expected parsing error.", result.isError());
        } else {
            assertFalse("Expected parsing failure (not error).", result.isError());
        }
    }
    
    protected void testParse_BinaryOperator(String input, ASTNodePredicate predicate) {
        ParseResult<ASTNode> result = parser.parse(input);
        assertNotNull(result);
        System.err.println("Input: " + input);
        System.err.println(result.format());
        //System.err.println("Error message: " + result.getErrorMessage());
        //System.err.println("Result: " + ObjectUtils.toString(result.getValue()));
        assertTrue("Exected no syntax errors.", result.isSuccess());
        if (predicate != null) {
            assertTrue("Predicate is true.", predicate.test(result.getValue()));
        }
    }
    
    protected void testParse_BinaryOperator(String input, ASTNodeTypes nodeType) {
        testParse_BinaryOperator(input, ASTNodePredicate.type(nodeType));
    }
    
    
    @Test
    public void testParse_Assignment00() {
        testParse_BinaryOperator(
                "a = 0",
                ASTNodePredicate.type(ASTNodeTypes.ASSIGNMENT).children(
                        ASTNodePredicate.type(ASTNodeTypes.VARIABLE),
                        ASTNodePredicate.type(ASTNodeTypes.REAL_LITERAL)
                )
        );
    }
    
    @Test
    public void testParse_Assignment01() {
        testParse_BinaryOperator(
                "a = 2 * (3 + b)",
                type(ASTNodeTypes.ASSIGNMENT).children(
                        type(ASTNodeTypes.VARIABLE),
                        type(ASTNodeTypes.MULTIPLICATION).children(
                                type(ASTNodeTypes.REAL_LITERAL),
                                type(ASTNodeTypes.ADDITION).children(
                                        type(ASTNodeTypes.REAL_LITERAL),
                                        type(ASTNodeTypes.VARIABLE)
                                )
                        )
                )
        );
    }
    
    /**
     * Test of parse method, of class CalcParser.
     */
    @Test
    public void testParse_Addition00() {
        testParse_BinaryOperator("123 + 456", ASTNodeTypes.ADDITION);
    }
    
    @Test
    public void testParse_Addition01() {
        testParse_BinaryOperator(
                "123 + 456 + 1 + 2 + 3",
                ASTNodePredicate.type(ASTNodeTypes.ADDITION).children(
                        ASTNodePredicate.type(ASTNodeTypes.ADDITION).children(
                                ASTNodePredicate.type(ASTNodeTypes.ADDITION).children(
                                        ASTNodePredicate.type(ASTNodeTypes.ADDITION).children(
                                                ASTNodePredicate.make("123", ASTNodeTypes.REAL_LITERAL),
                                                ASTNodePredicate.make("456", ASTNodeTypes.REAL_LITERAL)
                                        ),
                                        ASTNodePredicate.make("1", ASTNodeTypes.REAL_LITERAL)
                                ),
                                ASTNodePredicate.make("2", ASTNodeTypes.REAL_LITERAL)
                        ),
                        ASTNodePredicate.make("3", ASTNodeTypes.REAL_LITERAL)
                )
        );
    }
    
    @Test
    public void testParse_Addition02() {
        testParse_Failure("123 + 456 789", false);
    }
    
    @Test
    public void testParse_Subtraction00() {
        testParse_BinaryOperator("123 - 456",
                ASTNodeTypes.SUBTRACTION);
    }
    
    @Test
    public void testParse_Subtraction01() {
        testParse_BinaryOperator(
                "123 - 456 - 1 - 2 - 3",
                ASTNodePredicate.type(ASTNodeTypes.SUBTRACTION).children(
                        ASTNodePredicate.type(ASTNodeTypes.SUBTRACTION).children(
                                ASTNodePredicate.type(ASTNodeTypes.SUBTRACTION).children(
                                        ASTNodePredicate.type(ASTNodeTypes.SUBTRACTION).children(
                                                ASTNodePredicate.make("123", ASTNodeTypes.REAL_LITERAL),
                                                ASTNodePredicate.make("456", ASTNodeTypes.REAL_LITERAL)
                                        ),
                                        ASTNodePredicate.make("1", ASTNodeTypes.REAL_LITERAL)
                                ),
                                ASTNodePredicate.make("2", ASTNodeTypes.REAL_LITERAL)
                        ),
                        ASTNodePredicate.make("3", ASTNodeTypes.REAL_LITERAL)
                )
        );
    }
    
    @Test
    public void testParse_Multiplication00() {
        testParse_BinaryOperator("123 * 456",
                ASTNodeTypes.MULTIPLICATION);
    }
    
    @Test
    public void testParse_Multiplication01() {
        testParse_BinaryOperator(
                "123 * 456 * 1 * 2 * 3",
                ASTNodePredicate.type(ASTNodeTypes.MULTIPLICATION).children(
                        ASTNodePredicate.type(ASTNodeTypes.MULTIPLICATION).children(
                                ASTNodePredicate.type(ASTNodeTypes.MULTIPLICATION).children(
                                        ASTNodePredicate.type(ASTNodeTypes.MULTIPLICATION).children(
                                                ASTNodePredicate.make("123", ASTNodeTypes.REAL_LITERAL),
                                                ASTNodePredicate.make("456", ASTNodeTypes.REAL_LITERAL)
                                        ),
                                        ASTNodePredicate.make("1", ASTNodeTypes.REAL_LITERAL)
                                ),
                                ASTNodePredicate.make("2", ASTNodeTypes.REAL_LITERAL)
                        ),
                        ASTNodePredicate.make("3", ASTNodeTypes.REAL_LITERAL)
                )
        );
    }
    
    @Test
    public void testParse_Division00() {
        testParse_BinaryOperator("123 / 456",
                ASTNodeTypes.DIVISION);
        testParse_BinaryOperator("101/5",
                ASTNodeTypes.DIVISION);
    }
    
    @Test
    public void testParse_Division01() {
        testParse_BinaryOperator(
                "123 / 456 / 1 / 2 / 3",
                ASTNodePredicate.type(ASTNodeTypes.DIVISION).children(
                        ASTNodePredicate.type(ASTNodeTypes.DIVISION).children(
                                ASTNodePredicate.type(ASTNodeTypes.DIVISION).children(
                                        ASTNodePredicate.type(ASTNodeTypes.DIVISION).children(
                                                ASTNodePredicate.make("123", ASTNodeTypes.REAL_LITERAL),
                                                ASTNodePredicate.make("456", ASTNodeTypes.REAL_LITERAL)
                                        ),
                                        ASTNodePredicate.make("1", ASTNodeTypes.REAL_LITERAL)
                                ),
                                ASTNodePredicate.make("2", ASTNodeTypes.REAL_LITERAL)
                        ),
                        ASTNodePredicate.make("3", ASTNodeTypes.REAL_LITERAL)
                )
        );
    }
    
    @Test
    public void testParse_Division02() {
        testParse_BinaryOperator(
                "1 / (2 / 3)",
                ASTNodePredicate.type(ASTNodeTypes.DIVISION).children(
                        ASTNodePredicate.make("1", ASTNodeTypes.REAL_LITERAL),
                        ASTNodePredicate.type(ASTNodeTypes.DIVISION).children(
                                ASTNodePredicate.make("2", ASTNodeTypes.REAL_LITERAL),
                                ASTNodePredicate.make("3", ASTNodeTypes.REAL_LITERAL)
                        )
                )
        );
    }
    
    @Test
    public void testParse_Division03() {
        testParse_BinaryOperator(
                "(1 / 2) / 3",
                ASTNodePredicate.type(ASTNodeTypes.DIVISION).children(
                        ASTNodePredicate.type(ASTNodeTypes.DIVISION).children(
                                ASTNodePredicate.make("1", ASTNodeTypes.REAL_LITERAL),
                                ASTNodePredicate.make("2", ASTNodeTypes.REAL_LITERAL)
                        ),
                        ASTNodePredicate.make("3", ASTNodeTypes.REAL_LITERAL)
                )
        );
    }
    
}




