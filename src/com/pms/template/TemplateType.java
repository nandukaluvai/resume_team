package com.pms.template;

/**
 * TemplateType Enum
 * Defines the six distinct resume layout styles.
 */
public enum TemplateType {
    PROFESSIONAL("Professional", "Classic corporate layout with navy header and formal typography"),
    MODERN("Modern", "Sleek two-column layout with left sidebar and electric blue accents"),
    SIMPLE("Simple", "Clean minimalist single-column layout optimized for ATS readability"),
    STUDENT_FRESHER("Student / Fresher", "Highlights Education, Projects, and Academics with emerald teal accents"),
    TECHNICAL("Technical", "Developer-focused design with categorized skills and tech-stack highlights"),
    CREATIVE("Creative", "Vibrant Indigo/Violet aesthetic with modern section badges and cards");

    private final String displayName;
    private final String description;

    TemplateType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
