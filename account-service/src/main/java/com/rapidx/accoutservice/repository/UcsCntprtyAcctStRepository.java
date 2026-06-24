package com.rapidx.accoutservice.repository;

import com.rapidx.accoutservice.entity.UcsCntprtyAcctSt;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.List;

public interface UcsCntprtyAcctStRepository extends PagingAndSortingRepository<UcsCntprtyAcctSt, Long>, JpaSpecificationExecutor<UcsCntprtyAcctSt> {
    List<UcsCntprtyAcctSt> findAll();
}
