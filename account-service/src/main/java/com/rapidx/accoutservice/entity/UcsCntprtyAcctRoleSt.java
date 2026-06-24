package com.rapidx.accoutservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ucs_cntprty_acct_role_st", schema = "ba0352")
public class UcsCntprtyAcctRoleSt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cntprty_acct_role_st")
    private Long id;

    @Column(name = "role_status_name", nullable = false)
    private String roleStatusName;

    public UcsCntprtyAcctRoleSt() {}

    public UcsCntprtyAcctRoleSt(String roleStatusName) {
        this.roleStatusName = roleStatusName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleStatusName() {
        return roleStatusName;
    }

    public void setRoleStatusName(String roleStatusName) {
        this.roleStatusName = roleStatusName;
    }
}
