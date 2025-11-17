package com.bschooleventmanager.eventmanager.controller.client;

import com.bschooleventmanager.eventmanager.dao.EvenementDAO;
import com.bschooleventmanager.eventmanager.model.Evenement;
import com.bschooleventmanager.eventmanager.model.enums.StatutEvenement;
import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClientEventsController {

    private static final Logger logger = LoggerFactory.getLogger(ClientEventsController.class);

    private final EvenementDAO dao = new EvenementDAO();

    @FXML private TableView<Evenement> eventsTable;

    @FXML private TableColumn<Evenement, String> colNom;
    @FXML private TableColumn<Evenement, String> colLieu;

    @FXML private TableColumn<Evenement, TypeEvenement> colType;
    @FXML private TableColumn<Evenement, StatutEvenement> colStatut;
    @FXML private TableColumn<Evenement, LocalDateTime> colDate;

    @FXML private TextField searchNomField;
    @FXML private TextField searchLieuField;
    @FXML private ComboBox<String> typeFilter;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        logger.info("Initializing ClientEventsController");

        setupTable();
        loadTypeFilterOptions();
        loadAllEvents();
    }

    private void setupTable() {

        // Bind getters to columns
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colLieu.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeEvenement"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateEvenement"));

        // Format enums (Type, Statut)
        colType.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(TypeEvenement item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.name());
                setStyle("-fx-text-fill: black;");
            }
        });

        colStatut.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(StatutEvenement item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.name());
                setStyle("-fx-text-fill: black;");
            }
        });

        // Format date
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

        // Set black font for every table row
        eventsTable.setRowFactory(tv -> {
            TableRow<Evenement> row = new TableRow<>();
            row.setStyle("-fx-text-fill: black;");
            return row;
        });
    }

    private void loadTypeFilterOptions() {
        typeFilter.getItems().setAll("CONCERT", "SPECTACLE", "CONFERENCE");
    }

    @FXML
    private void loadAllEvents() {
        logger.info("Loading all events from DB...");

        List<Evenement> events = dao.getAllEvents();

        logger.info("Loaded {} events", events.size());
        events.forEach(e -> System.out.println("EVENT LOADED → " + e));

        eventsTable.getItems().setAll(events);
    }

    @FXML
    private void searchEvents() {
        logger.info("Search triggered (not implemented yet)");
        // implement search later
    }
}
