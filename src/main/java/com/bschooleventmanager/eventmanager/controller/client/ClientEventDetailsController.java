package com.bschooleventmanager.eventmanager.controller.client;

import com.bschooleventmanager.eventmanager.model.Evenement;
import com.bschooleventmanager.eventmanager.util.NotificationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;

public class ClientEventDetailsController {
    
    private static final Logger logger = LoggerFactory.getLogger(ClientEventDetailsController.class);
    
    @FXML private Button backButton;
    @FXML private Label eventNameLabel;
    @FXML private Label eventDateLabel;
    @FXML private Label eventLocationLabel;
    @FXML private Label eventTypeLabel;
    @FXML private Label eventStatusLabel;
    @FXML private TextArea eventDescriptionArea;
    
    // Prix et places
    @FXML private Label priceStandardLabel;
    @FXML private Label priceVipLabel;
    @FXML private Label pricePremiumLabel;
    @FXML private Label placesStandardLabel;
    @FXML private Label placesVipLabel;
    @FXML private Label placesPremiumLabel;
    
    // Boutons d'action
    @FXML private Button reserveButton;
    @FXML private Button shareButton;

    private Evenement currentEvent;
    private ClientDashboardController dashboardController;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

    /**
     * Définit le contrôleur parent du dashboard pour la navigation
     */
    public void setDashboardController(ClientDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    /**
     * Called by ClientEventsController to inject the selected event data.
     * @param event The Evenement object to display.
     */
    public void setEventData(Evenement event) {
        this.currentEvent = event;
        populateUI();
    }

    /**
     * Remplit l'interface avec les données de l'événement
     */
    private void populateUI() {
        if (currentEvent != null) {
            // Informations de base
            eventNameLabel.setText(currentEvent.getNom());
            eventDateLabel.setText(currentEvent.getDateEvenement().format(dateFormatter));
            eventLocationLabel.setText(currentEvent.getLieu());
            eventTypeLabel.setText(getTypeDisplayName(currentEvent.getTypeEvenement().name()));
            eventStatusLabel.setText(getStatusDisplayName(currentEvent.getStatut().name()));
            
            // Description
            String description = currentEvent.getDescription();
            eventDescriptionArea.setText(description != null ? description : "Aucune description disponible.");
            
            // Tarification
            populatePricingInfo();
            
            logger.info("Interface des détails peuplée pour l'événement: {}", currentEvent.getNom());
        }
    }

    /**
     * Remplit les informations de tarification
     */
    private void populatePricingInfo() {
        // Prix Standard
        BigDecimal prixStandard = currentEvent.getPrixStandard();
        priceStandardLabel.setText(prixStandard != null ? prixStandard + "€" : "N/A");
        placesStandardLabel.setText(currentEvent.getPlacesStandardDisponibles() + " places disponibles");
        
        // Prix VIP
        BigDecimal prixVip = currentEvent.getPrixVip();
        priceVipLabel.setText(prixVip != null ? prixVip + "€" : "N/A");
        placesVipLabel.setText(currentEvent.getPlacesVipDisponibles() + " places disponibles");
        
        // Prix Premium
        BigDecimal prixPremium = currentEvent.getPrixPremium();
        pricePremiumLabel.setText(prixPremium != null ? prixPremium + "€" : "N/A");
        placesPremiumLabel.setText(currentEvent.getPlacesPremiumDisponibles() + " places disponibles");
    }

    /**
     * Convertit le nom de type en nom d'affichage lisible
     */
    private String getTypeDisplayName(String typeName) {
        switch (typeName) {
            case "CONCERT": return "Concert";
            case "CONFERENCE": return "Conférence";
            case "SPECTACLE": return "Spectacle";
            default: return typeName;
        }
    }

    /**
     * Convertit le nom de statut en nom d'affichage lisible
     */
    private String getStatusDisplayName(String statusName) {
        switch (statusName) {
            case "A_VENIR": return "À venir";
            case "EN_COURS": return "En cours";
            case "TERMINE": return "Terminé";
            case "ANNULE": return "Annulé";
            default: return statusName;
        }
    }

    /**
     * Gère le retour vers la liste des événements
     */
    @FXML
    private void handleBack() {
        logger.info("Retour vers la liste des événements demandé");
        if (dashboardController != null) {
            dashboardController.showEvents();
        }
    }

    @FXML
    public void initialize() {
        logger.info("Initialisation du contrôleur des détails d'événement");
        
       
        reserveButton.setOnAction(e -> handleReservation());
        shareButton.setOnAction(e -> handleShare());
    }

    /**
     * Gère la réservation d'un événement
     */
    private void handleReservation() {
        logger.info("Fonction de réservation appelée pour l'événement: {}", 
                   currentEvent != null ? currentEvent.getNom() : "N/A");
        
        if (currentEvent == null) {
            NotificationUtils.showError("Erreur: aucun événement sélectionné");
            return;
        }
        
        // Vérifier la disponibilité générale
        if (currentEvent.getPlacesStandardDisponibles() + currentEvent.getPlacesVipDisponibles() + 
            currentEvent.getPlacesPremiumDisponibles() <= 0) {
            NotificationUtils.showError("Désolé, cet événement est complet");
            return;
        }
        
        // Rediriger vers le formulaire de réservation
        if (dashboardController != null) {
            dashboardController.showReservationForm(currentEvent);
        }
    }

    /**
     * Gère le partage d'un événement
     */
    private void handleShare() {
        logger.info("Fonction de partage appelée pour l'événement: {}", 
                   currentEvent != null ? currentEvent.getNom() : "N/A");
        
        if (currentEvent != null) {
            String shareInfo = "Découvrez cet événement: " + currentEvent.getNom() + 
                             "\nDate: " + currentEvent.getDateEvenement().format(dateFormatter) + 
                             "\nLieu: " + currentEvent.getLieu();
            
            // Copier dans le presse-papiers (simulation)
            NotificationUtils.showInfo("Partage", "Informations copiées dans le presse-papiers!");
            logger.info("Informations partagées: {}", shareInfo);
        }
    }
}
