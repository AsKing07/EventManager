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
import com.bschooleventmanager.eventmanager.model.Evenement;
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
    private Button reservationsTab;
    
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
    public void showEvents() {
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
     * Affiche l'historique des réservations
     */
    @FXML
    public void showReservations() {
        logger.info("Affichage de l'historique des réservations client");
        setActiveTab("reservations");
        loadReservationsContent();
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
        reservationsTab.setStyle(defaultStyle);
        profileTab.setStyle(defaultStyle);
        
        // Mettre en évidence l'onglet actif
        switch (tabName) {
            case "events":
                eventsTab.setStyle(activeStyle);
                break;
            case "reservations":
                reservationsTab.setStyle(activeStyle);
                break;
            case "profile":
                profileTab.setStyle(activeStyle);
                break;
            default:
                logger.warn("Onglet inconnu: {}", tabName);
        }
    }

    /**
     * Affiche les détails d'un événement spécifique
     */
    public void showEventDetails(Evenement event) {
        logger.info("Affichage des détails de l'événement: {}", event.getNom());
        setActiveTab("events"); // Garder l'onglet événements actif
        loadEventDetailsContent(event);
    }

    /**
     * Charge le contenu des détails d'un événement
     */
    private void loadEventDetailsContent(Evenement event) {
        contentArea.getChildren().clear();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/clientEventDetails.fxml"));
            Parent eventDetailsRoot = loader.load();
            
            // Récupérer le contrôleur et configurer les données
            ClientEventDetailsController detailsController = loader.getController();
            detailsController.setDashboardController(this); 
            detailsController.setEventData(event);
            
            contentArea.getChildren().add(eventDetailsRoot);
            
            logger.info("Contenu des détails de l'événement chargé avec succès");
            
        } catch (IOException e) {
            logger.error("Erreur lors du chargement des détails de l'événement", e);
            NotificationUtils.showError("Impossible de charger les détails de l'événement");
        }
    }
    private void loadEventsContent()   {
        contentArea.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/eventsList.fxml"));
            Parent eventsRoot = loader.load();
            
            // Récupérer le contrôleur et lui passer une référence au dashboard
            ClientEventsController eventsController = loader.getController();
            eventsController.setDashboardController(this);
            
            contentArea.getChildren().add(eventsRoot);
            logger.info("Events content loaded successfully");
        } catch (Exception e) {
            
            logger.error("Erreur de chargement de l'UI des éveènements", e);
            NotificationUtils.showError("Impossible de charger la liste des événements");
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
     * Affiche le formulaire de réservation pour un événement
     */
    public void showReservationForm(Evenement event) {
        logger.info("Affichage du formulaire de réservation pour l'événement: {}", event.getNom());
        setActiveTab("events"); // Garder l'onglet événements actif
        loadReservationFormContent(event);
    }

    /**
     * Charge le formulaire de réservation
     */
    private void loadReservationFormContent(Evenement event) {
        contentArea.getChildren().clear();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/reservationForm.fxml"));
            Parent reservationFormRoot = loader.load();
            
            // Récupérer le contrôleur et configurer les données
            ReservationController reservationController = loader.getController();
            reservationController.setDashboardController(this);
            reservationController.setEventData(event);
            
            contentArea.getChildren().add(reservationFormRoot);
            
            logger.info("Formulaire de réservation chargé avec succès");
            
        } catch (IOException e) {
            logger.error("Erreur lors du chargement du formulaire de réservation", e);
            NotificationUtils.showError("Impossible de charger le formulaire de réservation");
        }
    }

    /**
     * Charge le contenu de l'historique des réservations
     */
    private void loadReservationsContent() {
        contentArea.getChildren().clear();
        
        try {
            // Charger l'interface des réservations
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/reservationsHistory.fxml"));
            Parent reservationsRoot = loader.load();
            
            // Récupérer le contrôleur et lui passer une référence au dashboard
            ClientHistoriqueReservationsController reservationsController = loader.getController();
            reservationsController.setDashboardController(this);
            
            contentArea.getChildren().add(reservationsRoot);
            
            logger.info("Interface des réservations chargée avec succès");
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement de l'historique des réservations", e);
            NotificationUtils.showError("Impossible de charger l'historique des réservations");
        }
    }

    /**
     * Affiche l'interface de paiement dans la zone de contenu
     */
    public void showPaymentInterface(Parent paymentContent) {
        logger.info("Affichage de l'interface de paiement");
        contentArea.getChildren().clear();
        contentArea.getChildren().add(paymentContent);
    }
}