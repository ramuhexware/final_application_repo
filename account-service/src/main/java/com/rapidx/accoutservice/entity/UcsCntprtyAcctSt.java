package com.rapidx.accoutservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ucs_cntprty_acct_st", schema = "ba0352")
public class UcsCntprtyAcctSt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cntprty_acct_st")
    private Long id;

    @Column(name = "status_name", nullable = false)
    private String statusName;

    public UcsCntprtyAcctSt() {}

    public UcsCntprtyAcctSt(String statusName) {
        this.statusName = statusName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
