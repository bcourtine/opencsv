package com.opencsv.bean;

/**
 * Builder for a {@link ColumnPositionMappingStrategy}.
 * This allows opencsv to introduce new options for mapping strategies
 * while maintaining backward compatibility and without creating
 * reams of constructors for the mapping strategy.
 *
 * @param <T> The type of the bean being processed
 * @since 5.5
 * @author Andrew Rucker Jones
 */
public class ColumnPositionMappingStrategyBuilder<T>
        extends AbstractMappingStrategyBuilder<T, ColumnPositionMappingStrategy<T>> {

    /** Default constructor. */
    public ColumnPositionMappingStrategyBuilder() {}

    @Override
    public ColumnPositionMappingStrategy<T> build() {
        ColumnPositionMappingStrategy<T> builder = new ColumnPositionMappingStrategy<>();
        if (type != null) {
            builder.setType(type);
        }
        return builder;
    }
}
