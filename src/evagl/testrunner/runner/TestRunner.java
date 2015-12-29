package evagl.testrunner.runner;

import evagl.testrunner.settings.Settings;
import evagl.testrunner.parser.TestingOutputParser;
import evagl.testrunner.parser.TestingOutputListener;
import evagl.testrunner.runner.scope.TestingScope;
import evagl.testrunner.settings.SettingsManager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Encapsulates creating, execution and stopping of testing process.
 */
public class TestRunner {
    private static final Executor parserExecutor = Executors.newSingleThreadExecutor();

    private static final String PATH_TO_XCTEST = "Contents/Developer/usr/bin/xctest";

    private final ProcessBuilder processBuilder;

    private final TestingOutputListener listener;

    private Process process;

    public TestRunner(File testBundle, TestingScope scope, TestingOutputListener listener) {
        Settings settings = SettingsManager.loadSettings();
        processBuilder = new ProcessBuilder(
                new File(settings.getPathToXcode(), PATH_TO_XCTEST).getPath(),
                "-XCTest",
                scope.getCommandLineArgument(),
                testBundle.getAbsolutePath()
        ).redirectErrorStream(true);
        this.listener = listener;
    }

    /**
     * Starts testing process
     * When testing is started {@link TestingOutputParser} which listens to the running process is created.
     *
     * All parsers are executed on separated thread created for them exclusively.
     *
     * @throws IllegalStateException if process was already started
     * @throws IOException if process can't be started
     */
    public void run() throws IOException {
        if (process != null) {
            throw new IllegalStateException("Process was already started");
        }
        process = processBuilder.start();

        TestingOutputParser parser = new TestingOutputParser(process.getInputStream(), listener);
        parserExecutor.execute(parser::parse);
    }

    /**
     * Stops testing process.
     * Notice that testing will not stop immediately, so listener can be notified about
     * events even after that
     *
     * @throws IllegalStateException if process wasn't started yet
     */
    public void stop() {
        if (process == null) {
            throw new IllegalStateException("Process wasn't started yet");
        }

        process.destroyForcibly();
    }

}
