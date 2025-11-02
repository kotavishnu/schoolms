package com.school.management.service;

import com.school.management.dto.request.FeeMasterRequestDTO;
import com.school.management.dto.response.FeeMasterResponseDTO;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.exception.ValidationException;
import com.school.management.mapper.FeeMasterMapper;
import com.school.management.model.FeeMaster;
import com.school.management.model.FeeType;
import com.school.management.repository.FeeMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service layer for Fee Master management operations.
 *
 * Handles business logic for fee structure configuration, validation, and applicability.
 *
 * @author School Management Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FeeMasterService {

    private final FeeMasterRepository feeMasterRepository;
    private final FeeMasterMapper feeMasterMapper;

    /**
     * Create a new fee master
     *
     * @param requestDTO Fee master data
     * @return Created fee master DTO
     * @throws ValidationException if date range validation fails
     */
    @Transactional
    public FeeMasterResponseDTO createFeeMaster(FeeMasterRequestDTO requestDTO) {
        log.info("Creating fee master: {} - {}", requestDTO.getFeeType(), requestDTO.getAmount());

        // Validate applicable date range
        if (requestDTO.getApplicableTo() != null &&
            requestDTO.getApplicableTo().isBefore(requestDTO.getApplicableFrom())) {
            throw new ValidationException("Applicable to date must be after applicable from date");
        }

        // Map DTO to entity
        FeeMaster feeMaster = feeMasterMapper.toEntity(requestDTO);

        // Save fee master
        FeeMaster savedFeeMaster = feeMasterRepository.save(feeMaster);

        log.info("Fee master created successfully with ID: {}", savedFeeMaster.getId());

        return feeMasterMapper.toResponseDTO(savedFeeMaster);
    }

    /**
     * Get fee master by ID
     *
     * @param id Fee master ID
     * @return Fee master DTO
     * @throws ResourceNotFoundException if fee master not found
     */
    public FeeMasterResponseDTO getFeeMaster(Long id) {
        log.debug("Fetching fee master with ID: {}", id);

        FeeMaster feeMaster = feeMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeMaster", "id", id));

        return feeMasterMapper.toResponseDTO(feeMaster);
    }

    /**
     * Get all fee masters
     *
     * @return List of fee master DTOs
     */
    public List<FeeMasterResponseDTO> getAllFeeMasters() {
        log.debug("Fetching all fee masters");

        List<FeeMaster> feeMasters = feeMasterRepository.findAll();

        return feeMasterMapper.toResponseDTOList(feeMasters);
    }

    /**
     * Get fee masters by fee type
     *
     * @param feeType Fee type
     * @return List of fee master DTOs
     */
    public List<FeeMasterResponseDTO> getFeeMastersByType(FeeType feeType) {
        log.debug("Fetching fee masters by type: {}", feeType);

        List<FeeMaster> feeMasters = feeMasterRepository.findByFeeType(feeType);

        return feeMasterMapper.toResponseDTOList(feeMasters);
    }

    /**
     * Get active fee masters by fee type
     *
     * @param feeType Fee type
     * @return List of active fee master DTOs
     */
    public List<FeeMasterResponseDTO> getActiveFeeMastersByType(FeeType feeType) {
        log.debug("Fetching active fee masters by type: {}", feeType);

        List<FeeMaster> feeMasters = feeMasterRepository.findByFeeTypeAndIsActive(feeType, true);

        return feeMasterMapper.toResponseDTOList(feeMasters);
    }

    /**
     * Get fee masters by academic year
     *
     * @param academicYear Academic year
     * @return List of fee master DTOs
     */
    public List<FeeMasterResponseDTO> getFeeMastersByAcademicYear(String academicYear) {
        log.debug("Fetching fee masters for academic year: {}", academicYear);

        List<FeeMaster> feeMasters = feeMasterRepository.findByAcademicYear(academicYear);

        return feeMasterMapper.toResponseDTOList(feeMasters);
    }

    /**
     * Get active fee masters for academic year
     *
     * @param academicYear Academic year
     * @return List of active fee master DTOs
     */
    public List<FeeMasterResponseDTO> getActiveFeeMastersByAcademicYear(String academicYear) {
        log.debug("Fetching active fee masters for academic year: {}", academicYear);

        List<FeeMaster> feeMasters = feeMasterRepository.findByAcademicYearAndIsActive(academicYear, true);

        return feeMasterMapper.toResponseDTOList(feeMasters);
    }

    /**
     * Get currently applicable fee masters
     *
     * @return List of currently applicable fee master DTOs
     */
    public List<FeeMasterResponseDTO> getCurrentlyApplicableFeeMasters() {
        log.debug("Fetching currently applicable fee masters");

        List<FeeMaster> feeMasters = feeMasterRepository.findCurrentlyApplicable(LocalDate.now());

        return feeMasterMapper.toResponseDTOList(feeMasters);
    }

    /**
     * Get latest applicable fee master for a specific fee type
     *
     * @param feeType Fee type
     * @return Fee master DTO
     * @throws ResourceNotFoundException if no applicable fee master found
     */
    public FeeMasterResponseDTO getLatestApplicableFeeMaster(FeeType feeType) {
        log.debug("Fetching latest applicable fee master for type: {}", feeType);

        FeeMaster feeMaster = feeMasterRepository.findLatestApplicable(feeType, LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "FeeMaster", "feeType", feeType.toString()));

        return feeMasterMapper.toResponseDTO(feeMaster);
    }

    /**
     * Get all active fee masters
     *
     * @return List of active fee master DTOs
     */
    public List<FeeMasterResponseDTO> getActiveFeeMasters() {
        log.debug("Fetching all active fee masters");

        List<FeeMaster> feeMasters = feeMasterRepository.findByIsActiveTrue();

        return feeMasterMapper.toResponseDTOList(feeMasters);
    }

    /**
     * Update existing fee master
     *
     * @param id Fee master ID
     * @param requestDTO Updated fee master data
     * @return Updated fee master DTO
     * @throws ResourceNotFoundException if fee master not found
     * @throws ValidationException if date range validation fails
     */
    @Transactional
    public FeeMasterResponseDTO updateFeeMaster(Long id, FeeMasterRequestDTO requestDTO) {
        log.info("Updating fee master with ID: {}", id);

        // Fetch existing fee master
        FeeMaster feeMaster = feeMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeMaster", "id", id));

        // Validate applicable date range
        if (requestDTO.getApplicableTo() != null &&
            requestDTO.getApplicableTo().isBefore(requestDTO.getApplicableFrom())) {
            throw new ValidationException("Applicable to date must be after applicable from date");
        }

        // Update fields
        feeMasterMapper.updateEntityFromDTO(requestDTO, feeMaster);

        FeeMaster updatedFeeMaster = feeMasterRepository.save(feeMaster);

        log.info("Fee master updated successfully with ID: {}", updatedFeeMaster.getId());

        return feeMasterMapper.toResponseDTO(updatedFeeMaster);
    }

    /**
     * Deactivate fee master
     *
     * @param id Fee master ID
     * @return Updated fee master DTO
     * @throws ResourceNotFoundException if fee master not found
     */
    @Transactional
    public FeeMasterResponseDTO deactivateFeeMaster(Long id) {
        log.info("Deactivating fee master with ID: {}", id);

        FeeMaster feeMaster = feeMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeMaster", "id", id));

        feeMaster.setIsActive(false);

        FeeMaster updatedFeeMaster = feeMasterRepository.save(feeMaster);

        log.info("Fee master deactivated successfully with ID: {}", id);

        return feeMasterMapper.toResponseDTO(updatedFeeMaster);
    }

    /**
     * Activate fee master
     *
     * @param id Fee master ID
     * @return Updated fee master DTO
     * @throws ResourceNotFoundException if fee master not found
     */
    @Transactional
    public FeeMasterResponseDTO activateFeeMaster(Long id) {
        log.info("Activating fee master with ID: {}", id);

        FeeMaster feeMaster = feeMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeMaster", "id", id));

        feeMaster.setIsActive(true);

        FeeMaster updatedFeeMaster = feeMasterRepository.save(feeMaster);

        log.info("Fee master activated successfully with ID: {}", id);

        return feeMasterMapper.toResponseDTO(updatedFeeMaster);
    }

    /**
     * Delete fee master
     *
     * @param id Fee master ID
     * @throws ResourceNotFoundException if fee master not found
     */
    @Transactional
    public void deleteFeeMaster(Long id) {
        log.info("Deleting fee master with ID: {}", id);

        FeeMaster feeMaster = feeMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeMaster", "id", id));

        feeMasterRepository.deleteById(id);

        log.info("Fee master deleted successfully with ID: {}", id);
    }

    /**
     * Count active fee masters for academic year
     *
     * @param academicYear Academic year
     * @return Count of active fee masters
     */
    public long countActiveFeeMasters(String academicYear) {
        log.debug("Counting active fee masters for year: {}", academicYear);

        return feeMasterRepository.countByAcademicYearAndIsActive(academicYear, true);
    }
}
