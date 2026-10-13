package com.pms.ui;

import com.pms.model.Profile;
import com.pms.service.DataStorageException;
import com.pms.service.ProfileManager;
import com.pms.service.ValidationException;
import com.pms.template.TemplateType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * ProfileFormFrame
 * Comprehensive data entry form for candidate resumes.
 * Supports adding new profiles, editing existing ones, input validation,
 * sample auto-fill, and immediate resume preview.
 * 
 * Demonstrates:
 * - Swing Form Design (GridBagLayout, ScrollPane, Card Panels)
 * - Input Validation and Exception Handling
 * - State Management (Edit mode vs Create mode)
 */
public class ProfileFormFrame extends JFrame {

    private final ProfileManager profileManager;
    private Profile editingProfile; // null if creating new profile

    // Personal Details
    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtAddress;

    // Career Objective
    private JTextArea txtCareerObjective;

    // Education
    private JTextField txtCollegeName;
    private JTextField txtDegreeBranch;
    private JTextField txtGraduationYear;
    private JTextField txtCgpaPercentage;

    // Skills, Projects & Experience
    private JTextArea txtTechnicalSkills;
    private JTextArea txtProjects;
    private JTextArea txtExperience;

    // Additional Sections
    private JTextArea txtCertifications;
    private JTextArea txtAchievements;
    private JTextField txtLanguages;

    // Template selection for direct generation
    private JComboBox<TemplateType> cmbTemplate;

    public ProfileFormFrame(ProfileManager profileManager, Profile profileToEdit) {
        this.profileManager = profileManager;
        this.editingProfile = profileToEdit;

        String modeTitle = (editingProfile == null) ? "Create New Profile & Resume" : "Edit Profile - " + editingProfile.getFullName();
        setTitle(modeTitle + " | Profile Management System");
        setSize(920, 800);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();

        if (editingProfile != null) {
            populateFields(editingProfile);
        }
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_LIGHT);

        // --- Top Header Banner ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.NAVY_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(18, 28, 18, 28));

        JLabel titleLabel = new JLabel((editingProfile == null) ? "Create Candidate Profile" : "Edit Candidate Profile");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel subtitleLabel = new JLabel("Fill in all sections accurately to generate high-impact resumes");
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(new Color(224, 231, 255));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        root.add(headerPanel, BorderLayout.NORTH);

        // --- Form Body (Scrollable) ---
        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(UITheme.BG_LIGHT);
        formContent.setBorder(new EmptyBorder(20, 28, 20, 28));

        // 1. Personal Information Card
        formContent.add(createPersonalInfoCard());
        formContent.add(Box.createVerticalStrut(18));

        // 2. Career Objective Card
        formContent.add(createObjectiveCard());
        formContent.add(Box.createVerticalStrut(18));

        // 3. Education Card
        formContent.add(createEducationCard());
        formContent.add(Box.createVerticalStrut(18));

        // 4. Skills & Projects Card
        formContent.add(createSkillsProjectsCard());
        formContent.add(Box.createVerticalStrut(18));

        // 5. Experience & Additional Sections Card
        formContent.add(createExperienceAdditionalCard());
        formContent.add(Box.createVerticalStrut(18));

        JScrollPane scrollPane = new JScrollPane(formContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        root.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Action Bar ---
        JPanel actionBar = new JPanel(new BorderLayout());
        actionBar.setBackground(Color.WHITE);
        actionBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(14, 28, 14, 28)
        ));

        // Left Action: Back & Sample Fill
        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftActions.setOpaque(false);

        JButton btnBack = UITheme.createSecondaryButton("← Back to Home");
        btnBack.addActionListener(e -> dispose());
        leftActions.add(btnBack);

        JButton btnSample = UITheme.createSecondaryButton("✨ Fill Sample Data");
        btnSample.setToolTipText("Load rich sample engineering student data for quick demonstration");
        btnSample.addActionListener(e -> fillSampleData());
        leftActions.add(btnSample);

        JButton btnClear = UITheme.createSecondaryButton("🔄 Clear Form");
        btnClear.addActionListener(e -> clearForm());
        leftActions.add(btnClear);

        actionBar.add(leftActions, BorderLayout.WEST);

        // Right Action: Template Choice, Save, and Preview
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightActions.setOpaque(false);

        JLabel lblTpl = new JLabel("Template:");
        lblTpl.setFont(UITheme.FONT_LABEL);
        rightActions.add(lblTpl);

        cmbTemplate = new JComboBox<>(TemplateType.values());
        cmbTemplate.setFont(UITheme.FONT_BUTTON);
        rightActions.add(cmbTemplate);

        JButton btnSave = UITheme.createSecondaryButton("💾 Save Profile");
        btnSave.addActionListener(e -> saveProfileOnly());
        rightActions.add(btnSave);

        JButton btnPreview = UITheme.createPrimaryButton("👁 Generate & Preview Resume");
        btnPreview.addActionListener(e -> saveAndPreview());
        rightActions.add(btnPreview);

        actionBar.add(rightActions, BorderLayout.EAST);
        root.add(actionBar, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel createPersonalInfoCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = createGbc();

        // Section Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        card.add(UITheme.createSectionHeader("1. Personal Details"), gbc);

        // Full Name & Email
        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        card.add(UITheme.createFieldLabel("Full Name", true), gbc);

        gbc.gridx = 1;
        txtFullName = UITheme.createTextField();
        card.add(txtFullName, gbc);

        gbc.gridx = 2;
        card.add(UITheme.createFieldLabel("Email Address", true), gbc);

        gbc.gridx = 3;
        txtEmail = UITheme.createTextField();
        card.add(txtEmail, gbc);

        // Phone & Location
        gbc.gridy = 2; gbc.gridx = 0;
        card.add(UITheme.createFieldLabel("Phone Number", true), gbc);

        gbc.gridx = 1;
        txtPhone = UITheme.createTextField();
        card.add(txtPhone, gbc);

        gbc.gridx = 2;
        card.add(UITheme.createFieldLabel("Location / Address", true), gbc);

        gbc.gridx = 3;
        txtAddress = UITheme.createTextField();
        card.add(txtAddress, gbc);

        return card;
    }

    private JPanel createObjectiveCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 8));

        card.add(UITheme.createSectionHeader("2. Career Objective"), BorderLayout.NORTH);

        txtCareerObjective = UITheme.createTextArea(3);
        JScrollPane sp = new JScrollPane(txtCareerObjective);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        card.add(sp, BorderLayout.CENTER);

        return card;
    }

    private JPanel createEducationCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = createGbc();

        // Section Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        card.add(UITheme.createSectionHeader("3. Educational Qualifications"), gbc);

        // College Name & Degree/Branch
        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        card.add(UITheme.createFieldLabel("College / University", true), gbc);

        gbc.gridx = 1;
        txtCollegeName = UITheme.createTextField();
        card.add(txtCollegeName, gbc);

        gbc.gridx = 2;
        card.add(UITheme.createFieldLabel("Degree & Branch", true), gbc);

        gbc.gridx = 3;
        txtDegreeBranch = UITheme.createTextField();
        card.add(txtDegreeBranch, gbc);

        // Graduation Year & CGPA
        gbc.gridy = 2; gbc.gridx = 0;
        card.add(UITheme.createFieldLabel("Graduation Year", true), gbc);

        gbc.gridx = 1;
        txtGraduationYear = UITheme.createTextField();
        card.add(txtGraduationYear, gbc);

        gbc.gridx = 2;
        card.add(UITheme.createFieldLabel("CGPA / Percentage", true), gbc);

        gbc.gridx = 3;
        txtCgpaPercentage = UITheme.createTextField();
        card.add(txtCgpaPercentage, gbc);

        return card;
    }

    private JPanel createSkillsProjectsCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = createGbc();

        // Section Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(UITheme.createSectionHeader("4. Technical Skills & Projects"), gbc);

        // Technical Skills
        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 2;
        card.add(UITheme.createFieldLabel("Technical Skills (Languages, Tools, Frameworks, Core Subjects)", true), gbc);

        gbc.gridy = 2;
        txtTechnicalSkills = UITheme.createTextArea(3);
        JScrollPane spSkills = new JScrollPane(txtTechnicalSkills);
        spSkills.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        card.add(spSkills, gbc);

        // Projects
        gbc.gridy = 3;
        card.add(UITheme.createFieldLabel("Projects (Include project title, technology used, and key features)", false), gbc);

        gbc.gridy = 4;
        txtProjects = UITheme.createTextArea(4);
        JScrollPane spProjects = new JScrollPane(txtProjects);
        spProjects.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        card.add(spProjects, gbc);

        return card;
    }

    private JPanel createExperienceAdditionalCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = createGbc();

        // Section Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(UITheme.createSectionHeader("5. Experience, Certifications & Achievements"), gbc);

        // Experience / Internships
        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 2;
        card.add(UITheme.createFieldLabel("Internships or Work Experience", false), gbc);

        gbc.gridy = 2;
        txtExperience = UITheme.createTextArea(3);
        JScrollPane spExp = new JScrollPane(txtExperience);
        spExp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        card.add(spExp, gbc);

        // Certifications
        gbc.gridy = 3;
        card.add(UITheme.createFieldLabel("Certifications & Courses", false), gbc);

        gbc.gridy = 4;
        txtCertifications = UITheme.createTextArea(2);
        JScrollPane spCert = new JScrollPane(txtCertifications);
        spCert.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        card.add(spCert, gbc);

        // Achievements
        gbc.gridy = 5;
        card.add(UITheme.createFieldLabel("Achievements & Awards", false), gbc);

        gbc.gridy = 6;
        txtAchievements = UITheme.createTextArea(2);
        JScrollPane spAch = new JScrollPane(txtAchievements);
        spAch.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        card.add(spAch, gbc);

        // Languages Known
        gbc.gridy = 7;
        card.add(UITheme.createFieldLabel("Languages Known (e.g. English, Hindi, Telugu)", false), gbc);

        gbc.gridy = 8;
        txtLanguages = UITheme.createTextField();
        card.add(txtLanguages, gbc);

        return card;
    }

    private GridBagConstraints createGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        return gbc;
    }

    private void populateFields(Profile p) {
        txtFullName.setText(p.getFullName());
        txtEmail.setText(p.getEmail());
        txtPhone.setText(p.getPhone());
        txtAddress.setText(p.getAddress());
        txtCareerObjective.setText(p.getCareerObjective());
        txtCollegeName.setText(p.getCollegeName());
        txtDegreeBranch.setText(p.getDegreeBranch());
        txtGraduationYear.setText(p.getGraduationYear());
        txtCgpaPercentage.setText(p.getCgpaPercentage());
        txtTechnicalSkills.setText(p.getTechnicalSkills());
        txtProjects.setText(p.getProjects());
        txtExperience.setText(p.getExperience());
        txtCertifications.setText(p.getCertifications());
        txtAchievements.setText(p.getAchievements());
        txtLanguages.setText(p.getLanguages());
    }

    private Profile buildProfileFromForm() {
        Profile p = (editingProfile != null) ? editingProfile : new Profile();
        p.setFullName(txtFullName.getText().trim());
        p.setEmail(txtEmail.getText().trim());
        p.setPhone(txtPhone.getText().trim());
        p.setAddress(txtAddress.getText().trim());
        p.setCareerObjective(txtCareerObjective.getText().trim());
        p.setCollegeName(txtCollegeName.getText().trim());
        p.setDegreeBranch(txtDegreeBranch.getText().trim());
        p.setGraduationYear(txtGraduationYear.getText().trim());
        p.setCgpaPercentage(txtCgpaPercentage.getText().trim());
        p.setTechnicalSkills(txtTechnicalSkills.getText().trim());
        p.setProjects(txtProjects.getText().trim());
        p.setExperience(txtExperience.getText().trim());
        p.setCertifications(txtCertifications.getText().trim());
        p.setAchievements(txtAchievements.getText().trim());
        p.setLanguages(txtLanguages.getText().trim());
        return p;
    }

    private boolean saveProfileOnly() {
        Profile p = buildProfileFromForm();
        try {
            if (editingProfile == null) {
                profileManager.addProfile(p);
                editingProfile = p; // switch to editing mode for saved profile
            } else {
                profileManager.updateProfile(p);
            }
            JOptionPane.showMessageDialog(this,
                    "Profile for '" + p.getFullName() + "' saved successfully!",
                    "Saved Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this,
                    ve.getMessage(),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (DataStorageException dse) {
            JOptionPane.showMessageDialog(this,
                    "Storage Error: " + dse.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void saveAndPreview() {
        Profile p = buildProfileFromForm();
        try {
            // Validate input first
            profileManager.validateProfile(p);

            // Save to manager
            if (editingProfile == null) {
                profileManager.addProfile(p);
                editingProfile = p;
            } else {
                profileManager.updateProfile(p);
            }

            TemplateType selectedType = (TemplateType) cmbTemplate.getSelectedItem();
            ResumePreviewFrame previewFrame = new ResumePreviewFrame(profileManager, p, selectedType);
            previewFrame.setVisible(true);
            dispose();
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this,
                    ve.getMessage(),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
        } catch (DataStorageException dse) {
            JOptionPane.showMessageDialog(this,
                    "Storage Error: " + dse.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillSampleData() {
        Profile sample = ProfileManager.createSampleProfile();
        populateFields(sample);
    }

    private void clearForm() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear all form fields?",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            txtFullName.setText("");
            txtEmail.setText("");
            txtPhone.setText("");
            txtAddress.setText("");
            txtCareerObjective.setText("");
            txtCollegeName.setText("");
            txtDegreeBranch.setText("");
            txtGraduationYear.setText("");
            txtCgpaPercentage.setText("");
            txtTechnicalSkills.setText("");
            txtProjects.setText("");
            txtExperience.setText("");
            txtCertifications.setText("");
            txtAchievements.setText("");
            txtLanguages.setText("");
        }
    }
}
