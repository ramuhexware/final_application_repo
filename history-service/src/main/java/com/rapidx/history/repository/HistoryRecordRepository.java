package com.rapidx.history.repository;

import com.rapidx.history.entity.HistoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRecordRepository extends JpaRepository<HistoryRecord, Long> {
}
