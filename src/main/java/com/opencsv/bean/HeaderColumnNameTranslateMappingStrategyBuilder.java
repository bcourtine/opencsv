package com.opencsv.bean;

/**
 * Builder for a {@link HeaderColumnNameMappingStrategy}.
 * This allows opencsv to introduce new options for mapping strategies
 * while maintaining backward compatibility and without creating
 * reams of constructors for the mapping strategy.
 *
 * @param <T> The type of the bean being processed
 * @since 5.5
 * @author Andrew Rucker Jones
 */
public class HeaderColumnNameTranslateMappingStrategyBuilder<T>
        extends AbstractMappingStrategyBuilder<T, HeaderColumnNameTranslateMappingStrategy<T>> {

    private boolean forceCorrectRecordLength = false;

    /** Default constructor. */
    public HeaderColumnNameTranslateMappingStrategyBuilder() {}

    /**
     * Builds a new mapping strategy for parsing/writing.
     * @return A new mapping strategy using the options selected
     */
    @Override
    public HeaderColumnNameTranslateMappingStrategy<T> build() {
        HeaderColumnNameTranslateMappingStrategy<T> builder = new HeaderColumnNameTranslateMappingStrategy<>(forceCorrectRecordLength);
        if (type != null) {
            builder.setType(type);
        }
        return builder;
    }

    /**
     * Insists that every record will be considered to be of the correct
     * length (that is, the same number of columns as the header).
     * <p>Excess fields at the end of a record will be ignored. Missing
     * fields at the end of a record will be interpreted as {@code null}.
     * This is only relevant on reading.</p>
     * <p>If not set, incorrect record length will throw an exception. That
     * is, the default value is {@code false}.</p>
     *
     * @param forceCorrectRecordLength Whether records should be forced to
     *                                 the correct length
     * @return {@code this}
     */
    public HeaderColumnNameTranslateMappingStrategyBuilder<T> withForceCorrectRecordLength(boolean forceCorrectRecordLength) {
        this.forceCorrectRecordLength = forceCorrectRecordLength;
        return this;
    }
}
