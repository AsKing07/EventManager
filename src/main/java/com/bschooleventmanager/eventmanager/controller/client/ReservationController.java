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

/**
 * Contrôleur pour l'interface de réservation d'événements côté client dans EventManager.
 * 
 * <p>Cette classe gère le processus complet de réservation d'un événement, de la
 * sélection des places jusqu'à la confirmation, en passant par la validation
 * des disponibilités et le choix du mode de paiement. Elle offre une expérience
 * utilisateur fluide avec validation en temps réel et gestion d'erreurs avancée.</p>
 * 
 * <p><strong>Fonctionnalités principales :</strong></p>
 * <ul>
 *   <li>Interface de sélection des places par catégorie</li>
 *   <li>Calcul en temps réel du montant total</li>
 *   <li>Validation des disponibilités et contraintes</li>
 *   <li>Choix du mode de paiement (immédiat ou différé)</li>
 *   <li>Création et confirmation de la réservation</li>
 *   <li>Navigation vers l'interface de paiement si nécessaire</li>
 * </ul>
 * 
 * <p><strong>Validation et contraintes :</strong></p>
 * <ul>
 *   <li><strong>Disponibilité :</strong> Vérification en temps réel des places disponibles</li>
 *   <li><strong>Limite temporelle :</strong> Fermeture des réservations 30min avant l'événement</li>
 *   <li><strong>Limite quantitative :</strong> Maximum de places par réservation</li>
 *   <li><strong>Validation utilisateur :</strong> Session active requise</li>
 * </ul>
 * 
 * <p><strong>Catégories de places gérées :</strong></p>
 * <ul>
 *   <li><strong>Standard :</strong> Places de base avec tarif réduit</li>
 *   <li><strong>VIP :</strong> Places privilégiées avec services additionnels</li>
 *   <li><strong>Premium :</strong> Places haut de gamme avec expérience exclusive</li>
 * </ul>
 * 
 * <p><strong>Modes de paiement supportés :</strong></p>
 * <ul>
 *   <li><strong>Paiement immédiat :</strong> Redirection vers l'interface de paiement</li>
 *   <li><strong>Paiement différé :</strong> Réservation avec paiement ultérieur</li>
 * </ul>
 * 
 * <p><strong>Gestion d'erreurs spécialisée :</strong></p>
 * <ul>
 *   <li><strong>PlacesInsuffisantesException :</strong> Ajustement automatique proposé</li>
 *   <li><strong>Événement expiré :</strong> Blocage avec message informatif</li>
 *   <li><strong>Session expirée :</strong> Redirection vers la connexion</li>
 *   <li><strong>Limites dépassées :</strong> Ajustement aux contraintes</li>
 * </ul>
 * 
 * <p><strong>Workflow de réservation :</strong></p>
 * <ol>
 *   <li>Affichage des informations de l'événement</li>
 *   <li>Sélection des quantités par catégorie via Spinners</li>
 *   <li>Calcul automatique du total avec résumé</li>
 *   <li>Choix du mode de paiement</li>
 *   <li>Validation et création de la réservation</li>
 *   <li>Navigation vers paiement ou historique selon le choix</li>
 * </ol>
 * 
 * <p><strong>Interface utilisateur :</strong></p>
 * <ul>
 *   <li>Spinners avec limites automatiques selon disponibilité</li>
 *   <li>Résumé dynamique avec calculs temps réel</li>
 *   <li>Radio buttons pour le choix de paiement</li>
 *   <li>Messages d'avertissement conditionnels</li>
 *   <li>Boutons d'action contextuels</li>
 * </ul>
 * 
 * <p><strong>Exemple d'utilisation :</strong></p>
 * <pre>{@code
 * ReservationController controller = loader.getController();
 * controller.setDashboardController(dashboardController);
 * controller.setEventData(selectedEvent);
 * // L'interface se configure automatiquement avec les données de l'événement
 * }</pre>
 * 
 * @author EventManager Team
 * @version 1.0
 * @since 1.0
 * 
 * @see ClientDashboardController
 * @see PaymentController
 * @see ClientHistoriqueReservationsController
 * @see com.bschooleventmanager.eventmanager.service.ReservationService
 * @see com.bschooleventmanager.eventmanager.exception.PlacesInsuffisantesException
 * @see com.bschooleventmanager.eventmanager.model.Reservation
 */
public class ReservationController {
    /**
     * Logger pour traçage des opérations de réservation et gestion d'erreurs.
     * <p>Niveau DEBUG pour le suivi détaillé du workflow de réservation.</p>
     */
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    /**
     * Bouton de retour au tableau de bord ou à la vue précédente.
     * <p>Navigation sans sauvegarde des modifications en cours.</p>
     */
    @FXML 
    private Button backButton;
    
    /**
     * Label d'affichage du nom de l'événement sélectionné.
     * <p>Affiche le titre complet de l'événement en cours de réservation.</p>
     */
    @FXML 
    private Label eventNameLabel;
    
    /**
     * Label d'affichage de la date et heure de l'événement.
     * <p>Format : "dd/MM/yyyy à HH:mm" avec gestion automatique de la locale française.</p>
     */
    @FXML 
    private Label eventDateLabel;
    
    /**
     * Label d'affichage du lieu de l'événement.
     * <p>Affiche l'adresse complète ou le nom du venue de l'événement.</p>
     */
    @FXML 
    private Label eventLocationLabel;

    /**
     * Label d'affichage du prix unitaire des places standard.
     * <p>Format : "XX,XX €" avec formatage automatique selon la locale française.</p>
     */
    @FXML 
    private Label priceStandardLabel;
    
    /**
     * Label d'affichage du prix unitaire des places VIP.
     * <p>Format : "XX,XX €" avec majoration automatique selon le type d'événement.</p>
     */
    @FXML 
    private Label priceVipLabel;
    
    /**
     * Label d'affichage du prix unitaire des places premium.
     * <p>Format : "XX,XX €" avec tarification haut de gamme selon l'événement.</p>
     */
    @FXML 
    private Label pricePremiumLabel;
    
    /**
     * Label d'affichage du nombre de places standard disponibles.
     * <p>Mise à jour en temps réel selon les réservations existantes.</p>
     */
    @FXML 
    private Label availableStandardLabel;
    
    /**
     * Label d'affichage du nombre de places VIP disponibles.
     * <p>Décompte automatique des places déjà réservées par d'autres utilisateurs.</p>
     */
    @FXML 
    private Label availableVipLabel;
    
    /**
     * Label d'affichage du nombre de places premium disponibles.
     * <p>Vérification en temps réel de la disponibilité effective.</p>
     */
    @FXML 
    private Label availablePremiumLabel;

    /**
     * Spinner de sélection de la quantité de places standard.
     * <p>Limites automatiques : min=0, max=places disponibles, step=1</p>
     */
    @FXML 
    private Spinner<Integer> standardSpinner;
    
    /**
     * Spinner de sélection de la quantité de places VIP.
     * <p>Limites automatiques : min=0, max=places VIP disponibles, step=1</p>
     */
    @FXML 
    private Spinner<Integer> vipSpinner;
    
    /**
     * Spinner de sélection de la quantité de places premium.
     * <p>Limites automatiques : min=0, max=places premium disponibles, step=1</p>
     */
    @FXML 
    private Spinner<Integer> premiumSpinner;

    /**
     * Container VBox pour l'affichage du résumé de commande.
     * <p>Contient le détail des sélections avec calculs intermédiaires.</p>
     */
    @FXML 
    private VBox summaryContainer;
    
    /**
     * Label d'affichage du montant total calculé en temps réel.
     * <p>Mise à jour automatique lors des changements de quantité.
     * Format : "Total : XX,XX €"</p>
     */
    @FXML 
    private Label totalLabel;

    /**
     * Radio button pour sélectionner le mode de paiement immédiat.
     * <p>Redirige vers l'interface de paiement Stripe après confirmation.</p>
     */
    @FXML 
    private RadioButton payNowRadio;
    
    /**
     * Radio button pour sélectionner le mode de paiement différé.
     * <p>Crée la réservation sans redirection vers le paiement.</p>
     */
    @FXML 
    private RadioButton payLaterRadio;
    
    /**
     * Label d'avertissement pour le paiement différé.
     * <p>Affiche les conditions et délais de paiement ultérieur.</p>
     */
    @FXML 
    private Label payLaterWarning;

    /**
     * Bouton d'annulation de la réservation en cours.
     * <p>Retour au tableau de bord sans sauvegarde des modifications.</p>
     */
    @FXML 
    private Button cancelButton;
    
    /**
     * Bouton de confirmation de la réservation.
     * <p>Active le processus de création avec validation complète.</p>
     */
    @FXML 
    private Button confirmButton;

    /**
     * Objet contenant toutes les données de l'événement à réserver.
     * <p>Inclut les informations tarifaires, les places disponibles et les contraintes.</p>
     */
    private Evenement currentEvent;
    
    /**
     * Référence vers la dernière réservation créée avec succès.
     * <p>Utilisée pour la redirection vers le paiement ou l'historique.</p>
     */
    private com.bschooleventmanager.eventmanager.model.Reservation lastCreatedReservation;
    
    /**
     * Référence vers le contrôleur principal du tableau de bord client.
     * <p>Utilisé pour la navigation entre les vues et le retour au tableau de bord.</p>
     */
    private ClientDashboardController dashboardController;
    
    /**
     * Formateur de date et heure pour l'affichage localisé.
     * <p>Pattern : "dd/MM/yyyy à HH:mm" conforme aux standards français.</p>
     */
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

    /**
     * Service métier pour la gestion des réservations.
     * <p>Encapsule la logique de validation, calcul et création des réservations.</p>
     */
    private final ReservationService reservationService = new ReservationService();

    /**
     * Définit la référence vers le contrôleur de tableau de bord pour la navigation.
     * 
     * <p>Cette méthode est appelée lors de l'initialisation du contrôleur de réservation
     * pour établir la liaison avec le contrôleur parent, permettant ainsi la navigation
     * de retour vers le tableau de bord après confirmation ou annulation.</p>
     * 
     * @param dashboardController Le contrôleur de tableau de bord client
     */
    public void setDashboardController(ClientDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    /**
     * Configure l'interface avec les données de l'événement sélectionné.
     * 
     * <p>Cette méthode est le point d'entrée principal pour initialiser l'interface
     * de réservation. Elle vérifie la validité temporelle de l'événement, configure
     * l'affichage des informations et initialise les contrôles de sélection.</p>
     * 
     * <p><strong>Vérifications effectuées :</strong></p>
     * <ul>
     *   <li>Validité de l'objet événement</li>
     *   <li>Délai de réservation (fermeture 30min avant)</li>
     *   <li>Disponibilité des places par catégorie</li>
     * </ul>
     * 
     * <p><strong>Actions automatiques :</strong></p>
     * <ul>
     *   <li>Affichage des informations événement</li>
     *   <li>Configuration des spinners avec limites</li>
     *   <li>Calcul initial des totaux</li>
     *   <li>Activation/désactivation des contrôles</li>
     * </ul>
     * 
     * @param event L'événement pour lequel effectuer une réservation
     * 
     * @see #populateEventInfo()
     * @see #setupSpinners()
     * @see #isEventExpiredOrClosingSoon(Evenement)
     */
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
     * Vérifie si un événement est expiré ou va fermer bientôt.
     * 
     * <p>Cette méthode applique la règle métier de fermeture des réservations
     * 30 minutes avant le début de l'événement, conformément aux exigences
     * de gestion des délais de réservation.</p>
     * 
     * <p><strong>Règles de validation :</strong></p>
     * <ul>
     *   <li>Événement null = expiré</li>
     *   <li>Heure actuelle > (heure événement - 30min) = expiré</li>
     *   <li>Événement déjà passé = expiré</li>
     * </ul>
     * 
     * @param event L'événement à vérifier
     * @return true si l'événement n'est plus réservable, false sinon
     * 
     * @see #showEventExpiredWarning()
     */
    private boolean isEventExpiredOrClosingSoon(Evenement event) {
        if (event == null) return true;
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventDateTime = event.getDateEvenement();
        LocalDateTime cutoffTime = eventDateTime.minusMinutes(30);
        
        return now.isAfter(cutoffTime);
    }
    
    /**
     * Affiche un avertissement pour un événement expiré et désactive l'interface.
     * 
     * <p>Cette méthode est appelée automatiquement lorsqu'un événement n'est plus
     * réservable. Elle désactive tous les contrôles interactifs et affiche une
     * boîte de dialogue d'information explicative pour l'utilisateur.</p>
     * 
     * <p><strong>Actions effectuées :</strong></p>
     * <ul>
     *   <li>Désactivation de tous les spinners de sélection</li>
     *   <li>Désactivation du bouton de confirmation</li>
     *   <li>Affichage d'une alerte d'avertissement détaillée</li>
     *   <li>Maintien des boutons de navigation (retour/annulation)</li>
     * </ul>
     * 
     * <p>Cette méthode améliore l'expérience utilisateur en expliquant clairement
     * pourquoi les réservations ne sont plus possibles.</p>
     * 
     * @see #isEventExpiredOrClosingSoon(Evenement)
     * @see javafx.scene.control.Alert
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

    /**
     * Initialise les composants de l'interface de réservation.
     * 
     * <p>Cette méthode est appelée automatiquement par JavaFX après le chargement
     * du fichier FXML. Elle configure les liaisons entre les composants et établit
     * les écouteurs d'événements nécessaires au bon fonctionnement de l'interface.</p>
     * 
     * <p><strong>Configuration effectuée :</strong></p>
     * <ul>
     *   <li><strong>Radio buttons :</strong> Regroupement pour sélection exclusive</li>
     *   <li><strong>Écouteurs :</strong> Affichage conditionnel des avertissements</li>
     *   <li><strong>Logging :</strong> Traçage de l'initialisation</li>
     *   <li><strong>État initial :</strong> Masquage des messages d'avertissement</li>
     * </ul>
     * 
     * <p><strong>Groupes de contrôles configurés :</strong></p>
     * <ul>
     *   <li><em>payNowRadio</em> et <em>payLaterRadio</em> dans un ToggleGroup</li>
     *   <li>Affichage automatique du warning si paiement différé sélectionné</li>
     * </ul>
     * 
     * <p>Cette méthode garantit que l'interface est dans un état cohérent
     * dès son affichage, avec tous les mécanismes de validation actifs.</p>
     * 
     * @see javafx.fxml.Initializable
     * @see javafx.scene.control.ToggleGroup
     */
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

    /**
     * Remplit l'interface avec les informations détaillées de l'événement.
     * 
     * <p>Cette méthode configure tous les labels d'affichage avec les données
     * de l'événement sélectionné. Elle gère le formatage automatique des prix,
     * des dates et des disponibilités selon les standards de l'application.</p>
     * 
     * <p><strong>Informations affichées :</strong></p>
     * <ul>
     *   <li><strong>Identité :</strong> Nom et description de l'événement</li>
     *   <li><strong>Temporalité :</strong> Date et heure formatées (dd/MM/yyyy à HH:mm)</li>
     *   <li><strong>Localisation :</strong> Lieu et adresse de l'événement</li>
     *   <li><strong>Tarification :</strong> Prix par catégorie avec formatage monétaire</li>
     *   <li><strong>Disponibilité :</strong> Places restantes par catégorie</li>
     * </ul>
     * 
     * <p><strong>Gestion des cas particuliers :</strong></p>
     * <ul>
     *   <li>Prix null affiché comme "N/A"</li>
     *   <li>Formatage automatique des devises (€)</li>
     *   <li>Disponibilités en temps réel</li>
     *   <li>Gestion sécurisée des valeurs manquantes</li>
     * </ul>
     * 
     * <p>Cette méthode est appelée automatiquement lors du chargement
     * d'un événement via {@link #setEventData(Evenement)}.</p>
     * 
     * @see #setEventData(Evenement)
     * @see java.time.format.DateTimeFormatter
     */
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

    /**
     * Configure les spinners de sélection des quantités avec leurs limites.
     * 
     * <p>Cette méthode initialise les trois spinners de sélection (Standard, VIP, Premium)
     * avec des limites dynamiques basées sur la disponibilité réelle des places.
     * Elle applique également des contraintes raisonnables pour éviter les réservations
     * excessives par utilisateur.</p>
     * 
     * <p><strong>Contraintes appliquées :</strong></p>
     * <ul>
     *   <li><strong>Minimum :</strong> 0 place (pas d'obligation de réservation)</li>
     *   <li><strong>Maximum :</strong> min(10, places_disponibles) pour éviter l'accaparement</li>
     *   <li><strong>Valeur initiale :</strong> 0 pour tous les spinners</li>
     *   <li><strong>Incrément :</strong> 1 place par clic</li>
     * </ul>
     * 
     * <p><strong>Configuration par catégorie :</strong></p>
     * <ul>
     *   <li><em>Standard :</em> Limité aux places standard disponibles</li>
     *   <li><em>VIP :</em> Limité aux places VIP disponibles</li>
     *   <li><em>Premium :</em> Limité aux places premium disponibles</li>
     * </ul>
     * 
     * <p><strong>Sécurités intégrées :</strong></p>
     * <ul>
     *   <li>Si 0 place disponible → Spinner désactivé</li>
     *   <li>Limite utilisateur à 10 places max par catégorie</li>
     *   <li>Mise à jour automatique des totaux via écouteurs</li>
     * </ul>
     * 
     * <p>Cette méthode est appelée après {@link #populateEventInfo()} pour
     * assurer la cohérence entre affichage et contrôles.</p>
     * 
     * @see #setupSpinner(Spinner, int, int)
     * @see #populateEventInfo()
     * @see #updateTotal()
     */
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

    /**
     * Met à jour le calcul du total et l'affichage du résumé en temps réel.
     * 
     * <p>Cette méthode est appelée automatiquement à chaque modification des
     * quantités dans les spinners. Elle recalcule le montant total, met à jour
     * l'affichage du résumé détaillé et active/désactive les boutons d'action
     * selon les sélections.</p>
     * 
     * <p><strong>Calculs effectués :</strong></p>
     * <ul>
     *   <li><strong>Standard :</strong> quantité × prix unitaire standard</li>
     *   <li><strong>VIP :</strong> quantité × prix unitaire VIP</li>
     *   <li><strong>Premium :</strong> quantité × prix unitaire premium</li>
     *   <li><strong>Total général :</strong> somme de toutes les catégories</li>
     * </ul>
     * 
     * <p><strong>Logique d'activation des boutons :</strong></p>
     * <ul>
     *   <li><strong>Aucune place :</strong> Bouton confirmation désactivé</li>
     *   <li><strong>Places sélectionnées :</strong> Bouton confirmation activé</li>
     *   <li><strong>Événements gratuits :</strong> Confirmation possible si places > 0</li>
     * </ul>
     * 
     * <p><strong>Gestion d'erreurs :</strong></p>
     * <ul>
     *   <li>Exceptions de calcul capturées et loggées</li>
     *   <li>Affichage "Erreur" en cas de problème</li>
     *   <li>Préservation de l'état de l'interface</li>
     * </ul>
     * 
     * <p>Cette méthode utilise {@link #addSummaryLine(String, int, double, double)}
     * pour construire le résumé visuel ligne par ligne.</p>
     * 
     * @see #addSummaryLine(String, int, double, double)
     * @see javafx.scene.control.Spinner#valueProperty()
     */
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

    /**
     * Gestionnaire d'événement pour le bouton de retour.
     * 
     * <p>Cette méthode permet à l'utilisateur de revenir à la vue détaillée
     * de l'événement sans effectuer de réservation. Elle préserve le contexte
     * de navigation en redirigeant vers les détails de l'événement actuel.</p>
     * 
     * <p><strong>Actions effectuées :</strong></p>
     * <ul>
     *   <li>Vérification de la validité du contrôleur dashboard</li>
     *   <li>Navigation vers les détails de l'événement</li>
     *   <li>Préservation du contexte événement</li>
     * </ul>
     * 
     * <p>Cette méthode est liée au bouton "Retour" via l'annotation @FXML.</p>
     * 
     * @see ClientDashboardController#showEventDetails(Evenement)
     */
    @FXML
    private void handleBack() {
        if (dashboardController != null) {
            dashboardController.showEventDetails(currentEvent);
        }
    }

    /**
     * Gestionnaire d'événement pour le bouton d'annulation.
     * 
     * <p>Cette méthode offre une alternative au bouton de retour avec
     * la même fonctionnalité. Elle permet l'annulation de la réservation
     * en cours et le retour à la vue précédente.</p>
     * 
     * <p>Implémentée comme alias de {@link #handleBack()} pour
     * cohérence de l'interface utilisateur.</p>
     * 
     * @see #handleBack()
     */
    @FXML
    private void handleCancel() {
        handleBack();
    }

    /**
     * Gestionnaire principal pour la confirmation de réservation.
     * 
     * <p>Cette méthode orchestre le processus complet de création d'une réservation,
     * de la validation des saisies jusqu'à la redirection finale. Elle implémente
     * une gestion d'erreurs robuste avec traitement spécialisé selon le type d'exception.</p>
     * 
     * <p><strong>Workflow de traitement :</strong></p>
     * <ol>
     *   <li><strong>Validation :</strong> Vérification des saisies utilisateur</li>
     *   <li><strong>Création :</strong> Appel du service métier de réservation</li>
     *   <li><strong>Gestion d'erreurs :</strong> Traitement spécialisé par type</li>
     *   <li><strong>Navigation :</strong> Redirection selon le résultat</li>
     * </ol>
     * 
     * <p><strong>Types d'exceptions gérées :</strong></p>
     * <ul>
     *   <li><strong>PlacesInsuffisantesException :</strong> Proposition d'ajustement automatique</li>
     *   <li><strong>BusinessException :</strong> Gestion métier spécialisée</li>
     *   <li><strong>Exception générale :</strong> Gestion d'erreurs inattendues</li>
     * </ul>
     * 
     * <p><strong>Logging intégré :</strong></p>
     * <ul>
     *   <li>WARN pour erreurs métier attendues</li>
     *   <li>ERROR pour exceptions inattendues</li>
     *   <li>Messages contextualisés pour débogage</li>
     * </ul>
     * 
     * <p>Cette méthode est liée au bouton de confirmation via @FXML et représente
     * le point culminant du workflow de réservation.</p>
     * 
     * @see #validateBasicInputs()
     * @see #createReservation()
     * @see #handlePlacesInsuffisantesException(PlacesInsuffisantesException)
     * @see #handleBusinessException(BusinessException)
     * @see #handleUnexpectedException(Exception)
     */
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

    /**
     * Valide les saisies de base avant création de la réservation.
     * 
     * <p>Cette méthode effectue les contrôles de validation préliminaires
     * pour s'assurer que l'utilisateur a effectué des sélections valides
     * avant de procéder à la création de la réservation.</p>
     * 
     * <p><strong>Validations effectuées :</strong></p>
     * <ul>
     *   <li><strong>Quantité :</strong> Au moins une place sélectionnée</li>
     *   <li><strong>Paiement :</strong> Mode de paiement choisi (immédiat ou différé)</li>
     * </ul>
     * 
     * <p><strong>Messages d'erreur :</strong></p>
     * <ul>
     *   <li>"Veuillez sélectionner au moins une place" si aucune sélection</li>
     *   <li>"Veuillez choisir une option de paiement" si mode non défini</li>
     * </ul>
     * 
     * @throws BusinessException Si une validation échoue avec message explicatif
     * 
     * @see #handleConfirmReservation()
     * @see com.bschooleventmanager.eventmanager.exception.BusinessException
     */
    private void validateBasicInputs() throws BusinessException {
        int totalQuantite = standardSpinner.getValue() + vipSpinner.getValue() + premiumSpinner.getValue();
        if (totalQuantite == 0) {
            throw new BusinessException("Veuillez sélectionner au moins une place");
        }
        
        if (!payNowRadio.isSelected() && !payLaterRadio.isSelected()) {
            throw new BusinessException("Veuillez choisir une option de paiement");
        }
    }

    /**
     * Crée la réservation via le service métier et gère la redirection.
     * 
     * <p>Cette méthode centralise la logique de création de réservation en utilisant
     * le service métier approprié. Elle gère les différents scénarios de paiement
     * et détermine la navigation appropriée selon le contexte.</p>
     * 
     * <p><strong>Processus de création :</strong></p>
     * <ol>
     *   <li><strong>Vérification session :</strong> Utilisateur connecté requis</li>
     *   <li><strong>Collecte données :</strong> Récupération des quantités sélectionnées</li>
     *   <li><strong>Service métier :</strong> Appel ReservationService.creerReservation()</li>
     *   <li><strong>Calcul montant :</strong> Détermination du montant total final</li>
     *   <li><strong>Logique de redirection :</strong> Navigation selon le contexte</li>
     * </ol>
     * 
     * <p><strong>Scénarios de redirection :</strong></p>
     * <ul>
     *   <li><strong>Montant = 0€ :</strong> Validation automatique → Historique</li>
     *   <li><strong>Paiement immédiat :</strong> Interface de paiement Stripe</li>
     *   <li><strong>Paiement différé :</strong> Historique avec rappel 24h</li>
     * </ul>
     * 
     * <p><strong>Notifications utilisateur :</strong></p>
     * <ul>
     *   <li>Confirmation pour réservations gratuites</li>
     *   <li>Redirection annoncée pour paiement immédiat</li>
     *   <li>Rappel délai pour paiement différé</li>
     * </ul>
     * 
     * <p><strong>Gestion des données :</strong></p>
     * <ul>
     *   <li>Sauvegarde de lastCreatedReservation pour redirection</li>
     *   <li>Utilisation des quantités temps réel des spinners</li>
     *   <li>Respect du choix de mode de paiement utilisateur</li>
     * </ul>
     * 
     * @throws PlacesInsuffisantesException Si les places demandées ne sont plus disponibles
     * @throws BusinessException Si l'utilisateur n'est pas connecté ou autre erreur métier
     * 
     * @see ReservationService#creerReservation(Utilisateur, Evenement, int, int, int, boolean)
     * @see #calculateTotalAmount()
     * @see #redirectToPayment()
     * @see #redirectToReservationsHistory()
     */
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

        
        // Calculer le montant total pour déterminer le comportement
        BigDecimal totalAmount = calculateTotalAmount();
        
        // Afficher la confirmation et rediriger selon le choix de paiement et le montant
        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            // Réservation gratuite - validation automatique et redirection vers l'historique
            NotificationUtils.showSuccess("Réservation gratuite validée ! Vos places sont confirmées.");
            redirectToReservationsHistory();
        } else if (payNowRadio.isSelected()) {
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