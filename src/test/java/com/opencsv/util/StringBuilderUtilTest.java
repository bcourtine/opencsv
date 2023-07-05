package com.opencsv.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class StringBuilderUtilTest {
    private static final String EMPTY = "";
    private static final String SINGLEQUOTE = "\"";
    private static final String DOUBLEQUOTES = "\"\"";
    private static final String NOQUOTES = "This is a String without quotes";
    private static final String WITHQUOTES = "This is a String that \"has\" quotes inside of it";
    private static final String WITHQUOTESREMOVED = "This is a String that has quotes inside of it";
    private static final String WITHQUOTESOUTPUT = "This is a String that \"\"has\"\" quotes inside of it";

    @Test
    @DisplayName("replaceAll handles null")
    public void nullTest() {
        assertNull(StringBuilderUtil.replaceAll(null, SINGLEQUOTE, DOUBLEQUOTES));
    }

    @Test
    @DisplayName("Test value that has no quotes in it")
    public void noquoteTest() {
        StringBuilder builder = new StringBuilder(NOQUOTES);
        assertEquals(builder, StringBuilderUtil.replaceAll(builder, SINGLEQUOTE, DOUBLEQUOTES));
    }

    @Test
    @DisplayName("Actually replace values")
    public void singleToDoubleQuotes() {
        StringBuilder builder = new StringBuilder(WITHQUOTES);
        StringBuilder expectedOutput = new StringBuilder(WITHQUOTESOUTPUT);

        assertEquals(expectedOutput.toString(), StringBuilderUtil.replaceAll(builder, SINGLEQUOTE, DOUBLEQUOTES).toString());
    }

    @Test
    @DisplayName("remove findString if replaceString is empty")
    public void emptyReplaceString() {
        StringBuilder builder = new StringBuilder(WITHQUOTES);
        StringBuilder expectedOutput = new StringBuilder(WITHQUOTESREMOVED);

        assertEquals(expectedOutput.toString(), StringBuilderUtil.replaceAll(builder, SINGLEQUOTE, EMPTY).toString());
    }

    @Test
    @DisplayName("remove findString if replaceString is null")
    public void nullReplaceString() {
        StringBuilder builder = new StringBuilder(WITHQUOTES);
        StringBuilder expectedOutput = new StringBuilder(WITHQUOTESREMOVED);

        assertEquals(expectedOutput.toString(), StringBuilderUtil.replaceAll(builder, SINGLEQUOTE, null).toString());
    }
}
