package com.opencsv;

import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An abstract class representing the basic functionality required for parsing CSV files.
 * Implements the ICSVParser interface and provides common shared behavior for various CSV
 * parsing implementations.
 */
public abstract class AbstractCSVParser implements ICSVParser {
    /**
     * This is needed by the split command in case the separator character is a regex special character.
     */
    protected static final Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");

    /**
     * Empty StringBuilder
     */
    protected static final StringBuilder EMPTY_STRINGBUILDER = new StringBuilder("");

    /**
     * This is the character that the CSVParser will treat as the separator.
     */
    protected final char separator;
    /**
     * This is the separator in Stirng form to reduce the number of calls to toString.
     */
    private final String separatorAsString;
    /**
     * This is the character that the CSVParser will treat as the quotation character.
     */
    protected final char quotechar;
    /**
     * This is the quotechar in String form to reduce the number of calls to toString.
     */
    private final String quotecharAsString;


    /**
     * Returns the doubled quote character as a string representation.
     *
     * @return The string representation of the doubled quote character.
     */
    protected final String getQuoteDoubledAsString() {
        return quoteDoubledAsString;
    }

    /**
     * This is quotecharAsString+quotecharAsString - used in replaceAll to reduce the number of strings being created.
     */
    private final String quoteDoubledAsString;

    /**
     * pattern created to match quotechars - optimizaion of the String.replaceAll.
     */
    private final Pattern quoteMatcherPattern;


    /**
     * Determines the handling of null fields.
     *
     * @see CSVReaderNullFieldIndicator
     */
    protected final CSVReaderNullFieldIndicator nullFieldIndicator;

    /**
     * Value to be appended to string to process.
     */
    protected String pending;

    /**
     * Common constructor.
     *
     * @param separator          The delimiter to use for separating entries
     * @param quotechar          The character to use for quoted elements
     * @param nullFieldIndicator Indicate what should be considered null
     */
    public AbstractCSVParser(char separator, char quotechar, CSVReaderNullFieldIndicator nullFieldIndicator) {
        this.separator = separator;
        this.separatorAsString = SPECIAL_REGEX_CHARS.matcher(Character.toString(separator)).replaceAll("\\\\$0");

        this.quotechar = quotechar;
        this.quotecharAsString = Character.toString(quotechar);
        this.quoteDoubledAsString = this.quotecharAsString + this.quotecharAsString;
        this.quoteMatcherPattern = Pattern.compile(quotecharAsString);

        this.nullFieldIndicator = nullFieldIndicator;
    }


    /**
     * Retrieves the compiled {@link Pattern} used for matching quoted elements in a CSV input.
     *
     * @return The {@link Pattern} used for identifying quoted elements.
     */
    protected final Pattern getQuoteMatcherPattern() {
        return quoteMatcherPattern;
    }

    @Override
    public char getSeparator() {
        return separator;
    }

    /**
     * @return String version of separator to reduce number of calls to toString.
     */
    public String getSeparatorAsString() {
        return separatorAsString;
    }

    @Override
    public char getQuotechar() {
        return quotechar;
    }

    /**
     * @return String version of quotechar to reduce the number of calls to toString.
     */
    public String getQuotecharAsString() {
        return quotecharAsString;
    }

    @Override
    public boolean isPending() {
        return pending != null;
    }


    @Override
    public String[] parseLineMulti(String nextLine) throws IOException {
        return parseLine(nextLine, true);
    }

    @Override
    public String[] parseLine(String nextLine) throws IOException {
        return parseLine(nextLine, false);
    }

    @Override
    public String parseToLine(String[] values, boolean applyQuotesToAll) {
        return Stream.of(values)
                .map(v -> convertToCsvValue(v, applyQuotesToAll))
                .collect(Collectors.joining(getSeparatorAsString()));
    }

    @Override
    public void parseToLine(String[] values, boolean applyQuotesToAll, Appendable appendable) throws IOException {
        boolean first = true;
        for (String value : values) {
            if (!first) {
                appendable.append(getSeparator());
            } else {
                first = false;
            }
            convertToCsvValue(value, applyQuotesToAll, appendable);
        }
    }

    /**
     * Used when reverse parsing an array of strings to a single string.  Handles the application of quotes around
     * the string and handling any quotes within the string.
     *
     * @param value            String to be converted
     * @param applyQuotestoAll All values should be surrounded with quotes
     * @return String that will go into the CSV string
     */
    protected abstract String convertToCsvValue(String value, boolean applyQuotestoAll);

    /**
     * Used when reverse parsing an array of strings to a single string.  Handles the application of quotes around
     * the string and handling any quotes within the string.
     * <p>
     * NOTE: as of 5.7.2 most objects will be inheriting a solution that calls the existing convertToCsvValue and thus
     * will not receive much benefit.
     *
     * @param value            String to be converted
     * @param applyQuotesToAll All values should be surrounded with quotes
     * @param appendable       Appendable object that the converted values are added to.
     */
    protected void convertToCsvValue(String value, boolean applyQuotesToAll, Appendable appendable) throws IOException {
        appendable.append(convertToCsvValue(value, applyQuotesToAll));
    }

    /**
     * Used by reverse parsing to determine if a value should be surrounded by quote characters.
     *
     * @param value         String to be tested
     * @param forceSurround If the value is not {@code null} it will be surrounded with quotes
     * @return True if the string should be surrounded with quotes, false otherwise
     */
    protected boolean isSurroundWithQuotes(String value, boolean forceSurround) {
        if (value == null) {
            return nullFieldIndicator.equals(CSVReaderNullFieldIndicator.EMPTY_QUOTES);
        } else if (value.isEmpty() && nullFieldIndicator.equals(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)) {
            return true;
        }

        return forceSurround || value.contains(getSeparatorAsString()) || value.contains(NEWLINE);
    }

    /**
     * Parses an incoming {@link java.lang.String} and returns an array of elements.
     *
     * @param nextLine The string to parse
     * @param multi    Whether it takes multiple lines to form a single record
     * @return The list of elements, or {@code null} if {@code nextLine} is {@code null}
     * @throws IOException If bad things happen during the read
     */
    protected abstract String[] parseLine(String nextLine, boolean multi) throws IOException;

    @Override
    public CSVReaderNullFieldIndicator nullFieldIndicator() {
        return nullFieldIndicator;
    }

    @Override
    public String getPendingText() {
        return StringUtils.defaultString(pending);
    }
}
