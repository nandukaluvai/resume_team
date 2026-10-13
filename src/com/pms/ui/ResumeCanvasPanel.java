package com.pms.ui;

import com.pms.model.Profile;
import com.pms.template.ResumeTemplate;

import javax.swing.*;
import java.awt.*;

/**
 * ResumeCanvasPanel
 * Custom Swing component that renders the resume preview with high-DPI
 * anti-aliasing, page zoom scaling, and a paper drop-shadow effect.
 * 
 * Demonstrates:
 * - Custom component painting (paintComponent)
 * - Graphics2D AffineTransform scaling (zoom)
 * - Rendering Hints for crisp vector-like text
 */
public class ResumeCanvasPanel extends JPanel {

    // Standard A4 aspect ratio at 96 DPI (794 x 1123 pixels)
    public static final int BASE_PAGE_WIDTH = 794;
    public static final int BASE_PAGE_HEIGHT = 1123;

    private Profile profile;
    private ResumeTemplate template;
    private double zoomFactor = 0.90; // Default comfortable preview scale

    public ResumeCanvasPanel(Profile profile, ResumeTemplate template) {
        this.profile = profile;
        this.template = template;
        setBackground(new Color(226, 232, 240)); // Slate desk background
        updateCanvasDimension();
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
        repaint();
    }

    public void setTemplate(ResumeTemplate template) {
        this.template = template;
        repaint();
    }

    public void setZoomFactor(double zoom) {
        this.zoomFactor = Math.max(0.5, Math.min(2.0, zoom));
        updateCanvasDimension();
        revalidate();
        repaint();
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    private void updateCanvasDimension() {
        int scaledWidth = (int) (BASE_PAGE_WIDTH * zoomFactor);
        int scaledHeight = (int) (BASE_PAGE_HEIGHT * zoomFactor);
        int margin = 30;
        Dimension dim = new Dimension(scaledWidth + (2 * margin), scaledHeight + (2 * margin));
        setPreferredSize(dim);
        setMinimumSize(dim);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (profile == null || template == null) return;

        Graphics2D g2 = (Graphics2D) g.create();

        // Enable high quality rendering hints
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        int scaledWidth = (int) (BASE_PAGE_WIDTH * zoomFactor);
        int scaledHeight = (int) (BASE_PAGE_HEIGHT * zoomFactor);

        // Center paper on canvas
        int x = Math.max(25, (getWidth() - scaledWidth) / 2);
        int y = 25;

        // Draw Paper Drop Shadow
        g2.setColor(new Color(0, 0, 0, 35));
        g2.fillRoundRect(x + 5, y + 6, scaledWidth, scaledHeight, 4, 4);
        g2.setColor(new Color(0, 0, 0, 18));
        g2.fillRoundRect(x + 2, y + 3, scaledWidth, scaledHeight, 4, 4);

        // Clip to paper boundaries and render template
        Shape origClip = g2.getClip();
        g2.clipRect(x, y, scaledWidth, scaledHeight);

        // Transform for Zoom
        g2.translate(x, y);
        g2.scale(zoomFactor, zoomFactor);

        // Polymorphic Template Rendering
        template.render(g2, profile, BASE_PAGE_WIDTH, BASE_PAGE_HEIGHT);

        g2.setClip(origClip);

        // Draw subtle border around sheet
        g2.scale(1.0 / zoomFactor, 1.0 / zoomFactor);
        g2.translate(-x, -y);
        g2.setColor(new Color(203, 213, 225));
        g2.drawRect(x, y, scaledWidth, scaledHeight);

        g2.dispose();
    }
}
