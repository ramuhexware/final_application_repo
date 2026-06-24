package com.rapidx.accoutservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ucs_orgtn_role", schema = "ba0352")
public class UcsOrgtnRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orgtn_role")
    private Integer idOrgtnRole;

    @Column(name = "role_name", nullable = false)
    private String nameOrgtnRole;

    public UcsOrgtnRole() {}

    public UcsOrgtnRole(Integer idOrgtnRole, String nameOrgtnRole) {
        this.idOrgtnRole = idOrgtnRole;
        this.nameOrgtnRole = nameOrgtnRole;
    }

    public Integer getIdOrgtnRole() {
        return idOrgtnRole;
    }

    public void setIdOrgtnRole(Integer idOrgtnRole) {
        this.idOrgtnRole = idOrgtnRole;
    }

    public String getNameOrgtnRole() {
        return nameOrgtnRole;
    }

    public void setNameOrgtnRole(String nameOrgtnRole) {
        this.nameOrgtnRole = nameOrgtnRole;
    }
}
