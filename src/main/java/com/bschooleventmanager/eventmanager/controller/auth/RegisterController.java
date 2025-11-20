package com.bschooleventmanager.eventmanager.controller.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import com.bschooleventmanager.eventmanager.model.enums.TypeUtilisateur;
import com.bschooleventmanager.eventmanager.service.UtilisateurService;
import com.bschooleventmanager.eventmanager.util.NotificationUtils;
import com.bschooleventmanager.eventmanager.util.ValidationUtils;
import com.bschooleventmanager.eventmanager.util.AppConfig;
import com.bschooleventmanager.eventmanager.exception.BusinessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Contrôleur pour l'interface d'inscription des nouveaux utilisateurs dans EventManager.
 * 
 * <p>Cette classe gère l'ensemble du processus d'inscription, incluant la validation
 * des données, la création de comptes utilisateurs, et l'interface utilisateur interactive
 * avec validation en temps réel.</p>
 * 
 * <p><strong>Fonctionnalités principales :</strong></p>
 * <ul>
 *   <li>Interface d'inscription avec formulaire complet</li>
 *   <li>Validation en temps réel des champs (nom, email, mot de passe)</li>
 *   <li>Indicateur de force du mot de passe dynamique</li>
 *   <li>Vérification de correspondance des mots de passe</li>
 *   <li>Sélection du type d'utilisateur (Client/Organisateur)</li>
 *   <li>Gestion des erreurs avec messages utilisateur</li>
 *   <li>Navigation vers l'interface de connexion</li>
 * </ul>
 * 
 * <p><strong>Validation des données :</strong></p>
 * <ul>
 *   <li>Nom : minimum 2 caractères, lettres uniquement</li>
 *   <li>Email : format valide avec validation RFC</li>
 *   <li>Mot de passe : minimum 6 caractères, force évaluée</li>
 *   <li>Type utilisateur : sélection obligatoire</li>
 * </ul>
 * 
 * <p><strong>Interface utilisateur :</strong></p>
 * <ul>
 *   <li>Validation temps réel avec indicateurs visuels</li>
 *   <li>Messages d'erreur contextuels</li>
 *   <li>Notifications de succès/échec</li>
 *   <li>Navigation fluide entre les écrans</li>
 * </ul>
 * 
 * <p><strong>Exemple d'utilisation FXML :</strong></p>
 * <pre>{@code
 * <Button fx:id="registerButton" text="S'inscrire" onAction="#handleRegister"/>
 * <TextField fx:id="nomField" promptText="Nom complet"/>
 * <TextField fx:id="emailField" promptText="Email"/>
 * <PasswordField fx:id="passwordField" promptText="Mot de passe"/>
 * <ComboBox fx:id="typeComboBox" promptText="Type de compte"/>
 * }</pre>
 * 
 * @author Charbel SONON
 * @version 1.0
 * @since 1.0
 * 
 * @see LoginController
 * @see com.bschooleventmanager.eventmanager.service.UtilisateurService
 * @see com.bschooleventmanager.eventmanager.model.enums.TypeUtilisateur
 * @see com.bschooleventmanager.eventmanager.util.ValidationUtils
 */
public class RegisterController {
    /** Logger pour le traçage et la gestion des erreurs d'inscription */
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    // === Éléments FXML ===
    
    /** Champ de saisie pour le nom complet de l'utilisateur */
    @FXML
    private TextField nomField;
    
    /** Champ de saisie pour l'adresse email (identifiant de connexion) */
    @FXML
    private TextField emailField;
    
    /** Champ de saisie sécurisé pour le mot de passe */
    @FXML
    private PasswordField passwordField;
    
    /** Champ de confirmation du mot de passe pour éviter les erreurs de frappe */
    @FXML
    private PasswordField confirmPasswordField;
    
    /** ComboBox pour sélectionner le type d'utilisateur (Client/Organisateur) */
    @FXML
    private ComboBox<TypeUtilisateur> typeComboBox;
    
    /** Bouton principal pour déclencher l'inscription */
    @FXML
    private Button registerButton;
    
    /** Bouton d'annulation pour vider le formulaire et revenir à la connexion */
    @FXML
    private Button cancelButton;
    
    /** Lien hypertexte pour naviguer vers l'interface de connexion */
    @FXML
    private Hyperlink loginLink;
    
    /** Label pour afficher les messages d'erreur de validation */
    @FXML
    private Label errorLabel;

    /** Label pour afficher l'indicateur de force du mot de passe en temps réel */
    @FXML
    private Label passwordStrengthLabel;
    
    /** Label pour indiquer si les mots de passe correspondent */
    @FXML
    private Label passwordMatchLabel;

    // === Services ===
    
    /** Service de gestion des utilisateurs pour les opérations CRUD */
    private final UtilisateurService utilisateurService = new UtilisateurService();

    /**
     * Initialisation du contrôleur
     * Configure les éléments de l'interface après le chargement du FXML
     */
    @FXML
    public void initialize() {
        
        // Initialiser la ComboBox avec les types d'utilisateurs
        initializeTypeComboBox();
        
        // Configurer les validations en temps réel
        setupRealTimeValidation();
        
        // Masquer le message d'erreur au départ
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        
        // Initialiser les indicateurs
        passwordStrengthLabel.setText("Aucun");
        passwordStrengthLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11px; -fx-font-weight: bold;");
        passwordMatchLabel.setText("");
    }

    /**
     * Initialise la ComboBox des types d'utilisateurs avec affichage personnalisé.
     * 
     * <p>Configure la ComboBox pour afficher les types d'utilisateurs disponibles
     * (Client, Organisateur) avec des labels localisés et un rendu personnalisé
     * pour une meilleure expérience utilisateur.</p>
     * 
     * <p><strong>Configuration effectuée :</strong></p>
     * <ul>
     *   <li>Chargement de tous les types d'utilisateurs disponibles</li>
     *   <li>Cell Factory personnalisée pour l'affichage des éléments</li>
     *   <li>Button Cell personnalisée pour l'affichage de la sélection</li>
     *   <li>Utilisation des labels localisés via getLabel()</li>
     * </ul>
     * 
     * @see TypeUtilisateur#getLabel()
     * @see javafx.scene.control.ComboBox#setCellFactory(javafx.util.Callback)
     * 
     * @since 1.0
     */
    private void initializeTypeComboBox() {
        typeComboBox.setItems(FXCollections.observableArrayList(TypeUtilisateur.values()));
        
        // Affichage personnalisé pour les types d'utilisateurs
        typeComboBox.setCellFactory(listView -> new ListCell<TypeUtilisateur>() {
            @Override
            protected void updateItem(TypeUtilisateur item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getLabel());
                }
            }
        });
        
        typeComboBox.setButtonCell(new ListCell<TypeUtilisateur>() {
            @Override
            protected void updateItem(TypeUtilisateur item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getLabel());
                }
            }
        });
    }

    /**
     * Configure la validation en temps réel des champs du formulaire.
     * 
     * <p>Met en place des listeners sur tous les champs de saisie pour fournir
     * un feedback immédiat à l'utilisateur pendant qu'il tape. Améliore
     * significativement l'expérience utilisateur en évitant d'attendre
     * la soumission pour voir les erreurs.</p>
     * 
     * <p><strong>Validations configurées :</strong></p>
     * <ul>
     *   <li>Masquage automatique des messages d'erreur lors de la saisie</li>
     *   <li>Mise à jour de l'indicateur de force du mot de passe</li>
     *   <li>Vérification de correspondance des mots de passe</li>
     *   <li>Validation immédiate de tous les champs</li>
     * </ul>
     * 
     * <p><strong>Événements écoutés :</strong></p>
     * <ul>
     *   <li>textProperty() pour les TextField et PasswordField</li>
     *   <li>valueProperty() pour les ComboBox</li>
     * </ul>
     * 
     * @see #updatePasswordStrength()
     * @see #updatePasswordMatch()
     * @see #hideError()
     * 
     * @since 1.0
     */
    private void setupRealTimeValidation() {
        // Masquer le message d'erreur quand l'utilisateur tape
        nomField.textProperty().addListener((obs, oldText, newText) -> hideError());
        emailField.textProperty().addListener((obs, oldText, newText) -> hideError());
        passwordField.textProperty().addListener((obs, oldText, newText) -> {
            hideError();
            updatePasswordStrength();
            updatePasswordMatch();
        });
        confirmPasswordField.textProperty().addListener((obs, oldText, newText) -> {
            hideError();
            updatePasswordMatch();
        });
        typeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> hideError());
    }

    /**
     * Masque le message d'erreur et réinitialise l'affichage.
     * 
     * <p>Utilisée lors de la validation en temps réel pour cacher
     * les messages d'erreur dès que l'utilisateur corrige sa saisie.
     * Améliore la fluidité de l'interface en évitant la persistance
     * d'erreurs obsolètes.</p>
     * 
     * @since 1.0
     */
    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    /**
     * Affiche un message d'erreur à l'utilisateur.
     * 
     * <p>Centralise l'affichage des messages d'erreur avec un formatage
     * cohérent. Le message est affiché dans le label d'erreur dédié
     * et rendu visible pour attirer l'attention de l'utilisateur.</p>
     * 
     * @param message le message d'erreur à afficher à l'utilisateur,
     *                doit être clair et informatif
     * 
     * @since 1.0
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    /**
     * Met à jour l'indicateur de force du mot de passe en temps réel.
     * 
     * <p>Analyse la force du mot de passe saisi et affiche un indicateur
     * visuel avec couleur et texte correspondants. Aide l'utilisateur
     * à créer un mot de passe sécurisé en fournissant un feedback immédiat.</p>
     * 
     * <p><strong>Niveaux de force évalués :</strong></p>
     * <ul>
     *   <li>Aucun : Champ vide (gris)</li>
     *   <li>Faible : Critères minimums non respectés (rouge)</li>
     *   <li>Moyen : Critères de base respectés (orange)</li>
     *   <li>Fort : Bon équilibre de caractères (vert)</li>
     *   <li>Très fort : Tous les critères respectés (vert foncé)</li>
     * </ul>
     * 
     * <p><strong>Critères évalués :</strong></p>
     * <ul>
     *   <li>Longueur du mot de passe</li>
     *   <li>Présence de lettres minuscules/majuscules</li>
     *   <li>Présence de chiffres</li>
     *   <li>Présence de caractères spéciaux</li>
     * </ul>
     * 
     * @see ValidationUtils#getPasswordStrength(String)
     * @see ValidationUtils.PasswordStrength
     * 
     * @since 1.0
     */
    private void updatePasswordStrength() {
        String password = passwordField.getText();
        
        if (password.isEmpty()) {
            passwordStrengthLabel.setText("Aucun");
            passwordStrengthLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11px; -fx-font-weight: bold;");
            return;
        }
        
        ValidationUtils.PasswordStrength strength = ValidationUtils.getPasswordStrength(password);
        passwordStrengthLabel.setText(strength.getLabel());
        passwordStrengthLabel.setStyle("-fx-text-fill: " + strength.getColor() + "; -fx-font-size: 11px; -fx-font-weight: bold;");
    }

    /**
     * Met à jour l'indicateur de correspondance des mots de passe.
     * 
     * <p>Compare en temps réel le mot de passe principal avec sa confirmation
     * et affiche un indicateur visuel pour informer l'utilisateur de leur
     * correspondance. Prévient les erreurs de frappe lors de la création
     * du compte.</p>
     * 
     * <p><strong>États affichés :</strong></p>
     * <ul>
     *   <li>Vide : Confirmation non commencée</li>
     *   <li>"✓ Correspondent" : Mots de passe identiques (vert)</li>
     *   <li>"✗ Ne correspondent pas" : Mots de passe différents (rouge)</li>
     * </ul>
     * 
     * <p><strong>Validation effectuée :</strong></p>
     * <ul>
     *   <li>Comparaison exacte des chaînes de caractères</li>
     *   <li>Prise en compte de la casse</li>
     *   <li>Gestion des champs vides</li>
     * </ul>
     * 
     * @since 1.0
     */
    private void updatePasswordMatch() {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (confirmPassword.isEmpty()) {
            passwordMatchLabel.setText("");
            return;
        }
        
        if (password.equals(confirmPassword)) {
            passwordMatchLabel.setText("✓ Correspondent");
            passwordMatchLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px; -fx-font-weight: bold;");
        } else {
            passwordMatchLabel.setText("✗ Ne correspondent pas");
            passwordMatchLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11px; -fx-font-weight: bold;");
        }
    }

    /**
     * Gestionnaire principal pour l'inscription d'un nouvel utilisateur.
     * 
     * <p>Orchestre le processus complet d'inscription en plusieurs étapes :</p>
     * <ol>
     *   <li>Récupération et nettoyage des données du formulaire</li>
     *   <li>Validation complète des données (champs obligatoires, formats, contraintes)</li>
     *   <li>Création du compte via le service utilisateur</li>
     *   <li>Gestion des retours (succès/échec) avec notifications</li>
     *   <li>Navigation vers l'interface de connexion en cas de succès</li>
     * </ol>
     * 
     * <p><strong>Validations effectuées :</strong></p>
     * <ul>
     *   <li>Champs obligatoires non vides</li>
     *   <li>Format email RFC valide</li>
     *   <li>Nom avec lettres uniquement, minimum 2 caractères</li>
     *   <li>Mot de passe force minimum (critères de sécurité)</li>
     *   <li>Correspondance des mots de passe</li>
     *   <li>Type d'utilisateur sélectionné</li>
     * </ul>
     * 
     * <p><strong>Gestion des erreurs :</strong></p>
     * <ul>
     *   <li>Erreurs métier : affichage message spécifique + warning</li>
     *   <li>Erreurs techniques : message générique + logging</li>
     *   <li>Prevention double-clic : désactivation temporaire du bouton</li>
     *   <li>Focus automatique sur le champ en erreur</li>
     * </ul>
     * 
     * <p><strong>Actions en cas de succès :</strong></p>
     * <ul>
     *   <li>Logging de l'inscription avec email</li>
     *   <li>Notification de bienvenue personnalisée</li>
     *   <li>Redirection automatique vers la connexion</li>
     * </ul>
     * 
     * @throws BusinessException si les données sont invalides ou l'email existe déjà
     * @throws Exception pour toute autre erreur technique
     * 
     * @see #validateRequiredFields(String, String, String, String, TypeUtilisateur)
     * @see #validatePasswordStrength(String)
     * @see UtilisateurService#inscrire(String, String, String, String)
     * @see #handleGoToLogin()
     * 
     * @since 1.0
     */
    @FXML
    private void handleRegister() {
        
        try {
            // Récupération des données du formulaire
            String nom = nomField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            TypeUtilisateur typeUtilisateur = typeComboBox.getValue();

            // === VALIDATION DES DONNÉES ===
            
            // Validation des champs obligatoires
            if (!validateRequiredFields(nom, email, password, confirmPassword, typeUtilisateur)) {
                return;
            }

            // Validation du format email
            if (!ValidationUtils.isEmailValid(email)) {
                showError("Veuillez saisir une adresse email valide");
                emailField.requestFocus();
                return;
            }

            // Validation de la force du mot de passe
            if (!validatePasswordStrength(password)) {
                return;
            }

            // Validation de la confirmation du mot de passe
            if (!password.equals(confirmPassword)) {
                showError("Les mots de passe ne correspondent pas");
                confirmPasswordField.requestFocus();
                return;
            }

            // === CRÉATION DU COMPTE ===
            
            // Désactiver le bouton pour éviter les doubles clics
            registerButton.setDisable(true);
            
            // Créer le compte via le service
            utilisateurService.inscrire(
                nom, email, password, typeUtilisateur.name()
            );

            logger.info("✓ Inscription réussie pour l'utilisateur: {}", email);

            // === NOTIFICATIONS ET CONFIRMATIONS ===
            
            // Afficher une notification de succès
            NotificationUtils.showSuccess(
                "Inscription réussie !\n\n" +
                "Bienvenue " + nom + " !\n" +
                "Votre compte " + typeUtilisateur.getLabel() + " a été créé avec succès.\n" +
                "Vous pouvez maintenant vous connecter."
            );


            // Rediriger vers l'interface de connexion
            handleGoToLogin();

        } catch (BusinessException e) {
            logger.warn("Erreur métier lors de l'inscription: {}", e.getMessage());
            showError(e.getMessage());
            NotificationUtils.showWarning(e.getMessage());
            
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de l'inscription", e);
            showError("Une erreur inattendue s'est produite. Veuillez réessayer.");
            NotificationUtils.showError("Erreur lors de l'inscription");
            
        } finally {
            // Réactiver le bouton
            registerButton.setDisable(false);
        }
    }

    /**
     * Valide que tous les champs obligatoires sont correctement remplis.
     * 
     * <p>Effectue une validation complète et détaillée de tous les champs
     * obligatoires du formulaire d'inscription. En cas d'erreur, affiche
     * un message spécifique et place le focus sur le champ problématique
     * pour guider l'utilisateur dans la correction.</p>
     * 
     * <p><strong>Validations par champ :</strong></p>
     * <ul>
     *   <li><strong>Nom :</strong> Non null, minimum 2 caractères, lettres uniquement</li>
     *   <li><strong>Email :</strong> Non null et non vide</li>
     *   <li><strong>Mot de passe :</strong> Non null et non vide</li>
     *   <li><strong>Confirmation :</strong> Non null et non vide</li>
     *   <li><strong>Type utilisateur :</strong> Sélection obligatoire</li>
     * </ul>
     * 
     * <p><strong>Comportement en cas d'erreur :</strong></p>
     * <ul>
     *   <li>Affichage d'un message d'erreur spécifique</li>
     *   <li>Placement du focus sur le champ en erreur</li>
     *   <li>Arrêt immédiat de la validation (fail-fast)</li>
     * </ul>
     * 
     * @param nom le nom complet saisi par l'utilisateur
     * @param email l'adresse email saisie par l'utilisateur
     * @param password le mot de passe principal saisi
     * @param confirmPassword la confirmation du mot de passe
     * @param type le type d'utilisateur sélectionné (Client/Organisateur)
     * 
     * @return true si tous les champs sont valides, false sinon
     * 
     * @see ValidationUtils#isNonNull(String)
     * @see ValidationUtils#isNomValid(String)
     * @see #showError(String)
     * 
     * @since 1.0
     */
    private boolean validateRequiredFields(String nom, String email, String password, 
                                         String confirmPassword, TypeUtilisateur type) {
        if (!ValidationUtils.isNonNull(nom) || !ValidationUtils.isNomValid(nom)) {
            showError("Veuillez saisir un nom valide (au moins 2 caractères, lettres uniquement)");
            nomField.requestFocus();
            return false;
        }

        if (!ValidationUtils.isNonNull(email)) {
            showError("Veuillez saisir votre adresse email");
            emailField.requestFocus();
            return false;
        }

        if (!ValidationUtils.isNonNull(password)) {
            showError("Veuillez saisir un mot de passe");
            passwordField.requestFocus();
            return false;
        }

        if (!ValidationUtils.isNonNull(confirmPassword)) {
            showError("Veuillez confirmer votre mot de passe");
            confirmPasswordField.requestFocus();
            return false;
        }

        if (type == null) {
            showError("Veuillez sélectionner le type de compte");
            typeComboBox.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Valide la force du mot de passe avec des critères de sécurité renforcés.
     * 
     * <p>Applique une politique de mot de passe robuste pour assurer la sécurité
     * des comptes utilisateurs. La validation comprend des critères de longueur
     * minimale et d'analyse de la force du mot de passe.</p>
     * 
     * <p><strong>Critères de validation :</strong></p>
     * <ul>
     *   <li><strong>Longueur minimale :</strong> 6 caractères minimum</li>
     *   <li><strong>Force minimale :</strong> Au moins "Moyen" (rejette "Faible")</li>
     *   <li><strong>Composition recommandée :</strong> Lettres + chiffres minimum</li>
     * </ul>
     * 
     * <p><strong>Niveaux de force acceptés :</strong></p>
     * <ul>
     *   <li>❌ <strong>Faible :</strong> Rejeté avec message d'aide</li>
     *   <li>✅ <strong>Moyen :</strong> Accepté (lettres + chiffres)</li>
     *   <li>✅ <strong>Fort :</strong> Accepté (lettres + chiffres + casse)</li>
     *   <li>✅ <strong>Très fort :</strong> Accepté (tous critères)</li>
     * </ul>
     * 
     * <p><strong>Messages d'aide :</strong> En cas de rejet, un message explicite
     * indique les critères à respecter pour aider l'utilisateur à créer
     * un mot de passe sécurisé.</p>
     * 
     * @param password le mot de passe à valider
     * 
     * @return true si le mot de passe respecte les critères de sécurité,
     *         false sinon (avec affichage d'un message d'erreur explicite)
     * 
     * @see ValidationUtils#isPasswordValid(String)
     * @see ValidationUtils#getPasswordStrength(String)
     * @see ValidationUtils.PasswordStrength
     * @see #showError(String)
     * 
     * @since 1.0
     */
    private boolean validatePasswordStrength(String password) {
        if (!ValidationUtils.isPasswordValid(password)) {
            showError("Le mot de passe doit contenir au moins 6 caractères");
            passwordField.requestFocus();
            return false;
        }

        // Analyser la force du mot de passe
        ValidationUtils.PasswordStrength strength = ValidationUtils.getPasswordStrength(password);
        
        switch (strength) {
            case WEAK:
                showError("Mot de passe trop faible. Utilisez au moins 6 caractères avec lettres et chiffres.");
                passwordField.requestFocus();
                return false;
                

                
            case MEDIUM, STRONG, VERY_STRONG:
                return true;

            default:
                showError("Erreur lors de la validation du mot de passe");
                return false;
        
        }

    
    }


    /**
     * Gestionnaire pour annuler l'inscription
     */
    @FXML
    private void handleCancel() {
        
        // Vider tous les champs
        nomField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        typeComboBox.setValue(null);
        hideError();
        
        // Réinitialiser les indicateurs
        passwordStrengthLabel.setText("Aucun");
        passwordStrengthLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11px; -fx-font-weight: bold;");
        passwordMatchLabel.setText("");
        
        // Rediriger vers l'interface de connexion
        handleGoToLogin();
    }

    /**
     * Gestionnaire pour rediriger vers l'interface de connexion
     */
    @FXML
    private void handleGoToLogin() {
        loadLoginScene();
    }
    
    /**
     * Charge l'interface de connexion
     */
    private void loadLoginScene() {
        try {
            // Charger l'interface de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth/login.fxml"));
            Parent loginRoot = loader.load();
            
            // Obtenir la scène actuelle et changer le contenu
            Stage stage = (Stage) registerButton.getScene().getWindow();
            
            // Récupérer les dimensions depuis la configuration
            int windowWidth = AppConfig.getWindowWidth();
            int windowHeight = AppConfig.getWindowHeight();
            
            Scene loginScene = new Scene(loginRoot, windowWidth, windowHeight);
            
            // Appliquer le CSS si disponible
            applyCssIfAvailable(loginScene);
            
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
     * Applique le CSS à la scène si disponible
     */
    private void applyCssIfAvailable(Scene scene) {
        try {
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        } catch (Exception e) {
            logger.warn("Impossible de charger le fichier CSS: {}", e.getMessage());
        }
    }
}