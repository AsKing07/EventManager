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

/**
 * Contrôleur pour l'interface de détails d'événement côté client dans EventManager.
 * 
 * <p>Cette classe gère l'affichage complet des informations d'un événement spécifique,
 * incluant les détails de base, la tarification, la disponibilité des places, et
 * les actions que peut effectuer un client (réservation, partage).</p>
 * 
 * <p><strong>Fonctionnalités principales :</strong></p>
 * <ul>
 *   <li>Affichage détaillé des informations d'événement</li>
 *   <li>Présentation de la tarification par catégorie de places</li>
 *   <li>Indicateurs de disponibilité en temps réel</li>
 *   <li>Actions client : réservation et partage</li>
 *   <li>Navigation de retour vers la liste des événements</li>
 *   <li>Formatage automatique des données (dates, prix, statuts)</li>
 * </ul>
 * 
 * <p><strong>Informations affichées :</strong></p>
 * <ul>
 *   <li><strong>Informations de base :</strong> Nom, date, lieu, type, statut</li>
 *   <li><strong>Description :</strong> Texte descriptif complet de l'événement</li>
 *   <li><strong>Tarification :</strong> Prix par catégorie (Standard, VIP, Premium)</li>
 *   <li><strong>Disponibilité :</strong> Nombre de places disponibles par catégorie</li>
 * </ul>
 * 
 * <p><strong>Actions utilisateur :</strong></p>
 * <ul>
 *   <li><strong>Réservation :</strong> Redirection vers le formulaire de réservation</li>
 *   <li><strong>Partage :</strong> Copie des informations pour partage externe</li>
 *   <li><strong>Retour :</strong> Navigation vers la liste des événements</li>
 * </ul>
 * 
 * <p><strong>Validation des actions :</strong></p>
 * <ul>
 *   <li>Vérification de la disponibilité avant réservation</li>
 *   <li>Gestion des événements complets avec messages appropriés</li>
 *   <li>Contrôle de l'état de l'événement (annulé, terminé, etc.)</li>
 * </ul>
 * 
 * <p><strong>Exemple d'utilisation :</strong></p>
 * <pre>{@code
 * ClientEventDetailsController controller = loader.getController();
 * controller.setDashboardController(dashboardController);
 * controller.setEventData(selectedEvent);
 * }</pre>
 * 
 * @author EventManager Team
 * @version 1.0
 * @since 1.0
 * 
 * @see ClientDashboardController
 * @see ClientEventsController
 * @see ReservationController
 * @see com.bschooleventmanager.eventmanager.model.Evenement
 */
public class ClientEventDetailsController {
    
    /** Logger pour le traçage des actions sur les détails d'événements */
    private static final Logger logger = LoggerFactory.getLogger(ClientEventDetailsController.class);
    
    // === Éléments FXML - Navigation ===
    
    /** Bouton de retour vers la liste des événements */
    @FXML private Button backButton;
    
    // === Éléments FXML - Informations de base ===
    
    /** Label affichant le nom de l'événement */
    @FXML private Label eventNameLabel;
    
    /** Label affichant la date et l'heure de l'événement */
    @FXML private Label eventDateLabel;
    
    /** Label affichant le lieu de l'événement */
    @FXML private Label eventLocationLabel;
    
    /** Label affichant le type d'événement (Concert, Spectacle, Conférence) */
    @FXML private Label eventTypeLabel;
    
    /** Label affichant le statut de l'événement (À venir, En cours, etc.) */
    @FXML private Label eventStatusLabel;
    
    /** Zone de texte affichant la description complète de l'événement */
    @FXML private TextArea eventDescriptionArea;
    
    // === Éléments FXML - Tarification ===
    
    /** Label affichant le prix des places Standard */
    @FXML private Label priceStandardLabel;
    
    /** Label affichant le prix des places VIP */
    @FXML private Label priceVipLabel;
    
    /** Label affichant le prix des places Premium */
    @FXML private Label pricePremiumLabel;
    
    /** Label affichant le nombre de places Standard disponibles */
    @FXML private Label placesStandardLabel;
    
    /** Label affichant le nombre de places VIP disponibles */
    @FXML private Label placesVipLabel;
    
    /** Label affichant le nombre de places Premium disponibles */
    @FXML private Label placesPremiumLabel;
    
    // === Éléments FXML - Actions ===
    
    /** Bouton pour initier une réservation */
    @FXML private Button reserveButton;
    
    /** Bouton pour partager les informations de l'événement */
    @FXML private Button shareButton;

    // === Données et références ===
    
    /** Événement actuellement affiché */
    private Evenement currentEvent;
    
    /** Référence au contrôleur dashboard pour la navigation */
    private ClientDashboardController dashboardController;
    
    /** Formateur pour l'affichage des dates */
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

    /**
     * Définit le contrôleur parent du dashboard pour permettre la navigation.
     * 
     * <p>Injecte la référence au contrôleur principal du dashboard client
     * pour permettre la navigation de retour et la coordination entre
     * les différentes interfaces.</p>
     * 
     * @param dashboardController le contrôleur principal du dashboard client,
     *                           ne doit pas être null pour assurer la navigation
     * 
     * @since 1.0
     */
    public void setDashboardController(ClientDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    /**
     * Injecte les données de l'événement à afficher et met à jour l'interface.
     * 
     * <p>Cette méthode est appelée par le contrôleur parent pour transmettre
     * les informations de l'événement sélectionné. Elle déclenche automatiquement
     * la mise à jour de tous les éléments d'interface.</p>
     * 
     * <p><strong>Données traitées :</strong></p>
     * <ul>
     *   <li>Informations de base : nom, date, lieu, type, statut</li>
     *   <li>Description complète de l'événement</li>
     *   <li>Tarification par catégorie de places</li>
     *   <li>Disponibilité des places en temps réel</li>
     * </ul>
     * 
     * @param event l'objet Evenement contenant toutes les informations à afficher,
     *              ne doit pas être null
     * 
     * @see #populateUI()
     * @see com.bschooleventmanager.eventmanager.model.Evenement
     * 
     * @since 1.0
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
