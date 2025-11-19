package com.bschooleventmanager.eventmanager.controller.events;

import com.bschooleventmanager.eventmanager.exception.BusinessException;
import com.bschooleventmanager.eventmanager.model.Concert;
import com.bschooleventmanager.eventmanager.model.Conference;
import com.bschooleventmanager.eventmanager.model.EventBaseData;
import com.bschooleventmanager.eventmanager.model.Spectacle;
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
    @FXML private Label lbtfAge, lbtfArtits, lbtyConcert, lbTySpectacle, lbNvExpert, lbDomaine, lbIntervenant;
    @FXML private Label lblErrDate, lblErrTitre, lbErrLieu, lblErrTyEvent, lblErrTypeConcert, lblErrTypeSpectacle, lblErrNvExpert, lblErrDomaine, lblErrAge, lblErrArtiste;
    @FXML private TextField tfNom, tfLieu, prixPlaceStandard, nbPlacesVip, prixPlaceVip, nbPlacesPremium, prixPlacePremium, nbPlacesStandard, tfArtits, Domaine, Intervenant, tfIdEvent;
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
        
        // Chargement dynamique des ComboBox depuis les enums
        loadComboBoxValues();
        
        // Configuration de la validation numérique pour les champs places et prix
        setupNumericValidation();
    }

    /**
     * Charge dynamiquement les valeurs des ComboBox depuis les enums
     */
    private void loadComboBoxValues() {
        logger.info("Chargement dynamique des valeurs des ComboBox depuis les enums");
        
        // Charger les types d'événements
        if (evType != null) {
            evType.getItems().clear();
            for (TypeEvenement type : TypeEvenement.values()) {
                evType.getItems().add(type.getLabel());
            }
        }
        
        // Charger les types de concert
        if (tyConcert != null) {
            tyConcert.getItems().clear();
            for (TypeConcert type : TypeConcert.values()) {
                tyConcert.getItems().add(type.getLabel());
            }
        }
        
        // Charger les types de spectacle
        if (tySpectacle != null) {
            tySpectacle.getItems().clear();
            for (TypeSpectacle type : TypeSpectacle.values()) {
                tySpectacle.getItems().add(type.getLabel());
            }
        }
        
        // Charger les niveaux d'expertise
        if (nvExpert != null) {
            nvExpert.getItems().clear();
            for (NiveauExpertise niveau : NiveauExpertise.values()) {
                nvExpert.getItems().add(niveau.getLabel());
            }
        }
        
        // Charger les âges (pour les concerts et spectacles)
        if (tfAge != null) {
            tfAge.getItems().clear();
            for (int age = 5; age <= 18; age++) {
                tfAge.getItems().add(age);
            }
        }
        
        logger.info("Valeurs des ComboBox chargées avec succès");
    }

    /**
     * Configure la validation numérique pour les champs places et prix
     */
    private void setupNumericValidation() {
        logger.info("Configuration de la validation numérique des champs");
        
        // Validation pour les champs de places (entiers uniquement)
        setupIntegerField(nbPlacesStandard);
        setupIntegerField(nbPlacesVip);
        setupIntegerField(nbPlacesPremium);
        
        // Validation pour les champs de prix (décimaux)
        setupDecimalField(prixPlaceStandard);
        setupDecimalField(prixPlaceVip);
        setupDecimalField(prixPlacePremium);
        
        logger.info("Validation numérique configurée avec succès");
    }
    
    /**
     * Configure un champ TextField pour accepter uniquement des entiers
     */
    private void setupIntegerField(TextField field) {
        if (field != null) {
            field.setTextFormatter(new TextFormatter<>(change -> {
                String newText = change.getControlNewText();
                if (newText.matches("\\d*")) { // Accepte uniquement les chiffres
                    return change;
                }
                return null; // Rejette la modification
            }));
        }
    }
    
    /**
     * Configure un champ TextField pour accepter uniquement des décimaux
     */
    private void setupDecimalField(TextField field) {
        if (field != null) {
            field.setTextFormatter(new TextFormatter<>(change -> {
                String newText = change.getControlNewText();
                if (newText.matches("\\d*\\.?\\d{0,2}")) { // Accepte les décimaux avec max 2 décimales
                    return change;
                }
                return null; // Rejette la modification
            }));
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
    /**
     * Création de l'évènement en fonction du type sélectionné
     * @Author Loic Vanel & Charbel SONON - Amélioré
     * @throws BusinessException
     */
    @FXML
    private void createEvent() throws BusinessException {
        logger.info("Début de la création d'un événement");
        clearAllErrors();
        
        // Validation complète du formulaire
        if (!validateForm()) {
            logger.warn("Validation du formulaire échouée, création annulée");
            return;
        }

        //Chargement des valeurs communes a tous les types d'évènements
        EventBaseData baseData = loadCommonValues();
        if (baseData == null) {
            logger.error("Impossible de charger les données communes de l'événement");
            lblError.setText("❌ Erreur dans les données de l'événement. Vérifiez tous les champs.");
            return;
        }
        
        // Validation finale avant création
        if (!performFinalValidation(baseData)) {
            return;
        }
        
        // Création de l'évènement en fonction du type
        try {
            logger.info("Création de l'événement de type: {}", baseData.typeEvent);
            
            if (baseData.typeEvent.equals(TypeEvenement.CONCERT.getLabel())) {
                createConcert(1, baseData);
                NotificationUtils.showSuccess("✅ Concert créé avec succès !");
                logger.info("Concert créé avec succès: {}", baseData.titre);
                
            } else if (baseData.typeEvent.equals(TypeEvenement.SPECTACLE.getLabel())) {
                createSpectacle(1, baseData);
                NotificationUtils.showSuccess("✅ Spectacle créé avec succès !");
                logger.info("Spectacle créé avec succès: {}", baseData.titre);
                
            } else if (baseData.typeEvent.equals(TypeEvenement.CONFERENCE.getLabel())) {
                createConference(1, baseData);
                NotificationUtils.showSuccess("✅ Conférence créée avec succès !");
                logger.info("Conférence créée avec succès: {}", baseData.titre);
            } else {
                logger.error("Type d'événement non reconnu: {}", baseData.typeEvent);
                lblError.setText("❌ Type d'événement non valide.");
                return;
            }
            
            // Retour au dashboard après création réussie
            if (dashboardController != null) {
                dashboardController.showDashboard();
            }
            
        } catch (BusinessException e) {
            logger.error("Erreur métier lors de la création de l'événement", e);
            lblError.setText("❌ " + e.getMessage());
            NotificationUtils.showError("Erreur lors de la création : " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur technique lors de la création de l'événement", e);
            lblError.setText("❌ Erreur technique lors de la création. Réessayez.");
            NotificationUtils.showError("Une erreur technique est survenue. Veuillez réessayer.");
        }
    }
    
    /**
     * Effectue une validation finale avant la création
     */
    private boolean performFinalValidation(EventBaseData baseData) {
        // Vérification de cohérence des prix (VIP >= Standard, Premium >= VIP)
        if (baseData.prixVip < baseData.prixStand) {
            lblError.setText("❌ Le prix VIP doit être supérieur ou égal au prix Standard");
            return false;
        }
        
        if (baseData.prixPremium < baseData.prixVip) {
            lblError.setText("❌ Le prix Premium doit être supérieur ou égal au prix VIP");
            return false;
        }
        
        // Vérification que l'événement a au moins une place
        int totalPlaces = baseData.nbreStandard + baseData.nbreVip + baseData.nbrePremium;
        if (totalPlaces == 0) {
            lblError.setText("❌ L'événement doit avoir au moins une place disponible");
            return false;
        }
        
        // Vérification de l'heure pour les événements d'aujourd'hui
        if (baseData.dateEvent.toLocalDate().isEqual(LocalDate.now())) {
            if (baseData.dateEvent.isBefore(LocalDateTime.now().plusHours(1))) {
                lblError.setText("❌ L'événement doit être prévu au minimum 1 heure à l'avance");
                return false;
            }
        }
        
        return true;
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
            // Création du concert
            evenementService.modifierConcert(concert);
            logger.info("Concert créé avec succès: {}", baseData.titre);
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
    private void createSpectacle(int createOrModif, EventBaseData baseData) throws BusinessException {
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
        if (createOrModif == 2) {
            int idEvent = Integer.parseInt(tfIdEvent.getText());
            spectacle.setIdEvenement(idEvent);
            evenementService.modifierSpectacle(spectacle);
            logger.info("Spectacle modifié avec succès: {}", baseData.titre);
        } else if (createOrModif == 1) {
            evenementService.creerSpectacle(spectacle);
            logger.info("Spectacle créé avec succès: {}", baseData.titre);
        }
    }

    /**
     * Création d'une conférence
     * @param create_or_modif pour savoir si on est en création ou modification de la conférence (1 = création, 2 = modification)
     * @param baseData
     * @Author Loic Vanel
     * @throws BusinessException
     */
    private void createConference(int createOrModif, EventBaseData baseData) throws BusinessException {
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
        if (createOrModif == 2) {
            int idEvent = Integer.parseInt(tfIdEvent.getText());
            conference.setIdEvenement(idEvent);
            evenementService.modifierConference(conference);
            logger.info("Conférence modifiée avec succès: {}", baseData.titre);
        } else if (createOrModif == 1) {
            evenementService.creerConference(conference);
            logger.info("Conférence créée avec succès: {}", baseData.titre);
        }
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
     * @Author Loic Vanel - Amélioré
     */
    private boolean validateForm(){
        logger.info("Début de la validation du formulaire");
        clearAllErrors();
        boolean isValid = true;

        // 1. Validation des champs obligatoires de base
        isValid = validateBasicFields() && isValid;
        
        // 2. Validation de la date
        isValid = validateDate() && isValid;
        
        // 3. Validation des champs numériques (places et prix)
        isValid = validateNumericFields() && isValid;
        
        // 4. Validation des champs spécifiques selon le type d'événement
        isValid = validateTypeSpecificFields() && isValid;

        // Message d'erreur global si le formulaire n'est pas valide
        if (!isValid) {
            lblError.setText("⚠️ Veuillez corriger les erreurs ci-dessus avant de continuer !");
            logger.warn("Validation du formulaire échouée");
        } else {
            logger.info("Validation du formulaire réussie");
        }

        return isValid;
    }
    
    /**
     * Valide les champs de base obligatoires
     */
    private boolean validateBasicFields() {
        boolean isValid = true;
        
        // Nom/Titre
        if (isEmpty(tfNom)) {
            setError(lblErrTitre, "Le nom de l'événement est obligatoire");
            isValid = false;
        } else if (tfNom.getText().trim().length() < 3) {
            setError(lblErrTitre, "Le nom doit contenir au moins 3 caractères");
            isValid = false;
        } else if (tfNom.getText().trim().length() > 100) {
            setError(lblErrTitre, "Le nom ne peut pas dépasser 100 caractères");
            isValid = false;
        }
        
        // Lieu
        if (isEmpty(tfLieu)) {
            setError(lbErrLieu, "Le lieu est obligatoire");
            isValid = false;
        } else if (tfLieu.getText().trim().length() < 2) {
            setError(lbErrLieu, "Le lieu doit contenir au moins 2 caractères");
            isValid = false;
        } else if (tfLieu.getText().trim().length() > 150) {
            setError(lbErrLieu, "Le lieu ne peut pas dépasser 150 caractères");
            isValid = false;
        }
        
        // Type d'événement
        if (evType.getValue() == null || evType.getValue().isBlank()) {
            setError(lblErrTyEvent, "Le type d'événement est obligatoire");
            isValid = false;
        }
        
        // Description (optionnelle mais avec limite)
        if (Description.getText() != null && Description.getText().length() > 1000) {
            lblError.setText("La description ne peut pas dépasser 1000 caractères");
            isValid = false;
        }
        
        return isValid;
    }
    
    /**
     * Valide la date et l'heure
     */
    private boolean validateDate() {
        boolean isValid = true;
        
        if (dpDate.getValue() == null) {
            setError(lblErrDate, "La date de l'événement est obligatoire");
            isValid = false;
        } else {
            LocalDate selectedDate = dpDate.getValue();
            LocalDate today = LocalDate.now();
            
            // Vérifier que la date n'est pas dans le passé
            if (selectedDate.isBefore(today)) {
                setError(lblErrDate, "La date ne peut pas être antérieure à aujourd'hui");
                isValid = false;
            }
            // Vérifier que la date n'est pas trop loin dans le futur (2 ans max)
            else if (selectedDate.isAfter(today.plusYears(2))) {
                setError(lblErrDate, "La date ne peut pas être plus de 2 ans dans le futur");
                isValid = false;
            }
            // Vérifier l'heure si c'est aujourd'hui
            else if (selectedDate.isEqual(today)) {
                int currentHour = java.time.LocalTime.now().getHour();
                int selectedHour = (spHour != null && spHour.getValue() != null) ? spHour.getValue() : 0;
                
                if (selectedHour <= currentHour) {
                    setError(lblErrDate, "Pour aujourd'hui, l'heure doit être ultérieure à maintenant");
                    isValid = false;
                }
            }
        }
        
        return isValid;
    }
    
    /**
     * Valide les champs numériques (places et prix)
     */
    private boolean validateNumericFields() {
        boolean isValid = true;
        
        // Validation des places
        isValid = validatePlacesField(nbPlacesStandard, "Nombre de places standard") && isValid;
        isValid = validatePlacesField(nbPlacesVip, "Nombre de places VIP") && isValid;
        isValid = validatePlacesField(nbPlacesPremium, "Nombre de places Premium") && isValid;
        
        // Validation des prix
        isValid = validatePriceField(prixPlaceStandard, "Prix places standard") && isValid;
        isValid = validatePriceField(prixPlaceVip, "Prix places VIP") && isValid;
        isValid = validatePriceField(prixPlacePremium, "Prix places Premium") && isValid;
        
        // Vérification qu'au moins une catégorie de places est disponible
        if (isValid) {
            try {
                int totalPlaces = Integer.parseInt(nbPlacesStandard.getText().trim()) +
                                 Integer.parseInt(nbPlacesVip.getText().trim()) +
                                 Integer.parseInt(nbPlacesPremium.getText().trim());
                
                if (totalPlaces <= 0) {
                    lblError.setText("L'événement doit avoir au moins une place disponible");
                    isValid = false;
                } else if (totalPlaces > 100000) {
                    lblError.setText("Le nombre total de places ne peut pas dépasser 100,000");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                lblError.setText("Erreur dans les valeurs numériques des places");
                isValid = false;
            }
        }
        
        return isValid;
    }
    
    /**
     * Valide un champ de places
     */
    private boolean validatePlacesField(TextField field, String fieldName) {
        if (field == null) return true;
        
        String value = field.getText().trim();
        if (value.isEmpty()) {
            lblError.setText(fieldName + " ne peut pas être vide");
            return false;
        }
        
        try {
            int places = Integer.parseInt(value);
            if (places < 0) {
                lblError.setText(fieldName + " ne peut pas être négatif");
                return false;
            }
            if (places > 50000) {
                lblError.setText(fieldName + " ne peut pas dépasser 50,000 par catégorie");
                return false;
            }
        } catch (NumberFormatException e) {
            lblError.setText(fieldName + " doit être un nombre entier valide");
            return false;
        }
        
        return true;
    }
    
    /**
     * Valide un champ de prix
     */
    private boolean validatePriceField(TextField field, String fieldName) {
        if (field == null) return true;
        
        String value = field.getText().trim();
        if (value.isEmpty()) {
            lblError.setText(fieldName + " ne peut pas être vide");
            return false;
        }
        
        try {
            double price = Double.parseDouble(value);
            if (price < 0) {
                lblError.setText(fieldName + " ne peut pas être négatif");
                return false;
            }
            if (price > 10000) {
                lblError.setText(fieldName + " ne peut pas dépasser 10,000€ par place");
                return false;
            }
            // Vérifier qu'il n'y a pas plus de 2 décimales
            if (value.contains(".") && value.substring(value.indexOf(".") + 1).length() > 2) {
                lblError.setText(fieldName + " ne peut avoir que 2 décimales maximum");
                return false;
            }
        } catch (NumberFormatException e) {
            lblError.setText(fieldName + " doit être un prix valide (ex: 25.50)");
            return false;
        }
        
        return true;
    }
    
    /**
     * Valide les champs spécifiques selon le type d'événement
     */
    private boolean validateTypeSpecificFields() {
        if (evType.getValue() == null) return true; // Déjà géré dans validateBasicFields
        
        String typeEvent = evType.getValue();
        boolean isValid = true;
        
        if (typeEvent.equals(TypeEvenement.CONCERT.getLabel())) {
            isValid = validateConcertFields() && isValid;
        } else if (typeEvent.equals(TypeEvenement.SPECTACLE.getLabel())) {
            isValid = validateSpectacleFields() && isValid;
        } else if (typeEvent.equals(TypeEvenement.CONFERENCE.getLabel())) {
            isValid = validateConferenceFields() && isValid;
        }
        
        return isValid;
    }
    
    /**
     * Valide les champs spécifiques aux concerts
     */
    private boolean validateConcertFields() {
        boolean isValid = true;
        
        // Type de concert
        if (tyConcert.getValue() == null || tyConcert.getValue().isBlank()) {
            setError(lblErrTypeConcert, "Le type de concert est obligatoire");
            isValid = false;
        }
        
        // Âge minimum
        if (tfAge.getValue() == null) {
            setError(lblErrAge, "L'âge minimum est obligatoire pour les concerts");
            isValid = false;
        }
        
        // Artiste/Groupe
        if (isEmpty(tfArtits)) {
            setError(lblErrArtiste, "Le nom de l'artiste ou du groupe est obligatoire");
            isValid = false;
        } else if (tfArtits.getText().trim().length() < 2) {
            setError(lblErrArtiste, "Le nom de l'artiste doit contenir au moins 2 caractères");
            isValid = false;
        } else if (tfArtits.getText().trim().length() > 100) {
            setError(lblErrArtiste, "Le nom de l'artiste ne peut pas dépasser 100 caractères");
            isValid = false;
        }
        
        return isValid;
    }
    
    /**
     * Valide les champs spécifiques aux spectacles
     */
    private boolean validateSpectacleFields() {
        boolean isValid = true;
        
        // Type de spectacle
        if (tySpectacle.getValue() == null || tySpectacle.getValue().isBlank()) {
            setError(lblErrTypeSpectacle, "Le type de spectacle est obligatoire");
            isValid = false;
        }
        
        // Âge minimum
        if (tfAge.getValue() == null) {
            setError(lblErrAge, "L'âge minimum est obligatoire pour les spectacles");
            isValid = false;
        }
        
        // Artiste/Groupe
        if (isEmpty(tfArtits)) {
            setError(lblErrArtiste, "Le nom de l'artiste ou de la troupe est obligatoire");
            isValid = false;
        } else if (tfArtits.getText().trim().length() < 2) {
            setError(lblErrArtiste, "Le nom de l'artiste doit contenir au moins 2 caractères");
            isValid = false;
        } else if (tfArtits.getText().trim().length() > 100) {
            setError(lblErrArtiste, "Le nom de l'artiste ne peut pas dépasser 100 caractères");
            isValid = false;
        }
        
        return isValid;
    }
    
    /**
     * Valide les champs spécifiques aux conférences
     */
    private boolean validateConferenceFields() {
        boolean isValid = true;
        
        // Niveau d'expertise
        if (nvExpert.getValue() == null || nvExpert.getValue().isBlank()) {
            setError(lblErrNvExpert, "Le niveau d'expertise est obligatoire");
            isValid = false;
        }
        
        // Domaine
        if (isEmpty(Domaine)) {
            setError(lblErrDomaine, "Le domaine est obligatoire pour les conférences");
            isValid = false;
        } else if (Domaine.getText().trim().length() < 2) {
            setError(lblErrDomaine, "Le domaine doit contenir au moins 2 caractères");
            isValid = false;
        } else if (Domaine.getText().trim().length() > 100) {
            setError(lblErrDomaine, "Le domaine ne peut pas dépasser 100 caractères");
            isValid = false;
        }
        
        // Intervenant (optionnel mais avec validation si renseigné)
        if (Intervenant.getText() != null && !Intervenant.getText().trim().isEmpty()) {
            if (Intervenant.getText().trim().length() > 200) {
                lblError.setText("Le nom de l'intervenant ne peut pas dépasser 200 caractères");
                isValid = false;
            }
        }
        
        return isValid;
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
