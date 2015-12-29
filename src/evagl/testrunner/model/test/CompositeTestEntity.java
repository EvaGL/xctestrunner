package evagl.testrunner.model.test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents testing entity which can have children (i.e. test suite and test case)
 * @param <Child> entity children type
 */
public abstract class CompositeTestEntity<Child extends TestEntity> extends TestEntity {

    private final List<Child> children = new CopyOnWriteArrayList<>();

    protected CompositeTestEntity(TestEntity parent, String name) {
        super(parent, name);
    }

    public void addChild(Child entity) {
        children.add(entity);
    }

    /**
     * @return child by index or null if position is out of bounds
     */
    public Child getChild(int position) {
        try {
            return children.get(position);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public int getChildCount() {
        return children.size();
    }

    /**
     * @return index of child or -1 if it's not a child of this node
     */
    public int getIndexOfChild(TestEntity child) {
        for (int i = 0; i < children.size(); ++i) {
            if (children.get(i).equals(child)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public String getLog() {
        StringBuilder logBuilder = new StringBuilder();
        for (Child child : children) {
            logBuilder.append(child.getLog());
        }
        return logBuilder.toString();
    }

    public void clearChildren() {
        children.clear();
    }
}
