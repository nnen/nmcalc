/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.parser;

import java.io.IOException;
import java.io.Reader;

/**
 *
 * @author jan
 */
public class ReaderInput implements IInput {

    private String fileName;
    private Reader reader;
    
    private boolean hasNextChar;
    private char nextChar;
    
    private void nextInternal()
    {
        int value = -1;
        try {
            value = reader.read();
        } catch (IOException e) {
            hasNextChar = false;
            nextChar = 0;
            return;
        }
        if (value < 0)
        {
            hasNextChar = false;
            nextChar = 0;
            return;
        }
        hasNextChar = true;
        nextChar = (char)value;
    }
    
    @Override
    public char peek() {
        if (hasNextChar)
        {
            return nextChar;
        }
        return Character.MIN_VALUE;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public boolean hasNext() {
        return hasNextChar;
    }
    
    @Override
    public Character next() {
        char result = nextChar;
        nextInternal();
        return result;
    }
    
    public ReaderInput(Reader reader, String fileName)
    {
        this.reader = reader;
        this.fileName = fileName;
        nextInternal();
    }
    
}
