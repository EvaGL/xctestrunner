package evagl.testrunner.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class StatisticsTest {
    @Test
    public void emptyStatsShouldBeZeroes() {
        Statistics statistics = Statistics.emptyStats();
        assertThat(statistics.passed, is(0));
        assertThat(statistics.failed, is(0));
        assertThat(statistics.total, is(0));
        assertThat(statistics.status, is(Status.RUNNING));
    }

    @Test
    public void newStatusShouldSetStatusToNew() {
        Statistics statistics = Statistics.emptyStats().newStatus(Status.CANCELED);

        assertThat(statistics.passed, is(0));
        assertThat(statistics.failed, is(0));
        assertThat(statistics.total, is(0));
        assertThat(statistics.status, is(Status.CANCELED));
    }

    @Test
    public void newTestsShouldIncrementAppropriateValues() {
        Statistics statistics = Statistics.emptyStats().newTestCase();

        assertThat(statistics.passed, is(0));
        assertThat(statistics.failed, is(0));
        assertThat(statistics.total, is(1));
        assertThat(statistics.status, is(Status.RUNNING));

        statistics = statistics.newPassedTestCase();

        assertThat(statistics.passed, is(1));
        assertThat(statistics.failed, is(0));
        assertThat(statistics.total, is(1));
        assertThat(statistics.status, is(Status.RUNNING));

        statistics = statistics.newFailedTestCase();
        assertThat(statistics.passed, is(1));
        assertThat(statistics.failed, is(1));
        assertThat(statistics.total, is(1));
        assertThat(statistics.status, is(Status.RUNNING));
    }

}
