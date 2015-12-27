package evagl.testrunner.ui;

import evagl.testrunner.controller.TestRunnerController;
import evagl.testrunner.model.test.TestEntity;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TreeMouseListener extends MouseAdapter {

    private final JTree tree;

    private final TestRunnerController controller;

    public TreeMouseListener(JTree tree, TestRunnerController controller) {
        this.tree = tree;
        this.controller = controller;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        TreePath path = tree.getPathForLocation(e.getX(), e.getY());
        if (path == null) {
            return;
        }
        TestEntity entity = (TestEntity) path.getLastPathComponent();
        if (entity == null) {
            return;
        }
        if (SwingUtilities.isRightMouseButton(e)) {
            tree.setSelectionPath(path);
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem run = new JMenuItem("Run", Icons.PLAY);
            run.addActionListener(event -> controller.runTests(entity));
            popupMenu.add(run);
            popupMenu.show(tree, e.getX(), e.getY());
        }
    }
}
