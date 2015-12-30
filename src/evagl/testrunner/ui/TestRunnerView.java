package evagl.testrunner.ui;

import evagl.testrunner.controller.TestRunnerController;
import evagl.testrunner.model.BundleTreeModel;
import evagl.testrunner.model.Statistics;
import evagl.testrunner.model.test.TestEntity;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

import static javax.swing.SwingUtilities.invokeLater;

public class TestRunnerView {

    private final JFrame mainWindow = new JFrame("XCTestRunner");

    private final JTree tree = new JTree();

    private final JTextArea logArea = new JTextArea();

    private final JLabel statsLabel = new JLabel();

    private final JButton stopButton = new JButton(Icons.STOP);

    private final JButton runButton = new JButton(Icons.PLAY);

    private final TestRunnerController controller = new TestRunnerController(this);

    private final TreeMouseListener treeMouseListener = new TreeMouseListener(tree, controller);

    public TestRunnerView() {
        JComponent logAndStatsPanel = initLogAndStatisticsPanel();

        JComponent treeScrollPane = initTreeComponent();

        JToolBar toolBar = initToolbar();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.add(treeScrollPane);
        splitPane.add(logAndStatsPanel);

        mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension size = new Dimension(800, 450);
        mainWindow.setMinimumSize(size);
        mainWindow.setPreferredSize(size);

        Container contentPane = mainWindow.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(toolBar, BorderLayout.NORTH);
        contentPane.add(splitPane, BorderLayout.CENTER);
    }

    private JComponent initLogAndStatisticsPanel() {
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(
                logArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        JPanel logAndStatsPanel = new JPanel();
        logAndStatsPanel.setLayout(new BorderLayout());
        logAndStatsPanel.add(statsLabel, BorderLayout.NORTH);
        logAndStatsPanel.add(logScrollPane, BorderLayout.CENTER);
        return logAndStatsPanel;
    }

    private JComponent initTreeComponent() {
        tree.setModel(new BundleTreeModel(null));
        tree.setCellRenderer(new TestTreeCellRenderer());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(event -> {
            TestEntity entity = (TestEntity) event.getPath().getLastPathComponent();
            logArea.setText(entity.getLog());
        });
        tree.setShowsRootHandles(true);
        JScrollPane treeScrollPane = new JScrollPane(
                tree,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        treeScrollPane.setPreferredSize(new Dimension(200, 450));
        return treeScrollPane;
    }

    private JToolBar initToolbar() {
        runButton.setToolTipText("Run all tests");
        runButton.addActionListener(event -> controller.runTests((TestEntity) tree.getModel().getRoot()));
        runButton.setEnabled(false);

        stopButton.setToolTipText("Stop testing");
        stopButton.addActionListener(event -> controller.stopTests());
        stopButton.setEnabled(false);

        JButton openButton = new JButton(Icons.OPEN);
        openButton.setToolTipText("Open bundle");
        openButton.addActionListener(event -> openNewBundle());

        JButton settingsButton = new JButton(Icons.SETTINGS);
        settingsButton.setToolTipText("Settings");
        settingsButton.addActionListener(event -> showSettingsDialog());

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(openButton);
        toolBar.add(runButton);
        toolBar.add(stopButton);
        toolBar.add(settingsButton);
        return toolBar;
    }

    private void openNewBundle() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XCTest bundle (*.xctest)", "xctest");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(true);
        int ret = fileChooser.showDialog(mainWindow, "Open");
        if (ret == JFileChooser.APPROVE_OPTION) {
            controller.onNewBundle(fileChooser.getSelectedFile());
        }
    }

    private void showSettingsDialog() {
        new SettingsDialog(mainWindow).setVisible(true);
    }

    public void show() {
        invokeLater(() -> mainWindow.setVisible(true));
    }

    public void setBundleTreeModel(BundleTreeModel treeModel) {
        invokeLater(() -> tree.setModel(treeModel));
    }

    public void expandNode(TestEntity entity) {
        invokeLater(() -> tree.expandPath(entity.getTreePath()));
    }

    public void selectNode(TestEntity entity) {
        invokeLater(() -> tree.setSelectionPath(entity.getTreePath()));
    }

    public void setRunStopEnabled(boolean isRunEnabled, boolean isStopEnabled) {
        invokeLater(() -> {
            runButton.setEnabled(isRunEnabled);
            stopButton.setEnabled(isStopEnabled);
            if (isRunEnabled) {
                tree.addMouseListener(treeMouseListener);
            } else {
                tree.removeMouseListener(treeMouseListener);
            }
        });
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(mainWindow, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void updateLogText() {
        invokeLater(() -> {
            TreePath path = tree.getSelectionPath();
            if (path != null) {
                TestEntity entity = (TestEntity) path.getLastPathComponent();
                if (entity != null) {
                    logArea.setText(entity.getLog());
                }
            }
        });
    }

    public void updateStatistics(Statistics statistics) {
        invokeLater(() -> {
            if (statistics.total == 0) {
                statsLabel.setText("");
                return;
            }
            StringBuilder builder = new StringBuilder(statistics.status.toString());
            builder.append(": ");
            builder.append(statistics.passed);
            builder.append(" passed");
            if (statistics.failed != 0) {
                builder.append(", ");
                builder.append(statistics.failed);
                builder.append(" failed");
            }
            builder.append(" from ");
            builder.append(statistics.total);
            builder.append(" tests");
            statsLabel.setText(builder.toString());
        });
    }
}
