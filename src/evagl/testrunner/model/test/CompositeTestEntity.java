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

    public Child getChild(int position) {
        return children.get(position);
    }

    public int getChildCount() {
        return children.size();
    }

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
