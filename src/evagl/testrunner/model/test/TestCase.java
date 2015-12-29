package evagl.testrunner.model.test;

import evagl.testrunner.runner.scope.TestCaseScope;
import evagl.testrunner.runner.scope.TestingScope;

public class TestCase extends TestEntity {

    private final StringBuffer logOutput = new StringBuffer();

    TestCase(CompositeTestEntity parent, String name) {
        super(parent, name);
    }

    @Override
    public TestingScope getTestingScope() {
        return new TestCaseScope(getParent().getName(), getName());
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public String getLog() {
        return logOutput.toString();
    }

    /**
     * Appends line to log output of this test case
     * @param logLine line to append
     */
    public void addLogLine(String logLine) {
        logOutput.append(logLine);
    }
}