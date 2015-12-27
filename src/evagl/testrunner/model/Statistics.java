package evagl.testrunner.model;

/**
 * Represents statistics about particular testing process
 */
public class Statistics {

    public final Status status;

    public final int passed;

    public final int failed;

    public final int total;

    private Statistics(Status status, int passed, int failed, int total) {
        this.status = status;
        this.passed = passed;
        this.failed = failed;
        this.total = total;
    }

    /**
     * @return Initial stats of testing process
     */
    public static Statistics emptyStats() {
        return new Statistics(Status.RUNNING, 0, 0, 0);
    }

    /**
     * @return Statistics with total count of test cases incremented by 1
     */
    public Statistics newTestCase() {
        return new Statistics(status, passed, failed, total + 1);
    }

    /**
     * @return Statistics with count of passed test cases incremented by 1
     */
    public Statistics newPassedTestCase() {
        return new Statistics(status, passed + 1, failed, total);
    }

    /**
     * @return Statistics with count of failed test cases incremented by 1
     */
    public Statistics newFailedTestCase() {
        return new Statistics(status, passed, failed + 1, total);
    }

    /**
     * @param status new status of testing process
     * @return Statistics with changed status
     */
    public Statistics newStatus(Status status) {
        return new Statistics(status, passed, failed, total);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Statistics that = (Statistics) o;

        if (passed != that.passed) return false;
        if (failed != that.failed) return false;
        if (total != that.total) return false;
        return status == that.status;

    }

    @Override
    public int hashCode() {
        int result = status != null ? status.hashCode() : 0;
        result = 31 * result + passed;
        result = 31 * result + failed;
        result = 31 * result + total;
        return result;
    }
}
