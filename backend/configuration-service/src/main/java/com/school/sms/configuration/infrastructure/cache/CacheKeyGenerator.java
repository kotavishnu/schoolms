package com.school.sms.configuration.infrastructure.cache;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Custom cache key generator for Configuration Service.
 *
 * Implements consistent cache key naming convention as per LESSONS_LEARNED.md [D-004]
 *
 * Cache Key Patterns:
 * - Config by category and key: sms:config:category:{category}:key:{key}
 * - All configs by category: sms:config:category:{category}:all
 * - Grouped configs: sms:config:grouped:{category}
 *
 * All keys are kept under 100 characters for performance.
 *
 * @author SMS Backend Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Component("configKeyGenerator")
public class CacheKeyGenerator implements KeyGenerator {

    private static final String PREFIX = "sms:config:";
    private static final int MAX_KEY_LENGTH = 100;

    /**
     * Generate cache key from method invocation.
     *
     * @param target The target instance
     * @param method The method being invoked
     * @param params The method parameters
     * @return Generated cache key
     */
    @Override
    public Object generate(Object target, Method method, Object... params) {
        String methodName = method.getName();
        String key;

        // Generate key based on method name and parameters
        if (methodName.contains("getConfiguration") && params.length == 2) {
            // Pattern: sms:config:category:{category}:key:{key}
            key = PREFIX + "category:" + params[0] + ":key:" + params[1];
        } else if (methodName.contains("getAllConfigurations") && params.length == 1) {
            // Pattern: sms:config:category:{category}:all
            key = PREFIX + "category:" + params[0] + ":all";
        } else if (methodName.contains("getGroupedByCategory") && params.length == 1) {
            // Pattern: sms:config:grouped:{category}
            key = PREFIX + "grouped:" + params[0];
        } else if (params.length > 0) {
            // Default: use method name and parameters
            key = PREFIX + methodName + ":" + joinParams(params);
        } else {
            // Fallback: use method name only
            key = PREFIX + methodName;
        }

        // Ensure key length is under limit
        if (key.length() > MAX_KEY_LENGTH) {
            key = PREFIX + DigestUtils.md5Hex(key);
        }

        return key;
    }

    /**
     * Join parameters with colon delimiter.
     *
     * @param params Method parameters
     * @return Joined parameter string
     */
    private String joinParams(Object... params) {
        return Arrays.stream(params)
                .map(param -> param != null ? param.toString() : "null")
                .collect(Collectors.joining(":"));
    }

    /**
     * Generate key for configuration by category and key.
     *
     * Pattern: sms:config:category:{category}:key:{key}
     *
     * @param category Configuration category
     * @param key Configuration key
     * @return Cache key
     */
    public static String configByCategoryAndKeyKey(String category, String key) {
        return PREFIX + "category:" + category + ":key:" + key;
    }

    /**
     * Generate key for all configurations by category.
     *
     * Pattern: sms:config:category:{category}:all
     *
     * @param category Configuration category
     * @return Cache key
     */
    public static String allConfigsByCategoryKey(String category) {
        return PREFIX + "category:" + category + ":all";
    }

    /**
     * Generate key for grouped configurations.
     *
     * Pattern: sms:config:grouped:{category}
     *
     * @param category Configuration category
     * @return Cache key
     */
    public static String groupedConfigsKey(String category) {
        return PREFIX + "grouped:" + category;
    }
}
