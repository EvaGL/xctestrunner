package evagl.testrunner.model.test;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
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

    @Test
    public void compositeTestEntity() {
        TestSuite suite = new TestSuite(null, "suite");

        TestCase testCase = new TestCase(suite, "test");
        suite.addChild(testCase);
        assertThat(suite.getChildCount(), is(1));

        TestCase anotherTestCase = new TestCase(suite, "another");
        suite.addChild(anotherTestCase);
        assertThat(suite.getChildCount(), is(2));

        assertThat(suite.getChild(0), is(testCase));
        assertThat(suite.getIndexOfChild(anotherTestCase), is(1));

        suite.clearChildren();
        assertThat(suite.getChildCount(), is(0));
        assertThat(suite.getChild(1), nullValue());
        assertThat(suite.getIndexOfChild(testCase), is(-1));
    }

    @Test
    public void createSuiteShouldAddToChildren() {
        TestBundle bundle = new TestBundle(null);
        assertThat(bundle.getChildCount(), is(0));

        TestSuite suite = bundle.createSuite("suite");
        assertThat(suite.getName(), is("suite"));
        assertThat(suite.getParent(), is(bundle));
        assertThat(bundle.getChildCount(), is(1));
        assertThat(bundle.getChild(0), is(suite));
        assertThat(suite.getChildCount(), is(0));

        TestCase testCase = suite.createTestCase("testCase");
        assertThat(testCase.getName(), is("testCase"));
        assertThat(testCase.getParent(), is(suite));
        assertThat(suite.getChildCount(), is(1));
        assertThat(suite.getChild(0), is(testCase));
    }
}
