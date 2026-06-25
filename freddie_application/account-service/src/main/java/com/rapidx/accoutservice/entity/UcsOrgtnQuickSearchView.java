package com.rapidx.accoutservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "UCS_ORGTN_QUICK_SEARCH_VIEW", schema = "ba0352")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UcsOrgtnQuickSearchView {

    @Id
    @Column(name = "ROW_ID")
    private Long rowId;

    @Column(name = "ORGANIZATION_ID")
    private Integer organizationId;

    @Column(name = "INSTITUTION_NAME")
    private String institutionName;

    @Column(name = "ACCOUNT_IDENTIFIER")
    private Long accountIdentifier;

    @Column(name = "ACCOUNT_NAME")
    private String accountName;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STATE")
    private String state;

    @Column(name = "SEARCH_KEY")
    private String searchKey;

    public Long getRowId() {
        return rowId;
    }

    public void setRowId(Long rowId) {
        this.rowId = rowId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public Long getAccountIdentifier() {
        return accountIdentifier;
    }

    public void setAccountIdentifier(Long accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }
}
