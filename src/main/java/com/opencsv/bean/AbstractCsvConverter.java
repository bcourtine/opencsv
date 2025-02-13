/*
 * Copyright 2017 Andrew Rucker Jones.
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

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.Objects;

/**
 * This implementation of {@link CsvConverter} provides a reasonable default
 * for {@link CsvConverter#convertToWrite(java.lang.Object)} as well as a couple
 * of common fields.
 * 
 * @author Andrew Rucker Jones
 * @since 4.2
 */
public abstract class AbstractCsvConverter implements CsvConverter {
    
    /**
     * The type to which (on reading) or from which (on writing) conversion
     * is being performed.
     */
    protected Class<?> type;
    
    /**
     * The locale to be used when converting for reading, if a locale is relevant.
     */
    protected Locale locale;

    /**
     * The locale to be used when converting for writing, if a locale is
     * relevant.
     */
    protected Locale writeLocale;
    
    /** The locale to be used for error messages. */
    protected Locale errorLocale;

    /**
     * Default nullary constructor, so derived classes aren't forced to create
     * a constructor identical to this one.
     *
     * @since 4.3
     */
    public AbstractCsvConverter() {
        this.type = null;
        this.locale = null;
        this.writeLocale = null;
        errorLocale = Locale.getDefault();
    }

    /**
     * Currently the only constructor for this class.
     * 
     * @param type The type to which (on reading) or from which (on writing) is
     *   being converted
     * @param locale The locale to be used when converting for reading, if a
     *               locale is relevant
     * @param writeLocale The locale to be used when converting for writing, if
     *                    a locale is relevant
     * @param errorLocale The locale to be used for error messages
     */
    public AbstractCsvConverter(Class<?> type, String locale, String writeLocale, Locale errorLocale) {
        this.type = type;
        this.locale = StringUtils.isNotEmpty(locale) ? Locale.forLanguageTag(locale) : null;
        this.writeLocale = StringUtils.isNotEmpty(writeLocale) ? Locale.forLanguageTag(writeLocale) : null;
        this.errorLocale = ObjectUtils.defaultIfNull(errorLocale, Locale.getDefault());
    }
    
    /**
     * This implementation simply calls {@code toString()} on {@code value}.
     * For complex types, overriding the {@code toString()} method in the type
     * of the field in question would be an alternative to writing a conversion
     * routine in a class derived from this one.
     * 
     * @param value The contents of the field currently being processed from the
     *   bean to be written. Can be null if the field is not marked as required.
     * @return A string representation of the value of the field in question in
     *   the bean passed in, or an empty string if {@code value} is null
     * @throws CsvDataTypeMismatchException This implementation doesn't, but
     *   subclasses do, so it must be declared
     */
    @Override
    public String convertToWrite(Object value)
            throws CsvDataTypeMismatchException {
        // Since we have no concept of which field is required at this level,
        // we can't check for null and throw an exception.
        return Objects.toString(value, StringUtils.EMPTY);
    }
    
    @Override
    public void setErrorLocale(Locale errorLocale) {
        this.errorLocale = ObjectUtils.defaultIfNull(errorLocale, Locale.getDefault());
    }

    @Override
    public void setType(Class<?> type) {
        this.type = type;
    }

    @Override
    public void setLocale(String locale) {
        this.locale = StringUtils.isNotEmpty(locale) ? Locale.forLanguageTag(locale) : null;
    }

    @Override
    public void setWriteLocale(String writeLocale) {
        this.writeLocale = StringUtils.isNotEmpty(writeLocale)
                ? Locale.forLanguageTag(writeLocale)
                : null;
    }
}
