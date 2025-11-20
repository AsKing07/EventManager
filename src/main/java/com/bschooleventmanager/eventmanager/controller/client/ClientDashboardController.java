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
 * Contrôleur principal pour l'interface du tableau de bord client dans EventManager.
 * 
 * <p>Cette classe gère la navigation entre les différents onglets et le contenu principal
 * de l'interface client. Elle orchestre l'affichage dynamique du contenu dans une zone
 * centrale selon l'onglet sélectionné par l'utilisateur.</p>
 * 
 * <p><strong>Fonctionnalités principales :</strong></p>
 * <ul>
 *   <li>Navigation par onglets (Événements, Réservations, Profil)</li>
 *   <li>Affichage dynamique du contenu dans la zone principale</li>
 *   <li>Gestion de session utilisateur avec personnalisation d'accueil</li>
 *   <li>Déconnexion sécurisée avec nettoyage de session</li>
 *   <li>Navigation vers les détails d'événements et formulaires</li>
 *   <li>Interface de paiement intégrée</li>
 * </ul>
 * 
 * <p><strong>Architecture de navigation :</strong></p>
 * <ul>
 *   <li><strong>Onglet Événements :</strong> Liste des événements disponibles avec filtres</li>
 *   <li><strong>Onglet Réservations :</strong> Historique des réservations client</li>
 *   <li><strong>Onglet Profil :</strong> Interface de gestion du profil utilisateur</li>
 * </ul>
 * 
 * <p><strong>Gestion du contenu dynamique :</strong></p>
 * <ul>
 *   <li>Chargement FXML selon l'onglet sélectionné</li>
 *   <li>Injection de références aux contrôleurs enfants</li>
 *   <li>Gestion des erreurs de chargement avec notifications</li>
 *   <li>Application automatique des styles CSS</li>
 * </ul>
 * 
 * <p><strong>Workflows supportés :</strong></p>
 * <ol>
 *   <li>Consultation des événements → Détails → Réservation → Paiement</li>
 *   <li>Gestion des réservations → Consultation → Paiement/Annulation</li>
 *   <li>Gestion du profil utilisateur → Modification des informations</li>
 * </ol>
 * 
 * <p><strong>Exemple d'utilisation FXML :</strong></p>
 * <pre>{@code
 * <StackPane fx:id="contentArea"/>
 * <Button fx:id="eventsTab" text="Événements" onAction="#showEvents"/>
 * <Button fx:id="reservationsTab" text="Réservations" onAction="#showReservations"/>
 * <Button fx:id="profileTab" text="Profil" onAction="#showProfile"/>
 * }</pre>
 * 
 * @author EventManager Team
 * @version 1.0
 * @since 1.0
 * 
 * @see ClientEventsController
 * @see ClientEventDetailsController
 * @see ClientHistoriqueReservationsController
 * @see ReservationController
 * @see PaymentController
 * @see com.bschooleventmanager.eventmanager.controller.shared.ProfileController
 */
public class ClientDashboardController {
    /** Logger pour le traçage des actions et erreurs du dashboard client */
    private static final Logger logger = LoggerFactory.getLogger(ClientDashboardController.class);

    // === Éléments FXML ===
    
    /** Texte d'accueil personnalisé avec le nom de l'utilisateur connecté */
    @FXML
    private Text welcomeText;
    
    /** Affichage de la version de l'application */
    @FXML
    private Text versionText;
    
    /** Bouton de déconnexion pour terminer la session */
    @FXML
    private Button logoutButton;
    
    /** Onglet pour accéder à la liste des événements */
    @FXML
    private Button eventsTab;
    
    /** Onglet pour accéder à l'historique des réservations */
    @FXML
    private Button reservationsTab;
    
    /** Onglet pour accéder au profil utilisateur */
    @FXML
    private Button profileTab;
    
    /** Zone de contenu principal où s'affichent les différentes interfaces */
    @FXML
    private StackPane contentArea;

    /**
     * Initialise le contrôleur après le chargement du FXML.
     * 
     * <p>Configure l'interface utilisateur avec les informations de l'utilisateur
     * connecté et affiche par défaut la liste des événements. Cette méthode
     * personnalise l'accueil et prépare l'interface pour l'utilisation.</p>
     * 
     * <p><strong>Opérations d'initialisation :</strong></p>
     * <ul>
     *   <li>Récupération de l'utilisateur depuis SessionManager</li>
     *   <li>Personnalisation du message d'accueil</li>
     *   <li>Affichage de la version de l'application</li>
     *   <li>Chargement par défaut de la liste des événements</li>
     * </ul>
     * 
     * <p><strong>Gestion de session :</strong></p>
     * <ul>
     *   <li>Vérification de la présence d'un utilisateur connecté</li>
     *   <li>Récupération sécurisée des informations utilisateur</li>
     *   <li>Affichage conditionnel selon l'état de la session</li>
     * </ul>
     * 
     * @author @AsKing07 Charbel SONON
     * @since 1.0
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
     * Affiche la liste des événements disponibles pour consultation.
     * 
     * <p>Charge l'interface de consultation des événements avec filtres et
     * options de recherche. Met à jour la navigation pour indiquer l'onglet
     * actif et configure le contrôleur enfant avec une référence au dashboard.</p>
     * 
     * <p><strong>Fonctionnalités de la liste des événements :</strong></p>
     * <ul>
     *   <li>Affichage tabulaire avec colonnes informatives</li>
     *   <li>Filtres par type d'événement et critères de recherche</li>
     *   <li>Actions de consultation des détails</li>
     *   <li>Navigation vers les formulaires de réservation</li>
     * </ul>
     * 
     * @see ClientEventsController
     * @see #setActiveTab(String)
     * @see #loadEventsContent()
     * 
     * @since 1.0
     */
    @FXML
    public void showEvents() {
        logger.info("Affichage des événements client");
        setActiveTab("events");
        loadEventsContent();
    }

    /**
     * Affiche le profil de l'utilisateur connecté.
     * 
     * <p>Charge l'interface de consultation et de modification du profil et configure le contrôleur enfant avec une référence au dashboard.</p>
     * 
     * <p><strong>Fonctionnalités de la page profil :</strong></p>
     * <ul>
     *   <li>Affichage des informations du profil</li>
     *   <li>Modification des informations personnelles</li>
     *   <li>Modification du mot de passe</li>
     * </ul>
     * 
     * @see ClientEventsController
     * @see #setActiveTab(String)
     * @see #loadEventsContent()
     * 
     * @author @AsKing07 Charbel SONON
     * @since 1.0
     */
    @FXML
    private void showProfile() {
        logger.info("Affichage du profil client");
        setActiveTab("profile");
        loadProfileContent();
    }

    /**
     * Affiche l'historique des réservations
     * <p>Charge l'interface de l'historique des réservations effectuées par le client.
     * Met à jour la navigation pour indiquer l'onglet actif et configure le contrôleur
     * enfant avec une référence au dashboard.</p>
     * <p><strong>Fonctionnalités de l'historique des réservations :</strong></p>
     * <ul>
     *   <li>Affichage tabulaire des réservations avec détails</li>
     *  <li>Actions pour consulter, payer ou annuler des réservations</li>
     * </ul>
     * @see ClientHistoriqueReservationsController
     * @see #setActiveTab(String)
     * @see #loadReservationsContent()
     * @author @AsKing07 Charbel SONON
     * @since 1.0
     * 
     */
    @FXML
    public void showReservations() {
        logger.info("Affichage de l'historique des réservations client");
        setActiveTab("reservations");
        loadReservationsContent();
    }

    /**
     * Gère la déconnexion de l'utilisateur
     * <p>Termine la session utilisateur en cours, efface les données de session
     * et redirige vers la page de connexion. Assure une déconnexion sécurisée
     * avec nettoyage de session.</p>
     * <p><strong>Étapes de la déconnexion :</strong></p>
     * <ul>
     *   <li>Effacement des données de session via SessionManager</li>
     *  <li>Redirection vers l'interface de connexion</li>
     * </ul>
     * @author @AsKing07 Charbel SONON
     * @since 1.0
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
     * <p>Charge l'interface de connexion et remplace la scène actuelle
     * avec celle de connexion. Applique le CSS et centre la fenêtre.</p>
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
     * @param scene la scène à laquelle appliquer le CSS
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
     * <p>Applique des styles CSS pour indiquer visuellement
     * l'onglet actuellement sélectionné dans la navigation.</p>
     * <p><strong>Fonctionnalités :</strong></p>
     * <ul>
     *  <li>Style par défaut pour les onglets inactifs</li>
     * <li>Mise en évidence de l'onglet actif avec un style distinct</li>
     * </ul>
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
     * <p>Charge l'interface des détails d'un événement sélectionné.
     * Met à jour la navigation pour indiquer l'onglet actif et configure
     * le contrôleur enfant avec une référence au dashboard.</p>
     * <p><strong>Fonctionnalités :</strong></p>
     * <ul>
     *   <li>Affichage des informations complètes de l'événement</li>
     *   <li>Navigation vers les formulaires de réservation</li>
     * </ul>
     * 
     * @see ClientEventDetailsController
     * @see #setActiveTab(String)
     * @see #loadEventDetailsContent(Evenement)
     * @author @AsKing07 Charbel SONON
     * @since 1.0
     */
    public void showEventDetails(Evenement event) {
        logger.info("Affichage des détails de l'événement: {}", event.getNom());
        setActiveTab("events"); // Garder l'onglet événements actif
        loadEventDetailsContent(event);
    }

    /**
     * Charge le contenu des détails d'un événement
     * @param event l'événement dont les détails doivent être affichés
     * <p>Charge l'interface des détails d'un événement sélectionné.
     * Met à jour la navigation pour indiquer l'onglet actif et configure
     * le contrôleur enfant avec une référence au dashboard.</p>
     * <p><strong>Fonctionnalités :</strong></p>
     * <ul>
     *   <li>Affichage des informations complètes de l'événement</li>
     *  <li>Navigation vers les formulaires de réservation</li>
     * </ul>
     * @since 1.0
     * @author @AsKing07 Charbel SONON
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
    
    /**
     * Charge le contenu de la liste des événements
     * <p>Charge l'interface de consultation des événements avec filtres et
     * options de recherche. Met à jour la navigation pour indiquer l'onglet
     * actif et configure le contrôleur enfant avec une référence au dashboard.</p>
     * <p><strong>Fonctionnalités de la liste des événements :</strong></p>
     * <ul>
     *  <li>Affichage tabulaire avec colonnes informatives</li>
     *  <li>Filtres par type d'événement et critères de recherche</li>
     *  <li>Actions de consultation des détails</li>
     * </ul>
     * @see ClientEventsController
     * @see #setActiveTab(String)
     * @see #loadEventsContent()
     * @since 1.0
     * @author @koki-pickles Yvonne NJOKI
     */ 
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
     * <p>Charge l'interface de consultation et de modification du profil et configure le contrôleur enfant avec une référence au dashboard.</p>
     * <p><strong>Fonctionnalités de la page profil :</strong></p
     * <ul>
     *   <li>Affichage des informations du profil</li>
     *  <li>Modification des informations personnelles</li>
     * </ul>
     * @author @AsKing07 Charbel SONON
     * @since 1.0
     * 
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
     * @param event l'événement à réserver
     * <p>Charge le formulaire de réservation pour l'événement sélectionné.
     * Met à jour la navigation pour indiquer l'onglet actif et configure
     * le contrôleur enfant avec une référence au dashboard.</p>
     * <p><strong>Fonctionnalités :</strong></p>
     * <ul>
     *   <li>Collecte des informations nécessaires à la réservation</li>
     *  <li>Validation des données saisies</li>
     *  <li>Soumission de la réservation</li>
     * <li>Redirection vers la page de paiement après réservation</li>
     * </ul>
     * @see ReservationController
     * @see #setActiveTab(String)
     * @see #loadReservationFormContent(Evenement)
     * @since 1.0
     * @author @AsKing07 Charbel SONON
     */
    public void showReservationForm(Evenement event) {
        logger.info("Affichage du formulaire de réservation pour l'événement: {}", event.getNom());
        setActiveTab("events"); // Garder l'onglet événements actif
        loadReservationFormContent(event);
    }

    /**
     * Charge le formulaire de réservation
     * @param event l'événement à réserver
     * <p>Charge le formulaire de réservation pour l'événement sélectionné.
     * Met à jour la navigation pour indiquer l'onglet actif et configure
     * le contrôleur enfant avec une référence au dashboard.</p>
     * <p><strong>Fonctionnalités :</strong></p>
     * <ul>
     *   <li>Collecte des informations nécessaires à la réservation</li>
     *  <li>Validation des données saisies</li>
     *  <li>Soumission de la réservation</li>
     * <li>Redirection vers la page de paiement après réservation</li>
     * </ul>
     * @since 1.0
     * @author @AsKing07 Charbel SONON
     * @see ReservationController
     * @see #setActiveTab(String)
     * @see #loadReservationFormContent(Evenement)
     * 
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
     * <p>Charge l'interface de l'historique des réservations effectuées par le client.
     * Met à jour la navigation pour indiquer l'onglet actif et configure le contrôleur
     * enfant avec une référence au dashboard.</p>
     * <p><strong>Fonctionnalités de l'historique des réservations :</strong></p>
     * <ul>
     *  <li>Affichage tabulaire des réservations avec détails</li>
     * <li>Actions pour consulter, payer ou annuler des réservations</li>
     * </ul>
     * @see ClientHistoriqueReservationsController
     * @see #setActiveTab(String)
     * @see #loadReservationsContent()
     * @author @AsKing07 Charbel SONON
     * @since 1.0
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
     * @param paymentContent le contenu de l'interface de paiement à afficher
     * <p>Charge l'interface de paiement dans la zone de contenu principale.
     * <p><strong>Fonctionnalités :</strong></p>
     * <ul>
     *  <li>Intégration avec les passerelles de paiement</li>
     * <li>Collecte sécurisée des informations de paiement</li>
     * <li>Validation et traitement des paiements</li>
     * </ul>
     * @since 1.0
     * 
     * 
     */
    public void showPaymentInterface(Parent paymentContent) {
        logger.info("Affichage de l'interface de paiement");
        contentArea.getChildren().clear();
        contentArea.getChildren().add(paymentContent);
    }
}