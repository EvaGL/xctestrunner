package evagl.testrunner.ui;

import evagl.testrunner.model.test.TestEntity;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class TestTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        TestEntity entity = ((TestEntity) value);
        setText(entity.getName());
        setIcon(entity.getStatus().getIcon());
        return this;
    }
}
