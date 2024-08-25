package com.opencsv.bean;

public abstract class AbstractMappingStrategyBuilder<S, T extends MappingStrategy<S>>  {
    protected Class<? extends S> type;

    public AbstractMappingStrategyBuilder<S, T> withType(Class<? extends S> type) {
        this.type = type;
        return this;
    }

    public abstract T build();
}
