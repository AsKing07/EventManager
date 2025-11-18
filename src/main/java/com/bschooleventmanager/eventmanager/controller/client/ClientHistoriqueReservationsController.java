package com.bschooleventmanager.eventmanager.controller.client;

import com.bschooleventmanager.eventmanager.exception.AnnulationTardiveException;
import com.bschooleventmanager.eventmanager.exception.BusinessException;
import com.bschooleventmanager.eventmanager.model.Evenement;
import com.bschooleventmanager.eventmanager.model.Reservation;
import com.bschooleventmanager.eventmanager.model.ReservationDetail;
import com.bschooleventmanager.eventmanager.model.Utilisateur;
import com.bschooleventmanager.eventmanager.model.enums.StatutReservation;
import com.bschooleventmanager.eventmanager.service.ReservationService;
import com.bschooleventmanager.eventmanager.service.EvenementService;
import com.bschooleventmanager.eventmanager.util.SessionManager;
import com.bschooleventmanager.eventmanager.util.NotificationUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Contrôleur pour l'interface de l'historique des réservations client
 * Gère l'affichage, le paiement et l'annulation des réservations
 */
public class ClientHistoriqueReservationsController {
    private static final Logger logger = LoggerFactory.getLogger(ClientHistoriqueReservationsController.class);

    // Éléments FXML
    @FXML private Button refreshButton;
    @FXML private ComboBox<StatutReservation> statusFilter;
    @FXML private Label totalReservationsLabel;
    @FXML private VBox noReservationsContainer;
    @FXML private VBox reservationsContainer;
    @FXML private VBox loadingContainer;
    @FXML private VBox errorContainer;
    @FXML private Label errorMessageLabel;

    // Services
    private final ReservationService reservationService = new ReservationService();
    private final EvenementService evenementService = new EvenementService();
    
    // Référence au dashboard
    private ClientDashboardController dashboardController;
    
    // Données
    private List<Reservation> allReservations;
    private Utilisateur currentUser;
    
    // Cache pour éviter de refaire les mêmes requêtes
    private final Map<Integer, Evenement> evenementCache = new HashMap<>();

    @FXML
    public void initialize() {
        logger.info("Initialisation de l'interface des réservations client");
        
        currentUser = SessionManager.getUtilisateurConnecte();
        if (currentUser == null) {
            showError("Utilisateur non connecté");
            return;
        }

        setupStatusFilter();
        loadReservations();
    }

    /**
     * Définit la référence au dashboard controller
     */
    public void setDashboardController(ClientDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    /**
     * Configure le filtre de statut
     */
    private void setupStatusFilter() {
        statusFilter.getItems().clear();
        statusFilter.getItems().add(null); // Pour "Tous les statuts"
        statusFilter.getItems().addAll(StatutReservation.values());
        
        statusFilter.setConverter(new javafx.util.StringConverter<StatutReservation>() {
            @Override
            public String toString(StatutReservation statut) {
                if (statut == null) return "Tous les statuts";
                return switch (statut) {
                    case EN_ATTENTE -> "En attente de paiement";
                    case CONFIRMEE -> "Confirmée";
                    case ANNULEE -> "Annulée";
                };
            }

            @Override
            public StatutReservation fromString(String string) {
                return null; // Non utilisé
            }
        });

        // Écouter les changements de filtre (avec debounce pour éviter trop d'appels)
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (allReservations != null && !allReservations.isEmpty()) {
                filterReservations();
            }
        });
    }

    /**
     * Actualise les données (bouton refresh) 
     */
    @FXML
    private void handleRefresh() {
        // Vider les caches et recharger pour éviter les connexions multiples
        evenementCache.clear();
        allReservations = null;
        loadReservations();
    }

    private void loadReservations() {
        showLoading(true);
        
        CompletableFuture.supplyAsync(() -> {
            try {
                List<Reservation> reservations = reservationService.getReservationsClient(currentUser.getIdUtilisateur());
                
                
                // Pré-charger tous les détails en une seule fois pour éviter trop de connexions
                for (Reservation reservation : reservations) {
                    try {
                        if (reservation.getDetails() == null || reservation.getDetails().isEmpty()) {
                            List<ReservationDetail> details = reservationService.getDetailsReservation(reservation.getIdReservation());
                            reservation.setDetails(details);
                            
                        }
                    } catch (Exception e) {
                        logger.warn("Erreur lors du chargement des détails de la réservation {}: {}", 
                                  reservation.getIdReservation(), e.getMessage());
                    }
                }
                
                return reservations;
            } catch (BusinessException e) {
                logger.error("Erreur lors du chargement des réservations", e);
                Platform.runLater(() -> showError("Erreur lors du chargement: " + e.getMessage()));
                return null;
            }
        }).thenAccept(reservations -> Platform.runLater(() -> {
            if (reservations != null) {
                this.allReservations = reservations;
                displayReservations(reservations);
                updateTotalLabel(reservations.size());
                showLoading(false);
            } else {
                showLoading(false);
            }
        }));
    }

    /**
     * Filtre les réservations selon le statut sélectionné
     */
    private void filterReservations() {
        if (allReservations == null) return;
        
        StatutReservation selectedStatus = statusFilter.getValue();
        List<Reservation> filteredReservations;
        
        if (selectedStatus == null) {
            filteredReservations = allReservations;
        } else {
            filteredReservations = allReservations.stream()
                .filter(r -> r.getStatut() == selectedStatus)
                .toList();
        }
        
        displayReservations(filteredReservations);
        updateTotalLabel(filteredReservations.size());
    }

    /**
     * Affiche les réservations dans l'interface
     */
    private void displayReservations(List<Reservation> reservations) {
        reservationsContainer.getChildren().clear();
        
        if (reservations.isEmpty()) {
            showNoReservations(true);
            return;
        }
        
        showNoReservations(false);
        
        for (Reservation reservation : reservations) {
            VBox reservationCard = createReservationCard(reservation);
            reservationsContainer.getChildren().add(reservationCard);
        }
    }

    /**
     * Crée une carte pour une réservation
     */
    private VBox createReservationCard(Reservation reservation) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2); " +
                     "-fx-padding: 20;");

        // Header avec nom de l'événement et statut
        HBox header = createReservationHeader(reservation);
        
        // Informations principales
        GridPane infoGrid = createReservationInfo(reservation);
        
        // Détails des tickets
        VBox detailsSection = createTicketDetails(reservation);
        
        // Actions (boutons)
        HBox actionsBox = createActionButtons(reservation);

        card.getChildren().addAll(header, new Separator(), infoGrid, detailsSection, actionsBox);
        
        return card;
    }

    /**
     * Crée l'en-tête de la carte de réservation
     */
    private HBox createReservationHeader(Reservation reservation) {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        // Nom de l'événement avec cache
        try {
            Evenement evenement = evenementCache.get(reservation.getIdEvenement());
            if (evenement == null) {
                evenement = evenementService.getEvenementById(reservation.getIdEvenement());
                if (evenement != null) {
                    evenementCache.put(reservation.getIdEvenement(), evenement);
                }
            }
            
            Text eventName = new Text(evenement != null ? evenement.getNom() : "Événement #" + reservation.getIdEvenement());
            eventName.setFont(Font.font("System", FontWeight.BOLD, 18));
            eventName.setStyle("-fx-fill: #2c3e50;");
            header.getChildren().add(eventName);
        } catch (Exception _) {
            logger.warn("Impossible de charger l'événement {}", reservation.getIdEvenement());
            Text eventName = new Text("Événement #" + reservation.getIdEvenement());
            eventName.setFont(Font.font("System", FontWeight.BOLD, 18));
            header.getChildren().add(eventName);
        }

        // Espaceur
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(spacer);

        // Badge de statut
        Label statusBadge = createStatusBadge(reservation.getStatut());
        header.getChildren().add(statusBadge);

        return header;
    }

    /**
     * Crée un badge de statut coloré
     */
    private Label createStatusBadge(StatutReservation statut) {
        Label badge = new Label();
        badge.setPadding(new Insets(5, 10, 5, 10));
        badge.setStyle("-fx-background-radius: 15; -fx-font-weight: bold; -fx-font-size: 12px;");

        switch (statut) {
            case EN_ATTENTE:
                badge.setText("⏳ En attente de paiement");
                badge.setStyle(badge.getStyle() + "-fx-background-color: #f39c12; -fx-text-fill: white;");
                break;
            case CONFIRMEE:
                badge.setText("✅ Confirmée");
                badge.setStyle(badge.getStyle() + "-fx-background-color: #27ae60; -fx-text-fill: white;");
                break;
            case ANNULEE:
                badge.setText("❌ Annulée");
                badge.setStyle(badge.getStyle() + "-fx-background-color: #e74c3c; -fx-text-fill: white;");
                break;
        }

        return badge;
    }

    /**
     * Crée la grille d'informations de la réservation
     */
    private GridPane createReservationInfo(Reservation reservation) {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);

        // Numéro de réservation formaté (masqué pour la sécurité)
        String numeroReservation = generateReservationNumber(reservation.getIdReservation());
        addInfoRow(grid, 0, "📋 Réservation", numeroReservation);

        // Date de réservation
        addInfoRow(grid, 1, "📅 Date de réservation", reservation.getDateReservation());

        // Total payé
        String totalText = String.format("%.2f €", reservation.getTotalPaye());
        if (reservation.getTotalPaye() == 0.0) {
            totalText += " (Gratuit)";
        }
        addInfoRow(grid, 2, "💰 Total", totalText);

        return grid;
    }

    /**
     * Ajoute une ligne d'information à la grille
     */
    private void addInfoRow(GridPane grid, int row, String label, String value) {
        Label labelControl = new Label(label);
        labelControl.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        Label valueControl = new Label(value);
        valueControl.setStyle("-fx-text-fill: #2c3e50;");

        grid.add(labelControl, 0, row);
        grid.add(valueControl, 1, row);
    }

    /**
     * Crée la section des détails des tickets
     */
    private VBox createTicketDetails(Reservation reservation) {
        VBox detailsBox = new VBox(8);
        
        Label detailsTitle = new Label("🎫 Détails des tickets");
        detailsTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e; -fx-font-size: 14px;");
        detailsBox.getChildren().add(detailsTitle);

        try {
            // Utiliser les détails déjà chargés pour éviter une nouvelle connexion DB
            List<ReservationDetail> details = reservation.getDetails();
            
            if (details == null || details.isEmpty()) {
                Label noDetails = new Label("Aucun détail disponible");
                noDetails.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
                detailsBox.getChildren().add(noDetails);
            } else {
                for (ReservationDetail detail : details) {
                    HBox detailLine = new HBox(10);
                    detailLine.setAlignment(Pos.CENTER_LEFT);
                    
                    String categoryText = detail.getCategoriePlace().toString().toLowerCase();
                    categoryText = categoryText.substring(0, 1).toUpperCase() + categoryText.substring(1);
                    
                    Label categoryLabel = new Label(String.format("• %s x%d", categoryText, detail.getNombreTickets()));
                    categoryLabel.setStyle("-fx-text-fill: #2c3e50;");
                    
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    Label priceLabel = new Label(String.format("%.2f €", detail.getSousTotal()));
                    priceLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    
                    detailLine.getChildren().addAll(categoryLabel, spacer, priceLabel);
                    detailsBox.getChildren().add(detailLine);
                }
            }
        } catch (Exception e) {
            logger.error("Erreur lors de l'affichage des détails de la réservation {}", reservation.getIdReservation(), e);
            Label errorLabel = new Label("Erreur lors de l'affichage des détails");
            errorLabel.setStyle("-fx-text-fill: #e74c3c;");
            detailsBox.getChildren().add(errorLabel);
        }

        return detailsBox;
    }

    /**
     * Crée les boutons d'action pour une réservation
     */
    private HBox createActionButtons(Reservation reservation) {
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        // Bouton Effectuer le paiement (si en attente)
        if (reservation.getStatut() == StatutReservation.EN_ATTENTE) {
            Button payButton = new Button("💳 Effectuer le paiement");
            payButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                              "-fx-background-radius: 6; -fx-font-weight: bold; -fx-cursor: hand; " +
                              "-fx-padding: 8 15 8 15;");
            payButton.setOnAction(e -> handlePayment(reservation));
            actionsBox.getChildren().add(payButton);
        }

        // Bouton Annuler (si confirmée ou en attente)
        if (reservation.getStatut() == StatutReservation.CONFIRMEE || 
            reservation.getStatut() == StatutReservation.EN_ATTENTE) {
            
            Button cancelButton = new Button("🗑️ Annuler");
            cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                                 "-fx-background-radius: 6; -fx-font-weight: bold; -fx-cursor: hand; " +
                                 "-fx-padding: 8 15 8 15;");
            cancelButton.setOnAction(e -> handleCancellation(reservation));
            actionsBox.getChildren().add(cancelButton);
        }

        return actionsBox;
    }

    /**
     * Gère le paiement d'une réservation
     */
    private void handlePayment(Reservation reservation) {
        logger.info("Redirection vers le paiement pour la réservation {}", reservation.getIdReservation());
        
        try {
            // Charger l'événement pour obtenir les détails
            Evenement evenement = evenementService.getEvenementById(reservation.getIdEvenement());
            if (evenement == null) {
                NotificationUtils.showError("Impossible de charger les détails de l'événement");
                return;
            }

            // Charger l'interface de paiement
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/payment.fxml"));
            Parent paymentRoot = loader.load();
            
            // Récupérer le contrôleur
            PaymentController paymentController = loader.getController();
            paymentController.setDashboardController(dashboardController);
            
            // Calculer le montant à payer
            BigDecimal totalAmount = BigDecimal.valueOf(reservation.getTotalPaye());
            
            // Passer les données de la réservation
            paymentController.setReservationData(reservation, evenement, totalAmount);
            
            // Afficher l'interface de paiement dans le dashboard
            if (dashboardController != null) {
                dashboardController.showPaymentInterface(paymentRoot);
            }
            
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de l'interface de paiement", e);
            NotificationUtils.showError("Impossible de charger l'interface de paiement");
        } catch (Exception e) {
            logger.error("Erreur lors de la redirection vers le paiement", e);
            NotificationUtils.showError("Erreur lors de la redirection vers le paiement");
        }
    }

    /**
     * Gère l'annulation d'une réservation
     */
    private void handleCancellation(Reservation reservation) {
        // Confirmation de l'utilisateur
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmer l'annulation");
        
        String numeroReservation = generateReservationNumber(reservation.getIdReservation());
        confirmDialog.setHeaderText("Annuler la réservation " + numeroReservation);
        confirmDialog.setContentText("Êtes-vous sûr de vouloir annuler cette réservation ?\n" +
                                     "Cette action est irréversible. \n" +
                                     "Vous recevrez un mail de confirmation vous indiquant le processus de remboursement. \n" +
                                     "Des frais peuvent s'appliquer selon les conditions générales.");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                performCancellation(reservation);
            }
        });
    }

    /**
     * Effectue l'annulation de la réservation
     */
    private void performCancellation(Reservation reservation) {
        CompletableFuture.runAsync(() -> {
            try {
                reservationService.annulerReservation(reservation.getIdReservation(), currentUser);
                
                Platform.runLater(() -> {
                    NotificationUtils.showSuccess("Réservation annulée avec succès");
                    loadReservations(); // Recharger la liste
                });
                
            } catch (AnnulationTardiveException _) {
                Platform.runLater(() -> {
                    Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                    warningAlert.setTitle("Annulation tardive");
                    warningAlert.setHeaderText("Attention : Annulation moins de 24h avant l'événement");
                    warningAlert.setContentText("Cette réservation ne peut pas être annulée car l'événement a lieu dans moins de 24 heures.");
                    warningAlert.show();
                });
                
            } catch (BusinessException _) {
                Platform.runLater(() -> {
                    logger.error("Erreur lors de l'annulation de la réservation {}", reservation.getIdReservation());
                    NotificationUtils.showError("Erreur lors de l'annulation de la réservation");
                });
            }
        });
    }

    /**
     * Redirige vers la liste des événements
     */
    @FXML
    private void handleGoToEvents() {
        if (dashboardController != null) {
            dashboardController.showEvents();
        }
    }

    // === MÉTHODES UTILITAIRES D'AFFICHAGE ===

    private void showLoading(boolean show) {
        loadingContainer.setVisible(show);
        loadingContainer.setManaged(show);
        
        if (show) {
            noReservationsContainer.setVisible(false);
            noReservationsContainer.setManaged(false);
            errorContainer.setVisible(false);
            errorContainer.setManaged(false);
            reservationsContainer.getChildren().clear();
        }
    }

    private void showNoReservations(boolean show) {
        noReservationsContainer.setVisible(show);
        noReservationsContainer.setManaged(show);
    }

    private void showError(String message) {
        errorMessageLabel.setText(message);
        errorContainer.setVisible(true);
        errorContainer.setManaged(true);
        
        loadingContainer.setVisible(false);
        loadingContainer.setManaged(false);
        noReservationsContainer.setVisible(false);
        noReservationsContainer.setManaged(false);
    }



    private void updateTotalLabel(int count) {
        totalReservationsLabel.setText(String.format("Total: %d réservation%s", count, count > 1 ? "s" : ""));
    }

    /**
     * Génère un numéro de réservation formaté pour masquer l'ID interne
     * Format: RES-YYYY-XXXX où XXXX est un code basé sur l'ID mais moins prévisible
     */
    private String generateReservationNumber(int idReservation) {
        // Utiliser l'année courante
        int currentYear = java.time.LocalDateTime.now().getYear();
        
        // Générer un code à partir de l'ID mais moins prévisible
        // On utilise une transformation simple pour masquer l'ID réel
        int maskedId = (idReservation * 7 + 1000) % 10000;
        
        return String.format("RES-%d-%04d", currentYear, maskedId);
    }
}