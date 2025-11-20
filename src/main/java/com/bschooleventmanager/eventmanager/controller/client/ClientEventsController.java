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

/**
 * Contrôleur pour l'interface de consultation des événements côté client dans EventManager.
 * 
 * <p>Cette classe gère l'affichage de la liste complète des événements disponibles
 * avec des fonctionnalités avancées de filtrage, recherche, et navigation vers
 * les détails. Elle fournit une interface riche pour que les clients puissent
 * explorer et découvrir les événements qui les intéressent.</p>
 * 
 * <p><strong>Fonctionnalités principales :</strong></p>
 * <ul>
 *   <li>Affichage tabulaire des événements avec colonnes informatives</li>
 *   <li>Filtrage en temps réel par type d'événement</li>
 *   <li>Recherche textuelle par nom et lieu</li>
 *   <li>Tri des colonnes pour personnaliser l'affichage</li>
 *   <li>Navigation vers les détails de chaque événement</li>
 *   <li>Actualisation des données à la demande</li>
 * </ul>
 * 
 * <p><strong>Structure des données affichées :</strong></p>
 * <ul>
 *   <li><strong>Nom :</strong> Titre de l'événement</li>
 *   <li><strong>Lieu :</strong> Localisation de l'événement</li>
 *   <li><strong>Type :</strong> Concert, Spectacle, ou Conférence</li>
 *   <li><strong>Statut :</strong> À venir, En cours, Terminé, Annulé</li>
 *   <li><strong>Date :</strong> Date et heure formatées</li>
 *   <li><strong>Actions :</strong> Bouton de consultation des détails</li>
 * </ul>
 * 
 * <p><strong>Système de filtrage :</strong></p>
 * <ul>
 *   <li><strong>Filtrage combiné :</strong> Tous les critères appliqués simultanément</li>
 *   <li><strong>Recherche temps réel :</strong> Mise à jour immédiate lors de la saisie</li>
 *   <li><strong>Tri personnalisé :</strong> Colonnes cliquables pour tri ascendant/descendant</li>
 *   <li><strong>Réinitialisation :</strong> Retour à l'affichage complet en un clic</li>
 * </ul>
 * 
 * <p><strong>Architecture technique :</strong></p>
 * <ul>
 *   <li><strong>ObservableList :</strong> Données maîtres réactives</li>
 *   <li><strong>FilteredList :</strong> Application des critères de filtrage</li>
 *   <li><strong>SortedList :</strong> Liaison avec les comparateurs de table</li>
 *   <li><strong>Cell Factories :</strong> Rendu personnalisé pour chaque type de données</li>
 * </ul>
 * 
 * <p><strong>Navigation et actions :</strong></p>
 * <ul>
 *   <li>Boutons d'action intégrés dans chaque ligne</li>
 *   <li>Navigation vers détails avec transfert de données</li>
 *   <li>Ouverture en modal ou intégration au dashboard</li>
 *   <li>Gestion des erreurs avec notifications utilisateur</li>
 * </ul>
 * 
 * <p><strong>Exemple d'utilisation :</strong></p>
 * <pre>{@code
 * ClientEventsController controller = loader.getController();
 * controller.setDashboardController(dashboardController);
 * // Le contrôleur charge automatiquement tous les événements
 * }</pre>
 * 
 * @author @AsKing07 Charbel SONON,  Yvonne NJOKI
 * @version 1.0
 * @since 1.0
 * 
 * @see ClientDashboardController
 * @see ClientEventDetailsController
 * @see com.bschooleventmanager.eventmanager.model.Evenement
 * @see com.bschooleventmanager.eventmanager.dao.EvenementDAO
 */
public class ClientEventsController {

    /** Logger pour le traçage des opérations de consultation des événements */
    private static final Logger logger = LoggerFactory.getLogger(ClientEventsController.class);

    // === Éléments FXML - Table principale ===
    
    /** Table principale affichant la liste des événements */
    @FXML private TableView<Evenement> eventsTable;

    // === Colonnes de la table ===
    
    /** Colonne affichant le nom de l'événement */
    @FXML private TableColumn<Evenement, String> colNom;
    
    /** Colonne affichant le lieu de l'événement */
    @FXML private TableColumn<Evenement, String> colLieu;
    
    /** Colonne affichant le type d'événement */
    @FXML private TableColumn<Evenement, TypeEvenement> colType;
    
    /** Colonne affichant le statut de l'événement */
    @FXML private TableColumn<Evenement, StatutEvenement> colStatut;
    
    /** Colonne affichant la date de l'événement */
    @FXML private TableColumn<Evenement, LocalDateTime> colDate;
    
    /** Colonne contenant les boutons d'action */
    @FXML private TableColumn<Evenement, Void> colActions;

    // === Éléments de filtrage et recherche ===
    
    /** Champ de recherche par nom d'événement */
    @FXML private TextField searchNomField;
    
    /** Champ de recherche par lieu */
    @FXML private TextField searchLieuField;
    
    /** ComboBox de filtrage par type d'événement */
    @FXML private ComboBox<String> typeFilter;

    // === Structure des données ===
    
    /** Liste maître observable contenant tous les événements */
    private final ObservableList<Evenement> masterData = FXCollections.observableArrayList();
    
    /** Liste filtrée basée sur les critères de recherche */
    private FilteredList<Evenement> filteredData;
    
    /** Liste triée liée aux comparateurs de la table */
    private SortedList<Evenement> sortedData;
    
    /** Référence au contrôleur dashboard pour la navigation */
    private ClientDashboardController dashboardController;

    /** Formateur pour l'affichage des dates dans la table */
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Définit le contrôleur parent du dashboard pour la navigation
     */
    public void setDashboardController(ClientDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    /**
     * Initialise le contrôleur après le chargement du FXML.
     * Configure la table, les colonnes, les filtres, et charge les données.
     */
    @FXML
    public void initialize() {
        logger.info("Initializing ClientEventsController");

        setupTable();
        loadTypeFilterOptions();
        setupFilteringAndSorting();
        loadAllEvents();
        setupCombinedFilterListeners();
    }

    /** Configure la table des événements et ses colonnes */
    private void setupTable() {
        setupTableColumns();
        setupActionsColumn();
        setupTableRowFactory();
    }

    /** Configure les colonnes de la table des événements */
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

    /** Configure la cellule personnalisée pour la colonne Type */
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
    /** Configure la cellule personnalisée pour la colonne Statut */
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

    /** Configure la cellule personnalisée pour la colonne Date */
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

    /** Configure la cellule personnalisée pour la colonne Actions */
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

    /** Cellule personnalisée pour le bouton "Voir détails" dans la colonne Actions
     * <p>Cette classe interne crée une cellule de table contenant un bouton
     * "Voir détails" pour chaque ligne de la table des événements. Lorsqu'on clique
     * sur le bouton, elle ouvre la page affichant les détails de l'événement sélectionné.
     */
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

        /**
         * Fonction appelée pour mettre à jour le contenu de la cellule.
         */
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


    /**
        * Ouvre une nouvelle fenêtre affichant les détails de l'événement sélectionné.
        * 
        * @param event L'événement dont les détails doivent être affichés.
        */
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



    /** Charge les options de filtrage par type d'événement dans le ComboBox */
    private void loadTypeFilterOptions() {
        typeFilter.getItems().add("All Types");
        for (TypeEvenement type : TypeEvenement.values()) {
            typeFilter.getItems().add(type.name());
        }
    }

    private void setupFilteringAndSorting() {
        filteredData = new FilteredList<>(masterData, p -> true);
        sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(eventsTable.comparatorProperty());

        eventsTable.setItems(sortedData);

        typeFilter.getSelectionModel().selectFirst();
    }

/**
 * Initialise les écouteurs pour les champs de recherche et le filtre de type
 * <p>Cette méthode configure des écouteurs sur les champs de texte de recherche
 * et le ComboBox de type pour appliquer le filtrage combiné en temps réel.
 * </p>
 */
    private void setupCombinedFilterListeners() {
        searchNomField.textProperty().addListener((observable, oldValue, newValue) -> filterEvents());
        searchLieuField.textProperty().addListener((observable, oldValue, newValue) -> filterEvents());
        typeFilter.valueProperty().addListener((observable, oldValue, newValue) -> filterEvents());
    }

    /** 
     * Fonction de filtrage combiné pour les événements
     * <p>Cette méthode applique les critères de filtrage basés sur le nom,
     * le lieu, et le type d'événement simultanément.</p>
    */
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

    /** Charge tous les événements depuis la base de données */
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

    /**
     * Fonction déclenchée par le bouton de recherche
     */
    @FXML
    private void searchEvents() {
        logger.info("Search triggered (now calling filterEvents)");
        filterEvents();
    }

    /**
     * Réinitialise tous les filtres et affiche tous les événements.
     */
    @FXML
    private void resetFilters() {
        loadAllEvents();
    }
}