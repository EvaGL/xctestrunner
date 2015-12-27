package evagl.testrunner.model.test;

import evagl.testrunner.model.Status;
import evagl.testrunner.runner.scope.AllTestsScope;
import evagl.testrunner.runner.scope.TestingScope;

import java.io.File;

public class TestBundle extends CompositeTestEntity<TestSuite> {

    private final File bundle;

    /**
     * @param bundle Bundle file
     *               If it's null then creates bundle with name (No tests) and canceled status
     */
    public TestBundle(File bundle) {
        super(null, bundle == null ? "(No tests)" : bundle.getName());
        this.bundle = bundle;
        if (bundle == null) {
            setStatus(Status.CANCELED);
        }
    }

    @Override
    public TestingScope getTestingScope() {
        return AllTestsScope.INSTANCE;
    }

    /**
     * Creates suite and adds it to the children
     * @param suiteName name of suite to create
     * @return created suite
     */
    public TestSuite createSuite(String suiteName) {
        TestSuite suite = new TestSuite(this, suiteName);
        addChild(suite);
        return suite;
    }

    public File getFile() {
        return bundle;
    }
}
