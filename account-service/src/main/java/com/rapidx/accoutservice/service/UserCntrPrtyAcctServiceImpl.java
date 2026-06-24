package com.rapidx.accoutservice.service;

import com.rapidx.accoutservice.client.notification.NotificationClient;
import com.rapidx.accoutservice.dto.*;
import com.rapidx.accoutservice.entity.*;
import com.rapidx.accoutservice.repository.*;
import com.rapidx.accoutservice.util.ServiceHelper;
import org.springframework.http.HttpStatus;
import java.util.stream.Collectors;
import com.rapidx.accoutservice.exception.ResourceNotFoundException;
import com.rapidx.accoutservice.repository.UserCntrPrtyAcctRepository;
import com.rapidx.accoutservice.specification.UserCntrPrtyAcctSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserCntrPrtyAcctServiceImpl implements UserCntrPrtyAcctService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserCntrPrtyAcctServiceImpl.class);
    private static final org.slf4j.Logger LOGGER = log;

    private final UserCntrPrtyAcctRepository repository;
    private final NotificationClient notificationClient;
    private final EntityManager entityManager;
    private final UcsCntprtyAcctStRepository ucsCntprtyAcctStRepository;
    private final UcsCntprtyAcctRoleStRepository ucsCntprtyAcctRoleStRepository;
    private final UcsOrgtnRoleRepository ucsOrgtnRoleRepository;
    private final UcsProdtRepository ucsProdtRepository;
    private final UcsLiOfBusProdtAssnRepository ucsLiOfBusProdtAssnRepository;
    private final UCSliofBusCdRepository ucsLiOfBusCdRepository;
    private final com.rapidx.accoutservice.messaging.AmqMessageProducer amqMessageProducer;

    // Explicit constructor injection
    public UserCntrPrtyAcctServiceImpl(
            UserCntrPrtyAcctRepository repository,
            NotificationClient notificationClient,
            EntityManager entityManager,
            UcsCntprtyAcctStRepository ucsCntprtyAcctStRepository,
            UcsCntprtyAcctRoleStRepository ucsCntprtyAcctRoleStRepository,
            UcsOrgtnRoleRepository ucsOrgtnRoleRepository,
            UcsProdtRepository ucsProdtRepository,
            UcsLiOfBusProdtAssnRepository ucsLiOfBusProdtAssnRepository,
            UCSliofBusCdRepository ucsLiOfBusCdRepository,
            com.rapidx.accoutservice.messaging.AmqMessageProducer amqMessageProducer
    ) {
        this.repository = repository;
        this.notificationClient = notificationClient;
        this.entityManager = entityManager;
        this.ucsCntprtyAcctStRepository = ucsCntprtyAcctStRepository;
        this.ucsCntprtyAcctRoleStRepository = ucsCntprtyAcctRoleStRepository;
        this.ucsOrgtnRoleRepository = ucsOrgtnRoleRepository;
        this.ucsProdtRepository = ucsProdtRepository;
        this.ucsLiOfBusProdtAssnRepository = ucsLiOfBusProdtAssnRepository;
        this.ucsLiOfBusCdRepository = ucsLiOfBusCdRepository;
        this.amqMessageProducer = amqMessageProducer;
    }

    @Override
    public Page<UserCntrPrtyAcct> getAccounts(Pageable pageable) {
        log.info("Fetching all accounts with pagination: {}", pageable);
        return repository.findAll(pageable);
    }

    @Override
    public Page<UserCntrPrtyAcct> getAccountsByOrg(Integer orgId, Pageable pageable) {
        log.info("Fetching accounts for Org: {} with pagination: {}", orgId, pageable);
        return repository.findDistinctByIdOrgtn(orgId, pageable);
    }

    @Override
    public Page<UserCntrPrtyAcct> getAccountsByOrgAndStatus(Integer orgId, String roleStatus, Pageable pageable) {
        log.info("Fetching accounts for Org: {} and Status: {} with pagination: {}", orgId, roleStatus, pageable);
        return repository.findDistinctByIdOrgtnAndUcsCntPrtyAcctRoleSts(orgId, roleStatus, pageable);
    }

    @Override
    public Page<UserCntrPrtyAcct> getAccountsByOrgAndLineOfBusiness(Integer orgId, String lineOfBusiness, Pageable pageable) {
        log.info("Fetching accounts for Org: {} and Line of Business: {} with pagination: {}", orgId, lineOfBusiness, pageable);
        return repository.findByOrgAndLiOfBus(orgId, lineOfBusiness, pageable);
    }

    @Override
    public Page<UserCntrPrtyAcct> getAccountsByOrgStatusAndLineOfBusiness(Integer orgId, String roleStatus, String lineOfBusiness, Pageable pageable) {
        log.info("Fetching accounts for Org: {}, Status: {}, Line of Business: {} with pagination: {}", orgId, roleStatus, lineOfBusiness, pageable);
        return repository.findDistinctByIdOrgtnAndUcsCntPrtyAcctRoleAssnUCSliofBusCdLiOfBusAndUcsCntPrtyAcctRoleStCdCntPrtyAccrRoleStCdCntprtyAcctRoleSts(orgId, roleStatus, lineOfBusiness, pageable);
    }

    @Override
    public Page<UserCntrPrtyAcct> filterAccounts(Integer orgId, String roleStatus, String lineOfBusiness, Pageable pageable) {
        log.info("Filtering accounts dynamic spec - Org: {}, Status: {}, Line of Business: {} with pagination: {}", orgId, roleStatus, lineOfBusiness, pageable);
        Specification<UserCntrPrtyAcct> spec = UserCntrPrtyAcctSpecification.filterAccounts(orgId, roleStatus, lineOfBusiness);
        return repository.findAll(spec, pageable);
    }

    @Override
    public List<ActionAccountDto> getAllActionAccounts() {
        log.info("Fetching all raw action accounts (Native Query)");
        return repository.findAllActionAccounts();
    }

    @Override
    public List<ActionAccountDto> getActionAccountsByOrg(Integer orgId) {
        log.info("Fetching raw action accounts for Org: {} (Native Query)", orgId);
        return repository.findAllActionAccountsOfOrg(orgId);
    }

    @Override
    public UserCntrPrtyAcct getAccountById(Long id) {
        log.info("Fetching account by ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User Counterparty Account not found with ID: " + id));
    }

    @Override
    @Transactional
    public UserCntrPrtyAcct saveAccount(UserCntrPrtyAcct account) {
        log.info("Saving user counterparty account: {}", account.getNameCntprtyAcct());
        if (account.getUcsCntPrtyAcctRoleAssn() != null) {
            account.getUcsCntPrtyAcctRoleAssn().forEach(assn -> assn.setUserCntrPrtyAcct(account));
        }
        UserCntrPrtyAcct saved = repository.save(account);
        notificationClient.sendNotification(
                "admin@rapidx.com",
                "Account created/updated: ID = " + saved.getId() + ", Name = " + saved.getNameCntprtyAcct()
        );
        amqMessageProducer.publishEvent("SAVE", saved.getId(), saved.getNameCntprtyAcct());
        return saved;
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        log.info("Deleting account with ID: {}", id);
        UserCntrPrtyAcct account = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User Counterparty Account not found with ID: " + id));
        repository.delete(account);
        notificationClient.sendNotification(
                "admin@rapidx.com",
                "Account deleted: ID = " + id + ", Name = " + account.getNameCntprtyAcct()
        );
        amqMessageProducer.publishEvent("DELETE", id, account.getNameCntprtyAcct());
    }

    @Override
    public AccountLookupUpdateDto getLookupData() {
        log.info("Fetching reference tables for account lookup update");

        // 1. Fetch all Lines of Business and map them to DTOs
        List<UCSliofBusCd> listUcsLiOfBusCd = ucsLiOfBusCdRepository.findAll();
        List<UcsLineOfBusinessDTO> listUcsLineOfBusinessDTO = listUcsLiOfBusCd.stream()
                .map(l -> new UcsLineOfBusinessDTO(l.getId().intValue(), l.getLiOfBus()))
                .collect(Collectors.toList());

        // Create LOB id to name map for quick lookup
        java.util.Map<Integer, String> lobIdToNameMap = listUcsLiOfBusCd.stream()
                .collect(Collectors.toMap(
                        l -> l.getId().intValue(),
                        UCSliofBusCd::getLiOfBus,
                        (v1, v2) -> v1
                ));

        // 2. Fetch all associations
        List<UcsLiOfBusProdtAssn> listUcsLiOfBusProdtAssn = ucsLiOfBusProdtAssnRepository.findAll();

        AccountLookupUpdateDto accountLookupUpdateDTO = new AccountLookupUpdateDto();
        accountLookupUpdateDTO.setUcsLineOfBusinessDTOs(listUcsLineOfBusinessDTO);

        java.util.Map<String, List<UcsOrgtnRoleDTO>> maplistUcsOrgtnRoleDTOs = new java.util.HashMap<>();
        java.util.Map<String, List<UcsProdtDTO>> maplistUcsProdtDTOs = new java.util.HashMap<>();

        // Loop through all associations exactly as shown in the user's image:
        for (UcsLiOfBusProdtAssn ucsLiOfBusProdtAssn : listUcsLiOfBusProdtAssn) {
            List<Integer> listInteger = ucsLiOfBusProdtAssnRepository.findProductByLineOfBusiness(ucsLiOfBusProdtAssn.getIdLiOfBusCd());
            List<UcsProdt> listUcsProdt = ucsProdtRepository.findAllById(listInteger);
            List<UcsProdtDTO> listUcsProdtDTOs = new java.util.ArrayList<>();

            for (UcsProdt ucsProdt : listUcsProdt) {
                UcsProdtDTO ucsProdtDTO = new UcsProdtDTO();
                ucsProdtDTO.setIdProdt(ucsProdt.getIdProdt());
                ucsProdtDTO.setNameProdt(ucsProdt.getNameProdt());
                listUcsProdtDTOs.add(ucsProdtDTO);

                List<UcsOrgtnRoleDTO> listUcsOrgtnRoleDTOs = new java.util.ArrayList<>();
                List<Integer> listOfOrgId = ucsLiOfBusProdtAssnRepository.findOrgRoleByProductTypeId(ucsProdt.getIdProdt());
                List<UcsOrgtnRole> listUcsOrgtnRole = ucsOrgtnRoleRepository.findAllById(listOfOrgId);

                for (UcsOrgtnRole ucsOrgtnRole : listUcsOrgtnRole) {
                    UcsOrgtnRoleDTO ucsOrgtnRoleDTO = new UcsOrgtnRoleDTO();
                    ucsOrgtnRoleDTO.setIdOrgtnRole(ucsOrgtnRole.getIdOrgtnRole());
                    ucsOrgtnRoleDTO.setNameOrgtnRole(ucsOrgtnRole.getNameOrgtnRole());
                    listUcsOrgtnRoleDTOs.add(ucsOrgtnRoleDTO);
                }

                // Populate org roles map: key is Product Name, value is the list of Org Roles DTOs
                maplistUcsOrgtnRoleDTOs.put(ucsProdt.getNameProdt(), listUcsOrgtnRoleDTOs);
            }

            // Populate products map: key is Line of Business Name, value is the list of Products DTOs
            String lobName = lobIdToNameMap.get(ucsLiOfBusProdtAssn.getIdLiOfBusCd());
            if (lobName != null) {
                maplistUcsProdtDTOs.put(lobName, listUcsProdtDTOs);
            }
        }

        accountLookupUpdateDTO.setMaplistUcsOrgtnRoleDTOs(maplistUcsOrgtnRoleDTOs);
        accountLookupUpdateDTO.setMaplistUcsProdtDTOs(maplistUcsProdtDTOs);

        // Fetch other reference data
        List<UcsCntprtyAcctSt> ucsCntprtyAcctSt = ucsCntprtyAcctStRepository.findAll();
        List<UcsCntprtyAcctRoleSt> ucsCntprtyAcctRoleSt = ucsCntprtyAcctRoleStRepository.findAll();

        accountLookupUpdateDTO.setUcsCntprtyAcctSt(ucsCntprtyAcctSt);
        accountLookupUpdateDTO.setUcsCntprtyAcctRoleSt(ucsCntprtyAcctRoleSt);
        accountLookupUpdateDTO.setRespSts(
            (ServiceHelper.setRespSts(HttpStatus.OK, "Account look up data is retrieved successfully")));

        // For backward compatibility (or tests checking simple DTO fields)
        List<UcsOrgtnRole> allRoles = ucsOrgtnRoleRepository.findAll();
        List<UcsOrgtnRoleDTO> simpleRoles = allRoles.stream()
                .map(r -> new UcsOrgtnRoleDTO(r.getIdOrgtnRole(), r.getNameOrgtnRole()))
                .collect(Collectors.toList());
        accountLookupUpdateDTO.setUcsOrgtnRoleDTOs(simpleRoles);

        List<UcsProdt> allProducts = ucsProdtRepository.findAll();
        List<UcsProdtDTO> simpleProducts = allProducts.stream()
                .map(p -> new UcsProdtDTO(p.getIdProdt(), p.getNameProdt()))
                .collect(Collectors.toList());
        accountLookupUpdateDTO.setUcsProdtDTOs(simpleProducts);

        LOGGER.info("Org API: Account lookup update data is retrieved successfully.");
        return accountLookupUpdateDTO;
    }
}
