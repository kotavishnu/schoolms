package com.school.management.service;

import com.school.management.model.FeeMaster;
import com.school.management.repository.FeeMasterRepository;
import com.school.management.util.FeeCalculationRequest;
import com.school.management.util.FeeCalculationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeCalculationService {

    private final FeeMasterRepository feeMasterRepository;

    public FeeCalculationResult calculateFee(Long studentId, Integer classNumber,
                                            String academicYear, Boolean isFirstMonth) {
        // Create result
        FeeCalculationResult result = new FeeCalculationResult();

        // Get all applicable fee masters
        List<FeeMaster> feeMasters = feeMasterRepository
            .findApplicableFeesForClass(academicYear, classNumber);

        // Simple calculation logic (TODO: Replace with Drools when fixed)
        for (FeeMaster fee : feeMasters) {
            if ("Special".equals(fee.getFeeType()) && !isFirstMonth) {
                continue; // Skip special fee if not first month
            }
            result.addFeeComponent(fee.getFeeType() + " Fee", fee.getAmount());
        }

        result.addDescription("Fee calculated for Class " + classNumber);

        return result;
    }
}
