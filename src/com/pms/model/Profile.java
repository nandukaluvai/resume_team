package com.pms.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Profile Model Class
 * Represents a student or professional user profile for resume generation.
 * Implements Serializable for persistent file storage.
 * 
 * Demonstrates:
 * - Object-Oriented Programming (OOP)
 * - Encapsulation (private attributes with public getters/setters)
 * - Java Serialization for file storage
 */
public class Profile implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique Identifier & Metadata
    private String id;
    private long lastModified;

    // Personal Details
    private String fullName;
    private String email;
    private String phone;
    private String address;

    // Career Objective
    private String careerObjective;

    // Educational Qualifications
    private String collegeName;
    private String degreeBranch;
    private String graduationYear;
    private String cgpaPercentage;

    // Skills & Technical Expertise
    private String technicalSkills;

    // Practical Exposure & Work
    private String projects;
    private String experience; // Internships or work experience

    // Additional Sections
    private String certifications;
    private String achievements;
    private String languages;

    /**
     * Default Constructor
     */
    public Profile() {
        this.id = UUID.randomUUID().toString();
        this.lastModified = System.currentTimeMillis();
    }

    /**
     * Parameterized Constructor
     */
    public Profile(String fullName, String email, String phone, String address,
                   String careerObjective, String collegeName, String degreeBranch,
                   String graduationYear, String cgpaPercentage, String technicalSkills,
                   String projects, String experience, String certifications,
                   String achievements, String languages) {
        this();
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.careerObjective = careerObjective;
        this.collegeName = collegeName;
        this.degreeBranch = degreeBranch;
        this.graduationYear = graduationYear;
        this.cgpaPercentage = cgpaPercentage;
        this.technicalSkills = technicalSkills;
        this.projects = projects;
        this.experience = experience;
        this.certifications = certifications;
        this.achievements = achievements;
        this.languages = languages;
    }

    // --- Getters and Setters (Demonstrating Encapsulation) ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public void touch() {
        this.lastModified = System.currentTimeMillis();
    }

    public String getFormattedLastModified() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
        return sdf.format(new Date(lastModified));
    }

    public String getFullName() {
        return fullName != null ? fullName : "";
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email != null ? email : "";
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone != null ? phone : "";
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address != null ? address : "";
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCareerObjective() {
        return careerObjective != null ? careerObjective : "";
    }

    public void setCareerObjective(String careerObjective) {
        this.careerObjective = careerObjective;
    }

    public String getCollegeName() {
        return collegeName != null ? collegeName : "";
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getDegreeBranch() {
        return degreeBranch != null ? degreeBranch : "";
    }

    public void setDegreeBranch(String degreeBranch) {
        this.degreeBranch = degreeBranch;
    }

    public String getGraduationYear() {
        return graduationYear != null ? graduationYear : "";
    }

    public void setGraduationYear(String graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getCgpaPercentage() {
        return cgpaPercentage != null ? cgpaPercentage : "";
    }

    public void setCgpaPercentage(String cgpaPercentage) {
        this.cgpaPercentage = cgpaPercentage;
    }

    public String getTechnicalSkills() {
        return technicalSkills != null ? technicalSkills : "";
    }

    public void setTechnicalSkills(String technicalSkills) {
        this.technicalSkills = technicalSkills;
    }

    public String getProjects() {
        return projects != null ? projects : "";
    }

    public void setProjects(String projects) {
        this.projects = projects;
    }

    public String getExperience() {
        return experience != null ? experience : "";
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getCertifications() {
        return certifications != null ? certifications : "";
    }

    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    public String getAchievements() {
        return achievements != null ? achievements : "";
    }

    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }

    public String getLanguages() {
        return languages != null ? languages : "";
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    @Override
    public String toString() {
        return fullName + " (" + degreeBranch + ", " + graduationYear + ")";
    }
}
