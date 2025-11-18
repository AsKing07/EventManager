package com.bschooleventmanager.eventmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.bschooleventmanager.eventmanager.util.AppConfig;
import com.bschooleventmanager.eventmanager.util.WindowUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe principale de l'application EventManager
 * Lance l'interface de connexion avec les dimensions configurées
 */
public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth/login.fxml"));
            Parent root = loader.load();

            double[] dimensions = WindowUtils.getOptimalDimensions();
            
            Scene scene = new Scene(root, dimensions[0], dimensions[1]);
            
            applyCssToScene(scene);

            primaryStage.setTitle(AppConfig.getAppTitle());
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            
            // Configurer la fenêtre selon les paramètres
            WindowUtils.configureStage(primaryStage);
            
            primaryStage.show();

            logger.info("Application démarrée - Dimensions: {}x{}", (int)dimensions[0], (int)dimensions[1]);
            logger.info("✓ Titre: {}", AppConfig.getAppTitle());
            
        } catch (Exception e) {
            logger.error("Erreur au démarrage de l'application", e);
            e.printStackTrace();
        }
    }

    private void applyCssToScene(Scene scene) {
        try {
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        } catch (Exception e) {
            logger.warn("Impossible de charger le fichier CSS: {}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        logger.info("Démarrage de EventManager v{}", AppConfig.getAppVersion());
        launch(args);
    }
}
