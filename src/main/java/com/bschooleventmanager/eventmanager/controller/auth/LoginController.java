package com.bschooleventmanager.eventmanager.controller.auth;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import com.bschooleventmanager.eventmanager.model.Utilisateur;
import com.bschooleventmanager.eventmanager.service.UtilisateurService;
import com.bschooleventmanager.eventmanager.util.SessionManager;
import com.bschooleventmanager.eventmanager.util.NotificationUtils;
import com.bschooleventmanager.eventmanager.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;

    private UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    public void initialize() {
        // Initialisation si nécessaire
    }

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

    @FXML
    private void handleRegister() {
        // Rediriger vers la page d'inscription
        // loadScene("register.fxml");
    }

    private void navigateToDashboard(Utilisateur user) {
        // TODO: Implémenter la navigation selon le type d'utilisateur
        // if (user.getTypeUtilisateur() == TypeUtilisateur.CLIENT) {
        //     loadScene("client/dashboard.fxml");
        // } else {
        //     loadScene("organisateur/dashboard.fxml");
        // }
    }
}


