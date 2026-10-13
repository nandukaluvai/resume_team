package com.pms.service;

import com.pms.model.Profile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ProfileManager Service Class
 * Handles CRUD operations, search, input validation, and local persistence.
 * 
 * Demonstrates:
 * - Collections Framework (ArrayList, List)
 * - Java File I/O and Object Serialization
 * - Safe error handling (corrupted/missing file recovery)
 * - Regular Expressions for Input Validation
 * - Singleton pattern or service encapsulation
 */
public class ProfileManager {
    private static final String DATA_DIR = "data";
    private static final String DATA_FILE = "data/profiles.dat";

    // Email & Phone Validation Patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9\\s\\-()]{7,18}$");

    private List<Profile> profiles;

    public ProfileManager() {
        this.profiles = new ArrayList<>();
        ensureDataDirectoryExists();
        loadFromFile();
    }

    /**
     * Ensures the data folder exists on disk.
     */
    private void ensureDataDirectoryExists() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Validates all required profile fields.
     * Throws ValidationException with specific error message on failure.
     */
    public void validateProfile(Profile profile) throws ValidationException {
        if (profile == null) {
            throw new ValidationException("Profile cannot be null.");
        }

        // Full Name
        if (profile.getFullName() == null || profile.getFullName().trim().length() < 2) {
            throw new ValidationException("Full Name is required (minimum 2 characters).");
        }

        // Email
        if (profile.getEmail() == null || profile.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email address is required.");
        }
        if (!EMAIL_PATTERN.matcher(profile.getEmail().trim()).matches()) {
            throw new ValidationException("Invalid email format! Example: john.doe@example.com");
        }

        // Phone
        if (profile.getPhone() == null || profile.getPhone().trim().isEmpty()) {
            throw new ValidationException("Phone number is required.");
        }
        if (!PHONE_PATTERN.matcher(profile.getPhone().trim()).matches()) {
            throw new ValidationException("Invalid phone number format! Use 7 to 15 digits (e.g. +91 9876543210).");
        }

        // Address
        if (profile.getAddress() == null || profile.getAddress().trim().isEmpty()) {
            throw new ValidationException("Location/Address is required.");
        }

        // Career Objective
        if (profile.getCareerObjective() == null || profile.getCareerObjective().trim().isEmpty()) {
            throw new ValidationException("Career Objective is required.");
        }

        // Education Details
        if (profile.getCollegeName() == null || profile.getCollegeName().trim().isEmpty()) {
            throw new ValidationException("College/University name is required.");
        }
        if (profile.getDegreeBranch() == null || profile.getDegreeBranch().trim().isEmpty()) {
            throw new ValidationException("Degree & Branch is required (e.g. B.Tech Computer Science).");
        }
        if (profile.getGraduationYear() == null || profile.getGraduationYear().trim().isEmpty()) {
            throw new ValidationException("Graduation year is required.");
        }
        try {
            int year = Integer.parseInt(profile.getGraduationYear().trim());
            if (year < 1980 || year > 2040) {
                throw new ValidationException("Graduation year must be a realistic year (1980 - 2040).");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Graduation year must be a valid 4-digit number (e.g. 2026).");
        }

        if (profile.getCgpaPercentage() == null || profile.getCgpaPercentage().trim().isEmpty()) {
            throw new ValidationException("CGPA or Percentage is required.");
        }

        // Skills
        if (profile.getTechnicalSkills() == null || profile.getTechnicalSkills().trim().isEmpty()) {
            throw new ValidationException("Please provide at least one technical skill.");
        }
    }

    /**
     * Adds a new profile to the list and persists changes.
     */
    public synchronized void addProfile(Profile profile) throws ValidationException, DataStorageException {
        validateProfile(profile);
        profile.touch();
        profiles.add(profile);
        saveToFile();
    }

    /**
     * Updates an existing profile by ID.
     */
    public synchronized void updateProfile(Profile profile) throws ValidationException, DataStorageException {
        validateProfile(profile);
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).getId().equals(profile.getId())) {
                profile.touch();
                profiles.set(i, profile);
                saveToFile();
                return;
            }
        }
        throw new DataStorageException("Profile with ID " + profile.getId() + " not found for update.");
    }

    /**
     * Deletes a profile by ID.
     */
    public synchronized boolean deleteProfile(String profileId) throws DataStorageException {
        boolean removed = profiles.removeIf(p -> p.getId().equals(profileId));
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    /**
     * Returns an unmodifiable list of all profiles.
     */
    public List<Profile> getAllProfiles() {
        return Collections.unmodifiableList(new ArrayList<>(profiles));
    }

    /**
     * Finds a profile by ID.
     */
    public Profile getProfileById(String id) {
        for (Profile p : profiles) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Searches profiles by name (case-insensitive substring match).
     */
    public List<Profile> searchProfilesByName(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllProfiles();
        }
        String lowerQuery = query.trim().toLowerCase();
        List<Profile> results = new ArrayList<>();
        for (Profile p : profiles) {
            if (p.getFullName().toLowerCase().contains(lowerQuery) ||
                p.getDegreeBranch().toLowerCase().contains(lowerQuery) ||
                p.getCollegeName().toLowerCase().contains(lowerQuery) ||
                p.getTechnicalSkills().toLowerCase().contains(lowerQuery)) {
                results.add(p);
            }
        }
        return results;
    }

    public int getProfileCount() {
        return profiles.size();
    }

    /**
     * Serializes the profiles list to disk using Java ObjectOutputStream.
     */
    public synchronized void saveToFile() throws DataStorageException {
        ensureDataDirectoryExists();
        File file = new File(DATA_FILE);
        File tempFile = new File(DATA_FILE + ".tmp");

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile))) {
            oos.writeObject(new ArrayList<>(profiles));
            oos.flush();
        } catch (IOException e) {
            throw new DataStorageException("Failed to serialize profiles: " + e.getMessage(), e);
        }

        // Safe file replace
        try {
            Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            try {
                if (file.exists()) {
                    file.delete();
                }
                Files.copy(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                tempFile.delete();
            } catch (IOException ex) {
                throw new DataStorageException("Failed to finalize profile storage file: " + ex.getMessage(), ex);
            }
        }
    }

    /**
     * Deserializes profiles from disk.
     * Safely recovers if file is missing or corrupted.
     */
    @SuppressWarnings("unchecked")
    public synchronized void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            profiles = new ArrayList<>();
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                profiles = (List<Profile>) obj;
            } else {
                handleCorruptedFile(file, "File does not contain a valid Profile list.");
            }
        } catch (Exception e) {
            handleCorruptedFile(file, e.getMessage());
        }
    }

    /**
     * Safely preserves corrupted files and resets the profile store.
     */
    private void handleCorruptedFile(File file, String reason) {
        System.err.println("Warning: Corrupted profiles data file detected. (" + reason + ")");
        File backup = new File(DATA_DIR, "profiles_corrupted_" + System.currentTimeMillis() + ".bak");
        try {
            Files.copy(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.err.println("Corrupted file backed up to: " + backup.getAbsolutePath());
        } catch (IOException ignored) {
        }
        profiles = new ArrayList<>();
    }

    /**
     * Creates a comprehensive sample engineering student profile.
     * Ideal for academic demonstration and testing.
     */
    public static Profile createSampleProfile() {
        Profile p = new Profile();
        p.setFullName("Aarav Sharma");
        p.setEmail("aarav.sharma@college.edu");
        p.setPhone("+91 98765 43210");
        p.setAddress("Bangalore, Karnataka, India");
        p.setCareerObjective("Aspiring Software Engineer and 2nd-year B.Tech CSE student with a solid foundation in Java, Data Structures, and Object-Oriented Software Engineering. Passionate about designing robust desktop and web systems, problem-solving, and contributing to innovative developer tooling.");
        p.setCollegeName("National Institute of Technology Karnataka (NITK)");
        p.setDegreeBranch("B.Tech in Computer Science & Engineering");
        p.setGraduationYear("2026");
        p.setCgpaPercentage("9.12 CGPA");
        p.setTechnicalSkills("Programming: Java (Core & Advanced), Python, C++, SQL\nFrameworks & GUI: Java Swing, AWT, JDBC, JavaFX\nTools & Platforms: Git, GitHub, VS Code, Apache Maven, Linux, Docker\nConcepts: OOP, Data Structures & Algorithms, Design Patterns, Database Management");
        p.setProjects("1. Profile Management System & Resume Generator (Java Swing):\n   - Architected a desktop application for automated CV synthesis across 6 distinct visual themes.\n   - Implemented Apache PDFBox export, OOP polymorphism, and file serialization.\n\n2. Smart Campus Navigation & Route Finder (Java & Graph Algorithms):\n   - Developed a shortest-path campus navigator utilizing Dijkstra's algorithm.\n   - Visualized interactive campus nodes with custom AWT/Swing rendering.\n\n3. Library Records & Inventory Portal (Java & SQLite):\n   - Built an automated book issue/return tracking system with real-time fine calculation.");
        p.setExperience("Software Engineering Intern | CyberTech Labs (Summer 2025)\n- Designed and implemented automated data validation routines in Java.\n- Optimized data structure lookups, reducing query latency by 35%.\n- Collaborated with senior engineers in Agile sprints and peer code reviews.");
        p.setCertifications("• Oracle Certified Associate, Java SE 8 Programmer (1Z0-808)\n• HackerRank Problem Solving (Gold Badge - 5 Stars)\n• Meta Front-End & Core Programming Specialization (Coursera)");
        p.setAchievements("• 1st Place - State Level Hackathon 2025 (Built automated disaster response dispatch system)\n• Dean's Honor Roll - Consecutive top 5% academic performance (2024, 2025)\n• Technical Lead - College Coding Society, mentored 60+ junior students in Java");
        p.setLanguages("English (Fluent), Hindi (Native), Kannada (Conversational)");
        return p;
    }
}
