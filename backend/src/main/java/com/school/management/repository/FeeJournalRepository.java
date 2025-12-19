package com.school.management.repository;

import com.school.management.model.FeeJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeJournalRepository extends JpaRepository<FeeJournal, Long> {

    List<FeeJournal> findByStudentId(Long studentId);

    List<FeeJournal> findByStudentIdAndAcademicYear(Long studentId, String academicYear);

    List<FeeJournal> findByStudentIdAndIsPending(Long studentId, Boolean isPending);

    List<FeeJournal> findByAcademicYearAndIsPending(String academicYear, Boolean isPending);

    Long countByStudentIdAndIsPending(Long studentId, Boolean isPending);
}
