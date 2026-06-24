package com.rapidx.accoutservice.repository;

import com.rapidx.accoutservice.entity.UcsOrgtnRole;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.List;

public interface UcsOrgtnRoleRepository extends PagingAndSortingRepository<UcsOrgtnRole, Integer>, JpaSpecificationExecutor<UcsOrgtnRole> {
    List<UcsOrgtnRole> findAll();
    List<UcsOrgtnRole> findAllById(Iterable<Integer> ids);
}
