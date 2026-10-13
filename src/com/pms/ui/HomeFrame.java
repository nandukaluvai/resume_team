package com.pms.ui;

import com.pms.model.Profile;
import com.pms.service.ProfileManager;
import com.pms.template.ResumeTemplate;
import com.pms.template.TemplateType;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * HomeFrame
 * Modern, attractive main dashboard for the Profile Management System.
 * 
 * Features:
 * - Quick Action Cards for Create Resume, View Profiles, Manage Profiles, and Exit
 * - Live stats on saved profiles
 * - Showcase of 6 available resume templates
 * - College academic demonstration ready UI
 */
public class HomeFrame extends JFrame {

    private final ProfileManager profileManager;
    private JLabel lblProfileCountStat;

    public HomeFrame() {
        this.profileManager = new ProfileManager();

        setTitle("Profile Management System | Resume & CV Generator");
        setSize(980, 680);
        setMinimumSize(new Dimension(850, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmExit();
            }
        });

        initUI();
        updateStats();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_LIGHT);

        // --- Top Hero Header Banner ---
        JPanel heroPanel = new JPanel(new BorderLayout());
        heroPanel.setBackground(UITheme.NAVY_PRIMARY);
        heroPanel.setBorder(new EmptyBorder(28, 36, 28, 36));

        JPanel heroLeft = new JPanel();
        heroLeft.setLayout(new BoxLayout(heroLeft, BoxLayout.Y_AXIS));
        heroLeft.setOpaque(false);

        JLabel appTitle = new JLabel("Profile Management System");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        appTitle.setForeground(Color.WHITE);
        heroLeft.add(appTitle);

        heroLeft.add(Box.createVerticalStrut(6));

        JLabel appSub = new JLabel("Automated Resume & CV Generation Engine with 6 Professional Templates");
        appSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        appSub.setForeground(new Color(224, 231, 255));
        heroLeft.add(appSub);

        heroPanel.add(heroLeft, BorderLayout.WEST);

        // Quick Stats Badge on Hero Right
        JPanel statBox = new JPanel(new GridLayout(2, 1, 4, 4));
        statBox.setBackground(new Color(255, 255, 255, 25));
        statBox.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 255, 255, 60), 1, true),
                new EmptyBorder(10, 18, 10, 18)
        ));

        JLabel lblStatTitle = new JLabel("SAVED PROFILES");
        lblStatTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblStatTitle.setForeground(new Color(224, 231, 255));
        statBox.add(lblStatTitle);

        lblProfileCountStat = new JLabel("0 Profiles");
        lblProfileCountStat.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblProfileCountStat.setForeground(Color.WHITE);
        statBox.add(lblProfileCountStat);

        heroPanel.add(statBox, BorderLayout.EAST);
        root.add(heroPanel, BorderLayout.NORTH);

        // --- Center Main Body ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(UITheme.BG_LIGHT);
        centerPanel.setBorder(new EmptyBorder(28, 36, 28, 36));

        // Welcome Greeting
        JPanel welcomeRow = new JPanel(new BorderLayout());
        welcomeRow.setOpaque(false);

        JLabel welcomeTitle = new JLabel("Welcome to Your Academic Career Dashboard");
        welcomeTitle.setFont(UITheme.FONT_HEADER);
        welcomeTitle.setForeground(UITheme.NAVY_DARK);
        welcomeRow.add(welcomeTitle, BorderLayout.WEST);

        JLabel techBadge = new JLabel("Pure Java • Java Swing • Apache PDFBox • Java Serialization");
        techBadge.setFont(UITheme.FONT_SMALL);
        techBadge.setForeground(UITheme.TEXT_MUTED);
        welcomeRow.add(techBadge, BorderLayout.EAST);

        centerPanel.add(welcomeRow);
        centerPanel.add(Box.createVerticalStrut(20));

        // 4 Action Feature Cards Grid
        JPanel cardsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsGrid.setOpaque(false);

        // 1. Create Resume Card
        cardsGrid.add(createActionCard(
                "➕  Create Resume",
                "Start building a new profile with our guided form. Fill your details, academics, skills, and projects.",
                "Launch Builder",
                UITheme.NAVY_PRIMARY,
                e -> openCreateForm()
        ));

        // 2. View Profiles Card
        cardsGrid.add(createActionCard(
                "📋  View Profiles",
                "Browse all candidate records saved in local storage. Quickly select any profile to generate a CV.",
                "Browse Records",
                UITheme.BLUE_ACCENT,
                e -> openManageProfiles()
        ));

        // 3. Manage Profiles Card
        cardsGrid.add(createActionCard(
                "⚙  Manage Profiles",
                "Search, edit existing entries, delete records with safety confirmation, and export PDFs directly.",
                "Manage Database",
                new Color(13, 148, 136),
                e -> openManageProfiles()
        ));

        // 4. Quick Sample Demo Card / Exit
        cardsGrid.add(createActionCard(
                "✨  Instant Demo Resume",
                "Instantly load a rich pre-filled Engineering profile and test-drive all 6 visual templates right away.",
                "Open Sample Demo",
                new Color(124, 58, 237),
                e -> openSampleDemo()
        ));

        centerPanel.add(cardsGrid);
        centerPanel.add(Box.createVerticalStrut(24));

        // Templates Showcase Footer Bar
        JPanel templateBanner = UITheme.createCardPanel();
        templateBanner.setLayout(new BorderLayout(12, 4));

        JLabel lblTemplatesTitle = new JLabel("Supported Resume Templates (6 Distinct Visual Formats):");
        lblTemplatesTitle.setFont(UITheme.FONT_LABEL);
        lblTemplatesTitle.setForeground(UITheme.NAVY_PRIMARY);
        templateBanner.add(lblTemplatesTitle, BorderLayout.NORTH);

        JPanel badgesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        badgesPanel.setOpaque(false);
        for (TemplateType t : TemplateType.values()) {
            JLabel badge = new JLabel(" " + t.getDisplayName() + " ");
            badge.setFont(UITheme.FONT_SMALL);
            badge.setOpaque(true);
            badge.setBackground(new Color(241, 245, 249));
            badge.setForeground(UITheme.NAVY_DARK);
            badge.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1, true));
            badgesPanel.add(badge);
        }
        templateBanner.add(badgesPanel, BorderLayout.CENTER);

        centerPanel.add(templateBanner);

        root.add(centerPanel, BorderLayout.CENTER);

        // --- Bottom Status Bar ---
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(12, 36, 12, 36)
        ));

        JLabel creditsLabel = new JLabel("College Academic Project  |  2nd-Year B.Tech Computer Science & Engineering");
        creditsLabel.setFont(UITheme.FONT_SMALL);
        creditsLabel.setForeground(UITheme.TEXT_MUTED);
        bottomBar.add(creditsLabel, BorderLayout.WEST);

        JPanel bottomBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        bottomBtns.setOpaque(false);

        JButton btnExit = UITheme.createDangerButton("🚪 Exit");
        btnExit.addActionListener(e -> confirmExit());
        bottomBtns.add(btnExit);

        bottomBar.add(bottomBtns, BorderLayout.EAST);
        root.add(bottomBar, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel createActionCard(String title, String description, String buttonText, Color accentColor, java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(18, 20, 18, 20)
        ));

        // Header Title
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(UITheme.NAVY_DARK);
        card.add(lblTitle, BorderLayout.NORTH);

        // Description Body
        JLabel lblDesc = new JLabel("<html><body style='width: 260px; color: #475569;'>" + description + "</body></html>");
        lblDesc.setFont(UITheme.FONT_REGULAR);
        card.add(lblDesc, BorderLayout.CENTER);

        // Action Button
        JButton btn = UITheme.createPrimaryButton(buttonText);
        btn.setBackground(accentColor);
        btn.addActionListener(action);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btn);
        card.add(btnPanel, BorderLayout.SOUTH);

        // Card Hover Effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(new CompoundBorder(
                        new LineBorder(accentColor, 1, true),
                        new EmptyBorder(18, 20, 18, 20)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(new CompoundBorder(
                        new LineBorder(UITheme.BORDER_COLOR, 1, true),
                        new EmptyBorder(18, 20, 18, 20)
                ));
            }
        });

        return card;
    }

    private void openCreateForm() {
        ProfileFormFrame form = new ProfileFormFrame(profileManager, null);
        form.setVisible(true);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                updateStats();
            }
        });
    }

    private void openManageProfiles() {
        ProfileManagerFrame managerFrame = new ProfileManagerFrame(profileManager);
        managerFrame.setVisible(true);
        managerFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                updateStats();
            }
        });
    }

    private void openSampleDemo() {
        Profile sample = ProfileManager.createSampleProfile();
        // Check if sample already exists or use it directly for preview
        ResumePreviewFrame preview = new ResumePreviewFrame(profileManager, sample, TemplateType.PROFESSIONAL);
        preview.setVisible(true);
    }

    public void updateStats() {
        int count = profileManager.getProfileCount();
        lblProfileCountStat.setText(count + (count == 1 ? " Profile" : " Profiles"));
    }

    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit Profile Management System?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
