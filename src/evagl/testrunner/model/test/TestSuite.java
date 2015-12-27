package evagl.testrunner.model.test;

import evagl.testrunner.runner.scope.SuiteScope;
import evagl.testrunner.runner.scope.TestingScope;

public class TestSuite extends CompositeTestEntity<TestCase> {

    TestSuite(CompositeTestEntity parent, String name) {
        super(parent, name);
    }

    @Override
    public TestingScope getTestingScope() {
        return new SuiteScope(getName());
    }

    /**
     * Creates test case and adds it to the children
     * @param caseName name of test case to create
     * @return created test case
     */
    public TestCase createTestCase(String caseName) {
        TestCase testCase = new TestCase(this, caseName);
        addChild(testCase);
        return testCase;
    }
}
