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

    private TestRunnerHandler runnerHandler;

    private File pathToBundle;

    public TestRunnerController(TestRunnerView view) {
        this.view = view;
    }

    public void onNewBundle(File bundle) {
        pathToBundle = bundle;
        runTests(null);
    }

    public void runTests(TestEntity entity) {
        if (runnerHandler != null) {
            runnerHandler.stop(true);
            runnerHandler = null;
        }

        BundleTreeModel treeModel = new BundleTreeModel(pathToBundle);
        if (entity == null) {
            entity = treeModel.getRoot();
        }
        runnerHandler = new TestRunnerHandler(treeModel, view, entity);
        runnerHandler.run();
    }

    public void stopTests() {
        if (runnerHandler != null) {
            runnerHandler.stop(false);
            runnerHandler = null;
        }
    }
}
