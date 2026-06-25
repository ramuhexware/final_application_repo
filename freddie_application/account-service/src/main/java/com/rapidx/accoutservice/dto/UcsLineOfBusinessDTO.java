package com.rapidx.accoutservice.dto;

public class UcsLineOfBusinessDTO {

    private Integer idLiOfBusCd;
    private String liOfBus;

    public UcsLineOfBusinessDTO() {}

    public UcsLineOfBusinessDTO(Integer idLiOfBusCd, String liOfBus) {
        this.idLiOfBusCd = idLiOfBusCd;
        this.liOfBus = liOfBus;
    }

    public Integer getIdLiOfBusCd() {
        return idLiOfBusCd;
    }

    public void setIdLiOfBusCd(Integer idLiOfBusCd) {
        this.idLiOfBusCd = idLiOfBusCd;
    }

    public String getLiOfBus() {
        return liOfBus;
    }

    public void setLiOfBus(String liOfBus) {
        this.liOfBus = liOfBus;
    }
}
