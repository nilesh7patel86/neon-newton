package com.neon.newton.core.preferences;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class UserPreferenceService {
    private static UserPreferenceService instance;
    private static final String PREFS_DIR = "preferences";
    private static final String PREFS_FILE = "user-prefs.properties";

    private final Properties properties = new Properties();

    private UserPreferenceService() {
        load();
    }

    public static UserPreferenceService getInstance() {
        if (instance == null) {
            instance = new UserPreferenceService();
        }
        return instance;
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

    private void load() {
        File file = new File(getPrefsDir(), PREFS_FILE);
        if (file.exists()) {
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
                properties.load(in);
            } catch (IOException e) {
                System.err.println("Failed to load user preferences: " + e.getMessage());
            }
        }
    }

    private synchronized void save() {
        File file = new File(getPrefsDir(), PREFS_FILE);
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            properties.store(out, "User Preferences");
        } catch (IOException e) {
            System.err.println("Failed to save user preferences: " + e.getMessage());
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public void setBoolean(String key, boolean value) {
        properties.setProperty(key, String.valueOf(value));
        save();
    }

    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void setString(String key, String value) {
        properties.setProperty(key, value);
        save();
    }
}
