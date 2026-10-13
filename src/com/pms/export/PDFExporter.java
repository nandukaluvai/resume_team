package com.pms.export;

import com.pms.model.Profile;
import com.pms.template.ResumeTemplate;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PDFExporter Service
 * Exports user profiles into professional PDF resumes using Apache PDFBox.
 * Matches styling and colors with the selected ResumeTemplate.
 * 
 * Demonstrates:
 * - Integration with third-party libraries (Apache PDFBox)
 * - Vector graphics and document generation
 * - Pagination and text wrapping algorithms
 * - Exception handling during document I/O
 */
public class PDFExporter {

    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();   // 595.27 points
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight(); // 841.89 points
    private static final float MARGIN = 38f;
    private static final float BOTTOM_MARGIN = 45f;

    private PDDocument document;
    private PDPage currentPage;
    private PDPageContentStream contentStream;
    private float currentY;
    private int pageNumber;
    private final Profile profile;
    private final ResumeTemplate template;

    public PDFExporter(Profile profile, ResumeTemplate template) {
        this.profile = profile;
        this.template = template;
    }

    /**
     * Generates and writes the PDF file to the destination path.
     */
    public void exportToFile(File destinationFile) throws IOException {
        document = new PDDocument();
        pageNumber = 0;

        try {
            addNewPage();

            // Render Header based on template style
            renderHeader();

            // Render Sections
            renderAllSections();

            // Close last stream
            if (contentStream != null) {
                contentStream.close();
                contentStream = null;
            }

            // Save document
            document.save(destinationFile);
        } finally {
            if (contentStream != null) {
                try {
                    contentStream.close();
                } catch (Exception ignored) {
                }
            }
            if (document != null) {
                document.close();
            }
        }
    }

    /**
     * Adds a new page and initializes its content stream and Y coordinate.
     */
    private void addNewPage() throws IOException {
        if (contentStream != null) {
            contentStream.close();
        }

        currentPage = new PDPage(PDRectangle.A4);
        document.addPage(currentPage);
        pageNumber++;
        contentStream = new PDPageContentStream(document, currentPage);
        currentY = PAGE_HEIGHT - MARGIN;

        // On subsequent pages, print candidate continuation header
        if (pageNumber > 1) {
            contentStream.setFont(PDType1Font.HELVETICA, 8);
            setNonStrokingColor(new Color(148, 163, 184));
            drawText(cleanText(profile.getFullName()) + " - Resume (Page " + pageNumber + ")", MARGIN, currentY);
            currentY -= 20;

            // Thin divider line
            setStrokingColor(new Color(226, 232, 240));
            contentStream.setLineWidth(0.5f);
            contentStream.moveTo(MARGIN, currentY + 10);
            contentStream.lineTo(PAGE_WIDTH - MARGIN, currentY + 10);
            contentStream.stroke();
        }
    }

    /**
     * Ensures there is enough space on the current page, or creates a new page.
     */
    private void ensureSpace(float requiredHeight) throws IOException {
        if (currentY - requiredHeight < BOTTOM_MARGIN) {
            addNewPage();
        }
    }

    /**
     * Renders header block styled per template.
     */
    private void renderHeader() throws IOException {
        Color primary = template.getPrimaryColor();
        Color accent = template.getAccentColor();

        switch (template.getType()) {
            case PROFESSIONAL:
                // Navy header bar across top
                float headerH = 88f;
                setNonStrokingColor(primary);
                contentStream.addRect(0, PAGE_HEIGHT - headerH, PAGE_WIDTH, headerH);
                contentStream.fill();

                // Accent stripe
                setNonStrokingColor(accent);
                contentStream.addRect(0, PAGE_HEIGHT - headerH - 3, PAGE_WIDTH, 3);
                contentStream.fill();

                // White text
                setNonStrokingColor(Color.WHITE);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                drawText(cleanText(profile.getFullName()).toUpperCase(), MARGIN, PAGE_HEIGHT - 32);

                contentStream.setFont(PDType1Font.HELVETICA, 11);
                setNonStrokingColor(new Color(224, 231, 255));
                drawText(cleanText(profile.getDegreeBranch()), MARGIN, PAGE_HEIGHT - 48);

                contentStream.setFont(PDType1Font.HELVETICA, 9);
                setNonStrokingColor(new Color(241, 245, 249));
                String contact = "Email: " + cleanText(profile.getEmail()) +
                        "  |  Phone: " + cleanText(profile.getPhone()) +
                        "  |  Location: " + cleanText(profile.getAddress());
                drawText(contact, MARGIN, PAGE_HEIGHT - 66);

                currentY = PAGE_HEIGHT - headerH - 24;
                break;

            case SIMPLE:
                // Centered ATS Header
                String name = cleanText(profile.getFullName()).toUpperCase();
                float nameW = getTextWidth(PDType1Font.HELVETICA_BOLD, 20, name);
                setNonStrokingColor(primary);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                drawText(name, (PAGE_WIDTH - nameW) / 2, currentY);
                currentY -= 16;

                String simpleContact = cleanText(profile.getEmail()) + "  *  " +
                        cleanText(profile.getPhone()) + "  *  " +
                        cleanText(profile.getAddress());
                float contactW = getTextWidth(PDType1Font.HELVETICA, 9, simpleContact);
                setNonStrokingColor(template.getSecondaryColor());
                contentStream.setFont(PDType1Font.HELVETICA, 9);
                drawText(simpleContact, (PAGE_WIDTH - contactW) / 2, currentY);
                currentY -= 14;

                // Full line
                setStrokingColor(new Color(209, 213, 219));
                contentStream.setLineWidth(1f);
                contentStream.moveTo(MARGIN, currentY);
                contentStream.lineTo(PAGE_WIDTH - MARGIN, currentY);
                contentStream.stroke();
                currentY -= 16;
                break;

            case STUDENT_FRESHER:
                // Emerald Top Bar
                setNonStrokingColor(primary);
                contentStream.addRect(0, PAGE_HEIGHT - 5, PAGE_WIDTH, 5);
                contentStream.fill();

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                setNonStrokingColor(template.getSecondaryColor());
                drawText(cleanText(profile.getFullName()), MARGIN, currentY);
                currentY -= 16;

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                setNonStrokingColor(primary);
                drawText(cleanText(profile.getDegreeBranch()), MARGIN, currentY);
                currentY -= 14;

                contentStream.setFont(PDType1Font.HELVETICA, 9);
                setNonStrokingColor(new Color(71, 85, 105));
                String fContact = "Email: " + cleanText(profile.getEmail()) +
                        "  |  Phone: " + cleanText(profile.getPhone()) +
                        "  |  Location: " + cleanText(profile.getAddress());
                drawText(fContact, MARGIN, currentY);
                currentY -= 20;

                // Highlighted Academic Summary Box
                float boxH = 40f;
                setNonStrokingColor(new Color(240, 253, 250));
                contentStream.addRect(MARGIN, currentY - boxH + 10, PAGE_WIDTH - (2 * MARGIN), boxH);
                contentStream.fill();
                setStrokingColor(new Color(204, 251, 241));
                contentStream.setLineWidth(1f);
                contentStream.addRect(MARGIN, currentY - boxH + 10, PAGE_WIDTH - (2 * MARGIN), boxH);
                contentStream.stroke();

                setNonStrokingColor(template.getSecondaryColor());
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                drawText(cleanText(profile.getCollegeName()), MARGIN + 10, currentY - 6);

                setNonStrokingColor(new Color(71, 85, 105));
                contentStream.setFont(PDType1Font.HELVETICA, 9);
                drawText(cleanText(profile.getDegreeBranch()), MARGIN + 10, currentY - 20);

                String scorePill = "CGPA: " + cleanText(profile.getCgpaPercentage()) + "  |  Class of " + cleanText(profile.getGraduationYear());
                setNonStrokingColor(primary);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 9);
                float pillW = getTextWidth(PDType1Font.HELVETICA_BOLD, 9, scorePill);
                drawText(scorePill, PAGE_WIDTH - MARGIN - pillW - 10, currentY - 12);

                currentY -= (boxH + 10);
                break;

            case TECHNICAL:
                // Developer Header
                setNonStrokingColor(primary);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                drawText(cleanText(profile.getFullName()), MARGIN, currentY);

                String techTag = "<" + cleanText(profile.getDegreeBranch()).toLowerCase().replace(" ", "_") + " />";
                setNonStrokingColor(accent);
                contentStream.setFont(PDType1Font.COURIER_BOLD, 9);
                float tagW = getTextWidth(PDType1Font.COURIER_BOLD, 9, techTag);
                drawText(techTag, PAGE_WIDTH - MARGIN - tagW, currentY);
                currentY -= 14;

                contentStream.setFont(PDType1Font.COURIER, 8);
                setNonStrokingColor(new Color(71, 85, 105));
                String tContact = "contact: " + cleanText(profile.getEmail()) +
                        " | " + cleanText(profile.getPhone()) +
                        " | " + cleanText(profile.getAddress());
                drawText(tContact, MARGIN, currentY);
                currentY -= 12;

                setStrokingColor(new Color(226, 232, 240));
                contentStream.setLineWidth(1f);
                contentStream.moveTo(MARGIN, currentY);
                contentStream.lineTo(PAGE_WIDTH - MARGIN, currentY);
                contentStream.stroke();
                setNonStrokingColor(accent);
                contentStream.addRect(MARGIN, currentY - 1, 50, 2);
                contentStream.fill();
                currentY -= 16;
                break;

            case CREATIVE:
                // Indigo/Violet Banner
                float cBannerH = 75f;
                setNonStrokingColor(primary);
                contentStream.addRect(MARGIN, PAGE_HEIGHT - MARGIN - cBannerH, PAGE_WIDTH - (2 * MARGIN), cBannerH);
                contentStream.fill();

                setNonStrokingColor(Color.WHITE);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                drawText(cleanText(profile.getFullName()), MARGIN + 14, PAGE_HEIGHT - MARGIN - 26);

                contentStream.setFont(PDType1Font.HELVETICA, 10);
                setNonStrokingColor(new Color(238, 242, 255));
                drawText(cleanText(profile.getDegreeBranch()), MARGIN + 14, PAGE_HEIGHT - MARGIN - 42);

                contentStream.setFont(PDType1Font.HELVETICA, 8);
                setNonStrokingColor(new Color(224, 231, 255));
                String crContact = cleanText(profile.getEmail()) + "  *  " + cleanText(profile.getPhone()) + "  *  " + cleanText(profile.getAddress());
                drawText(crContact, MARGIN + 14, PAGE_HEIGHT - MARGIN - 58);

                currentY = PAGE_HEIGHT - MARGIN - cBannerH - 20;
                break;

            case MODERN:
            default:
                // Modern Clean Header
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 22);
                setNonStrokingColor(primary);
                drawText(cleanText(profile.getFullName()), MARGIN, currentY);
                currentY -= 16;

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                setNonStrokingColor(accent);
                drawText(cleanText(profile.getDegreeBranch()).toUpperCase(), MARGIN, currentY);
                currentY -= 14;

                contentStream.setFont(PDType1Font.HELVETICA, 9);
                setNonStrokingColor(new Color(100, 116, 139));
                String mContact = cleanText(profile.getEmail()) + "   |   " +
                        cleanText(profile.getPhone()) + "   |   " +
                        cleanText(profile.getAddress());
                drawText(mContact, MARGIN, currentY);
                currentY -= 14;

                setStrokingColor(new Color(226, 232, 240));
                contentStream.setLineWidth(1f);
                contentStream.moveTo(MARGIN, currentY);
                contentStream.lineTo(PAGE_WIDTH - MARGIN, currentY);
                contentStream.stroke();
                currentY -= 16;
                break;
        }
    }

    /**
     * Renders all resume content sections sequentially with automatic pagination.
     */
    private void renderAllSections() throws IOException {
        float contentWidth = PAGE_WIDTH - (2 * MARGIN);

        // 1. Career Objective
        renderSection("CAREER OBJECTIVE", profile.getCareerObjective(), contentWidth);

        // 2. Education Details (Special layout with Degree, College, Year, and CGPA)
        renderEducationSection(contentWidth);

        // 3. Technical Skills
        if (!profile.getTechnicalSkills().isEmpty()) {
            renderSection("TECHNICAL SKILLS", profile.getTechnicalSkills(), contentWidth);
        }

        // 4. Projects
        if (!profile.getProjects().isEmpty()) {
            renderSection("KEY PROJECTS", profile.getProjects(), contentWidth);
        }

        // 5. Work Experience / Internships
        if (!profile.getExperience().isEmpty()) {
            renderSection("EXPERIENCE & INTERNSHIPS", profile.getExperience(), contentWidth);
        }

        // 6. Certifications
        if (!profile.getCertifications().isEmpty()) {
            renderSection("CERTIFICATIONS", profile.getCertifications(), contentWidth);
        }

        // 7. Achievements
        if (!profile.getAchievements().isEmpty()) {
            renderSection("ACHIEVEMENTS & AWARDS", profile.getAchievements(), contentWidth);
        }

        // 8. Languages
        if (!profile.getLanguages().isEmpty()) {
            renderSection("LANGUAGES KNOWN", profile.getLanguages(), contentWidth);
        }
    }

    /**
     * Renders education section with college, degree, graduation year, and score.
     */
    private void renderEducationSection(float width) throws IOException {
        ensureSpace(50f);
        drawSectionHeader("EDUCATION", width);

        // Degree and Year
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        setNonStrokingColor(template.getTextColor());
        drawText(cleanText(profile.getDegreeBranch()), MARGIN, currentY);

        String yearStr = "Graduation: " + cleanText(profile.getGraduationYear());
        float yearW = getTextWidth(PDType1Font.HELVETICA, 9, yearStr);
        contentStream.setFont(PDType1Font.HELVETICA, 9);
        drawText(yearStr, PAGE_WIDTH - MARGIN - yearW, currentY);
        currentY -= 14;

        // College Name and Score
        contentStream.setFont(PDType1Font.HELVETICA, 9);
        setNonStrokingColor(new Color(71, 85, 105));
        drawText(cleanText(profile.getCollegeName()), MARGIN, currentY);

        String scoreStr = "CGPA / Percentage: " + cleanText(profile.getCgpaPercentage());
        float scoreW = getTextWidth(PDType1Font.HELVETICA_BOLD, 9, scoreStr);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 9);
        setNonStrokingColor(template.getPrimaryColor());
        drawText(scoreStr, PAGE_WIDTH - MARGIN - scoreW, currentY);
        currentY -= 18;
    }

    /**
     * Renders a generic section with title and multi-line body text.
     */
    private void renderSection(String title, String content, float width) throws IOException {
        if (content == null || content.trim().isEmpty()) return;

        List<String> wrappedLines = wrapText(content, width, PDType1Font.HELVETICA, 9);
        float sectionHeight = 24f + (wrappedLines.size() * 13f);

        ensureSpace(Math.min(sectionHeight, 60f));
        drawSectionHeader(title, width);

        contentStream.setFont(PDType1Font.HELVETICA, 9);
        setNonStrokingColor(template.getTextColor());

        for (String line : wrappedLines) {
            ensureSpace(14f);
            drawText(cleanText(line), MARGIN, currentY);
            currentY -= 13f;
        }
        currentY -= 8f;
    }

    /**
     * Draws formatted section header with accent rules matching the template.
     */
    private void drawSectionHeader(String title, float width) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        setNonStrokingColor(template.getPrimaryColor());
        drawText(title, MARGIN, currentY);

        float titleW = getTextWidth(PDType1Font.HELVETICA_BOLD, 10, title);
        setStrokingColor(new Color(226, 232, 240));
        contentStream.setLineWidth(0.8f);
        contentStream.moveTo(MARGIN + titleW + 8, currentY + 3);
        contentStream.lineTo(PAGE_WIDTH - MARGIN, currentY + 3);
        contentStream.stroke();

        currentY -= 14f;
    }

    /**
     * Helper to draw text at an exact (x, y) location.
     */
    private void drawText(String text, float x, float y) throws IOException {
        if (text == null || text.isEmpty()) return;
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(cleanText(text));
        contentStream.endText();
    }

    /**
     * Calculates width of a string in points for a given font and size.
     */
    private float getTextWidth(PDFont font, float fontSize, String text) {
        try {
            return font.getStringWidth(cleanText(text)) / 1000f * fontSize;
        } catch (Exception e) {
            return text.length() * fontSize * 0.5f;
        }
    }

    /**
     * Splits text into wrapped lines for the PDF document.
     */
    private List<String> wrapText(String text, float maxWidth, PDFont font, float fontSize) {
        List<String> result = new ArrayList<>();
        if (text == null) return result;

        String[] paragraphs = text.split("\n");
        for (String p : paragraphs) {
            String trimmed = p.trim();
            if (trimmed.isEmpty()) {
                result.add("");
                continue;
            }

            String[] words = trimmed.split("\\s+");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                String candidate = currentLine.length() == 0 ? word : currentLine + " " + word;
                if (getTextWidth(font, fontSize, candidate) <= maxWidth) {
                    currentLine = new StringBuilder(candidate);
                } else {
                    if (currentLine.length() > 0) {
                        result.add(currentLine.toString());
                    }
                    currentLine = new StringBuilder(word);
                }
            }
            if (currentLine.length() > 0) {
                result.add(currentLine.toString());
            }
        }
        return result;
    }

    /**
     * Replaces non-ASCII unicode symbols with standard ASCII equivalents
     * to ensure compatibility with standard PDF fonts without embedding.
     */
    private String cleanText(String input) {
        if (input == null) return "";
        return input.replace("•", "*")
                .replace("✉", "Email:")
                .replace("☎", "Tel:")
                .replace("📍", "Loc:")
                .replace("🎓", "[Edu]")
                .replace("—", "-")
                .replace("–", "-")
                .replace("“", "\"")
                .replace("”", "\"")
                .replace("‘", "'")
                .replace("’", "'")
                .replaceAll("[^\\x00-\\x7F]", " ");
    }

    private void setNonStrokingColor(Color c) throws IOException {
        contentStream.setNonStrokingColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
    }

    private void setStrokingColor(Color c) throws IOException {
        contentStream.setStrokingColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
    }
}
