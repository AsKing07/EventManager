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
 * Contrôleur partagé pour la gestion complète du profil utilisateur avec validation en temps réel.
 * 
 * <p><b>Fonctionnalités principales :</b></p>
 * <ul>
 *   <li>Interface commune pour organisateurs et clients permettant la gestion de profil</li>
 *   <li>Modification des informations personnelles (nom, email) avec validation</li>
 *   <li>Changement sécurisé du mot de passe avec vérification de l'ancien</li>
 *   <li>Validation en temps réel des saisies avec indicateurs visuels</li>
 *   <li>Affichage des informations de compte (type utilisateur, date de création)</li>
 * </ul>
 * 
 * <p><b>Architecture de validation :</b></p>
 * <ul>
 *   <li>Validation instantanée via des listeners sur les champs de saisie</li>
 *   <li>Indicateurs visuels de validation (✓/✗) avec codes couleurs appropriés</li>
 *   <li>Évaluation de la force du mot de passe en temps réel</li>
 *   <li>Vérification de correspondance des mots de passe de confirmation</li>
 * </ul>
 * 
 * <p><b>Sécurité et validation :</b></p>
 * <ul>
 *   <li>Vérification de l'ancien mot de passe avant changement</li>
 *   <li>Validation complète des formats email et contraintes de nom</li>
 *   <li>Gestion robuste des erreurs avec notifications utilisateur</li>
 *   <li>Logging sécurisé des opérations sans exposition de données sensibles</li>
 * </ul>
 * 
 * <p><b>Intégration système :</b></p>
 * <ul>
 *   <li>Utilisation de SessionManager pour récupération de l'utilisateur connecté</li>
 *   <li>Communication avec UtilisateurService pour toutes les opérations de persistance</li>
 *   <li>ValidationUtils pour standardisation des règles de validation</li>
 *   <li>NotificationUtils pour feedback utilisateur cohérent</li>
 * </ul>
 * 
 * @author Charbel SONON @AsKing07
 * @version 1.0
 * @since 1.0
 * 
 * @see UtilisateurService
 * @see SessionManager
 * @see ValidationUtils
 * @see NotificationUtils
 * @see Utilisateur
 */
public class ProfileController {
    /** Logger pour traçage des opérations de gestion de profil et sécurité. */
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    // === Services ===
    /** Service métier pour toutes les opérations CRUD sur les utilisateurs. */
    private final UtilisateurService utilisateurService = new UtilisateurService();

    // === Éléments FXML ===
    /** Champ de saisie du nom utilisateur avec validation en temps réel. */
    @FXML
    private TextField nomField;

    /** Champ de saisie de l'adresse email avec validation de format. */
    @FXML
    private TextField emailField;

    /** Label d'affichage du type d'utilisateur (Client/Organisateur) en lecture seule. */
    @FXML
    private Label userTypeLabel;
    
    /** Champ de saisie du mot de passe actuel pour vérification avant changement. */
    @FXML
    private PasswordField currentPasswordField;
    
    /** Champ de saisie du nouveau mot de passe avec évaluation de force. */
    @FXML
    private PasswordField newPasswordField;
    
    /** Champ de confirmation du nouveau mot de passe pour validation de correspondance. */
    @FXML
    private PasswordField confirmPasswordField;
    
    /** Bouton de sauvegarde des modifications d'informations personnelles. */
    @FXML
    private Button saveInformationsButton;
    
    /** Bouton de changement de mot de passe avec validation complète. */
    @FXML
    private Button changePasswordButton;
    
    /** Texte d'affichage des informations de compte (date de création, etc.). */
    @FXML
    private Text accountInfoText;
    
    // Éléments de validation visuels
    /** Indicateur visuel de validation du nom avec message d'erreur/succès. */
    @FXML
    private Text nomValidation;
    
    /** Indicateur visuel de validation de l'email avec message d'erreur/succès. */
    @FXML
    private Text emailValidation;
    
    /** Indicateur de force du mot de passe avec échelle colorée. */
    @FXML
    private Text passwordStrengthIndicator;
    
    /** Indicateur de correspondance des mots de passe de confirmation. */
    @FXML
    private Text passwordMatchIndicator;

    /**
     * Initialise le contrôleur avec configuration des listeners de validation en temps réel.
     * 
     * <p>Cette méthode est appelée automatiquement après le chargement du FXML
     * pour configurer la validation interactive des champs de saisie. Elle établit
     * les listeners qui fournissent un feedback immédiat à l'utilisateur.</p>
     * 
     * <p><b>Listeners configurés :</b></p>
     * <ul>
     *   <li>nomField : Validation de longueur minimale (2 caractères)</li>
     *   <li>emailField : Validation de format email avec regex</li>
     *   <li>newPasswordField : Évaluation de force du mot de passe</li>
     *   <li>confirmPasswordField : Vérification de correspondance</li>
     * </ul>
     * 
     * @see #setupValidationListeners()
     */
    @FXML
    public void initialize() {
        setupValidationListeners();
    }

    /**
     * Initialise l'affichage du profil avec les données de l'utilisateur connecté.
     * 
     * <p><b>Workflow d'initialisation :</b></p>
     * <ol>
     *   <li>Récupération de l'utilisateur connecté via SessionManager</li>
     *   <li>Pré-remplissage des champs nom et email avec données actuelles</li>
     *   <li>Affichage du type d'utilisateur (Client/Organisateur)</li>
     *   <li>Formatage et affichage de la date de création du compte</li>
     * </ol>
     * 
     * <p><b>Données affichées :</b></p>
     * <ul>
     *   <li>Nom complet : Valeur actuelle pour modification</li>
     *   <li>Adresse email : Valeur actuelle pour modification</li>
     *   <li>Type utilisateur : Information en lecture seule</li>
     *   <li>Date de création : Formatée en DD/MM/YYYY</li>
     * </ul>
     * 
     * <p>Cette méthode doit être appelée après la navigation vers la vue profil
     * pour s'assurer que les données affichées correspondent à l'utilisateur connecté.</p>
     * 
     * @see SessionManager#getUtilisateurConnecte()
     * @see Utilisateur#getDateCreation()
     * @see NotificationUtils#showError(String)
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
     * Traite la mise à jour des informations du profil utilisateur avec validation complète.
     * 
     * <p><b>Workflow de mise à jour :</b></p>
     * <ol>
     *   <li>Vérification de la session utilisateur connecté</li>
     *   <li>Récupération et nettoyage des données saisies</li>
     *   <li>Validation complète du nom et de l'email</li>
     *   <li>Mise à jour de l'objet utilisateur en mémoire</li>
     *   <li>Persistance en base de données via UtilisateurService</li>
     *   <li>Notification du résultat à l'utilisateur</li>
     * </ol>
     * 
     * <p><b>Validations effectuées :</b></p>
     * <ul>
     *   <li>Nom : Minimum 2 caractères, caractères valides</li>
     *   <li>Email : Format valide selon RFC 5322</li>
     *   <li>Session : Vérification utilisateur connecté</li>
     * </ul>
     * 
     * <p><b>Gestion d'erreurs :</b></p>
     * <ul>
     *   <li>Session invalide : Message d'erreur et arrêt du processus</li>
     *   <li>Données invalides : Focus sur le champ en erreur</li>
     *   <li>Erreur de persistance : Notification et logging de l'exception</li>
     * </ul>
     * 
     * @see #isValidProfileData(String, String)
     * @see UtilisateurService#updateUtilisateur(Utilisateur)
     * @see NotificationUtils#showSuccess(String)
     * @see NotificationUtils#showError(String)
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



    /**
     * Annule les modifications en cours et restaure les valeurs originales du profil.
     * 
     * <p>Cette méthode réinitialise tous les champs du formulaire avec les données
     * actuelles de l'utilisateur connecté, annulant ainsi toutes les modifications
     * non sauvegardées. Elle fournit une fonction d'annulation sécurisée pour l'utilisateur.</p>
     * 
     * @see #initializeProfile()
     */
    @FXML
    private void handleCancelChanges() {

        initializeProfile();
    }

    /**
     * Traite le changement sécurisé du mot de passe utilisateur avec validation complète.
     * 
     * <p><b>Workflow de changement de mot de passe :</b></p>
     * <ol>
     *   <li>Vérification de la session utilisateur connecté</li>
     *   <li>Récupération des trois mots de passe saisis</li>
     *   <li>Validation complète des critères de sécurité</li>
     *   <li>Vérification de l'ancien mot de passe pour authentification</li>
     *   <li>Mise à jour sécurisée du mot de passe en base</li>
     *   <li>Effacement automatique des champs sensibles</li>
     *   <li>Notification du succès ou de l'échec</li>
     * </ol>
     * 
     * <p><b>Validations de sécurité :</b></p>
     * <ul>
     *   <li>Mot de passe actuel : Vérification contre la base de données</li>
     *   <li>Nouveau mot de passe : Minimum 8 caractères</li>
     *   <li>Confirmation : Correspondance exacte avec le nouveau</li>
     *   <li>Session : Utilisateur valide et connecté</li>
     * </ul>
     * 
     * <p><b>Sécurité renforcée :</b></p>
     * <ul>
     *   <li>Authentification obligatoire avec l'ancien mot de passe</li>
     *   <li>Effacement immédiat des champs après opération</li>
     *   <li>Logging sécurisé sans exposition des mots de passe</li>
     *   <li>Gestion robuste des erreurs d'authentification</li>
     * </ul>
     * 
     * @see #isValidPasswordChange(String, String, String, Utilisateur)
     * @see UtilisateurService#verifyPassword(String, String)
     * @see UtilisateurService#changePassword(int, String)
     * @see NotificationUtils#showSuccess(String)
     * @see NotificationUtils#showError(String)
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
     * Configure les écouteurs pour la validation en temps réel de tous les champs de saisie.
     * 
     * <p><b>Listeners configurés :</b></p>
     * <ul>
     *   <li>nomField : Validation de longueur et caractères avec indicateur visuel ✓/✗</li>
     *   <li>emailField : Validation de format RFC 5322 avec feedback coloré</li>
     *   <li>newPasswordField : Évaluation de force (faible/moyen/fort/très fort)</li>
     *   <li>confirmPasswordField : Vérification de correspondance en temps réel</li>
     * </ul>
     * 
     * <p><b>Système de validation visuelle :</b></p>
     * <ul>
     *   <li>Indicateurs colorés : Vert pour valide, rouge pour invalide</li>
     *   <li>Messages descriptifs : Explications claires des erreurs</li>
     *   <li>Feedback immédiat : Mise à jour à chaque caractère saisi</li>
     *   <li>Masquage intelligent : Disparition des messages sur champs vides</li>
     * </ul>
     * 
     * <p>Cette méthode améliore l'expérience utilisateur en fournissant un feedback
     * instantané et réduit les erreurs de saisie par validation proactive.</p>
     * 
     * @see ValidationUtils#isValidName(String)
     * @see ValidationUtils#isValidEmail(String)
     * @see ValidationUtils#getPasswordStrength(String)
     * @see #showValidation(Text, String, String)
     * @see #updatePasswordStrengthDisplay(ValidationUtils.PasswordStrength, int)
     * @see #updatePasswordMatchDisplay(String, String)
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
     * Affiche un message de validation avec style coloré pour un champ donné.
     * 
     * <p><b>Fonctionnement :</b></p>
     * <ul>
     *   <li>Définit le texte du message de validation</li>
     *   <li>Applique la couleur spécifiée avec style CSS inline</li>
     *   <li>Rend l'indicateur visible à l'utilisateur</li>
     *   <li>Utilise une police de 12px pour la lisibilité</li>
     * </ul>
     * 
     * <p><b>Codes couleurs standards :</b></p>
     * <ul>
     *   <li>#27ae60 : Vert pour validation réussie</li>
     *   <li>#e74c3c : Rouge pour erreur de validation</li>
     *   <li>#f39c12 : Orange pour avertissements</li>
     * </ul>
     * 
     * @param validationText L'élément Text où afficher le message
     * @param message Le message de validation à afficher
     * @param color La couleur hexadécimale pour le style CSS
     */
    private void showValidation(Text validationText, String message, String color) {
        validationText.setText(message);
        validationText.setStyle("-fx-fill: " + color + "; -fx-font-size: 12px;");
        validationText.setVisible(true);
    }

    /**
     * Met à jour l'affichage de la force du mot de passe avec code couleur approprié.
     * 
     * <p><b>Échelle de force et couleurs :</b></p>
     * <ul>
     *   <li>WEAK (Faible) : Rouge (#e74c3c) - Mot de passe trop simple</li>
     *   <li>MEDIUM (Moyen) : Orange (#f39c12) - Sécurité acceptable</li>
     *   <li>STRONG (Fort) : Vert (#27ae60) - Bonne sécurité</li>
     *   <li>VERY_STRONG (Très fort) : Vert foncé (#2ecc71) - Excellente sécurité</li>
     * </ul>
     * 
     * <p><b>Gestion intelligente de l'affichage :</b></p>
     * <ul>
     *   <li>Masquage automatique si le champ est vide (longueur = 0)</li>
     *   <li>Mise à jour en temps réel à chaque caractère saisi</li>
     *   <li>Messages descriptifs pour guider l'utilisateur</li>
     * </ul>
     * 
     * @param strength L'énumération de force retournée par ValidationUtils
     * @param length La longueur actuelle du mot de passe
     * 
     * @see ValidationUtils.PasswordStrength
     * @see #showValidation(Text, String, String)
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
     * Met à jour l'affichage de correspondance entre le nouveau mot de passe et sa confirmation.
     * 
     * <p><b>États d'affichage :</b></p>
     * <ul>
     *   <li>Champ vide : Masquage de l'indicateur pour éviter la pollution visuelle</li>
     *   <li>Correspondance : Message de succès en vert avec icône ✓</li>
     *   <li>Non-correspondance : Message d'erreur en rouge avec icône ✗</li>
     * </ul>
     * 
     * <p><b>Validation en temps réel :</b></p>
     * <ul>
     *   <li>Comparaison exacte caractère par caractère</li>
     *   <li>Mise à jour immédiate à chaque modification</li>
     *   <li>Feedback visuel immédiat pour l'utilisateur</li>
     * </ul>
     * 
     * <p>Cette validation préventive réduit les erreurs de saisie et améliore
     * l'expérience utilisateur lors du changement de mot de passe.</p>
     * 
     * @param password Le nouveau mot de passe saisi
     * @param confirmation La confirmation du mot de passe
     * 
     * @see #showValidation(Text, String, String)
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
     * Valide les données du profil utilisateur avant mise à jour en base.
     * 
     * <p><b>Validations effectuées :</b></p>
     * <ul>
     *   <li>Nom : Vérification via ValidationUtils.isValidName() (min. 2 caractères)</li>
     *   <li>Email : Validation du format via ValidationUtils.isValidEmail()</li>
     * </ul>
     * 
     * <p><b>Gestion des erreurs :</b></p>
     * <ul>
     *   <li>Messages d'erreur spécifiques et descriptifs pour chaque champ</li>
     *   <li>Focus automatique sur le premier champ en erreur</li>
     *   <li>Retour booléen pour contrôle de flux dans la méthode appelante</li>
     * </ul>
     * 
     * <p>Cette méthode assure la cohérence des données avant persistance
     * et guide l'utilisateur vers la correction des erreurs.</p>
     * 
     * @param nom Le nom à valider (après trim())
     * @param email L'email à valider (après trim())
     * @return true si toutes les validations passent, false sinon
     * 
     * @see ValidationUtils#isValidName(String)
     * @see ValidationUtils#isValidEmail(String)
     * @see NotificationUtils#showError(String)
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
     * Valide complètement une demande de changement de mot de passe avec sécurité renforcée.
     * 
     * <p><b>Validations séquentielles :</b></p>
     * <ol>
     *   <li>Présence du mot de passe actuel (non vide)</li>
     *   <li>Vérification du mot de passe actuel contre la base de données</li>
     *   <li>Longueur minimale du nouveau mot de passe (8 caractères)</li>
     *   <li>Correspondance exacte entre nouveau mot de passe et confirmation</li>
     * </ol>
     * 
     * <p><b>Sécurité et authentification :</b></p>
     * <ul>
     *   <li>Authentification obligatoire : Vérification de l'ancien mot de passe</li>
     *   <li>Politique de mot de passe : Minimum 8 caractères</li>
     *   <li>Double validation : Confirmation requise pour éviter les erreurs</li>
     *   <li>Focus intelligent : Redirection vers le champ en erreur</li>
     * </ul>
     * 
     * <p><b>Gestion des erreurs :</b></p>
     * <ul>
     *   <li>Messages spécifiques pour chaque type d'erreur</li>
     *   <li>Focus automatique sur le champ problématique</li>
     *   <li>Validation via UtilisateurService pour l'ancien mot de passe</li>
     * </ul>
     * 
     * @param currentPassword Le mot de passe actuel saisi pour vérification
     * @param newPassword Le nouveau mot de passe proposé
     * @param confirmPassword La confirmation du nouveau mot de passe
     * @param user L'utilisateur connecté pour vérification d'authentification
     * @return true si toutes les validations passent, false sinon
     * 
     * @see UtilisateurService#verifyPassword(String, String)
     * @see NotificationUtils#showError(String)
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