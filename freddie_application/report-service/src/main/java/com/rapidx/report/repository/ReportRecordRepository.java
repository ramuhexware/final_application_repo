package com.rapidx.report.repository;

import com.rapidx.report.entity.ReportRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRecordRepository extends JpaRepository<ReportRecord, Long> {
}
