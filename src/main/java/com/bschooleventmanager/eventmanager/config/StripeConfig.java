package com.bschooleventmanager.eventmanager.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration Singleton pour l'intégration Stripe dans l'application EventManager.
 * 
 * <p>Cette classe gère la configuration et les paramètres nécessaires pour l'intégration
 * avec l'API Stripe pour le traitement des paiements. Elle charge automatiquement
 * les paramètres depuis le fichier application.properties et fournit un accès
 * centralisé aux clés API et aux paramètres de configuration.</p>
 * 
 * <p><strong>Fonctionnalités principales :</strong></p>
 * <ul>
 *   <li>Chargement automatique de la configuration depuis application.properties</li>
 *   <li>Gestion sécurisée des clés API Stripe (secrète et publique)</li>
 *   <li>Support des modes test et production</li>
 *   <li>Validation de la configuration</li>
 *   <li>Pattern Singleton pour un accès global</li>
 * </ul>
 * 
 * <p><strong>Configuration requise dans application.properties :</strong></p>
 * <pre>
 * stripe.secret.key=sk_test_YOUR_SECRET_KEY_HERE
 * stripe.publishable.key=pk_test_YOUR_PUBLISHABLE_KEY_HERE
 * stripe.test.mode=true
 * </pre>
 * 
 * <p><strong>Exemple d'utilisation :</strong></p>
 * <pre>{@code
 * StripeConfig config = StripeConfig.getInstance();
 * if (config.isConfigured()) {
 *     String secretKey = config.getSecretKey();
 *     boolean isTestMode = config.isTestMode();
 *     // Initialiser Stripe avec ces paramètres
 * }
 * }</pre>
 * 
 * @author Charbel SONON
 * @version 1.0
 * @since 1.0
 * 
 * @see java.util.Properties
 * @see org.slf4j.Logger
 */
public class StripeConfig {
    /** Logger pour le traçage et la gestion des erreurs de configuration */
    private static final Logger logger = LoggerFactory.getLogger(StripeConfig.class);
    
    /** Instance unique du singleton StripeConfig */
    private static StripeConfig instance;
    
    /** Propriétés chargées depuis le fichier application.properties */
    private Properties properties;
    
    /**
     * Constructeur privé pour implémenter le pattern Singleton.
     * 
     * <p>Initialise automatiquement la configuration en chargeant les propriétés
     * depuis le fichier application.properties au moment de la création de l'instance.</p>
     * 
     * @see #loadProperties()
     */
    private StripeConfig() {
        loadProperties();
    }
    
    /**
     * Retourne l'instance unique de StripeConfig (Singleton).
     * 
     * <p>Cette méthode implémente le pattern Singleton en créant l'instance
     * uniquement lors du premier appel. Les appels suivants retournent
     * la même instance, garantissant ainsi un point d'accès unique
     * à la configuration Stripe dans toute l'application.</p>
     * 
     * @return l'instance unique de StripeConfig
     * 
     * @since 1.0
     */
    public static StripeConfig getInstance() {
        if (instance == null) {
            instance = new StripeConfig();
        }
        return instance;
    }
    
    /**
     * Charge les propriétés de configuration depuis le fichier application.properties.
     * 
     * <p>Cette méthode lit le fichier application.properties depuis le classpath
     * et charge toutes les propriétés dans l'objet Properties interne.
     * En cas d'erreur de lecture, des messages sont loggés mais l'exécution continue
     * avec des valeurs par défaut.</p>
     * 
     * <p><strong>Propriétés attendues :</strong></p>
     * <ul>
     *   <li>stripe.secret.key - Clé secrète Stripe</li>
     *   <li>stripe.publishable.key - Clé publique Stripe</li>
     *   <li>stripe.test.mode - Mode test (true/false)</li>
     * </ul>
     * 
     * <p><strong>Gestion des erreurs :</strong></p>
     * <ul>
     *   <li>Fichier non trouvé : Logged comme erreur, propriétés vides utilisées</li>
     *   <li>Erreur IOException : Logged avec stack trace, propriétés vides utilisées</li>
     * </ul>
     * 
     * @see java.util.Properties#load(InputStream)
     * @see ClassLoader#getResourceAsStream(String)
     * 
     * @since 1.0
     */
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
    
    /**
     * Retourne la clé secrète Stripe pour les appels API côté serveur.
     * 
     * <p>La clé secrète est utilisée pour authentifier les appels API Stripe
     * côté serveur. Elle ne doit jamais être exposée côté client et doit
     * être gardée confidentielle.</p>
     * 
     * <p><strong>Formats attendus :</strong></p>
     * <ul>
     *   <li>Mode test : sk_test_...</li>
     *   <li>Mode production : sk_live_...</li>
     * </ul>
     * 
     * @return la clé secrète Stripe depuis application.properties,
     *         ou une chaîne vide si non configurée
     * 
     * @see #isConfigured()
     * @see #isTestMode()
     * 
     * @since 1.0
     */
    public String getSecretKey() {
        return properties.getProperty("stripe.secret.key", "");
    }
    
    /**
     * Retourne la clé publique Stripe pour les intégrations côté client.
     * 
     * <p>La clé publique peut être exposée côté client et est utilisée
     * pour initialiser Stripe.js ou d'autres bibliothèques client.
     * Contrairement à la clé secrète, elle peut être incluse dans
     * le code JavaScript ou mobile.</p>
     * 
     * <p><strong>Formats attendus :</strong></p>
     * <ul>
     *   <li>Mode test : pk_test_...</li>
     *   <li>Mode production : pk_live_...</li>
     * </ul>
     * 
     * @return la clé publique Stripe depuis application.properties,
     *         ou une chaîne vide si non configurée
     * 
     * @see #getSecretKey()
     * @see #isTestMode()
     * 
     * @since 1.0
     */
    public String getPublishableKey() {
        return properties.getProperty("stripe.publishable.key", "");
    }
    
    /**
     * Détermine si l'application fonctionne en mode test Stripe.
     * 
     * <p>Le mode test permet d'utiliser l'environnement de test Stripe
     * sans traiter de vrais paiements. Très utile pour le développement
     * et les tests automatisés.</p>
     * 
     * <p><strong>Comportement :</strong></p>
     * <ul>
     *   <li>Mode test (true) : Utilise les clés sk_test_ et pk_test_</li>
     *   <li>Mode production (false) : Utilise les clés sk_live_ et pk_live_</li>
     * </ul>
     * 
     * <p><strong>Valeur par défaut :</strong> true (mode test) pour la sécurité</p>
     * 
     * @return true si en mode test, false si en mode production
     * 
     * @see #getSecretKey()
     * @see #getPublishableKey()
     * 
     * @since 1.0
     */
    public boolean isTestMode() {
        return "true".equals(properties.getProperty("stripe.test.mode", "true"));
    }
    
    /**
     * Vérifie si la configuration Stripe est complète et valide.
     * 
     * <p>Cette méthode effectue des vérifications de base pour s'assurer
     * que la configuration Stripe est utilisable :</p>
     * 
     * <ul>
     *   <li>La clé secrète est définie (non null et non vide)</li>
     *   <li>La clé secrète n'est pas la valeur par défaut du template</li>
     *   <li>Les propriétés ont été chargées correctement</li>
     * </ul>
     * 
     * <p><strong>Utilisation recommandée :</strong></p>
     * <pre>{@code
     * StripeConfig config = StripeConfig.getInstance();
     * if (!config.isConfigured()) {
     *     logger.error("Configuration Stripe incomplète");
     *     // Gérer l'erreur ou désactiver les paiements
     * }
     * }</pre>
     * 
     * <p><strong>Note :</strong> Cette méthode ne valide pas la validité des clés
     * auprès de l'API Stripe, seulement leur présence et format de base.</p>
     * 
     * @return true si la configuration est complète, false sinon
     * 
     * @see #getSecretKey()
     * 
     * @since 1.0
     */
    public boolean isConfigured() {
        String secretKey = getSecretKey();
        return secretKey != null && !secretKey.isEmpty() && !secretKey.equals("sk_test_YOUR_SECRET_KEY_HERE");
    }
}