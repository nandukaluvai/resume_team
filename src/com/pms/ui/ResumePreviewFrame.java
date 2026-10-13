package com.pms.ui;

import com.pms.export.PDFExporter;
import com.pms.model.Profile;
import com.pms.service.ProfileManager;
import com.pms.template.ResumeTemplate;
import com.pms.template.TemplateType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * ResumePreviewFrame
 * Interactive resume viewer window allowing real-time switching between
 * all six resume templates, zooming, and direct high-quality PDF export.
 * 
 * Demonstrates:
 * - Dynamic GUI event handling (ItemListener, ActionListener)
 * - JFileChooser integration with desktop file opening
 * - Clean user flow between preview, editing, and exporting
 */
public class ResumePreviewFrame extends JFrame {

    private final ProfileManager profileManager;
    private Profile profile;
    private ResumeTemplate currentTemplate;

    private ResumeCanvasPanel canvasPanel;
    private JComboBox<TemplateType> templateCombo;
    private JLabel lblTemplateDesc;
    private JLabel lblZoomLevel;

    public ResumePreviewFrame(ProfileManager profileManager, Profile profile, TemplateType initialType) {
        this.profileManager = profileManager;
        this.profile = profile;
        this.currentTemplate = ResumeTemplate.getTemplate(initialType != null ? initialType : TemplateType.PROFESSIONAL);

        setTitle("Resume Preview - " + profile.getFullName() + " [" + currentTemplate.getName() + " Template]");
        setSize(1100, 850);
        setMinimumSize(new Dimension(850, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon().getImage());

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_LIGHT);

        // --- Top Control Toolbar ---
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(12, 20, 12, 20)
        ));

        // Left controls: Template Selector
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);

        JLabel lblSelect = new JLabel("Resume Template:");
        lblSelect.setFont(UITheme.FONT_LABEL);
        lblSelect.setForeground(UITheme.TEXT_PRIMARY);
        leftPanel.add(lblSelect);

        templateCombo = new JComboBox<>(TemplateType.values());
        templateCombo.setSelectedItem(currentTemplate.getType());
        templateCombo.setFont(UITheme.FONT_BUTTON);
        templateCombo.setBackground(Color.WHITE);
        templateCombo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        templateCombo.addActionListener(e -> onTemplateChanged());
        leftPanel.add(templateCombo);

        lblTemplateDesc = new JLabel("  •  " + currentTemplate.getDescription());
        lblTemplateDesc.setFont(UITheme.FONT_SMALL);
        lblTemplateDesc.setForeground(UITheme.TEXT_MUTED);
        leftPanel.add(lblTemplateDesc);

        toolbar.add(leftPanel, BorderLayout.WEST);

        // Right controls: Zoom & Actions
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        // Zoom Controls
        JButton btnZoomOut = UITheme.createSecondaryButton(" - ");
        btnZoomOut.setToolTipText("Zoom Out");
        btnZoomOut.addActionListener(e -> adjustZoom(-0.10));
        rightPanel.add(btnZoomOut);

        lblZoomLevel = new JLabel("90%");
        lblZoomLevel.setFont(UITheme.FONT_LABEL);
        lblZoomLevel.setForeground(UITheme.TEXT_MUTED);
        rightPanel.add(lblZoomLevel);

        JButton btnZoomIn = UITheme.createSecondaryButton(" + ");
        btnZoomIn.setToolTipText("Zoom In");
        btnZoomIn.addActionListener(e -> adjustZoom(0.10));
        rightPanel.add(btnZoomIn);

        JButton btnResetZoom = UITheme.createSecondaryButton("Fit");
        btnResetZoom.setToolTipText("Reset Zoom to 90%");
        btnResetZoom.addActionListener(e -> {
            canvasPanel.setZoomFactor(0.90);
            updateZoomLabel();
        });
        rightPanel.add(btnResetZoom);

        // Edit Profile Button
        JButton btnEdit = UITheme.createSecondaryButton("✏ Edit Profile");
        btnEdit.addActionListener(e -> {
            ProfileFormFrame formFrame = new ProfileFormFrame(profileManager, profile);
            formFrame.setVisible(true);
            dispose();
        });
        rightPanel.add(btnEdit);

        // PDF Export Button
        JButton btnExport = UITheme.createSuccessButton("💾 Export as PDF");
        btnExport.addActionListener(e -> exportToPDF());
        rightPanel.add(btnExport);

        toolbar.add(rightPanel, BorderLayout.EAST);
        root.add(toolbar, BorderLayout.NORTH);

        // --- Center Canvas Scroll Pane ---
        canvasPanel = new ResumeCanvasPanel(profile, currentTemplate);
        JScrollPane scrollPane = new JScrollPane(canvasPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(24);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(24);
        scrollPane.setBackground(new Color(226, 232, 240));

        root.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Status Bar ---
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(Color.WHITE);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(6, 20, 6, 20)
        ));

        JLabel statusLeft = new JLabel("Candidate: " + profile.getFullName() + " (" + profile.getDegreeBranch() + ")");
        statusLeft.setFont(UITheme.FONT_SMALL);
        statusLeft.setForeground(UITheme.TEXT_MUTED);
        statusBar.add(statusLeft, BorderLayout.WEST);

        JLabel statusRight = new JLabel("Standard A4 Dimensions • Ready for Export");
        statusRight.setFont(UITheme.FONT_SMALL);
        statusRight.setForeground(UITheme.TEXT_MUTED);
        statusBar.add(statusRight, BorderLayout.EAST);

        root.add(statusBar, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void onTemplateChanged() {
        TemplateType selected = (TemplateType) templateCombo.getSelectedItem();
        if (selected != null) {
            currentTemplate = ResumeTemplate.getTemplate(selected);
            canvasPanel.setTemplate(currentTemplate);
            lblTemplateDesc.setText("  •  " + currentTemplate.getDescription());
            setTitle("Resume Preview - " + profile.getFullName() + " [" + currentTemplate.getName() + " Template]");
        }
    }

    private void adjustZoom(double delta) {
        double newZoom = canvasPanel.getZoomFactor() + delta;
        canvasPanel.setZoomFactor(newZoom);
        updateZoomLabel();
    }

    private void updateZoomLabel() {
        int percent = (int) Math.round(canvasPanel.getZoomFactor() * 100);
        lblZoomLevel.setText(percent + "%");
    }

    private void exportToPDF() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Resume as PDF");
        chooser.setFileFilter(new FileNameExtensionFilter("PDF Documents (*.pdf)", "pdf"));

        // Default sanitized file name
        String sanitizedName = profile.getFullName().trim().replaceAll("[^a-zA-Z0-9.-]", "_");
        String defaultName = "Resume_" + sanitizedName + "_" + currentTemplate.getType().name() + ".pdf";
        chooser.setSelectedFile(new File(defaultName));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().endsWith(".pdf")) {
                selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".pdf");
            }

            // Confirm overwrite
            if (selectedFile.exists()) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "File '" + selectedFile.getName() + "' already exists.\nDo you want to overwrite it?",
                        "Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            try {
                PDFExporter exporter = new PDFExporter(profile, currentTemplate);
                exporter.exportToFile(selectedFile);

                int openChoice = JOptionPane.showOptionDialog(this,
                        "Resume exported successfully to:\n" + selectedFile.getAbsolutePath() +
                                "\n\nWould you like to open the generated PDF now?",
                        "Export Successful",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new String[]{"Open PDF", "Done"},
                        "Open PDF");

                if (openChoice == JOptionPane.YES_OPTION) {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                        Desktop.getDesktop().open(selectedFile);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to export PDF: " + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
