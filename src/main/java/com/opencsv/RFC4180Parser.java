package com.opencsv;

import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This Parser is meant to parse csv data according to the RFC4180 specification.
 * <p>Since it shares the same interface with the CSVParser there are methods here that will do nothing.
 * For example, the RFC4180 specification does not have a concept of an escape character, so the getEscape method
 * will return char 0.  The methods that are not supported are noted in the Javadocs.</p>
 * <p>Another departure from the CSVParser is that there are only two constructors and only one is available publicly.
 * The intent is that if you want to create anything other than a default RFC4180Parser you should use the
 * CSVParserBuilder.  This way the code will not become cluttered with constructors as the CSVParser did.</p>
 * <p>You can view the RFC4180 specification at <a href="https://tools.ietf.org/html/rfc4180">the Internet Engineering
 * Task Force (IETF) website</a>.</p>
 * <p>Examples:</p>
 * {@code
 * ICSVParser parser = new RFC4180Parser();
 * }
 * <p>or</p>
 * {@code
 * CSVParserBuilder builder = new CSVParserBuilder()
 * ICSVParser parser = builder.withParserType(ParserType.RFC4180Parser).build()
 * }
 *
 * @author Scott Conway
 * @since 3.9
 */

public class RFC4180Parser extends AbstractCSVParser {

    /**
     * Default constructor for the RFC4180Parser.  Uses values from the ICSVParser.
     */
    public RFC4180Parser() {
        this(ICSVParser.DEFAULT_QUOTE_CHARACTER, ICSVParser.DEFAULT_SEPARATOR, CSVReaderNullFieldIndicator.NEITHER);
    }

    /**
     * Constructor used by the CSVParserBuilder.
     *
     * @param separator The delimiter to use for separating entries
     * @param quoteChar The character to use for quoted elements
     * @param nullFieldIndicator Indicate what should be considered null
     */
    protected RFC4180Parser(char quoteChar, char separator, CSVReaderNullFieldIndicator nullFieldIndicator) {
        super(separator, quoteChar, nullFieldIndicator);
    }

    @Override
    protected String convertToCsvValue(String value, boolean applyQuotesToAll) {
        String testValue = (value == null && !nullFieldIndicator.equals(CSVReaderNullFieldIndicator.NEITHER)) ? "" : value;
        StringBuilder builder = new StringBuilder(testValue == null ? MAX_SIZE_FOR_EMPTY_FIELD : (testValue.length() * 2));
        boolean containsQuoteChar = testValue != null && testValue.contains(getQuotecharAsString());
        boolean surroundWithQuotes = applyQuotesToAll || isSurroundWithQuotes(value, containsQuoteChar);

        String convertedString = !containsQuoteChar ? testValue : getQuoteMatcherPattern().matcher(testValue).replaceAll(getQuoteDoubledAsString());

        if (surroundWithQuotes) {
            builder.append(getQuotechar());
        }

        builder.append(convertedString);

        if (surroundWithQuotes) {
            builder.append(getQuotechar());
        }

        return builder.toString();
    }

    /**
     * Parses an incoming String and returns an array of elements.
     *
     * @param nextLine The string to parse
     * @param multi    Does it take multiple lines to form a single record?
     * @return The list of elements, or null if nextLine is null
     */
    protected String[] parseLine(String nextLine, boolean multi) {
        String[] elements;

        if (!multi && pending != null) {
            pending = null;
        }

        if (nextLine == null) {
            if (pending != null) {
                String s = pending;
                pending = null;
                return new String[]{s};
            }
            return null;
        }

        String lineToProcess = multi && pending != null ? pending + nextLine : nextLine;
        pending = null;

        if (!StringUtils.contains(lineToProcess, quotechar)) {
            elements = handleEmptySeparators(tokenizeStringIntoArray(lineToProcess));
        } else {
            elements = handleEmptySeparators(splitWhileNotInQuotes(lineToProcess, multi));
            for (int i = 0; i < elements.length; i++) {
                if (StringUtils.contains(elements[i], quotechar)) {
                    elements[i] = handleQuotes(elements[i]);
                }
            }
        }
        return elements;
    }

    private String[] tokenizeStringIntoArray(String nextLine) {
        return StringUtils.splitPreserveAllTokens(nextLine, separator);
    }

    private String[] handleEmptySeparators(String[] strings) {
        if (nullFieldIndicator == CSVReaderNullFieldIndicator.EMPTY_SEPARATORS || nullFieldIndicator == CSVReaderNullFieldIndicator.BOTH) {
            for (int i = 0; i < strings.length; i++) {
                if (strings[i].isEmpty()) {
                    strings[i] = null;
                }
            }
        }
        return strings;
    }

    private String[] splitWhileNotInQuotes(String nextLine, boolean multi) {
        int currentPosition = 0;
        List<String> elements = new ArrayList<>();
        int nextSeparator;
        int nextQuote;


        while (currentPosition < nextLine.length()) {
            nextSeparator = nextLine.indexOf(separator, currentPosition);
            nextQuote = nextLine.indexOf(quotechar, currentPosition);

            if (nextSeparator == -1) {
                elements.add(nextLine.substring(currentPosition));
                currentPosition = nextLine.length();
            } else if (nextQuote == -1 || nextQuote > nextSeparator || nextQuote != currentPosition) {
                elements.add(nextLine.substring(currentPosition, nextSeparator));
                currentPosition = nextSeparator + 1;
            } else {
                int fieldEnd = findEndOfFieldFromPosition(nextLine, currentPosition);

                elements.add(fieldEnd >= nextLine.length() ? nextLine.substring(currentPosition) : nextLine.substring(currentPosition, fieldEnd));

                currentPosition = fieldEnd + 1;
            }

        }

        if (multi && lastElementStartedWithQuoteButDidNotEndInOne(elements)) {
            pending = elements.get(elements.size() - 1) + NEWLINE;
            elements.remove(elements.size() - 1);
        } else if (nextLine.lastIndexOf(separator) == nextLine.length() - 1) {
            elements.add("");
        }
        return elements.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    private boolean lastElementStartedWithQuoteButDidNotEndInOne(List<String> elements) {
        String lastElement = elements.get(elements.size() - 1);
        return startsButDoesNotEndWithQuote(lastElement) || hasOnlyOneQuote(lastElement) || hasOddQuotes(lastElement);
    }

    private boolean hasOddQuotes(String lastElement) {
        return StringUtils.countMatches(lastElement, quotechar) % 2 != 0;
    }

    private boolean hasOnlyOneQuote(String lastElement) {
        return StringUtils.countMatches(lastElement, quotechar) == 1;
    }

    private boolean startsButDoesNotEndWithQuote(String lastElement) {
        return lastElement.startsWith(getQuotecharAsString()) && !lastElement.endsWith(getQuotecharAsString());
    }

    private int findEndOfFieldFromPosition(String nextLine, int currentPosition) {
        int nextQuote = nextLine.indexOf(quotechar, currentPosition + 1);

        boolean inQuote = false;
        while (haveNotFoundLastQuote(nextLine, nextQuote)) {
            if (!inQuote && nextLine.charAt(nextQuote + 1) == separator) {
                return nextQuote + 1;
            }

            do {
                nextQuote = nextLine.indexOf(quotechar, nextQuote + 1);
                inQuote = !inQuote;
            } while (haveNotFoundLastQuote(nextLine, nextQuote) && nextLine.charAt(nextQuote + 1) == quotechar);
        }

        return nextLine.length();
    }

    private boolean haveNotFoundLastQuote(String nextLine, int nextQuote) {
        return nextQuote != -1 && nextQuote < nextLine.length() - 1;
    }

    private String handleQuotes(String element) {
        String ret = element;

        if (!hasOnlyOneQuote(ret) && ret.startsWith(getQuotecharAsString())) {
            ret = StringUtils.removeStart(ret, getQuotecharAsString());
            ret = StringUtils.removeEnd(ret, getQuotecharAsString());
        }
        ret = StringUtils.replace(ret, getQuoteDoubledAsString(), getQuotecharAsString());
        if (ret.isEmpty() && (nullFieldIndicator == CSVReaderNullFieldIndicator.BOTH || nullFieldIndicator == CSVReaderNullFieldIndicator.EMPTY_QUOTES)) {
            ret = null;
        }
        return ret;
    }
    
    @Override
    public void setErrorLocale(Locale errorLocale) {
        // Curiously enough, this implementation never throws exceptions and so
        // has no need of translations.
    }
}
