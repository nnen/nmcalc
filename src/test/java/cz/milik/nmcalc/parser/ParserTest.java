/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.parser;

import cz.milik.nmcalc.ast.ASTNodeTypes;
import cz.milik.nmcalc.ast.ASTNode;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

/**
 *
 * @author jan
 */
 @Ignore
public class ParserTest {
    
    public ParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of parse method, of class Parser.
     */
    @Test
    public void testParse() {
        Parser parser = new Parser();
        ASTNode node = parser.parse("123");
        assertEquals(node.getType(), ASTNodeTypes.REAL_LITERAL);
        assertEquals(node.getLiteralValue().getValue(), "123");
        
        node = parser.parse("123 + 456");
        assertEquals(ASTNodeTypes.ADDITION, node.getType());
        assertEquals(node.getChildren().size(), 2);
        assertEquals(node.getChildren().get(0).getType(), ASTNodeTypes.REAL_LITERAL);
        assertEquals(node.getChildren().get(1).getType(), ASTNodeTypes.REAL_LITERAL);
        
        node = parser.parse("123 + 456 + 789 + 0");
        assertEquals(ASTNodeTypes.ADDITION, node.getType());
        assertEquals(4, node.getChildren().size());
        assertEquals(ASTNodeTypes.REAL_LITERAL, node.getChildren().get(0).getType());
        assertEquals(ASTNodeTypes.REAL_LITERAL, node.getChildren().get(1).getType());
        assertEquals(ASTNodeTypes.REAL_LITERAL, node.getChildren().get(2).getType());
        assertEquals(ASTNodeTypes.REAL_LITERAL, node.getChildren().get(3).getType());
    }
    
}