package com.bschooleventmanager.eventmanager.controller.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import com.bschooleventmanager.eventmanager.model.Utilisateur;
import com.bschooleventmanager.eventmanager.service.UtilisateurService;
import com.bschooleventmanager.eventmanager.util.SessionManager;
import com.bschooleventmanager.eventmanager.util.NotificationUtils;
import com.bschooleventmanager.eventmanager.util.AppConfig;
import com.bschooleventmanager.eventmanager.util.WindowUtils;
import com.bschooleventmanager.eventmanager.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Contrôleur pour l'interface de connexion
 * Gère l'authentification des utilisateurs existants
 */
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink registerLink;
    @FXML
    private Label errorLabel;

    private UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    public void initialize() {
        // Initialisation si nécessaire
        logger.info("Initialisation de l'interface de connexion");
    }

    @FXML
    private void handleLogin() {
        try {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                NotificationUtils.showWarning("Veuillez remplir tous les champs");
                return;
            }

            // Authentifier
            Utilisateur user = utilisateurService.authentifier(email, password);

            // Stocker en session
            SessionManager.setUtilisateurConnecte(user);

            logger.info("✓ Connexion réussie: {}", email);
            NotificationUtils.showSuccess("Connexion réussie!");

            // Naviguer vers le dashboard
            navigateToDashboard(user);

        } catch (BusinessException e) {
            logger.warn("Erreur authentification: {}", e.getMessage());
            NotificationUtils.showError(e.getMessage());
            errorLabel.setText(e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur inattendue", e);
            NotificationUtils.showError("Une erreur s'est produite");
        }
    }

    /**
     * Gestionnaire pour rediriger vers l'interface d'inscription
     */
    @FXML
    private void handleRegister() {
        logger.info("Redirection vers l'interface d'inscription");
        loadRegisterScene();
    }

    /**
     * Charge l'interface d'inscription
     */
    private void loadRegisterScene() {
        try {
            // Charger l'interface d'inscription
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth/register.fxml"));
            Parent registerRoot = loader.load();
            
            // Obtenir la scène actuelle et changer le contenu
            Stage stage = (Stage) loginButton.getScene().getWindow();
            
            // Obtenir les dimensions optimales
            double[] dimensions = WindowUtils.getOptimalDimensions();
            
            Scene registerScene = new Scene(registerRoot, dimensions[0], dimensions[1]);
            
            // Appliquer le CSS si disponible
            applyCssIfAvailable(registerScene);
            
            stage.setScene(registerScene);
            stage.setTitle(AppConfig.getAppTitle() + " - Inscription");
            
            // Configurer la fenêtre selon les paramètres
            WindowUtils.configureStage(stage);
            
            logger.info("✓ Redirection vers l'inscription réussie");
            
        } catch (IOException e) {
            logger.error("Erreur lors de la redirection vers l'inscription", e);
            NotificationUtils.showError("Impossible de charger l'interface d'inscription");
        }
    }
    
    /**
     * Applique le CSS à la scène si disponible
     */
    private void applyCssIfAvailable(Scene scene) {
        try {
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        } catch (Exception e) {
            logger.warn("Impossible de charger le fichier CSS: {}", e.getMessage());
        }
    }

    private void navigateToDashboard(Utilisateur user) {
        System.out.println("Navigation vers le dashboard pour l'utilisateur: " + user.getEmail());
        logger.info("Navigation vers le dashboard pour l'utilisateur: {}", user.getMotDePasse());
        
        try {
            String fxmlPath;
            String title;
            
            // Déterminer l'interface à charger selon le type d'utilisateur
            switch (user.getTypeUtilisateur()) {
                case ORGANISATEUR:
                    fxmlPath = "/fxml/organisateur/dashboard.fxml";
                    title = "Dashboard Organisateur";
                    break;
                case CLIENT:
                    fxmlPath = "/fxml/client/dashboard.fxml";
                    title = "Dashboard Client";
                    break;
                default:
                    throw new IllegalArgumentException("Type d'utilisateur non reconnu: " + user.getTypeUtilisateur());
            }
            
            // Charger l'interface appropriée
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent dashboardRoot = loader.load();
            
            // Obtenir la scène actuelle
            Stage stage = (Stage) loginButton.getScene().getWindow();
            
            // Obtenir les dimensions optimales
            double[] dimensions = WindowUtils.getOptimalDimensions();
            
            Scene dashboardScene = new Scene(dashboardRoot, dimensions[0], dimensions[1]);
            
            // Appliquer le CSS si disponible
            applyCssIfAvailable(dashboardScene);
            
            stage.setScene(dashboardScene);
            stage.setTitle(AppConfig.getAppTitle() + " - " + title);
            
            // Configurer la fenêtre selon les paramètres
            WindowUtils.configureStage(stage);
            
            logger.info("✓ Redirection vers le dashboard {} réussie", user.getTypeUtilisateur().getLabel());
            
        } catch (IOException e) {
            logger.error("Erreur lors de la redirection vers le dashboard", e);
            NotificationUtils.showError("Impossible de charger l'interface du dashboard");
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la navigation", e);
            NotificationUtils.showError("Erreur lors de la navigation vers le dashboard");
        }
    }
}


