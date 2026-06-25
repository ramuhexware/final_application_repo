package com.rapidx.accoutservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "ucs_address", schema = "ba0352")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UcsAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_address")
    private Long id;

    @Column(name = "name_city")
    private String nameCity;

    @Column(name = "cd_st")
    private String cdSt;

    @Column(name = "id_cntryprty_acct", nullable = false)
    private Long idCntryprtyAcct;

    // Manual Getter / Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameCity() {
        return nameCity;
    }

    public void setNameCity(String nameCity) {
        this.nameCity = nameCity;
    }

    public String getCdSt() {
        return cdSt;
    }

    public void setCdSt(String cdSt) {
        this.cdSt = cdSt;
    }

    public Long getIdCntryprtyAcct() {
        return idCntryprtyAcct;
    }

    public void setIdCntryprtyAcct(Long idCntryprtyAcct) {
        this.idCntryprtyAcct = idCntryprtyAcct;
    }
}

