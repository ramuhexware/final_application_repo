package com.rapidx.accoutservice.repository;

import com.rapidx.accoutservice.entity.UcsCntprtyAcctRoleSt;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.List;

public interface UcsCntprtyAcctRoleStRepository extends PagingAndSortingRepository<UcsCntprtyAcctRoleSt, Long>, JpaSpecificationExecutor<UcsCntprtyAcctRoleSt> {
    List<UcsCntprtyAcctRoleSt> findAll();
}
