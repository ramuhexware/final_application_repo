package com.rapidx.aggregator.repository;

import com.rapidx.aggregator.entity.OimSyncError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OimSyncErrorRepository extends JpaRepository<OimSyncError, Long> {
}
