package com.rapidx.accoutservice.repository;

import com.rapidx.accoutservice.entity.UcsOrgtnQuickSearchView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UcsOrgtnQuickSearchViewRepository extends PagingAndSortingRepository<UcsOrgtnQuickSearchView, Long>, JpaSpecificationExecutor<UcsOrgtnQuickSearchView> {
    Optional<UcsOrgtnQuickSearchView> findById(Long id);

    @Query("select v from UcsOrgtnQuickSearchView v where lower(v.searchKey) like lower(concat('%', :searchKey, '%'))")
    Page<UcsOrgtnQuickSearchView> searchBySearchKey(@Param("searchKey") String searchKey, Pageable pageable);
}
