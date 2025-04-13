package integrationTest.Bug257;

import com.opencsv.RFC4180ParserBuilder;
import org.junit.jupiter.api.Test;
import com.opencsv.CSVParserBuilder;
import com.opencsv.ICSVParser;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Bug257Test {
    @Test
    public void parseToLineUsesCorrectSeparator() {
        CSVParserBuilder builder = new CSVParserBuilder();
        ICSVParser parser = builder.withSeparator('.').withQuoteChar('\'').build();

        String[] items = {"This", " is", " a", " test."};
        assertEquals("This. is. a.' test.'", parser.parseToLine(items, false));
    }

    @Test
    public void parseUsingRFC4180Parser() {
        RFC4180ParserBuilder builder = new RFC4180ParserBuilder();
        ICSVParser parser = builder.withSeparator('.').withQuoteChar('\'').build();

        String[] items = {"This", " is", " a", " test."};
        assertEquals("This. is. a.' test.'", parser.parseToLine(items, false));
    }
}
