package com.rapidx.accoutservice.repository;

import com.rapidx.accoutservice.dto.ActionAccountDto;
import com.rapidx.accoutservice.entity.UserCntrPrtyAcct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public interface UserCntrPrtyAcctRepository extends PagingAndSortingRepository<UserCntrPrtyAcct, Long>, JpaSpecificationExecutor<UserCntrPrtyAcct> {

    Optional<UserCntrPrtyAcct> findById(Long id);
    UserCntrPrtyAcct save(UserCntrPrtyAcct entity);
    void delete(UserCntrPrtyAcct entity);

    // Derived Query 1
    Page<UserCntrPrtyAcct> findDistinctByIdOrgtn(Integer idOrgtn, Pageable pageable);

    // Derived Query 2
    Page<UserCntrPrtyAcct> findDistinctByIdOrgtnAndUcsCntPrtyAcctRoleSts(
            Integer idOrgtn,
            String roleStatus,
            Pageable pageable
    );

    // JPQL Query 1 - mapped to method findByOrgAndLiOfBus
    @Query("select distinct a from UserCntrPrtyAcct a " +
           "join a.ucsCntPrtyAcctRoleAssn r " +
           "join r.uCSliofBusCd l " +
           "where a.idOrgtn = :idOrgtn " +
           "and lower(l.liOfBus) = lower(:liOfBus)")
    Page<UserCntrPrtyAcct> findByOrgAndLiOfBus(
            @Param("idOrgtn") Integer idOrgtn,
            @Param("liOfBus") String liOfBus,
            Pageable pageable
    );

    // JPQL Query 2 - mapped to method findByOrgAndRoleStatusAndLiOfBus
    @Query("select distinct a from UserCntrPrtyAcct a " +
           "join a.ucsCntPrtyAcctRoleAssn r " +
           "join r.uCSliofBusCd l " +
           "where a.idOrgtn = :idOrgtn " +
           "and lower(a.ucsCntPrtyAcctRoleSts) = lower(:roleStatus) " +
           "and lower(l.liOfBus) = lower(:liOfBus)")
    Page<UserCntrPrtyAcct> findByOrgAndRoleStatusAndLiOfBus(
            @Param("idOrgtn") Integer idOrgtn,
            @Param("roleStatus") String roleStatus,
            @Param("liOfBus") String liOfBus,
            Pageable pageable
    );

    // JPQL Query 3 - custom long name from user request
    @Query("select distinct a from UserCntrPrtyAcct a " +
           "join a.ucsCntPrtyAcctRoleAssn r " +
           "join r.uCSliofBusCd l " +
           "where a.idOrgtn = :idOrgtn " +
           "and lower(a.ucsCntPrtyAcctRoleSts) = lower(:rolestatus) " +
           "and lower(l.liOfBus) = lower(:liOfBus)")
    Page<UserCntrPrtyAcct> findDistinctByIdOrgtnAndUcsCntPrtyAcctRoleAssnUCSliofBusCdLiOfBusAndUcsCntPrtyAcctRoleStCdCntPrtyAccrRoleStCdCntprtyAcctRoleSts(
            @Param("idOrgtn") int idOrgtn,
            @Param("rolestatus") String rolestatus,
            @Param("liOfBus") String liOfBus,
            Pageable pageable
    );

    // Native Query 1
    @Query(value = "select distinct " +
            "acct.id_cntryprty_acct, " +
            "acct.name_cntprty_acct, " +
            "acct.id_orgtn, " +
            "CONCAT(' | ', CONCAT(COALESCE(ADDR.NAME_CITY, ''), CONCAT(' | ', COALESCE(ADDR.CD_ST, '')))) AS SEARCH_KEY " +
            "from ucs_cntprty_acct_role_assn assn " +
            "join ucs_cntprty_acct acct on acct.id_cntryprty_acct = assn.id_cntryprty_acct " +
            "left join ucs_address ADDR on ADDR.id_cntryprty_acct = acct.id_cntryprty_acct",
            nativeQuery = true)
    List<Object[]> findAllActionAccountsRaw();

    // Native Query 2
    @Query(value = "select distinct " +
            "acct.id_cntryprty_acct, " +
            "acct.name_cntprty_acct, " +
            "acct.id_orgtn, " +
            "CONCAT(' | ', CONCAT(COALESCE(ADDR.NAME_CITY, ''), CONCAT(' | ', COALESCE(ADDR.CD_ST, '')))) AS SEARCH_KEY " +
            "from ucs_cntprty_acct_role_assn assn " +
            "join ucs_cntprty_acct acct on acct.id_cntryprty_acct = assn.id_cntryprty_acct " +
            "left join ucs_address ADDR on ADDR.id_cntryprty_acct = acct.id_cntryprty_acct " +
            "where acct.id_orgtn = :orgId",
            nativeQuery = true)
    List<Object[]> findAllActionAccountsOfOrgRaw(@Param("orgId") Integer orgId);

    // Default Methods
    default List<ActionAccountDto> findAllActionAccounts() {
        List<Object[]> raw = findAllActionAccountsRaw();
        if (raw == null) {
            return Collections.emptyList();
        }
        return raw.stream()
                .filter(Objects::nonNull)
                .map(ActionAccountDto::createAccount)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    default List<ActionAccountDto> findAllActionAccountsOfOrg(Integer orgId) {
        List<Object[]> raw = findAllActionAccountsOfOrgRaw(orgId);
        if (raw == null) {
            return Collections.emptyList();
        }
        return raw.stream()
                .filter(Objects::nonNull)
                .map(ActionAccountDto::createAccount)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}

