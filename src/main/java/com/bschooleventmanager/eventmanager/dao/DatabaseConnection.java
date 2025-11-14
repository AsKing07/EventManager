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
        connect();
    }

    private void connect() {
        try {
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
        } catch (SQLException e) {
            logger.error("Erreur de connexion à la BD", e);
        } catch (IOException e) {
            logger.error("Erreur lecture fichier config", e);
        }
    }
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            logger.error("Erreur vérification connexion", e);
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

