package evagl.testrunner.parser;

/**
 * Listener for events produced by {@link TestingOutputParser}
 */
public interface TestingOutputListener {
    void onTestSuiteStarted(String suite);
    void onTestSuitePassed(String suite);
    void onTestSuiteFailed(String suite);

    void onTestCaseStarted(String suite, String test);
    void onTestCasePassed(String suite, String test);
    void onTestCaseFailed(String suite, String test);

    void onLogLine(String line);

    void onParsingFinished();
    void onFailure(String message);
}
