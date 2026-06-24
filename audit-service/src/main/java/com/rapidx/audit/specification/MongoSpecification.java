package com.rapidx.audit.specification;

import org.springframework.data.mongodb.core.query.Criteria;

@FunctionalInterface
public interface MongoSpecification<T> {
    Criteria toMongoCriteria();
}
