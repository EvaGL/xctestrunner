package evagl.testrunner.runner.scope;

public class SuiteScope implements TestingScope {

    private final String suiteName;

    public SuiteScope(String suiteName) {
        this.suiteName = suiteName;
    }

    @Override
    public String getCommandLineArgument() {
        return suiteName;
    }
}
