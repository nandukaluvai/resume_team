package com.pms.template;

import com.pms.model.Profile;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ResumeTemplate Base Class
 * Abstract class defining the blueprint and common rendering algorithms
 * for all resume visual templates.
 * 
 * Demonstrates:
 * - Abstract Classes and Inheritance
 * - Polymorphism (Subclasses customize colors, layout, and section flow)
 * - Template Method Design Pattern
 * - Java 2D Graphics rendering (Graphics2D)
 */
public abstract class ResumeTemplate {
    protected final TemplateType type;
    protected final String name;
    protected final String description;

    public ResumeTemplate(TemplateType type) {
        this.type = type;
        this.name = type.getDisplayName();
        this.description = type.getDescription();
    }

    public TemplateType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Abstract methods defining color palette and layout behaviors
    public abstract Color getPrimaryColor();
    public abstract Color getSecondaryColor();
    public abstract Color getAccentColor();
    public abstract Color getBackgroundColor();
    public abstract Color getTextColor();
    public abstract Color getLightBackground();
    public abstract boolean isTwoColumn();

    /**
     * Polymorphic rendering method for previewing on a Java Swing Graphics2D canvas.
     */
    public abstract void render(Graphics2D g2, Profile profile, int width, int height);

    // --- Common Typography & Layout Helper Methods ---

    /**
     * Splits text into lines that fit within the specified maxWidth.
     */
    protected List<String> wrapText(FontMetrics fm, String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return lines;
        }

        String[] rawLines = text.split("\n");
        for (String rawLine : rawLines) {
            String trimmed = rawLine.trim();
            if (trimmed.isEmpty()) {
                lines.add("");
                continue;
            }

            String[] words = trimmed.split("\\s+");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                String candidate = currentLine.length() == 0 ? word : currentLine + " " + word;
                if (fm.stringWidth(candidate) <= maxWidth) {
                    currentLine = new StringBuilder(candidate);
                } else {
                    if (currentLine.length() > 0) {
                        lines.add(currentLine.toString());
                    }
                    currentLine = new StringBuilder(word);
                }
            }
            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }
        }
        return lines;
    }

    /**
     * Draws wrapped text paragraph and returns the new Y position.
     */
    protected int drawWrappedText(Graphics2D g2, String text, int x, int y, int maxWidth, int lineHeight) {
        if (text == null || text.trim().isEmpty()) return y;
        FontMetrics fm = g2.getFontMetrics();
        List<String> lines = wrapText(fm, text, maxWidth);
        for (String line : lines) {
            g2.drawString(line, x, y);
            y += lineHeight;
        }
        return y;
    }

    /**
     * Factory method to obtain all registered templates.
     */
    public static List<ResumeTemplate> getAllTemplates() {
        List<ResumeTemplate> list = new ArrayList<>();
        list.add(new ProfessionalTemplate());
        list.add(new ModernTemplate());
        list.add(new SimpleTemplate());
        list.add(new StudentFresherTemplate());
        list.add(new TechnicalTemplate());
        list.add(new CreativeTemplate());
        return list;
    }

    /**
     * Factory method to obtain template instance by type.
     */
    public static ResumeTemplate getTemplate(TemplateType type) {
        switch (type) {
            case MODERN:
                return new ModernTemplate();
            case SIMPLE:
                return new SimpleTemplate();
            case STUDENT_FRESHER:
                return new StudentFresherTemplate();
            case TECHNICAL:
                return new TechnicalTemplate();
            case CREATIVE:
                return new CreativeTemplate();
            case PROFESSIONAL:
            default:
                return new ProfessionalTemplate();
        }
    }
}
