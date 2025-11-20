package com.bschooleventmanager.eventmanager.controller.events;

import com.bschooleventmanager.eventmanager.controller.organisateur.OrganisateurDashboardController;
import com.bschooleventmanager.eventmanager.model.Concert;
import com.bschooleventmanager.eventmanager.model.Conference;
import com.bschooleventmanager.eventmanager.model.Evenement;
import com.bschooleventmanager.eventmanager.model.Spectacle;
import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventDetailsController {
    private static final Logger logger = LoggerFactory.getLogger(ModifyEventController.class);

    // === FXML Elements ===

    // --- Boutons ---
    @FXML private Button btnReturn;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;

    // --- Textes / Labels Infos Générales ---
    @FXML private Label lblNom;
    @FXML private Label lblDescription;
    @FXML private Label lblDate;
    @FXML private Label lblLieu;
    @FXML private Label lblType;

    @FXML private Label lblOrganisateur;
    @FXML private Label lblStatus;
    @FXML private Label lblEtatEvent;

// --- Infos spécifiques aux types d’événements ---

    // CONCERT
    @FXML private Label lblArtisteGroupe,lblArtisteGroupe1;
    @FXML private Label lblAgeMin,lblAgeMin1;
    @FXML private Label lblTypeConcert,lblTypeConcert1;

    // SPECTACLE
    @FXML private Label lblTypeSpectacle,lblTypeSpectacle1;
    @FXML private Label lblNiveauExpertise,lblNiveauExpertise1;

    // CONFERENCE
    @FXML private Label lblIntervenant,lblIntervenant1;
    @FXML private Label lblDomaine,lblDomaine1;

    // --- Statistiques ---
    @FXML private ProgressBar pBarTauxRemplissage;
    @FXML private Label lblTauxRemplissage;
    @FXML private Label lblBilletsVendus;
    @FXML private Label lblTicketsAnnules;
    @FXML private Label lblChiffreAffaire;

    // --- Graphiques ---
    @FXML private PieChart pieVentesCategorie;
    @FXML private LineChart<String, Number> lineChartCA;

    // --- Axes du LineChart ---
    @FXML private CategoryAxis xAxis_CA;
    @FXML private NumberAxis yAxis_CA;

    // --- Layouts ---
    @FXML private ScrollPane mainScrollPane;
    @FXML private VBox rootContainer;
    @FXML
    private PieChart pieChartCategories;

    // Référence au contrôleur parent
    private OrganisateurDashboardController parentController;

    /**     * Définit le contrôleur parent pour permettre la navigation entre les vues.
     *
     * @param parentController Le contrôleur du tableau de bord de l'organisateur.
     */
    public void setParentController(OrganisateurDashboardController parentController) {
        this.parentController = parentController;
    }

    private Evenement evenement;


    @FXML
    public void returnToDashboard() {
        logger.info("Retour à la liste des événements de l'organisateur.");
        parentController.showEvents();
    }

    @FXML
    public void modifyEvent() {
        logger.info("Ouverture de l'interface de modification de l'événement.");
        // Logique pour ouvrir l'interface de modification
    }

    @FXML
    public void deleteEvent() {
        logger.info("Suppression de l'événement après confirmation.");
        // Logique pour supprimer l'événement
    }

    /**
     * Remplit les champs de l'interface avec les détails de l'événement fourni.
     * Affiche ou masque les champs spécifiques en fonction du type d'événement.
     *
     * @param e L'événement dont les détails doivent être affichés.
     */
    public void setDetailsEvenement(Evenement e) {
        logger.info("Affichage des détails pour l'événement: {}",
                e != null ? e.getNom() : "NULL");

        if (e == null) {
            logger.error("L'événement transmis est null !");
            return;
        }

        //INFORMATIONS GÉNÉRALES
        lblNom.setText(e.getNom());
        if(!e.getDescription().isEmpty()){
            lblDescription.setText(e.getDescription());
        }else {
            lblDescription.setText("Aucune description disponible pour cet évènement.");
        }

        lblLieu.setText(e.getLieu());
        lblDate.setText(e.getDateEvenement().toString());
        lblType.setText(e.getTypeEvenement().toString());

        // Champs génériques
        lblOrganisateur.setText(String.valueOf(e.getOrganisateurId()));

        // Champs spécifiques
        if(e instanceof Concert)
        {
            Concert concert = (Concert) e;
            lblArtisteGroupe.setText(concert.getArtiste_groupe());
            lblAgeMin.setText(concert.getAgeMin().toString());
            lblTypeConcert.setText(concert.getType().toString());
        }
        if(e instanceof Spectacle)
        {
            Spectacle spectacle = (Spectacle) e;
            lblArtisteGroupe.setText(spectacle.getTroupe_artistes());
            lblAgeMin.setText(spectacle.getAgeMin().toString());
            lblTypeSpectacle.setText(spectacle.getTypeSpectacle().toString());
        }
        if(e instanceof Conference)
        {
            Conference conference = (Conference) e;
            lblNiveauExpertise.setText(conference.getNiveauExpertise().toString());
            lblIntervenant.setText(conference.getIntervenants());
            lblDomaine.setText(conference.getDomaine());
        }

        //AFFICHAGE SELON TYPE
        TypeEvenement type = e.getTypeEvenement();

        // Tout afficher d'abord
        showAllSpecificFields();

        switch (type) {
            case TypeEvenement.CONCERT:
                hide(lblIntervenant,lblIntervenant1, lblDomaine,lblDomaine1, lblTypeSpectacle,lblTypeSpectacle1, lblNiveauExpertise,lblNiveauExpertise1,
                        lblOrganisateur, lblStatus, lblEtatEvent);
                break;

            case TypeEvenement.SPECTACLE:
                hide(lblTypeConcert,lblTypeConcert1, lblIntervenant,lblIntervenant1, lblDomaine,lblDomaine1,
                        lblOrganisateur, lblStatus, lblEtatEvent);
                break;

            case TypeEvenement.CONFERENCE:
                hide(lblArtisteGroupe,lblArtisteGroupe1, lblAgeMin,lblAgeMin1, lblTypeConcert,lblTypeConcert1, lblTypeSpectacle,lblTypeSpectacle1,
                        lblOrganisateur, lblStatus, lblEtatEvent);
                break;

            default:
                logger.warn("Type d'événement inconnu : {}", type);
        }

        //STATISTIQUES
        int totalPlaces = e.getPlacesPremiumDisponibles() + e.getPlacesStandardDisponibles() + e.getPlacesVipDisponibles();
        // totalPlaces = e.getTotalPlaces();
        int vendues = e.getPlacePremiumVendues()+ e.getPlaceStandardVendues()+ e.getPlaceVipVendues();

        Double nbrePlacePremium = Double.valueOf(e.getPlacePremiumVendues());
        Double nbrePlaceStandard = Double.valueOf(e.getPlaceStandardVendues());
        Double nbrePlaceVip = Double.valueOf(e.getPlaceVipVendues());

        Double prixPremium = e.getPrixPremium().doubleValue();
        Double prixStandard = e.getPrixStandard().doubleValue();
        Double prixVip = e.getPrixVip().doubleValue();

        Double ca = nbrePlacePremium*prixPremium + nbrePlaceStandard*prixStandard + nbrePlaceVip*prixVip;

        // Taux de remplissage
        double taux = totalPlaces > 0 ? (double) vendues / totalPlaces : 0.0;
        pBarTauxRemplissage.setProgress(taux);
        lblTauxRemplissage.setText(String.format("%.2f %%", taux * 100));

        lblBilletsVendus.setText(String.valueOf(vendues));
        //lblTicketsAnnules.setText(String.valueOf(annulees));
        lblChiffreAffaire.setText(String.format("%.2f €", ca));

        //PIE CHART CATEGORIES
        pieVentesCategorie.getData().clear();
        if (e != null) {
            //e.getDetailsCategories().forEach((cat, val) ->
                    //pieVentesCategorie.getData().add(new PieChart.Data("cat", val)));
                    pieVentesCategorie.getData().add(new PieChart.Data("Standard ", e.getPlaceStandardVendues()));
                    pieVentesCategorie.getData().add(new PieChart.Data("VIP ", e.getPlaceVipVendues()));
                    pieVentesCategorie.getData().add(new PieChart.Data("Premium ", e.getPlacePremiumVendues()));
        }

        logger.info("Détails de l'événement correctement affichés.");
    }


    /*     * Cache les nœuds spécifiés en les rendant invisibles et non gérés.
     */
    private void hide(Node... nodes) {
        for (Node n : nodes) {
            if (n != null) {
                n.setVisible(false);
                n.setManaged(false);
            }
        }
    }

    /*     * Affiche tous les champs spécifiques en les rendant visibles et gérés.
     */
    private void showAllSpecificFields() {
        // remet visible tous les labels spécifiques
        Node[] all = {
                lblOrganisateur, lblStatus, lblEtatEvent,
                lblArtisteGroupe, lblAgeMin, lblTypeConcert,
                lblTypeSpectacle, lblNiveauExpertise,
                lblIntervenant, lblDomaine
        };

        for (Node n : all) {
            if (n != null) {
                n.setVisible(true);
                n.setManaged(true);
            }
        }
    }


}
