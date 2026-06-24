package com.rapidx.accoutservice.repository;

import com.rapidx.accoutservice.entity.UcsProdt;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.List;

public interface UcsProdtRepository extends PagingAndSortingRepository<UcsProdt, Integer>, JpaSpecificationExecutor<UcsProdt> {
    List<UcsProdt> findAll();
    List<UcsProdt> findAllById(Iterable<Integer> ids);
}
