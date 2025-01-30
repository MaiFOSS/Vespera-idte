package org.maifoss.blackholeidte;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private boolean darkTheme = false;

    public TextEditor() {
        setTitle("Black Hole IDTE");
        setSize(600, 400);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        fileChooser = new JFileChooser();

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
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> quitApp());
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // View Menu
        JMenu viewMenu = new JMenu("View");
        JMenuItem themeItem = new JMenuItem("Toggle Theme");
        themeItem.addActionListener(e -> toggleTheme());
        viewMenu.add(themeItem);
        menuBar.add(viewMenu);

        // Help Menu
        JMenu aboutMenu = new JMenu("Help");
        JMenuItem wikiItem = new JMenuItem("Wiki");
        wikiItem.addActionListener(e -> openWikiPage());
        aboutMenu.add(wikiItem);
        JMenuItem aboutItem = new JMenuItem("About");
        aboutMenu.add(aboutItem);
        menuBar.add(aboutMenu);

        setJMenuBar(menuBar);

        // Right-click Menu (Context Menu)
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem cutItem = new JMenuItem("Cut");
        cutItem.addActionListener(e -> textArea.cut());
        popupMenu.add(cutItem);
        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.addActionListener(e -> textArea.copy());
        popupMenu.add(copyItem);
        JMenuItem pasteItem = new JMenuItem("Paste");
        pasteItem.addActionListener(e -> textArea.paste());
        popupMenu.add(pasteItem);

        textArea.setComponentPopupMenu(popupMenu);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                quitApp();
            }
        });

        setVisible(true);
    }

    private void toggleTheme() {
        if (darkTheme) {
            textArea.setBackground(Color.WHITE);
            textArea.setForeground(Color.BLACK);
        } else {
            textArea.setBackground(Color.DARK_GRAY);
            textArea.setForeground(Color.WHITE);
        }
        darkTheme = !darkTheme;
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
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error opening file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean saveFile() {
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                textArea.write(writer);
                return true;
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return false;
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
    }
}
