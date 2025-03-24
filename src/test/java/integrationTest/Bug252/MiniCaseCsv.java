package integrationTest.Bug252;

import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MiniCaseCsv {

    @Test
    void testExportByName() {
        // given
        final CsvNameExporter csvExporter = new CsvNameExporter();
        final ExportRow exportRow = new ExportRowName();
        exportRow.setNumber(new BigDecimal("1.23456789"));

        final List<ExportRow> exportRowList = new ArrayList<>();
        exportRowList.add(exportRow);

        // when
        final String export = csvExporter.export(exportRowList, ExportRowName.class);
        // then
        final String secondLine = export.split("\n")[1];
        assertEquals("1.2", secondLine);
    }

    @Test
    void testExportByPosition() {
        // given
        final CsvExporter csvExporter = new CsvExporter();
        final ExportRow exportRow = new ExportRowPosition();
        exportRow.setNumber(new BigDecimal("1.23456789"));

        final List<ExportRow> exportRowList = new ArrayList<>();
        exportRowList.add(exportRow);

        // when
        final String export = csvExporter.export(exportRowList, ExportRowPosition.class);
        // then
        final String secondLine = export.split("\n")[1];
        assertEquals("1.2", secondLine);
    }

    class CsvExporter<T extends ExportRow> {
        public String export(final List<T> rows, final Class<T> clazz) {

            final Writer writer = new StringWriter();
            final ColumnPositionMappingStrategy<T> strategy = new ColumnPositionMappingStrategy<T>() {
                @Override
                public String[] generateHeader(final T row) {
                    return new String[]{"number"};
                }
            };

            strategy.setType(clazz);
            strategy.setColumnMapping("number");

            final StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    .withMappingStrategy(strategy)
                    .withQuotechar('"')
                    .withSeparator(',')
                    .withLineEnd("\n")
                    .withApplyQuotesToAll(false)
                    .withOrderedResults(true)
                    .build();

            try {
                beanToCsv.write(rows);
                return writer.toString();
            } catch (final CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                throw new RuntimeException("Error while exporting to csv", e);
            }
        }

    }

    class CsvNameExporter<T extends ExportRow> {
        public String export(final List<T> rows, final Class<T> clazz) {

            final Writer writer = new StringWriter();
            final HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<T>();

            strategy.setType(clazz);

            final StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    .withMappingStrategy(strategy)
                    .withQuotechar('"')
                    .withSeparator(',')
                    .withLineEnd("\n")
                    .withApplyQuotesToAll(false)
                    .withOrderedResults(true)
                    .build();

            try {
                beanToCsv.write(rows);
                return writer.toString();
            } catch (final CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                throw new RuntimeException("Error while exporting to csv", e);
            }
        }

    }

    interface ExportRow {
        void setNumber(BigDecimal bigDecimal);
    }

    public static class ExportRowName implements ExportRow {

        @CsvCustomBindByName(column = "number", converter = BigDecimalFormatterTest.class)
        @CsvBigDecimalFormat(format = "20.1")
        private BigDecimal number;

        @Override
        public void setNumber(final BigDecimal bigDecimal) {
            this.number = bigDecimal;
        }

        public BigDecimal getNumber() {
            return number;
        }
    }

    public static class ExportRowPosition implements ExportRow {

        @CsvCustomBindByPosition(position = 0, converter = BigDecimalFormatterTest.class)
        @CsvBigDecimalFormat(format = "20.1")
        private BigDecimal number;

        @Override
        public void setNumber(final BigDecimal bigDecimal) {
            this.number = bigDecimal;
        }

        public BigDecimal getNumber() {
            return number;
        }

    }

    public static class BigDecimalFormatterTest<T, I> extends AbstractBeanField<T, I> {

        public BigDecimalFormatterTest() {
        }

        @Override
        protected Object convert(final String value) {
            throw new UnsupportedOperationException("Only writing of CSV files is possible");
        }

        @Override
        protected String convertToWrite(final Object value) {
            if (value == null) {
                return "";
            } else {
                final CsvBigDecimalFormat annotation = field.getAnnotation(CsvBigDecimalFormat.class);
                final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                if (annotation != null) {
                    ((DecimalFormat) numberFormat).applyPattern(parseFormat(annotation.format()));
                }
                return numberFormat.format(value);
            }
        }

        private String parseFormat(final String format) {
            final String[] split = format.split("\\.");
            if (split.length == 2) {
                final int digits = Integer.parseInt(split[0]);
                final int decimalPlaces = Integer.parseInt(split[1]);
                return StringUtils.repeat("#", digits) + "." + StringUtils.repeat("#", decimalPlaces);
            } else {
                final int digits = Integer.parseInt(split[0]);
                return StringUtils.repeat("#", digits);
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface CsvBigDecimalFormat {

        String format();

    }

}
