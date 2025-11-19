package com.bschooleventmanager.eventmanager.controller.organisateur;

import com.bschooleventmanager.eventmanager.model.Evenement;
import com.bschooleventmanager.eventmanager.service.EvenementService;
import com.bschooleventmanager.eventmanager.exception.BusinessException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

    @FXML
    private TableColumn<Evenement, Void> actionsColumn;

    private final EvenementService evenementService = new EvenementService();
    private int organisateurId = -1;


    @FXML
    private void handleRefresh() {
        chargerEvenementsOrganisateur();
    }

    public void setOrganisateurId(int id) {
        this.organisateurId = id;
        chargerEvenementsOrganisateur();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initialisation du contrôleur des événements organisateur");

        if (eventTable == null || nomColumn == null || dateColumn == null ||
                statutColumn == null || actionsColumn == null) {
            logger.error("⚠ FXML injection failed: One or more UI elements are NULL");
            return;
        }
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateEvenement"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        configurerColonneActions();

        logger.info("Colonnes configurées avec succès");
    }

    private void configurerColonneActions() {

        actionsColumn.setCellFactory(col -> new TableCell<Evenement, Void>() {

            private final Button btnUpdate = new Button("Modifier");
            private final Button btnDelete = new Button("Supprimer");

            {
                btnUpdate.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                btnDelete.setStyle("-fx-background-color: #E53935; -fx-text-fill: white;");

                btnUpdate.setOnAction(event -> {
                    Evenement evt = getTableView().getItems().get(getIndex());
                    ouvrirFenetreModification(evt);
                });

                btnDelete.setOnAction(event -> {
                    Evenement evt = getTableView().getItems().get(getIndex());
                    supprimerEvenement(evt);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(10, btnUpdate, btnDelete);
                    setGraphic(box);
                }
            }
        });

        actionsColumn.setPrefWidth(160);
    }


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

    private void supprimerEvenement(Evenement evt) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Supprimer l'événement ?");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer : " + evt.getNom() + " ?");

        var result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            logger.info("Suppression de l'événement {}", evt.getIdEvenement());

            // TODO: Call DAO delete and refresh
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Supprimé !");
            alert.setContentText("L'événement a été supprimé (implémente le DAO).");
            alert.show();
        }
    }

    private void deleteEvent(Evenement evt) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setHeaderText("Supprimer l'événement ?");
            confirm.setContentText("Êtes-vous sûr de vouloir supprimer : " + evt.getNom() + " ?");

            var result = confirm.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    evenementService.supprimerEvenement(evt.getIdEvenement());
                    chargerEvenementsOrganisateur(); // refresh table
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setHeaderText("Supprimé !");
                    info.setContentText("L'événement a été supprimé.");
                    info.show();

                } catch (BusinessException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            }
    }

    protected void ouvrirFenetreModification(Evenement evt) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/organisateur/updateEvent.fxml"));
            Parent root = loader.load();

            UpdateEventController controller = loader.getController();

            // Pass both the event AND a callback to refresh table after closing
            controller.setEvent(evt, this::chargerEvenementsOrganisateur);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier l'événement");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // table refresh now happens automatically via callback
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir la fenêtre de modification").show();
        }
    }






}
