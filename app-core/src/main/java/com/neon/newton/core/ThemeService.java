package com.neon.newton.core;

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
        availableThemes.add(new Theme("neon-dark", "Neon Dark", "/styles.css"));
        availableThemes.add(new Theme("classic-light", "Classic Light", "/themes/light-theme.css"));
        availableThemes.add(new Theme("midnight-blue", "Midnight Blue", "/themes/midnight-theme.css"));

        // Load persisted theme
        String savedId = loadThemePreference();
        currentTheme = availableThemes.stream()
                .filter(t -> t.id().equals(savedId))
                .findFirst()
                .orElse(availableThemes.get(0));
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
                return props.getProperty(THEME_PREF_KEY, "neon-dark");
            } catch (IOException e) {
                System.err.println("Failed to load theme preference: " + e.getMessage());
            }
        }
        return "neon-dark";
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
        scene.getStylesheets().clear();
        String css = getClass().getResource(currentTheme.cssPath()).toExternalForm();
        scene.getStylesheets().add(css);
    }

    public void addThemeChangeListener(Consumer<Theme> listener) {
        listeners.add(listener);
    }
}
