package evagl.testrunner.runner.scope;

public class TestCaseScope implements TestingScope {

    private final String suiteName;

    private final String testCaseName;

    public TestCaseScope(String suiteName, String testCaseName) {
        this.suiteName = suiteName;
        this.testCaseName = testCaseName;
    }

    @Override
    public String getCommandLineArgument() {
        return suiteName + "/" + testCaseName;
    }
}
