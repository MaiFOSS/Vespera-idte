package org.maifoss.blackholeidte;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TextEditor::new);
    }
}

class TextEditor extends JFrame {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private Color backgroundColor = new Color(0, 0, 0); // Default background color
    private Color foregroundColor = new Color(127, 255, 0); // Default text color
    private String selectedTheme = "System";  // Default theme is System
    private JTextArea filePathArea;  // To show the file path

    public TextEditor() {
        setTitle("Black Hole IDTE");
        setSize(600, 400);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setBackground(backgroundColor);
        textArea.setForeground(foregroundColor);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        fileChooser = new JFileChooser();

        // Bottom toolbar with file path display
        JPanel bottomToolbar = new JPanel();
        bottomToolbar.setLayout(new BorderLayout());

        // Create a JTextArea for the file path with scrolling capabilities
        filePathArea = new JTextArea(1, 40);  // Single line, long enough for file paths
        filePathArea.setText("Unsaved File");  // Default text
        filePathArea.setEditable(false);  // Set to non-editable
        filePathArea.setBackground(backgroundColor);  // Light background color for contrast
        filePathArea.setForeground(foregroundColor);  // Black text color

        // Add the JTextArea inside a JScrollPane for scrolling
        JScrollPane scrollPane = new JScrollPane(filePathArea);
        bottomToolbar.add(scrollPane, BorderLayout.CENTER);

        this.add(bottomToolbar, BorderLayout.SOUTH);

        // Apply the selected theme after components are fully initialized
        applyTheme(selectedTheme);

        // Adding menu bar
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(e -> openFile());
        fileMenu.add(openItem);
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> saveFile());
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        JMenu settingItem = new JMenu("Settings...");
        JMenuItem themeSubItem = new JMenuItem("Theme & Colors");
        themeSubItem.addActionListener(e -> themePreferences());
        settingItem.add(themeSubItem);
        fileMenu.add(settingItem);
        menuBar.add(fileMenu);
        fileMenu.addSeparator();
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> quitApp());
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // View Menu
        JMenu viewMenu = new JMenu("View");
        JMenuItem preferenceItem = new JMenuItem("Preferences");
        preferenceItem.addActionListener(e -> themePreferences());
        viewMenu.add(preferenceItem);
        menuBar.add(viewMenu);

        // Run Menu
        JMenu runMenu = new JMenu("Run");
        menuBar.add(runMenu);

        // Help Menu
        JMenu aboutMenu = new JMenu("Help");
        JMenuItem wikiItem = new JMenuItem("Wiki");
        wikiItem.addActionListener(e -> openWikiPage());
        aboutMenu.add(wikiItem);
        JMenuItem aboutItem = new JMenuItem("About");
        aboutMenu.add(aboutItem);
        menuBar.add(aboutMenu);

        setJMenuBar(menuBar);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                quitApp();
            }
        });

        setVisible(true);
    }

    private void themePreferences() {
        // Create a panel with a GridLayout for the settings
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Dropdown for theme selection
        String[] themes = {"System", "Metal", "Nimbus"};
        JComboBox<String> themeComboBox = new JComboBox<>(themes);
        themeComboBox.setSelectedItem(selectedTheme);
        themeComboBox.addActionListener(e -> {
            selectedTheme = (String) themeComboBox.getSelectedItem();
            applyTheme(selectedTheme);
        });
        panel.add(createLabeledComponent("Select Theme:", themeComboBox));

        // Color pickers for background and foreground
        JColorChooser bgColorChooser = new JColorChooser(backgroundColor);
        JColorChooser fgColorChooser = new JColorChooser(foregroundColor);

        JPanel colorPanel = new JPanel(new GridLayout(2, 1));
        colorPanel.add(createLabeledComponent("Background Color:", bgColorChooser));
        colorPanel.add(createLabeledComponent("Text Color:", fgColorChooser));
        panel.add(colorPanel);

        // Preview area
        JPanel previewPanel = new JPanel();
        previewPanel.setBackground(backgroundColor);
        previewPanel.setForeground(foregroundColor);
        JLabel previewLabel = new JLabel("Preview: System.out.println(\"Hello World!\");");
        previewPanel.add(previewLabel);
        previewLabel.setForeground(foregroundColor);
        previewPanel.setBackground(backgroundColor);

        panel.add(previewPanel);

        // Add listeners to update the preview
        bgColorChooser.getSelectionModel().addChangeListener(e -> {
            Color selectedBgColor = bgColorChooser.getColor();
            previewPanel.setBackground(selectedBgColor);
            backgroundColor = selectedBgColor;
        });

        fgColorChooser.getSelectionModel().addChangeListener(e -> {
            Color selectedFgColor = fgColorChooser.getColor();
            previewLabel.setForeground(selectedFgColor);
            foregroundColor = selectedFgColor;
        });

        // Buttons: Apply & Cancel
        JPanel buttonPanel = new JPanel();
        JButton applyButton = new JButton("Apply");
        JButton cancelButton = new JButton("Cancel");

        applyButton.addActionListener(e -> {
            textArea.setBackground(backgroundColor);
            textArea.setForeground(foregroundColor);
            applyTheme(selectedTheme);
        });

        cancelButton.addActionListener(e -> {
            // Reset to previous settings (i.e., no change)
            bgColorChooser.setColor(backgroundColor);
            fgColorChooser.setColor(foregroundColor);
            themeComboBox.setSelectedItem(selectedTheme);
        });

        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        // Show the dialog
        JOptionPane.showMessageDialog(this, panel, "Preferences", JOptionPane.PLAIN_MESSAGE);
    }

    private JPanel createLabeledComponent(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(label), BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private void applyTheme(String theme) {
        try {
            switch (theme) {
                case "Metal":
                    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                    break;
                case "Nimbus":
                    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                    break;
                case "System":
                default:
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    break;
            }
            // Ensure a default font is set for all components to prevent NullPointerException
            Font defaultFont = new Font("Arial", Font.PLAIN, 12);
            UIManager.put("Label.font", defaultFont);
            UIManager.put("Button.font", defaultFont);
            UIManager.put("TextArea.font", defaultFont);
            UIManager.put("TextField.font", defaultFont);
            UIManager.put("ComboBox.font", defaultFont);
            SwingUtilities.updateComponentTreeUI(this); // Refresh the UI to apply the new theme and font

            // Set the theme for the bottom one
            filePathArea.setBackground(backgroundColor);
            filePathArea.setForeground(foregroundColor);

        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private void openWikiPage() {
        String url = "https://github.com/MaiFOSS/black-hole-idte/wiki";
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                String os = System.getProperty("os.name").toLowerCase();
                Runtime rt = Runtime.getRuntime();
                if (os.contains("win")) {
                    rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
                } else if (os.contains("mac")) {
                    rt.exec("open " + url);
                } else {
                    rt.exec("xdg-open " + url);
                }
            }
        } catch (IOException | URISyntaxException e) {
            JOptionPane.showMessageDialog(this, "Failed to open Wiki page.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openFile() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                textArea.read(reader, null);
                filePathArea.setText(file.getAbsolutePath());  // Display the file path in the bottom toolbar
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error opening file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean saveFile() {
        if (fileChooser.getSelectedFile() == null) {
            int returnValue = fileChooser.showSaveDialog(this);
            if (returnValue != JFileChooser.APPROVE_OPTION) {
                return false; // User canceled save
            }
        }

        File file = fileChooser.getSelectedFile();
        if (file == null) {
            return false; // No file chosen
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            textArea.write(writer);
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void quitApp() {
        int response = JOptionPane.showConfirmDialog(this, "Do you want to save before quitting?", "Confirm Exit", JOptionPane.YES_NO_CANCEL_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            if (saveFile()) {
                System.exit(0);
            }
        } else if (response == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
        // If CANCEL is clicked, do nothing (prevents accidental exit)
    }
}
