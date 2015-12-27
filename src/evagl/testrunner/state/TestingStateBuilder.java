package evagl.testrunner.state;

import evagl.testrunner.model.*;
import evagl.testrunner.model.test.TestBundle;
import evagl.testrunner.model.test.TestCase;
import evagl.testrunner.model.test.TestSuite;
import evagl.testrunner.parser.TestingOutputListener;
import evagl.testrunner.parser.TestingOutputParser;

/**
 * Listens events by {@link TestingOutputParser},
 * builds internal representation of all tests and builds testing statistics
 */
public class TestingStateBuilder implements TestingOutputListener {

    private static final String ALL_TESTS = "All tests";

    private static final String SELECTED_TESTS = "Selected tests";

    private final TestBundle testBundle;

    private final TestingStateListener listener;

    private TestSuite currentSuite;

    private TestCase currentTestCase;

    private Statistics statistics = Statistics.emptyStats();

    public TestingStateBuilder(TestBundle bundle, TestingStateListener listener) {
        this.testBundle = bundle;
        this.listener = listener;
    }

    @Override
    public void onTestSuiteStarted(String suite) {
        if (isBundle(suite)) {
            testBundle.setStatus(Status.RUNNING);
            listener.onTreeNodeChanged(testBundle);
            return;
        }
        if (currentSuite != null) {
            return;
        }
        currentSuite = testBundle.createSuite(suite);

        listener.onTreeNodeAdded(testBundle, currentSuite);
    }

    @Override
    public void onTestSuitePassed(String suite) {
        onSuiteEnd(suite, Status.PASSED);
    }

    @Override
    public void onTestSuiteFailed(String suite) {
        onSuiteEnd(suite, Status.FAILED);
    }

    private void onSuiteEnd(String suite, Status status) {
        if (isBundle(suite)) {
            testBundle.setStatus(status);
            listener.onTreeNodeChanged(testBundle);

            statistics = statistics.newStatus(status);
            listener.onStatisticsUpdated(statistics);
            return;
        }
        if (currentSuite == null || !currentSuite.getName().equals(suite)) {
            return;
        }
        currentSuite.setStatus(status);
        listener.onTreeNodeChanged(currentSuite);

        currentSuite = null;
    }

    @Override
    public void onTestCaseStarted(String suite, String test) {
        if (currentSuite == null || !currentSuite.getName().equals(suite) || currentTestCase != null) {
            return;
        }
        currentTestCase = currentSuite.createTestCase(test);

        listener.onTreeNodeAdded(currentSuite, currentTestCase);

        statistics = statistics.newTestCase();
        listener.onStatisticsUpdated(statistics);
    }

    @Override
    public void onTestCasePassed(String suite, String test) {
        onTestCaseEnd(suite, test, Status.PASSED);

        statistics = statistics.newPassedTestCase();
        listener.onStatisticsUpdated(statistics);
    }

    @Override
    public void onTestCaseFailed(String suite, String test) {
        onTestCaseEnd(suite, test, Status.FAILED);

        statistics = statistics.newFailedTestCase();
        listener.onStatisticsUpdated(statistics);
    }

    private void onTestCaseEnd(String suite, String test, Status status) {
        if (currentSuite == null || currentTestCase == null
                || !currentSuite.getName().equals(suite)
                || !currentTestCase.getName().equals(test)) {
            return;
        }
        currentTestCase.setStatus(status);

        listener.onTreeNodeChanged(currentTestCase);
        currentTestCase = null;
    }

    @Override
    public void onLogLine(String line) {
        if (currentTestCase == null) {
            return;
        }
        currentTestCase.addLogLine(line);
        listener.onLogChanged();
    }

    @Override
    public void onParsingFinished() {
        setStatusToRunningEntities(Status.CANCELED);
        listener.onFinish();
    }

    @Override
    public void onFailure(String message) {
        setStatusToRunningEntities(Status.FAILED);
        listener.onFailure(message);
    }

    private void setStatusToRunningEntities(Status status) {
        if (currentTestCase != null) {
            currentTestCase.setStatus(status);
            listener.onTreeNodeChanged(currentTestCase);
            currentTestCase = null;
        }
        if (currentSuite != null) {
            currentSuite.setStatus(status);
            listener.onTreeNodeChanged(currentSuite);
            currentSuite = null;
        }
        if (testBundle.getStatus() == Status.RUNNING) {
            testBundle.setStatus(status);
            listener.onTreeNodeChanged(testBundle);
        }

        statistics = statistics.newStatus(testBundle.getStatus());
        listener.onStatisticsUpdated(statistics);
    }

    private boolean isBundle(String suiteName) {
        return ALL_TESTS.equals(suiteName)
                || SELECTED_TESTS.equals(suiteName)
                || suiteName.equals(testBundle.getName());
    }
}
