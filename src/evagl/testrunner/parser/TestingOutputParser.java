package evagl.testrunner.parser;

import evagl.testrunner.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestingOutputParser {

    private static final Pattern SUITE_PATTERN = Pattern.compile("(.*)Test Suite '([^']+)' (\\w+) at [\\d\\s:\\-.]+$");

    private static final Pattern CASE_PATTERN = Pattern.compile("(.*)Test Case '-\\[([^'\\s]+) ([^']+)\\]' (\\w+)( \\([^)]+\\))?\\.$");

    private static final String PASSED = "passed";

    private static final String FAILED = "failed";

    private static final String STARTED = "started";

    private static final String FAILURE = "Failure: ";

    private final InputStream stream;

    private final TestingOutputListener listener;

    /**
     * Parses given steam and produces events during it.
     *
     * @param stream stream to parse
     * @param listener listener which listens to parsing event
     */
    public TestingOutputParser(InputStream stream, TestingOutputListener listener) {
        this.stream = stream;
        this.listener = listener;
    }

    public void parse() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        boolean isStatsLine = false;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(FAILURE)) {
                    String failureMessage = line.substring(FAILURE.length());
                    listener.onFailure(failureMessage);
                    return;
                }

                Matcher suiteMatcher = SUITE_PATTERN.matcher(line);
                if (suiteMatcher.matches()) {
                    String log = suiteMatcher.group(1);
                    String suiteName = suiteMatcher.group(2);
                    if (!log.isEmpty()) {
                        listener.onLogLine(log);
                    }
                    switch (suiteMatcher.group(3)) {
                        case STARTED:
                            listener.onTestSuiteStarted(suiteName);
                            continue;
                        case PASSED:
                            listener.onTestSuitePassed(suiteName);
                            isStatsLine = true;
                            continue;
                        case FAILED:
                            listener.onTestSuiteFailed(suiteName);
                            isStatsLine = true;
                            continue;
                    }
                }

                Matcher caseMatcher = CASE_PATTERN.matcher(line);
                if (caseMatcher.matches()) {
                    String log = caseMatcher.group(1);
                    if (!log.isEmpty()) {
                        listener.onLogLine(log);
                    }
                    String suiteName = caseMatcher.group(2);
                    String testName = caseMatcher.group(3);
                    switch (caseMatcher.group(4)) {
                        case STARTED:
                            listener.onTestCaseStarted(suiteName, testName);
                            continue;
                        case PASSED:
                            listener.onTestCasePassed(suiteName, testName);
                            continue;
                        case FAILED:
                            listener.onTestCaseFailed(suiteName, testName);
                            continue;
                    }
                }

                if (isStatsLine) {
                    isStatsLine = false;
                } else {
                    listener.onLogLine(line + '\n');
                }
            }
            listener.onParsingFinished();
        } catch (IOException e) {
            listener.onFailure(e.getMessage());
        } finally {
            IOUtils.closeSilently(reader);
        }
    }
}
