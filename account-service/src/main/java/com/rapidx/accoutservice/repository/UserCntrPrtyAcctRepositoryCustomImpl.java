package com.rapidx.accoutservice.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UserCntrPrtyAcctRepositoryCustomImpl implements UserCntrPrtyAcctRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final String findAllActionAccountsSql;
    private final String findAllActionAccountsOfOrgSql;

    public UserCntrPrtyAcctRepositoryCustomImpl() {
        this.findAllActionAccountsSql = loadSql("queries/findAllActionAccounts.sql");
        this.findAllActionAccountsOfOrgSql = loadSql("queries/findAllActionAccountsOfOrg.sql");
    }

    private String loadSql(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load SQL file from classpath: " + path, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> findAllActionAccountsRaw() {
        Query query = entityManager.createNativeQuery(findAllActionAccountsSql);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> findAllActionAccountsOfOrgRaw(Integer orgId) {
        Query query = entityManager.createNativeQuery(findAllActionAccountsOfOrgSql);
        query.setParameter("orgId", orgId);
        return query.getResultList();
    }
}

