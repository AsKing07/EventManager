package com.bschooleventmanager.eventmanager.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        // Constructeur privé pour le singleton
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

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

