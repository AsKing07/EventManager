package com.bschooleventmanager.eventmanager.controller.shared;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import com.bschooleventmanager.eventmanager.model.Utilisateur;
import com.bschooleventmanager.eventmanager.service.UtilisateurService;
import com.bschooleventmanager.eventmanager.util.SessionManager;
import com.bschooleventmanager.eventmanager.util.NotificationUtils;
import com.bschooleventmanager.eventmanager.util.ValidationUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contrôleur pour la gestion du profil utilisateur
 * Interface partagée entre les organisateurs et les clients
 */
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    // === Services ===
    private final UtilisateurService utilisateurService = new UtilisateurService();

    // === Éléments FXML ===
    @FXML
    private TextField nomField;

    @FXML
    private TextField emailField;

    @FXML
    private Label userTypeLabel;
    
    @FXML
    private PasswordField currentPasswordField;
    
    @FXML
    private PasswordField newPasswordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Button saveInformationsButton;
    
    @FXML
    private Button changePasswordButton;
    
    @FXML
    private Text accountInfoText;
    
    // Éléments de validation visuels
    @FXML
    private Text nomValidation;
    
    @FXML
    private Text emailValidation;
    
    @FXML
    private Text passwordStrengthIndicator;
    
    @FXML
    private Text passwordMatchIndicator;

    /**
     * Initialisation du contrôleur
     */
    @FXML
    public void initialize() {
        setupValidationListeners();
    }

    /**
     * Initialise le profil avec les données de l'utilisateur connecté
     */
    public void initializeProfile() {
        logger.info("Initialisation du profil utilisateur");
        
        Utilisateur user = SessionManager.getUtilisateurConnecte();
        if (user != null) {
            nomField.setText(user.getNom());
            emailField.setText(user.getEmail());
            userTypeLabel.setText(user.getTypeUtilisateur().getLabel());
            
            
            // Afficher la date de création si disponible
            if (user.getDateCreation() != null && accountInfoText != null) {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String dateCreation = user.getDateCreation().format(formatter);
                accountInfoText.setText("Membre depuis le " + dateCreation);
            }
        } else {
            logger.error("Aucun utilisateur connecté trouvé");
            NotificationUtils.showError("Erreur : aucun utilisateur connecté");
        }
    }

    /**
     * Met à jour les informations du profil
     */
    @FXML
    private void handleUpdateProfile() {
        logger.info("Mise à jour du profil utilisateur");
        
        // Récupérer l'utilisateur connecté
        Utilisateur user = SessionManager.getUtilisateurConnecte();
        if (user == null) {
            NotificationUtils.showError("Erreur : aucun utilisateur connecté");
            return;
        }
        
        // Validation des champs
        String nom = nomField.getText().trim();
        String email = emailField.getText().trim();
        
        // Vérifier si les champs sont valides
        if (!isValidProfileData(nom, email)) {
            return;
        }
        
        try {
            // Mettre à jour les données
            user.setNom(nom);
            user.setEmail(email);
            
            // Sauvegarder dans la base de données
            boolean success = utilisateurService.updateUtilisateur(user);
            
            if (success) {
                NotificationUtils.showSuccess("Profil mis à jour avec succès");
                logger.info("✓ Profil utilisateur mis à jour: {}", email);
            } else {
                NotificationUtils.showError("Erreur lors de la mise à jour du profil");
            }
            
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du profil", e);
            NotificationUtils.showError("Erreur technique lors de la mise à jour");
        }
    }



    @FXML
    private void handleCancelChanges() {

        initializeProfile();
    }

    /**
     * Change le mot de passe de l'utilisateur
     */
    @FXML
    private void handleChangePassword() {
        logger.info("Changement du mot de passe utilisateur");
        
        // Récupérer l'utilisateur connecté
        Utilisateur user = SessionManager.getUtilisateurConnecte();
        if (user == null) {
            NotificationUtils.showError("Erreur : aucun utilisateur connecté");
            return;
        }
        
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Validation
        if (!isValidPasswordChange(currentPassword, newPassword, confirmPassword, user)) {
            return;
        }
        
        try {
            // Mettre à jour le mot de passe
            boolean success = utilisateurService.changePassword(user.getIdUtilisateur(), newPassword);
            
            if (success) {
                NotificationUtils.showSuccess("Mot de passe modifié avec succès");
                
                // Effacer les champs
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
                
                logger.info("✓ Mot de passe modifié pour l'utilisateur: {}", user.getEmail());
            } else {
                NotificationUtils.showError("Erreur lors du changement de mot de passe");
            }
            
        } catch (Exception e) {
            logger.error("Erreur lors du changement de mot de passe", e);
            NotificationUtils.showError("Erreur technique lors du changement de mot de passe");
        }
    }

    /**
     * Configure les écouteurs pour la validation en temps réel
     */
    private void setupValidationListeners() {
        // Validation du nom
        nomField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (ValidationUtils.isValidName(newValue)) {
                showValidation(nomValidation, "✓", "#27ae60");
            } else {
                showValidation(nomValidation, "✗ Au moins 2 caractères", "#e74c3c");
            }
        });


        // Validation de l'email
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (ValidationUtils.isValidEmail(newValue)) {
                showValidation(emailValidation, "✓", "#27ae60");
            } else {
                showValidation(emailValidation, "✗ Format email invalide", "#e74c3c");
            }
        });

        // Validation de la force du nouveau mot de passe
        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            ValidationUtils.PasswordStrength strength = ValidationUtils.getPasswordStrength(newValue);
            updatePasswordStrengthDisplay(strength, newValue.length());
        });

        // Validation de la confirmation du mot de passe
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePasswordMatchDisplay(newPasswordField.getText(), newValue);
        });
    }

    /**
     * Affiche le message de validation pour un champ
     */
    private void showValidation(Text validationText, String message, String color) {
        validationText.setText(message);
        validationText.setStyle("-fx-fill: " + color + "; -fx-font-size: 12px;");
        validationText.setVisible(true);
    }

    /**
     * Met à jour l'affichage de la force du mot de passe
     */
    private void updatePasswordStrengthDisplay(ValidationUtils.PasswordStrength strength, int length) {
        if (length == 0) {
            passwordStrengthIndicator.setVisible(false);
            return;
        }

        String message;
        String color;

        switch (strength) {
            case WEAK:
                message = "Mot de passe faible";
                color = "#e74c3c";
                break;
            case MEDIUM:
                message = "Mot de passe moyen";
                color = "#f39c12";
                break;
            case STRONG:
                message = "Mot de passe fort";
                color = "#27ae60";
                break;
            case VERY_STRONG:
                message = "Mot de passe très fort";
                color = "#2ecc71";
                break;
            default:
                message = "";
                color = "#000000";
        }

        showValidation(passwordStrengthIndicator, message, color);
    }

    /**
     * Met à jour l'affichage de la correspondance des mots de passe
     */
    private void updatePasswordMatchDisplay(String password, String confirmation) {
        if (confirmation.isEmpty()) {
            passwordMatchIndicator.setVisible(false);
            return;
        }

        if (password.equals(confirmation)) {
            showValidation(passwordMatchIndicator, "✓ Mots de passe identiques", "#27ae60");
        } else {
            showValidation(passwordMatchIndicator, "✗ Mots de passe différents", "#e74c3c");
        }
    }

    /**
     * Valide les données du profil
     */
    private boolean isValidProfileData(String nom, String email) {
        if (!ValidationUtils.isValidName(nom)) {
            NotificationUtils.showError("Le nom doit contenir au moins 2 caractères");
            nomField.requestFocus();
            return false;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            NotificationUtils.showError("Veuillez saisir un email valide");
            emailField.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Valide le changement de mot de passe
     */
    private boolean isValidPasswordChange(String currentPassword, String newPassword, 
                                        String confirmPassword, Utilisateur user) {
        if (currentPassword.isEmpty()) {
            NotificationUtils.showError("Veuillez saisir votre mot de passe actuel");
            currentPasswordField.requestFocus();
            return false;
        }

        // Vérifier le mot de passe actuel
        if (!utilisateurService.verifyPassword(user.getEmail(), currentPassword)) {
            NotificationUtils.showError("Mot de passe actuel incorrect");
            currentPasswordField.requestFocus();
            return false;
        }

        if (newPassword.length() < 8) {
            NotificationUtils.showError("Le nouveau mot de passe doit contenir au moins 8 caractères");
            newPasswordField.requestFocus();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            NotificationUtils.showError("La confirmation du mot de passe ne correspond pas");
            confirmPasswordField.requestFocus();
            return false;
        }

        return true;
    }
}