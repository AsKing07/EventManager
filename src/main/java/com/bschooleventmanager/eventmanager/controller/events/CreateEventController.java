package com.bschooleventmanager.eventmanager.controller.events;

import com.bschooleventmanager.eventmanager.exception.BusinessException;
import com.bschooleventmanager.eventmanager.model.*;
import com.bschooleventmanager.eventmanager.model.enums.NiveauExpertise;
import com.bschooleventmanager.eventmanager.model.enums.TypeConcert;
import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;
import com.bschooleventmanager.eventmanager.model.enums.TypeSpectacle;
import com.bschooleventmanager.eventmanager.service.EvenementService;
import com.bschooleventmanager.eventmanager.util.NotificationUtils;
import com.bschooleventmanager.eventmanager.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreateEventController {
    @FXML private Label tfIdEvent,lbtfAge, lbtfArtits, lbtyConcert, lbTySpectacle, lbNvExpert, lbDomaine, lbIntervenant;
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

    //Initialisation du service Evenement
    private EvenementService evenementService = new EvenementService();

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
    private void returnToDashboard2() {
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

    /**
     *
     * @param idEvent
     * @throws BusinessException
     * @Author Loic Vanel
     *
     */
    @FXML
    public void suppEvent(int idEvent) throws BusinessException {
        try {
            logger.info("Fonction suppEvent dans le controller CreateEventController");
            evenementService.suppEvent(idEvent);
        } catch (Exception e) {
            logger.error("Erreur dans la fonction suppEvent du controller CreateEventController", e);
        }

    }

    public Evenement findEvent(int idEvent) throws BusinessException {
        try {
            logger.info("Fonction findEvent dans le controller CreateEventController");
            Evenement event = evenementService.getEvenement(idEvent);
            return event;
        } catch (Exception e) {
            logger.error("Erreur dans la fonction findEvent du controller CreateEventController", e);
            throw new BusinessException("Erreur récupération événement", e);
        }
    }

    /**
     * Création de l'évènement en fonction du type sélectionné
     * @Author Loic Vanel & Charbel SONON
     * @throws BusinessException
     */
    @FXML
    private void createEvent() throws BusinessException {
        clearAllErrors();
        // Validation du formulaire (Champs basiques)
        Boolean valid = validateForm();
        if (!valid) return;

        //Chargement des valeurs communes a tous les types d'évènements
        EventBaseData baseData = loadCommonValues();
        if (baseData == null) {
            logger.info("loadCommonValues(creation event) a déjà positionné lblError");
            return;
        }
        // Création de l'évènement en fonction du type
        try {
            if (baseData.typeEvent.equals(TypeEvenement.CONCERT.getLabel())) {
                createConcert(1,baseData);
                NotificationUtils.showSuccess("Création du concert réussie !");
                if (dashboardController != null) dashboardController.showDashboard();

            } else if (baseData.typeEvent.equals(TypeEvenement.SPECTACLE.getLabel())) {
                createSpectacle(1,baseData);
                NotificationUtils.showSuccess("Création du spectacle réussie !");
                if (dashboardController != null) dashboardController.showDashboard();

            } else {
                createConference(1,baseData);
                NotificationUtils.showSuccess("Création de la conférence réussie !");
                if (dashboardController != null) dashboardController.showDashboard();
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'évènement", e);
            NotificationUtils.showError("Une erreur est survenue lors de la création de l'évènement. Vérifiez les champs et réessayez.");
        }
    }

    @FXML
    /**
     * Modification de l'évènement en fonction du type sélectionné
     * @Author Loic Vanel
     * @throws BusinessException
     */
    private void modificationEvent() throws BusinessException {
        clearAllErrors();
        // Validation du formulaire (Champs basiques)
        Boolean valid = validateForm();
        if (!valid) return;

        //Chargement des valeurs communes a tous les types d'évènements
        EventBaseData baseData = loadCommonValues();
        if (baseData == null) {
            logger.info("loadCommonValues a déjà positionné lblError");
            return;
        }
        // Création de l'évènement en fonction du type
        try {
            if (baseData.getTypeEvent().equals(TypeEvenement.CONCERT.getLabel())) {
                createConcert(2,baseData);
                //NotificationUtils.showSuccess("Modification du concert réussie !");
                if (dashboardController != null) dashboardController.showDashboard();

            } else if (baseData.getTypeEvent().equals(TypeEvenement.SPECTACLE.getLabel())) {
                createSpectacle(2,baseData);
                //NotificationUtils.showSuccess("Création du spectacle réussie !");
                if (dashboardController != null) dashboardController.showDashboard();

            } else if( baseData.getTypeEvent().equals(TypeEvenement.CONFERENCE.getLabel())) {
                createConference(2,baseData);
                //NotificationUtils.showSuccess("Création de la conférence réussie !");
                if (dashboardController != null) dashboardController.showDashboard();
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la MODIFICATION de l'évènement", e);
            //NotificationUtils.showError("Une erreur est survenue lors de la MODIFICATION de l'évènement. Vérifiez les champs et réessayez.");
        }
    }


    /**
     * Création d'un concert
     * @param baseData
     * @param create_or_modif pour savoir si on est en création ou modification (1 = création, 2 = modification)
     * @Author Loic Vanel
     * @throws BusinessException
     */
    private  void createConcert(int create_or_modif, EventBaseData baseData) throws BusinessException {
        // Implémentation de la création de concert
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

        Concert concert = new Concert(organisateurId, baseData.titre, baseData.dateEvent, baseData.lieu, baseData.description, baseData.nbreStandard, baseData.nbreVip, baseData.nbrePremium, BigDecimal.valueOf(baseData.prixStand), BigDecimal.valueOf(baseData.prixVip), BigDecimal.valueOf(baseData.prixPremium), LocalDateTime.now(), artisteGroupe, typeConcert, ageMin);
        if (create_or_modif == 2) {
            int idConcert = Integer.parseInt(tfIdEvent.getText());
            concert.setIdEvenement(idConcert);
            evenementService.modifierConcert(concert);
        } else if (create_or_modif == 1) {
            evenementService.creerConcert(concert);
        }
    }

    /**
     * Création d'un spectacle
     * @param baseData
     * @param create_or_modif pour savoir si on est en création ou modification du spectacle (1 = création, 2 = modification)
     * @Author Loic Vanel
     * @throws BusinessException
     */
    private void createSpectacle(int create_or_modif,EventBaseData baseData) throws BusinessException {
        // Implémentation de la création de spectacle
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

        Spectacle spectacle = new Spectacle(organisateurId, baseData.titre, baseData.dateEvent, baseData.lieu, baseData.description, baseData.nbreStandard, baseData.nbreVip, baseData.nbrePremium, BigDecimal.valueOf(baseData.prixStand), BigDecimal.valueOf(baseData.prixVip), BigDecimal.valueOf(baseData.prixPremium), typeSpect, artisteGroupe, ageMin);
        if (create_or_modif == 2) {
            int idEvent = Integer.parseInt(tfIdEvent.getText());
            spectacle.setIdEvenement(idEvent);
            evenementService.modifierSpectacle(spectacle);
        } else if (create_or_modif == 1) {
            evenementService.creerSpectacle(spectacle);
        }
        //evenementService.creerSpectacle(spectacle);
    }

    public void initializeFormForEdit(EventTotal event) {
        try {
            tfIdEvent.setText(String.valueOf(event.getIdEvenement()));
            tfNom.setText(event.getNom());
            dpDate.setValue(event.getDateEvenement().toLocalDate());
            spHour.getValueFactory().setValue(event.getDateEvenement().getHour());
            spMinute.getValueFactory().setValue(event.getDateEvenement().getMinute());
            tfLieu.setText(event.getLieu());
            evType.setValue(event.getTypeEvenement().getLabel());
            Description.setText(event.getDescription());
            nbPlacesStandard.setText(String.valueOf(event.getPlacesStandardDisponibles()));
            nbPlacesVip.setText(String.valueOf(event.getPlacesVipDisponibles()));
            nbPlacesPremium.setText(String.valueOf(event.getPlacesPremiumDisponibles()));
            prixPlaceStandard.setText(String.valueOf(event.getPrixStandard().intValue()));
            prixPlaceVip.setText(String.valueOf(event.getPrixVip().intValue()));
            prixPlacePremium.setText(String.valueOf(event.getPrixPremium().intValue()));

            // Initialisation des champs spécifiques en fonction du type d'évènement
            if (event.getTypeEvenement().equals(TypeEvenement.CONCERT)) {
                tfAge.setValue(event.getAgeMin());
                tfArtits.setText(event.getArtisteGroupe());
                tyConcert.setValue(event.getTypeConcert().getLabel());
            } else if (event.getTypeEvenement().equals(TypeEvenement.SPECTACLE)) {
                tfAge.setValue(event.getAgeMin());
                tfArtits.setText(event.getArtisteGroupe());
                tySpectacle.setValue(event.getTypeSpectacle().getLabel());
            } else if (event.getTypeEvenement().equals(TypeEvenement.CONFERENCE)) {
                Domaine.setText(event.getDomaine());
                Intervenant.setText(event.getIntervenant());
                nvExpert.setValue(event.getNiveauExpertise().getLabel());
            }

            // Met à jour la visibilité des champs en fonction du type d'évènement
            if (event.getTypeEvenement().equals(TypeEvenement.CONCERT)) {
                //Remplir les champs spécifiques au concert et afficher les champs
                tyConcert.setManaged(true);
                tyConcert.setVisible(true);
                lbtyConcert.setVisible(true);
                lbtyConcert.setManaged(true);
                tfAge.setManaged(true);
                tfAge.setVisible(true);
                lbtfAge.setManaged(true);
                lbtfAge.setVisible(true);
                tfArtits.setManaged(true);
                tfArtits.setVisible(true);
                lbtfArtits.setManaged(true);
                lbtfArtits.setVisible(true);
            }
            else if (event.getTypeEvenement().equals(TypeEvenement.SPECTACLE)) {
                //Remplir les champs spécifiques au spectacle et afficher les champs
                tySpectacle.setManaged(true);
                tySpectacle.setVisible(true);
                lbTySpectacle.setVisible(true);
                lbTySpectacle.setManaged(true);
                tfAge.setManaged(true);
                tfAge.setVisible(true);
                lbtfAge.setManaged(true);
                lbtfAge.setVisible(true);
                tfAge.setValue(event.getAgeMin());
                tfArtits.setManaged(true);
                tfArtits.setVisible(true);
                lbtfArtits.setManaged(true);
                lbtfArtits.setVisible(true);
                tfArtits.setText(event.getArtisteGroupe());
            }
            else if (event.getTypeEvenement().equals(TypeEvenement.CONFERENCE)) {
                //Remplir les champs spécifiques à la conférence et afficher les champs
                Domaine.setManaged(true);
                Domaine.setVisible(true);
                lbDomaine.setManaged(true);
                lbDomaine.setVisible(true);
                Intervenant.setManaged(true);
                Intervenant.setVisible(true);
                lbIntervenant.setManaged(true);
                lbIntervenant.setVisible(true);
                lbNvExpert.setManaged(true);
                lbNvExpert.setVisible(true);
                nvExpert.setManaged(true);
                nvExpert.setVisible(true);
            }

        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation du formulaire pour la modification de l'évènement", e);
        }
    }

    /**
     * Création d'une conférence
     * @param create_or_modif pour savoir si on est en création ou modification de la conférence (1 = création, 2 = modification)
     * @param baseData
     * @Author Loic Vanel
     * @throws BusinessException
     */
    private void createConference(int create_or_modif,EventBaseData baseData) throws BusinessException {
        // Implémentation de la création de conférence
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
        Conference conference = new Conference(organisateurId, baseData.titre, baseData.dateEvent, baseData.lieu, TypeEvenement.CONFERENCE, baseData.description, baseData.nbreStandard, baseData.nbreVip, baseData.nbrePremium, BigDecimal.valueOf(baseData.prixStand), BigDecimal.valueOf(baseData.prixVip), BigDecimal.valueOf(baseData.prixPremium), intervenants, domaine, nivExpert);
        if (create_or_modif == 2) {
            int idEvent = Integer.parseInt(tfIdEvent.getText());
            conference.setIdEvenement(idEvent);
            evenementService.modifierConference(conference);
        } else if (create_or_modif == 1) {
            evenementService.creerConference(conference);
        }
        //evenementService.creerConference(conference);
    }

    /** Charge les valeurs communes à tous les types d'évènements
     * @return EventBaseData contenant les valeurs communes, ou null en cas d'erreur de parsing
     * @Author Loic Vanel
     */
    private EventBaseData loadCommonValues() {
        // Lecture et parsing des champs numériques avec gestion d'erreur
        Integer nbreStandard = parseInteger(nbPlacesStandard, lblError, "nombre de places standard");
        Integer nbreVip = parseInteger(nbPlacesVip, lblError, "nombre de places VIP");
        Integer nbrePremium = parseInteger(nbPlacesPremium, lblError, "nombre de places Premium");
        Integer prixStand = parseInteger(prixPlaceStandard, lblError, "prix standard");
        Integer prixVip = parseInteger(prixPlaceVip, lblError, "prix VIP");
        Integer prixPremium = parseInteger(prixPlacePremium, lblError, "prix Premium");

        if (nbreStandard == null || nbreVip == null || nbrePremium == null || prixStand == null || prixVip == null || prixPremium == null) {
            // parseInteger a déjà positionné lblError
            logger.error("Erreur de parsing des champs numériques lors de la création d'un évènement (LES PRIX ET NOMBRE DE PLACES PAR CATEGORIE SONT OBLIGATOIRES)");
            return null;
        }
        String titre = tfNom.getText();
        String valDescription = Description.getText();
        LocalDate selectedDate = dpDate.getValue();
        int hour = (spHour != null && spHour.getValue() != null) ? spHour.getValue() : 0;
        int minute = (spMinute != null && spMinute.getValue() != null) ? spMinute.getValue() : 0;
        LocalDateTime dateEvent = selectedDate.atTime(hour, minute);
        String lieu = tfLieu.getText();
        String typeEvent = evType.getValue();

        return new EventBaseData(titre, valDescription, dateEvent, lieu, typeEvent, nbreStandard, nbreVip, nbrePremium, prixStand, prixVip, prixPremium);
    }


    /** Valide les champs du formulaire commun à de tout type d'évènement  avant la soumission
     * @return true si le formulaire est valide, false sinon
     * @Author Loic Vanel
     */
    private boolean validateForm(){
        // Implémenter les validations de formulaire ici si nécessaire
        clearAllErrors();

        if (isEmpty(tfNom) || dpDate.getValue() == null || isEmpty(tfLieu) || evType.getValue() == null) {
            if (dpDate.getValue() == null) {
                setError(lblErrDate, "La date de l'évènement ne peut pas être vide !");
            } else if (dpDate.getValue().isBefore(LocalDate.now())) {
                setError(lblErrDate, "La date de l'évènement ne peut pas être avant la date du jour !");
            }
            if (isEmpty(tfNom)) setError(lblErrTitre, "Le titre ne peut pas être vide");
            if (isEmpty(tfLieu)) setError(lbErrLieu, "Le lieu ne peut pas être vide");
            if (evType.getValue() == null) setError(lblErrTyEvent, "Le type est obligatoire");
            lblError.setText("⚠️ Veuillez remplir tous les champs obligatoires (*) !");
            return false;
        }

        if (dpDate.getValue().isBefore(LocalDate.now())) {
            setError(lblErrDate, "La date ne peut pas être avant aujourd'hui !");
            return false;
        }

        return true;
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
