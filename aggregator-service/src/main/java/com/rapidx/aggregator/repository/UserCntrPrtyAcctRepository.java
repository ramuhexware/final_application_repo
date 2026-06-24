package com.rapidx.aggregator.repository;

import com.rapidx.aggregator.entity.UserCntrPrtyAcct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserCntrPrtyAcctRepository extends PagingAndSortingRepository<UserCntrPrtyAcct, Long>, JpaSpecificationExecutor<UserCntrPrtyAcct> {
    Optional<UserCntrPrtyAcct> findById(Long id);
    UserCntrPrtyAcct save(UserCntrPrtyAcct entity);
    void delete(UserCntrPrtyAcct entity);
    Page<UserCntrPrtyAcct> findDistinctByIdOrgtn(Integer idOrgtn, Pageable pageable);
}
