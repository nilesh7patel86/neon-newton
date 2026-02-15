package com.neon.newton.core;

import atlantafx.base.theme.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ThemeService {
    private static ThemeService instance;
    private static final String PREFS_DIR = "preferences";
    private static final String THEME_FILE = "theme.properties";
    private static final String THEME_PREF_KEY = "active-theme-id";

    private final ObservableList<Theme> availableThemes = FXCollections.observableArrayList();
    private Theme currentTheme;
    private final List<Consumer<Theme>> listeners = new ArrayList<>();

    private ThemeService() {
        // Built-in themes
        availableThemes.add(new Theme("neon-dark", "Neon Dark", "/styles.css", false));
        availableThemes.add(new Theme("classic-light", "Classic Light", "/themes/light-theme.css", false));
        availableThemes.add(new Theme("midnight-blue", "Midnight Blue", "/themes/midnight-theme.css", false));

        // AtlantaFX themes
        availableThemes.add(new Theme("primer-dark", "Primer Dark", new PrimerDark().getUserAgentStylesheet(), true));
        availableThemes
                .add(new Theme("primer-light", "Primer Light", new PrimerLight().getUserAgentStylesheet(), true));
        availableThemes.add(new Theme("nord-dark", "Nord Dark", new NordDark().getUserAgentStylesheet(), true));
        availableThemes.add(new Theme("nord-light", "Nord Light", new NordLight().getUserAgentStylesheet(), true));
        availableThemes
                .add(new Theme("cupertino-dark", "Cupertino Dark", new CupertinoDark().getUserAgentStylesheet(), true));
        availableThemes.add(
                new Theme("cupertino-light", "Cupertino Light", new CupertinoLight().getUserAgentStylesheet(), true));
        availableThemes.add(new Theme("dracula", "Dracula", new Dracula().getUserAgentStylesheet(), true));
        availableThemes.add(new Theme("vitality", "Vitality", "/themes/vitality-theme.css", false));

        // Load persisted theme
        String savedId = loadThemePreference();
        currentTheme = availableThemes.stream()
                .filter(t -> t.id().equals(savedId))
                .findFirst()
                .orElse(availableThemes.stream()
                        .filter(t -> t.id().equals("primer-dark"))
                        .findFirst()
                        .orElse(availableThemes.get(0)));
    }

    private File getPrefsDir() {
        Path baseDir = Paths.get("").toAbsolutePath();
        if (baseDir.toString().endsWith("app-core")) {
            baseDir = baseDir.getParent();
        }
        File dir = baseDir.resolve(PREFS_DIR).toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private String loadThemePreference() {
        Properties props = new Properties();
        File file = new File(getPrefsDir(), THEME_FILE);
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                props.load(in);
                return props.getProperty(THEME_PREF_KEY, "primer-dark");
            } catch (IOException e) {
                System.err.println("Failed to load theme preference: " + e.getMessage());
            }
        }
        return "primer-dark";
    }

    private void saveThemePreference(String themeId) {
        Properties props = new Properties();
        File file = new File(getPrefsDir(), THEME_FILE);
        props.setProperty(THEME_PREF_KEY, themeId);
        try (FileOutputStream out = new FileOutputStream(file)) {
            props.store(out, "Application Theme Preference");
        } catch (IOException e) {
            System.err.println("Failed to save theme preference: " + e.getMessage());
        }
    }

    public static ThemeService getInstance() {
        if (instance == null) {
            instance = new ThemeService();
        }
        return instance;
    }

    public ObservableList<Theme> getAvailableThemes() {
        return availableThemes;
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public void setTheme(Theme theme) {
        if (theme != null && !theme.equals(currentTheme)) {
            this.currentTheme = theme;
            saveThemePreference(theme.id());
            listeners.forEach(listener -> listener.accept(theme));
        }
    }

    public void applyTheme(Scene scene) {
        if (currentTheme.isAtlantaFX()) {
            Application.setUserAgentStylesheet(currentTheme.cssPath());

            // Still add our base styles as an overlay for NeonNewton specific components
            scene.getStylesheets().clear();
            String baseStyles = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(baseStyles);
        } else {
            Application.setUserAgentStylesheet(null); // Reset to default
            scene.getStylesheets().clear();
            String css = getClass().getResource(currentTheme.cssPath()).toExternalForm();
            scene.getStylesheets().add(css);
        }
    }

    public void addThemeChangeListener(Consumer<Theme> listener) {
        listeners.add(listener);
    }
}
