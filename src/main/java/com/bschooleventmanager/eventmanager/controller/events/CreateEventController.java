package com.bschooleventmanager.eventmanager.controller.events;
import com.bschooleventmanager.eventmanager.dao.EvenementDAO;
import com.bschooleventmanager.eventmanager.exception.BusinessException;
import com.bschooleventmanager.eventmanager.model.enums.*;
import com.bschooleventmanager.eventmanager.service.EvenementService;
import com.bschooleventmanager.eventmanager.util.NotificationUtils;
import com.bschooleventmanager.eventmanager.util.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.bschooleventmanager.eventmanager.model.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class CreateEventController {
    @FXML private Label lbtfAge, lbtfArtits, lbtyConcert, lbTySpectacle, lbNvExpert, lbDomaine, lbIntervenant;
    @FXML private Label lblErrDate, lblErrTitre, lbErrLieu, lblErrTyEvent, lblErrTypeConcert, lblErrTypeSpectacle, lblErrNvExpert, lblErrDomaine, lblErrAge, lblErrArtiste;
    @FXML private TextField tfNom, tfLieu, prixPlaceStandard, nbPlacesVip, prixPlaceVip, nbPlacesPremium, prixPlacePremium, nbPlacesStandard, tfArtits, Domaine, Intervenant;
    @FXML private TextArea Description;
    @FXML private DatePicker dpDate;
    @FXML private Spinner<Integer> spHour, spMinute;
    @FXML private ComboBox<String> evType, nvExpert, tyConcert, tySpectacle;
    @FXML private ComboBox<Integer> tfAge;
    @FXML private Label lblError;
    @FXML private ImageView imgPreview;
    private static final Logger logger = LoggerFactory.getLogger(CreateEventController.class);
    
    // Référence au contrôleur dashboard pour permettre le retour
    private com.bschooleventmanager.eventmanager.controller.organisateur.OrganisateurDashboardController dashboardController;

    public CreateEventController() {
        // Constructeur requis par JavaFX
    }

    @FXML
    private void initialize() {
        // Initialisation des spinners pour l'heure et les minutes
        if (spHour != null) {
            spHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
            spHour.setEditable(true);
        }
        if (spMinute != null) {
            spMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
            spMinute.setEditable(true);
        }
    }

    /**
     * Définit le contrôleur dashboard pour permettre le retour
     */
    public void setDashboardController(com.bschooleventmanager.eventmanager.controller.organisateur.OrganisateurDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    /**
     * Retourne au dashboard
     */
    @FXML
    private void returnToDashboard() {
        if (dashboardController != null) {
            dashboardController.showDashboard();
        }
    }



    @FXML
    private void typeEventChange(){
        var typeEvent = evType.getValue();
        if (typeEvent == null) return;

        // Ajuste visibilité des champs en fonction du type
        if (typeEvent.equals(TypeEvenement.CONCERT.getLabel())){
            //Visibilité TRUE de l'attribut age minmum
            tfAge.setVisible(true); tfAge.setManaged(true); lbtfAge.setVisible(true);lbtfAge.setManaged(true);
            //vISU TRUE Artiste/groupe
            tfArtits.setVisible(true); tfArtits.setManaged(true); lbtfArtits.setVisible(true);lbtfArtits.setManaged(true);
            //vISU TRUE typeConcert
            tyConcert.setVisible(true); tyConcert.setManaged(true); lbtyConcert.setVisible(true);lbtyConcert.setManaged(true);

            //visu False typeSpectacle
            tySpectacle.setVisible(false); tySpectacle.setManaged(false); lbTySpectacle.setVisible(false);lbTySpectacle.setManaged(false);
            //Visibilité False Niveau experience
            nvExpert.setVisible(false); nvExpert.setManaged(false); lbNvExpert.setVisible(false);lbNvExpert.setManaged(false);
            //vISU False Domaine
            Domaine.setVisible(false); Domaine.setManaged(false); lbDomaine.setVisible(false);lbDomaine.setManaged(false);
            //vISU False Intervenants
            Intervenant.setVisible(false); Intervenant.setManaged(false); lbIntervenant.setVisible(false);lbIntervenant.setManaged(false);

        }else if(typeEvent.equals(TypeEvenement.SPECTACLE.getLabel())){
            // Spectacle
            //Visibilité TRUE de l'attribut age minmum
            tfAge.setVisible(true); tfAge.setManaged(true); lbtfAge.setVisible(true);lbtfAge.setManaged(true);
            //Visibilité TRUE de Artiste
            tfArtits.setVisible(true); tfArtits.setManaged(true); lbtfArtits.setVisible(true);lbtfArtits.setManaged(true);
            //Visibilité TRUE TpeSpectacle
            tySpectacle.setVisible(true); tySpectacle.setManaged(true); lbTySpectacle.setVisible(true);lbTySpectacle.setManaged(true);

            //Visibilité False Niveau experience
            nvExpert.setVisible(false); nvExpert.setManaged(false); lbNvExpert.setVisible(false);lbNvExpert.setManaged(false);
            //vISU False Domaine
            Domaine.setVisible(false); Domaine.setManaged(false); lbDomaine.setVisible(false);lbDomaine.setManaged(false);
            //vISU False Intervenants
            Intervenant.setVisible(false); Intervenant.setManaged(false); lbIntervenant.setVisible(false);lbIntervenant.setManaged(false);
            //vISU false typeConcert
            tyConcert.setVisible(false); tyConcert.setManaged(false); lbtyConcert.setVisible(false);lbtyConcert.setManaged(false);
        }else{
            // Conference
            //Visibilité FALSE de l'attribut age minmum
            tfAge.setVisible(false);tfAge.setManaged(false);lbtfAge.setVisible(false);lbtfAge.setManaged(false);
            //vISU false Artiste/groupe
            tfArtits.setVisible(false); tfArtits.setManaged(false); lbtfArtits.setVisible(false);lbtfArtits.setManaged(false);
            //vISU false typeConcert
            tyConcert.setVisible(false); tyConcert.setManaged(false); lbtyConcert.setVisible(false);lbtyConcert.setManaged(false);
            //visu False typeSpectacle
            tySpectacle.setVisible(false); tySpectacle.setManaged(false); lbTySpectacle.setVisible(false);lbTySpectacle.setManaged(false);

            //Visibilité TRUE Niveau experience
            nvExpert.setVisible(true); nvExpert.setManaged(true); lbNvExpert.setVisible(true);lbNvExpert.setManaged(true);
            //vISU TRUE Domaine
            Domaine.setVisible(true); Domaine.setManaged(true); lbDomaine.setVisible(true);lbDomaine.setManaged(true);
            //vISU TRUE Intervenants
            Intervenant.setVisible(true); Intervenant.setManaged(true); lbIntervenant.setVisible(true);lbIntervenant.setManaged(true);
        }
    }

    @FXML
    private void createEvent() throws BusinessException {
        clearAllErrors();

        // Vérifications basiques
        if (isEmpty(tfNom) || dpDate.getValue() == null || isEmpty(tfLieu) || evType.getValue() == null) {
            if (dpDate.getValue() == null) {
                setError(lblErrDate, "La date de l'évènement ne peut pas être vide !");
            } else if (dpDate.getValue().isBefore(LocalDate.now())) {
                setError(lblErrDate, "La date de l'évènement ne peut pas être avant la date du jour !");
            }
            if (isEmpty(tfNom)) setError(lblErrTitre, "Le titre ne peut pas être vide");
            if (isEmpty(tfLieu)) setError(lbErrLieu, "Le lieu de l'évènement ne peut pas être vide");
            if (evType.getValue() == null) setError(lblErrTyEvent, "Le type d'évènement est obligatoire");

            lblError.setText("⚠️ Veuillez remplir tous les champs obligatoires (*) !");
            return;
        }

        if (dpDate.getValue().isBefore(LocalDate.now())) {
            setError(lblErrDate, "La date de l'évènement ne peut pas être avant la date du jour !");
            return;
        }

        // Lecture et parsing des champs numériques avec gestion d'erreur
        Integer nbreStandard = parseInteger(nbPlacesStandard, lblError, "nombre de places standard");
        Integer nbreVip = parseInteger(nbPlacesVip, lblError, "nombre de places VIP");
        Integer nbrePremium = parseInteger(nbPlacesPremium, lblError, "nombre de places Premium");
        Integer prixStand = parseInteger(prixPlaceStandard, lblError, "prix standard");
        Integer prixVip = parseInteger(prixPlaceVip, lblError, "prix VIP");
        Integer prixPremium = parseInteger(prixPlacePremium, lblError, "prix Premium");

        if (nbreStandard == null || nbreVip == null || nbrePremium == null || prixStand == null || prixVip == null || prixPremium == null) {
            // parseInteger a déjà positionné lblError
            return;
        }

        String titre = tfNom.getText();
        String valDescription = Description.getText();
        LocalDate selectedDate = dpDate.getValue();
        int hour = (spHour != null && spHour.getValue() != null) ? spHour.getValue() : 0;
        int minute = (spMinute != null && spMinute.getValue() != null) ? spMinute.getValue() : 0;
        LocalDateTime dateEvent = selectedDate.atTime(hour, minute);
        String lieu = tfLieu.getText();
        String typeEvent = evType.getValue();

        try {
            if (typeEvent.equals(TypeEvenement.CONCERT.getLabel())) {
                if (tyConcert.getValue() == null) {
                    setError(lblErrTypeConcert, "Le type de concert est obligatoire");
                    return;
                }
                if (tfAge.getValue() == null) {
                    setError(lblErrAge, "Veuillez choisir un âge minimum !");
                    return;
                }
                if (isEmpty(tfArtits)) {
                    setError(lblErrArtiste, "Veuillez renseigner l'artiste(s) / groupe en spectacle");
                    return;
                }

                TypeConcert typeConcert = tyConcert.getValue().equals(TypeConcert.LIVE) ? TypeConcert.LIVE : TypeConcert.ACOUSTIQUE;
                int ageMin = tfAge.getValue();
                String artisteGroupe = tfArtits.getText();
                int organisateurId = SessionManager.getUtilisateurConnecte().getIdUtilisateur();

                Concert concert = new Concert(organisateurId, titre, dateEvent, lieu, valDescription, nbreStandard, nbreVip, nbrePremium, BigDecimal.valueOf(prixStand), BigDecimal.valueOf(prixVip), BigDecimal.valueOf(prixPremium), LocalDateTime.now(), artisteGroupe, typeConcert, ageMin);
                EvenementService.creerConcert(concert);
                NotificationUtils.showSuccess("Création du concert réussie !");
                if (dashboardController != null) dashboardController.showDashboard();

            } else if (typeEvent.equals(TypeEvenement.SPECTACLE.getLabel())) {
                if (tySpectacle.getValue() == null) {
                    setError(lblErrTypeSpectacle, "Le type du spectacle est obligatoire");
                    return;
                }
                if (tfAge.getValue() == null) {
                    setError(lblErrAge, "Veuillez choisir un âge minimum !");
                    return;
                }
                if (isEmpty(tfArtits)) {
                    setError(lblErrArtiste, "Veuillez renseigner l'artiste(s) / groupe en spectacle");
                    return;
                }

                TypeSpectacle typeSpect;
                if (tySpectacle.getValue().equals(TypeSpectacle.CIRQUE.getLabel())) typeSpect = TypeSpectacle.CIRQUE;
                else if (tySpectacle.getValue().equals(TypeSpectacle.HUMOUR.getLabel())) typeSpect = TypeSpectacle.HUMOUR;
                else typeSpect = TypeSpectacle.THEATRE;

                int ageMin = tfAge.getValue();
                String artisteGroupe = tfArtits.getText();
                int organisateurId = SessionManager.getUtilisateurConnecte().getIdUtilisateur();

                Spectacle spectacle = new Spectacle(organisateurId, titre, dateEvent, lieu, valDescription, nbreStandard, nbreVip, nbrePremium, BigDecimal.valueOf(prixStand), BigDecimal.valueOf(prixVip), BigDecimal.valueOf(prixPremium), typeSpect, artisteGroupe, ageMin);
                EvenementService.creerSpectacle(spectacle);
                NotificationUtils.showSuccess("Création du spectacle réussie !");
                if (dashboardController != null) dashboardController.showDashboard();

            } else {
                if (nvExpert.getValue() == null) {
                    setError(lblErrNvExpert, "Le Niveau d'expertise est obligatoire");
                    return;
                }
                if (isEmpty(Domaine)) {
                    setError(lblErrDomaine, "Le domaine est obligatoire");
                    return;
                }

                String domaine = Domaine.getText();
                String intervenants = Intervenant.getText();
                NiveauExpertise nivExpert = null;
                if (nvExpert.getValue().equals(NiveauExpertise.DEBUTANT.getLabel())) nivExpert = NiveauExpertise.DEBUTANT;
                if (nvExpert.getValue().equals(NiveauExpertise.INTERMEDIAIRE.getLabel())) nivExpert = NiveauExpertise.INTERMEDIAIRE;
                if (nvExpert.getValue().equals(NiveauExpertise.PROFESSIONNEL.getLabel())) nivExpert = NiveauExpertise.PROFESSIONNEL;

                int organisateurId = SessionManager.getUtilisateurConnecte().getIdUtilisateur();
                Conference conference = new Conference(organisateurId, titre, dateEvent, lieu, TypeEvenement.CONFERENCE, valDescription, nbreStandard, nbreVip, nbrePremium, BigDecimal.valueOf(prixStand), BigDecimal.valueOf(prixVip), BigDecimal.valueOf(prixPremium), intervenants, domaine, nivExpert);
                EvenementService.creerConference(conference);
                NotificationUtils.showSuccess("Création de la conférence réussie !");
                if (dashboardController != null) dashboardController.showDashboard();
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'évènement", e);
            NotificationUtils.showError("Une erreur est survenue lors de la création de l'évènement. Vérifiez les champs et réessayez.");
        }
    }

    /* ------------------ Helpers ------------------ */
    private void clearAllErrors() {
        lblError.setText("");
        Label[] labels = {lblErrDate, lblErrTitre, lbErrLieu, lblErrTyEvent, lblErrTypeConcert, lblErrTypeSpectacle, lblErrNvExpert, lblErrDomaine, lblErrAge, lblErrArtiste};
        for (Label l : labels) if (l != null) { l.setText(""); l.setManaged(false); }
    }

    private void setError(Label label, String message) {
        if (label == null) return;
        label.setManaged(true);
        label.setText(message);
    }

    private Integer parseInteger(TextField field, Label globalErrorLabel, String fieldName) {
        if (field == null) return null;
        try {
            String txt = field.getText();
            if (txt == null || txt.isBlank()) {
                globalErrorLabel.setText("Le champ '" + fieldName + "' est requis.");
                return null;
            }
            return Integer.parseInt(txt.trim());
        } catch (NumberFormatException _) {
            globalErrorLabel.setText("Le champ '" + fieldName + "' doit être un nombre valide.");
            return null;
        }
    }

    private boolean isEmpty(TextField f) { return f == null || f.getText() == null || f.getText().isBlank(); }

}
