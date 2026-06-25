package com.rapidx.accoutservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "ucs_cntprty_acct_role_assn", schema = "ba0352")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UcsCntPrtyAcctRoleAssn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role_assn")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cntryprty_acct", nullable = false)
    private UserCntrPrtyAcct userCntrPrtyAcct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_li_of_bus_cd", nullable = false)
    private UCSliofBusCd uCSliofBusCd;

    // Manual Getter / Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserCntrPrtyAcct getUserCntrPrtyAcct() {
        return userCntrPrtyAcct;
    }

    public void setUserCntrPrtyAcct(UserCntrPrtyAcct userCntrPrtyAcct) {
        this.userCntrPrtyAcct = userCntrPrtyAcct;
    }

    public UCSliofBusCd getuCSliofBusCd() {
        return uCSliofBusCd;
    }

    public void setuCSliofBusCd(UCSliofBusCd uCSliofBusCd) {
        this.uCSliofBusCd = uCSliofBusCd;
    }
}

