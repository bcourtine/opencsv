package integrationTest.SR120;

import com.opencsv.bean.CsvToBeanBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AllowSpacesForRequiredFieldsHeaders {
    public StringBuilder fileString;

    @BeforeEach
    public void createFileData() {
        fileString = new StringBuilder(1024);

        fileString.append("field1, field2, field3\n");
        fileString.append("fi,fi,fo\n");
        fileString.append("doh,reh,mi\n");
    }

    @Test
    @DisplayName("BindByName with no required fields.")
    public void noRequiredFields() {
        List<ExampleBean1> beans1 = new CsvToBeanBuilder<ExampleBean1>(new StringReader(fileString.toString()))
                .withType(ExampleBean1.class).build()
                .parse();
        assertEquals(2, beans1.size());
    }

    @Test
    @DisplayName("BindByName with required fields.")
    public void bindByNameWithRequiredFields() {
        List<ExampleBean2> beans2 = new CsvToBeanBuilder<ExampleBean2>(new StringReader(fileString.toString()))
                .withType(ExampleBean2.class).build()
                .parse();
        assertEquals(2, beans2.size());
    }
}
