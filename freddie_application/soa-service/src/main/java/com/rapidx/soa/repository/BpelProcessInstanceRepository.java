package com.rapidx.soa.repository;

import com.rapidx.soa.entity.BpelProcessInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BpelProcessInstanceRepository extends JpaRepository<BpelProcessInstance, String> {
}
