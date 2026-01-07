package com.school.management.util;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FeeCalculationResult {

    private Map<String, BigDecimal> feeComponents = new HashMap<>();
    private List<String> descriptions = new ArrayList<>();
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public void addFeeComponent(String componentName, BigDecimal amount) {
        feeComponents.put(componentName, amount);
        totalAmount = totalAmount.add(amount);
    }

    public void addDescription(String description) {
        descriptions.add(description);
    }
}
