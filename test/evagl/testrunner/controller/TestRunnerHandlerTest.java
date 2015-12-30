package evagl.testrunner.controller;

import evagl.testrunner.model.BundleTreeModel;
import evagl.testrunner.model.Statistics;
import evagl.testrunner.model.test.TestBundle;
import evagl.testrunner.model.test.TestSuite;
import evagl.testrunner.ui.TestRunnerView;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.mockito.Mockito.*;

public class TestRunnerHandlerTest {

    private TestRunnerHandler handler;

    private TestRunnerView view;

    private BundleTreeModel model;

    private TestBundle root;

    @Before
    public void setUp() {
        view = mock(TestRunnerView.class);
        model = mock(BundleTreeModel.class);
        root = new TestBundle(new File("bundle"));
        when(model.getRoot()).thenReturn(root);
        handler = new TestRunnerHandler(model, view, root);
    }

    @Test
    public void failureShouldBePassedToView() {
        handler.onFailure("Message", false);

        verify(view).showErrorMessage("Message");
        verify(view).setRunStopEnabled(false, false);
    }

    @Test
    public void statsUpdatesShouldBePassedToView() {
        Statistics statistics = Statistics.emptyStats();
        handler.onStatisticsUpdated(statistics);

        verify(view).updateStatistics(statistics);
    }

    @Test
    public void treeChangesShouldBePassedToModel() {
        TestSuite suite = root.createSuite("suite");
        handler.onTreeNodeAdded(root, suite);
        verify(model).notifyTreeNodeAdded(root, suite);

        handler.onTreeNodeChanged(root);
        verify(model).notifyTreeNodeChanged(root);
    }

    @Test
    public void logChangesShouldBePassedToView() {
        handler.onLogChanged();

        verify(view).updateLogText();
    }

    @Test
    public void noEventsAfterStopNotifying() {
        handler.stopNotifying();
        handler.onFailure("Message", false);
        handler.onFinish();
        handler.onLogChanged();
        handler.onStatisticsUpdated(null);
        handler.onTreeNodeAdded(null, null);
        handler.onTreeNodeChanged(null);

        verifyZeroInteractions(view);
        verify(model, never()).notifyTreeNodeAdded(any(), any());
        verify(model, never()).notifyTreeNodeChanged(any());
    }
}
