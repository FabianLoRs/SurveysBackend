package com.flopr.surveysbackend.utils.transformer;

public interface Transformer<K, T> {
    T transformData(K data);
}
