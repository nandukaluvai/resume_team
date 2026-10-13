package com.pms.template;

import com.pms.model.Profile;

import java.awt.*;

/**
 * CreativeTemplate
 * Vibrant, modern aesthetic with rich indigo/violet styling and rounded section badges.
 * 
 * Demonstrates:
 * - Expressive styling variations in polymorphic hierarchy
 * - Custom gradient and geometric rendering in Java 2D
 */
public class CreativeTemplate extends ResumeTemplate {

    public CreativeTemplate() {
        super(TemplateType.CREATIVE);
    }

    @Override
    public Color getPrimaryColor() {
        return new Color(79, 70, 229); // Indigo
    }

    @Override
    public Color getSecondaryColor() {
        return new Color(124, 58, 237); // Purple / Violet
    }

    @Override
    public Color getAccentColor() {
        return new Color(192, 132, 252); // Soft Violet
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
        return new Color(245, 243, 255); // Lavender Ice
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

        // --- Creative Header Banner with Gradient ---
        int headerHeight = 96;
        GradientPaint gradient = new GradientPaint(0, 0, getPrimaryColor(), width, 0, getSecondaryColor());
        g2.setPaint(gradient);
        g2.fillRoundRect(margin, 20, contentWidth, headerHeight, 14, 14);

        // Candidate Name
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 24));
        g2.drawString(profile.getFullName(), margin + 20, 52);

        // Candidate Degree / Track
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.setColor(new Color(238, 242, 255));
        g2.drawString(profile.getDegreeBranch(), margin + 20, 72);

        // Contact details line inside banner
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.setColor(new Color(224, 231, 255));
        String contact = "✉ " + profile.getEmail() + "   •   ☎ " + profile.getPhone() + "   •   📍 " + profile.getAddress();
        g2.drawString(contact, margin + 20, 94);

        int y = headerHeight + 42;

        // 1. Objective
        y = drawCreativeSection(g2, "ABOUT ME / OBJECTIVE", profile.getCareerObjective(), margin, y, contentWidth);

        // 2. Education
        y = drawCreativeEducation(g2, profile, margin, y, contentWidth);

        // 3. Technical Skills
        if (!profile.getTechnicalSkills().isEmpty()) {
            y = drawCreativeSection(g2, "SKILLS & EXPERTISE", profile.getTechnicalSkills(), margin, y, contentWidth);
        }

        // 4. Projects
        if (!profile.getProjects().isEmpty()) {
            y = drawCreativeSection(g2, "FEATURED PROJECTS", profile.getProjects(), margin, y, contentWidth);
        }

        // 5. Work Experience / Internships
        if (!profile.getExperience().isEmpty()) {
            y = drawCreativeSection(g2, "EXPERIENCE & ROLES", profile.getExperience(), margin, y, contentWidth);
        }

        // 6. Certifications
        if (!profile.getCertifications().isEmpty()) {
            y = drawCreativeSection(g2, "CERTIFICATIONS", profile.getCertifications(), margin, y, contentWidth);
        }

        // 7. Achievements
        if (!profile.getAchievements().isEmpty()) {
            y = drawCreativeSection(g2, "ACHIEVEMENTS & AWARDS", profile.getAchievements(), margin, y, contentWidth);
        }

        // 8. Languages
        if (!profile.getLanguages().isEmpty()) {
            drawCreativeSection(g2, "LANGUAGES", profile.getLanguages(), margin, y, contentWidth);
        }
    }

    private int drawCreativeEducation(Graphics2D g2, Profile profile, int x, int y, int width) {
        y = drawCreativeHeader(g2, "EDUCATION & QUALIFICATIONS", x, y, width);

        g2.setColor(getLightBackground());
        g2.fillRoundRect(x, y, width, 48, 8, 8);
        g2.setColor(new Color(221, 214, 254));
        g2.drawRoundRect(x, y, width, 48, 8, 8);

        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.setColor(getTextColor());
        g2.drawString(profile.getDegreeBranch(), x + 14, y + 20);

        String year = "Graduation: " + profile.getGraduationYear();
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(year, x + width - fm.stringWidth(year) - 14, y + 20);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(new Color(75, 85, 99));
        g2.drawString(profile.getCollegeName(), x + 14, y + 38);

        String score = "Score: " + profile.getCgpaPercentage();
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.setColor(getPrimaryColor());
        fm = g2.getFontMetrics();
        g2.drawString(score, x + width - fm.stringWidth(score) - 14, y + 38);

        return y + 60;
    }

    private int drawCreativeHeader(Graphics2D g2, String title, int x, int y, int width) {
        // Pill Badge
        int badgeWidth = g2.getFontMetrics().stringWidth(title) + 24;
        g2.setColor(getLightBackground());
        g2.fillRoundRect(x, y - 13, badgeWidth, 20, 10, 10);
        g2.setColor(new Color(221, 214, 254));
        g2.drawRoundRect(x, y - 13, badgeWidth, 20, 10, 10);

        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.setColor(getPrimaryColor());
        g2.drawString(title, x + 12, y + 2);

        // Thin violet accent rule
        g2.setColor(new Color(238, 242, 255));
        g2.fillRect(x + badgeWidth + 10, y - 2, width - badgeWidth - 10, 1);

        return y + 18;
    }

    private int drawCreativeSection(Graphics2D g2, String title, String content, int x, int y, int width) {
        if (content == null || content.trim().isEmpty()) return y;
        y = drawCreativeHeader(g2, title, x, y, width);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(getTextColor());
        y = drawWrappedText(g2, content, x + 4, y, width - 8, 16);
        return y + 12;
    }
}
