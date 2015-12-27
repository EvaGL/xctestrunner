package evagl.testrunner.state;

import evagl.testrunner.model.Statistics;
import evagl.testrunner.model.Status;
import evagl.testrunner.model.test.TestBundle;
import evagl.testrunner.model.test.TestCase;
import evagl.testrunner.model.test.TestSuite;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TestingStateBuilderTest {

    protected static final String BUNDLE_NAME = "bundle.xctest";

    protected TestingStateBuilder builder;

    protected TestBundle root;

    protected TestingStateListener listener;

    @Before
    public void setUp() {
        root = new TestBundle(new File(BUNDLE_NAME));
        listener = mock(TestingStateListener.class);
        builder = new TestingStateBuilder(root, listener);
    }

    private void goInsideTestCase(String suite, String testCase) {
        builder.onTestSuiteStarted(suite);
        builder.onTestCaseStarted(suite, testCase);
    }

    @Test
    public void builderShouldIgnoreSpecialSuites() {
        builder.onTestSuiteStarted("All tests");
        builder.onTestSuiteStarted("Selected tests");
        assertThat(root.getChildCount(), is(0));
    }

    @Test
    public void bundleSuiteStatuses() {
        builder.onTestSuiteStarted(BUNDLE_NAME);
        assertThat(root.getStatus(), is(Status.RUNNING));
        builder.onTestSuitePassed(BUNDLE_NAME);
        assertThat(root.getStatus(), is(Status.PASSED));
    }

    @Test
    public void startAnotherSuite() {
        builder.onTestSuiteStarted("a");
        assertThat(root.getChildCount(), is(1));
        TestSuite suite = root.getChild(0);
        assertThat(suite.getName(), is("a"));
        assertThat(suite.getStatus(), is(Status.RUNNING));
        assertThat(suite.getChildCount(), is(0));
    }

    @Test
    public void startTestCase() {
        builder.onTestSuiteStarted("a");
        builder.onTestCaseStarted("a", "b");
        TestSuite suite = root.getChild(0);
        assertThat(suite.getChildCount(), is(1));
        TestCase test = suite.getChild(0);
        assertThat(test.getName(), is("b"));
        assertThat(test.getStatus(), is(Status.RUNNING));
    }

    @Test
    public void finishTestCase() {
        goInsideTestCase("a", "b");

        builder.onTestCasePassed("a", "b");
        assertThat(root.getChild(0).getChild(0).getStatus(), is(Status.PASSED));
        builder.onTestSuiteFailed("a");
        assertThat(root.getChild(0).getStatus(), is(Status.FAILED));
    }

    @Test
    public void severalTestSuites() {
        goInsideTestCase("a", "b");

        builder.onTestCaseFailed("a", "b");
        builder.onTestSuiteFailed("a");
        builder.onTestSuiteStarted("c");
        assertThat(root.getChildCount(), is(2));
        assertThat(root.getChild(0).getName(), is("a"));
        assertThat(root.getChild(1).getName(), is("c"));
    }

    @Test
    public void logLineWithinTestCase() {
        goInsideTestCase("a", "b");

        builder.onLogLine("log");
        assertThat(root.getChild(0).getChild(0).getLog(), is("log\n"));
    }

    @Test
    public void finishShouldSetStatusToCanceled() {
        goInsideTestCase("a", "b");
        builder.onParsingFinished();

        assertThat(root.getStatus(), is(Status.CANCELED));
        assertThat(root.getChild(0).getStatus(), is(Status.CANCELED));
        assertThat(root.getChild(0).getChild(0).getStatus(), is(Status.CANCELED));
    }

    @Test
    public void failureShouldSetStatusToFailed() {
        builder.onFailure("aaa");

        assertThat(root.getStatus(), is(Status.FAILED));
    }

    @Test
    public void notifyAboutAdditionAndModification() {
        goInsideTestCase("a", "b");
        verify(listener).onTreeNodeAdded(root, root.getChild(0));
        verify(listener).onTreeNodeAdded(root.getChild(0), root.getChild(0).getChild(0));
        builder.onTestCasePassed("a", "b");
        verify(listener).onTreeNodeChanged(root.getChild(0).getChild(0));
        builder.onTestSuiteFailed("a");
        verify(listener).onTreeNodeChanged(root.getChild(0));
    }

    @Test
    public void notifyAboutFinish() {
        builder.onParsingFinished();

        verify(listener).onFinish();
    }

    @Test
    public void notifyAboutFailure() {
        builder.onFailure("aaa");

        verify(listener).onFailure("aaa");
    }

    @Test
    public void notifyAboutLogChange() {
        goInsideTestCase("a", "b");

        builder.onLogLine("line");
        verify(listener).onLogChanged();
    }

    @Test
    public void notifyAboutStatisticsChange() {
        Statistics statistics = Statistics.emptyStats();

        goInsideTestCase("a", "b");
        statistics = statistics.newTestCase();
        verify(listener).onStatisticsUpdated(statistics);

        builder.onTestCasePassed("a", "b");
        statistics = statistics.newPassedTestCase();
        verify(listener).onStatisticsUpdated(statistics);

        builder.onTestCaseStarted("a", "c");
        statistics = statistics.newTestCase();
        verify(listener).onStatisticsUpdated(statistics);


        builder.onTestCaseFailed("a", "b");
        statistics = statistics.newFailedTestCase();
        verify(listener).onStatisticsUpdated(statistics);

        builder.onTestSuiteFailed("a");
        builder.onTestSuiteFailed(BUNDLE_NAME);
        statistics = statistics.newStatus(Status.FAILED);
        verify(listener).onStatisticsUpdated(statistics);
    }
}
