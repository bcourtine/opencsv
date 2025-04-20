/*
 * Copyright 2016 Andrew Rucker Jones.
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
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.locale.LocaleConvertUtilsBean;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class wraps fields from the reflection API in order to handle
 * translation of primitive types and to add a "required" flag.
 *
 * @author Andrew Rucker Jones
 * @since 4.2 (previously BeanFieldPrimitiveTypes since 3.8)
 */
public class ConverterPrimitiveTypes extends AbstractCsvConverter {

    /**
     * The formatter for all inputs to wrapped and unwrapped primitive
     * types when a specific locale is not required.
     * <p>Either this or {@link #readLocaleConverter} should be used, and the
     * other should always be {@code null}.</p>
     * <p><em>It is absolutely critical that access to this member variable is
     * always synchronized!</em></p>
     */
    protected final ConvertUtilsBean readConverter;

    /**
     * The formatter for all inputs to wrapped and unwrapped primitive
     * types when a specific locale is required.
     * <p>Either this or {@link #readConverter} should be used, and the other
     * should always be {@code null}.</p>
     * <p><em>It is absolutely critical that access to this member variable is
     * always synchronized!</em></p>
     */
    protected final LocaleConvertUtilsBean readLocaleConverter;

    /**
     * The formatter for all inputs from wrapped and unwrapped primitive
     * types when a specific locale is not required.
     * <p>Either this or {@link #writeLocaleConverter} should be used, and the
     * other should always be {@code null}.</p>
     * <p><em>It is absolutely critical that access to this member variable is
     * always synchronized!</em></p>
     */
    protected final ConvertUtilsBean writeConverter;

    /**
     * The formatter for all inputs from wrapped and unwrapped primitive
     * types when a specific locale is required.
     * <p>Either this or {@link #writeConverter} should be used, and the other
     * should always be {@code null}.</p>
     * <p><em>It is absolutely critical that access to this member variable is
     * always synchronized!</em></p>
     */
    protected final LocaleConvertUtilsBean writeLocaleConverter;

    /**
     * @param type    The class of the type of the data being processed
     * @param locale   If not null or empty, specifies the locale used for
     *                 converting locale-specific data types
     * @param writeLocale   If not null or empty, specifies the locale used for
     *                 converting locale-specific data types for writing
     * @param errorLocale The locale to use for error messages.
     */
    public ConverterPrimitiveTypes(Class<?> type, String locale, String writeLocale, Locale errorLocale) {
        super(type, locale, writeLocale, errorLocale);
        if(this.locale == null) {
            readConverter = BeanUtilsBean.getInstance().getConvertUtils();
            readLocaleConverter = null;
            if (readConverter.lookup(type) == null) {
                readConverter.register(true, false, 0);
            }
        }
        else {
            readLocaleConverter = new LocaleConvertUtilsBean();
            readLocaleConverter.setDefaultLocale(this.locale);
            readConverter = null;
        }
        if(this.writeLocale == null) {
            writeConverter = BeanUtilsBean.getInstance().getConvertUtils();
            writeLocaleConverter = null;
            if (writeConverter.lookup(type) == null) {
                writeConverter.register(true, false, 0);
            }
        }
        else {
            writeLocaleConverter = new LocaleConvertUtilsBean();
            writeLocaleConverter.setDefaultLocale(this.writeLocale);
            writeConverter = null;
        }
    }

    @Override
    public Object convertToRead(String value)
            throws CsvDataTypeMismatchException {
        Object o = null;

        if (StringUtils.isNotBlank(value) || (value != null && type.equals(String.class))) {
            try {
                if(readConverter != null) {
                    synchronized (readConverter) {
                        o = readConverter.convert(value, type);
                    }
                }
                else {
                    synchronized (readLocaleConverter) {
                        o = readLocaleConverter.convert(value, type);
                    }
                }
            } catch (ConversionException e) {
                CsvDataTypeMismatchException csve = new CsvDataTypeMismatchException(
                        value, type, String.format(
                                ResourceBundle.getBundle(ICSVParser.DEFAULT_BUNDLE_NAME, errorLocale).getString("conversion.impossible"),
                                value, type.getCanonicalName()));
                csve.initCause(e);
                throw csve;
            }
        }
        return o;
    }
    
    /**
     * This method takes the current value of the field in question in the bean
     * passed in and converts it to a string.
     * It works for all of the primitives, wrapped primitives,
     * {@link java.lang.String}, {@link java.math.BigDecimal}, and
     * {@link java.math.BigInteger}.
     * 
     * @throws CsvDataTypeMismatchException If there is an error converting
     *   value to a string
     */
    // The rest of the Javadoc is automatically inherited from the base class.
    @Override
    public String convertToWrite(Object value)
            throws CsvDataTypeMismatchException {
        String result = null;
        if(value != null) {
            try {
                if(writeConverter != null) {
                    synchronized (writeConverter) {
                        result = writeConverter.convert(value);
                    }
                }
                else {
                    synchronized (writeLocaleConverter) {
                        result = writeLocaleConverter.convert(value);
                    }
                }
            }
            catch(ConversionException e) {
                CsvDataTypeMismatchException csve = new CsvDataTypeMismatchException(
                        ResourceBundle.getBundle(ICSVParser.DEFAULT_BUNDLE_NAME, errorLocale).getString("field.not.primitive"));
                csve.initCause(e);
                throw csve;
            }
        }
        return result;
    }
}
