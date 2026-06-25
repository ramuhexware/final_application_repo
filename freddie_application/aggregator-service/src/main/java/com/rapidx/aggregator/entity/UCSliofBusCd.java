package com.rapidx.aggregator.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "ucs_li_of_bus_cd", schema = "ba0352")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UCSliofBusCd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_li_of_bus_cd")
    private Long id;

    @Column(name = "li_of_bus", nullable = false)
    private String liOfBus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLiOfBus() {
        return liOfBus;
    }

    public void setLiOfBus(String liOfBus) {
        this.liOfBus = liOfBus;
    }
}
