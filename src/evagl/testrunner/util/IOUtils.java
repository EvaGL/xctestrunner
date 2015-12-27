package evagl.testrunner.util;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {
    private IOUtils() {
        // no instances
    }

    /**
     * Closes closable without throwing exceptions
     * @param closeable closable to close
     */
    public static void closeSilently(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
