/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.values.ICalcValue;
import org.hamcrest.Matcher;

/**
 *
 * @author jan
 */
public class TestData {
    
    private final String name;
    
    private final String input;
    
    private final Matcher<ICalcValue> parseResult;
    
    private final Matcher<ICalcValue> evalResult;

    public String getName() {
        return name;
    }

    public String getInput() {
        return input;
    }

    public Matcher<ICalcValue> getParseResult() {
        return parseResult;
    }

    public Matcher<ICalcValue> getEvalResult() {
        return evalResult;
    }
    
    public TestData(String name, String input, Matcher<ICalcValue> parseResult, Matcher<ICalcValue> evalResult) {
        this.name = name;
        this.input = input;
        this.parseResult = parseResult;
        this.evalResult = evalResult;
    }

    public TestData(String name, String input, Matcher<ICalcValue> parseResult, ICalcValue evalResult) {
        this.name = name;
        this.input = input;
        this.parseResult = parseResult;
        this.evalResult = MatcherCombinator.equalsTo(evalResult);
    }
    
    public TestData(String name, String input, ICalcValue parseResult, ICalcValue evalResult) {
        this.name = name;
        this.input = input;
        this.parseResult = MatcherCombinator.equalsTo(parseResult);
        this.evalResult = MatcherCombinator.equalsTo(evalResult);
    }
    
    @Override
    public String toString() { return name; }
    
}
