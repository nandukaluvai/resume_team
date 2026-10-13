package com.pms.template;

import com.pms.model.Profile;

import java.awt.*;
import java.util.List;

/**
 * ModernTemplate
 * Contemporary two-column layout featuring an elegant dark sidebar and clean main content.
 * 
 * Demonstrates:
 * - Complex 2-column Graphics2D layout calculation
 * - Polymorphism in resume structure
 */
public class ModernTemplate extends ResumeTemplate {

    public ModernTemplate() {
        super(TemplateType.MODERN);
    }

    @Override
    public Color getPrimaryColor() {
        return new Color(14, 116, 144); // Deep Cyan/Teal
    }

    @Override
    public Color getSecondaryColor() {
        return new Color(15, 23, 42); // Midnight Slate
    }

    @Override
    public Color getAccentColor() {
        return new Color(6, 182, 212); // Bright Cyan
    }

    @Override
    public Color getBackgroundColor() {
        return Color.WHITE;
    }

    @Override
    public Color getTextColor() {
        return new Color(30, 41, 59);
    }

    @Override
    public Color getLightBackground() {
        return new Color(248, 250, 252);
    }

    @Override
    public boolean isTwoColumn() {
        return true;
    }

    @Override
    public void render(Graphics2D g2, Profile profile, int width, int height) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // White background for main content
        g2.setColor(getBackgroundColor());
        g2.fillRect(0, 0, width, height);

        // --- Left Sidebar (32% width) ---
        int sidebarWidth = (int) (width * 0.32);
        g2.setColor(new Color(30, 41, 59)); // Dark Slate Blue Sidebar
        g2.fillRect(0, 0, sidebarWidth, height);

        // Sidebar Accent Stripe
        g2.setColor(getAccentColor());
        g2.fillRect(sidebarWidth - 3, 0, 3, height);

        // --- Sidebar Content ---
        int sx = 20;
        int sContentWidth = sidebarWidth - 40;
        int sy = 35;

        // Initials Avatar Circle
        int circleSize = 60;
        g2.setColor(getAccentColor());
        g2.fillOval(sx + (sContentWidth - circleSize) / 2, sy, circleSize, circleSize);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        String initials = getInitials(profile.getFullName());
        FontMetrics fmInit = g2.getFontMetrics();
        g2.drawString(initials,
                sx + (sContentWidth - fmInit.stringWidth(initials)) / 2,
                sy + (circleSize + fmInit.getAscent() - 6) / 2);
        sy += circleSize + 25;

        // Contact Section in Sidebar
        sy = drawSidebarSectionHeading(g2, "CONTACT", sx, sy, sContentWidth);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.setColor(new Color(226, 232, 240));

        sy = drawSidebarField(g2, "EMAIL", profile.getEmail(), sx, sy, sContentWidth);
        sy = drawSidebarField(g2, "PHONE", profile.getPhone(), sx, sy, sContentWidth);
        sy = drawSidebarField(g2, "LOCATION", profile.getAddress(), sx, sy, sContentWidth);
        sy += 12;

        // Education in Sidebar
        sy = drawSidebarSectionHeading(g2, "EDUCATION", sx, sy, sContentWidth);
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.setColor(Color.WHITE);
        sy = drawWrappedText(g2, profile.getDegreeBranch(), sx, sy, sContentWidth, 14);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.setColor(new Color(203, 213, 225));
        sy = drawWrappedText(g2, profile.getCollegeName(), sx, sy, sContentWidth, 14);
        g2.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2.setColor(getAccentColor());
        g2.drawString("Class of " + profile.getGraduationYear() + "  |  " + profile.getCgpaPercentage(), sx, sy);
        sy += 24;

        // Technical Skills in Sidebar
        if (!profile.getTechnicalSkills().isEmpty()) {
            sy = drawSidebarSectionHeading(g2, "SKILLS", sx, sy, sContentWidth);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(new Color(226, 232, 240));
            sy = drawWrappedText(g2, profile.getTechnicalSkills(), sx, sy, sContentWidth, 15);
            sy += 12;
        }

        // Languages in Sidebar
        if (!profile.getLanguages().isEmpty()) {
            sy = drawSidebarSectionHeading(g2, "LANGUAGES", sx, sy, sContentWidth);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(new Color(226, 232, 240));
            drawWrappedText(g2, profile.getLanguages(), sx, sy, sContentWidth, 14);
        }

        // --- Right Main Column Content ---
        int mx = sidebarWidth + 30;
        int mContentWidth = width - mx - 30;
        int my = 40;

        // Full Name
        g2.setColor(getSecondaryColor());
        g2.setFont(new Font("SansSerif", Font.BOLD, 28));
        g2.drawString(profile.getFullName(), mx, my);
        my += 24;

        // Subtitle Degree & Track
        g2.setColor(getPrimaryColor());
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2.drawString(profile.getDegreeBranch().toUpperCase(), mx, my);
        my += 28;

        // Thin Separator
        g2.setColor(new Color(226, 232, 240));
        g2.fillRect(mx, my - 12, mContentWidth, 2);

        // 1. Profile / Objective
        my = drawMainSection(g2, "PROFILE SUMMARY", profile.getCareerObjective(), mx, my, mContentWidth);

        // 2. Work Experience / Internships
        if (!profile.getExperience().isEmpty()) {
            my = drawMainSection(g2, "EXPERIENCE & INTERNSHIPS", profile.getExperience(), mx, my, mContentWidth);
        }

        // 3. Projects
        if (!profile.getProjects().isEmpty()) {
            my = drawMainSection(g2, "KEY PROJECTS", profile.getProjects(), mx, my, mContentWidth);
        }

        // 4. Certifications
        if (!profile.getCertifications().isEmpty()) {
            my = drawMainSection(g2, "CERTIFICATIONS", profile.getCertifications(), mx, my, mContentWidth);
        }

        // 5. Achievements
        if (!profile.getAchievements().isEmpty()) {
            drawMainSection(g2, "ACHIEVEMENTS & HONORS", profile.getAchievements(), mx, my, mContentWidth);
        }
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "U";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    private int drawSidebarSectionHeading(Graphics2D g2, String title, int x, int y, int width) {
        g2.setColor(getAccentColor());
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.drawString(title, x, y);
        g2.fillRect(x, y + 4, width, 1);
        return y + 18;
    }

    private int drawSidebarField(Graphics2D g2, String label, String value, int x, int y, int width) {
        if (value == null || value.trim().isEmpty()) return y;
        g2.setFont(new Font("SansSerif", Font.BOLD, 9));
        g2.setColor(new Color(148, 163, 184));
        g2.drawString(label, x, y);
        y += 12;

        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.setColor(new Color(241, 245, 249));
        y = drawWrappedText(g2, value, x, y, width, 13);
        return y + 6;
    }

    private int drawMainSection(Graphics2D g2, String title, String content, int x, int y, int width) {
        if (content == null || content.trim().isEmpty()) return y;

        // Section header badge
        g2.setColor(getPrimaryColor());
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2.drawString(title, x, y);

        // Accent underline
        g2.setColor(getAccentColor());
        g2.fillRect(x, y + 4, 30, 2);
        y += 20;

        // Body Text
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(getTextColor());
        y = drawWrappedText(g2, content, x, y, width, 16);
        return y + 14;
    }
}
