package com.bschooleventmanager.eventmanager.controller.organisateur;

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
 * Contrôleur principal pour l'interface Organisateur
 * Gère la navigation entre les différents onglets et le contenu principal
 */
public class OrganisateurDashboardController {
    private static final Logger logger = LoggerFactory.getLogger(OrganisateurDashboardController.class);

    // === Éléments FXML ===
    @FXML
    private Text welcomeText;
    
    @FXML
    private Text versionText;
    
    @FXML
    private Button logoutButton;
    
    @FXML
    private Button dashboardTab;
    
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
        logger.info("Initialisation de l'interface organisateur");
        
        // Récupérer l'utilisateur connecté
        Utilisateur user = SessionManager.getUtilisateurConnecte();
        if (user != null) {
            welcomeText.setText("Bienvenue, " + user.getNom());
        }
        
        // Afficher la version
        versionText.setText("Version " + AppConfig.getAppVersion());
        
        // Afficher le dashboard par défaut
        showDashboard();
    }

    /**
     * Affiche le contenu du Dashboard
     */
    @FXML
    public void showDashboard() {
        logger.info("Affichage du dashboard organisateur");
        setActiveTab("dashboard");
        loadDashboardContent();
    }

    /**
     * Affiche la liste des événements
     */
    @FXML
    private void showEvents() {
        logger.info("Affichage des événements organisateur");
        setActiveTab("events");
        loadEventsContent();
    }

    /**
     * Affiche l'interface de profil
     */
    @FXML
    private void showProfile() {
        logger.info("Affichage du profil organisateur");
        setActiveTab("profile");
        loadProfileContent();
    }

    /**
     * Affiche l'interface de création d'événement
     */
    @FXML
    public void showCreateEvent() {
        logger.info("Affichage de l'interface de création d'événement");
        setActiveTab(""); // Aucun onglet actif pour la création d'événement
        loadCreateEventContent();
    }

    /**
     * Gère la déconnexion de l'utilisateur
     */
    @FXML
    private void handleLogout() {
        logger.info("Déconnexion de l'organisateur");
        
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
        dashboardTab.setStyle(defaultStyle);
        eventsTab.setStyle(defaultStyle);
        profileTab.setStyle(defaultStyle);
        
        // Mettre en évidence l'onglet actif
        switch (tabName) {
            case "dashboard":
                dashboardTab.setStyle(activeStyle);
                break;
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
     * Charge le contenu du dashboard
     */
    private void loadDashboardContent() {
        contentArea.getChildren().clear();
        try {
            // Charger le contenu FXML dédié au dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/organisateur/dashboard_content.fxml"));
            Parent dashboardContent = loader.load();

            // Transmettre la référence du contrôleur parent au contrôleur du contenu afin de pouvoir
            // rediriger vers la création d'événement
            com.bschooleventmanager.eventmanager.controller.organisateur.OrganisateurDashboardContentController contentController = loader.getController();
            contentController.setParentController(this);

            contentArea.getChildren().add(dashboardContent);
        } catch (Exception e) {
            logger.error("Erreur lors du chargement du dashboard", e);
            // Fallback simple si le FXML échoue
            try {
                Text title = new Text("Dashboard Organisateur");
                title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #2c3e50;");
                Text subtitle = new Text("Ici sera affiché le tableau de bord avec les statistiques de vos événements");
                subtitle.setStyle("-fx-font-size: 16px; -fx-fill: #7f8c8d;");
                Button button = new Button("Créer un événement");
                button.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15 30 15 30; -fx-background-radius: 5; -fx-font-weight: bold;");
                button.setOnAction(e2 -> showCreateEvent());
                javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(20.0, title, subtitle, button);
                content.setAlignment(javafx.geometry.Pos.CENTER);
                contentArea.getChildren().add(content);
            } catch (Exception ex) {
                logger.error("Erreur fallback dashboard", ex);
            }
        }
    }

    /**
     * Charge le contenu de la liste des événements
     */
    private void loadEventsContent() {
        contentArea.getChildren().clear();
        
        try {
            Text title = new Text("Liste de mes Événements");
            title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #2c3e50;");
            
            Text subtitle = new Text("Ici sera affichée la liste de tous vos événements créés");
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

    /**
     * Charge le contenu de création d'événement
     */
    private void loadCreateEventContent() {
        contentArea.getChildren().clear();
        
        try {
            // Charger l'interface de création d'événement
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/organisateur/Events/addEvent.fxml"));
            Parent createEventContent = loader.load();
            
            // Récupérer le contrôleur pour passer une référence au dashboard
            com.bschooleventmanager.eventmanager.controller.events.CreateEventController eventController = loader.getController();
            eventController.setDashboardController(this);
            
            contentArea.getChildren().add(createEventContent);
            
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de l'interface de création d'événement", e);
            NotificationUtils.showError("Impossible de charger l'interface de création d'événement");
        }
    }
}