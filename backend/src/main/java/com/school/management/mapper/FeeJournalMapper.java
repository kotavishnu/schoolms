package com.school.management.mapper;

import com.school.management.dto.request.FeeJournalRequestDTO;
import com.school.management.dto.response.FeeJournalResponseDTO;
import com.school.management.model.FeeJournal;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for FeeJournal entity and DTOs.
 *
 * Automatically generates implementation for entity-DTO conversions.
 *
 * @author School Management Team
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeeJournalMapper {

    /**
     * Convert FeeJournal entity to response DTO
     * @param feeJournal FeeJournal entity
     * @return FeeJournalResponseDTO
     */
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", expression = "java(feeJournal.getStudent() != null ? feeJournal.getStudent().getFullName() : null)")
    @Mapping(target = "studentMobile", source = "student.mobile")
    @Mapping(target = "className", expression = "java(feeJournal.getStudent() != null && feeJournal.getStudent().getSchoolClass() != null ? feeJournal.getStudent().getSchoolClass().getDisplayName() : null)")
    @Mapping(target = "monthYearDisplay", expression = "java(feeJournal.getMonthYearDisplay())")
    @Mapping(target = "receiptId", source = "receipt.id")
    @Mapping(target = "receiptNumber", source = "receipt.receiptNumber")
    @Mapping(target = "isOverdue", expression = "java(feeJournal.isOverdue())")
    FeeJournalResponseDTO toResponseDTO(FeeJournal feeJournal);

    /**
     * Convert list of FeeJournal entities to list of response DTOs
     * @param feeJournals List of fee journals
     * @return List of FeeJournalResponseDTOs
     */
    List<FeeJournalResponseDTO> toResponseDTOList(List<FeeJournal> feeJournals);

    /**
     * Convert request DTO to FeeJournal entity
     * Note: Student must be set separately by service layer
     * @param dto FeeJournalRequestDTO
     * @return FeeJournal entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "receipt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FeeJournal toEntity(FeeJournalRequestDTO dto);

    /**
     * Update existing entity from request DTO
     * @param dto FeeJournalRequestDTO
     * @param feeJournal Existing FeeJournal entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "receipt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(FeeJournalRequestDTO dto, @MappingTarget FeeJournal feeJournal);
}
