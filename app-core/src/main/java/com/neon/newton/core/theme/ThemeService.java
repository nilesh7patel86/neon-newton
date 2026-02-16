package com.neon.newton.core.theme;

import atlantafx.base.theme.*;
import com.neon.newton.core.preferences.UserPreferenceService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ThemeService {
    private static ThemeService instance;
    private static final String THEME_PREF_KEY = "active-theme-id";

    private final ObservableList<Theme> availableThemes = FXCollections.observableArrayList();
    private Theme currentTheme;
    private final List<WeakReference<Consumer<Theme>>> listeners = new ArrayList<>();

    private ThemeService() {
        // AtlantaFX themes
        availableThemes.add(new Theme("primer-dark", "Primer Dark", new PrimerDark().getUserAgentStylesheet(), true));
        availableThemes.add(new Theme("primer-light", "Primer Light", new PrimerLight().getUserAgentStylesheet(), true));
        availableThemes.add(new Theme("nord-dark", "Nord Dark", new NordDark().getUserAgentStylesheet(), true));
        availableThemes.add(new Theme("nord-light", "Nord Light", new NordLight().getUserAgentStylesheet(), true));
        availableThemes.add(new Theme("cupertino-dark", "Cupertino Dark", new CupertinoDark().getUserAgentStylesheet(), true));
        availableThemes.add(new Theme("cupertino-light", "Cupertino Light", new CupertinoLight().getUserAgentStylesheet(), true));
        availableThemes.add(new Theme("dracula", "Dracula", new Dracula().getUserAgentStylesheet(), true));

        // Load persisted theme
        String savedId = loadThemePreference();
        currentTheme = availableThemes.stream()
                .filter(t -> t.id().equals(savedId))
                .findFirst()
                .orElse(availableThemes.stream()
                        .filter(t -> t.id().equals("primer-dark"))
                        .findFirst()
                        .orElse(availableThemes.getFirst()));
    }

    private String loadThemePreference() {
        return UserPreferenceService.getInstance().getString(THEME_PREF_KEY, "primer-dark");
    }

    private void saveThemePreference(String themeId) {
        UserPreferenceService.getInstance().setString(THEME_PREF_KEY, themeId);
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
            notifyListeners(theme);
        }
    }

    private void notifyListeners(Theme theme) {
        Iterator<WeakReference<Consumer<Theme>>> it = listeners.iterator();
        while (it.hasNext()) {
            Consumer<Theme> listener = it.next().get();
            if (listener == null) {
                it.remove();
            } else {
                listener.accept(theme);
            }
        }
    }

    public void applyTheme(Scene scene) {
        if (currentTheme.isAtlantaFX()) {
            Application.setUserAgentStylesheet(currentTheme.cssPath());

            // Still add our base styles as an overlay for NeonNewton specific components
            scene.getStylesheets().clear();
            String baseStyles = Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm();
            scene.getStylesheets().add(baseStyles);
        } else {
            Application.setUserAgentStylesheet(null); // Reset to default
            scene.getStylesheets().clear();
            String css = Objects.requireNonNull(getClass().getResource(currentTheme.cssPath())).toExternalForm();
            scene.getStylesheets().add(css);
        }
    }

    public void addThemeChangeListener(Consumer<Theme> listener) {
        listeners.add(new WeakReference<>(listener));
    }
}
