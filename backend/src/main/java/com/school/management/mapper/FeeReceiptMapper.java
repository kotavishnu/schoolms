package com.school.management.mapper;

import com.school.management.dto.request.FeeReceiptRequestDTO;
import com.school.management.dto.response.FeeReceiptResponseDTO;
import com.school.management.model.FeeReceipt;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for FeeReceipt entity and DTOs.
 *
 * Automatically generates implementation for entity-DTO conversions.
 *
 * @author School Management Team
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeeReceiptMapper {

    /**
     * Convert FeeReceipt entity to response DTO
     * @param feeReceipt FeeReceipt entity
     * @return FeeReceiptResponseDTO
     */
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", expression = "java(feeReceipt.getStudent() != null ? feeReceipt.getStudent().getFullName() : null)")
    @Mapping(target = "className", expression = "java(feeReceipt.getStudent() != null && feeReceipt.getStudent().getSchoolClass() != null ? feeReceipt.getStudent().getSchoolClass().getDisplayName() : null)")
    @Mapping(target = "pdfUrl", expression = "java(\"/api/fee-receipts/\" + feeReceipt.getId() + \"/pdf\")")
    FeeReceiptResponseDTO toResponseDTO(FeeReceipt feeReceipt);

    /**
     * Convert list of FeeReceipt entities to list of response DTOs
     * @param feeReceipts List of fee receipts
     * @return List of FeeReceiptResponseDTOs
     */
    List<FeeReceiptResponseDTO> toResponseDTOList(List<FeeReceipt> feeReceipts);

    /**
     * Convert request DTO to FeeReceipt entity
     * Note: Student and receipt number must be set separately by service layer
     * @param dto FeeReceiptRequestDTO
     * @return FeeReceipt entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "receiptNumber", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "monthsPaid", expression = "java(dto.getMonthsPaid() != null ? String.join(\", \", dto.getMonthsPaid()) : \"\")")
    @Mapping(target = "generatedBy", constant = "ADMIN")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "journalEntries", ignore = true)
    FeeReceipt toEntity(FeeReceiptRequestDTO dto);

    /**
     * Update existing entity from request DTO
     * @param dto FeeReceiptRequestDTO
     * @param feeReceipt Existing FeeReceipt entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "receiptNumber", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "monthsPaid", expression = "java(dto.getMonthsPaid() != null ? String.join(\", \", dto.getMonthsPaid()) : \"\")")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "journalEntries", ignore = true)
    void updateEntityFromDTO(FeeReceiptRequestDTO dto, @MappingTarget FeeReceipt feeReceipt);
}
