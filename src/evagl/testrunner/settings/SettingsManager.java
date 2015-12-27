package evagl.testrunner.settings;

import evagl.testrunner.Main;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Manages persisted storing of {@link Settings} and caches it in memory
 */
public class SettingsManager {
    private final static String XCODE_PREF = "xcode_path";

    private static final String DEFAULT_PATH_TO_XCODE = "/Applications/Xcode.app";

    private static Settings cachedSettings;

    /**
     * Load settings from cache or, if settings wasn't cached yet, loads it from persisted storage and caches it
     * @return current settings
     */
    public static synchronized Settings loadSettings() {
        if (cachedSettings == null) {
            Preferences preferences = getPreferences();
            String pathToXcode = preferences.get(XCODE_PREF, DEFAULT_PATH_TO_XCODE);
            cachedSettings = new Settings(new File(pathToXcode));
        }
        return cachedSettings;
    }

    /**
     * Stores settings to cache and persisted storage
     * @param settings settings to save
     */
    public static synchronized void saveSettings(Settings settings) {
        cachedSettings = settings;
        Preferences preferences = getPreferences();
        preferences.put(XCODE_PREF, settings.getPathToXcode().toString());
    }

    private static Preferences getPreferences() {
        return Preferences.userNodeForPackage(Main.class);
    }
}
