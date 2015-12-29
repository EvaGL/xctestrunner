package evagl.testrunner.settings;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.prefs.Preferences;

import static evagl.testrunner.settings.SettingsManager.DEFAULT_PATH_TO_XCODE;
import static evagl.testrunner.settings.SettingsManager.XCODE_PREF;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class SettingsManagerTest {

    private SettingsManager settingsManager;

    private Preferences preferences;

    @Before
    public void setUp() {
        preferences = mock(Preferences.class);
        when(preferences.get(XCODE_PREF, DEFAULT_PATH_TO_XCODE)).thenReturn("/some/path");

        settingsManager = new ShadowSettingsManager(preferences);
    }

    @Test
    public void managerShouldLoadSettingsFromPreferences() {
        Settings settings = settingsManager.loadSettings();

        verify(preferences).get(XCODE_PREF, DEFAULT_PATH_TO_XCODE);
        assertThat(settings.getPathToXcode(), is(new File("/some/path")));
    }

    @Test
    public void managerShouldCacheValue() {
        Settings settings = settingsManager.loadSettings();
        Settings anotherSettings = settingsManager.loadSettings();
        assertThat(anotherSettings, sameInstance(settings));

        verify(preferences, only()).get(anyString(), anyString());
    }

    @Test
    public void managerShouldSaveSettingsToPreferences() {
        Settings settings = new Settings(new File("/some/another/path"));
        settingsManager.saveSettings(settings);
        verify(preferences).put(XCODE_PREF, "/some/another/path");

        Settings anotherSettings = settingsManager.loadSettings();
        assertThat(anotherSettings, sameInstance(settings));
    }
}
