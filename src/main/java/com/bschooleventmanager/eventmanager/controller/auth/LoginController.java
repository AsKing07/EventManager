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
 * Contrôleur pour l'interface de connexion des utilisateurs existants dans EventManager.
 * 
 * <p>Cette classe gère le processus d'authentification des utilisateurs existants,
 * incluant la validation des identifiants, la gestion de session, et la navigation
 * vers les interfaces appropriées selon le type d'utilisateur.</p>
 * 
 * <p><strong>Fonctionnalités principales :</strong></p>
 * <ul>
 *   <li>Interface de connexion avec formulaire email/mot de passe</li>
 *   <li>Authentification sécurisée via UtilisateurService</li>
 *   <li>Gestion de session avec SessionManager</li>
 *   <li>Navigation conditionnelle selon le type d'utilisateur</li>
 *   <li>Gestion des erreurs avec messages utilisateur</li>
 *   <li>Redirection vers l'interface d'inscription</li>
 * </ul>
 * 
 * <p><strong>Processus d'authentification :</strong></p>
 * <ol>
 *   <li>Validation des champs email et mot de passe</li>
 *   <li>Appel du service d'authentification</li>
 *   <li>Stockage de l'utilisateur en session</li>
 *   <li>Navigation vers le dashboard approprié</li>
 * </ol>
 * 
 * <p><strong>Navigation par type d'utilisateur :</strong></p>
 * <ul>
 *   <li><strong>ORGANISATEUR :</strong> → Dashboard Organisateur</li>
 *   <li><strong>CLIENT :</strong> → Dashboard Client</li>
 * </ul>
 * 
 * <p><strong>Gestion des erreurs :</strong></p>
 * <ul>
 *   <li>Champs vides : Warning avec message explicite</li>
 *   <li>Identifiants invalides : Erreur avec message sécurisé</li>
 *   <li>Erreurs techniques : Logging + message générique</li>
 * </ul>
 * 
 * <p><strong>Interface utilisateur :</strong></p>
 * <ul>
 *   <li>Formulaire simple et épuré</li>
 *   <li>Notifications visuelles (succès/erreur/avertissement)</li>
 *   <li>Navigation fluide entre les écrans</li>
 *   <li>Configuration adaptative des fenêtres</li>
 * </ul>
 * 
 * <p><strong>Exemple d'utilisation FXML :</strong></p>
 * <pre>{@code
 * <Button fx:id="loginButton" text="Se connecter" onAction="#handleLogin"/>
 * <TextField fx:id="emailField" promptText="Adresse email"/>
 * <PasswordField fx:id="passwordField" promptText="Mot de passe"/>
 * <Hyperlink fx:id="registerLink" text="Créer un compte" onAction="#handleRegister"/>
 * }</pre>
 * 
 * @author Charbel SONON
 * @version 1.0
 * @since 1.0
 * 
 * @see RegisterController
 * @see com.bschooleventmanager.eventmanager.service.UtilisateurService
 * @see com.bschooleventmanager.eventmanager.util.SessionManager
 * @see com.bschooleventmanager.eventmanager.model.enums.TypeUtilisateur
 */
public class LoginController {
    /** Logger pour le traçage des connexions et la gestion des erreurs d'authentification */
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    /** Champ de saisie pour l'adresse email (identifiant de connexion) */
    @FXML
    private TextField emailField;
    
    /** Champ de saisie sécurisé pour le mot de passe */
    @FXML
    private PasswordField passwordField;
    
    /** Bouton principal pour déclencher la connexion */
    @FXML
    private Button loginButton;
    
    /** Lien hypertexte pour naviguer vers l'interface d'inscription */
    @FXML
    private Hyperlink registerLink;
    
    /** Label pour afficher les messages d'erreur d'authentification */
    @FXML
    private Label errorLabel;

    /** Service de gestion des utilisateurs pour l'authentification */
    private UtilisateurService utilisateurService = new UtilisateurService();

    /**
     * Initialise le contrôleur après le chargement du FXML.
     * 
     * <p>Cette méthode est automatiquement appelée par JavaFX après
     * l'injection des éléments FXML. Elle permet d'effectuer des
     * configurations supplémentaires si nécessaires.</p>
     * 
     * <p><strong>Opérations d'initialisation :</strong></p>
     * <ul>
     *   <li>Logging du démarrage de l'interface</li>
     *   <li>Configuration éventuelle des listeners</li>
     *   <li>Préparation de l'interface utilisateur</li>
     * </ul>
     * 
     * @since 1.0
     */
    @FXML
    public void initialize() {
        // Initialisation si nécessaire
        logger.info("Initialisation de l'interface de connexion");
    }

    /**
     * Gestionnaire principal pour l'authentification des utilisateurs.
     * 
     * <p>Orchestre le processus complet de connexion en plusieurs étapes :</p>
     * <ol>
     *   <li>Récupération et validation des identifiants</li>
     *   <li>Authentification via le service utilisateur</li>
     *   <li>Gestion de la session utilisateur</li>
     *   <li>Navigation vers le dashboard approprié</li>
     * </ol>
     * 
     * <p><strong>Validations préalables :</strong></p>
     * <ul>
     *   <li>Champs email et mot de passe non vides</li>
     *   <li>Nettoyage automatique des espaces (trim)</li>
     * </ul>
     * 
     * <p><strong>Processus d'authentification :</strong></p>
     * <ul>
     *   <li>Appel sécurisé du service d'authentification</li>
     *   <li>Vérification des identifiants en base de données</li>
     *   <li>Création de la session utilisateur si succès</li>
     * </ul>
     * 
     * <p><strong>Actions en cas de succès :</strong></p>
     * <ul>
     *   <li>Stockage de l'utilisateur dans SessionManager</li>
     *   <li>Logging sécurisé de la connexion (email uniquement)</li>
     *   <li>Notification de succès à l'utilisateur</li>
     *   <li>Redirection vers le dashboard personnalisé</li>
     * </ul>
     * 
     * <p><strong>Gestion des erreurs :</strong></p>
     * <ul>
     *   <li>Champs vides : Warning avec message explicite</li>
     *   <li>Identifiants invalides : Affichage erreur + logging warning</li>
     *   <li>Erreurs techniques : Logging erreur + message générique</li>
     * </ul>
     * 
     * @throws BusinessException si les identifiants sont invalides
     * @throws Exception pour toute autre erreur technique
     * 
     * @see UtilisateurService#authentifier(String, String)
     * @see SessionManager#setUtilisateurConnecte(com.bschooleventmanager.eventmanager.model.Utilisateur)
     * @see #navigateToDashboard(com.bschooleventmanager.eventmanager.model.Utilisateur)
     * 
     * @since 1.0
     */
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
     * Gestionnaire pour rediriger vers l'interface d'inscription.
     * 
     * <p>Permet aux nouveaux utilisateurs d'accéder facilement à l'interface
     * de création de compte depuis l'écran de connexion. Assure une navigation
     * fluide entre les deux interfaces d'authentification.</p>
     * 
     * <p><strong>Actions effectuées :</strong></p>
     * <ul>
     *   <li>Logging de la redirection pour suivi</li>
     *   <li>Chargement de l'interface d'inscription</li>
     *   <li>Conservation de l'état de la fenêtre</li>
     * </ul>
     * 
     * @see #loadRegisterScene()
     * 
     * @since 1.0
     */
    @FXML
    private void handleRegister() {
        logger.info("Redirection vers l'interface d'inscription");
        loadRegisterScene();
    }

    /**
     * Charge et affiche l'interface d'inscription.
     * 
     * <p>Effectue la transition complète vers l'interface d'inscription en
     * gérant tous les aspects techniques : chargement FXML, configuration
     * de la fenêtre, application du style CSS, et gestion des erreurs.</p>
     * 
     * <p><strong>Opérations effectuées :</strong></p>
     * <ul>
     *   <li>Chargement du fichier FXML register.fxml</li>
     *   <li>Récupération des dimensions optimales via WindowUtils</li>
     *   <li>Création et configuration de la nouvelle scène</li>
     *   <li>Application du CSS si disponible</li>
     *   <li>Mise à jour du titre de la fenêtre</li>
     *   <li>Configuration de la fenêtre (centrage, etc.)</li>
     * </ul>
     * 
     * <p><strong>Gestion des erreurs :</strong></p>
     * <ul>
     *   <li>Fichier FXML introuvable : Logging + notification utilisateur</li>
     *   <li>Erreurs de chargement : Gestion gracieuse avec message d'erreur</li>
     * </ul>
     * 
     * <p><strong>Configuration de la fenêtre :</strong></p>
     * <ul>
     *   <li>Dimensions adaptatives selon les paramètres</li>
     *   <li>Titre personnalisé avec nom de l'application</li>
     *   <li>Style CSS appliqué automatiquement</li>
     *   <li>Optimisations de fenêtre via WindowUtils</li>
     * </ul>
     * 
     * @throws IOException si le fichier FXML ne peut pas être chargé
     * 
     * @see WindowUtils#getOptimalDimensions()
     * @see WindowUtils#configureStage(javafx.stage.Stage)
     * @see AppConfig#getAppTitle()
     * @see #applyCssIfAvailable(javafx.scene.Scene)
     * 
     * @since 1.0
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
     * Applique les styles CSS à la scène si le fichier est disponible.
     * 
     * <p>Tente de charger et d'appliquer le fichier CSS principal de l'application
     * pour maintenir une cohérence visuelle. En cas d'échec, l'application
     * continue de fonctionner avec les styles par défaut.</p>
     * 
     * <p><strong>Comportement :</strong></p>
     * <ul>
     *   <li>Chargement du fichier /css/styles.css depuis le classpath</li>
     *   <li>Application automatique à la scène fournie</li>
     *   <li>Gestion gracieuse des erreurs (warning + continuation)</li>
     *   <li>Aucun impact sur le fonctionnement si CSS indisponible</li>
     * </ul>
     * 
     * @param scene la scène JavaFX à laquelle appliquer le CSS
     * 
     * @since 1.0
     */
    private void applyCssIfAvailable(Scene scene) {
        try {
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        } catch (Exception e) {
            logger.warn("Impossible de charger le fichier CSS: {}", e.getMessage());
        }
    }

    /**
     * Navigue vers le dashboard approprié selon le type d'utilisateur connecté.
     * 
     * <p>Effectue une redirection intelligente vers l'interface correspondant
     * au type d'utilisateur authentifié. Gère tous les aspects techniques
     * de la navigation : chargement FXML, configuration de fenêtre, et
     * gestion d'erreurs.</p>
     * 
     * <p><strong>Navigation par type d'utilisateur :</strong></p>
     * <ul>
     *   <li><strong>ORGANISATEUR :</strong> → Dashboard Organisateur (gestion événements)</li>
     *   <li><strong>CLIENT :</strong> → Dashboard Client (consultation/réservation)</li>
     * </ul>
     * 
     * <p><strong>Opérations effectuées :</strong></p>
     * <ul>
     *   <li>Détermination du chemin FXML selon le type utilisateur</li>
     *   <li>Chargement dynamique de l'interface appropriée</li>
     *   <li>Configuration adaptative de la fenêtre</li>
     *   <li>Application des styles CSS</li>
     *   <li>Mise à jour du titre contextualisé</li>
     *   <li>Logging de la navigation pour audit</li>
     * </ul>
     * 
     * <p><strong>Gestion des erreurs :</strong></p>
     * <ul>
     *   <li>Type utilisateur non reconnu : Exception avec message explicite</li>
     *   <li>Erreur de chargement FXML : Logging + notification utilisateur</li>
     *   <li>Erreurs techniques : Gestion gracieuse avec message générique</li>
     * </ul>
     * 
     * <p><strong>Configuration de la fenêtre :</strong></p>
     * <ul>
     *   <li>Dimensions optimales calculées automatiquement</li>
     *   <li>Titre personnalisé par type d'utilisateur</li>
     *   <li>Styles appliqués de manière cohérente</li>
     *   <li>Optimisations de performance et d'affichage</li>
     * </ul>
     * 
     * @param user l'utilisateur authentifié pour lequel naviguer,
     *             ne doit pas être null et doit avoir un type valide
     * 
     * @throws IllegalArgumentException si le type d'utilisateur n'est pas reconnu
     * @throws IOException si le fichier FXML du dashboard ne peut pas être chargé
     * @throws Exception pour toute autre erreur technique
     * 
     * @see com.bschooleventmanager.eventmanager.model.enums.TypeUtilisateur
     * @see WindowUtils#getOptimalDimensions()
     * @see WindowUtils#configureStage(javafx.stage.Stage)
     * @see AppConfig#getAppTitle()
     * 
     * @since 1.0
     */
    private void navigateToDashboard(Utilisateur user) {
        logger.info("Navigation vers le dashboard pour l'utilisateur: {}", user.getEmail());
        
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


