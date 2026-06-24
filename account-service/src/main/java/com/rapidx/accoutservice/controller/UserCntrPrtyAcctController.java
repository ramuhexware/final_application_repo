package com.rapidx.accoutservice.controller;

import com.rapidx.accoutservice.dto.ActionAccountDto;
import com.rapidx.accoutservice.entity.UserCntrPrtyAcct;
import com.rapidx.accoutservice.service.UserCntrPrtyAcctService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.rapidx.accoutservice.entity.UcsOrgtnQuickSearchView;
import com.rapidx.accoutservice.repository.UcsOrgtnQuickSearchViewRepository;
import com.rapidx.accoutservice.client.notification.EmailNotificationClientServicer;
import com.rapidx.accoutservice.client.notification.EmailNotificationClientConfig;
import com.rapidx.accoutservice.dto.AccountLookupDTO;
import com.rapidx.accoutservice.dto.AccountLookupUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/accounts")
@Tag(name = "User Counterparty Account", description = "Endpoints for managing and querying User Counterparty Accounts")
public class UserCntrPrtyAcctController {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UserCntrPrtyAcctController.class);
    private static final org.slf4j.Logger log = LOGGER;

    @Autowired
    EmailNotificationClientServicer emailNotificationClientServicer;

    @Autowired
    EmailNotificationClientConfig emailNotificationClientConfig;

    @Value("${bypassPingAuth:false}")
    private String bypassPingAuth;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields();
    }

    private final UserCntrPrtyAcctService service;
    private final UcsOrgtnQuickSearchViewRepository quickSearchViewRepository;

    // Explicit constructor injection
    public UserCntrPrtyAcctController(
            UserCntrPrtyAcctService service,
            UcsOrgtnQuickSearchViewRepository quickSearchViewRepository
    ) {
        this.service = service;
        this.quickSearchViewRepository = quickSearchViewRepository;
    }

    @Operation(summary = "Retrieves the reference tables for create account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error") })
    @GetMapping(value = "/lookup", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody AccountLookupUpdateDto lookup() {
        LOGGER.info("OrgAPI: Lookup Account...");
        return service.getLookupData();
    }

    @Operation(summary = "Create Account object and persisit it to Database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error") })
    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody UserCntrPrtyAcct create(@RequestBody UserCntrPrtyAcct account) {
        LOGGER.info("OrgAPI: Create Account...");
        UserCntrPrtyAcct saved = service.saveAccount(account);
        emailNotificationClientServicer.sendEmailNotification("admin@rapidx.com", "Account Created", "Successfully created account: " + saved.getNameCntprtyAcct());
        return saved;
    }

    @GetMapping
    @Operation(summary = "Get accounts using derived/JPQL queries based on query parameters")
    public ResponseEntity<Page<UserCntrPrtyAcct>> getAccounts(
            @Parameter(description = "Organization ID") @RequestParam(required = false) Integer orgId,
            @Parameter(description = "Role Status") @RequestParam(required = false) String roleStatus,
            @Parameter(description = "Line of Business") @RequestParam(required = false) String lineOfBusiness,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("REST request to get accounts: orgId={}, roleStatus={}, lineOfBusiness={}, pageable={}",
                orgId, roleStatus, lineOfBusiness, pageable);

        Page<UserCntrPrtyAcct> page;

        if (orgId != null) {
            if (roleStatus != null && lineOfBusiness != null) {
                page = service.getAccountsByOrgStatusAndLineOfBusiness(orgId, roleStatus, lineOfBusiness, pageable);
            } else if (roleStatus != null) {
                page = service.getAccountsByOrgAndStatus(orgId, roleStatus, pageable);
            } else if (lineOfBusiness != null) {
                page = service.getAccountsByOrgAndLineOfBusiness(orgId, lineOfBusiness, pageable);
            } else {
                page = service.getAccountsByOrg(orgId, pageable);
            }
        } else {
            page = service.getAccounts(pageable);
        }

        return ResponseEntity.ok(page);
    }

    @GetMapping("/org/{id}")
    @Operation(summary = "Get distinct accounts by Organization ID")
    public ResponseEntity<Page<UserCntrPrtyAcct>> getAccountsByOrg(
            @PathVariable("id") Integer orgId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("REST request to get accounts by Org ID: {}, pageable={}", orgId, pageable);
        Page<UserCntrPrtyAcct> page = service.getAccountsByOrg(orgId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/filter")
    @Operation(summary = "Get accounts using dynamic filtering (JPA Specifications)")
    public ResponseEntity<Page<UserCntrPrtyAcct>> filterAccounts(
            @Parameter(description = "Organization ID") @RequestParam(required = false) Integer orgId,
            @Parameter(description = "Role Status") @RequestParam(required = false) String roleStatus,
            @Parameter(description = "Line of Business") @RequestParam(required = false) String lineOfBusiness,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("REST request to dynamically filter accounts: orgId={}, roleStatus={}, lineOfBusiness={}, pageable={}",
                orgId, roleStatus, lineOfBusiness, pageable);
        Page<UserCntrPrtyAcct> page = service.filterAccounts(orgId, roleStatus, lineOfBusiness, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/raw")
    @Operation(summary = "Get raw Action Accounts using Native SQL Query")
    public ResponseEntity<List<ActionAccountDto>> getRawActionAccounts(
            @Parameter(description = "Optional Organization ID") @RequestParam(required = false) Integer orgId
    ) {
        log.info("REST request to get raw action accounts. Org ID filter: {}", orgId);
        List<ActionAccountDto> list;
        if (orgId != null) {
            list = service.getActionAccountsByOrg(orgId);
        } else {
            list = service.getAllActionAccounts();
        }
        return ResponseEntity.ok(list);
    }

    @PostMapping
    @Operation(summary = "Create or update an account")
    public ResponseEntity<UserCntrPrtyAcct> createAccount(@RequestBody UserCntrPrtyAcct account) {
        log.info("REST request to save account: {}", account.getNameCntprtyAcct());
        UserCntrPrtyAcct saved = service.saveAccount(account);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an account")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") Long id) {
        log.info("REST request to delete account ID: {}", id);
        service.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/update-family/{id}")
    @Operation(summary = "Update account counterparty name via GET request (custom template)")
    public ResponseEntity<String> updateFamily(
            @PathVariable("id") Long id,
            @RequestParam("id") Long idParam,
            @RequestParam("value") String value
    ) {
        log.info("REST GET request to update family of account ID: {} (param id: {}, value: {})", id, idParam, value);
        UserCntrPrtyAcct account = service.getAccountById(id);
        account.setNameCntprtyAcct(value);
        service.saveAccount(account);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/quick-search")
    @Operation(summary = "Search organizations and counterparty accounts using database view")
    public ResponseEntity<Page<UcsOrgtnQuickSearchView>> quickSearch(
            @RequestParam("searchKey") String searchKey,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("REST request to quick search view using key: {}, pageable: {}", searchKey, pageable);
        Page<UcsOrgtnQuickSearchView> result = quickSearchViewRepository.searchBySearchKey(searchKey, pageable);
        return ResponseEntity.ok(result);
    }
}

