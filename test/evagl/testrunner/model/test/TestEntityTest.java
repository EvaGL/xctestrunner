package evagl.testrunner.model.test;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestEntityTest {

    @Test
    public void logTest() {
        TestSuite suite = new TestSuite(null, "a");

        TestCase case1 = suite.createTestCase("b");
        case1.addLogLine("first");

        TestCase case2 = suite.createTestCase("c");
        case2.addLogLine("second");

        assertThat(case1.getLog(), is("first"));
        assertThat(suite.getLog(), is("firstsecond"));
    }
}
