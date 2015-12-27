package evagl.testrunner.model;

import evagl.testrunner.model.test.CompositeTestEntity;
import evagl.testrunner.model.test.TestBundle;
import evagl.testrunner.model.test.TestEntity;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BundleTreeModel implements TreeModel {

    private final TestBundle bundle;

    private final List<TreeModelListener> listeners = new CopyOnWriteArrayList<>();

    public BundleTreeModel(File bundleFile) {
        bundle = new TestBundle(bundleFile);
    }

    @Override
    public TestBundle getRoot() {
        return bundle;
    }

    @Override
    public TestEntity getChild(Object parent, int index) {
        if (parent instanceof CompositeTestEntity) {
            CompositeTestEntity entity = ((CompositeTestEntity) parent);
            return entity.getChild(index);
        }
        return null;
    }

    @Override
    public int getChildCount(Object o) {
        if (o instanceof CompositeTestEntity) {
            CompositeTestEntity entity = ((CompositeTestEntity) o);
            return entity.getChildCount();
        }
        return 0;
    }

    @Override
    public boolean isLeaf(Object o) {
        return o instanceof TestEntity && ((TestEntity) o).isLeaf();
    }

    @Override
    public void valueForPathChanged(TreePath treePath, Object o) {
        // do nothing
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof CompositeTestEntity && child instanceof TestEntity) {
            return ((CompositeTestEntity) parent).getIndexOfChild((TestEntity) child);
        }
        return -1;
    }

    @Override
    public void addTreeModelListener(TreeModelListener treeModelListener) {
        listeners.add(treeModelListener);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener treeModelListener) {
        listeners.remove(treeModelListener);
    }

    /**
     * Removes all nodes in model except root and notifies all listeners about it
     */
    public void clear() {
        bundle.clearChildren();
        TreeModelEvent event = new TreeModelEvent(bundle, bundle.getTreePath());
        for (TreeModelListener listener : listeners) {
            listener.treeStructureChanged(event);
        }
    }

    /**
     * Notifies all listeners about changes in one node
     * @param entity node that was changed
     */
    public void notifyTreeNodeChanged(TestEntity entity) {
        TreeModelEvent event = new TreeModelEvent(entity, entity.getTreePath());
        for (TreeModelListener listener : listeners) {
            listener.treeNodesChanged(event);
        }
    }

    /**
     * Notifies all listeners about adding one node to parent
     * @param parent parent of added node
     * @param child added node
     * @param <Child> type of added node
     */
    public <Child extends TestEntity> void notifyTreeNodeAdded(CompositeTestEntity<Child> parent, Child child) {
        int index = parent.getIndexOfChild(child);
        TreeModelEvent event = new TreeModelEvent(parent, parent.getTreePath(), new int[]{index}, new Object[]{child});
        for (TreeModelListener listener : listeners) {
            listener.treeNodesInserted(event);
        }
    }
}
