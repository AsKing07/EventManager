package com.bschooleventmanager.eventmanager.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestionnaire singleton de connexion à la base de données MySQL.
 * 
 * <p>Fournit une connexion unique et réutilisable avec reconnexion automatique
 * en cas de perte de connexion. Les paramètres sont chargés depuis application.properties.</p>
 * 
 * @author Charbel SONON (@AsKing07)
 * @version 1.0
 * @since 1.0
 */
public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static DatabaseConnection instance;
    private Connection connection;

    /**
     * Constructeur privé pour empêcher l'instanciation directe (pattern Singleton).
     */
    private DatabaseConnection() {
        // Constructeur privé pour le singleton
    }

    /**
     * Retourne l'instance unique du gestionnaire de connexion.
     * 
     * @return L'instance singleton de DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Établit la connexion à la base de données en chargeant la configuration.
     * 
     * <p>Charge les paramètres depuis application.properties et établit
     * la connexion MySQL avec gestion d'erreurs complète.</p>
     */
    private void connect() {
        try {
            // Fermer l'ancienne connexion si elle existe
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            
            Properties props = new Properties();
        
            // Charger depuis config/application.properties dans resources
            InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties");

            if (in == null) {
                throw new IOException("Fichier 'config/application.properties' introuvable dans les ressources");
            }

            props.load(in);
            in.close();

            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, username, password);

            logger.info("✓ Connexion à la base de données réussie");
        } catch (ClassNotFoundException e) {
            logger.error("Erreur: Driver MySQL non trouvé", e);
            this.connection = null;
        } catch (SQLException e) {
            logger.error("Erreur de connexion à la BD", e);
            this.connection = null;
        } catch (IOException e) {
            logger.error("Erreur lecture fichier config application.properties", e);
            this.connection = null;
        }
    }

    /**
     * Retourne une connexion valide à la base de données avec reconnexion automatique.
     * 
     * <p>Vérifie la validité de la connexion existante et se reconnecte automatiquement
     * si nécessaire. Thread-safe grâce à la synchronisation.</p>
     * 
     * @return La connexion active à la base de données
     */
    public synchronized Connection getConnection() {
        try {
            // Vérifier si la connexion est valide avec un timeout de 3 secondes
            if (connection == null || connection.isClosed() || !connection.isValid(3)) {
                logger.warn("Connexion fermée ou invalide, reconnexion en cours...");
                connect();
            }
        } catch (SQLException e) {
            logger.error("Erreur vérification connexion, reconnexion en cours...", e);
            connect();
        }
        return connection;
    }

    /**
     * Ferme proprement la connexion à la base de données.
     * 
     * <p>Méthode à appeler lors de l'arrêt de l'application pour libérer
     * les ressources de connexion.</p>
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Connexion fermée");
            }
        } catch (SQLException e) {
            logger.error("Erreur fermeture connexion", e);
        }
    }
}

