package com.bschooleventmanager.eventmanager.controller.organisateur;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.bschooleventmanager.eventmanager.model.Evenement;
import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;
import com.bschooleventmanager.eventmanager.model.Utilisateur;
import com.bschooleventmanager.eventmanager.util.SessionManager;
import com.bschooleventmanager.eventmanager.util.NotificationUtils;
import com.bschooleventmanager.eventmanager.util.AppConfig;
import com.bschooleventmanager.eventmanager.controller.shared.ProfileController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Contrôleur principal pour l'interface Organisateur dans EventManager.
 * 
 * <p>Cette classe gère la navigation principale de l'espace organisateur, orchestrant
 * l'affichage des différents onglets et la gestion du contenu dynamique. Elle fournit
 * une interface unifiée pour la gestion complète des événements par les organisateurs.</p>
 * 
 * <p><strong>Fonctionnalités principales :</strong></p>
 * <ul>
 *   <li>Navigation entre les différents onglets (Dashboard, Événements, Profil)</li>
 *   <li>Gestion dynamique du contenu via StackPane</li>
 *   <li>Interface de création et modification d'événements</li>
 *   <li>Gestion de session et déconnexion sécurisée</li>
 *   <li>Intégration avec les contrôleurs spécialisés</li>
 * </ul>
 * 
 * <p><strong>Architecture de navigation :</strong></p>
 * <ul>
 *   <li><strong>Dashboard :</strong> Métriques, statistiques et vue d'ensemble</li>
 *   <li><strong>Événements :</strong> Liste, gestion et actions sur les événements</li>
 *   <li><strong>Profil :</strong> Gestion des informations personnelles</li>
 *   <li><strong>Création :</strong> Interface de création d'événements</li>
 *   <li><strong>Modification :</strong> Interface de modification d'événements existants</li>
 * </ul>
 * 
 * <p><strong>Gestion de contenu dynamique :</strong></p>
 * <ul>
 *   <li>Chargement FXML à la demande pour optimisation mémoire</li>
 *   <li>Injection de contrôleurs avec références croisées</li>
 *   <li>Gestion des états d'onglets avec styles CSS</li>
 *   <li>Fallback gracieux en cas d'erreur de chargement</li>
 * </ul>
 * 
 * <p><strong>Sécurité et session :</strong></p>
 * <ul>
 *   <li>Vérification continue de la session utilisateur</li>
 *   <li>Transmission sécurisée de l'ID organisateur</li>
 *   <li>Nettoyage de session lors de la déconnexion</li>
 *   <li>Redirection automatique vers login si session expirée</li>
 * </ul>
 * 
 * <p><strong>Interface utilisateur :</strong></p>
 * <ul>
 *   <li>Barre de navigation avec onglets stylisés</li>
 *   <li>Zone de contenu dynamique (StackPane)</li>
 *   <li>Informations utilisateur et version application</li>
 *   <li>Bouton de déconnexion sécurisé</li>
 * </ul>
 * 
 * <p><strong>Intégrations FXML :</strong></p>
 * <ul>
 *   <li><em>/fxml/organisateur/dashboard_content.fxml</em> → Dashboard content</li>
 *   <li><em>/fxml/organisateur/Events/eventsList.fxml</em> → Liste des événements</li>
 *   <li><em>/fxml/shared/profile.fxml</em> → Interface de profil</li>
 *   <li><em>/fxml/organisateur/Events/addEvent.fxml</em> → Création d'événement</li>
 *   <li><em>/fxml/organisateur/Events/editEvent.fxml</em> → Modification d'événement</li>
 * </ul>
 * 
 * <p><strong>Gestion d'erreurs :</strong></p>
 * <ul>
 *   <li>Fallback interface en cas d'échec de chargement FXML</li>
 *   <li>Logging détaillé des erreurs pour diagnostic</li>
 *   <li>Notifications utilisateur via NotificationUtils</li>
 *   <li>Préservation de l'état interface en cas d'erreur</li>
 * </ul>
 * 
 * <p><strong>Workflow typique :</strong></p>
 * <ol>
 *   <li>Initialisation avec informations utilisateur</li>
 *   <li>Affichage du dashboard par défaut</li>
 *   <li>Navigation utilisateur entre les onglets</li>
 *   <li>Chargement dynamique du contenu demandé</li>
 *   <li>Gestion des actions (création, modification, etc.)</li>
 *   <li>Déconnexion et nettoyage de session</li>
 * </ol>
 * 
 * <p><strong>Exemple d'utilisation :</strong></p>
 * <pre>{@code
 * // Lors du chargement depuis le login
 * FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/organisateur/dashboard.fxml"));
 * Parent root = loader.load();
 * OrganisateurDashboardController controller = loader.getController();
 * // L'initialisation automatique configure l'interface
 * }</pre>
 * 
 * @author @AsKing07 Charbel SONON
 * @version 1.0
 * @since 1.0
 * 
 * @see OrganisateurDashboardContentController
 * @see OrganisateurEventListController
 * @see com.bschooleventmanager.eventmanager.controller.events.CreateEventController
 * @see com.bschooleventmanager.eventmanager.controller.events.ModifyEventController
 * @see com.bschooleventmanager.eventmanager.controller.shared.ProfileController
 * @see com.bschooleventmanager.eventmanager.util.SessionManager
 */
public class OrganisateurDashboardController {
    /** Logger pour traçage des opérations de navigation et gestion d'interface. */
    private static final Logger logger = LoggerFactory.getLogger(OrganisateurDashboardController.class);

    // === Éléments FXML - Interface utilisateur ===
    
    /** Label d'affichage du message de bienvenue personnalisé avec nom utilisateur. */
    @FXML
    private Text welcomeText;
    
    /** Label d'affichage de la version de l'application depuis AppConfig. */
    @FXML
    private Text versionText;
    
    /** Bouton de déconnexion sécurisée avec nettoyage de session. */
    @FXML
    private Button logoutButton;
    
    /** Bouton onglet Dashboard pour navigation vers vue d'ensemble. */
    @FXML
    private Button dashboardTab;
    
    /** Bouton onglet Événements pour navigation vers liste des événements. */
    @FXML
    private Button eventsTab;
    
    /** Bouton onglet Profil pour navigation vers gestion du profil utilisateur. */
    @FXML
    private Button profileTab;
    
    /** Zone de contenu dynamique pour affichage des différentes vues FXML. */
    @FXML
    private StackPane contentArea;

    /**
     * Initialise le contrôleur principal de l'interface organisateur.
     * 
     * <p>Cette méthode est appelée automatiquement par JavaFX après le chargement
     * du fichier FXML principal. Elle configure l'interface avec les informations
     * de l'utilisateur connecté et affiche le contenu par défaut.</p>
     * 
     * <p><strong>Configuration effectuée :</strong></p>
     * <ul>
     *   <li><strong>Message de bienvenue :</strong> Personnalisation avec nom utilisateur</li>
     *   <li><strong>Version application :</strong> Affichage depuis AppConfig</li>
     *   <li><strong>Contenu initial :</strong> Chargement automatique du dashboard</li>
     *   <li><strong>Validation session :</strong> Vérification de l'utilisateur connecté</li>
     * </ul>
     * 
     * <p><strong>Gestion de session :</strong></p>
     * <ul>
     *   <li>Récupération automatique de l'utilisateur via SessionManager</li>
     *   <li>Affichage conditionnel selon l'état de la session</li>
     *   <li>Configuration des informations contextuelles</li>
     * </ul>
     * 
     * @see SessionManager#getUtilisateurConnecte()
     * @see AppConfig#getAppVersion()
     * @see #showDashboard()
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
     * Affiche le contenu du Dashboard principal avec métriques et statistiques.
     * 
     * <p>Cette méthode charge l'interface de tableau de bord qui présente une vue
     * d'ensemble des activités de l'organisateur avec des métriques temps réel,
     * graphiques et options d'export de données.</p>
     * 
     * <p><strong>Contenu affiché :</strong></p>
     * <ul>
     *   <li><strong>Métriques :</strong> Nombre d'événements, revenus, taux de remplissage</li>
     *   <li><strong>Graphiques :</strong> Répartition par type, évolution des revenus</li>
     *   <li><strong>Filtres :</strong> Date et type d'événement pour analyse ciblée</li>
     *   <li><strong>Actions rapides :</strong> Bouton de création d'événement</li>
     * </ul>
     * 
     * <p><strong>Interface responsive :</strong></p>
     * <ul>
     *   <li>Mise à jour automatique des styles d'onglets</li>
     *   <li>Chargement asynchrone du contenu FXML</li>
     *   <li>Injection du contrôleur parent pour navigation</li>
     * </ul>
     * 
     * @see #loadDashboardContent()
     * @see #setActiveTab(String)
     * @see OrganisateurDashboardContentController
     */
    @FXML
    public void showDashboard() {
        logger.info("Affichage du dashboard organisateur");
        setActiveTab("dashboard");
        loadDashboardContent();
    }

    /**
     * Affiche la liste complète des événements de l'organisateur.
     * 
     * <p>Cette méthode charge l'interface de gestion des événements qui permet
     * de visualiser, modifier, supprimer et consulter les statistiques de tous
     * les événements créés par l'organisateur connecté.</p>
     * 
     * <p><strong>Fonctionnalités disponibles :</strong></p>
     * <ul>
     *   <li><strong>Vue tableau :</strong> Liste complète avec colonnes triables</li>
     *   <li><strong>Actions en ligne :</strong> Modifier, supprimer, voir statistiques</li>
     *   <li><strong>Filtrage :</strong> Par statut, date, type d'événement</li>
     *   <li><strong>Gestion :</strong> Confirmation de suppression, navigation modification</li>
     * </ul>
     * 
     * <p><strong>Configuration automatique :</strong></p>
     * <ul>
     *   <li>Transmission automatique de l'ID organisateur</li>
     *   <li>Chargement initial des événements</li>
     *   <li>Configuration des permissions d'accès</li>
     * </ul>
     * 
     * <p><strong>Sécurité :</strong></p>
     * <ul>
     *   <li>Vérification de session avant chargement</li>
     *   <li>Filtrage automatique par organisateur</li>
     *   <li>Validation des permissions pour chaque action</li>
     * </ul>
     * 
     * @see #loadEventsContent()
     * @see OrganisateurEventListController
     * @see SessionManager#getUtilisateurConnecte()
     */
    @FXML
    public void showEvents() {
        logger.info("Affichage des événements organisateur");
        setActiveTab("events");
        loadEventsContent();
    }

    /**
     * Affiche l'interface de gestion du profil utilisateur.
     * 
     * <p>Cette méthode charge l'interface de profil partagée qui permet à
     * l'organisateur de consulter et modifier ses informations personnelles,
     * paramètres de compte et préférences.</p>
     * 
     * <p><strong>Informations gérées :</strong></p>
     * <ul>
     *   <li><strong>Données personnelles :</strong> Nom, email, informations contact</li>
     *   <li><strong>Sécurité :</strong> Modification du mot de passe</li>
     *   <li><strong>Préférences :</strong> Paramètres d'affichage et notifications</li>
     * </ul>
     * 
     * <p><strong>Interface partagée :</strong></p>
     * <ul>
     *   <li>Utilisation du contrôleur ProfileController commun</li>
     *   <li>Adaptation automatique au type d'utilisateur</li>
     *   <li>Validation et sauvegarde sécurisées</li>
     * </ul>
     * 
     * @see #loadProfileContent()
     * @see com.bschooleventmanager.eventmanager.controller.shared.ProfileController
     */
    @FXML
    private void showProfile() {
        logger.info("Affichage du profil organisateur");
        setActiveTab("profile");
        loadProfileContent();
    }

    /**
     * Affiche l'interface de création d'un nouvel événement.
     * 
     * <p>Cette méthode charge l'interface de création d'événements qui guide
     * l'organisateur à travers le processus complet de création avec validation
     * en temps réel et interface adaptative selon le type d'événement.</p>
     * 
     * <p><strong>Processus de création :</strong></p>
     * <ul>
     *   <li><strong>Sélection type :</strong> Concert, Spectacle, Conférence</li>
     *   <li><strong>Interface adaptative :</strong> Champs spécifiques au type</li>
     *   <li><strong>Validation complète :</strong> Temps réel avec feedback</li>
     *   <li><strong>Intégration services :</strong> Sauvegarde via EvenementService</li>
     * </ul>
     * 
     * <p><strong>Navigation :</strong></p>
     * <ul>
     *   <li>Aucun onglet actif (interface dédiée)</li>
     *   <li>Retour automatique après création réussie</li>
     *   <li>Injection de référence pour navigation de retour</li>
     * </ul>
     * 
     * @see #loadCreateEventContent()
     * @see com.bschooleventmanager.eventmanager.controller.events.CreateEventController
     */
    @FXML
    public void showCreateEvent() {
        logger.info("Affichage de l'interface de création d'événement");
        setActiveTab(""); // Aucun onglet actif pour la création d'événement
        loadCreateEventContent();
    }

    /**
     * Affiche l'interface de modification d'un événement existant.
     * 
     * <p>Cette méthode charge l'interface de modification pré-configurée avec
     * les données de l'événement sélectionné. Elle applique les contraintes
     * de sécurité et valide les permissions de modification.</p>
     * 
     * <p><strong>Fonctionnalités de modification :</strong></p>
     * <ul>
     *   <li><strong>Pré-remplissage :</strong> Chargement automatique des données existantes</li>
     *   <li><strong>Contraintes :</strong> Type non modifiable, validation renforcée</li>
     *   <li><strong>Sécurité :</strong> Vérification propriétaire et permissions</li>
     *   <li><strong>Interface adaptative :</strong> Selon le type d'événement existant</li>
     * </ul>
     * 
     * <p><strong>Validation et sécurité :</strong></p>
     * <ul>
     *   <li>Vérification que l'événement appartient à l'organisateur</li>
     *   <li>Contrôle des contraintes temporelles et référentielles</li>
     *   <li>Validation des modifications avant sauvegarde</li>
     * </ul>
     * 
     * @param event L'événement à modifier avec ses données complètes
     * 
     * @see #loadModifyEventContent(int, TypeEvenement)
     * @see com.bschooleventmanager.eventmanager.controller.events.ModifyEventController
     */
    @FXML
    public void showModifyEvent(Evenement event) {
        logger.info("Affichage de l'interface de modification d'événement");
        setActiveTab(""); // Aucun onglet actif
        loadModifyEventContent(event.getIdEvenement(), event.getTypeEvenement());
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
            // Charger le contenu FXML dédié au dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/organisateur/Events/eventsList.fxml"));
            Parent dashboardContent = loader.load();

            // Transmettre la référence du contrôleur parent au contrôleur du contenu afin de pouvoir
            // rediriger vers la création d'événement
            com.bschooleventmanager.eventmanager.controller.organisateur.OrganisateurEventListController contentController = loader.getController();
            contentController.setParentController(this);
            
            // CORRECTION: Transmettre l'ID de l'organisateur connecté
            Utilisateur user = SessionManager.getUtilisateurConnecte();
            if (user != null) {
                logger.info("Configuration de l'organisateur ID: {} pour le chargement des événements", user.getIdUtilisateur());
                contentController.setOrganisateurId(user.getIdUtilisateur());
            } else {
                logger.error("Aucun utilisateur connecté trouvé pour charger les événements");
                NotificationUtils.showError("Erreur: Utilisateur non connecté");
            }

            contentArea.getChildren().add(dashboardContent);
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des événements", e);
            NotificationUtils.showError("Erreur lors du chargement des événements: ");
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

    /**
     * Charge le contenu de modification d'événement
     */
    private void loadModifyEventContent(int eventId, TypeEvenement eventType) {
        contentArea.getChildren().clear();
        try {
            // Charger l'interface de modification d'événement
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/organisateur/Events/editEvent.fxml"));
            Parent modifyEventContent = loader.load();
            
            // Récupérer le contrôleur pour passer une référence au dashboard et l'événement à modifier
            com.bschooleventmanager.eventmanager.controller.events.ModifyEventController eventController = loader.getController();
            eventController.setDashboardController(this);
            eventController.setEvenementInfo(eventId, eventType);
            
            contentArea.getChildren().add(modifyEventContent);
           
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement de l'interface de modification d'événement", e);
            NotificationUtils.showError("Impossible de charger l'interface de modification d'événement");
        }
    }


}