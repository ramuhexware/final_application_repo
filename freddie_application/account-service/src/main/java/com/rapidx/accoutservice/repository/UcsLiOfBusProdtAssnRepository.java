package com.rapidx.accoutservice.repository;

import com.rapidx.accoutservice.entity.UcsLiOfBusProdtAssn;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UcsLiOfBusProdtAssnRepository extends PagingAndSortingRepository<UcsLiOfBusProdtAssn, Long>, JpaSpecificationExecutor<UcsLiOfBusProdtAssn> {

    @Query("SELECT DISTINCT a.idProdt FROM UcsLiOfBusProdtAssn a WHERE a.idLiOfBusCd = :idLiOfBusCd")
    List<Integer> findProductByLineOfBusiness(@Param("idLiOfBusCd") Integer idLiOfBusCd);

    @Query("SELECT DISTINCT a.idOrgtnRole FROM UcsLiOfBusProdtAssn a WHERE a.idProdt = :idProdt")
    List<Integer> findOrgRoleByProductTypeId(@Param("idProdt") Integer idProdt);

    List<UcsLiOfBusProdtAssn> findAll();
}
