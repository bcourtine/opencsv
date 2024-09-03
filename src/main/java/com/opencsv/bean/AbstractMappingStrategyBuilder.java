package com.opencsv.bean;

public abstract class AbstractMappingStrategyBuilder<S, T extends MappingStrategy<S>>  {
    protected Class<? extends S> type;

    /**
     * Add type to the builder.  Will be passed to the strategy when the build is called.
     *
     * @param type - class type
     * @return builder with the class type set.
     */
    public AbstractMappingStrategyBuilder<S, T> withType(Class<? extends S> type) {
        this.type = type;
        return this;
    }

    /**
     * Builds a new mapping strategy for parsing/writing.
     * @return A new mapping strategy using the options selected
     */
    public abstract T build();
}
