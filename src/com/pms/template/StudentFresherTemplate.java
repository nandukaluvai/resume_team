package com.pms.template;

import com.pms.model.Profile;

import java.awt.*;

/**
 * StudentFresherTemplate
 * Specially designed for university students and entry-level graduates.
 * Emphasizes academic credentials, coursework projects, and technical skills with emerald accents.
 * 
 * Demonstrates:
 * - Domain-specific customization in OOP
 * - Custom card drawing in Java 2D
 */
public class StudentFresherTemplate extends ResumeTemplate {

    public StudentFresherTemplate() {
        super(TemplateType.STUDENT_FRESHER);
    }

    @Override
    public Color getPrimaryColor() {
        return new Color(13, 148, 136); // Teal / Emerald
    }

    @Override
    public Color getSecondaryColor() {
        return new Color(19, 78, 74); // Deep Teal
    }

    @Override
    public Color getAccentColor() {
        return new Color(20, 184, 166); // Light Emerald
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
        return new Color(240, 253, 250); // Mint Ice
    }

    @Override
    public boolean isTwoColumn() {
        return false;
    }

    @Override
    public void render(Graphics2D g2, Profile profile, int width, int height) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background
        g2.setColor(getBackgroundColor());
        g2.fillRect(0, 0, width, height);

        int margin = 36;
        int contentWidth = width - (2 * margin);
        int y = 35;

        // --- Top Decorative Accent Line ---
        g2.setColor(getPrimaryColor());
        g2.fillRect(0, 0, width, 6);

        // Candidate Name
        g2.setFont(new Font("SansSerif", Font.BOLD, 26));
        g2.setColor(getSecondaryColor());
        g2.drawString(profile.getFullName(), margin, y);
        y += 20;

        // Subtitle / Track
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2.setColor(getPrimaryColor());
        g2.drawString(profile.getDegreeBranch(), margin, y);
        y += 18;

        // Contact info pills
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.setColor(new Color(71, 85, 105));
        String contact = "✉ " + profile.getEmail() + "   |   ☎ " + profile.getPhone() + "   |   📍 " + profile.getAddress();
        g2.drawString(contact, margin, y);
        y += 24;

        // --- Highlighted Education Card ---
        int cardHeight = 58;
        g2.setColor(getLightBackground());
        g2.fillRoundRect(margin, y, contentWidth, cardHeight, 10, 10);
        g2.setColor(new Color(204, 251, 241));
        g2.drawRoundRect(margin, y, contentWidth, cardHeight, 10, 10);

        // Inside Education Card
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.setColor(getSecondaryColor());
        g2.drawString("🎓 " + profile.getCollegeName(), margin + 14, y + 22);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(new Color(51, 65, 85));
        g2.drawString(profile.getDegreeBranch(), margin + 14, y + 42);

        // Right side badge inside card: CGPA & Grad Year
        String scoreBadge = "CGPA: " + profile.getCgpaPercentage() + "   •   Class of " + profile.getGraduationYear();
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.setColor(getPrimaryColor());
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(scoreBadge, margin + contentWidth - fm.stringWidth(scoreBadge) - 14, y + 32);

        y += cardHeight + 20;

        // 1. Objective
        y = drawFresherSection(g2, "CAREER OBJECTIVE", profile.getCareerObjective(), margin, y, contentWidth);

        // 2. Technical Skills
        if (!profile.getTechnicalSkills().isEmpty()) {
            y = drawFresherSection(g2, "CORE TECHNICAL SKILLS", profile.getTechnicalSkills(), margin, y, contentWidth);
        }

        // 3. Academic & Personal Projects
        if (!profile.getProjects().isEmpty()) {
            y = drawFresherSection(g2, "ACADEMIC & SOFTWARE PROJECTS", profile.getProjects(), margin, y, contentWidth);
        }

        // 4. Internships & Practical Training
        if (!profile.getExperience().isEmpty()) {
            y = drawFresherSection(g2, "INTERNSHIPS & PRACTICAL EXPERIENCE", profile.getExperience(), margin, y, contentWidth);
        }

        // 5. Certifications & Badges
        if (!profile.getCertifications().isEmpty()) {
            y = drawFresherSection(g2, "CERTIFICATIONS & ONLINE COURSES", profile.getCertifications(), margin, y, contentWidth);
        }

        // 6. Extracurricular Achievements
        if (!profile.getAchievements().isEmpty()) {
            y = drawFresherSection(g2, "AWARDS & ACHIEVEMENTS", profile.getAchievements(), margin, y, contentWidth);
        }

        // 7. Languages
        if (!profile.getLanguages().isEmpty()) {
            drawFresherSection(g2, "LANGUAGES KNOWN", profile.getLanguages(), margin, y, contentWidth);
        }
    }

    private int drawFresherSection(Graphics2D g2, String title, String content, int x, int y, int width) {
        if (content == null || content.trim().isEmpty()) return y;

        // Section badge header
        g2.setColor(getPrimaryColor());
        g2.fillRoundRect(x, y - 12, 14, 14, 4, 4);

        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString(title, x + 22, y);

        g2.setColor(new Color(204, 251, 241));
        g2.fillRect(x + 22 + g2.getFontMetrics().stringWidth(title) + 12, y - 4,
                width - (g2.getFontMetrics().stringWidth(title) + 34), 2);

        y += 16;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(getTextColor());
        y = drawWrappedText(g2, content, x + 4, y, width - 8, 16);
        return y + 12;
    }
}
