package evagl.testrunner.model;

import evagl.testrunner.model.test.TestBundle;
import evagl.testrunner.model.test.TestCase;
import evagl.testrunner.model.test.TestSuite;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BundleTreeModelTest {

    private static final File BUNDLE_FILE = new File("file.bundle");

    private BundleTreeModel model;

    @Before
    public void setUp() {
        model = new BundleTreeModel(BUNDLE_FILE);

        TestSuite suite = model.getRoot().createSuite("first");
        suite.createTestCase("test1");
        suite.createTestCase("test2");
        suite.createTestCase("test3");

        TestSuite anotherSuite = model.getRoot().createSuite("second");
        anotherSuite.createTestCase("test1");
        anotherSuite.createTestCase("test2");
    }


    @Test
    public void modelRootIsBundleWithConstructorFile() {
        TestBundle bundle = model.getRoot();

        assertThat(bundle.getFile(), is(BUNDLE_FILE));
    }

    @Test
    public void getChildCountWorksCorrectly() {
        assertThat(model.getChildCount(model.getRoot()), is(2));
        assertThat(model.getChildCount(model.getRoot().getChild(0)), is(3));
        assertThat(model.getChildCount(model.getRoot().getChild(1).getChild(0)), is(0));
    }

    @Test
    public void getChildWorksCorrectly() {
        assertThat(model.getChild(model.getRoot(), 1), is(model.getRoot().getChild(1)));
        assertThat(model.getChild(model.getRoot(), 55), nullValue());
        assertThat(model.getChild(model.getRoot().getChild(1), 1), is(model.getRoot().getChild(1).getChild(1)));
    }

    @Test
    public void isLeafWorksCorrectly() {
        assertThat(model.isLeaf(model.getRoot()), is(false));
        assertThat(model.isLeaf(model.getRoot().getChild(0)), is(false));
        assertThat(model.isLeaf(model.getRoot().getChild(0).getChild(0)), is(true));
    }

    @Test
    public void getIndexOfChildWorksCorrectly() {
        assertThat(model.getIndexOfChild(model.getRoot(), model.getRoot().getChild(0)), is(0));
        assertThat(model.getIndexOfChild(model.getRoot().getChild(0), model.getRoot().getChild(0).getChild(2)), is(2));
        assertThat(model.getIndexOfChild(model.getRoot(), model.getRoot().getChild(0).getChild(0)), is(-1));
        assertThat(model.getIndexOfChild(model.getRoot().getChild(0), model.getRoot()), is(-1));
    }

    @Test
    public void modelShouldNotifyListenersAboutChanges() {
        TreeModelListener listener = mock(TreeModelListener.class);
        model.addTreeModelListener(listener);

        TestSuite suite = model.getRoot().getChild(0);
        model.notifyTreeNodeChanged(suite);

        ArgumentCaptor<TreeModelEvent> argument = ArgumentCaptor.forClass(TreeModelEvent.class);
        verify(listener).treeNodesChanged(argument.capture());

        TreeModelEvent event = argument.getValue();
        assertThat(event.getChildIndices(), is(new int[0]));
        assertThat(event.getChildren(), nullValue());
        assertThat(event.getTreePath(), is(suite.getTreePath()));
        assertThat(event.getSource(), is(suite));
    }

    @Test
    public void modelShouldNotifyListenersAboutAddedNode() {
        TreeModelListener listener = mock(TreeModelListener.class);
        model.addTreeModelListener(listener);

        TestSuite suite = model.getRoot().getChild(0);
        TestCase testCase = suite.getChild(2);
        model.notifyTreeNodeAdded(suite, testCase);

        ArgumentCaptor<TreeModelEvent> argument = ArgumentCaptor.forClass(TreeModelEvent.class);
        verify(listener).treeNodesInserted(argument.capture());

        TreeModelEvent event = argument.getValue();
        assertThat(event.getChildIndices(), is(new int[]{2}));
        assertThat(event.getChildren(), is(new Object[]{testCase}));
        assertThat(event.getTreePath(), is(suite.getTreePath()));
        assertThat(event.getSource(), is(suite));
    }
}
