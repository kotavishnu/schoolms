package com.school.management.domain.student;

import com.school.management.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * FeeJournal domain entity (placeholder for later sprint)
 * Represents fee transactions for a student
 *
 * @author School Management System
 * @version 1.0
 */
@Entity
@Table(name = "fee_journals")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class FeeJournal extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;
}
