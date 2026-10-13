package com.pms.ui;

import com.pms.export.PDFExporter;
import com.pms.model.Profile;
import com.pms.service.DataStorageException;
import com.pms.service.ProfileManager;
import com.pms.template.ResumeTemplate;
import com.pms.template.TemplateType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

/**
 * ProfileManagerFrame
 * Dedicated management dashboard to view, search, edit, delete,
 * and generate resumes from locally persisted profiles.
 * 
 * Demonstrates:
 * - JTable with custom TableModel, renderers, and selection listeners
 * - Real-time search and filtering with DocumentListener
 * - CRUD operations and confirmation dialogs
 */
public class ProfileManagerFrame extends JFrame {

    private final ProfileManager profileManager;
    private JTable profileTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JLabel lblProfileCount;
    private List<Profile> displayedProfiles;

    public ProfileManagerFrame(ProfileManager profileManager) {
        this.profileManager = profileManager;

        setTitle("Manage Profiles | Profile Management System");
        setSize(1050, 680);
        setMinimumSize(new Dimension(850, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        refreshTable();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_LIGHT);

        // --- Top Header Banner ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.NAVY_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel titleLabel = new JLabel("Saved Candidate Profiles");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        lblProfileCount = new JLabel("Total Profiles: 0");
        lblProfileCount.setFont(UITheme.FONT_SUBTITLE);
        lblProfileCount.setForeground(new Color(224, 231, 255));
        headerPanel.add(lblProfileCount, BorderLayout.EAST);

        root.add(headerPanel, BorderLayout.NORTH);

        // --- Search Bar & Quick Filters Toolbar ---
        JPanel searchBar = new JPanel(new BorderLayout(12, 0));
        searchBar.setBackground(Color.WHITE);
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(12, 24, 12, 24)
        ));

        JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchInputPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("🔍 Search Profiles:");
        lblSearch.setFont(UITheme.FONT_LABEL);
        lblSearch.setForeground(UITheme.TEXT_PRIMARY);
        searchInputPanel.add(lblSearch);

        txtSearch = new JTextField(24);
        UITheme.styleTextInput(txtSearch);
        txtSearch.setToolTipText("Search by candidate name, degree, college, or skills...");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { onSearch(); }
            @Override
            public void removeUpdate(DocumentEvent e) { onSearch(); }
            @Override
            public void changedUpdate(DocumentEvent e) { onSearch(); }
        });
        searchInputPanel.add(txtSearch);

        JButton btnClearSearch = UITheme.createSecondaryButton("Clear");
        btnClearSearch.addActionListener(e -> {
            txtSearch.setText("");
            refreshTable();
        });
        searchInputPanel.add(btnClearSearch);

        searchBar.add(searchInputPanel, BorderLayout.WEST);

        // New Profile & Sample Profile Button
        JPanel searchRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchRightPanel.setOpaque(false);

        JButton btnAddSample = UITheme.createSecondaryButton("✨ Add Sample Profile");
        btnAddSample.setToolTipText("Insert a comprehensive test profile");
        btnAddSample.addActionListener(e -> addSampleProfile());
        searchRightPanel.add(btnAddSample);

        JButton btnNewProfile = UITheme.createPrimaryButton("+ Create New Profile");
        btnNewProfile.addActionListener(e -> {
            ProfileFormFrame form = new ProfileFormFrame(profileManager, null);
            form.setVisible(true);
            form.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    refreshTable();
                }
            });
        });
        searchRightPanel.add(btnNewProfile);

        searchBar.add(searchRightPanel, BorderLayout.EAST);
        root.add(searchBar, BorderLayout.CENTER);

        // --- Center Table View ---
        String[] columns = {"#", "Candidate Name", "Email", "Degree & Branch", "College / University", "Grad Year", "CGPA", "Last Modified"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only table
            }
        };

        profileTable = new JTable(tableModel);
        profileTable.setFont(UITheme.FONT_REGULAR);
        profileTable.setRowHeight(32);
        profileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        profileTable.getTableHeader().setFont(UITheme.FONT_LABEL);
        profileTable.getTableHeader().setBackground(new Color(241, 245, 249));
        profileTable.getTableHeader().setForeground(UITheme.NAVY_DARK);
        profileTable.setGridColor(UITheme.BORDER_COLOR);
        profileTable.setShowVerticalLines(true);
        profileTable.setSelectionBackground(new Color(224, 231, 255));
        profileTable.setSelectionForeground(UITheme.NAVY_DARK);

        // Column widths
        profileTable.getColumnModel().getColumn(0).setPreferredWidth(35);
        profileTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        profileTable.getColumnModel().getColumn(2).setPreferredWidth(170);
        profileTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        profileTable.getColumnModel().getColumn(4).setPreferredWidth(180);
        profileTable.getColumnModel().getColumn(5).setPreferredWidth(75);
        profileTable.getColumnModel().getColumn(6).setPreferredWidth(75);
        profileTable.getColumnModel().getColumn(7).setPreferredWidth(140);

        // Center align ID, Year, CGPA
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        profileTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        profileTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        profileTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        // Double click row to generate/preview resume
        profileTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onGenerateResume();
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(profileTable);
        tableScroll.setBorder(new EmptyBorder(16, 24, 16, 24));
        tableScroll.setBackground(UITheme.BG_LIGHT);

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(UITheme.BG_LIGHT);
        centerContainer.add(searchBar, BorderLayout.NORTH);
        centerContainer.add(tableScroll, BorderLayout.CENTER);

        root.add(centerContainer, BorderLayout.CENTER);

        // --- Bottom Action Toolbar ---
        JPanel actionBar = new JPanel(new BorderLayout());
        actionBar.setBackground(Color.WHITE);
        actionBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(14, 24, 14, 24)
        ));

        JPanel leftBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftBtns.setOpaque(false);

        JButton btnClose = UITheme.createSecondaryButton("← Back to Home");
        btnClose.addActionListener(e -> dispose());
        leftBtns.add(btnClose);

        JButton btnRefresh = UITheme.createSecondaryButton("🔄 Refresh");
        btnRefresh.addActionListener(e -> refreshTable());
        leftBtns.add(btnRefresh);

        actionBar.add(leftBtns, BorderLayout.WEST);

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightBtns.setOpaque(false);

        JButton btnDelete = UITheme.createDangerButton("🗑 Delete Profile");
        btnDelete.addActionListener(e -> onDeleteProfile());
        rightBtns.add(btnDelete);

        JButton btnEdit = UITheme.createSecondaryButton("✏ Edit Profile");
        btnEdit.addActionListener(e -> onEditProfile());
        rightBtns.add(btnEdit);

        JButton btnQuickPdf = UITheme.createSecondaryButton("💾 Export PDF");
        btnQuickPdf.addActionListener(e -> onExportQuickPDF());
        rightBtns.add(btnQuickPdf);

        JButton btnPreview = UITheme.createPrimaryButton("📄 Generate Resume Preview");
        btnPreview.addActionListener(e -> onGenerateResume());
        rightBtns.add(btnPreview);

        actionBar.add(rightBtns, BorderLayout.EAST);
        root.add(actionBar, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void refreshTable() {
        displayedProfiles = profileManager.getAllProfiles();
        updateTableRows(displayedProfiles);
        lblProfileCount.setText("Total Profiles: " + displayedProfiles.size());
    }

    private void onSearch() {
        String q = txtSearch.getText().trim();
        displayedProfiles = profileManager.searchProfilesByName(q);
        updateTableRows(displayedProfiles);
        lblProfileCount.setText("Matches Found: " + displayedProfiles.size());
    }

    private void updateTableRows(List<Profile> list) {
        tableModel.setRowCount(0);
        int index = 1;
        for (Profile p : list) {
            tableModel.addRow(new Object[]{
                    index++,
                    p.getFullName(),
                    p.getEmail(),
                    p.getDegreeBranch(),
                    p.getCollegeName(),
                    p.getGraduationYear(),
                    p.getCgpaPercentage(),
                    p.getFormattedLastModified()
            });
        }
    }

    private Profile getSelectedProfile() {
        int row = profileTable.getSelectedRow();
        if (row < 0 || row >= displayedProfiles.size()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a profile from the table first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return displayedProfiles.get(row);
    }

    private void onGenerateResume() {
        Profile selected = getSelectedProfile();
        if (selected == null) return;

        ResumePreviewFrame previewFrame = new ResumePreviewFrame(profileManager, selected, TemplateType.PROFESSIONAL);
        previewFrame.setVisible(true);
    }

    private void onEditProfile() {
        Profile selected = getSelectedProfile();
        if (selected == null) return;

        ProfileFormFrame form = new ProfileFormFrame(profileManager, selected);
        form.setVisible(true);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                refreshTable();
            }
        });
    }

    private void onDeleteProfile() {
        Profile selected = getSelectedProfile();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete profile for:\n'" + selected.getFullName() + "'?\n\nThis action cannot be undone.",
                "Confirm Delete Profile",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                profileManager.deleteProfile(selected.getId());
                JOptionPane.showMessageDialog(this,
                        "Profile for '" + selected.getFullName() + "' deleted successfully.",
                        "Deleted",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } catch (DataStorageException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting profile: " + ex.getMessage(),
                        "Storage Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onExportQuickPDF() {
        Profile selected = getSelectedProfile();
        if (selected == null) return;

        TemplateType[] types = TemplateType.values();
        TemplateType chosen = (TemplateType) JOptionPane.showInputDialog(this,
                "Select Resume Template for PDF Export:",
                "Choose Template",
                JOptionPane.QUESTION_MESSAGE,
                null,
                types,
                types[0]);

        if (chosen == null) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Resume as PDF");
        chooser.setFileFilter(new FileNameExtensionFilter("PDF Documents (*.pdf)", "pdf"));
        String sanitized = selected.getFullName().trim().replaceAll("[^a-zA-Z0-9.-]", "_");
        chooser.setSelectedFile(new File("Resume_" + sanitized + "_" + chosen.name() + ".pdf"));

        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File dest = chooser.getSelectedFile();
            if (!dest.getName().toLowerCase().endsWith(".pdf")) {
                dest = new File(dest.getParentFile(), dest.getName() + ".pdf");
            }
            try {
                ResumeTemplate template = ResumeTemplate.getTemplate(chosen);
                PDFExporter exporter = new PDFExporter(selected, template);
                exporter.exportToFile(dest);

                int openChoice = JOptionPane.showOptionDialog(this,
                        "PDF exported successfully to:\n" + dest.getAbsolutePath() + "\n\nOpen PDF now?",
                        "Export Successful",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new String[]{"Open PDF", "Close"},
                        "Open PDF");

                if (openChoice == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(dest);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Export Failed: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addSampleProfile() {
        try {
            Profile sample = ProfileManager.createSampleProfile();
            profileManager.addProfile(sample);
            refreshTable();
            JOptionPane.showMessageDialog(this,
                    "Sample profile for '" + sample.getFullName() + "' added successfully!",
                    "Sample Added",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to add sample: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
