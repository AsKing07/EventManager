package com.bschooleventmanager.eventmanager.controller.organisateur;
import com.bschooleventmanager.eventmanager.model.Evenement;
import com.bschooleventmanager.eventmanager.service.EvenementService;
import com.bschooleventmanager.eventmanager.util.NotificationUtils;
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

public class OrganisateurEventListController implements Initializable {

    private static final Logger logger =
            LoggerFactory.getLogger(OrganisateurEventListController.class);

    @FXML
    private TableView<Evenement> eventTable;

    @FXML
    private TableColumn<Evenement, String> nomColumn;

    @FXML
    private TableColumn<Evenement, LocalDateTime> dateColumn;

    @FXML
    private TableColumn<Evenement, String> statutColumn;

          // Référence au contrôleur parent (injected par le loader dans le parent)
    private OrganisateurDashboardController parentController;

    private final EvenementService evenementService = new EvenementService();
    private int organisateurId = -1;


    public void setParentController(OrganisateurDashboardController parent) {
        this.parentController = parent;
    }


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
            NotificationUtils.showError("ID de l'organisateur non valide. Veuillez vous reconnecter.");
            return;
        }

        try {
            logger.info("Chargement des événements pour l'organisateur {}", organisateurId);

            List<Evenement> listeEvenements =
                    evenementService.getEvenementsParOrganisateur(organisateurId); 


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
     * Méthode pour rafraîchir la liste des événements
     */
    @FXML
    private void handleRefresh() {
        logger.info("Rafraîchissement de la liste des événements demandé");
        chargerEvenementsOrganisateur();
    }
}
    

