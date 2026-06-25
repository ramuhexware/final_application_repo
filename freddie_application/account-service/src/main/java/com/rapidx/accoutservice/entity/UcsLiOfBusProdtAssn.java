package com.rapidx.accoutservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ucs_li_of_bus_prodt_assn", schema = "ba0352")
public class UcsLiOfBusProdtAssn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_li_of_bus_prodt_assn")
    private Long id;

    @Column(name = "id_li_of_bus_cd", nullable = false)
    private Integer idLiOfBusCd;

    @Column(name = "id_prodt", nullable = false)
    private Integer idProdt;

    @Column(name = "id_orgtn_role", nullable = false)
    private Integer idOrgtnRole;

    public UcsLiOfBusProdtAssn() {}

    public UcsLiOfBusProdtAssn(Integer idLiOfBusCd, Integer idProdt, Integer idOrgtnRole) {
        this.idLiOfBusCd = idLiOfBusCd;
        this.idProdt = idProdt;
        this.idOrgtnRole = idOrgtnRole;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdLiOfBusCd() {
        return idLiOfBusCd;
    }

    public void setIdLiOfBusCd(Integer idLiOfBusCd) {
        this.idLiOfBusCd = idLiOfBusCd;
    }

    public Integer getIdProdt() {
        return idProdt;
    }

    public void setIdProdt(Integer idProdt) {
        this.idProdt = idProdt;
    }

    public Integer getIdOrgtnRole() {
        return idOrgtnRole;
    }

    public void setIdOrgtnRole(Integer idOrgtnRole) {
        this.idOrgtnRole = idOrgtnRole;
    }
}
