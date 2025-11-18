package com.bschooleventmanager.eventmanager.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration pour l'intégration Stripe
 */
public class StripeConfig {
    private static final Logger logger = LoggerFactory.getLogger(StripeConfig.class);
    
    private static StripeConfig instance;
    private Properties properties;
    
    private StripeConfig() {
        loadProperties();
    }
    
    public static StripeConfig getInstance() {
        if (instance == null) {
            instance = new StripeConfig();
        }
        return instance;
    }
    
    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
                logger.info("✓ Configuration Stripe chargée");
            } else {
                logger.error("❌ Fichier application.properties non trouvé");
            }
        } catch (IOException e) {
            logger.error("❌ Erreur lors du chargement de la configuration Stripe", e);
        }
    }
    
    public String getSecretKey() {
        return properties.getProperty("stripe.secret.key", "");
    }
    
    public String getPublishableKey() {
        return properties.getProperty("stripe.publishable.key", "");
    }
    
    public boolean isTestMode() {
        return "true".equals(properties.getProperty("stripe.test.mode", "true"));
    }
    
    public boolean isConfigured() {
        String secretKey = getSecretKey();
        return secretKey != null && !secretKey.isEmpty() && !secretKey.equals("sk_test_YOUR_SECRET_KEY_HERE");
    }
}