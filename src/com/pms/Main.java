package com.pms;

import com.pms.ui.HomeFrame;
import com.pms.ui.UITheme;

import javax.swing.*;

/**
 * Main Application Launcher
 * 
 * Profile Management System for Resume/CV Generation
 * Academic Mini-Project for B.Tech Computer Science & Engineering
 * 
 * Demonstrated Core Java Concepts:
 * 1. Object-Oriented Programming (OOP):
 *    - Encapsulation: Profile model with getters, setters, and validation.
 *    - Inheritance & Polymorphism: ResumeTemplate abstract class implemented
 *      by 6 specialized template subclasses (Professional, Modern, Simple,
 *      Student/Fresher, Technical, Creative).
 * 2. Collections Framework:
 *    - ArrayList and List for managing candidate profiles and templates.
 * 3. File Handling & Serialization:
 *    - Java Object Serialization (ObjectOutputStream / ObjectInputStream).
 *    - Corrupted file backup and graceful recovery.
 * 4. Exception Handling:
 *    - Custom exceptions (ValidationException, DataStorageException).
 *    - Robust try-catch-finally and resource management.
 * 5. Swing GUI & Event Handling:
 *    - Event Dispatch Thread (SwingUtilities.invokeLater).
 *    - ActionListeners, DocumentListeners, ItemListeners, MouseAdapters.
 *    - 2D Canvas Graphics rendering with anti-aliasing and scaling.
 * 6. PDF Export:
 *    - Vector PDF generation using Apache PDFBox library.
 */
public class Main {
    public static void main(String[] args) {
        // Apply modern system look and feel
        UITheme.setupSystemLookAndFeel();

        // Safely start application on the Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                HomeFrame homeFrame = new HomeFrame();
                homeFrame.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Fatal error starting Profile Management System: " + e.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
