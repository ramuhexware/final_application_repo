package com.rapidx.accoutservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ucs_cntprty_acct", schema = "ba0352")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserCntrPrtyAcct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cntryprty_acct")
    private Long id;

    @Column(name = "name_cntprty_acct", nullable = false)
    private String nameCntprtyAcct;

    @Column(name = "id_orgtn", nullable = false)
    private Integer idOrgtn;

    @Column(name = "ucs_cntprty_acct_role_sts", nullable = false)
    private String ucsCntPrtyAcctRoleSts;

    @OneToMany(mappedBy = "userCntrPrtyAcct", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<UcsCntPrtyAcctRoleAssn> ucsCntPrtyAcctRoleAssn = new HashSet<>();

    // Manual Getter / Setter to avoid @Getter / @Setter Lombok warnings
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameCntprtyAcct() {
        return nameCntprtyAcct;
    }

    public void setNameCntprtyAcct(String nameCntprtyAcct) {
        this.nameCntprtyAcct = nameCntprtyAcct;
    }

    public Integer getIdOrgtn() {
        return idOrgtn;
    }

    public void setIdOrgtn(Integer idOrgtn) {
        this.idOrgtn = idOrgtn;
    }

    public String getUcsCntPrtyAcctRoleSts() {
        return ucsCntPrtyAcctRoleSts;
    }

    public void setUcsCntPrtyAcctRoleSts(String ucsCntPrtyAcctRoleSts) {
        this.ucsCntPrtyAcctRoleSts = ucsCntPrtyAcctRoleSts;
    }

    public Set<UcsCntPrtyAcctRoleAssn> getUcsCntPrtyAcctRoleAssn() {
        return ucsCntPrtyAcctRoleAssn;
    }

    public void setUcsCntPrtyAcctRoleAssn(Set<UcsCntPrtyAcctRoleAssn> ucsCntPrtyAcctRoleAssn) {
        this.ucsCntPrtyAcctRoleAssn = ucsCntPrtyAcctRoleAssn;
    }
}

