package evagl.testrunner.settings;

import java.io.File;

/**
 * Represents user settings of this application
 */
public class Settings {
    private final File pathToXcode;

    public Settings(File pathToXcode) {
        this.pathToXcode = pathToXcode;
    }

    /**
     * @return path to the xcode application
     */
    public File getPathToXcode() {
        return pathToXcode;
    }
}
