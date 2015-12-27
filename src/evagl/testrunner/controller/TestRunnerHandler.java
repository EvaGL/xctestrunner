package evagl.testrunner.controller;

import evagl.testrunner.model.BundleTreeModel;
import evagl.testrunner.model.Statistics;
import evagl.testrunner.model.Status;
import evagl.testrunner.model.test.CompositeTestEntity;
import evagl.testrunner.model.test.TestEntity;
import evagl.testrunner.runner.TestRunner;
import evagl.testrunner.state.TestingStateBuilder;
import evagl.testrunner.state.TestingStateListener;
import evagl.testrunner.ui.TestRunnerView;

import java.io.IOException;

/**
 * Handles running and halting testing process and notifies view about changes
 */
public class TestRunnerHandler implements TestingStateListener {

    private final BundleTreeModel treeModel;

    private final TestRunnerView view;

    private final TestRunner runner;

    private volatile boolean isStopped = false;

    public TestRunnerHandler(BundleTreeModel treeModel, TestRunnerView view, TestEntity entity) {
        this.treeModel = treeModel;
        this.view = view;
        this.runner = new TestRunner(treeModel.getRoot().getFile(),
                entity.getTestingScope(), new TestingStateBuilder(treeModel.getRoot(), this));
    }

    /**
     * Runs testing process
     */
    public void run() {
        treeModel.clear();
        view.selectNode(treeModel.getRoot());
        view.setRunEnabled(false);
        view.setStopEnabled(true);

        try {
            runner.run();
        } catch (IOException e) {
            treeModel.getRoot().setStatus(Status.FAILED);
            view.setRunEnabled(true);
            view.setStopEnabled(false);
            view.showErrorMessage("Can't start xctest process. Check if XCode installed");
        }
    }

    /**
     * Stops testing process
     */
    public void stop() {
        runner.stop();
    }

    /**
     * Stops notification to the view about changes in testing state
     */
    public void stopNotifying() {
        isStopped = true;
    }

    @Override
    public void onTreeNodeChanged(TestEntity entity) {
        if (isStopped) {
            return;
        }
        treeModel.notifyTreeNodeChanged(entity);
    }

    @Override
    public <C extends TestEntity> void onTreeNodeAdded(CompositeTestEntity<C> parent, C child) {
        if (isStopped) {
            return;
        }
        treeModel.notifyTreeNodeAdded(parent, child);
        view.expandNode(child);
    }

    @Override
    public void onLogChanged() {
        if (isStopped) {
            return;
        }
        view.updateLogText();
    }

    @Override
    public void onStatisticsUpdated(Statistics newStatistics) {
        if (isStopped) {
            return;
        }
        view.updateStatistics(newStatistics);
    }

    @Override
    public void onFailure(String message) {
        if (isStopped) {
            return;
        }
        view.setRunEnabled(false);
        view.setStopEnabled(false);
        view.showErrorMessage(message);
    }

    @Override
    public void onFinish() {
        if (isStopped) {
            return;
        }
        view.setRunEnabled(true);
        view.setStopEnabled(false);
    }
}