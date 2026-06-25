package com.rapidx.aggregator.service;

public interface ApiService {
    <T> void get(String url, Object object, String operation, Class<T> responseType);
}
