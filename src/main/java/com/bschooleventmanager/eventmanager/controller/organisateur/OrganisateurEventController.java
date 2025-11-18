package com.bschooleventmanager.eventmanager.controller.organisateur;

import com.bschooleventmanager.eventmanager.model.Evenement;
import com.bschooleventmanager.eventmanager.service.EvenementService;
import com.bschooleventmanager.eventmanager.exception.BusinessException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class OrganisateurEventController implements Initializable {

    private static final Logger logger =
            LoggerFactory.getLogger(OrganisateurEventController.class);

    @FXML
    private TableView<Evenement> eventTable;

    @FXML
    private TableColumn<Evenement, String> nomColumn;

    @FXML
    private TableColumn<Evenement, LocalDateTime> dateColumn;

    @FXML
    private TableColumn<Evenement, String> statutColumn;

    private final EvenementService evenementService = new EvenementService();
    private int organisateurId = -1;



    public void setOrganisateurId(int id) {
        this.organisateurId = id;
        chargerEvenementsOrganisateur(); // Load events only after ID is set
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initialisation du contrôleur des événements organisateur");

        // Defensive null checks (useful if FXML injection fails)
        if (eventTable == null || nomColumn == null || dateColumn == null || statutColumn == null) {
            logger.error("⚠ FXML injection failed: One or more UI elements are NULL");
            return;
        }

        // Link table columns to Evenement model fields
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateEvenement"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        logger.info("Colonnes configurées avec succès");
    }


    /**
     * Loads the events belonging to the selected organiser.
     */
    private void chargerEvenementsOrganisateur() {
        if (organisateurId <= 0) {
            logger.error("ERREUR: ID Organisateur non défini ou invalide: {}", organisateurId);
            return;
        }

        try {
            logger.info("Chargement des événements pour l'organisateur {}", organisateurId);

            List<Evenement> listeEvenements =
                    evenementService.listerEvenementsParOrganisateur(organisateurId);

            ObservableList<Evenement> data =
                    FXCollections.observableArrayList(listeEvenements);

            eventTable.setItems(data);

            logger.info("{} événements chargés dans la table", data.size());

        } catch (BusinessException e) {
            logger.error("Erreur lors du chargement des événements", e);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setHeaderText("Impossible de charger les événements");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
