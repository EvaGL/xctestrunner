package evagl.testrunner.ui;

import evagl.testrunner.settings.Settings;
import evagl.testrunner.settings.SettingsManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class SettingsDialog extends JDialog {

    public SettingsDialog(JFrame parent) {
        super(parent, "Settings", true);
        setContentPane(createDialogUi());
    }

    private JComponent createDialogUi() {
        JLabel pathLabel = new JLabel("Path to XCode: ");
        JTextField pathField = new JTextField(SettingsManager.INSTANCE.loadSettings().getPathToXcode().getPath());
        JButton pathSelection = new JButton(Icons.MORE);
        pathSelection.addActionListener(e -> showFileChooser(pathField));

        JPanel pathPanel = new JPanel(new BorderLayout());
        pathPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        pathPanel.add(pathLabel, BorderLayout.WEST);
        pathPanel.add(pathField, BorderLayout.CENTER);
        pathPanel.add(pathSelection, BorderLayout.EAST);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            SettingsManager.INSTANCE.saveSettings(new Settings(new File(pathField.getText())));
            dispose();
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonsSubPanel = new JPanel();
        buttonsSubPanel.add(okButton);
        buttonsSubPanel.add(cancelButton);

        JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.add(buttonsSubPanel, BorderLayout.EAST);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        panel.add(pathPanel, BorderLayout.CENTER);
        Dimension size = new Dimension(400, 110);
        setMinimumSize(size);
        setMaximumSize(size);
        return panel;
    }

    private void showFileChooser(JTextField textField) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Applications", "app");
        chooser.setCurrentDirectory(new File(textField.getText()).getParentFile());
        chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
        chooser.addChoosableFileFilter(filter);
        chooser.setFileFilter(filter);
        if (chooser.showDialog(SettingsDialog.this, "Select") == JFileChooser.APPROVE_OPTION) {
            textField.setText(chooser.getSelectedFile().getPath());
        }
    }

}
