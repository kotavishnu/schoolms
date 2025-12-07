package com.school.sms.student.infrastructure.cache;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Custom cache key generator for Student Service.
 *
 * Implements consistent cache key naming convention as per LESSONS_LEARNED.md [D-004]
 *
 * Cache Key Patterns:
 * - Student by ID: sms:student:id:{studentId}
 * - Student search: sms:student:search:{md5(searchParams)}
 * - Active count: sms:student:count:active
 * - Enrollment: sms:student:enrollment:{studentId}
 *
 * All keys are kept under 100 characters for performance.
 *
 * @author SMS Backend Team
 * @version 1.0.0
 * @since 2025-12-06
 */
@Component("studentKeyGenerator")
public class CacheKeyGenerator implements KeyGenerator {

    private static final String PREFIX = "sms:student:";
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

        // Generate key based on method name
        if (methodName.contains("Search") || methodName.contains("find")) {
            // For search operations, use MD5 hash of parameters
            key = PREFIX + "search:" + hashParams(params);
        } else if (methodName.contains("count") || methodName.contains("Count")) {
            // For count operations
            key = PREFIX + "count:" + joinParams(params);
        } else if (params.length > 0) {
            // Default: use method name and first parameter
            key = PREFIX + methodName + ":" + params[0];
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
     * Create MD5 hash of method parameters for search keys.
     *
     * Prevents key collisions and keeps keys short.
     *
     * @param params Method parameters
     * @return MD5 hash of parameters
     */
    private String hashParams(Object... params) {
        String paramsString = Arrays.stream(params)
                .map(param -> param != null ? param.toString() : "null")
                .collect(Collectors.joining(":"));

        return DigestUtils.md5Hex(paramsString);
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
     * Generate key for student ID lookup.
     *
     * Pattern: sms:student:id:{studentId}
     *
     * @param studentId Student identifier
     * @return Cache key
     */
    public static String studentByIdKey(String studentId) {
        return PREFIX + "id:" + studentId;
    }

    /**
     * Generate key for active students count.
     *
     * Pattern: sms:student:count:active
     *
     * @return Cache key
     */
    public static String activeStudentsCountKey() {
        return PREFIX + "count:active";
    }

    /**
     * Generate key for student enrollment.
     *
     * Pattern: sms:student:enrollment:{studentId}
     *
     * @param studentId Student identifier
     * @return Cache key
     */
    public static String enrollmentKey(String studentId) {
        return PREFIX + "enrollment:" + studentId;
    }

    /**
     * Generate key for search results.
     *
     * Pattern: sms:student:search:{md5Hash}
     *
     * @param searchCriteria Search criteria object
     * @return Cache key
     */
    public static String searchKey(Object searchCriteria) {
        String criteriaString = searchCriteria != null ? searchCriteria.toString() : "empty";
        return PREFIX + "search:" + DigestUtils.md5Hex(criteriaString);
    }
}
