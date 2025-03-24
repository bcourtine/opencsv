package integrationTest.ParserDoubleQuoteHandling;

import com.opencsv.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

public class DoubleQuoteHandlingTest {
    public static final String[] TEST_DATA = new String[] {"{\"\"}"};
    private CSVWriterBuilder builder;
    private StringWriter sw;

    @BeforeEach
    public void setup() {
         sw = new StringWriter();
         builder = new CSVWriterBuilder(sw);
    }

    @Test
    @DisplayName("show the workings of the CSVParser")
    public void usingCSVParser() throws IOException {
        CSVParser parser = new CSVParserBuilder()
                .withEscapeChar(ICSVWriter.NO_ESCAPE_CHARACTER)
                .build();

       builder.withParser(parser)
                .build()
                .writeNext(TEST_DATA, true);


        String[] columns = parser.parseLine(sw.toString().trim());
        Assertions.assertEquals(TEST_DATA[0] ,columns[0]);
    }

    @Test
    @DisplayName("show the workings of the RFC4180Parser with applyQuotesToAll set to true")
    public void usingRFC4180Parser() throws IOException {
        RFC4180Parser parser = new RFC4180ParserBuilder()
                .build();

        builder.withParser(parser)
                .build()
                .writeNext(TEST_DATA, true);


        String[] columns = parser.parseLine(sw.toString().trim());
        Assertions.assertEquals(TEST_DATA[0] ,columns[0]);
    }

    @Test
    @DisplayName("show the workings of the RFC4180Parser with applyQuotesToAll set to false")
    public void usingRFC4180ParserNoApplyQuotesToAll() throws IOException {
        RFC4180Parser parser = new RFC4180ParserBuilder()
                .build();

        builder.withParser(parser)
                .build()
                .writeNext(TEST_DATA, false);


        String[] columns = parser.parseLine(sw.toString().trim());
        Assertions.assertEquals(TEST_DATA[0] ,columns[0]);
    }
}
