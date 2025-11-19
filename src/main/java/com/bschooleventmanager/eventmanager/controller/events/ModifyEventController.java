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
     * Sauvegarde les modifications avec validation complète
     */
    @FXML
    private void saveModifications() {
        logger.info("Tentative de sauvegarde des modifications pour l'événement: {}", 
            evenementAModifier != null ? evenementAModifier.getNom() : "Inconnu");
        
        try {
            // Validation complète des champs
            if (!validerChamps()) {
                logger.warn("Validation des champs échouée, modification annulée");
                return;
            }
            
            // Vérifier que l'événement à modifier existe toujours
            if (evenementAModifier == null) {
                afficherErreur("❌ Erreur : Aucun événement sélectionné pour modification");
                logger.error("Tentative de modification sans événement sélectionné");
                return;
            }
            
            // Appliquer les modifications à l'objet existant
            if (!appliquerModifications()) {
                afficherErreur("❌ Erreur lors de la préparation des données");
                return;
            }
            
            // Validation finale avant sauvegarde
            if (!validateBeforeSave()) {
                return;
            }
            
            // Sauvegarder via le service en fonction du type concret
            boolean saveResult = performSave();
            
            if (!saveResult) {
                afficherErreur("❌ Erreur lors de la sauvegarde en base de données");
                return;
            }
            
            // Succès
            NotificationUtils.showSuccess("✅ Événement modifié avec succès !");
            logger.info("Événement '{}' modifié avec succès (ID: {})", 
                evenementAModifier.getNom(), evenementAModifier.getIdEvenement());
            
            // Retourner au dashboard
            returnToDashboard();
            
        } catch (BusinessException e) {
            logger.error("Erreur métier lors de la modification", e);
            afficherErreur("❌ " + e.getMessage());
            NotificationUtils.showError("Erreur lors de la modification : " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la modification", e);
            afficherErreur("❌ Erreur technique lors de la modification de l'événement");
            NotificationUtils.showError("Une erreur technique est survenue. Veuillez réessayer.");
        }
    }
    
    /**
     * Effectue une validation finale avant sauvegarde
     */
    private boolean validateBeforeSave() {
        // Vérifier que les utilisateurs connectés sont autorisés
        if (SessionManager.getUtilisateurConnecte() == null) {
            afficherErreur("❌ Session expirée. Veuillez vous reconnecter.");
            return false;
        }
        
        // Vérifier que l'événement appartient à l'organisateur connecté
        int organisateurConnecteId = SessionManager.getUtilisateurConnecte().getIdUtilisateur();
        if (evenementAModifier.getOrganisateurId() != organisateurConnecteId) {
            afficherErreur("❌ Vous n'êtes pas autorisé à modifier cet événement");
            logger.warn("Tentative de modification non autorisée - Événement ID: {}, Organisateur: {} vs {}", 
                evenementAModifier.getIdEvenement(), evenementAModifier.getOrganisateurId(), organisateurConnecteId);
            return false;
        }
        
        return true;
    }
    
    /**
     * Effectue la sauvegarde selon le type d'événement
     */
    private boolean performSave() throws BusinessException {
        logger.info("Sauvegarde de l'événement de type: {}", evenementAModifier.getClass().getSimpleName());
        
        if (evenementAModifier instanceof Concert) {
            Concert updated = evenementService.modifierConcert((Concert) evenementAModifier);
            if (updated == null) {
                logger.error("Échec de la mise à jour du concert");
                return false;
            }
            logger.info("Concert mis à jour avec succès");
            
        } else if (evenementAModifier instanceof Spectacle) {
            Spectacle updated = evenementService.modifierSpectacle((Spectacle) evenementAModifier);
            if (updated == null) {
                logger.error("Échec de la mise à jour du spectacle");
                return false;
            }
            logger.info("Spectacle mis à jour avec succès");
            
        } else if (evenementAModifier instanceof Conference) {
            Conference updated = evenementService.modifierConference((Conference) evenementAModifier);
            if (updated == null) {
                logger.error("Échec de la mise à jour de la conférence");
                return false;
            }
            logger.info("Conférence mise à jour avec succès");
            
        } else {
            // Fallback: essayer la mise à jour générique si le service le propose
            boolean result = evenementService.updateEvent(evenementAModifier);
            if (!result) {
                logger.error("Échec de la mise à jour générique de l'événement");
                return false;
            }
            logger.info("Événement mis à jour avec succès (méthode générique)");
        }
        
        return true;
    }
    
    /**
     * Valide les champs du formulaire avec validation complète
     */
    private boolean validerChamps() {
        clearErrorMessages();
        boolean isValid = true;
        logger.info("Validation complète des champs du formulaire de modification");
        
        // Validation des champs de base
        isValid &= validateBasicFields();
        
        // Validation de la date et heure
        isValid &= validateDate();
        
        // Validation des champs numériques (prix et places)
        isValid &= validateNumericFields();
        
        // Validation spécifique selon le type d'événement
        isValid &= validateTypeSpecificFields();
        
        // Validation finale de cohérence
        if (isValid) {
            isValid &= performFinalValidation();
        }
        
        logger.info("Validation du formulaire terminée. Résultat: {}", isValid ? "SUCCÈS" : "ÉCHEC");
        return isValid;
    }
    
    /**
     * Valide les champs de base du formulaire
     */
    private boolean validateBasicFields() {
        boolean isValid = true;
        
        // Validation nom/titre
        if (tfNom == null || tfNom.getText().trim().isEmpty()) {
            afficherErreurChamp(lblErrTitre, "❌ Le nom de l'événement est obligatoire");
            isValid = false;
        } else if (tfNom.getText().trim().length() < 3) {
            afficherErreurChamp(lblErrTitre, "❌ Le nom doit contenir au moins 3 caractères");
            isValid = false;
        } else if (tfNom.getText().trim().length() > 100) {
            afficherErreurChamp(lblErrTitre, "❌ Le nom ne peut pas dépasser 100 caractères");
            isValid = false;
        }
        
        // Validation description
        if (Description != null && !Description.getText().trim().isEmpty()) {
            if (Description.getText().trim().length() < 10) {
                afficherErreur("❌ La description doit contenir au moins 10 caractères");
                isValid = false;
            } else if (Description.getText().trim().length() > 500) {
                afficherErreur("❌ La description ne peut pas dépasser 500 caractères");
                isValid = false;
            }
        }
        
        // Validation lieu
        if (tfLieu == null || tfLieu.getText().trim().isEmpty()) {
            afficherErreurChamp(lbErrLieu, "❌ Le lieu est obligatoire");
            isValid = false;
        } else if (tfLieu.getText().trim().length() < 3) {
            afficherErreurChamp(lbErrLieu, "❌ Le lieu doit contenir au moins 3 caractères");
            isValid = false;
        } else if (tfLieu.getText().trim().length() > 100) {
            afficherErreurChamp(lbErrLieu, "❌ Le lieu ne peut pas dépasser 100 caractères");
            isValid = false;
        }
        
        // Validation type d'événement
        if (evType == null || evType.getValue() == null) {
            afficherErreurChamp(lblErrTyEvent, "❌ Le type d'événement est obligatoire");
            isValid = false;
        }
        
        return isValid;
    }
    
    /**
     * Valide la date et l'heure de l'événement
     */
    private boolean validateDate() {
        if (dpDate == null || dpDate.getValue() == null) {
            afficherErreurChamp(lblErrDate, "❌ La date est obligatoire");
            return false;
        }
        
        if (spHour == null || spMinute == null) {
            afficherErreurChamp(lblErrDate, "❌ L'heure est obligatoire");
            return false;
        }
        
        try {
            LocalDateTime dateEvenement = LocalDateTime.of(dpDate.getValue(), 
                java.time.LocalTime.of(spHour.getValue(), spMinute.getValue()));
            
            // Vérifier que la date n'est pas dans le passé
            if (dateEvenement.isBefore(LocalDateTime.now())) {
                afficherErreurChamp(lblErrDate, "❌ La date doit être dans le futur");
                return false;
            }
            
            // Si c'est aujourd'hui, vérifier qu'il reste au moins 1 heure
            if (dateEvenement.toLocalDate().isEqual(LocalDate.now())) {
                if (dateEvenement.isBefore(LocalDateTime.now().plusHours(1))) {
                    afficherErreurChamp(lblErrDate, "❌ L'événement doit être prévu au minimum 1 heure à l'avance");
                    return false;
                }
            }
            
            // Vérifier que la date n'est pas trop lointaine (ex: plus de 5 ans)
            if (dateEvenement.isAfter(LocalDateTime.now().plusYears(5))) {
                afficherErreurChamp(lblErrDate, "❌ La date ne peut pas être dans plus de 5 ans");
                return false;
            }
            
        } catch (Exception e) {
            afficherErreurChamp(lblErrDate, "❌ Date ou heure invalide");
            return false;
        }
        
        return true;
    }
    
    /**
     * Valide les champs numériques (prix et places)
     */
    private boolean validateNumericFields() {
        boolean isValid = true;
        
        try {
            // Validation des places
            int placesStandard = parseIntOrZero(nbPlacesStandard.getText());
            int placesVip = parseIntOrZero(nbPlacesVip.getText());
            int placesPremium = parseIntOrZero(nbPlacesPremium.getText());
            
            // Vérifier les limites des places
            if (placesStandard < 0 || placesStandard > 10000) {
                afficherErreur("❌ Le nombre de places Standard doit être entre 0 et 10 000");
                isValid = false;
            }
            if (placesVip < 0 || placesVip > 10000) {
                afficherErreur("❌ Le nombre de places VIP doit être entre 0 et 10 000");
                isValid = false;
            }
            if (placesPremium < 0 || placesPremium > 10000) {
                afficherErreur("❌ Le nombre de places Premium doit être entre 0 et 10 000");
                isValid = false;
            }
            
            // Vérifier qu'il y a au moins une place
            if (placesStandard + placesVip + placesPremium == 0) {
                afficherErreur("❌ L'événement doit avoir au moins une place disponible");
                isValid = false;
            }
            
            // Validation des prix
            BigDecimal prixStandard = parseBigDecimalOrZero(prixPlaceStandard.getText());
            BigDecimal prixVip = parseBigDecimalOrZero(prixPlaceVip.getText());
            BigDecimal prixPremium = parseBigDecimalOrZero(prixPlacePremium.getText());
            
            // Vérifier les limites des prix
            if (prixStandard.compareTo(BigDecimal.valueOf(5)) < 0 || 
                prixStandard.compareTo(BigDecimal.valueOf(500)) > 0) {
                afficherErreur("❌ Le prix Standard doit être entre 5€ et 500€");
                isValid = false;
            }
            if (prixVip.compareTo(BigDecimal.valueOf(10)) < 0 || 
                prixVip.compareTo(BigDecimal.valueOf(1000)) > 0) {
                afficherErreur("❌ Le prix VIP doit être entre 10€ et 1000€");
                isValid = false;
            }
            if (prixPremium.compareTo(BigDecimal.valueOf(20)) < 0 || 
                prixPremium.compareTo(BigDecimal.valueOf(2000)) > 0) {
                afficherErreur("❌ Le prix Premium doit être entre 20€ et 2000€");
                isValid = false;
            }
            
        } catch (NumberFormatException e) {
            afficherErreur("❌ Veuillez saisir des valeurs numériques valides pour les prix et places");
            isValid = false;
        } catch (Exception e) {
            afficherErreur("❌ Erreur dans la validation des champs numériques");
            isValid = false;
        }
        
        return isValid;
    }
    
    /**
     * Valide les champs spécifiques selon le type d'événement
     */
    private boolean validateTypeSpecificFields() {
        if (evType == null || evType.getValue() == null) {
            return true; // Déjà validé dans validateBasicFields
        }
        
        TypeEvenement type = evType.getValue();
        boolean isValid = true;
        
        switch (type) {
            case CONCERT:
                // Validation artiste/groupe
                if (tfArtits == null || tfArtits.getText().trim().isEmpty()) {
                    afficherErreurChamp(lblErrArtiste, "❌ L'artiste/groupe est obligatoire");
                    isValid = false;
                } else {
                    if (tfArtits.getText().trim().length() < 2) {
                        afficherErreurChamp(lblErrArtiste, "❌ Le nom de l'artiste doit contenir au moins 2 caractères");
                        isValid = false;
                    } else if (tfArtits.getText().trim().length() > 100) {
                        afficherErreurChamp(lblErrArtiste, "❌ Le nom de l'artiste ne peut pas dépasser 100 caractères");
                        isValid = false;
                    }
                }
                
                // Validation type de concert
                if (tyConcert == null || tyConcert.getValue() == null) {
                    afficherErreurChamp(lblErrTypeConcert, "❌ Le type de concert est obligatoire");
                    isValid = false;
                }
                
                // Validation âge minimum (pour concerts)
                if (tfAge != null && tfAge.getValue() != null) {
                    int ageMin = tfAge.getValue();
                    if (ageMin < 0 || ageMin > 18) {
                        afficherErreurChamp(lblErrAge, "❌ L'âge minimum doit être entre 0 et 18 ans");
                        isValid = false;
                    }
                }
                break;
                
            case SPECTACLE:
                // Validation troupe/artistes
                if (tfArtits == null || tfArtits.getText().trim().isEmpty()) {
                    afficherErreurChamp(lblErrArtiste, "❌ La troupe/artistes est obligatoire");
                    isValid = false;
                } else {
                    if (tfArtits.getText().trim().length() < 2) {
                        afficherErreurChamp(lblErrArtiste, "❌ Le nom de la troupe doit contenir au moins 2 caractères");
                        isValid = false;
                    } else if (tfArtits.getText().trim().length() > 100) {
                        afficherErreurChamp(lblErrArtiste, "❌ Le nom de la troupe ne peut pas dépasser 100 caractères");
                        isValid = false;
                    }
                }
                
                // Validation type de spectacle
                if (tySpectacle == null || tySpectacle.getValue() == null) {
                    afficherErreurChamp(lblErrTypeSpectacle, "❌ Le type de spectacle est obligatoire");
                    isValid = false;
                }
                
                // Validation âge minimum (pour spectacles)
                if (tfAge != null && tfAge.getValue() != null) {
                    int ageMin = tfAge.getValue();
                    if (ageMin < 0 || ageMin > 18) {
                        afficherErreurChamp(lblErrAge, "❌ L'âge minimum doit être entre 0 et 18 ans");
                        isValid = false;
                    }
                }
                break;
                
            case CONFERENCE:
                // Validation domaine
                if (Domaine == null || Domaine.getText().trim().isEmpty()) {
                    afficherErreurChamp(lblErrDomaine, "❌ Le domaine est obligatoire");
                    isValid = false;
                } else {
                    if (Domaine.getText().trim().length() < 3) {
                        afficherErreurChamp(lblErrDomaine, "❌ Le domaine doit contenir au moins 3 caractères");
                        isValid = false;
                    } else if (Domaine.getText().trim().length() > 100) {
                        afficherErreurChamp(lblErrDomaine, "❌ Le domaine ne peut pas dépasser 100 caractères");
                        isValid = false;
                    }
                }
                
                // Validation intervenant (optionnel mais si rempli, doit respecter les limites)
                if (Intervenant != null && !Intervenant.getText().trim().isEmpty()) {
                    if (Intervenant.getText().trim().length() > 200) {
                        afficherErreur("❌ Le nom de l'intervenant ne peut pas dépasser 200 caractères");
                        isValid = false;
                    }
                }
                
                // Validation niveau d'expertise
                if (nvExpert == null || nvExpert.getValue() == null) {
                    afficherErreurChamp(lblErrNvExpert, "❌ Le niveau d'expertise est obligatoire");
                    isValid = false;
                }
                break;
        }
        
        return isValid;
    }
    
    /**
     * Effectue une validation finale de cohérence
     */
    private boolean performFinalValidation() {
        try {
            // Vérification de cohérence des prix (VIP >= Standard, Premium >= VIP)
            BigDecimal prixStandard = parseBigDecimalOrZero(prixPlaceStandard.getText());
            BigDecimal prixVip = parseBigDecimalOrZero(prixPlaceVip.getText());
            BigDecimal prixPremium = parseBigDecimalOrZero(prixPlacePremium.getText());
            
            if (prixVip.compareTo(prixStandard) < 0) {
                afficherErreur("❌ Le prix VIP doit être supérieur ou égal au prix Standard");
                return false;
            }
            
            if (prixPremium.compareTo(prixVip) < 0) {
                afficherErreur("❌ Le prix Premium doit être supérieur ou égal au prix VIP");
                return false;
            }
            
            // Vérification que l'événement a au moins une place
            int placesStandard = parseIntOrZero(nbPlacesStandard.getText());
            int placesVip = parseIntOrZero(nbPlacesVip.getText());
            int placesPremium = parseIntOrZero(nbPlacesPremium.getText());
            int totalPlaces = placesStandard + placesVip + placesPremium;
            
            if (totalPlaces == 0) {
                afficherErreur("❌ L'événement doit avoir au moins une place disponible");
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("Erreur lors de la validation finale", e);
            afficherErreur("❌ Erreur lors de la validation finale des données");
            return false;
        }
    }
    
    /**
     * Applique les modifications aux champs de l'événement existant
     * @return true si les modifications ont été appliquées avec succès, false sinon
     */
    private boolean appliquerModifications() {
        logger.info("Application des modifications à l'événement: {}", evenementAModifier.getNom());
        
        try {
            // Validation des données avant application
            if (!validateDataBeforeApply()) {
                return false;
            }
            
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
            if (!appliquerModificationsSpecifiques()) {
                return false;
            }
            
            logger.info("Modifications appliquées avec succès pour l'événement: {}", evenementAModifier.getNom());
            return true;
            
        } catch (Exception e) {
            logger.error("Erreur lors de l'application des modifications", e);
            afficherErreur("❌ Erreur lors de la préparation des données");
            return false;
        }
    }
    
    /**
     * Valide les données avant application des modifications
     */
    private boolean validateDataBeforeApply() {
        // Vérifier que tous les champs nécessaires sont présents
        if (dpDate == null || dpDate.getValue() == null) {
            afficherErreur("❌ Date manquante");
            return false;
        }
        
        if (spHour == null || spMinute == null || 
            spHour.getValue() == null || spMinute.getValue() == null) {
            afficherErreur("❌ Heure manquante");
            return false;
        }
        
        if (tfNom == null || tfLieu == null || Description == null) {
            afficherErreur("❌ Champs obligatoires manquants");
            return false;
        }
        
        return true;
    }
    
    /**
     * Applique les modifications spécifiques selon le type d'événement
     */
    private boolean appliquerModificationsSpecifiques() {
        try {
            if (evenementAModifier instanceof Concert) {
                Concert concert = (Concert) evenementAModifier;
                
                if (tfArtits == null || tfArtits.getText().trim().isEmpty()) {
                    afficherErreur("❌ Artiste/groupe manquant pour le concert");
                    return false;
                }
                if (tyConcert == null || tyConcert.getValue() == null) {
                    afficherErreur("❌ Type de concert manquant");
                    return false;
                }
                
                concert.setArtiste_groupe(tfArtits.getText().trim());
                concert.setType(tyConcert.getValue());
                concert.setAgeMin(tfAge != null && tfAge.getValue() != null ? tfAge.getValue() : 0);
                
                logger.info("Modifications spécifiques appliquées pour le concert");
                
            } else if (evenementAModifier instanceof Spectacle) {
                Spectacle spectacle = (Spectacle) evenementAModifier;
                
                if (tfArtits == null || tfArtits.getText().trim().isEmpty()) {
                    afficherErreur("❌ Troupe/artistes manquant pour le spectacle");
                    return false;
                }
                if (tySpectacle == null || tySpectacle.getValue() == null) {
                    afficherErreur("❌ Type de spectacle manquant");
                    return false;
                }
                
                spectacle.setTroupe_artistes(tfArtits.getText().trim());
                spectacle.setTypeSpectacle(tySpectacle.getValue());
                spectacle.setAgeMin(tfAge != null && tfAge.getValue() != null ? tfAge.getValue() : 0);
                
                logger.info("Modifications spécifiques appliquées pour le spectacle");
                
            } else if (evenementAModifier instanceof Conference) {
                Conference conference = (Conference) evenementAModifier;
                
                if (Domaine == null || Domaine.getText().trim().isEmpty()) {
                    afficherErreur("❌ Domaine manquant pour la conférence");
                    return false;
                }
                if (nvExpert == null || nvExpert.getValue() == null) {
                    afficherErreur("❌ Niveau d'expertise manquant pour la conférence");
                    return false;
                }
                
                conference.setDomaine(Domaine.getText().trim());
                conference.setIntervenants(Intervenant != null ? Intervenant.getText().trim() : "");
                conference.setNiveauExpertise(nvExpert.getValue());
                
                logger.info("Modifications spécifiques appliquées pour la conférence");
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("Erreur lors de l'application des modifications spécifiques", e);
            afficherErreur("❌ Erreur lors de la mise à jour des données spécifiques");
            return false;
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
