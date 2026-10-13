package com.pms.test;

import com.pms.export.PDFExporter;
import com.pms.model.Profile;
import com.pms.service.DataStorageException;
import com.pms.service.ProfileManager;
import com.pms.service.ValidationException;
import com.pms.template.ResumeTemplate;
import com.pms.template.TemplateType;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * SystemTestRunner
 * Comprehensive automated verification test suite for Profile Management System.
 * Tests Model, Service, Persistence, Corrupted Data Recovery,
 * all 6 Resume Graphics Templates, and PDFBox Export.
 */
public class SystemTestRunner {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  RUNNING SYSTEM TEST SUITE: PROFILE MANAGEMENT  ");
        System.out.println("=================================================");

        testModelCreationAndEncapsulation();
        testValidationRules();
        testPersistenceCRUDAndSearch();
        testCorruptedFileRecovery();
        testAllSixResumeGraphicsTemplates();
        testPDFBoxExportAllTemplates();

        System.out.println("=================================================");
        System.out.printf("  TEST RESULTS: %d PASSED, %d FAILED%n", testsPassed, testsFailed);
        System.out.println("=================================================");

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    private static void assertTrue(boolean condition, String testName) {
        if (condition) {
            System.out.println(" [PASS] " + testName);
            testsPassed++;
        } else {
            System.err.println(" [FAIL] " + testName);
            testsFailed++;
        }
    }

    private static void testModelCreationAndEncapsulation() {
        System.out.println("\n--- Testing Model & Encapsulation ---");
        Profile p = ProfileManager.createSampleProfile();
        assertTrue(p.getFullName().equals("Aarav Sharma"), "Profile name getter");
        assertTrue(p.getEmail().contains("@"), "Profile email getter");
        assertTrue(p.getGraduationYear().equals("2026"), "Graduation year getter");
        assertTrue(p.getId() != null && !p.getId().isEmpty(), "UUID generation");

        p.setFullName("Rohan Verma");
        assertTrue(p.getFullName().equals("Rohan Verma"), "Profile name setter");
    }

    private static void testValidationRules() {
        System.out.println("\n--- Testing Input Validation ---");
        ProfileManager pm = new ProfileManager();

        // Valid Profile
        Profile valid = ProfileManager.createSampleProfile();
        try {
            pm.validateProfile(valid);
            assertTrue(true, "Valid profile passes validation");
        } catch (ValidationException e) {
            assertTrue(false, "Valid profile failed: " + e.getMessage());
        }

        // Invalid Email
        Profile invalidEmail = ProfileManager.createSampleProfile();
        invalidEmail.setEmail("not-an-email");
        try {
            pm.validateProfile(invalidEmail);
            assertTrue(false, "Invalid email should fail");
        } catch (ValidationException e) {
            assertTrue(true, "Invalid email correctly rejected: " + e.getMessage());
        }

        // Invalid Year
        Profile invalidYear = ProfileManager.createSampleProfile();
        invalidYear.setGraduationYear("abcd");
        try {
            pm.validateProfile(invalidYear);
            assertTrue(false, "Invalid year should fail");
        } catch (ValidationException e) {
            assertTrue(true, "Invalid year correctly rejected: " + e.getMessage());
        }

        // Empty Name
        Profile emptyName = ProfileManager.createSampleProfile();
        emptyName.setFullName(" ");
        try {
            pm.validateProfile(emptyName);
            assertTrue(false, "Empty name should fail");
        } catch (ValidationException e) {
            assertTrue(true, "Empty name correctly rejected: " + e.getMessage());
        }
    }

    private static void testPersistenceCRUDAndSearch() {
        System.out.println("\n--- Testing Persistence, CRUD & Search ---");
        ProfileManager pm = new ProfileManager();

        // Create
        Profile p1 = ProfileManager.createSampleProfile();
        p1.setFullName("Priya Nair");
        p1.setEmail("priya.nair@test.edu");

        try {
            pm.addProfile(p1);
            assertTrue(true, "Added profile to ProfileManager");

            // Verify Search
            List<Profile> searchResults = pm.searchProfilesByName("Priya");
            assertTrue(searchResults.size() >= 1 && searchResults.get(0).getFullName().contains("Priya"), "Search profile by name substring");

            // Update
            p1.setCgpaPercentage("9.45 CGPA");
            pm.updateProfile(p1);
            Profile reloaded = pm.getProfileById(p1.getId());
            assertTrue(reloaded != null && reloaded.getCgpaPercentage().equals("9.45 CGPA"), "Profile update persisted");

            // Delete
            boolean deleted = pm.deleteProfile(p1.getId());
            assertTrue(deleted, "Deleted profile successfully");
            assertTrue(pm.getProfileById(p1.getId()) == null, "Profile no longer exists after delete");

        } catch (Exception e) {
            assertTrue(false, "CRUD test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testCorruptedFileRecovery() {
        System.out.println("\n--- Testing Corrupted File Recovery ---");
        try {
            File testDir = new File("data");
            if (!testDir.exists()) testDir.mkdirs();
            File dataFile = new File("data/profiles.dat");

            // Corrupt file by writing garbage bytes
            try (FileOutputStream fos = new FileOutputStream(dataFile)) {
                fos.write("CORRUPTED_GARBAGE_DATA_12345".getBytes());
            }

            // ProfileManager should load without throwing uncaught crash and safely recover
            ProfileManager pm = new ProfileManager();
            assertTrue(pm.getProfileCount() == 0, "Gracefully handled corrupted file and initialized clean profile list");

            // Ensure we can add new profile cleanly after recovery
            Profile fresh = ProfileManager.createSampleProfile();
            pm.addProfile(fresh);
            assertTrue(pm.getProfileCount() == 1, "Can save new profile after corrupted file recovery");

        } catch (Exception e) {
            assertTrue(false, "Corrupted file recovery failed: " + e.getMessage());
        }
    }

    private static void testAllSixResumeGraphicsTemplates() {
        System.out.println("\n--- Testing Graphics2D Rendering for All 6 Templates ---");
        Profile sample = ProfileManager.createSampleProfile();

        for (TemplateType type : TemplateType.values()) {
            try {
                ResumeTemplate template = ResumeTemplate.getTemplate(type);
                BufferedImage img = new BufferedImage(794, 1123, BufferedImage.TYPE_INT_RGB);
                java.awt.Graphics2D g2 = img.createGraphics();

                template.render(g2, sample, 794, 1123);
                g2.dispose();

                assertTrue(img.getWidth() == 794 && img.getHeight() == 1123,
                        "Rendered template on canvas: " + template.getName());
            } catch (Exception e) {
                assertTrue(false, "Template rendering failed for " + type + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void testPDFBoxExportAllTemplates() {
        System.out.println("\n--- Testing PDFBox PDF Generation for All 6 Templates ---");
        Profile sample = ProfileManager.createSampleProfile();
        File testExportDir = new File("data/test_exports");
        if (!testExportDir.exists()) testExportDir.mkdirs();

        for (TemplateType type : TemplateType.values()) {
            try {
                ResumeTemplate template = ResumeTemplate.getTemplate(type);
                File pdfFile = new File(testExportDir, "Test_Resume_" + type.name() + ".pdf");

                PDFExporter exporter = new PDFExporter(sample, template);
                exporter.exportToFile(pdfFile);

                assertTrue(pdfFile.exists() && pdfFile.length() > 1000,
                        "Generated PDF file for template: " + template.getName() + " (" + pdfFile.length() + " bytes)");

                // Load with PDFBox to verify validity
                try (PDDocument doc = PDDocument.load(pdfFile)) {
                    assertTrue(doc.getNumberOfPages() >= 1,
                            "Verified PDFBox integrity for " + type + " (Page count: " + doc.getNumberOfPages() + ")");
                }

            } catch (Exception e) {
                assertTrue(false, "PDFBox export failed for " + type + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
