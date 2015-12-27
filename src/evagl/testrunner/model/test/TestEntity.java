package evagl.testrunner.model.test;

import evagl.testrunner.model.Status;
import evagl.testrunner.runner.scope.TestingScope;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Root class for every item representing information about some test entity like test case or test suite.
 * Entities form tree
 */
public abstract class TestEntity {
    private final TestEntity parent;

    private final String name;

    private Status status = Status.RUNNING;

    protected TestEntity(TestEntity parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public abstract TestingScope getTestingScope();

    /**
     * @return true is this node can have children (see {@link javax.swing.tree.TreeModel#isLeaf(Object)})
     */
    public abstract boolean isLeaf();

    /**
     * @return log output of this node and its children
     */
    public abstract String getLog();

    public String getName() {
        return name;
    }

    public TestEntity getParent() {
        return parent;
    }

    public TreePath getTreePath() {
        List<TestEntity> path = new ArrayList<>(3);
        TestEntity current = this;
        while (current != null) {
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);
        return new TreePath(path.toArray());
    }

}
