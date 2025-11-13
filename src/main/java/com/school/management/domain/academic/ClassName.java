package com.school.management.domain.academic;

/**
 * Enumeration of class names (1-10)
 *
 * @author School Management System
 * @version 1.0
 */
public enum ClassName {
    CLASS_1("1"),
    CLASS_2("2"),
    CLASS_3("3"),
    CLASS_4("4"),
    CLASS_5("5"),
    CLASS_6("6"),
    CLASS_7("7"),
    CLASS_8("8"),
    CLASS_9("9"),
    CLASS_10("10");

    private final String displayName;

    ClassName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
