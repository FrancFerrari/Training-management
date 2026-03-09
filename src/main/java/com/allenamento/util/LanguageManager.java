package com.allenamento.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;

/**
 * Manages language selection and loading of translated messages.
 * Supports Italian and English with persistence of user choice.
 */
public class LanguageManager {
    private static final Logger logger = LoggerFactory.getLogger(LanguageManager.class);
    private static final String CONFIG_FILE = "language.conf";
    private static final String DEFAULT_LANGUAGE = "it";
    
    private static LanguageManager instance;
    private Properties messages;
    private ObjectProperty<String> currentLanguageProperty;
    private ObservableList<String> availableLanguages;
    
    private LanguageManager() {
        messages = new Properties();
        currentLanguageProperty = new SimpleObjectProperty<>();
        availableLanguages = FXCollections.observableArrayList("it", "en");
        
        // Load saved language or default one
        String savedLanguage = loadSavedLanguage();
        setLanguage(savedLanguage);
    }
    
    public static synchronized LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }
    
    /**
     * Loads messages for the specified language.
     */
    public void setLanguage(String languageCode) {
        try {
            String resourceName = "messages_" + languageCode + ".properties";
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName);
            
            if (is == null) {
                logger.warn("Message file not found for language: " + languageCode + ", using Italian as default");
                is = getClass().getClassLoader().getResourceAsStream("messages_it.properties");
                languageCode = "it";
            }
            
            messages.load(new InputStreamReader(is, StandardCharsets.UTF_8));
            currentLanguageProperty.setValue(languageCode);
            saveLanguagePreference(languageCode);
            logger.info("Language changed to: " + languageCode);
        } catch (IOException e) {
            logger.error("Error loading messages for language: " + languageCode, e);
            // Fallback to Italian
            if (!languageCode.equals("it")) {
                setLanguage("it");
            }
        }
    }
    
    /**
     * Gets the translated message for the specified key.
     */
    public String get(String key) {
        String value = messages.getProperty(key);
        if (value == null) {
            logger.warn("Message key not found: " + key);
            return key; // Return the key if not found
        }
        return value;
    }
    
    /**
     * Gets the translated message for the key with parameter substitution.
     */
    public String get(String key, Object... params) {
        String template = get(key);
        try {
            return String.format(template, params);
        } catch (Exception e) {
            logger.warn("Error formatting message for key: " + key, e);
            return template;
        }
    }
    
    /**
     * Returns the current language.
     */
    public String getCurrentLanguage() {
        return currentLanguageProperty.getValue();
    }
    
    /**
     * Observable property for UI binding.
     */
    public ObjectProperty<String> currentLanguageProperty() {
        return currentLanguageProperty;
    }
    
    /**
     * Returns the list of available languages.
     */
    public ObservableList<String> getAvailableLanguages() {
        return availableLanguages;
    }
    
    /**
     * Converts language code to readable name.
     */
    public String getLanguageName(String languageCode) {
        switch (languageCode) {
            case "it":
                return get("menu.italian");
            case "en":
                return get("menu.english");
            default:
                return languageCode;
        }
    }
    
    /**
     * Saves language preference to configuration file.
     */
    private void saveLanguagePreference(String languageCode) {
        try {
            Files.write(Paths.get(CONFIG_FILE), languageCode.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.warn("Cannot save language preference", e);
        }
    }
    
    /**
     * Loads saved language from configuration file.
     */
    private String loadSavedLanguage() {
        try {
            if (Files.exists(Paths.get(CONFIG_FILE))) {
                String language = new String(Files.readAllBytes(Paths.get(CONFIG_FILE)), StandardCharsets.UTF_8).trim();
                if (availableLanguages.contains(language)) {
                    return language;
                }
            }
        } catch (IOException e) {
            logger.warn("Cannot load saved language preference", e);
        }
        return DEFAULT_LANGUAGE;
    }
}
