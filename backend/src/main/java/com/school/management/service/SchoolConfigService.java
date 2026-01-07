package com.school.management.service;

import com.school.management.dto.SchoolConfigDTO;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.model.SchoolConfig;
import com.school.management.repository.SchoolConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class SchoolConfigService {

    private final SchoolConfigRepository schoolConfigRepository;

    public SchoolConfigDTO createOrUpdateConfig(SchoolConfigDTO dto) {
        // Check if config already exists
        SchoolConfig config = schoolConfigRepository.findFirstByOrderByIdDesc()
                .orElse(new SchoolConfig());

        config.setName(dto.getName());
        config.setAddress(dto.getAddress());
        config.setFeeFrequency(dto.getFeeFrequency());

        if (config.getId() == null) {
            config.setCreatedAt(LocalDateTime.now());
        }
        config.setUpdatedAt(LocalDateTime.now());

        SchoolConfig saved = schoolConfigRepository.save(config);
        return convertToDTO(saved);
    }

    @Transactional(readOnly = true)
    public SchoolConfigDTO getConfig() {
        SchoolConfig config = schoolConfigRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new ResourceNotFoundException("School configuration not found"));
        return convertToDTO(config);
    }

    private SchoolConfigDTO convertToDTO(SchoolConfig config) {
        return SchoolConfigDTO.builder()
                .id(config.getId())
                .name(config.getName())
                .address(config.getAddress())
                .feeFrequency(config.getFeeFrequency())
                .build();
    }
}
