package com.quickstep.ui.dataproviders;

import com.vaadin.flow.data.provider.*;
import com.vaadin.flow.function.SerializableBiFunction;
import com.vaadin.flow.function.SerializableFunction;

import java.util.stream.Stream;

public class FilterableDataProvider<T,F> extends AbstractBackEndDataProvider<T, F> {

    @Override
    protected Stream<T> fetchFromBackEnd(Query<T, F> query) {
        return null;
    }

    @Override
    protected int sizeInBackEnd(Query<T, F> query) {
        return 0;
    }

    @Override
    public void setSortOrders(QuerySortOrderBuilder builder) {

    }

    @Override
    public void setSortOrder(QuerySortOrder sortOrder) {

    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public Object getId(T item) {
        return null;
    }

    @Override
    public <C> DataProvider<T, C> withConvertedFilter(SerializableFunction<C, F> filterConverter) {
        return null;
    }

    @Override
    public <Q, C> ConfigurableFilterDataProvider<T, Q, C> withConfigurableFilter(SerializableBiFunction<Q, C, F> filterCombiner) {
        return null;
    }

    @Override
    public ConfigurableFilterDataProvider<T, Void, F> withConfigurableFilter() {
        return null;
    }
}
