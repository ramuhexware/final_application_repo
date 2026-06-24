package com.rapidx.accoutservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ucs_prodt", schema = "ba0352")
public class UcsProdt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prodt")
    private Integer idProdt;

    @Column(name = "product_name", nullable = false)
    private String nameProdt;

    public UcsProdt() {}

    public UcsProdt(Integer idProdt, String nameProdt) {
        this.idProdt = idProdt;
        this.nameProdt = nameProdt;
    }

    public Integer getIdProdt() {
        return idProdt;
    }

    public void setIdProdt(Integer idProdt) {
        this.idProdt = idProdt;
    }

    public String getNameProdt() {
        return nameProdt;
    }

    public void setNameProdt(String nameProdt) {
        this.nameProdt = nameProdt;
    }
}
