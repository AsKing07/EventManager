package com.bschooleventmanager.eventmanager.controller.events;

import com.bschooleventmanager.eventmanager.exception.BusinessException;
import com.bschooleventmanager.eventmanager.model.*;
import com.bschooleventmanager.eventmanager.model.enums.*;
import com.bschooleventmanager.eventmanager.service.EvenementService;
import com.bschooleventmanager.eventmanager.util.NotificationUtils;
import com.bschooleventmanager.eventmanager.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Contrôleur pour la modification d'événements
 */
public class ModifyEventController {
    
    private static final Logger logger = LoggerFactory.getLogger(ModifyEventController.class);
    
    // === FXML Elements ===
    @FXML private Label tfIdEvent; // Affichage de l'ID en lecture seule
    @FXML private TextField tfNom, tfLieu, nbPlacesStandard, prixPlaceStandard, nbPlacesVip, prixPlaceVip, nbPlacesPremium, prixPlacePremium;
    @FXML private TextField tfArtits, Domaine, Intervenant; // Champs spécifiques selon le type
    @FXML private TextArea Description;
    @FXML private DatePicker dpDate;
    @FXML private Spinner<Integer> spHour, spMinute, tfAge;
    @FXML private ComboBox<TypeEvenement> evType;
    @FXML private ComboBox<TypeConcert> tyConcert;
    @FXML private ComboBox<TypeSpectacle> tySpectacle;
    @FXML private ComboBox<NiveauExpertise> nvExpert;
    
    // Labels pour champs conditionnels
    @FXML private Label lbtfAge, lbtfArtits, lbtyConcert, lbTySpectacle, lbNvExpert, lbDomaine, lbIntervenant;
    
    // Labels d'erreur
    @FXML private Label lblErrDate, lblErrTitre, lbErrLieu, lblErrTyEvent, lblErrTypeConcert;
    @FXML private Label lblErrTypeSpectacle, lblErrNvExpert, lblErrDomaine, lblErrAge, lblErrArtiste, lblError;
    
    // Boutons
    @FXML private Button btnReturn;
    
    // Services
    private final EvenementService evenementService = new EvenementService();
    
    // Données
    private Evenement evenementAModifier;
    private int evenementId;
    private TypeEvenement evenementType;
    private com.bschooleventmanager.eventmanager.controller.organisateur.OrganisateurDashboardController dashboardController;
    
    /**
     * Initialisation du contrôleur
     */
    @FXML
    private void initialize() {
        logger.info("Initialisation du contrôleur de modification d'événement");
        
        // Initialiser les spinners
        initializeSpinners();
        
        // Initialiser les ComboBox
        initializeComboBoxes();
        
        // Configurer la validation numérique pour les champs places et prix
        setupNumericValidation();
        
        // Masquer tous les champs conditionnels au démarrage
        hideAllConditionalFields();
        
        // Ajouter les listeners
        setupListeners();
        
        logger.info("Contrôleur de modification initialisé avec succès");
    }
    
    /**
     * Initialise les spinners pour heure/minute et âge
     */
    private void initializeSpinners() {
        if (spHour != null) {
            spHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        }
        
        if (spMinute != null) {
            spMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        }
        
        if (tfAge != null) {
            tfAge.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99, 18));
        }
    }
    
    /**
     * Initialise les ComboBox avec leurs valeurs
     */
    private void initializeComboBoxes() {
        // Type d'événement
        if (evType != null) {
            evType.getItems().addAll(TypeEvenement.values());
            evType.setDisable(true); // Désactiver car le type ne peut pas être modifié
        }
        
        // Type de concert
        if (tyConcert != null) {
            tyConcert.getItems().addAll(TypeConcert.values());
        }
        
        // Type de spectacle
        if (tySpectacle != null) {
            tySpectacle.getItems().addAll(TypeSpectacle.values());
        }
        
        // Niveau d'expertise
        if (nvExpert != null) {
            nvExpert.getItems().addAll(NiveauExpertise.values());
        }
    }
    
    /**
     * Configure la validation numérique pour les champs places et prix
     */
    private void setupNumericValidation() {
        // Validation pour les champs de places (entiers uniquement)
        setupIntegerField(nbPlacesStandard);
        setupIntegerField(nbPlacesVip);
        setupIntegerField(nbPlacesPremium);
        
        // Validation pour les champs de prix (décimaux)
        setupDecimalField(prixPlaceStandard);
        setupDecimalField(prixPlaceVip);
        setupDecimalField(prixPlacePremium);
    }
    
    /**
     * Configure un champ TextField pour accepter uniquement des entiers
     */
    private void setupIntegerField(TextField field) {
        if (field != null) {
            field.setTextFormatter(new TextFormatter<>(change -> {
                if (change.getControlNewText().matches("\\d*")) {
                    return change;
                }
                return null;
            }));
        }
    }
    
    /**
     * Configure un champ TextField pour accepter uniquement des décimaux
     */
    private void setupDecimalField(TextField field) {
        if (field != null) {
            field.setTextFormatter(new TextFormatter<>(change -> {
                if (change.getControlNewText().matches("\\d*([.]\\d{0,2})?")) {
                    return change;
                }
                return null;
            }));
        }
    }
    
    /**
     * Configure les listeners pour les champs
     */
    private void setupListeners() {
        if (evType != null) {
            evType.setOnAction(event -> typeEventChange());
        }
    }
    
    /**
     * Masque tous les champs conditionnels
     */
    private void hideAllConditionalFields() {
        // Champs Concert
        setFieldVisibility(lbtfArtits, tfArtits, false);
        setFieldVisibility(lbtyConcert, tyConcert, false);
        setFieldVisibility(lbtfAge, tfAge, false);
        
        // Champs Spectacle
        setFieldVisibility(lbTySpectacle, tySpectacle, false);
        
        // Champs Conférence
        setFieldVisibility(lbDomaine, Domaine, false);
        setFieldVisibility(lbIntervenant, Intervenant, false);
        setFieldVisibility(lbNvExpert, nvExpert, false);
    }
    
    /**
     * Utilitaire pour définir la visibilité des champs
     */
    private void setFieldVisibility(Label label, Control field, boolean visible) {
        if (label != null) {
            label.setVisible(visible);
            label.setManaged(visible);
        }
        if (field != null) {
            field.setVisible(visible);
            field.setManaged(visible);
        }
    }
    
    /**
     * Gère le changement de type d'événement
     */
    @FXML
    private void typeEventChange() {
        hideAllConditionalFields();
        clearErrorMessages();
        
        TypeEvenement selectedType = evType.getValue();
        if (selectedType == null) return;
        
        switch (selectedType) {
            case CONCERT:
                setFieldVisibility(lbtfArtits, tfArtits, true);
                setFieldVisibility(lbtyConcert, tyConcert, true);
                setFieldVisibility(lbtfAge, tfAge, true);
                break;
                
            case SPECTACLE:
                setFieldVisibility(lbtfArtits, tfArtits, true); // Artiste/Troupe
                setFieldVisibility(lbTySpectacle, tySpectacle, true);
                setFieldVisibility(lbtfAge, tfAge, true);
                break;
                
            case CONFERENCE:
                setFieldVisibility(lbDomaine, Domaine, true);
                setFieldVisibility(lbIntervenant, Intervenant, true);
                setFieldVisibility(lbNvExpert, nvExpert, true);
                break;
        }
    }
    
    /**
     * Définit les informations d'événement et charge l'objet complet depuis la base
     */
    public void setEvenementInfo(int eventId, TypeEvenement eventType) {
        this.evenementId = eventId;
        this.evenementType = eventType;
        loadEventById();
    }
    
    /**
     * Charge l'événement depuis la base de données par son ID
     */
    private void loadEventById() {
        logger.info("Chargement de l'événement ID: {} de type: {}", evenementId, evenementType);
        
        try {
            // Récupérer l'événement depuis la base
            Evenement evenement = evenementService.getEvenementById(evenementId);
            
            if (evenement == null) {
                logger.error("Événement non trouvé avec l'ID: {}", evenementId);
                afficherErreur("Événement non trouvé");
                return;
            }
            
            // Vérifier que le type correspond
            if (evenement.getTypeEvenement() != evenementType) {
                logger.warn("Type d'événement incohérent. Attendu: {}, Reçu: {}", 
                    evenementType, evenement.getTypeEvenement());
                // On utilise le type de la base de données qui est plus fiable
                this.evenementType = evenement.getTypeEvenement();
            }
            
            this.evenementAModifier = evenement;
            remplirChamps(evenement);
            
            logger.info("Événement chargé avec succès: {}", evenement.getNom());
            
        } catch (BusinessException e) {
            logger.error("Erreur lors du chargement de l'événement", e);
            afficherErreur("Erreur lors du chargement de l'événement: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur inattendue lors du chargement", e);
            afficherErreur("Erreur technique lors du chargement de l'événement");
        }
    }
    
    /**
     * Remplit les champs avec les données de l'événement
     */
    private void remplirChamps(Evenement evenement) {
        logger.info("Remplissage des champs pour l'événement: {}", evenement.getNom());
        
        try {
            // Nom de l'événement (en lecture seule)
            if (tfIdEvent != null) {
                tfIdEvent.setText("Modification de : " + evenement.getNom());
            }
            
            // Champs de base
            if (tfNom != null) tfNom.setText(evenement.getNom());
            if (tfLieu != null) tfLieu.setText(evenement.getLieu());
            if (Description != null) Description.setText(evenement.getDescription());
            
            // Date et heure
            if (evenement.getDateEvenement() != null) {
                if (dpDate != null) {
                    dpDate.setValue(evenement.getDateEvenement().toLocalDate());
                }
                if (spHour != null) {
                    spHour.getValueFactory().setValue(evenement.getDateEvenement().getHour());
                }
                if (spMinute != null) {
                    spMinute.getValueFactory().setValue(evenement.getDateEvenement().getMinute());
                }
            }
            
            // Places et prix
            if (nbPlacesStandard != null) 
                nbPlacesStandard.setText(String.valueOf(evenement.getPlacesStandardDisponibles()));
            if (prixPlaceStandard != null && evenement.getPrixStandard() != null) 
                prixPlaceStandard.setText(evenement.getPrixStandard().toString());
            
            if (nbPlacesVip != null) 
                nbPlacesVip.setText(String.valueOf(evenement.getPlacesVipDisponibles()));
            if (prixPlaceVip != null && evenement.getPrixVip() != null) 
                prixPlaceVip.setText(evenement.getPrixVip().toString());
            
            if (nbPlacesPremium != null) 
                nbPlacesPremium.setText(String.valueOf(evenement.getPlacesPremiumDisponibles()));
            if (prixPlacePremium != null && evenement.getPrixPremium() != null) 
                prixPlacePremium.setText(evenement.getPrixPremium().toString());
            
            // Type d'événement
            if (evType != null) {
                evType.setValue(evenement.getTypeEvenement());
                typeEventChange(); // Afficher les champs appropriés
            }
            
            // Champs spécifiques selon le type
            remplirChampsSpecifiques(evenement);
            
            logger.info("Champs remplis avec succès");
            
        } catch (Exception e) {
            logger.error("Erreur lors du remplissage des champs", e);
            NotificationUtils.showError("Erreur lors du chargement des données de l'événement");
        }
    }
    
    /**
     * Remplit les champs spécifiques selon le type d'événement
     */
    private void remplirChampsSpecifiques(Evenement evenement) {
        if (evenement instanceof Concert) {
            Concert concert = (Concert) evenement;
            if (tfArtits != null) tfArtits.setText(concert.getArtiste_groupe());
            if (tyConcert != null) tyConcert.setValue(concert.getType());
            if (tfAge != null && concert.getAgeMin() != null) 
                tfAge.getValueFactory().setValue(concert.getAgeMin());
                
        } else if (evenement instanceof Spectacle) {
            Spectacle spectacle = (Spectacle) evenement;
            if (tfArtits != null) tfArtits.setText(spectacle.getTroupe_artistes());
            if (tySpectacle != null) tySpectacle.setValue(spectacle.getTypeSpectacle());
            if (tfAge != null && spectacle.getAgeMin() != null) 
                tfAge.getValueFactory().setValue(spectacle.getAgeMin());
                
        } else if (evenement instanceof Conference) {
            Conference conference = (Conference) evenement;
            if (Domaine != null) Domaine.setText(conference.getDomaine());
            if (Intervenant != null) Intervenant.setText(conference.getIntervenants());
            if (nvExpert != null) nvExpert.setValue(conference.getNiveauExpertise());
        }
    }
    
    /**
     * Sauvegarde les modifications
     */
    @FXML
    private void saveModifications() {
        logger.info("Tentative de sauvegarde des modifications");
        
        try {
            // Validation des champs
            if (!validerChamps()) {
                return;
            }
            
            // Appliquer les modifications à l'objet existant au lieu de le recréer
            appliquerModifications();
            
            // Sauvegarder via le service en fonction du type concret
            if (evenementAModifier instanceof Concert) {
                Concert updated = evenementService.modifierConcert((Concert) evenementAModifier);
                if (updated == null) {
                    throw new BusinessException("La mise à jour du concert a échoué");
                }
            } else if (evenementAModifier instanceof Spectacle) {
                Spectacle updated = evenementService.modifierSpectacle((Spectacle) evenementAModifier);
                if (updated == null) {
                    throw new BusinessException("La mise à jour du spectacle a échoué");
                }
            } else if (evenementAModifier instanceof Conference) {
                Conference updated = evenementService.modifierConference((Conference) evenementAModifier);
                if (updated == null) {
                    throw new BusinessException("La mise à jour de la conférence a échoué");
                }
            } else {
                // Fallback: essayer la mise à jour générique si le service le propose
                boolean result = evenementService.updateEvent(evenementAModifier);
                if (!result) {
                    throw new BusinessException("La mise à jour de l'événement a échoué");
                }
            }

            NotificationUtils.showSuccess("Événement modifié avec succès !");
            logger.info("Événement {} modifié avec succès", evenementAModifier.getNom());

            // Retourner au dashboard
            returnToDashboard();
            
   
            
        } catch (BusinessException e) {
            logger.error("Erreur métier lors de la modification", e);
            afficherErreur("Erreur lors de la modification : " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la modification", e);
            afficherErreur("Erreur technique lors de la modification de l'événement");
        }
    }
    
    /**
     * Valide les champs du formulaire
     */
    private boolean validerChamps() {
        clearErrorMessages();
        boolean isValid = true;
        logger.info("Validation des champs du formulaire");
        
        // Validation nom
        if (tfNom == null || tfNom.getText().trim().isEmpty()) {
            afficherErreurChamp(lblErrTitre, "Le nom de l'événement est obligatoire");
            isValid = false;
        }
        
        // Validation lieu
        if (tfLieu == null || tfLieu.getText().trim().isEmpty()) {
            afficherErreurChamp(lbErrLieu, "Le lieu est obligatoire");
            isValid = false;
        }
        
        // Validation date
        if (dpDate == null || dpDate.getValue() == null) {
            afficherErreurChamp(lblErrDate, "La date est obligatoire");
            isValid = false;
        } else {
            LocalDateTime dateEvenement = LocalDateTime.of(dpDate.getValue(), 
                java.time.LocalTime.of(spHour.getValue(), spMinute.getValue()));
            if (dateEvenement.isBefore(LocalDateTime.now())) {
                afficherErreurChamp(lblErrDate, "La date doit être dans le futur");
                isValid = false;
            }
        }
        
        // Validation type d'événement
        if (evType == null || evType.getValue() == null) {
            afficherErreurChamp(lblErrTyEvent, "Le type d'événement est obligatoire");
            isValid = false;
        }
        
        // Validations spécifiques selon le type
        if (evType != null && evType.getValue() != null) {
            TypeEvenement type = evType.getValue();
            switch (type) {
                case CONCERT:
                    if (tfArtits == null || tfArtits.getText().trim().isEmpty()) {
                        afficherErreurChamp(lblErrArtiste, "L'artiste/groupe est obligatoire");
                        isValid = false;
                    }
                    if (tyConcert == null || tyConcert.getValue() == null) {
                        afficherErreurChamp(lblErrTypeConcert, "Le type de concert est obligatoire");
                        isValid = false;
                    }
                    break;
                    
                case SPECTACLE:
                    if (tfArtits == null || tfArtits.getText().trim().isEmpty()) {
                        afficherErreurChamp(lblErrArtiste, "La troupe/artistes est obligatoire");
                        isValid = false;
                    }
                    if (tySpectacle == null || tySpectacle.getValue() == null) {
                        afficherErreurChamp(lblErrTypeSpectacle, "Le type de spectacle est obligatoire");
                        isValid = false;
                    }
                    break;
                    
                case CONFERENCE:
                    if (Domaine == null || Domaine.getText().trim().isEmpty()) {
                        afficherErreurChamp(lblErrDomaine, "Le domaine est obligatoire");
                        isValid = false;
                    }
                    if (nvExpert == null || nvExpert.getValue() == null) {
                        afficherErreurChamp(lblErrNvExpert, "Le niveau d'expertise est obligatoire");
                        isValid = false;
                    }
                    break;
            }
        }
        
        return isValid;
    }
    
    /**
     * Applique les modifications aux champs de l'événement existant
     */
    private void appliquerModifications() {
        logger.info("Application des modifications à l'événement");
        
        try {
            // Données de base
            LocalDateTime dateEvenement = LocalDateTime.of(dpDate.getValue(), 
                java.time.LocalTime.of(spHour.getValue(), spMinute.getValue()));
            
            int placesStandard = parseIntOrZero(nbPlacesStandard.getText());
            int placesVip = parseIntOrZero(nbPlacesVip.getText());
            int placesPremium = parseIntOrZero(nbPlacesPremium.getText());
            
            BigDecimal prixStandard = parseBigDecimalOrZero(prixPlaceStandard.getText());
            BigDecimal prixVip = parseBigDecimalOrZero(prixPlaceVip.getText());
            BigDecimal prixPremium = parseBigDecimalOrZero(prixPlacePremium.getText());
            
            // Appliquer les modifications communes
            evenementAModifier.setNom(tfNom.getText().trim());
            evenementAModifier.setDateEvenement(dateEvenement);
            evenementAModifier.setLieu(tfLieu.getText().trim());
            evenementAModifier.setDescription(Description.getText().trim());
            evenementAModifier.setPlacesStandardDisponibles(placesStandard);
            evenementAModifier.setPlacesVipDisponibles(placesVip);
            evenementAModifier.setPlacesPremiumDisponibles(placesPremium);
            evenementAModifier.setPrixStandard(prixStandard);
            evenementAModifier.setPrixVip(prixVip);
            evenementAModifier.setPrixPremium(prixPremium);
            
            // Appliquer les modifications spécifiques selon le type
            if (evenementAModifier instanceof Concert) {
                Concert concert = (Concert) evenementAModifier;
                concert.setArtiste_groupe(tfArtits.getText().trim());
                concert.setType(tyConcert.getValue());
                concert.setAgeMin(tfAge.getValue());
                
            } else if (evenementAModifier instanceof Spectacle) {
                Spectacle spectacle = (Spectacle) evenementAModifier;
                spectacle.setTroupe_artistes(tfArtits.getText().trim());
                spectacle.setTypeSpectacle(tySpectacle.getValue());
                spectacle.setAgeMin(tfAge.getValue());
                
            } else if (evenementAModifier instanceof Conference) {
                Conference conference = (Conference) evenementAModifier;
                conference.setDomaine(Domaine.getText().trim());
                conference.setIntervenants(Intervenant != null ? Intervenant.getText().trim() : "");
                conference.setNiveauExpertise(nvExpert.getValue());
            }
            
            logger.info("Modifications appliquées avec succès: {}", evenementAModifier.getNom());
            
        } catch (Exception e) {
            logger.error("Erreur lors de l'application des modifications", e);
            throw new RuntimeException("Erreur lors de la préparation des données", e);
        }
    }
    
    /**
     * Retour au dashboard
     */
    @FXML
    private void returnToDashboard() {
        logger.info("Retour au dashboard organisateur");
        if (dashboardController != null) {
            dashboardController.showEvents();
        }
    }
    
    /**
     * Définit la référence au contrôleur dashboard
     */
    public void setDashboardController(com.bschooleventmanager.eventmanager.controller.organisateur.OrganisateurDashboardController controller) {
        this.dashboardController = controller;
    }
    
    // === Méthodes utilitaires ===
    
    private int parseIntOrZero(String text) {
        try {
            return text == null || text.trim().isEmpty() ? 0 : Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private BigDecimal parseBigDecimalOrZero(String text) {
        try {
            return text == null || text.trim().isEmpty() ? BigDecimal.ZERO : new BigDecimal(text.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
    
    private void clearErrorMessages() {
        clearErrorLabel(lblErrTitre);
        clearErrorLabel(lbErrLieu);
        clearErrorLabel(lblErrDate);
        clearErrorLabel(lblErrTyEvent);
        clearErrorLabel(lblErrTypeConcert);
        clearErrorLabel(lblErrTypeSpectacle);
        clearErrorLabel(lblErrNvExpert);
        clearErrorLabel(lblErrDomaine);
        clearErrorLabel(lblErrAge);
        clearErrorLabel(lblErrArtiste);
        clearErrorLabel(lblError);
    }
    
    private void clearErrorLabel(Label label) {
        if (label != null) {
            label.setText("");
            label.setVisible(false);
            label.setManaged(false);
        }
    }
    
    private void afficherErreurChamp(Label label, String message) {
        if (label != null) {
            label.setText(message);
            label.setVisible(true);
            label.setManaged(true);
        }
    }
    
    private void afficherErreur(String message) {
        if (lblError != null) {
            lblError.setText(message);
            lblError.setVisible(true);
            lblError.setManaged(true);
        }
        logger.error("Erreur affichée à l'utilisateur: {}", message);
    }
}
