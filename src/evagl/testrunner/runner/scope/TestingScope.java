package evagl.testrunner.runner.scope;

public interface TestingScope {
    /**
     * @return argument for -XCTest command line parameter
     */
    String getCommandLineArgument();
}
