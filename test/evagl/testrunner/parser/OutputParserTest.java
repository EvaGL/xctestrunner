package evagl.testrunner.parser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.*;

public class OutputParserTest {

    private TestingOutputListener listener;

    @Before
    public void setUp() {
        listener = mock(TestingOutputListener.class);
    }

    private void parseString(String input) {
        TestingOutputParser parser = new TestingOutputParser(new ByteArrayInputStream(input.getBytes()), listener);
        parser.parse();
    }

    @Test
    public void startedAndPassedTestSuiteParsing() {
        parseString("Test Suite 'Test' started at 2015-12-1\n" +
                "Test Suite 'Test' passed at 2015-12-17 22:15:53.631.\n" +
                "\t Executed 1 test, with 0 failures (0 unexpected) in 0.000 (0.000) seconds");
        InOrder order = inOrder(listener);
        order.verify(listener).onTestSuiteStarted("Test");
        order.verify(listener).onTestSuitePassed("Test");

        verify(listener).onParsingFinished();
    }

    @Test
    public void failedTestSuiteParsing() {
        parseString("Test Suite 'Test' failed at 2015-12-17 22:15:40.135.\n" +
                "\t Executed 4 tests, with 1 failure (0 unexpected) in 2.654 (2.655) seconds");
        verify(listener).onTestSuiteFailed("Test");

        verify(listener).onParsingFinished();
    }

    @Test
    public void startedAndFailedTestCaseParsing() {
        parseString("Test Case '-[Test testWrong]' started.\n" +
                "Some error string\n" +
                "Test Case '-[Test testWrong]' failed (0.000 seconds).");

        InOrder order = inOrder(listener);
        order.verify(listener).onTestCaseStarted("Test", "testWrong");
        order.verify(listener).onLogLine("Some error string\n");
        order.verify(listener).onTestCaseFailed("Test", "testWrong");

        verify(listener).onParsingFinished();
    }

    @Test
    public void passedTestCaseParsing() {
        parseString("Test Case '-[Test testRight]' passed (0.000 seconds).");
        verify(listener).onTestCasePassed("Test", "testRight");

        verify(listener).onParsingFinished();
    }

    @Test
    public void failedLineParsing() {
        parseString("Failure: fail message\nAnother line");
        verify(listener, only()).onFailure("fail message");
    }

    @Test
    public void logAndTestCaseInOneLine() {
        parseString("textTest Case '-[Test testLog]' started.\n");

        verify(listener).onLogLine("text");
        verify(listener).onTestCaseStarted("Test", "testLog");

        verify(listener).onParsingFinished();
    }
}
