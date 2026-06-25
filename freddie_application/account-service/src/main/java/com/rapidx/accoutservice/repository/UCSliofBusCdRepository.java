package com.rapidx.accoutservice.repository;

import com.rapidx.accoutservice.entity.UCSliofBusCd;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.List;

public interface UCSliofBusCdRepository extends PagingAndSortingRepository<UCSliofBusCd, Long>, JpaSpecificationExecutor<UCSliofBusCd> {
    List<UCSliofBusCd> findAll();
}
