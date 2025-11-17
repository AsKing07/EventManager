package com.bschooleventmanager.eventmanager.controller.client;

import com.bschooleventmanager.eventmanager.dao.EvenementDAO;
import com.bschooleventmanager.eventmanager.model.Evenement;
import com.bschooleventmanager.eventmanager.model.enums.StatutEvenement;
import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;
import com.bschooleventmanager.eventmanager.util.NotificationUtils;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClientEventsController {

    private static final Logger logger = LoggerFactory.getLogger(ClientEventsController.class);

    @FXML private TableView<Evenement> eventsTable;

    @FXML private TableColumn<Evenement, String> colNom;
    @FXML private TableColumn<Evenement, String> colLieu;
    @FXML private TableColumn<Evenement, TypeEvenement> colType;
    @FXML private TableColumn<Evenement, StatutEvenement> colStatut;
    @FXML private TableColumn<Evenement, LocalDateTime> colDate;
    @FXML private TableColumn<Evenement, Void> colActions;

    @FXML private TextField searchNomField;
    @FXML private TextField searchLieuField;
    @FXML private ComboBox<String> typeFilter;

    private final ObservableList<Evenement> masterData = FXCollections.observableArrayList();
    private FilteredList<Evenement> filteredData;
    private SortedList<Evenement> sortedData;
    private ClientDashboardController dashboardController;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Définit le contrôleur parent du dashboard pour la navigation
     */
    public void setDashboardController(ClientDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    public void initialize() {
        logger.info("Initializing ClientEventsController");

        setupTable();
        loadTypeFilterOptions();
        setupFilteringAndSorting();
        loadAllEvents();
        setupCombinedFilterListeners();
    }

    private void setupTable() {
        setupTableColumns();
        setupActionsColumn();
        setupTableRowFactory();
    }

    private void setupTableColumns() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colLieu.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeEvenement"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateEvenement"));

        setupTypeCellFactory();
        setupStatutCellFactory();
        setupDateCellFactory();
    }

    private void setupTypeCellFactory() {
        colType.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(TypeEvenement item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.name());
                setStyle("-fx-text-fill: black;");
            }
        });
    }

    private void setupStatutCellFactory() {
        colStatut.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(StatutEvenement item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.name());
                setStyle("-fx-text-fill: black;");
            }
        });
    }

    private void setupDateCellFactory() {
        colDate.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item.format(dateFormatter));
                }
                setStyle("-fx-text-fill: black;");
            }
        });
    }

    private void setupActionsColumn() {
        colActions.setCellFactory(param -> new DetailsButtonCell());
    }

    private void setupTableRowFactory() {
        eventsTable.setRowFactory(tv -> {
            TableRow<Evenement> row = new TableRow<>();
            row.setStyle("-fx-text-fill: black;");
            return row;
        });
    }

    private class DetailsButtonCell extends TableCell<Evenement, Void> {
        private final Button detailsBtn;

        public DetailsButtonCell() {
            detailsBtn = new Button("👁️ Voir détails");
            detailsBtn.setStyle(
                "-fx-background-color: #3498db; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 4; " +
                "-fx-cursor: hand; " +
                "-fx-font-size: 11px; " +
                "-fx-padding: 5 10 5 10;"
            );
            detailsBtn.setOnAction(event -> {
                Evenement selectedEvent = getTableView().getItems().get(getIndex());
                if (dashboardController != null) {
                    dashboardController.showEventDetails(selectedEvent);
                } else {
                    openEventDetails(selectedEvent);
                }
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(detailsBtn);
            }
        }
    }


    private void openEventDetails(Evenement event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/clientEventDetails.fxml"));
            Parent root = loader.load();

            ClientEventDetailsController detailsController = loader.getController();

            detailsController.setEventData(event);

            Stage stage = new Stage();
            stage.setTitle("Details de l'événement : " + event.getNom());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            logger.error("Impossible d'ouvrir la fenêtre des détails de l'événement", e);
            NotificationUtils.showError("Impossible d'ouvrir la fenêtre des détails de l'événement.");
        }
    }

    private void loadTypeFilterOptions() {
        typeFilter.getItems().add("All Types");
        for (TypeEvenement type : TypeEvenement.values()) {
            typeFilter.getItems().add(type.name());
        }
    }

    // check if we can split these filters to sth else eg design patterns
    private void setupFilteringAndSorting() {
        filteredData = new FilteredList<>(masterData, p -> true);
        sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(eventsTable.comparatorProperty());

        eventsTable.setItems(sortedData);

        typeFilter.getSelectionModel().selectFirst();
    }

    //setting up listeners
    private void setupCombinedFilterListeners() {
        searchNomField.textProperty().addListener((observable, oldValue, newValue) -> filterEvents());
        searchLieuField.textProperty().addListener((observable, oldValue, newValue) -> filterEvents());
        typeFilter.valueProperty().addListener((observable, oldValue, newValue) -> filterEvents());
    }

    private void filterEvents() {
        String nomFilter = searchNomField.getText() == null ? "" : searchNomField.getText().toLowerCase();
        String lieuFilter = searchLieuField.getText() == null ? "" : searchLieuField.getText().toLowerCase();
        String typeSelection = typeFilter.getValue();

        filteredData.setPredicate(event -> {
            //Filtre Type
            boolean typeMatch = true;
            if (typeSelection != null && !typeSelection.equals("All Types")) {
                typeMatch = event.getTypeEvenement().name().equals(typeSelection);
            }

            // Filtre Nom
            boolean nomMatch = event.getNom().toLowerCase().contains(nomFilter);

            // Filtre Lieu
            boolean lieuMatch = event.getLieu().toLowerCase().contains(lieuFilter);

            return typeMatch && nomMatch && lieuMatch;
        });
    }

    @FXML
    private void loadAllEvents() {
        logger.info("Loading all events from DB...");

        List<Evenement> events = EvenementDAO.getAllEvents();

        logger.info("Loaded {} events", events.size());
        events.forEach(e -> System.out.println("EVENT LOADED → " + e));

        masterData.setAll(events);

        searchNomField.setText("");
        searchLieuField.setText("");
        typeFilter.getSelectionModel().select("All Types");
    }

    @FXML
    private void searchEvents() {
        logger.info("Search triggered (now calling filterEvents)");
        filterEvents();
    }

    @FXML
    private void resetFilters() {
        loadAllEvents();
    }
}