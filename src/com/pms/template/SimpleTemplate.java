package com.pms.template;

import com.pms.model.Profile;

import java.awt.*;

/**
 * SimpleTemplate
 * Minimalist, ATS-friendly single-column layout with centered header and clean typographic dividers.
 * 
 * Demonstrates:
 * - High readability typography
 * - Clean layout formatting in Java 2D
 */
public class SimpleTemplate extends ResumeTemplate {

    public SimpleTemplate() {
        super(TemplateType.SIMPLE);
    }

    @Override
    public Color getPrimaryColor() {
        return new Color(17, 24, 39); // Deep Charcoal / Jet Black
    }

    @Override
    public Color getSecondaryColor() {
        return new Color(75, 85, 99); // Medium Neutral Gray
    }

    @Override
    public Color getAccentColor() {
        return new Color(31, 41, 55); // Dark Charcoal
    }

    @Override
    public Color getBackgroundColor() {
        return Color.WHITE;
    }

    @Override
    public Color getTextColor() {
        return new Color(31, 41, 55);
    }

    @Override
    public Color getLightBackground() {
        return new Color(249, 250, 251);
    }

    @Override
    public boolean isTwoColumn() {
        return false;
    }

    @Override
    public void render(Graphics2D g2, Profile profile, int width, int height) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // White paper background
        g2.setColor(getBackgroundColor());
        g2.fillRect(0, 0, width, height);

        int margin = 40;
        int contentWidth = width - (2 * margin);
        int y = 40;

        // --- Centered Header ---
        g2.setFont(new Font("Serif", Font.BOLD, 26));
        g2.setColor(getPrimaryColor());
        FontMetrics fm = g2.getFontMetrics();
        String name = profile.getFullName().toUpperCase();
        g2.drawString(name, (width - fm.stringWidth(name)) / 2, y);
        y += 20;

        // Contact details line (bullet separated)
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.setColor(getSecondaryColor());
        fm = g2.getFontMetrics();
        String contactLine = profile.getEmail() + "  •  " + profile.getPhone() + "  •  " + profile.getAddress();
        g2.drawString(contactLine, (width - fm.stringWidth(contactLine)) / 2, y);
        y += 20;

        // Header bottom divider
        g2.setColor(new Color(209, 213, 219));
        g2.fillRect(margin, y, contentWidth, 1);
        y += 20;

        // 1. Objective
        y = drawSimpleSection(g2, "OBJECTIVE", profile.getCareerObjective(), margin, y, contentWidth);

        // 2. Education
        y = drawSimpleEducation(g2, profile, margin, y, contentWidth);

        // 3. Technical Skills
        if (!profile.getTechnicalSkills().isEmpty()) {
            y = drawSimpleSection(g2, "TECHNICAL SKILLS", profile.getTechnicalSkills(), margin, y, contentWidth);
        }

        // 4. Projects
        if (!profile.getProjects().isEmpty()) {
            y = drawSimpleSection(g2, "PROJECTS", profile.getProjects(), margin, y, contentWidth);
        }

        // 5. Experience
        if (!profile.getExperience().isEmpty()) {
            y = drawSimpleSection(g2, "EXPERIENCE", profile.getExperience(), margin, y, contentWidth);
        }

        // 6. Certifications
        if (!profile.getCertifications().isEmpty()) {
            y = drawSimpleSection(g2, "CERTIFICATIONS", profile.getCertifications(), margin, y, contentWidth);
        }

        // 7. Achievements
        if (!profile.getAchievements().isEmpty()) {
            y = drawSimpleSection(g2, "ACHIEVEMENTS", profile.getAchievements(), margin, y, contentWidth);
        }

        // 8. Languages
        if (!profile.getLanguages().isEmpty()) {
            drawSimpleSection(g2, "LANGUAGES", profile.getLanguages(), margin, y, contentWidth);
        }
    }

    private int drawSimpleEducation(Graphics2D g2, Profile profile, int x, int y, int width) {
        y = drawHeaderWithLine(g2, "EDUCATION", x, y, width);

        // Degree and College
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.setColor(getPrimaryColor());
        g2.drawString(profile.getDegreeBranch(), x, y);

        String yearStr = "Graduation: " + profile.getGraduationYear();
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(yearStr, x + width - fm.stringWidth(yearStr), y);
        y += 16;

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(getSecondaryColor());
        g2.drawString(profile.getCollegeName(), x, y);

        String scoreStr = "Score: " + profile.getCgpaPercentage();
        fm = g2.getFontMetrics();
        g2.drawString(scoreStr, x + width - fm.stringWidth(scoreStr), y);
        y += 20;

        return y;
    }

    private int drawHeaderWithLine(Graphics2D g2, String title, int x, int y, int width) {
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.setColor(getPrimaryColor());
        g2.drawString(title, x, y);
        y += 4;

        g2.setColor(new Color(55, 65, 81));
        g2.fillRect(x, y, width, 1);
        return y + 14;
    }

    private int drawSimpleSection(Graphics2D g2, String title, String content, int x, int y, int width) {
        if (content == null || content.trim().isEmpty()) return y;
        y = drawHeaderWithLine(g2, title, x, y, width);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(getTextColor());
        y = drawWrappedText(g2, content, x, y, width, 16);
        return y + 12;
    }
}
