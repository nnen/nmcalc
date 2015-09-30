/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.parser;

import java.io.Reader;
import java.io.StringReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

/**
 *
 * @author jan
 */
public class ReaderInputTest {
    
    Reader reader;
    ReaderInput instance;
       
    public ReaderInputTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        reader = new StringReader("abc");
        instance = new ReaderInput(reader, "<string>");
    }
    
    @After
    public void tearDown() {
    }
    
    @org.junit.Test
    public void testUsage() {
        char[] expectedChars = new char[] { 'a', 'b', 'c' };
        
        for (int i = 0; i < expectedChars.length; i++)
        {
            assertEquals(instance.hasNext(), true);
            assertEquals(instance.peek(), expectedChars[i]);
            assertEquals((char)instance.next(), expectedChars[i]);
        }
        
        assertEquals(instance.hasNext(), false);
    }

    /**
     * Test of peek method, of class ReaderInput.
     */
    @org.junit.Test
    public void testPeek() {
        System.out.println("peek");
        char expResult = 'a';
        char result = instance.peek();
        assertEquals(expResult, result);
    }

    /**
     * Test of getFileName method, of class ReaderInput.
     */
    @org.junit.Test
    public void testGetFileName() {
        System.out.println("getFileName");
        String expResult = "<string>";
        String result = instance.getFileName();
        assertEquals(expResult, result);
    }

    /**
     * Test of hasNext method, of class ReaderInput.
     */
    @org.junit.Test
    public void testHasNext() {
        System.out.println("hasNext");
        boolean expResult = true;
        boolean result = instance.hasNext();
        assertEquals(expResult, result);
    }

    /**
     * Test of next method, of class ReaderInput.
     */
    @org.junit.Test
    public void testNext() {
        System.out.println("next");
        Character expResult = 'a';
        Character result = instance.next();
        assertEquals(expResult, result);
    }
    
}
