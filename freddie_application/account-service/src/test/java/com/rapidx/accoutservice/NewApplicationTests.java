package com.rapidx.accoutservice;

import com.rapidx.accoutservice.dto.ActionAccountDto;
import com.rapidx.accoutservice.entity.UserCntrPrtyAcct;
import com.rapidx.accoutservice.repository.UserCntrPrtyAcctRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class NewApplicationTests {

    @Autowired
    private UserCntrPrtyAcctRepository repository;

    @Test
    void contextLoads() {
        // Simple context load test
    }

    @Test
    void testDerivedAndJPQLQueries() {
        // 1. Test findDistinctByIdOrgtn (derived query)
        Page<UserCntrPrtyAcct> org1001 = repository.findDistinctByIdOrgtn(1001, PageRequest.of(0, 10));
        assertThat(org1001.getContent()).hasSize(2);
        assertThat(org1001.getContent().get(0).getNameCntprtyAcct()).containsAnyOf("Acme Mortgage Corp", "Apex Funding LLC");

        // 2. Test findDistinctByIdOrgtnAndUcsCntPrtyAcctRoleSts (derived query)
        Page<UserCntrPrtyAcct> activeOrg1001 = repository.findDistinctByIdOrgtnAndUcsCntPrtyAcctRoleSts(
                1001, "Active", PageRequest.of(0, 10)
        );
        assertThat(activeOrg1001.getContent()).hasSize(1);
        assertThat(activeOrg1001.getContent().get(0).getNameCntprtyAcct()).isEqualTo("Acme Mortgage Corp");

        // 3. Test findByOrgAndLiOfBus (JPQL 1)
        Page<UserCntrPrtyAcct> sfOrg1001 = repository.findByOrgAndLiOfBus(
                1001, "SingleFamily", PageRequest.of(0, 10)
        );
        assertThat(sfOrg1001.getContent()).hasSize(2); // Acme and Apex both have SingleFamily

        // 4. Test findByOrgAndRoleStatusAndLiOfBus (JPQL 2)
        Page<UserCntrPrtyAcct> mfActiveOrg1001 = repository.findByOrgAndRoleStatusAndLiOfBus(
                1001, "Active", "MultiFamily", PageRequest.of(0, 10)
        );
        assertThat(mfActiveOrg1001.getContent()).hasSize(1);
        assertThat(mfActiveOrg1001.getContent().get(0).getNameCntprtyAcct()).isEqualTo("Acme Mortgage Corp");

        // 5. Test findDistinctByIdOrgtnAndUcsCntPrtyAcctRoleAssnUCSliofBusCdLiOfBusAndUcsCntPrtyAcctRoleStCdCntPrtyAccrRoleStCdCntprtyAcctRoleSts (JPQL 3)
        Page<UserCntrPrtyAcct> mfActiveOrg1001Custom = repository.findDistinctByIdOrgtnAndUcsCntPrtyAcctRoleAssnUCSliofBusCdLiOfBusAndUcsCntPrtyAcctRoleStCdCntPrtyAccrRoleStCdCntprtyAcctRoleSts(
                1001, "Active", "MultiFamily", PageRequest.of(0, 10)
        );
        assertThat(mfActiveOrg1001Custom.getContent()).hasSize(1);
        assertThat(mfActiveOrg1001Custom.getContent().get(0).getNameCntprtyAcct()).isEqualTo("Acme Mortgage Corp");
    }

    @Test
    void testNativeQueries() {
        // Test native query without parameters
        List<ActionAccountDto> actionAccounts = repository.findAllActionAccounts();
        assertThat(actionAccounts).isNotEmpty();
        
        // Check concat search key matches pattern: e.g. " | McLean |  | VA" or " | McLean | VA"
        // In our data: city='McLean', st='VA'. Concatenated as: ' | ' + 'McLean' + ' | ' + 'VA' => ' | McLean | VA' (depending on H2 CONCAT execution, but should contain both city and state)
        ActionAccountDto acmeDto = actionAccounts.stream()
                .filter(a -> a.getName().equals("Acme Mortgage Corp"))
                .findFirst()
                .orElse(null);
        
        assertThat(acmeDto).isNotNull();
        assertThat(acmeDto.getSearchKey()).contains("McLean");
        assertThat(acmeDto.getSearchKey()).contains("VA");

        // Test native query with org parameter
        List<ActionAccountDto> actionAccountsOfOrg1001 = repository.findAllActionAccountsOfOrg(1001);
        assertThat(actionAccountsOfOrg1001).hasSize(2); // Acme has 2 roles, Apex has 1 role -> distinct query returns distinct account records in action DTO list (2 records)
        // Wait, native query uses `select distinct acct.id_cntryprty_acct, acct.name_cntprty_acct, acct.id_orgtn`
        // So it returns 2 distinct records for Org 1001 (Acme and Apex). Let's verify size:
        long distinctNamesCount = actionAccountsOfOrg1001.stream().map(ActionAccountDto::getName).distinct().count();
        assertThat(distinctNamesCount).isEqualTo(2);
    }

    @Autowired
    private com.rapidx.accoutservice.repository.UcsOrgtnQuickSearchViewRepository quickSearchViewRepository;

    @Autowired
    private com.rapidx.accoutservice.controller.UserCntrPrtyAcctController controller;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        repository.findAll(PageRequest.of(0, 100)).getContent().stream()
                .filter(a -> "Test Controller Account".equals(a.getNameCntprtyAcct()))
                .forEach(repository::delete);
    }

    @Test
    void testQuickSearchViewQuery() {
        Page<com.rapidx.accoutservice.entity.UcsOrgtnQuickSearchView> page = quickSearchViewRepository.searchBySearchKey("McLean", PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        com.rapidx.accoutservice.entity.UcsOrgtnQuickSearchView result = page.getContent().get(0);
        assertThat(result.getCity()).isEqualTo("McLean");
        assertThat(result.getState()).isEqualTo("VA");
        assertThat(result.getSearchKey()).contains("1001");
        assertThat(result.getSearchKey()).contains("Freddie Mac");
        assertThat(result.getSearchKey()).contains("McLean");
        assertThat(result.getSearchKey()).contains("VA");
    }

    @Autowired
    private com.rapidx.accoutservice.messaging.AmqMessageListener amqListener;

    @Test
    void testLookupAndCreateEndpoints() throws Exception {
        // Test Lookup
        com.rapidx.accoutservice.dto.AccountLookupUpdateDto lookup = controller.lookup();
        assertThat(lookup).isNotNull();
        assertThat(lookup.getUcsOrgtnRoleDTOs()).isNotEmpty();
        assertThat(lookup.getUcsProdtDTOs()).isNotEmpty();
        assertThat(lookup.getUcsLineOfBusinessDTOs()).isNotEmpty();
        assertThat(lookup.getMaplistUcsProdtDTOs()).isNotEmpty();
        assertThat(lookup.getMaplistUcsOrgtnRoleDTOs()).isNotEmpty();
        assertThat(lookup.getUcsCntprtyAcctSt()).isNotEmpty();
        assertThat(lookup.getUcsCntprtyAcctRoleSt()).isNotEmpty();
        assertThat(lookup.getRespSts()).isNotNull();
        assertThat(lookup.getRespSts().getStatusCode()).isEqualTo(200);

        // Clear last received message to start clean
        amqListener.clearLastReceivedMessage();

        // Test Create
        UserCntrPrtyAcct account = new UserCntrPrtyAcct();
        account.setIdOrgtn(1001);
        account.setNameCntprtyAcct("Test Controller Account");
        account.setUcsCntPrtyAcctRoleSts("Pending");
        
        UserCntrPrtyAcct created = controller.create(account);
        try {
            assertThat(created).isNotNull();
            assertThat(created.getId()).isNotNull();
            assertThat(created.getNameCntprtyAcct()).isEqualTo("Test Controller Account");

            // Wait for asynchronous JMS message arrival
            int count = 0;
            while (amqListener.getLastReceivedMessage() == null && count < 20) {
                Thread.sleep(100);
                count++;
            }

            // Verify message was received
            String jmsMsg = amqListener.getLastReceivedMessage();
            assertThat(jmsMsg).isNotNull();
            assertThat(jmsMsg).contains("SAVE");
            assertThat(jmsMsg).contains("Test Controller Account");
        } finally {
            repository.delete(created);
        }
    }
}

