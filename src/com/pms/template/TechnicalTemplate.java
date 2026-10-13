package com.pms.template;

import com.pms.model.Profile;

import java.awt.*;

/**
 * TechnicalTemplate
 * Developer and software engineer oriented layout with code accents, dark slate branding,
 * and prominent project and skill categorization.
 * 
 * Demonstrates:
 * - Technical styling adaptation
 * - Clean layout organization for software engineering resumes
 */
public class TechnicalTemplate extends ResumeTemplate {

    public TechnicalTemplate() {
        super(TemplateType.TECHNICAL);
    }

    @Override
    public Color getPrimaryColor() {
        return new Color(15, 23, 42); // Midnight Slate
    }

    @Override
    public Color getSecondaryColor() {
        return new Color(30, 41, 59); // Slate Dark
    }

    @Override
    public Color getAccentColor() {
        return new Color(2, 132, 199); // Sky / Tech Blue
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
        return new Color(241, 245, 249);
    }

    @Override
    public boolean isTwoColumn() {
        return false;
    }

    @Override
    public void render(Graphics2D g2, Profile profile, int width, int height) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Canvas Background
        g2.setColor(getBackgroundColor());
        g2.fillRect(0, 0, width, height);

        int margin = 36;
        int contentWidth = width - (2 * margin);
        int y = 35;

        // --- Tech Header ---
        g2.setColor(getPrimaryColor());
        g2.setFont(new Font("SansSerif", Font.BOLD, 26));
        g2.drawString(profile.getFullName(), margin, y);

        // Terminal Prompt Style Tag
        g2.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2.setColor(getAccentColor());
        String tag = "<" + profile.getDegreeBranch().toLowerCase().replace(" ", "_") + " />";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(tag, margin + contentWidth - fm.stringWidth(tag), y - 4);
        y += 20;

        // Contact details with tech icons
        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2.setColor(new Color(71, 85, 105));
        String contact = "email: " + profile.getEmail() + " | phone: " + profile.getPhone() + " | loc: " + profile.getAddress();
        g2.drawString(contact, margin, y);
        y += 16;

        // Header Divider Line
        g2.setColor(new Color(226, 232, 240));
        g2.fillRect(margin, y, contentWidth, 2);
        g2.setColor(getAccentColor());
        g2.fillRect(margin, y, 60, 2);
        y += 22;

        // 1. Technical Skills Highlight Box (Put at the top for engineers!)
        if (!profile.getTechnicalSkills().isEmpty()) {
            y = drawTechSection(g2, "CORE TECHNICAL SKILLS & STACK", profile.getTechnicalSkills(), margin, y, contentWidth);
        }

        // 2. Career Objective / Summary
        y = drawTechSection(g2, "SUMMARY / OBJECTIVE", profile.getCareerObjective(), margin, y, contentWidth);

        // 3. Technical Projects
        if (!profile.getProjects().isEmpty()) {
            y = drawTechSection(g2, "SOFTWARE PROJECTS & ARCHITECTURE", profile.getProjects(), margin, y, contentWidth);
        }

        // 4. Experience / Internships
        if (!profile.getExperience().isEmpty()) {
            y = drawTechSection(g2, "WORK EXPERIENCE / INTERNSHIPS", profile.getExperience(), margin, y, contentWidth);
        }

        // 5. Education
        y = drawTechEducation(g2, profile, margin, y, contentWidth);

        // 6. Certifications
        if (!profile.getCertifications().isEmpty()) {
            y = drawTechSection(g2, "CERTIFICATIONS & LICENSES", profile.getCertifications(), margin, y, contentWidth);
        }

        // 7. Achievements & Competitions
        if (!profile.getAchievements().isEmpty()) {
            y = drawTechSection(g2, "HONORS & COMPETITIVE PROGRAMMING", profile.getAchievements(), margin, y, contentWidth);
        }

        // 8. Languages
        if (!profile.getLanguages().isEmpty()) {
            drawTechSection(g2, "LANGUAGES", profile.getLanguages(), margin, y, contentWidth);
        }
    }

    private int drawTechEducation(Graphics2D g2, Profile profile, int x, int y, int width) {
        y = drawTechHeader(g2, "EDUCATION & ACADEMICS", x, y, width);

        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.setColor(getTextColor());
        g2.drawString(profile.getDegreeBranch(), x + 4, y);

        String yearStr = "[" + profile.getGraduationYear() + "]";
        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.setColor(getAccentColor());
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(yearStr, x + width - fm.stringWidth(yearStr), y);
        y += 16;

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(new Color(71, 85, 105));
        g2.drawString(profile.getCollegeName(), x + 4, y);

        String cgpaStr = "CGPA: " + profile.getCgpaPercentage();
        g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
        fm = g2.getFontMetrics();
        g2.drawString(cgpaStr, x + width - fm.stringWidth(cgpaStr), y);
        y += 20;

        return y;
    }

    private int drawTechHeader(Graphics2D g2, String title, int x, int y, int width) {
        g2.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2.setColor(getAccentColor());
        g2.drawString("// " + title, x, y);
        y += 4;

        g2.setColor(new Color(226, 232, 240));
        g2.fillRect(x, y, width, 1);
        return y + 14;
    }

    private int drawTechSection(Graphics2D g2, String title, String content, int x, int y, int width) {
        if (content == null || content.trim().isEmpty()) return y;
        y = drawTechHeader(g2, title, x, y, width);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(getTextColor());
        y = drawWrappedText(g2, content, x + 4, y, width - 8, 16);
        return y + 12;
    }
}
