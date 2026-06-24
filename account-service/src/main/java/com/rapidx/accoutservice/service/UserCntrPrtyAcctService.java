package com.rapidx.accoutservice.service;

import com.rapidx.accoutservice.dto.ActionAccountDto;
import com.rapidx.accoutservice.entity.UserCntrPrtyAcct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import com.rapidx.accoutservice.dto.AccountLookupDTO;
import com.rapidx.accoutservice.dto.AccountLookupUpdateDto;
import java.util.List;

public interface UserCntrPrtyAcctService {

    Page<UserCntrPrtyAcct> getAccounts(Pageable pageable);

    Page<UserCntrPrtyAcct> getAccountsByOrg(Integer orgId, Pageable pageable);

    Page<UserCntrPrtyAcct> getAccountsByOrgAndStatus(Integer orgId, String roleStatus, Pageable pageable);

    Page<UserCntrPrtyAcct> getAccountsByOrgAndLineOfBusiness(Integer orgId, String lineOfBusiness, Pageable pageable);

    Page<UserCntrPrtyAcct> getAccountsByOrgStatusAndLineOfBusiness(Integer orgId, String roleStatus, String lineOfBusiness, Pageable pageable);

    Page<UserCntrPrtyAcct> filterAccounts(Integer orgId, String roleStatus, String lineOfBusiness, Pageable pageable);

    List<ActionAccountDto> getAllActionAccounts();

    List<ActionAccountDto> getActionAccountsByOrg(Integer orgId);

    UserCntrPrtyAcct getAccountById(Long id);

    UserCntrPrtyAcct saveAccount(UserCntrPrtyAcct account);

    void deleteAccount(Long id);

    AccountLookupUpdateDto getLookupData();
}

