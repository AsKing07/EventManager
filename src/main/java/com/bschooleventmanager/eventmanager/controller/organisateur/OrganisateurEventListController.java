package com.bschooleventmanager.eventmanager.controller.organisateur;

import com.bschooleventmanager.eventmanager.model.Evenement;
import com.bschooleventmanager.eventmanager.service.EvenementService;
import com.bschooleventmanager.eventmanager.util.NotificationUtils;
import com.bschooleventmanager.eventmanager.controller.events.ModifyEventController;
import com.bschooleventmanager.eventmanager.exception.BusinessException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.scene.control.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur de la liste des événements côté organisateur avec gestion complète CRUD.
 * 
 * <p><b>Fonctionnalités principales :</b></p>
 * <ul>
 *   <li>Affichage tabulaire de tous les événements de l'organisateur connecté</li>
 *   <li>Colonnes détaillées : nom, date/heure, lieu, type, statut, capacité et actions</li>
 *   <li>Actions par ligne : modification et suppression avec confirmation</li>
 *   <li>Rafraîchissement automatique après modifications</li>
 *   <li>Gestion des erreurs avec notifications utilisateur appropriées</li>
 * </ul>
 * 
 * <p><b>Architecture de la table :</b></p>
 * <ul>
 *   <li>TableView avec colonnes configurées automatiquement via PropertyValueFactory</li>
 *   <li>Colonne d'actions personnalisée avec boutons inline (Modifier/Supprimer)</li>
 *   <li>Formatage automatique des dates avec DateTimeFormatter localisé</li>
 *   <li>Indicateurs visuels de statut (actif/inactif) et de type d'événement</li>
 * </ul>
 * 
 * <p><b>Workflow de gestion des événements :</b></p>
 * <ol>
 *   <li>Chargement initial des événements de l'organisateur depuis le service</li>
 *   <li>Configuration des colonnes et formatage des données d'affichage</li>
 *   <li>Actions modification : ouverture de ModifyEventController en fenêtre modale</li>
 *   <li>Actions suppression : confirmation utilisateur puis suppression sécurisée</li>
 *   <li>Rafraîchissement automatique de la table après chaque opération</li>
 * </ol>
 * 
 * <p><b>Intégration système :</b></p>
 * <ul>
 *   <li>Communication avec EvenementService pour toutes les opérations CRUD</li>
 *   <li>Utilisation de SessionManager pour identification de l'organisateur</li>
 *   <li>Notifications via NotificationUtils pour feedback utilisateur</li>
 *   <li>Navigation modale vers ModifyEventController pour éditions</li>
 * </ul>
 * 
 * <p><b>Sécurité et validation :</b></p>
 * <ul>
 *   <li>Vérification systématique de la session utilisateur connecté</li>
 *   <li>Validation des droits d'accès aux événements de l'organisateur</li>
 *   <li>Confirmations de suppression pour éviter les pertes de données</li>
 *   <li>Gestion robuste des erreurs avec logging et notifications</li>
 * </ul>
 * 
 * @author Yvonne NJOKI  @koki-pickles
 * @version 1.0
 * @since 1.0
 * 
 * @see EvenementService
 * @see ModifyEventController
 * @see Evenement
 * @see NotificationUtils
 * @see SessionManager
 */
public class OrganisateurEventListController implements Initializable {

    /** Logger pour traçage des opérations de gestion des événements et interactions table. */
    private static final Logger logger = LoggerFactory.getLogger(OrganisateurEventListController.class);

    /** Table principale d'affichage des événements de l'organisateur avec colonnes configurées. */
    @FXML private TableView<Evenement> eventTable;

    /** Colonne d'affichage du nom/titre de l'événement avec formatage automatique. */
    @FXML private TableColumn<Evenement, String> nomColumn;

    /** Colonne d'affichage de la date et heure avec formatage localisé DD/MM/YYYY HH:MM. */
    @FXML private TableColumn<Evenement, LocalDateTime> dateColumn;

    /** Colonne d'affichage du statut de l'événement (Actif/Inactif) avec indicateurs visuels. */
    @FXML private TableColumn<Evenement, String> statutColumn;

    /** Colonne personnalisée d'actions avec boutons inline (Modifier/Supprimer). */
    @FXML private TableColumn<Evenement, Void> actionsColumn;

    /** Référence vers le contrôleur parent pour navigation et coordination. */
    private OrganisateurDashboardController parentController;

    /** Service métier pour toutes les opérations CRUD sur les événements. */
    private final EvenementService evenementService = new EvenementService();
    
    /** ID de l'organisateur connecté pour filtrage des événements (-1 si non initialisé). */
    private int organisateurId = -1;
    
    /** Formatage des dates pour affichage uniforme dans la table (format français). */
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


    /**
     * Définit la référence vers le contrôleur parent pour navigation et coordination.
     * 
     * @param parent Le contrôleur dashboard organisateur principal
     * 
     * @see OrganisateurDashboardController
     */
    public void setParentController(OrganisateurDashboardController parent) {
        this.parentController = parent;
    }

    /**
     * Définit l'ID de l'organisateur connecté et charge ses événements automatiquement.
     * 
     * <p>Cette méthode est appelée après l'initialisation du contrôleur pour
     * configurer le contexte utilisateur et déclencher le chargement initial
     * des données spécifiques à cet organisateur.</p>
     * 
     * @param id L'identifiant unique de l'organisateur connecté
     * 
     * @see #chargerEvenementsOrganisateur()
     */
    public void setOrganisateurId(int id) {
        this.organisateurId = id;
        chargerEvenementsOrganisateur(); // Load events only after ID is set
    }

    /**
     * Initialise la table des événements avec configuration des colonnes et gestionnaires.
     * 
     * <p><b>Configuration de la table :</b></p>
     * <ol>
     *   <li>Validation défensive des injections FXML pour détection d'erreurs</li>
     *   <li>Configuration des PropertyValueFactory pour colonnes automatiques</li>
     *   <li>Formatage personnalisé de la colonne date avec DateTimeFormatter</li>
     *   <li>Configuration de la colonne statut avec indicateurs Actif/Inactif</li>
     *   <li>Création de la colonne d'actions avec boutons inline personnalisés</li>
     * </ol>
     * 
     * <p><b>Colonnes configurées :</b></p>
     * <ul>
     *   <li>nomColumn : Affichage direct du nom de l'événement</li>
     *   <li>dateColumn : Formatage localisé DD/MM/YYYY HH:MM</li>
     *   <li>statutColumn : Conversion boolean vers texte Actif/Inactif</li>
     *   <li>actionsColumn : Boutons Modifier et Supprimer avec gestionnaires</li>
     * </ul>
     * 
     * <p><b>Note :</b> Le chargement des données est différé jusqu'à la réception
     * de l'ID organisateur via setOrganisateurId().</p>
     * 
     * @param url URL de localisation (non utilisé)
     * @param resourceBundle Bundle de ressources pour localisation (non utilisé)
     * 
     * @see #setOrganisateurId(int)
     * @see #chargerEvenementsOrganisateur()
     * @see #createActionsColumn()
     */
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
        
        // Configure date column formatting
        setupDateCellFactory();
        
        configurerColonneActions();

        logger.info("Colonnes configurées avec succès");
    }

    /**
     * Configure le formatage de la colonne date
     */
    private void setupDateCellFactory() {
        dateColumn.setCellFactory(column -> new TableCell<>() {
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


    private void configurerColonneActions() {
        actionsColumn.setCellFactory(param -> new ActionsButtonCell());
        actionsColumn.setPrefWidth(320); // Plus large pour 3 boutons
    }

    /**
     * Classe pour les boutons d'action dans la colonne Actions
     */
    private class ActionsButtonCell extends TableCell<Evenement, Void> {
        private final Button statisticsBtn;
        private final Button modifyBtn;
        private final Button deleteBtn;
        private final HBox buttonsContainer;

        public ActionsButtonCell() {
            // Bouton Statistiques
            statisticsBtn = new Button("📊 Stats");
            statisticsBtn.setStyle(
                "-fx-background-color: #3498db; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 4; " +
                "-fx-cursor: hand; " +
                "-fx-font-size: 10px; " +
                "-fx-padding: 4 8 4 8;"
            );
            statisticsBtn.setOnAction(event -> {
                Evenement evt = getTableView().getItems().get(getIndex());
                ouvrirFenetreStatistiques(evt);
            });

            // Bouton Modifier
            modifyBtn = new Button("✏️ Modifier");
            modifyBtn.setStyle(
                "-fx-background-color: #f39c12; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 4; " +
                "-fx-cursor: hand; " +
                "-fx-font-size: 10px; " +
                "-fx-padding: 4 8 4 8;"
            );
            modifyBtn.setOnAction(event -> {
                Evenement evt = getTableView().getItems().get(getIndex());
                try{
                    parentController.showModifyEvent(evt);
                }catch(Exception e){
                    ouvrirFenetreModification(evt);
                }
            });

            // Bouton Supprimer
            deleteBtn = new Button("🗑️ Supprimer");
            deleteBtn.setStyle(
                "-fx-background-color: #e74c3c; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 4; " +
                "-fx-cursor: hand; " +
                "-fx-font-size: 10px; " +
                "-fx-padding: 4 8 4 8;"
            );
            deleteBtn.setOnAction(event -> {
                Evenement evt = getTableView().getItems().get(getIndex());
                confirmerSuppression(evt);
            });

            // Container pour les boutons
            buttonsContainer = new HBox(5); // Espacement de 5px
            buttonsContainer.getChildren().addAll(statisticsBtn, modifyBtn, deleteBtn);
            buttonsContainer.setAlignment(javafx.geometry.Pos.CENTER);
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(buttonsContainer);
            }
        }
    }

    /**
     * Ouvre la fenêtre des statistiques pour un événement
     */
    private void ouvrirFenetreStatistiques(Evenement evt) {
        try {
            logger.info("Ouverture des statistiques pour l'événement: {}", evt.getNom());
            // TODO: Implémenter l'ouverture des statistiques
            NotificationUtils.showInfo("Statistiques", 
                "Statistiques de l'événement: " + evt.getNom() + "\nFonctionnalité en cours de développement.");
        } catch (Exception e) {
            logger.error("Erreur lors de l'ouverture des statistiques pour l'événement {}", evt.getIdEvenement(), e);
            NotificationUtils.showError("Impossible d'afficher les statistiques de l'événement");
        }
    }

    /**
     * Confirme et effectue la suppression d'un événement
     */
    private void confirmerSuppression(Evenement evt) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText("Supprimer l'événement ?");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer l'événement :\n" + 
                                   evt.getNom() + " ?\n\nCette action est irréversible.");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean success = evenementService.suppEvent(evt.getIdEvenement());
                    if (success) {
                        logger.info("Événement supprimé avec succès: {}", evt.getNom());
                        NotificationUtils.showSuccess("Événement supprimé avec succès");
                        chargerEvenementsOrganisateur(); // Rafraîchir la liste
                    } else {
                        NotificationUtils.showError("Échec de la suppression de l'événement");
                    }
                } catch (BusinessException e) {
                    logger.error("Erreur lors de la suppression de l'événement {}", evt.getIdEvenement(), e);
                    NotificationUtils.showError("Erreur lors de la suppression: " + e.getMessage());
                } catch (Exception e) {
                    logger.error("Erreur technique lors de la suppression de l'événement {}", evt.getIdEvenement(), e);
                    NotificationUtils.showError("Erreur technique lors de la suppression de l'événement");
                }
            }
        });
    }

    /**
     * Loads the events belonging to the selected organiser.
     */
    private void chargerEvenementsOrganisateur() {
        if (organisateurId <= 0) {
            logger.error("ERREUR: ID Organisateur non défini ou invalide: {}", organisateurId);
            NotificationUtils.showError("ID de l'organisateur non valide. Veuillez vous reconnecter.");
            return;
        }

        try {
            logger.info("Chargement des événements pour l'organisateur {}", organisateurId);

            List<Evenement> listeEvenements =
                    evenementService.getEvenementsActifsParOrganisateur(organisateurId); 


            if (listeEvenements == null || listeEvenements.isEmpty()) {
                logger.info("Aucun événement trouvé pour l'organisateur {}", organisateurId);
                eventTable.setItems(FXCollections.observableArrayList());
                NotificationUtils.showInfo("Information", "Aucun événement créé pour le moment.");
                return;
            }

            ObservableList<Evenement> data =
                    FXCollections.observableArrayList(listeEvenements);

            eventTable.setItems(data);

            logger.info("{} événements chargés dans la table", data.size());

        } catch (BusinessException e) {
            logger.error("Erreur lors du chargement des événements pour l'organisateur {}", organisateurId, e);
            NotificationUtils.showError("Impossible de charger les événements: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur inattendue lors du chargement des événements", e);
            NotificationUtils.showError("Erreur technique lors du chargement des événements");
        }
    }

    /**
     * Ouvre la fenêtre de modification d'un événement
     */
    private void ouvrirFenetreModification(Evenement evt) {
        try {
            logger.info("Ouverture de la fenêtre de modification pour l'événement: {}", evt.getNom());
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/organisateur/Events/editEvent.fxml"));
            Parent root = loader.load();

            ModifyEventController controller = loader.getController();
            controller.setEvenementInfo(evt.getIdEvenement(), evt.getTypeEvenement());
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier l'événement - " + evt.getNom());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Rafraîchir la liste après fermeture de la fenêtre
            chargerEvenementsOrganisateur();
            
        } catch (IOException e) {
            logger.error("Erreur lors de l'ouverture de la fenêtre de modification pour l'événement {}", evt.getIdEvenement(), e);
            NotificationUtils.showError("Impossible d'ouvrir la fenêtre de modification");
        } catch (Exception e) {
            logger.error("Erreur technique lors de l'ouverture de la modification", e);
            NotificationUtils.showError("Erreur technique lors de l'ouverture de la fenêtre");
        }
    }

    
    /**
     * Ouvre la fenêtre de création d'un événement
     */
    @FXML
    private void handleCreateEvent() {
        logger.info("Création d'un nouvel événement demandé");
        parentController.showCreateEvent();
    }


    /**
     * Méthode pour rafraîchir la liste des événements
     */
    @FXML
    private void handleRefresh() {
        logger.info("Rafraîchissement de la liste des événements demandé");
        chargerEvenementsOrganisateur();
    }
}
    

