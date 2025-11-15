package com.bschooleventmanager.eventmanager.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utilitaire pour charger les propriétés de configuration de l'application
 * Permet d'accéder aux valeurs configurées dans application.properties
 */
public class AppConfig {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private static Properties properties;
    
    // Constructeur privé pour empêcher l'instanciation
    private AppConfig() {
        throw new UnsupportedOperationException("Classe utilitaire - ne peut pas être instanciée");
    }
    
    static {
        loadProperties();
    }
    
    /**
     * Charge les propriétés depuis le fichier application.properties
     */
    private static void loadProperties() {
        properties = new Properties();
        try (InputStream input = AppConfig.class.getResourceAsStream("/application.properties")) {
            if (input != null) {
                properties.load(input);
                logger.info("✓ Propriétés d'application chargées");
            } else {
                logger.warn("Fichier application.properties non trouvé, utilisation des valeurs par défaut");
                loadDefaultProperties();
            }
        } catch (IOException e) {
            logger.error("Erreur lors du chargement des propriétés", e);
            loadDefaultProperties();
        }
    }
    
    /**
     * Charge les propriétés par défaut en cas d'erreur
     */
    private static void loadDefaultProperties() {
        properties.setProperty("app.name", "EventManager");
        properties.setProperty("app.title", "EventManager - Plateforme de Réservation");
        properties.setProperty("ui.window.width", "1280");
        properties.setProperty("ui.window.height", "720");
        properties.setProperty("ui.theme", "LIGHT");
    }
    
    /**
     * Récupère une propriété de type String
     * @param key Clé de la propriété
     * @param defaultValue Valeur par défaut
     * @return Valeur de la propriété
     */
    public static String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Récupère une propriété de type String
     * @param key Clé de la propriété
     * @return Valeur de la propriété
     */
    public static String getString(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Récupère une propriété de type int
     * @param key Clé de la propriété
     * @param defaultValue Valeur par défaut
     * @return Valeur de la propriété
     */
    public static int getInt(String key, int defaultValue) {
        try {
            String value = properties.getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException _) {
            logger.warn("Valeur invalide pour la propriété {}: {}, utilisation de la valeur par défaut: {}", 
                       key, properties.getProperty(key), defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * Récupère le nom de l'application
     * @return Nom de l'application
     */
    public static String getAppName() {
        return getString("app.name", "EventManager");
    }
    
    /**
     * Récupère le titre de l'application
     * @return Titre de l'application
     */
    public static String getAppTitle() {
        return getString("app.title", "EventManager - Plateforme de Réservation");
    }
    
    /**
     * Récupère la largeur de la fenêtre
     * @return Largeur de la fenêtre
     */
    public static int getWindowWidth() {
        return getInt("ui.window.width", 1280);
    }
    
    /**
     * Récupère la hauteur de la fenêtre
     * @return Hauteur de la fenêtre
     */
    public static int getWindowHeight() {
        return getInt("ui.window.height", 720);
    }
    
    /**
     * Récupère le thème de l'interface
     * @return Thème de l'interface
     */
    public static String getUITheme() {
        return getString("ui.theme", "LIGHT");
    }
    
    /**
     * Récupère la version de l'application
     * @return Version de l'application
     */
    public static String getAppVersion() {
        return getString("app.version", "1.0.0");
    }
    
    /**
     * Vérifie si la fenêtre doit s'adapter à l'écran
     * @return true si la fenêtre doit être adaptative
     */
    public static boolean isWindowAdaptive() {
        String adaptive = getString("ui.window.adaptive", "false");
        return "true".equalsIgnoreCase(adaptive);
    }
    
    /**
     * Vérifie si la fenêtre doit être maximisée au démarrage
     * @return true si la fenêtre doit être maximisée
     */
    public static boolean isWindowMaximized() {
        String maximized = getString("ui.window.maximized", "false");
        return "true".equalsIgnoreCase(maximized);
    }
}