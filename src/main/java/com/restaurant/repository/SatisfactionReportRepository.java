package com.restaurant.repository;

import com.restaurant.model.SatisfactionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SatisfactionReportRepository extends JpaRepository<SatisfactionReport, Long> {
    
    List<SatisfactionReport> findByReportType(SatisfactionReport.ReportType reportType);
    
    List<SatisfactionReport> findByStatus(SatisfactionReport.ReportStatus status);
    
    List<SatisfactionReport> findByGeneratedBy(String generatedBy);
    
    @Query("SELECT sr FROM SatisfactionReport sr WHERE sr.reportDate BETWEEN :startDate AND :endDate ORDER BY sr.reportDate DESC")
    List<SatisfactionReport> findByReportDateBetween(@Param("startDate") LocalDate startDate, 
                                                    @Param("endDate") LocalDate endDate);
    
    @Query("SELECT sr FROM SatisfactionReport sr WHERE sr.periodStart <= :date AND sr.periodEnd >= :date ORDER BY sr.reportDate DESC")
    List<SatisfactionReport> findReportsForDate(@Param("date") LocalDate date);
    
    @Query("SELECT sr FROM SatisfactionReport sr WHERE sr.reportType = :reportType AND sr.status = :status ORDER BY sr.reportDate DESC")
    List<SatisfactionReport> findByReportTypeAndStatus(@Param("reportType") SatisfactionReport.ReportType reportType,
                                                       @Param("status") SatisfactionReport.ReportStatus status);
    
    @Query("SELECT sr FROM SatisfactionReport sr WHERE sr.status = 'FAILED' ORDER BY sr.createdAt ASC")
    List<SatisfactionReport> findFailedReports();
    
    @Query("SELECT sr FROM SatisfactionReport sr WHERE sr.satisfactionScore < :threshold ORDER BY sr.reportDate DESC")
    List<SatisfactionReport> findLowSatisfactionReports(@Param("threshold") Double threshold);
    
    @Query("SELECT sr FROM SatisfactionReport sr WHERE sr.criticalIssuesCount > 0 ORDER BY sr.reportDate DESC")
    List<SatisfactionReport> findReportsWithCriticalIssues();
    
    @Query("SELECT AVG(sr.satisfactionScore) FROM SatisfactionReport sr WHERE sr.reportType = :reportType AND sr.createdAt BETWEEN :startDate AND :endDate")
    Double getAverageSatisfactionScore(@Param("reportType") SatisfactionReport.ReportType reportType,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);
    
    @Query("SELECT sr FROM SatisfactionReport sr WHERE sr.reportType = :reportType ORDER BY sr.reportDate DESC LIMIT 1")
    SatisfactionReport findLatestReportByType(@Param("reportType") SatisfactionReport.ReportType reportType);
}
