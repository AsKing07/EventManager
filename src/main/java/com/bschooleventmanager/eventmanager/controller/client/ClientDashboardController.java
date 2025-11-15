package com.bschooleventmanager.eventmanager.controller.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.bschooleventmanager.eventmanager.model.Utilisateur;
import com.bschooleventmanager.eventmanager.util.SessionManager;
import com.bschooleventmanager.eventmanager.util.NotificationUtils;
import com.bschooleventmanager.eventmanager.util.AppConfig;
import com.bschooleventmanager.eventmanager.controller.shared.ProfileController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Contrôleur principal pour l'interface Client
 * Gère la navigation entre les différents onglets et le contenu principal
 */
public class ClientDashboardController {
    private static final Logger logger = LoggerFactory.getLogger(ClientDashboardController.class);

    // === Éléments FXML ===
    @FXML
    private Text welcomeText;
    
    @FXML
    private Text versionText;
    
    @FXML
    private Button logoutButton;
    
    @FXML
    private Button eventsTab;
    
    @FXML
    private Button profileTab;
    
    @FXML
    private StackPane contentArea;

    /**
     * Initialisation du contrôleur
     */
    @FXML
    public void initialize() {
        logger.info("Initialisation de l'interface client");
        
        // Récupérer l'utilisateur connecté
        Utilisateur user = SessionManager.getUtilisateurConnecte();
        if (user != null) {
            welcomeText.setText("Bienvenue, " + user.getNom());
        }
        
        // Afficher la version
        versionText.setText("Version " + AppConfig.getAppVersion());
        
        // Afficher les événements par défaut
        showEvents();
    }

    /**
     * Affiche la liste des événements disponibles
     */
    @FXML
    private void showEvents() {
        logger.info("Affichage des événements client");
        setActiveTab("events");
        loadEventsContent();
    }

    /**
     * Affiche l'interface de profil
     */
    @FXML
    private void showProfile() {
        logger.info("Affichage du profil client");
        setActiveTab("profile");
        loadProfileContent();
    }

    /**
     * Gère la déconnexion de l'utilisateur
     */
    @FXML
    private void handleLogout() {
        logger.info("Déconnexion du client");
        
        // Effacer la session
        SessionManager.clearSession();
        
        redirectToLogin();
    }

    /**
     * Redirige vers la page de connexion
     */
    private void redirectToLogin() {
        try {
            // Charger l'interface de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth/login.fxml"));
            Parent loginRoot = loader.load();
            
            // Obtenir la scène actuelle
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            
            // Récupérer les dimensions depuis la configuration
            int windowWidth = AppConfig.getWindowWidth();
            int windowHeight = AppConfig.getWindowHeight();
            
            Scene loginScene = new Scene(loginRoot, windowWidth, windowHeight);
            
            // Appliquer le CSS si disponible
            applyCssToScene(loginScene);
            
            stage.setScene(loginScene);
            stage.setTitle(AppConfig.getAppTitle() + " - Connexion");
            stage.centerOnScreen();
            
            logger.info("✓ Redirection vers la connexion réussie");
            
        } catch (IOException e) {
            logger.error("Erreur lors de la redirection vers la connexion", e);
            NotificationUtils.showError("Impossible de charger l'interface de connexion");
        }
    }

    /**
     * Applique le CSS à une scène
     */
    private void applyCssToScene(Scene scene) {
        try {
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        } catch (Exception e) {
            logger.warn("Impossible de charger le fichier CSS: {}", e.getMessage());
        }
    }

    /**
     * Met à jour l'apparence des onglets selon l'onglet actif
     */
    private void setActiveTab(String tabName) {
        // Style par défaut pour tous les onglets
        String defaultStyle = "-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 15 25 15 25; -fx-background-radius: 0; -fx-border-width: 0;";
        String activeStyle = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 15 25 15 25; -fx-background-radius: 0; -fx-border-width: 0;";
        
        // Réinitialiser tous les onglets
        eventsTab.setStyle(defaultStyle);
        profileTab.setStyle(defaultStyle);
        
        // Mettre en évidence l'onglet actif
        switch (tabName) {
            case "events":
                eventsTab.setStyle(activeStyle);
                break;
            case "profile":
                profileTab.setStyle(activeStyle);
                break;
            default:
                logger.warn("Onglet inconnu: {}", tabName);
        }
    }

    /**
     * Charge le contenu de la liste des événements
     */
    private void loadEventsContent() {
        contentArea.getChildren().clear();
        
        try {
            Text title = new Text("Événements Disponibles");
            title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #2c3e50;");
            
            Text subtitle = new Text("Découvrez et réservez vos places pour les événements à venir");
            subtitle.setStyle("-fx-font-size: 16px; -fx-fill: #7f8c8d;");
            
            javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(20.0, title, subtitle);
            content.setAlignment(javafx.geometry.Pos.CENTER);
            
            contentArea.getChildren().add(content);
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des événements", e);
        }
    }

    /**
     * Charge le contenu du profil
     */
    private void loadProfileContent() {
        contentArea.getChildren().clear();
        
        try {
            // Charger l'interface de profil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shared/profile.fxml"));
            Parent profileContent = loader.load();
            
            // Récupérer le contrôleur pour l'initialiser
            ProfileController profileController = loader.getController();
            profileController.initializeProfile();
            
            contentArea.getChildren().add(profileContent);
            
        } catch (IOException e) {
            logger.error("Erreur lors du chargement du profil", e);
            NotificationUtils.showError("Impossible de charger l'interface de profil");
        }
    }
}