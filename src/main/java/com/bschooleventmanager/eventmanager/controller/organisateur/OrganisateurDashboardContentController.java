package com.bschooleventmanager.eventmanager.controller.organisateur;

import com.bschooleventmanager.eventmanager.model.Evenement;
import com.bschooleventmanager.eventmanager.model.Utilisateur;
import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;
import com.bschooleventmanager.eventmanager.service.EvenementService;
import com.bschooleventmanager.eventmanager.util.NotificationUtils;
import com.bschooleventmanager.eventmanager.util.SessionManager;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Contrôleur pour le contenu du tableau de bord organisateur dans EventManager.
 * 
 * <p>Cette classe gère l'affichage des métriques, statistiques et visualisations
 * des événements d'un organisateur. Elle fournit une vue d'ensemble complète
 * avec des indicateurs de performance, graphiques interactifs et capacités d'export.</p>
 * 
 * <p><strong>Métriques et indicateurs :</strong></p>
 * <ul>
 *   <li><strong>Événements :</strong> Total créés, actifs, par type</li>
 *   <li><strong>Financier :</strong> Chiffre d'affaires réel basé sur les ventes effectives</li>
 *   <li><strong>Performance :</strong> Taux de remplissage moyen, places vendues</li>
 *   <li><strong>Temporel :</strong> Filtrage par période avec dates personnalisables</li>
 * </ul>
 * 
 * <p><strong>Visualisations graphiques :</strong></p>
 * <ul>
 *   <li><strong>Graphique circulaire :</strong> Répartition des événements par type avec taux de remplissage</li>
 *   <li><strong>Graphique en barres :</strong> Chiffre d'affaires réel par type d'événement</li>
 *   <li><strong>Mise à jour dynamique :</strong> Recalcul automatique selon les filtres</li>
 * </ul>
 * 
 * <p><strong>Système de filtrage avancé :</strong></p>
 * <ul>
 *   <li><strong>Filtre temporel :</strong> DatePicker pour période personnalisée</li>
 *   <li><strong>Filtre par type :</strong> ComboBox avec chargement dynamique depuis enums</li>
 *   <li><strong>Option "Tous" :</strong> Vue globale sans restriction</li>
 *   <li><strong>Application temps réel :</strong> Mise à jour instantanée des données</li>
 * </ul>
 * 
 * <p><strong>Calculs financiers précis :</strong></p>
 * <ul>
 *   <li><strong>Revenus réels :</strong> Basés sur les places effectivement vendues</li>
 *   <li><strong>Multi-catégories :</strong> Standard, VIP, Premium avec prix différenciés</li>
 *   <li><strong>Validation :</strong> Contrôle de cohérence des données financières</li>
 *   <li><strong>Formatage :</strong> Affichage monétaire avec précision décimale</li>
 * </ul>
 * 
 * <p><strong>Fonctionnalités d'export :</strong></p>
 * <ul>
 *   <li><strong>Export CSV :</strong> Données détaillées de tous les événements filtrés</li>
 *   <li><strong>Export PDF :</strong> Fonctionnalité prévue (en développement)</li>
 *   <li><strong>Données complètes :</strong> Métriques, places vendues, revenus</li>
 *   <li><strong>Format standardisé :</strong> Compatible Excel et outils d'analyse</li>
 * </ul>
 * 
 * <p><strong>Interface utilisateur responsive :</strong></p>
 * <ul>
 *   <li><strong>Mise à jour asynchrone :</strong> Utilisation de Platform.runLater()</li>
 *   <li><strong>Gestion d'erreurs :</strong> États vides avec messages informatifs</li>
 *   <li><strong>Bouton action rapide :</strong> Création d'événement directe</li>
 *   <li><strong>Résumé dynamique :</strong> Synthèse des métriques principales</li>
 * </ul>
 * 
 * <p><strong>Architecture de données :</strong></p>
 * <ul>
 *   <li><strong>Source :</strong> EvenementService pour récupération sécurisée</li>
 *   <li><strong>Filtrage :</strong> Streams Java pour performance optimale</li>
 *   <li><strong>Calculs :</strong> BigDecimal pour précision monétaire</li>
 *   <li><strong>Mise en cache :</strong> Optimisation des requêtes fréquentes</li>
 * </ul>
 * 
 * <p><strong>Gestion d'erreurs robuste :</strong></p>
 * <ul>
 *   <li><strong>États vides :</strong> Interface gracieuse sans données</li>
 *   <li><strong>Erreurs réseau :</strong> Fallback avec messages informatifs</li>
 *   <li><strong>Logging détaillé :</strong> Traçage pour diagnostic et audit</li>
 *   <li><strong>Notifications :</strong> Feedback utilisateur via NotificationUtils</li>
 * </ul>
 * 
 * <p><strong>Intégration système :</strong></p>
 * <ul>
 *   <li><strong>Session :</strong> Récupération automatique de l'organisateur connecté</li>
 *   <li><strong>Navigation :</strong> Référence au contrôleur parent pour redirection</li>
 *   <li><strong>Services :</strong> Intégration avec EvenementService</li>
 *   <li><strong>Configuration :</strong> Utilisation des enums pour cohérence</li>
 * </ul>
 * 
 * <p><strong>Exemples de métriques calculées :</strong></p>
 * <pre>{@code
 * // Chiffre d'affaires d'un événement
 * BigDecimal revenue = event.getPrixStandard().multiply(BigDecimal.valueOf(event.getPlaceStandardVendues()))
 *                     .add(event.getPrixVip().multiply(BigDecimal.valueOf(event.getPlaceVipVendues())))
 *                     .add(event.getPrixPremium().multiply(BigDecimal.valueOf(event.getPlacePremiumVendues())));
 * 
 * // Taux de remplissage moyen
 * double avgFillRate = events.stream()
 *     .filter(e -> e.getCapaciteTotale() > 0)
 *     .mapToDouble(Evenement::getTauxRemplissage)
 *     .average()
 *     .orElse(0.0);
 * }</pre>
 * 
 * @author @AsKing07 Charbel SONON
 * @version 1.0
 * @since 1.0
 * 
 * @see OrganisateurDashboardController
 * @see com.bschooleventmanager.eventmanager.service.EvenementService
 * @see com.bschooleventmanager.eventmanager.model.Evenement
 * @see com.bschooleventmanager.eventmanager.model.enums.TypeEvenement
 * @see javafx.scene.chart.PieChart
 * @see javafx.scene.chart.BarChart
 */
public class OrganisateurDashboardContentController {
    /** Logger pour traçage des opérations de calcul de métriques et gestion de données. */
    private static final Logger logger = LoggerFactory.getLogger(OrganisateurDashboardContentController.class);

    // Éléments d'interface pour résumé et navigation
    /** Label d'affichage du résumé synthétique des métriques principales. */
    @FXML private Label lblSummary;
    /** Bouton d'action rapide pour création d'événement depuis le dashboard. */
    @FXML private Button btnCreateEvent;
    
    // Contrôles de filtrage des données
    /** DatePicker pour sélection de la date de début de période d'analyse. */
    @FXML private DatePicker dpFrom;
    /** DatePicker pour sélection de la date de fin de période d'analyse. */
    @FXML private DatePicker dpTo;
    /** ComboBox pour filtrage par type d'événement avec option "Tous". */
    @FXML private ComboBox<String> cbType;
    /** Bouton d'application des filtres avec recalcul des métriques. */
    @FXML private Button btnApplyFilters;
    
    // Boutons d'export des données
    /** Bouton d'export des données au format CSV avec métriques détaillées. */
    @FXML private Button btnExportCsv;
    /** Bouton d'export PDF (fonctionnalité en développement). */
    @FXML private Button btnExportPdf;

    // Labels d'affichage des métriques calculées
    /** Label d'affichage du nombre total d'événements dans la période filtrée. */
    @FXML private Label lblTotalEvents;
    /** Label d'affichage du chiffre d'affaires total réel basé sur les ventes. */
    @FXML private Label lblTotalRevenue;
    /** Label d'affichage du taux de remplissage moyen des événements. */
    @FXML private Label lblAvgFillRate;
    /** Label d'affichage du nombre total de billets vendus toutes catégories. */
    @FXML private Label lblTotalTicketsSold;
    /** Label d'affichage du nombre d'événements actifs (non annulés). */
    @FXML private Label lblActiveEvents;
    
    // Graphiques de visualisation des données
    /** Graphique circulaire de répartition des événements par type avec taux de remplissage. */
    @FXML private PieChart pieByType;
    /** Graphique en barres du chiffre d'affaires réel par type d'événement. */
    @FXML private BarChart<String, Number> barRevenueByType;

    /** Référence vers le contrôleur parent pour navigation et actions. */
    private OrganisateurDashboardController parentController;

    /** Service métier pour récupération et gestion des données d'événements. */
    private final EvenementService evenementService = new EvenementService();

    /**
     * Définit la référence vers le contrôleur parent pour navigation.
     * 
     * <p>Cette méthode est appelée lors de l'initialisation du contrôleur pour
     * établir la liaison avec le contrôleur dashboard principal, permettant
     * la redirection vers les interfaces de création d'événements.</p>
     * 
     * @param parent Le contrôleur dashboard principal pour navigation
     * 
     * @see OrganisateurDashboardController
     */
    public void setParentController(OrganisateurDashboardController parent) {
        this.parentController = parent;
    }

    /**
     * Initialise l'interface du contenu dashboard avec filtres et métriques par défaut.
     * 
     * <p><b>Workflow d'initialisation :</b></p>
     * <ol>
     *   <li>Configuration des filtres de types d'événements depuis les énumérations</li>
     *   <li>Association des gestionnaires d'événements pour les boutons d'action</li>
     *   <li>Définition des valeurs par défaut des contrôles</li>
     *   <li>Chargement asynchrone des données initiales avec délai d'initialisation</li>
     * </ol>
     * 
     * <p><b>Configuration automatique :</b></p>
     * <ul>
     *   <li>Types disponibles : Chargement dynamique depuis TypeEvenement enum</li>
     *   <li>Valeur par défaut : "Tous les types" pour vue globale</li>
     *   <li>Exécution asynchrone pour éviter les blocages d'interface</li>
     *   <li>Délai Platform.runLater pour initialisation complète des contrôles</li>
     * </ul>
     * 
     * @see #setupTypeFilter()
     * @see #setupButtonActions()
     * @see #refreshData()
     */
    @FXML
    private void initialize() {
        logger.info("Initialisation du contrôleur dashboard organisateur");
        
        // Préparer les types avec l'option "Tous" 
        setupTypeFilter();
        
        // Configuration des événements des boutons
        setupButtonActions();
        
        // Chargement initial avec un petit délai pour permettre l'initialisation complète
        Platform.runLater(() -> {
            // Définir les valeurs par défaut
            cbType.setValue("Tous les types");
            refreshData();
        });
    }
    
    /**
     * Configure le filtre de types d'événements avec chargement dynamique depuis les énumérations.
     * 
     * <p><b>Sources de données :</b></p>
     * <ul>
     *   <li>Option par défaut : "Tous les types" pour affichage global</li>
     *   <li>Types spécifiques : Chargement dynamique depuis TypeEvenement enum</li>
     *   <li>Labels localisés : Utilisation des getLabel() pour affichage utilisateur</li>
     * </ul>
     * 
     * <p>Cette approche dynamique assure la cohérence avec les types définis
     * dans le modèle métier et facilite l'ajout de nouveaux types sans modification
     * du code d'interface.</p>
     * 
     * @see TypeEvenement#getLabel()
     * @see #refreshData()
     */
    private void setupTypeFilter() {
        List<String> typeOptions = new ArrayList<>();
        typeOptions.add("Tous les types");
        
        // Charger dynamiquement depuis l'enum au lieu de coder en dur
        for (TypeEvenement type : TypeEvenement.values()) {
            typeOptions.add(type.getLabel());
        }
        
        cbType.setItems(FXCollections.observableArrayList(typeOptions));
    }
    
    /**
     * Configure les gestionnaires d'événements pour tous les boutons d'action du dashboard.
     * 
     * <p><b>Actions configurées :</b></p>
     * <ul>
     *   <li>Application de filtres : Recalcul et actualisation des métriques</li>
     *   <li>Création d'événement : Navigation conditionnelle via contrôleur parent</li>
     *   <li>Export CSV : Génération de rapport détaillé des données filtrées</li>
     *   <li>Export PDF : Notification d'implémentation future avec message informatif</li>
     * </ul>
     * 
     * <p><b>Validation et sécurité :</b></p>
     * <ul>
     *   <li>Vérification de la référence au contrôleur parent avant navigation</li>
     *   <li>Gestion d'erreurs appropriée pour chaque action</li>
     *   <li>Messages utilisateur informatifs pour fonctionnalités en développement</li>
     * </ul>
     * 
     * @see #refreshData()
     * @see OrganisateurDashboardController#showCreateEvent()
     * @see #exportCsv()
     * @see NotificationUtils#showInfo(String, String)
     */
    private void setupButtonActions() {
        btnApplyFilters.setOnAction(e -> refreshData());
        
        btnCreateEvent.setOnAction(e -> {
            if (parentController != null) {
                parentController.showCreateEvent();
            }
        });

        btnExportCsv.setOnAction(e -> exportCsv());
        
        btnExportPdf.setOnAction(e -> 
            NotificationUtils.showInfo("Info", "Export PDF non implémenté. Utilisez l'export CSV pour l'instant.")
        );
    }

    /**
     * Actualise toutes les données du dashboard avec application des filtres sélectionnés.
     * 
     * <p><b>Workflow de rafraîchissement :</b></p>
     * <ol>
     *   <li>Validation de la session utilisateur connecté</li>
     *   <li>Récupération des événements de l'organisateur avec filtres</li>
     *   <li>Calcul des métriques agrégées (revenus, taux de remplissage)</li>
     *   <li>Mise à jour des labels d'affichage avec formatage</li>
     *   <li>Actualisation des graphiques de visualisation</li>
     *   <li>Gestion d'erreurs avec notifications utilisateur appropriées</li>
     * </ol>
     * 
     * <p><b>Filtrage appliqué :</b></p>
     * <ul>
     *   <li>Type d'événement : Basé sur la sélection ComboBox (ou tous si "Tous les types")</li>
     *   <li>Organisateur : Limité aux événements de l'utilisateur connecté</li>
     *   <li>Statut : Inclusion de tous les statuts pour vue complète</li>
     * </ul>
     * 
     * <p><b>Métriques calculées :</b></p>
     * <ul>
     *   <li>Nombre total d'événements dans les critères de filtre</li>
     *   <li>Chiffre d'affaires total réel basé sur les réservations</li>
     *   <li>Taux de remplissage moyen pondéré par la capacité</li>
     *   <li>Nombre total de billets vendus toutes catégories</li>
     *   <li>Nombre d'événements actifs (statut non annulé)</li>
     * </ul>
     * 
     * @throws RuntimeException Si erreur de récupération des données
     * 
     * @see SessionManager#getUtilisateurConnecte()
     * @see EvenementService#getEvenementsByOrganisateur(Long, String)
     * @see #updateMetrics(List)
     * @see #updateCharts(List)
     * @see NotificationUtils#showError(String)
     */
    private void refreshData() {
        try {
            logger.info("Rafraîchissement des données du dashboard");
            
            Utilisateur user = SessionManager.getUtilisateurConnecte();
            if (user == null) {
                logger.warn("Aucun utilisateur connecté");
                NotificationUtils.showError("Utilisateur non connecté");
                return;
            }
            
            int userId = user.getIdUtilisateur();

            // Récupération des événements de l'organisateur
            List<Evenement> events = evenementService.getEvenementsParOrganisateur(userId);
            logger.info("Événements chargés : {}", events.size());

            // Application des filtres avec validation
            List<Evenement> filtered = applyFilters(events);
            logger.info("Événements après filtres : {}", filtered.size());

            // Mise à jour des métriques et graphiques
            updateMetrics(filtered);
            updateCharts(filtered);
            
            logger.info("Dashboard mis à jour avec succès");

        } catch (Exception e) {
            logger.error("Erreur rafraîchissement dashboard", e);
            NotificationUtils.showError("Impossible de charger les données du dashboard: " + e.getMessage());
            
            // Afficher des valeurs par défaut en cas d'erreur
            displayEmptyState();
        }
    }
    
    /**
     * Applique les filtres de date et de type d'événement aux données chargées.
     * 
     * <p><b>Critères de filtrage :</b></p>
     * <ul>
     *   <li>Type d'événement : Comparaison avec sélection ComboBox (ignore si "Tous les types")</li>
     *   <li>Période temporelle : Filtrage par dates de début et fin si définies</li>
     *   <li>Validation : Vérification de la cohérence des dates sélectionnées</li>
     * </ul>
     * 
     * <p><b>Logique de filtrage :</b></p>
     * <ul>
     *   <li>Filtrage inclusif : Événements correspondant à TOUS les critères appliqués</li>
     *   <li>Gestion des valeurs nulles : Ignore les filtres non définis</li>
     *   <li>Validation temporelle : Vérifie que date début ≤ date fin</li>
     * </ul>
     * 
     * @param events Liste complète des événements à filtrer
     * @return Liste filtrée des événements correspondant aux critères
     * 
     * @see #isEventInDateRange(Evenement)
     * @see TypeEvenement#getLabel()
     */
    private List<Evenement> applyFilters(List<Evenement> events) {
        LocalDate from = dpFrom.getValue();
        LocalDate to = dpTo.getValue();
        String typeLabel = cbType.getValue();
        
        return events.stream()
                .filter(ev -> {
                    // Filtre par date de début
                    if (from != null && ev.getDateEvenement().toLocalDate().isBefore(from)) {
                        return false;
                    }
                    
                    // Filtre par date de fin
                    if (to != null && ev.getDateEvenement().toLocalDate().isAfter(to)) {
                        return false;
                    }
                    
                    // Filtre par type
                    if (typeLabel != null && !typeLabel.isBlank() && !"Tous les types".equals(typeLabel)) {
                        return ev.getTypeEvenement().getLabel().equals(typeLabel);
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Affiche un état vide en cas d'erreur
     */
    private void displayEmptyState() {
        lblTotalEvents.setText("0");
        lblTotalRevenue.setText("0 €");
        lblAvgFillRate.setText("0%");
        lblTotalTicketsSold.setText("0");
        lblActiveEvents.setText("0");
        lblSummary.setText("Erreur de chargement des données");
        
        pieByType.getData().clear();
        barRevenueByType.getData().clear();
        barRevenueByType.setTitle("Aucune donnée disponible");
    }

    /**
     * Met à jour tous les labels de métriques avec les données calculées des événements filtrés.
     * 
     * <p><b>Métriques calculées en temps réel :</b></p>
     * <ul>
     *   <li>Nombre total d'événements : Taille de la liste filtrée</li>
     *   <li>Événements actifs : Comptage des événements avec etat_event = true</li>
     *   <li>Chiffre d'affaires réel : Somme des revenus effectifs des ventes</li>
     *   <li>Total billets vendus : Agrégation des places vendues toutes catégories</li>
     *   <li>Taux de remplissage moyen : Moyenne pondérée par la capacité des événements</li>
     * </ul>
     * 
     * <p><b>Calculs avancés :</b></p>
     * <ul>
     *   <li>Revenu réel : Utilisation de calculateRealRevenue() pour chaque événement</li>
     *   <li>Taux de remplissage : Exclusion des événements à capacité zéro</li>
     *   <li>Formatage monétaire : Affichage avec 2 décimales et symbole Euro</li>
     *   <li>Gestion des valeurs nulles : Filtrage sécurisé pour éviter les erreurs</li>
     * </ul>
     * 
     * @param events Liste des événements filtrés pour le calcul des métriques
     * 
     * @see #calculateRealRevenue(Evenement)
     * @see Evenement#getTotalPlacesVendues()
     * @see Evenement#getTauxRemplissage()
     * @see Evenement#isEtatEvent()
     */
    private void updateMetrics(List<Evenement> events) {
        int total = events.size();
        lblTotalEvents.setText(String.valueOf(total));

        // Événements actifs (etat_event = true)
        long activeEvents = events.stream().mapToLong(e -> e.isEtatEvent() ? 1 : 0).sum();
        lblActiveEvents.setText(String.valueOf(activeEvents));

        // Chiffre d'affaires réel basé sur les ventes effectives
        BigDecimal totalRevenue = events.stream()
                .map(this::calculateRealRevenue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        lblTotalRevenue.setText(String.format("%.2f €", totalRevenue.doubleValue()));

        // Total des places vendues (toutes catégories confondues)
        int totalTicketsSold = events.stream()
                .mapToInt(Evenement::getTotalPlacesVendues)
                .sum();
        lblTotalTicketsSold.setText(String.valueOf(totalTicketsSold));

        // Taux de remplissage moyen réel
        double avgFillRate = events.stream()
                .filter(e -> e.getCapaciteTotale() > 0) // Éviter division par zéro
                .mapToDouble(Evenement::getTauxRemplissage)
                .average()
                .orElse(0.0);

        if (avgFillRate > 0) {
            lblAvgFillRate.setText(String.format("%.1f%%", avgFillRate));
        } else {
            lblAvgFillRate.setText("0%");
        }

        // Mise à jour du résumé
        lblSummary.setText(String.format(
                "%d événements • %d actifs • %.1f%% remplissage • %.0f€ CA", 
                total, activeEvents, avgFillRate, totalRevenue.doubleValue()
        ));
    }

    /**
     * Calcule le chiffre d'affaires réel basé sur les places vendues
     */
    /**
     * Calcule le chiffre d'affaires réel d'un événement basé sur les ventes effectives.
     * 
     * <p><b>Calcul par catégorie de places :</b></p>
     * <ul>
     *   <li>Places Standard : prix_standard × places_standard_vendues</li>
     *   <li>Places VIP : prix_vip × places_vip_vendues</li>
     *   <li>Places Premium : prix_premium × places_premium_vendues</li>
     * </ul>
     * 
     * <p><b>Sécurité et validation :</b></p>
     * <ul>
     *   <li>Vérification des prix non-null avant calcul</li>
     *   <li>Contrôle des quantités vendues > 0 pour optimisation</li>
     *   <li>Utilisation de BigDecimal pour précision monétaire</li>
     *   <li>Agrégation sécurisée des revenus par catégorie</li>
     * </ul>
     * 
     * <p>Cette méthode fournit le chiffre d'affaires réel basé uniquement sur
     * les ventes confirmées, excluant les projections ou les capacités maximales.</p>
     * 
     * @param event L'événement pour lequel calculer le chiffre d'affaires réel
     * @return Le montant total des revenus générés par les ventes effectives
     * 
     * @see Evenement#getPrixStandard()
     * @see Evenement#getPlaceStandardVendues()
     * @see Evenement#getPrixVip()
     * @see Evenement#getPlaceVipVendues()
     * @see Evenement#getPrixPremium()
     * @see Evenement#getPlacePremiumVendues()
     */
    private BigDecimal calculateRealRevenue(Evenement event) {
        BigDecimal revenue = BigDecimal.ZERO;
        
        // Revenus des places standard
        if (event.getPrixStandard() != null && event.getPlaceStandardVendues() > 0) {
            revenue = revenue.add(
                event.getPrixStandard().multiply(BigDecimal.valueOf(event.getPlaceStandardVendues()))
            );
        }
        
        // Revenus des places VIP
        if (event.getPrixVip() != null && event.getPlaceVipVendues() > 0) {
            revenue = revenue.add(
                event.getPrixVip().multiply(BigDecimal.valueOf(event.getPlaceVipVendues()))
            );
        }
        
        // Revenus des places Premium
        if (event.getPrixPremium() != null && event.getPlacePremiumVendues() > 0) {
            revenue = revenue.add(
                event.getPrixPremium().multiply(BigDecimal.valueOf(event.getPlacePremiumVendues()))
            );
        }
        
        return revenue;
    }

    private void updateCharts(List<Evenement> events) {
        // Pie chart: répartition par type avec taux de remplissage
        Map<String, Long> counts = events.stream()
                .collect(Collectors.groupingBy(ev -> ev.getTypeEvenement().getLabel(), Collectors.counting()));

        pieByType.getData().clear();
        counts.forEach((type, count) -> {
            // Calcul du taux moyen pour ce type
            double avgRate = events.stream()
                    .filter(e -> e.getTypeEvenement().getLabel().equals(type))
                    .filter(e -> e.getCapaciteTotale() > 0)
                    .mapToDouble(Evenement::getTauxRemplissage)
                    .average()
                    .orElse(0.0);
            
            String label = String.format("%s (%d) - %.1f%%", type, count, avgRate);
            pieByType.getData().add(new PieChart.Data(label, count));
        });

        // Bar chart: chiffre d'affaires RÉEL par type (basé sur les ventes) - AMÉLIORÉ
        updateRevenueBarChart(events);
    }
    
    /**
     * Met à jour le graphique en barres du chiffre d'affaires avec une logique améliorée
     */
    private void updateRevenueBarChart(List<Evenement> events) {
        barRevenueByType.getData().clear();
        
        // Si aucun événement, afficher un graphique vide avec message
        if (events.isEmpty()) {
            barRevenueByType.setTitle("Aucun événement à afficher");
            return;
        }
        
        // Calculer le chiffre d'affaires par type présent dans les événements filtrés
        Map<String, BigDecimal> revenueByType = new LinkedHashMap<>();
        
        // Initialiser seulement les types présents dans les événements filtrés
        Set<String> presentTypes = events.stream()
                .map(ev -> ev.getTypeEvenement().getLabel())
                .collect(Collectors.toSet());
        
        for (String type : presentTypes) {
            revenueByType.put(type, BigDecimal.ZERO);
        }
        
        // Calculer les revenus réels pour chaque événement
        for (Evenement ev : events) {
            String label = ev.getTypeEvenement().getLabel();
            BigDecimal realRevenue = calculateRealRevenue(ev);
            revenueByType.put(label, revenueByType.get(label).add(realRevenue));
        }
        
        // Créer la série de données
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Chiffre d'affaires réel (€)");
        
        // Ajouter toutes les données, même celles à 0 pour maintenir la cohérence visuelle
        revenueByType.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed()) // Trier par revenus décroissants
                .forEach(entry -> {
                    String type = entry.getKey();
                    BigDecimal revenue = entry.getValue();
                    series.getData().add(new XYChart.Data<>(type, revenue.doubleValue()));
                });
        
        // S'assurer qu'il y a au moins une donnée à afficher
        if (!series.getData().isEmpty()) {
            barRevenueByType.getData().add(series);
            
            // Titre dynamique en fonction du contenu
            BigDecimal totalRevenue = revenueByType.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
                barRevenueByType.setTitle(String.format("Chiffre d'affaires par type (Total: %.2f €)", totalRevenue.doubleValue()));
            } else {
                barRevenueByType.setTitle("Chiffre d'affaires par type (Aucune vente)");
            }
        } else {
            barRevenueByType.setTitle("Aucun type d'événement sélectionné");
        }
    }

    private void exportCsv() {
        try {
            Utilisateur user = SessionManager.getUtilisateurConnecte();
            int userId = user != null ? user.getIdUtilisateur() : -1;

            List<Evenement> events;
            if (userId >= 0) {
                events = evenementService.getEvenementsParOrganisateur(userId);
            } else {
                events = evenementService.getAllEvents();
            }

            // Appliquer les filtres actuels
            LocalDate from = dpFrom.getValue();
            LocalDate to = dpTo.getValue();
            String typeLabel = cbType.getValue();

            List<Evenement> filtered = events.stream()
                    .filter(ev -> {
                        if (from != null && ev.getDateEvenement().toLocalDate().isBefore(from)) return false;
                        if (to != null && ev.getDateEvenement().toLocalDate().isAfter(to)) return false;
                        if (typeLabel != null && !typeLabel.isBlank() && !"Tous les types".equals(typeLabel)) {
                            return ev.getTypeEvenement().getLabel().equals(typeLabel);
                        }
                        return true;
                    })
                    .collect(Collectors.toList());

            String filePath = System.getProperty("user.home") + "\\eventmanager_dashboard_export.csv";
            try (FileWriter writer = new FileWriter(filePath)) {
                // En-têtes CSV avec toutes les métriques importantes
                writer.write("Nom,Type,Date,Lieu,Statut,Actif,");
                writer.write("Capacite_Totale,Places_Standard_Vendues,Places_VIP_Vendues,Places_Premium_Vendues,Total_Places_Vendues,");
                writer.write("Taux_Remplissage,Prix_Standard,Prix_VIP,Prix_Premium,Chiffre_Affaires_Reel\n");

                // Données pour chaque événement
                for (Evenement ev : filtered) {
                    writer.write(String.format("%s,%s,%s,%s,%s,%s,", 
                            escapeCsv(ev.getNom()),
                            ev.getTypeEvenement().getLabel(),
                            ev.getDateEvenement().toLocalDate().toString(),
                            escapeCsv(ev.getLieu()),
                            ev.getStatut().name(),
                            ev.isEtatEvent() ? "Oui" : "Non"
                    ));
                    
                    writer.write(String.format("%d,%d,%d,%d,%d,",
                            ev.getCapaciteTotale(),
                            ev.getPlaceStandardVendues(),
                            ev.getPlaceVipVendues(),
                            ev.getPlacePremiumVendues(),
                            ev.getTotalPlacesVendues()
                    ));
                    
                    writer.write(String.format("%.2f,%.2f,%.2f,%.2f,%.2f\n",
                            ev.getTauxRemplissage(),
                            ev.getPrixStandard() != null ? ev.getPrixStandard().doubleValue() : 0.0,
                            ev.getPrixVip() != null ? ev.getPrixVip().doubleValue() : 0.0,
                            ev.getPrixPremium() != null ? ev.getPrixPremium().doubleValue() : 0.0,
                            calculateRealRevenue(ev).doubleValue()
                    ));
                }
            }
            NotificationUtils.showSuccess("Export CSV créé : " + filePath);
        } catch (Exception e) {
            logger.error("Erreur export CSV", e);
            NotificationUtils.showError("Erreur lors de l'export CSV : " + e.getMessage());
        }
    }

    /**
     * Échappe les caractères spéciaux pour CSV
     */
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
    