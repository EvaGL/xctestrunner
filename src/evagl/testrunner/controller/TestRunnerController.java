package evagl.testrunner.controller;

import evagl.testrunner.model.BundleTreeModel;
import evagl.testrunner.model.test.TestEntity;
import evagl.testrunner.ui.TestRunnerView;

import java.io.File;

/**
 * Handles view requests to run/stop testing process
 */
public class TestRunnerController {

    private final TestRunnerView view;

    private BundleTreeModel treeModel;

    private TestRunnerHandler runnerHandler;

    public TestRunnerController(TestRunnerView view) {
        this.view = view;
    }

    public void onNewBundle(File bundle) {
        treeModel = new BundleTreeModel(bundle);
        view.setBundleTreeModel(treeModel);
        runTests(treeModel.getRoot());
    }

    public void runTests(TestEntity entity) {
        if (runnerHandler != null) {
            runnerHandler.stopNotifying();
            runnerHandler = null;
        }
        stopTests();

        runnerHandler = new TestRunnerHandler(treeModel, view, entity);
        runnerHandler.run();
    }

    public void stopTests() {
        if (runnerHandler != null) {
            runnerHandler.stop();
            runnerHandler = null;
        }
    }
}
