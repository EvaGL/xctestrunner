package evagl.testrunner.settings;

import java.util.prefs.Preferences;

class ShadowSettingsManager extends SettingsManager {
    private Preferences preferences;

    ShadowSettingsManager(Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    Preferences getPreferences() {
        return preferences;
    }
}
