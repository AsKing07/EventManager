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
 * Contrôleur pour l'inscription d'un nouvel utilisateur
 * Gère l'interface d'inscription et la validation des données
 */
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    // === Éléments FXML ===
    @FXML
    private TextField nomField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private ComboBox<TypeUtilisateur> typeComboBox;
    
    @FXML
    private Button registerButton;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Hyperlink loginLink;
    
    @FXML
    private Label errorLabel;

    @FXML
    private Label passwordStrengthLabel;
    
    @FXML
    private Label passwordMatchLabel;

    // === Services ===
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
     * Initialise la ComboBox des types d'utilisateurs
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
     * Configure la validation en temps réel des champs
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
     * Masque le message d'erreur
     */
    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    /**
     * Affiche un message d'erreur
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    /**
     * Met à jour l'indicateur de force du mot de passe en temps réel
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
     * Met à jour l'indicateur de correspondance des mots de passe
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
     * Gestionnaire pour l'inscription d'un nouvel utilisateur
     * Valide les données et crée le compte
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
     * Valide que tous les champs obligatoires sont remplis
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
     * Valide la force du mot de passe avec des critères renforcés
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