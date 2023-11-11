/*
 * Copyright 2018 Andrew Rucker Jones.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opencsv.bean;

import com.opencsv.ICSVParser;
import com.opencsv.exceptions.CsvBadConverterException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

/**
 * This converter class is used in combination with {@link CsvNumber}, that is,
 * when number inputs and outputs should be formatted.
 *
 * @author Andrew Rucker Jones
 * @since 4.2
 */
public class ConverterNumber extends AbstractCsvConverter {

    private final DecimalFormat readFormatter, writeFormatter;
    private final UnaryOperator<Number> readConversionFunction;

    /**
     * @param type    The class of the type of the data being processed
     * @param locale   If not null or empty, specifies the locale used for
     *                 converting locale-specific data types for reading
     * @param writeLocale   If not null or empty, specifies the locale used for
     *                 converting locale-specific data types for writing
     * @param errorLocale The locale to use for error messages
     * @param readFormat The string to use for parsing the number.
     * @param writeFormat The string to use for formatting the number.
     * @param roundingMode The rounding mode used when converting {@link java.math.BigDecimal}s.
     * @throws CsvBadConverterException If the information given to initialize the converter are inconsistent (e.g.
     *   the annotation {@link com.opencsv.bean.CsvNumber} has been applied to a non-{@link java.lang.Number} type.
     * @see com.opencsv.bean.CsvNumber#value()
     */
    public ConverterNumber(Class<?> type, String locale, String writeLocale, Locale errorLocale,
            String readFormat, String writeFormat, RoundingMode roundingMode)
            throws CsvBadConverterException {
        super(type, locale, writeLocale, errorLocale);

        // Check that the bean member is of an applicable type
        if(!Number.class.isAssignableFrom(
                this.type.isPrimitive()
                        ? ClassUtils.primitiveToWrapper(this.type)
                        : this.type)) {
            throw new CsvBadConverterException(
                    ConverterNumber.class,
                    ResourceBundle.getBundle(
                            ICSVParser.DEFAULT_BUNDLE_NAME,
                            this.errorLocale)
                            .getString("csvnumber.not.number"));
        }

        // Set up the read formatter
        readFormatter = createDecimalFormat(readFormat, this.locale, roundingMode);

        // Account for BigDecimal and BigInteger, which require special
        // processing
        if(this.type == BigInteger.class || this.type == BigDecimal.class) {
            readFormatter.setParseBigDecimal(true);
        }

        // Save the read conversion function for later
        if(this.type == Byte.class || this.type == Byte.TYPE) {
            readConversionFunction = Number::byteValue;
        }
        else if(this.type == Short.class || this.type == Short.TYPE) {
            readConversionFunction = Number::shortValue;
        }
        else if(this.type == Integer.class || this.type == Integer.TYPE) {
            readConversionFunction = Number::intValue;
        }
        else if(this.type == Long.class || this.type == Long.TYPE) {
            readConversionFunction = Number::longValue;
        }
        else if(this.type == Float.class || this.type == Float.TYPE) {
            readConversionFunction = Number::floatValue;
        }
        else if(this.type == Double.class || this.type == Double.TYPE) {
            readConversionFunction = Number::doubleValue;
        }
        else if(this.type == BigInteger.class) {
            readConversionFunction = n -> ((BigDecimal) n).toBigInteger();
        }
        else {
            // Either it's already a BigDecimal and nothing need be done,
            // or it's some derivative of java.lang.Number that we couldn't be
            // expected to know and accommodate for. In the latter case, a class
            // cast exception will be thrown later on assignment.
            readConversionFunction = n -> n;
        }

        // Set up the write formatter
        writeFormatter = createDecimalFormat(writeFormat, this.writeLocale, roundingMode);
    }

    private DecimalFormat createDecimalFormat(String format, Locale locale, RoundingMode roundingMode) {
        NumberFormat nf = NumberFormat.getInstance(ObjectUtils.defaultIfNull(locale, Locale.getDefault(Locale.Category.FORMAT)));
        if (!(nf instanceof DecimalFormat)) {
            throw new CsvBadConverterException(
                    ConverterNumber.class,
                    ResourceBundle.getBundle(
                            ICSVParser.DEFAULT_BUNDLE_NAME,
                            this.errorLocale)
                            .getString("numberformat.not.decimalformat"));
        }
        DecimalFormat formatter = (DecimalFormat) nf;

        try {
            formatter.applyLocalizedPattern(format);
        } catch (IllegalArgumentException e) {
            CsvBadConverterException csve = new CsvBadConverterException(
                    ConverterNumber.class,
                    String.format(ResourceBundle.getBundle(
                            ICSVParser.DEFAULT_BUNDLE_NAME,
                            this.errorLocale)
                                    .getString("invalid.number.pattern"),
                            format));
            csve.initCause(e);
            throw csve;
        }
        
        formatter.setRoundingMode(roundingMode);

        return formatter;
    }

    @Override
    public Object convertToRead(String value) throws CsvDataTypeMismatchException {
        Number n = null;
        if(StringUtils.isNotEmpty(value)) {
            try {
                synchronized (readFormatter) {
                    n = readFormatter.parse(value);
                }
            }
            catch(ParseException | ArithmeticException e) {
                CsvDataTypeMismatchException csve = new CsvDataTypeMismatchException(
                        value, type,
                        String.format(ResourceBundle.getBundle(
                                ICSVParser.DEFAULT_BUNDLE_NAME,
                                errorLocale)
                                .getString("unparsable.number"), value, readFormatter.toPattern()));
                csve.initCause(e);
                throw csve;
            }

            n = readConversionFunction.apply(n);
        }
        return n;
    }

    /**
     *  Formats the number in question according to the pattern that has been
     *  provided.
     */
    // The rest of the Javadoc is inherited.
    @Override
    public String convertToWrite(Object value) throws CsvDataTypeMismatchException {
        synchronized (writeFormatter) {
            try {
                return value != null ? writeFormatter.format(value) : null;
            }
            catch (ArithmeticException e) {
                CsvDataTypeMismatchException csve = new CsvDataTypeMismatchException(
                        value, type,
                        String.format(ResourceBundle.getBundle(
                                ICSVParser.DEFAULT_BUNDLE_NAME,
                                errorLocale)
                                .getString("unparsable.number"), value, writeFormatter.toPattern()));
                csve.initCause(e);
                throw csve;
            }
        }
    }
}
