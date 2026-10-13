package com.pms.template;

import com.pms.model.Profile;

import java.awt.*;
import java.util.List;

/**
 * ProfessionalTemplate
 * Classic corporate executive aesthetic with navy banner and structured section blocks.
 * 
 * Demonstrates:
 * - Subclassing and Inheritance
 * - Method Overriding (Polymorphism)
 */
public class ProfessionalTemplate extends ResumeTemplate {

    public ProfessionalTemplate() {
        super(TemplateType.PROFESSIONAL);
    }

    @Override
    public Color getPrimaryColor() {
        return new Color(30, 58, 138); // Navy Blue
    }

    @Override
    public Color getSecondaryColor() {
        return new Color(51, 65, 85); // Slate Gray
    }

    @Override
    public Color getAccentColor() {
        return new Color(37, 99, 235); // Royal Blue Accent
    }

    @Override
    public Color getBackgroundColor() {
        return Color.WHITE;
    }

    @Override
    public Color getTextColor() {
        return new Color(30, 41, 59); // Charcoal Text
    }

    @Override
    public Color getLightBackground() {
        return new Color(241, 245, 249); // Soft Ice Gray
    }

    @Override
    public boolean isTwoColumn() {
        return false;
    }

    @Override
    public void render(Graphics2D g2, Profile profile, int width, int height) {
        // Antialiasing hints
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background
        g2.setColor(getBackgroundColor());
        g2.fillRect(0, 0, width, height);

        // --- Header Navy Banner ---
        int headerHeight = 110;
        g2.setColor(getPrimaryColor());
        g2.fillRect(0, 0, width, headerHeight);

        // Accent gold/blue bottom border line on header
        g2.setColor(getAccentColor());
        g2.fillRect(0, headerHeight - 4, width, 4);

        // Candidate Full Name
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 26));
        g2.drawString(profile.getFullName().toUpperCase(), 36, 42);

        // Subtitle / Degree & Branch
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2.setColor(new Color(224, 231, 255));
        g2.drawString(profile.getDegreeBranch(), 36, 66);

        // Contact Info Line
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(new Color(241, 245, 249));
        String contact = "✉ " + profile.getEmail() + "   |   ☎ " + profile.getPhone() + "   |   📍 " + profile.getAddress();
        g2.drawString(contact, 36, 88);

        // --- Body Content Flow ---
        int leftMargin = 36;
        int rightMargin = 36;
        int contentWidth = width - leftMargin - rightMargin;
        int y = headerHeight + 28;

        // 1. Career Objective
        y = drawSection(g2, "CAREER OBJECTIVE", profile.getCareerObjective(), leftMargin, y, contentWidth);

        // 2. Education Section
        y = drawEducationSection(g2, profile, leftMargin, y, contentWidth);

        // 3. Technical Skills
        if (!profile.getTechnicalSkills().isEmpty()) {
            y = drawSection(g2, "TECHNICAL SKILLS", profile.getTechnicalSkills(), leftMargin, y, contentWidth);
        }

        // 4. Projects
        if (!profile.getProjects().isEmpty()) {
            y = drawSection(g2, "PROJECTS", profile.getProjects(), leftMargin, y, contentWidth);
        }

        // 5. Experience / Internships
        if (!profile.getExperience().isEmpty()) {
            y = drawSection(g2, "EXPERIENCE & INTERNSHIPS", profile.getExperience(), leftMargin, y, contentWidth);
        }

        // 6. Certifications & Achievements
        String certAch = "";
        if (!profile.getCertifications().isEmpty()) {
            certAch += "Certifications:\n" + profile.getCertifications();
        }
        if (!profile.getAchievements().isEmpty()) {
            if (!certAch.isEmpty()) certAch += "\n\n";
            certAch += "Achievements:\n" + profile.getAchievements();
        }
        if (!certAch.isEmpty()) {
            y = drawSection(g2, "CERTIFICATIONS & ACHIEVEMENTS", certAch, leftMargin, y, contentWidth);
        }

        // 7. Languages
        if (!profile.getLanguages().isEmpty()) {
            drawSection(g2, "LANGUAGES", profile.getLanguages(), leftMargin, y, contentWidth);
        }
    }

    private int drawEducationSection(Graphics2D g2, Profile profile, int x, int y, int width) {
        y = drawSectionHeading(g2, "EDUCATION", x, y, width);

        // College Name & Year
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2.setColor(getTextColor());
        g2.drawString(profile.getCollegeName(), x + 6, y);

        String yearStr = "Graduation: " + profile.getGraduationYear();
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(yearStr, x + width - fm.stringWidth(yearStr) - 6, y);
        y += 18;

        // Degree & CGPA
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.setColor(getSecondaryColor());
        g2.drawString(profile.getDegreeBranch(), x + 6, y);

        String cgpaStr = "CGPA/Score: " + profile.getCgpaPercentage();
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.setColor(getPrimaryColor());
        fm = g2.getFontMetrics();
        g2.drawString(cgpaStr, x + width - fm.stringWidth(cgpaStr) - 6, y);
        y += 24;

        return y;
    }

    private int drawSectionHeading(Graphics2D g2, String title, int x, int y, int width) {
        // Vertical decorative bar
        g2.setColor(getPrimaryColor());
        g2.fillRect(x, y - 13, 4, 16);

        // Heading title
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2.drawString(title, x + 10, y);

        // Thin horizontal separator line
        g2.setColor(new Color(226, 232, 240));
        g2.fillRect(x + 10 + g2.getFontMetrics().stringWidth(title) + 12, y - 4,
                width - (g2.getFontMetrics().stringWidth(title) + 22), 1);

        return y + 16;
    }

    private int drawSection(Graphics2D g2, String title, String content, int x, int y, int width) {
        if (content == null || content.trim().isEmpty()) return y;
        y = drawSectionHeading(g2, title, x, y, width);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(getTextColor());
        y = drawWrappedText(g2, content, x + 6, y, width - 12, 16);
        return y + 12;
    }
}
