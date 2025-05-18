package integrationTest.SR121;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SR121Test {

    @Test
    public void newlineEscapeCharacterIssue() {
        // Create a File object with the name of the text file to create.
        File csvFilePath = new File("my_text_file_with_content.txt");

// Try to create the file. If the file already exists, it will be overwritten.
        try (FileWriter writer = new FileWriter(csvFilePath)) {
            // Write the text to the file.
            writer.write("\"ID\"|\"TYPE\"|\"PROFILE\"");
            writer.write("\n");
            writer.write("\"123\"|\"MISC\"|\"Profile ABC\"");
            writer.write("\n");
            writer.write("\"456\"|\"MISC\"|\"Profile DEF\\n& Profile GHI\"");
            writer.write("\n");
            writer.write("\"789\"|\"MISC\"|\"Profile \\\"JKL\\\"\"");
            writer.write("\n");
        } catch (IOException e) {
            System.out.println("An error occurred while trying to create the file: " + e.getMessage());
        }

// Print a message indicating that the file was created successfully.
        System.out.println("The file was created successfully!");

        CSVParser csvParser = new CSVParserBuilder()
                .withSeparator('|')
                .withQuoteChar('\"')
                .withEscapeChar(CSVParser.DEFAULT_ESCAPE_CHARACTER)
                .withIgnoreQuotations(false)
                .build();

        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(csvFilePath.getPath())).withCSVParser(csvParser).build()) {
            // Read all lines from the CSV file
            List<String[]> allLines = csvReader.readAll();

            // Iterate over the lines and print them to the console
            for (String[] line : allLines) {
                for (String column : line) {
                    System.out.print(column + "\t");
                }
                System.out.println();
            }

            // Close the CSV reader
            csvReader.close();
        } catch (IOException | CsvException e) {
            System.out.println("An error occurred while trying to open the file: " + e.getMessage());
        }
    }
}
