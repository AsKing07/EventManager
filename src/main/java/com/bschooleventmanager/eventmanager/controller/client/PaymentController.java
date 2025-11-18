package com.bschooleventmanager.eventmanager.controller.client;

import com.bschooleventmanager.eventmanager.exception.BusinessException;
import com.bschooleventmanager.eventmanager.exception.PaiementInvalideException;
import com.bschooleventmanager.eventmanager.model.Evenement;
import com.bschooleventmanager.eventmanager.model.Paiement;
import com.bschooleventmanager.eventmanager.model.Reservation;
import com.bschooleventmanager.eventmanager.model.enums.MethodePaiement;
import com.bschooleventmanager.eventmanager.service.PaiementService;
import com.bschooleventmanager.eventmanager.service.StripePaymentService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

/**
 * Contrôleur pour l'interface de paiement
 * Gère la saisie et le traitement des paiements
 */
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

    // === Éléments FXML ===
    @FXML private Button backButton;
    @FXML private VBox reservationSummaryContainer;
    @FXML private Label totalAmountLabel;
    
    // Méthodes de paiement
    @FXML private RadioButton creditCardRadio;
    @FXML private RadioButton stripeRadio;
    
    // Formulaire carte de crédit
    @FXML private VBox creditCardForm;
    @FXML private TextField cardHolderNameField;
    @FXML private TextField cardNumberField;
    @FXML private ComboBox<String> expiryMonthCombo;
    @FXML private ComboBox<String> expiryYearCombo;
    @FXML private TextField cvvField;
    
    // Formulaire Stripe
    @FXML private VBox stripeForm;
    @FXML private TextField stripeNameField;
    @FXML private TextField stripeTestCardField;
    
    @FXML private Label errorLabel;
    @FXML private Button cancelButton;
    @FXML private Button payButton;

    // === Services et données ===
    private final PaiementService paiementService = new PaiementService();
    private final StripePaymentService stripeService = new StripePaymentService();
    private ClientDashboardController dashboardController;
    private Reservation currentReservation;
    private Evenement currentEvent;
    private BigDecimal amountToPay;

    @FXML
    private void initialize() {
        logger.info("Initialisation du contrôleur de paiement");
        
        // Grouper les radio buttons
        ToggleGroup paymentMethodGroup = new ToggleGroup();
        creditCardRadio.setToggleGroup(paymentMethodGroup);
        stripeRadio.setToggleGroup(paymentMethodGroup);
        
        // Écouter les changements de méthode de paiement
        creditCardRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            creditCardForm.setVisible(newVal);
            creditCardForm.setManaged(newVal);
            stripeForm.setVisible(!newVal);
            stripeForm.setManaged(!newVal);
        });
        
        stripeRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            stripeForm.setVisible(newVal);
            stripeForm.setManaged(newVal);
            creditCardForm.setVisible(!newVal);
            creditCardForm.setManaged(!newVal);
        });
        
        // Masquer les erreurs par défaut
        hideError();
        
        // Formater automatiquement le numéro de carte
        setupCardNumberFormatting();
        
        // Limiter la saisie CVV
        setupCvvFieldLimits();
        
        // Initialiser les ComboBox de date d'expiration
        initializeExpirationComboBoxes();
        
        // Vérifier le statut de Stripe
        checkStripeStatus();
    }

    /**
     * Configure le formatage automatique du numéro de carte
     */
    private void setupCardNumberFormatting() {
        cardNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\d\\s]*")) {
                cardNumberField.setText(oldValue);
                return;
            }
            
            String formatted = formatCardNumber(newValue.replaceAll("\\s", ""));
            if (!formatted.equals(newValue)) {
                cardNumberField.setText(formatted);
                cardNumberField.positionCaret(formatted.length());
            }
        });
    }

    /**
     * Configure les limites du champ CVV
     */
    private void setupCvvFieldLimits() {
        cvvField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                cvvField.setText(oldValue);
                return;
            }
            
            if (newValue.length() > 4) {
                cvvField.setText(oldValue);
            }
        });
    }

    /**
     * Formate un numéro de carte avec des espaces
     */
    private String formatCardNumber(String cardNumber) {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < cardNumber.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");
            }
            formatted.append(cardNumber.charAt(i));
        }
        return formatted.toString();
    }

    /**
     * Initialise les ComboBox de date d'expiration
     */
    private void initializeExpirationComboBoxes() {
        // Remplir le ComboBox des mois
        for (int i = 1; i <= 12; i++) {
            expiryMonthCombo.getItems().add(String.format("%02d", i));
        }
        
        // Remplir le ComboBox des années (prochaines 10 années)
        int currentYear = java.time.LocalDate.now().getYear();
        for (int i = 0; i < 10; i++) {
            int year = currentYear + i;
            expiryYearCombo.getItems().add(String.valueOf(year % 100)); // Format 2 chiffres (24, 25, etc.)
        }
        
        // Ajouter un bouton pour utiliser une carte de test
        setupTestCardButton();
    }

    /**
     * Configure le bouton pour pré-remplir une carte de test Stripe
     */
    private void setupTestCardButton() {
        // Créer un bouton "Carte de test" si pas déjà créé
        if (cardNumberField.getParent().lookup("#testCardButton") == null) {
            javafx.scene.control.Button testCardButton = new javafx.scene.control.Button("Carte Test Visa");
            testCardButton.setId("testCardButton");
            testCardButton.getStyleClass().add("button-secondary");
            testCardButton.setOnAction(e -> fillTestCard());
            
            // Le parent est un GridPane, on ajoute le bouton dans une nouvelle ligne
            javafx.scene.layout.GridPane gridPane = (javafx.scene.layout.GridPane) cardNumberField.getParent();
            
            // Trouver la ligne suivante disponible
            int nextRowIndex = 4; // Après CVV qui est en ligne 3
            
            // Ajouter le bouton sur 2 colonnes
            gridPane.add(testCardButton, 0, nextRowIndex, 2, 1);
            javafx.scene.layout.GridPane.setHalignment(testCardButton, javafx.geometry.HPos.CENTER);
        }
    }

    /**
     * Pré-remplit le formulaire avec une carte de test Stripe valide
     */
    private void fillTestCard() {
        cardNumberField.setText("4242424242424242"); // Carte Visa de test Stripe
        expiryMonthCombo.setValue("12");
        expiryYearCombo.setValue("25");
        cvvField.setText("123");
        
        // Afficher une info à l'utilisateur
        showError("✓ Carte de test Stripe pré-remplie (toujours acceptée)");
    }

    /**
     * Vérifie le statut de Stripe et met à jour l'interface
     */
    private void checkStripeStatus() {
        boolean stripeConfigured = stripeService.isConfigured();
        
        if (stripeConfigured) {
            logger.info("✓ Stripe configuré ");
            stripeRadio.setText("Paiement Stripe ");
        } else {
            logger.warn("⚠️ Stripe non configuré - Mode simulation");
            stripeRadio.setText("Paiement Stripe (Mode simulation)");
        }
    }

    /**
     * Définit les données de réservation à afficher
     */
    public void setReservationData(Reservation reservation, Evenement event, BigDecimal amountToPay) {
        this.currentReservation = reservation;
        this.currentEvent = event;
        this.amountToPay = amountToPay;
        
        populateReservationSummary();
        updateTotalAmount();
    }

    /**
     * Définit le contrôleur dashboard pour navigation
     */
    public void setDashboardController(ClientDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    /**
     * Remplit le résumé de la réservation
     */
    private void populateReservationSummary() {
        if (currentReservation == null || currentEvent == null) return;
        
        reservationSummaryContainer.getChildren().clear();
        
        // Nom de l'événement
        addSummaryLine("Événement", currentEvent.getNom());
        
        // Date de l'événement
        addSummaryLine("Date", currentEvent.getDateEvenement().format(dateFormatter));
        
        // Lieu
        addSummaryLine("Lieu", currentEvent.getLieu());
        
        // Détails des places réservées
        // Note: Ces informations devront être récupérées depuis ReservationDetails

        currentReservation.getDetails().forEach(detail -> {
            String placeType = detail.getCategoriePlace().toString();
            String prixUnitaire ="(" +  String.format("%.2f €", detail.getPrixUnitaire()) + ")";
            String quantity = "x " + detail.getNombreTickets();
            String totalPrice = String.format("%.2f €", detail.getSousTotal());
            addSummaryLine("Places " + placeType, quantity);
        });


        
        addSummaryLine("Date de réservation", currentReservation.getDateReservation());
        addSummaryLine("Statut", currentReservation.getStatut().toString());
    }

    /**
     * Ajoute une ligne au résumé
     */
    private void addSummaryLine(String label, String value) {
        HBox line = new HBox(10);
        line.setStyle("-fx-padding: 5; -fx-background-color: #f8f9fa; -fx-background-radius: 3;");
        
        Label labelNode = new Label(label + " :");
        labelNode.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
        
        Label valueNode = new Label(value);
        valueNode.setStyle("-fx-text-fill: #2c3e50;");
        
        line.getChildren().addAll(labelNode, valueNode);
        reservationSummaryContainer.getChildren().add(line);
    }

    /**
     * Met à jour l'affichage du montant total
     */
    private void updateTotalAmount() {
        if (amountToPay != null) {
            totalAmountLabel.setText(String.format("%.2f €", amountToPay.doubleValue()));
        }
    }

    /**
     * Retour au dashboard
     */
    @FXML
    private void handleBack() {
        if (dashboardController != null) {
            dashboardController.showReservations();
        }
    }

    /**
     * Annulation du paiement
     */
    @FXML
    private void handleCancel() {
        handleBack();
    }

    /**
     * Traitement du paiement
     */
    @FXML
    private void handlePayment() {
        hideError();
        
        if (currentReservation == null || amountToPay == null) {
            showError("Données de réservation manquantes");
            return;
        }

        // Validation des données selon la méthode sélectionnée
        try {
            MethodePaiement methode;
            String nom, numeroCarteOuToken, cvv, mois, annee;

            if (creditCardRadio.isSelected()) {
                methode = MethodePaiement.CARTE_CREDIT;
                nom = cardHolderNameField.getText();
                numeroCarteOuToken = cardNumberField.getText().replaceAll("\\s", "");
                cvv = cvvField.getText();
                mois = expiryMonthCombo.getValue();
                annee = expiryYearCombo.getValue();
            } else {
                methode = MethodePaiement.STRIPE;
                nom = stripeNameField.getText();
                numeroCarteOuToken = stripeTestCardField.getText().replaceAll("\\s", "");
                cvv = "";
                mois = "";
                annee = "";
            }

            // Validation basique
            if (nom == null || nom.trim().isEmpty()) {
                showError("Le nom est obligatoire");
                return;
            }

            if (numeroCarteOuToken == null || numeroCarteOuToken.trim().isEmpty()) {
                showError("Le numéro de carte est obligatoire");
                return;
            }

            // Désactiver le bouton pour éviter les doubles clics
            payButton.setDisable(true);
            payButton.setText("Traitement en cours...");

            // Traiter le paiement de manière asynchrone
            CompletableFuture.supplyAsync(() -> {
                try {
                    return paiementService.traiterPaiement(
                        currentReservation.getIdReservation(),
                        nom.trim(),
                        numeroCarteOuToken,
                        cvv,
                        mois,
                        annee,
                        methode
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).whenComplete((paiement, throwable) -> 
                Platform.runLater(() -> {
                    payButton.setDisable(false);
                    payButton.setText("Payer maintenant");

                    if (throwable != null) {
                        handlePaymentError(throwable.getCause());
                    } else {
                        handlePaymentSuccess(paiement);
                    }
                })
            );

        } catch (Exception e) {
            logger.error("Erreur lors du traitement du paiement", e);
            showError("Erreur lors du traitement: " + e.getMessage());
            payButton.setDisable(false);
            payButton.setText("Payer maintenant");
        }
    }

    /**
     * Gère le succès du paiement
     */
    private void handlePaymentSuccess(Paiement paiement) {
        logger.info("✓ Paiement réussi - Transaction: {}", paiement.getNumeroTransaction());
        
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Paiement réussi");
        successAlert.setHeaderText("Transaction confirmée");
        successAlert.setContentText(String.format(
            "Votre paiement de %.2f € a été traité avec succès.\n\n" +
            "Numéro de transaction : %s\n" +
            "Votre réservation est maintenant confirmée !",
            paiement.getMontant().doubleValue(),
            paiement.getNumeroTransaction()
        ));
        
        successAlert.showAndWait();
        
        // Retour au dashboard après succès
        handleBack();
    }

    /**
     * Gère les erreurs de paiement
     */
    private void handlePaymentError(Throwable cause) {
        if (cause instanceof PaiementInvalideException) {
            showError(cause.getMessage());
        } else if (cause instanceof BusinessException) {
            showError("Erreur métier : " + cause.getMessage());
        } else {
            logger.error("Erreur inattendue lors du paiement", cause);
            showError("Une erreur inattendue s'est produite. Veuillez réessayer.");
        }
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
     * Masque le message d'erreur
     */
    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}