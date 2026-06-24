package com.rapidx.accoutservice.repository;

import java.util.List;

public interface UserCntrPrtyAcctRepositoryCustom {
    List<Object[]> findAllActionAccountsRaw();
    List<Object[]> findAllActionAccountsOfOrgRaw(Integer orgId);
}

