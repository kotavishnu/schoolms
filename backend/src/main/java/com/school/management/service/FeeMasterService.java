package com.school.management.service;

import com.school.management.dto.FeeMasterDTO;
import com.school.management.exception.BadRequestException;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.model.FeeMaster;
import com.school.management.repository.FeeMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FeeMasterService {

    private final FeeMasterRepository feeMasterRepository;

    public FeeMasterDTO createFeeMaster(FeeMasterDTO dto) {
        // Validate class range
        if (dto.getApplicableClassFrom() > dto.getApplicableClassTo()) {
            throw new BadRequestException("Applicable class 'from' cannot be greater than 'to'");
        }

        FeeMaster feeMaster = FeeMaster.builder()
                .feeType(dto.getFeeType())
                .amount(dto.getAmount())
                .applicableClassFrom(dto.getApplicableClassFrom())
                .applicableClassTo(dto.getApplicableClassTo())
                .frequency(dto.getFrequency())
                .academicYear(dto.getAcademicYear())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        FeeMaster saved = feeMasterRepository.save(feeMaster);
        return convertToDTO(saved);
    }

    @Transactional(readOnly = true)
    public FeeMasterDTO getFeeMasterById(Long id) {
        FeeMaster feeMaster = feeMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee Master", "id", id));
        return convertToDTO(feeMaster);
    }

    @Transactional(readOnly = true)
    public List<FeeMasterDTO> getAllFeeMasters() {
        return feeMasterRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeeMasterDTO> getFeeMastersByAcademicYear(String academicYear) {
        return feeMasterRepository.findByAcademicYear(academicYear).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeeMasterDTO> getApplicableFeesForClass(String academicYear, Integer classNumber) {
        return feeMasterRepository.findApplicableFeesForClass(academicYear, classNumber).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FeeMasterDTO updateFeeMaster(Long id, FeeMasterDTO dto) {
        FeeMaster existingFeeMaster = feeMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee Master", "id", id));

        if (dto.getApplicableClassFrom() > dto.getApplicableClassTo()) {
            throw new BadRequestException("Applicable class 'from' cannot be greater than 'to'");
        }

        existingFeeMaster.setFeeType(dto.getFeeType());
        existingFeeMaster.setAmount(dto.getAmount());
        existingFeeMaster.setApplicableClassFrom(dto.getApplicableClassFrom());
        existingFeeMaster.setApplicableClassTo(dto.getApplicableClassTo());
        existingFeeMaster.setFrequency(dto.getFrequency());
        existingFeeMaster.setAcademicYear(dto.getAcademicYear());
        existingFeeMaster.setIsActive(dto.getIsActive());
        existingFeeMaster.setUpdatedAt(LocalDateTime.now());

        FeeMaster updated = feeMasterRepository.save(existingFeeMaster);
        return convertToDTO(updated);
    }

    public void deleteFeeMaster(Long id) {
        FeeMaster feeMaster = feeMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee Master", "id", id));
        feeMasterRepository.delete(feeMaster);
    }

    private FeeMasterDTO convertToDTO(FeeMaster feeMaster) {
        return FeeMasterDTO.builder()
                .id(feeMaster.getId())
                .feeType(feeMaster.getFeeType())
                .amount(feeMaster.getAmount())
                .applicableClassFrom(feeMaster.getApplicableClassFrom())
                .applicableClassTo(feeMaster.getApplicableClassTo())
                .frequency(feeMaster.getFrequency())
                .academicYear(feeMaster.getAcademicYear())
                .isActive(feeMaster.getIsActive())
                .build();
    }
}
