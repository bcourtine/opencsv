package com.opencsv.bean;

import com.opencsv.CSVReader;
import com.opencsv.ICSVParser;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Allows for the mapping of columns with their positions. Using this strategy
 * without annotations ({@link com.opencsv.bean.CsvBindByPosition} or
 * {@link com.opencsv.bean.CsvCustomBindByPosition}) requires all the columns
 * to be present in the CSV file and for them to be in a particular order. Using
 * annotations allows one to specify arbitrary zero-based column numbers for
 * each bean member variable to be filled. Also this strategy requires that the
 * file does NOT have a header. That said, the main use of this strategy is
 * files that do not have headers.
 *
 * @param <T> Type of object that is being processed.
 */
public class ColumnPositionMappingStrategy<T> extends AbstractMappingStrategy<String, Integer, ComplexFieldMapEntry<String, Integer, T>, T> {

    /**
     * Whether the user has programmatically set the map from column positions
     * to field names.
     */
    private boolean columnsExplicitlySet = false;

    /**
     * The map from column position to {@link BeanField}.
     */
    private FieldMapByPosition<T> fieldMap;

    /**
     * Holds a {@link java.util.Comparator} to sort columns on writing.
     */
    private Comparator<Integer> writeOrder;

    /**
     * Used to store a mapping from presumed input column index to desired
     * output column index, as determined by applying {@link #writeOrder}.
     */
    private Integer[] columnIndexForWriting = null;

    /**
     * Default constructor.
     */
    public ColumnPositionMappingStrategy() {
    }

    /**
     * There is no header per se for this mapping strategy, but this method
     * checks the first line to determine how many fields are present and
     * adjusts its field map accordingly.
     */
    // The rest of the Javadoc is inherited
    @Override
    public void captureHeader(CSVReader reader) throws IOException {
        // Validation
        if (type == null) {
            throw new IllegalStateException(ResourceBundle
                    .getBundle(ICSVParser.DEFAULT_BUNDLE_NAME, errorLocale)
                    .getString("type.unset"));
        }

        String[] firstLine = ObjectUtils.defaultIfNull(reader.peek(), ArrayUtils.EMPTY_STRING_ARRAY);
        fieldMap.setMaxIndex(firstLine.length - 1);
        if (!columnsExplicitlySet) {
            headerIndex.clear();
            for (FieldMapByPositionEntry<T> entry : fieldMap) {
                Field f = entry.getField().getField();
                if (f.getAnnotation(CsvCustomBindByPosition.class) != null
                        || f.getAnnotation(CsvBindAndSplitByPosition.class) != null
                        || f.getAnnotation(CsvBindAndJoinByPosition.class) != null
                        || f.getAnnotation(CsvBindByPosition.class) != null) {
                    headerIndex.put(entry.getPosition(), f.getName().toUpperCase().trim());
                }
            }
        }
    }

    /**
     * @return {@inheritDoc} For this mapping strategy, it's simply
     * {@code index} wrapped as an {@link java.lang.Integer}.
     */
    // The rest of the Javadoc is inherited
    @Override
    protected Integer chooseMultivaluedFieldIndexFromHeaderIndex(int index) {
        return Integer.valueOf(index);
    }

    @Override
    protected BeanField<T, Integer> findField(int col) {
        // If we have a mapping for changing the order of the columns on
        // writing, be sure to use it.
        if (columnIndexForWriting != null) {
            return col < columnIndexForWriting.length ? fieldMap.get(columnIndexForWriting[col]) : null;
        }
        return fieldMap.get(col);
    }

    /**
     * This method returns an empty array.
     * The column position mapping strategy assumes that there is no header, and
     * thus it also does not write one, accordingly.
     *
     * @return An empty array
     */
    // The rest of the Javadoc is inherited
    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        String[] h = super.generateHeader(bean);
        columnIndexForWriting = new Integer[h.length];
        Arrays.setAll(columnIndexForWriting, i -> i);

        // Create the mapping for input column index to output column index.
        Arrays.sort(columnIndexForWriting, writeOrder);
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    /**
     * Gets a column name.
     *
     * @param col Position of the column.
     * @return Column name or null if col &gt; number of mappings.
     */
    @Override
    public String getColumnName(int col) {
        return headerIndex.getByPosition(col);
    }

    /**
     * Retrieves the column mappings.
     *
     * @return String array with the column mappings.
     */
    public String[] getColumnMapping() {
        return headerIndex.getHeaderIndex();
    }

    /**
     * Setter for the column mapping.
     * This mapping is for reading. Use of this method in conjunction with
     * writing is undefined.
     *
     * @param columnMapping Column names to be mapped.
     */
    public void setColumnMapping(String... columnMapping) {
        if (columnMapping != null) {
            headerIndex.initializeHeaderIndex(columnMapping);
        } else {
            headerIndex.clear();
        }
        columnsExplicitlySet = true;
        if(getType() != null) {
            loadFieldMap(); // In case setType() was called first.
        }
    }

    /**
     * Creates a map of annotated fields in the bean to be processed.
     * <p>This method is called by {@link #loadFieldMap()} when at least one
     * relevant annotation is found on a member variable.</p>
     */
    @Override
    protected void loadAnnotatedFieldMap(ListValuedMap<Class<?>, Field> fields) {
        boolean required;
        for (Map.Entry<Class<?>, Field> classAndField : fields.entries()) {
            Class<?> localType = classAndField.getKey();
            Field localField = classAndField.getValue();
            String fieldLocale, fieldWriteLocale, capture, format;

            // Custom converters always have precedence.
            if (localField.isAnnotationPresent(CsvCustomBindByPosition.class)) {
                CsvCustomBindByPosition annotation = localField
                        .getAnnotation(CsvCustomBindByPosition.class);
                @SuppressWarnings("unchecked")
                Class<? extends AbstractBeanField<T, Integer>> converter = (Class<? extends AbstractBeanField<T, Integer>>)annotation.converter();
                BeanField<T, Integer> bean = instantiateCustomConverter(converter);
                bean.setType(localType);
                bean.setField(localField);
                required = annotation.required();
                bean.setRequired(required);
                fieldMap.put(annotation.position(), bean);
            }

            // Then check for a collection
            else if (localField.isAnnotationPresent(CsvBindAndSplitByPosition.class)) {
                CsvBindAndSplitByPosition annotation = localField.getAnnotation(CsvBindAndSplitByPosition.class);
                required = annotation.required();
                fieldLocale = annotation.locale();
                fieldWriteLocale = annotation.writeLocaleEqualsReadLocale()
                        ? fieldLocale
                        : annotation.writeLocale();
                String splitOn = annotation.splitOn();
                String writeDelimiter = annotation.writeDelimiter();
                Class<? extends Collection> collectionType = annotation.collectionType();
                Class<?> elementType = annotation.elementType();
                Class<? extends AbstractCsvConverter> splitConverter = annotation.converter();
                capture = annotation.capture();
                format = annotation.format();

                CsvConverter converter = determineConverter(localField, elementType, fieldLocale, fieldWriteLocale, splitConverter);
                fieldMap.put(annotation.position(), new BeanFieldSplit<>(
                        localType, localField, required, errorLocale, converter, splitOn,
                        writeDelimiter, collectionType, capture, format));
            }

            // Then check for a multi-column annotation
            else if (localField.isAnnotationPresent(CsvBindAndJoinByPosition.class)) {
                CsvBindAndJoinByPosition annotation = localField.getAnnotation(CsvBindAndJoinByPosition.class);
                required = annotation.required();
                fieldLocale = annotation.locale();
                fieldWriteLocale = annotation.writeLocaleEqualsReadLocale()
                        ? fieldLocale
                        : annotation.writeLocale();
                Class<?> elementType = annotation.elementType();
                Class<? extends MultiValuedMap> mapType = annotation.mapType();
                Class<? extends AbstractCsvConverter> joinConverter = annotation.converter();
                capture = annotation.capture();
                format = annotation.format();

                CsvConverter converter = determineConverter(localField, elementType, fieldLocale, fieldWriteLocale, joinConverter);
                fieldMap.putComplex(annotation.position(), new BeanFieldJoinIntegerIndex<>(
                        localType, localField, required, errorLocale, converter, mapType, capture, format));
            }

            // Then it must be a bind by position.
            else {
                CsvBindByPosition annotation = localField.getAnnotation(CsvBindByPosition.class);
                required = annotation.required();
                fieldLocale = annotation.locale();
                fieldWriteLocale = annotation.writeLocaleEqualsReadLocale()
                        ? fieldLocale
                        : annotation.writeLocale();
                capture = annotation.capture();
                format = annotation.format();
                CsvConverter converter = determineConverter(localField, localField.getType(), fieldLocale, fieldWriteLocale, null);

                fieldMap.put(annotation.position(), new BeanFieldSingleValue<>(
                        localType, localField, required, errorLocale, converter, capture, format));
            }
        }
    }

    @Override
    protected void loadUnadornedFieldMap(ListValuedMap<Class<?>, Field> fields) {
        for(Map.Entry<Class<?>, Field> classAndField : fields.entries()) {
            Class<?> localType = classAndField.getKey();
            Field localField = classAndField.getValue();
            CsvConverter converter = determineConverter(localField, localField.getType(), null, null, null);
            int[] indices = headerIndex.getByName(localField.getName());
            if(indices.length != 0) {
                fieldMap.put(indices[0], new BeanFieldSingleValue<>(
                        localType, localField, false, errorLocale, converter, null, null));
            }
        }
    }

    /**
     * Returns a set of the annotations that are used for binding in this
     * mapping strategy.
     * <p>In this mapping strategy, those are currently:<ul>
     *     <li>{@link CsvBindByPosition}</li>
     *     <li>{@link CsvCustomBindByPosition}</li>
     *     <li>{@link CsvBindAndJoinByPosition}</li>
     *     <li>{@link CsvBindAndSplitByPosition}</li>
     * </ul></p>
     */
    @Override
    protected Set<Class<? extends Annotation>> getBindingAnnotations() {
        // With Java 9 this can be done more easily with Set.of()
        return new HashSet<>(Arrays.asList(
                CsvBindByPosition.class,
                CsvCustomBindByPosition.class,
                CsvBindAndJoinByPosition.class,
                CsvBindAndSplitByPosition.class));
    }

    @Override
    protected void initializeFieldMap() {
        fieldMap = new FieldMapByPosition<>(errorLocale);
        fieldMap.setColumnOrderOnWrite(writeOrder);
    }

    @Override
    protected void verifyLineLength(int numberOfFields) throws CsvRequiredFieldEmptyException {
        if (!headerIndex.isEmpty()) {
            BeanField<T, Integer> f;
            StringBuilder sb = null;
            for (int i = numberOfFields; i <= headerIndex.findMaxIndex(); i++) {
                f = findField(i);
                if (f != null && f.isRequired()) {
                    if (sb == null) {
                        sb = new StringBuilder(ResourceBundle.getBundle(ICSVParser.DEFAULT_BUNDLE_NAME, errorLocale).getString("multiple.required.field.empty"));
                    }
                    sb.append(' ');
                    sb.append(f.getField().getName());
                }
            }
            if (sb != null) {
                throw new CsvRequiredFieldEmptyException(type, sb.toString());
            }
        }
    }

    /**
     * Returns the column position for the given column number.
     * Yes, they're the same thing. For this mapping strategy, it's a simple
     * conversion from an integer to a string.
     */
    // The rest of the Javadoc is inherited
    @Override
    public String findHeader(int col) {
        return Integer.toString(col);
    }

    @Override
    protected FieldMap<String, Integer, ? extends ComplexFieldMapEntry<String, Integer, T>, T> getFieldMap() {
        return fieldMap;
    }

    /**
     * Sets the {@link java.util.Comparator} to be used to sort columns when
     * writing beans to a CSV file.
     * Behavior of this method when used on a mapping strategy intended for
     * reading data from a CSV source is not defined.
     *
     * @param writeOrder The {@link java.util.Comparator} to use. May be
     *                   {@code null}, in which case the natural ordering is used.
     * @since 4.3
     */
    public void setColumnOrderOnWrite(Comparator<Integer> writeOrder) {
        this.writeOrder = writeOrder;
        if (fieldMap != null) {
            fieldMap.setColumnOrderOnWrite(this.writeOrder);
        }
    }
}
