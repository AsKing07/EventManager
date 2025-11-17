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
 * Contrôleur du contenu du dashboard organisateur.
 * Calcule les métriques, gère les filtres, les graphiques et l'export CSV.
 */
public class OrganisateurDashboardContentController {
    private static final Logger logger = LoggerFactory.getLogger(OrganisateurDashboardContentController.class);

    @FXML private Label lblSummary;
    @FXML private Button btnCreateEvent;
    @FXML private DatePicker dpFrom, dpTo;
    @FXML private ComboBox<String> cbType;
    @FXML private Button btnApplyFilters;
    @FXML private Button btnExportCsv, btnExportPdf;

    @FXML private Label lblTotalEvents;
    @FXML private Label lblTotalRevenue;
    @FXML private Label lblAvgFillRate;
    @FXML private Label lblTotalTicketsSold;
    @FXML private Label lblActiveEvents;
    @FXML private PieChart pieByType;
    @FXML private BarChart<String, Number> barRevenueByType;

    // Référence au contrôleur parent (injected par le loader dans le parent)
    private OrganisateurDashboardController parentController;

    private final EvenementService evenementService = new EvenementService();

    public void setParentController(OrganisateurDashboardController parent) {
        this.parentController = parent;
    }

    @FXML
    private void initialize() {
        // Préparer les types avec l'option "Tous"
        List<String> typeOptions = new ArrayList<>();
        typeOptions.add("Tous les types");
        typeOptions.addAll(Arrays.stream(TypeEvenement.values())
                .map(TypeEvenement::getLabel)
                .collect(Collectors.toList()));
        
        cbType.setItems(FXCollections.observableArrayList(typeOptions));

        btnApplyFilters.setOnAction(e -> refreshData());
        btnCreateEvent.setOnAction(e -> {
            if (parentController != null) parentController.showCreateEvent();
        });

        btnExportCsv.setOnAction(e -> exportCsv());
    btnExportPdf.setOnAction(e -> NotificationUtils.showInfo("Info", "Export PDF non implémenté. Utilisez l'export CSV pour l'instant."));

        // Chargement initial
        Platform.runLater(this::refreshData);
    }

    private void refreshData() {
        try {
            Utilisateur user = SessionManager.getUtilisateurConnecte();
            int userId = user != null ? user.getIdUtilisateur() : -1;

            LocalDate from = dpFrom.getValue();
            LocalDate to = dpTo.getValue();
            String typeLabel = cbType.getValue();

            List<Evenement> events;
            if (userId >= 0) {
                events = evenementService.getEvenementsParOrganisateur(userId);
            } else {
                events = evenementService.getAllEvents();
            }

            // Apply filters
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

            updateMetrics(filtered);
            updateCharts(filtered);

        } catch (Exception e) {
            logger.error("Erreur rafraîchissement dashboard", e);
            NotificationUtils.showError("Impossible de charger les données du dashboard");
        }
    }

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

        // Bar chart: chiffre d'affaires RÉEL par type (basé sur les ventes)
        Map<String, BigDecimal> revenueByType = new LinkedHashMap<>();
        for (TypeEvenement t : TypeEvenement.values()) {
            revenueByType.put(t.getLabel(), BigDecimal.ZERO);
        }

        for (Evenement ev : events) {
            String label = ev.getTypeEvenement().getLabel();
            BigDecimal realRevenue = calculateRealRevenue(ev);
            revenueByType.put(label, revenueByType.getOrDefault(label, BigDecimal.ZERO).add(realRevenue));
        }

        barRevenueByType.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Chiffre d'affaires réel");
        revenueByType.forEach((k, v) -> {
            if (v.compareTo(BigDecimal.ZERO) > 0) { // Afficher seulement les types avec des ventes
                series.getData().add(new XYChart.Data<>(k, v.doubleValue()));
            }
        });
        barRevenueByType.getData().add(series);
        
        // Titre du graphique en fonction du mode
        barRevenueByType.setTitle("Chiffre d'affaires par type d'événement");
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
    