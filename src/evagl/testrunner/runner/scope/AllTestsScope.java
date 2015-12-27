package evagl.testrunner.runner.scope;

public final class AllTestsScope implements TestingScope {

    public static final TestingScope INSTANCE = new AllTestsScope();

    private AllTestsScope() { }

    @Override
    public String getCommandLineArgument() {
        return "All";
    }
}
