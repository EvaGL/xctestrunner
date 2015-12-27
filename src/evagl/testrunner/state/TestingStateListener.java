package evagl.testrunner.state;

import evagl.testrunner.model.Statistics;
import evagl.testrunner.model.test.CompositeTestEntity;
import evagl.testrunner.model.test.TestEntity;

/**
 * Listener for events produced by {@link TestingStateBuilder}
 */
public interface TestingStateListener {
    void onTreeNodeChanged(TestEntity entity);

    <C extends TestEntity> void onTreeNodeAdded(CompositeTestEntity<C> parent, C child);

    void onLogChanged();

    void onStatisticsUpdated(Statistics newStatistics);

    void onFinish();

    void onFailure(String message);
}
