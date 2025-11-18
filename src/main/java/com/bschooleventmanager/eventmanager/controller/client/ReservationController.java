package com.bschooleventmanager.eventmanager.controller.client;

import com.bschooleventmanager.eventmanager.exception.BusinessException;
import com.bschooleventmanager.eventmanager.exception.PlacesInsuffisantesException;
import com.bschooleventmanager.eventmanager.model.Evenement;
import com.bschooleventmanager.eventmanager.model.Utilisateur;
import com.bschooleventmanager.eventmanager.service.ReservationService;
import com.bschooleventmanager.eventmanager.util.NotificationUtils;
import com.bschooleventmanager.eventmanager.util.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReservationController {
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    // Éléments FXML
    @FXML private Button backButton;
    @FXML private Label eventNameLabel;
    @FXML private Label eventDateLabel;
    @FXML private Label eventLocationLabel;

    @FXML private Label priceStandardLabel;
    @FXML private Label priceVipLabel;
    @FXML private Label pricePremiumLabel;
    @FXML private Label availableStandardLabel;
    @FXML private Label availableVipLabel;
    @FXML private Label availablePremiumLabel;

    @FXML private Spinner<Integer> standardSpinner;
    @FXML private Spinner<Integer> vipSpinner;
    @FXML private Spinner<Integer> premiumSpinner;

    @FXML private VBox summaryContainer;
    @FXML private Label totalLabel;

    @FXML private RadioButton payNowRadio;
    @FXML private RadioButton payLaterRadio;
    @FXML private Label payLaterWarning;

    @FXML private Button cancelButton;
    @FXML private Button confirmButton;

    // Données
    private Evenement currentEvent;
    private com.bschooleventmanager.eventmanager.model.Reservation lastCreatedReservation;
    private ClientDashboardController dashboardController;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

    // Service
    private final ReservationService reservationService = new ReservationService();

    public void setDashboardController(ClientDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public void setEventData(Evenement event) {
        this.currentEvent = event;
        
        // Vérifier si l'événement est encore réservable
        if (isEventExpiredOrClosingSoon(event)) {
            showEventExpiredWarning();
            return;
        }
        
        populateEventInfo();
        setupSpinners();
    }
    
    /**
     * Vérifie si un événement est expiré ou va fermer bientôt
     */
    private boolean isEventExpiredOrClosingSoon(Evenement event) {
        if (event == null) return true;
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventDateTime = event.getDateEvenement();
        LocalDateTime cutoffTime = eventDateTime.minusMinutes(30);
        
        return now.isAfter(cutoffTime);
    }
    
    /**
     * Affiche un avertissement pour un événement expiré
     */
    private void showEventExpiredWarning() {
        // Désactiver tous les spinners et le bouton de confirmation
        if (standardSpinner != null) standardSpinner.setDisable(true);
        if (vipSpinner != null) vipSpinner.setDisable(true);
        if (premiumSpinner != null) premiumSpinner.setDisable(true);
        if (confirmButton != null) confirmButton.setDisable(true);
        
        // Afficher un message d'information
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Réservations fermées");
        alert.setHeaderText("Cet événement n'est plus disponible");
        alert.setContentText("Les réservations pour cet événement sont fermées car il est déjà terminé ou va commencer dans moins de 30 minutes.");
        alert.show();
    }

    @FXML
    private void initialize() {
        logger.info("Initialisation du contrôleur de réservation");

        // Grouper les radio buttons
        ToggleGroup paymentGroup = new ToggleGroup();
        payNowRadio.setToggleGroup(paymentGroup);
        payLaterRadio.setToggleGroup(paymentGroup);

        // Écouter les changements du radio button "Payer plus tard"
        payLaterRadio.selectedProperty().addListener((obs, oldVal, newVal) -> 
            payLaterWarning.setVisible(newVal)
        );
    }

    private void populateEventInfo() {
        if (currentEvent != null) {
            eventNameLabel.setText(currentEvent.getNom());
            eventDateLabel.setText(currentEvent.getDateEvenement().format(dateFormatter));
            eventLocationLabel.setText(currentEvent.getLieu());

            // Affichage des prix et disponibilités
            BigDecimal prixStd = currentEvent.getPrixStandard();
            priceStandardLabel.setText(prixStd != null ? prixStd + "€" : "N/A");
            availableStandardLabel.setText(currentEvent.getPlacesStandardRestantes() + " places disponibles");

            BigDecimal prixVip = currentEvent.getPrixVip();
            priceVipLabel.setText(prixVip != null ? prixVip + "€" : "N/A");
            availableVipLabel.setText(currentEvent.getPlacesVipRestantes() + " places disponibles");

            BigDecimal prixPrem = currentEvent.getPrixPremium();
            pricePremiumLabel.setText(prixPrem != null ? prixPrem + "€" : "N/A");
            availablePremiumLabel.setText(currentEvent.getPlacesPremiumRestantes() + " places disponibles");
        }
    }

    private void setupSpinners() {
        if (currentEvent != null) {
            // Configuration des limites basées sur la disponibilité
            int maxStandard = currentEvent.getPlacesStandardRestantes();
            int maxVip = currentEvent.getPlacesVipRestantes();
            int maxPremium = currentEvent.getPlacesPremiumRestantes();

            setupSpinner(standardSpinner, 0, Math.min(10, maxStandard));
            setupSpinner(vipSpinner, 0, Math.min(10, maxVip));
            setupSpinner(premiumSpinner, 0, Math.min(10, maxPremium));

            // Écouter les changements pour mettre à jour le total
            standardSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateTotal());
            vipSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateTotal());
            premiumSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateTotal());
        }
    }

    private void setupSpinner(Spinner<Integer> spinner, int min, int max) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, 0);
        spinner.setValueFactory(valueFactory);
        spinner.setEditable(true);
    }

    @FXML
    private void updateTotal() {
        try {
            double total = 0.0;
            summaryContainer.getChildren().clear();

            int stdQty = standardSpinner.getValue();
            int vipQty = vipSpinner.getValue();
            int premQty = premiumSpinner.getValue();

            // Calcul Standard
            if (stdQty > 0 && currentEvent.getPrixStandard() != null) {
                double stdTotal = stdQty * currentEvent.getPrixStandard().doubleValue();
                total += stdTotal;
                addSummaryLine("Standard", stdQty, currentEvent.getPrixStandard().doubleValue(), stdTotal);
            }

            // Calcul VIP
            if (vipQty > 0 && currentEvent.getPrixVip() != null) {
                double vipTotal = vipQty * currentEvent.getPrixVip().doubleValue();
                total += vipTotal;
                addSummaryLine("VIP", vipQty, currentEvent.getPrixVip().doubleValue(), vipTotal);
            }

            // Calcul Premium
            if (premQty > 0 && currentEvent.getPrixPremium() != null) {
                double premTotal = premQty * currentEvent.getPrixPremium().doubleValue();
                total += premTotal;
                addSummaryLine("Premium", premQty, currentEvent.getPrixPremium().doubleValue(), premTotal);
            }

            totalLabel.setText(String.format("%.2f €", total));

            // Calculer le nombre total de billets sélectionnés
            int totalQuantite = stdQty + vipQty + premQty;

            // Activer le bouton de confirmation si au moins une place est sélectionnée.
            // Dans le cas de billets gratuits (total == 0.0), on veut permettre la confirmation
            // tant que totalQuantite > 0.
            boolean disableConfirm = (total == 0.0 && totalQuantite == 0);
            confirmButton.setDisable(disableConfirm);

        } catch (Exception e) {
            logger.error("Erreur lors du calcul du total", e);
            totalLabel.setText("Erreur");
        }
    }

    private void addSummaryLine(String categorie, int quantity, double unitPrice, double total) {
        HBox line = new HBox(10);
        line.getChildren().addAll(
                new Label(String.format("%s x%d", categorie, quantity)),
                new Label(String.format("%.2f € x %d = %.2f €", unitPrice, quantity, total))
        );
        line.setStyle("-fx-padding: 5; -fx-background-color: #f8f9fa; -fx-background-radius: 3;");
        summaryContainer.getChildren().add(line);
    }

    @FXML
    private void handleBack() {
        if (dashboardController != null) {
            dashboardController.showEventDetails(currentEvent);
        }
    }

    @FXML
    private void handleCancel() {
        handleBack();
    }

    @FXML
    private void handleConfirmReservation() {
        try {
            validateBasicInputs();
            createReservation();
        } catch (PlacesInsuffisantesException e) {
            logger.warn("Places insuffisantes: {}", e.getMessage());
            handlePlacesInsuffisantesException(e);
        } catch (BusinessException e) {
            logger.warn("Erreur métier lors de la réservation: {}", e.getMessage());
            handleBusinessException(e);
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la réservation", e);
            handleUnexpectedException(e);
        }
    }

    private void validateBasicInputs() throws BusinessException {
        int totalQuantite = standardSpinner.getValue() + vipSpinner.getValue() + premiumSpinner.getValue();
        if (totalQuantite == 0) {
            throw new BusinessException("Veuillez sélectionner au moins une place");
        }
        
        if (!payNowRadio.isSelected() && !payLaterRadio.isSelected()) {
            throw new BusinessException("Veuillez choisir une option de paiement");
        }
    }

    private void createReservation() throws PlacesInsuffisantesException, BusinessException {
        Utilisateur user = SessionManager.getUtilisateurConnecte();
        if (user == null) {
            throw new BusinessException("Utilisateur non connecté");
        }

        // Récupérer les quantités
        int stdQty = standardSpinner.getValue();
        int vipQty = vipSpinner.getValue();
        int premQty = premiumSpinner.getValue();
        
        // Utiliser le service pour créer la réservation
        lastCreatedReservation = reservationService.creerReservation(
            user, 
            currentEvent,
            stdQty,
            vipQty, 
            premQty,
            payNowRadio.isSelected()
        );

        
        // Afficher la confirmation et rediriger selon le choix de paiement
        if (payNowRadio.isSelected()) {
            // Paiement immédiat - rediriger vers l'interface de paiement
            NotificationUtils.showSuccess("Réservation créée ! Redirection vers le paiement...");
            redirectToPayment();
        } else {
            // Paiement différé - rediriger vers l'historique des réservations
            String message = "Réservation enregistrée ! N'oubliez pas de finaliser le paiement dans votre historique avant 24h de l'événement.";
            NotificationUtils.showSuccess("Réservation réussie - " + message);
            redirectToReservationsHistory();
        }
    }

    // === GESTION SPÉCIALISÉE DES EXCEPTIONS ===

    /**
     * Gestion spécialisée de l'exception PlacesInsuffisantesException
     * Propose des solutions à l'utilisateur
     */
    private void handlePlacesInsuffisantesException(PlacesInsuffisantesException e) {
        // Afficher une notification d'erreur détaillée
        NotificationUtils.showError("Places insuffisantes: " + e.getMessage());
        
        // Proposer de réduire les quantités automatiquement
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Places insuffisantes");
        alert.setHeaderText("Voulez-vous ajuster automatiquement les quantités ?");
        alert.setContentText("Nous pouvons réduire vos sélections aux places disponibles.");
        
        ButtonType buttonAdjust = new ButtonType("Ajuster automatiquement");
        ButtonType buttonCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonAdjust, buttonCancel);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == buttonAdjust) {
                adjustToAvailableQuantities();
            }
        });
    }

    /**
     * Gestion spécialisée des erreurs métier
     */
    private void handleBusinessException(BusinessException e) {
        String message = e.getMessage();
        
        // Gestion spécifique selon le type d'erreur
        if (message.contains("événement passé")) {
            handleExpiredEventException();
        } else if (message.contains("non connecté")) {
            handleUserNotConnectedException();
        } else if (message.contains("Maximum") && message.contains("places")) {
            handleMaxTicketsException(message);
        } else {
            // Erreur générique
            NotificationUtils.showError("Erreur: " + message);
        }
    }

    /**
     * Gestion spécifique de l'erreur d'événement expiré
     */
    private void handleExpiredEventException() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Événement non disponible");
        alert.setHeaderText("Réservation impossible");
        alert.setContentText("Cet événement est déjà terminé ou ses réservations sont fermées.\n\nVous allez être redirigé vers la liste des événements disponibles.");
        
        ButtonType okButton = new ButtonType("Voir d'autres événements", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeButton = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, closeButton);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == okButton && dashboardController != null) {
                dashboardController.showEvents();
            }
        });
    }

    /**
     * Gestion de l'erreur d'utilisateur non connecté
     */
    private void handleUserNotConnectedException() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Session expirée");
        alert.setHeaderText("Vous avez été déconnecté");
        alert.setContentText("Votre session a expiré. Veuillez vous reconnecter pour continuer.");
        
        alert.showAndWait();
        
        // Redirection vers la page de connexion si possible
        logger.info("Utilisateur déconnecté détecté, demande de reconnexion");
    }

    /**
     * Gestion de l'erreur de limite de places
     */
    private void handleMaxTicketsException(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Limite de places");
        alert.setHeaderText("Trop de places sélectionnées");
        alert.setContentText(message + "\n\nVeuillez réduire le nombre de places.");
        
        ButtonType adjustButton = new ButtonType("Ajuster automatiquement", ButtonBar.ButtonData.OK_DONE);
        ButtonType manualButton = new ButtonType("Ajuster manuellement", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(adjustButton, manualButton);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == adjustButton) {
                adjustToMaximumAllowed();
            }
        });
    }

    /**
     * Ajuste les quantités au maximum autorisé (10 places total)
     */
    private void adjustToMaximumAllowed() {
        int currentTotal = standardSpinner.getValue() + vipSpinner.getValue() + premiumSpinner.getValue();
        if (currentTotal > 10) {
            // Stratégie d'ajustement: conserver les proportions autant que possible
            double ratio = 10.0 / currentTotal;
            
            int newStandard = Math.max(0, (int) Math.floor(standardSpinner.getValue() * ratio));
            int newVip = Math.max(0, (int) Math.floor(vipSpinner.getValue() * ratio));
            int newPremium = Math.max(0, (int) Math.floor(premiumSpinner.getValue() * ratio));
            
            // Ajuster pour atteindre exactement 10 si nécessaire
            int remaining = 10 - (newStandard + newVip + newPremium);
            if (remaining > 0 && standardSpinner.getValue() > 0) {
                newStandard += remaining;
            } else if (remaining > 0 && vipSpinner.getValue() > 0) {
                newVip += remaining;
            } else if (remaining > 0 && premiumSpinner.getValue() > 0) {
                newPremium += remaining;
            }
            
            standardSpinner.getValueFactory().setValue(newStandard);
            vipSpinner.getValueFactory().setValue(newVip);
            premiumSpinner.getValueFactory().setValue(newPremium);
            
            updateTotal();
            NotificationUtils.showInfo("Ajustement", "Les quantités ont été ajustées au maximum autorisé (10 places).");
        }
    }

    /**
     * Gestion des erreurs inattendues
     */
    private void handleUnexpectedException(Exception e) {
        NotificationUtils.showError("Une erreur inattendue s'est produite. Veuillez réessayer ou contacter le support.");
        
        // Log pour débuggage
        logger.error("Stack trace complète:", e);
    }

    /**
     * Ajuste automatiquement les quantités aux places disponibles
     */
    private void adjustToAvailableQuantities() {
        if (currentEvent != null) {
            int maxStandard = currentEvent.getPlacesStandardRestantes();
            int maxVip = currentEvent.getPlacesVipRestantes();
            int maxPremium = currentEvent.getPlacesPremiumRestantes();

            // Ajuster si nécessaire
            if (standardSpinner.getValue() > maxStandard) {
                standardSpinner.getValueFactory().setValue(maxStandard);
            }
            if (vipSpinner.getValue() > maxVip) {
                vipSpinner.getValueFactory().setValue(maxVip);
            }
            if (premiumSpinner.getValue() > maxPremium) {
                premiumSpinner.getValueFactory().setValue(maxPremium);
            }
            
            // Mettre à jour l'affichage
            updateTotal();
            
            // Informer l'utilisateur
            NotificationUtils.showInfo("Ajustement", "Les quantités ont été ajustées aux places disponibles.");
        }
    }

    // === MÉTHODES DE REDIRECTION ===

    /**
     * Redirige vers l'interface de paiement pour finaliser la transaction
     */
    private void redirectToPayment() {
        logger.info("Redirection vers l'interface de paiement");
        
        if (dashboardController != null) {
            try {
                // Charger l'interface de paiement
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/payment.fxml"));
                ScrollPane paymentRoot = loader.load();
                
                // Récupérer le contrôleur
                PaymentController paymentController = loader.getController();
                paymentController.setDashboardController(dashboardController);
                
                // Calculer le montant total
                BigDecimal totalAmount = calculateTotalAmount();
                
                // Passer les données de la réservation créée et l'événement
                paymentController.setReservationData(lastCreatedReservation, currentEvent, totalAmount);
                
                // Remplacer le contenu du dashboard
                dashboardController.showPaymentInterface(paymentRoot);
                
                logger.info("✓ Interface de paiement chargée");
                
            } catch (IOException e) {
                logger.error("Erreur lors du chargement de l'interface de paiement", e);
                
                // Fallback vers une simulation
                Alert paymentAlert = new Alert(Alert.AlertType.INFORMATION);
                paymentAlert.setTitle("Interface de paiement");
                paymentAlert.setHeaderText("Redirection vers le paiement");
                paymentAlert.setContentText("L'interface de paiement temporaire.\nVotre réservation est en attente de paiement.");
                
                paymentAlert.showAndWait().ifPresent(response -> 
                    dashboardController.showEvents()
                );
            }
        }
    }

    /**
     * Redirige vers l'historique des réservations du client
     */
    private void redirectToReservationsHistory() {
        logger.info("Redirection vers l'historique des réservations");
        
        if (dashboardController != null) {
            // Rediriger vers l'onglet réservations du dashboard
            dashboardController.showReservations();
        }
    }

    /**
     * Calcule le montant total de la réservation actuelle
     */
    private BigDecimal calculateTotalAmount() {
        double total = 0.0;

        if (standardSpinner != null && standardSpinner.getValue() > 0) {
            total += standardSpinner.getValue() * currentEvent.getPrixStandard().doubleValue();
        }
        
        if (vipSpinner != null && vipSpinner.getValue() > 0) {
            total += vipSpinner.getValue() * currentEvent.getPrixVip().doubleValue();
        }
        
        if (premiumSpinner != null && premiumSpinner.getValue() > 0) {
            total += premiumSpinner.getValue() * currentEvent.getPrixPremium().doubleValue();
        }

        return BigDecimal.valueOf(total);
    }
}