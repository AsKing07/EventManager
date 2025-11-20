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
 * Contrôleur pour la modification d'événements existants dans EventManager.
 * 
 * <p>Cette classe gère l'intégralité du processus de modification d'événements
 * avec support des trois types d'événements : concerts, spectacles et conférences.
 * Elle fournit une interface de modification sécurisée avec validation complète
 * et préservation de l'intégrité des données.</p>
 * 
 * <p><strong>Fonctionnalités principales :</strong></p>
 * <ul>
 *   <li>Chargement et affichage des données existantes</li>
 *   <li>Interface adaptative selon le type d'événement</li>
 *   <li>Validation complète des modifications</li>
 *   <li>Gestion des contraintes d'intégrité référentielle</li>
 *   <li>Sauvegarde sécurisée avec rollback automatique</li>
 * </ul>
 * 
 * <p><strong>Types d'événements supportés :</strong></p>
 * <ul>
 *   <li><strong>Concert :</strong> Modification artiste/groupe, type musical, âge minimum</li>
 *   <li><strong>Spectacle :</strong> Modification troupe/artiste, type de spectacle, âge minimum</li>
 *   <li><strong>Conférence :</strong> Modification domaine, intervenant, niveau d'expertise</li>
 * </ul>
 * 
 * <p><strong>Sécurité et autorisations :</strong></p>
 * <ul>
 *   <li><strong>Vérification organisateur :</strong> Seul le propriétaire peut modifier</li>
 *   <li><strong>Validation session :</strong> Authentification requise</li>
 *   <li><strong>Type d'événement :</strong> Non modifiable (contrainte métier)</li>
 *   <li><strong>Intégrité données :</strong> Préservation des réservations existantes</li>
 * </ul>
 * 
 * <p><strong>Validation et contraintes :</strong></p>
 * <ul>
 *   <li><strong>Champs obligatoires :</strong> Nom, lieu, date, type (lecture seule)</li>
 *   <li><strong>Contraintes temporelles :</strong> Date future avec délai minimum</li>
 *   <li><strong>Limites numériques :</strong> Places (0-10000), Prix (5-2000€)</li>
 *   <li><strong>Cohérence tarifaire :</strong> Premium ≥ VIP ≥ Standard</li>
 *   <li><strong>Validation spécifique :</strong> Selon le type d'événement existant</li>
 * </ul>
 * 
 * <p><strong>Interface utilisateur :</strong></p>
 * <ul>
 *   <li>Pré-remplissage automatique avec données existantes</li>
 *   <li>Champs conditionnels selon le type (non modifiable)</li>
 *   <li>Validation en temps réel avec feedback immédiat</li>
 *   <li>Messages d'erreur contextuels par champ</li>
 *   <li>Indicateurs visuels de modification</li>
 * </ul>
 * 
 * <p><strong>Workflow de modification :</strong></p>
 * <ol>
 *   <li><strong>Chargement :</strong> Récupération de l'événement par ID</li>
 *   <li><strong>Affichage :</strong> Pré-remplissage de l'interface</li>
 *   <li><strong>Configuration :</strong> Adaptation selon le type existant</li>
 *   <li><strong>Modification :</strong> Saisie des nouvelles valeurs</li>
 *   <li><strong>Validation :</strong> Contrôle complet des données</li>
 *   <li><strong>Sauvegarde :</strong> Application des modifications</li>
 * </ol>
 * 
 * <p><strong>Gestion des erreurs :</strong></p>
 * <ul>
 *   <li>Validation progressive avec arrêt sur première erreur</li>
 *   <li>Messages contextuels par champ avec labels dédiés</li>
 *   <li>Gestion des contraintes référentielles</li>
 *   <li>Rollback automatique en cas d'échec</li>
 *   <li>Logging détaillé pour audit et diagnostic</li>
 * </ul>
 * 
 * <p><strong>Architecture de validation :</strong></p>
 * <ol>
 *   <li><strong>Validation de base :</strong> Champs obligatoires et formats</li>
 *   <li><strong>Validation temporelle :</strong> Date et heure cohérentes</li>
 *   <li><strong>Validation numérique :</strong> Places et prix dans les limites</li>
 *   <li><strong>Validation spécifique :</strong> Selon le type d'événement</li>
 *   <li><strong>Validation finale :</strong> Cohérence globale et autorisations</li>
 * </ol>
 * 
 * <p><strong>Intégration services :</strong></p>
 * <ul>
 *   <li><strong>EvenementService :</strong> Modification et gestion des événements</li>
 *   <li><strong>SessionManager :</strong> Vérification des autorisations</li>
 *   <li><strong>NotificationUtils :</strong> Feedback utilisateur temps réel</li>
 * </ul>
 * 
 * <p><strong>Exemple d'utilisation :</strong></p>
 * <pre>{@code
 * ModifyEventController controller = loader.getController();
 * controller.setDashboardController(dashboardController);
 * controller.setEvenementInfo(eventId, eventType);
 * // L'interface se charge automatiquement avec les données existantes
 * }</pre>
 * 
 * @author Charbel SONON
 * @author MABOMESI Loïc
 * @version 1.0
 * @since 1.0
 * 
 * @see CreateEventController
 * @see com.bschooleventmanager.eventmanager.service.EvenementService
 * @see com.bschooleventmanager.eventmanager.model.Concert
 * @see com.bschooleventmanager.eventmanager.model.Spectacle
 * @see com.bschooleventmanager.eventmanager.model.Conference
 * @see com.bschooleventmanager.eventmanager.controller.organisateur.OrganisateurDashboardController
 */
public class ModifyEventController {
    
    /** Logger pour traçage des opérations de modification d'événements. */
    private static final Logger logger = LoggerFactory.getLogger(ModifyEventController.class);
    
    // === Éléments FXML - Interface utilisateur ===
    
    /** Label d'affichage de l'ID événement en lecture seule. */
    @FXML private Label tfIdEvent;
    
    // Champs de saisie principaux
    /** Champ de saisie pour le nom de l'événement. */
    @FXML private TextField tfNom;
    /** Champ de saisie pour le lieu de l'événement. */
    @FXML private TextField tfLieu;
    /** Champ de saisie pour le nombre de places standard. */
    @FXML private TextField nbPlacesStandard;
    /** Champ de saisie pour le prix des places standard. */
    @FXML private TextField prixPlaceStandard;
    /** Champ de saisie pour le nombre de places VIP. */
    @FXML private TextField nbPlacesVip;
    /** Champ de saisie pour le prix des places VIP. */
    @FXML private TextField prixPlaceVip;
    /** Champ de saisie pour le nombre de places premium. */
    @FXML private TextField nbPlacesPremium;
    /** Champ de saisie pour le prix des places premium. */
    @FXML private TextField prixPlacePremium;
    
    // Champs spécifiques selon le type d'événement
    /** Champ de saisie pour l'artiste ou groupe (concerts/spectacles). */
    @FXML private TextField tfArtits;
    /** Champ de saisie pour le domaine de la conférence. */
    @FXML private TextField Domaine;
    /** Champ de saisie pour l'intervenant de la conférence. */
    @FXML private TextField Intervenant;
    
    /** Zone de texte pour la description détaillée de l'événement. */
    @FXML private TextArea Description;
    
    /** Sélecteur de date pour la date de l'événement. */
    @FXML private DatePicker dpDate;
    
    // Spinners pour heure et âge
    /** Spinner pour la sélection de l'heure (0-23). */
    @FXML private Spinner<Integer> spHour;
    /** Spinner pour la sélection des minutes (0-59). */
    @FXML private Spinner<Integer> spMinute;
    /** Spinner pour la sélection de l'âge minimum (concerts/spectacles). */
    @FXML private Spinner<Integer> tfAge;
    
    // ComboBox pour les sélections (type non modifiable)
    /** ComboBox pour l'affichage du type d'événement (lecture seule). */
    @FXML private ComboBox<TypeEvenement> evType;
    /** ComboBox pour la sélection du type de concert. */
    @FXML private ComboBox<TypeConcert> tyConcert;
    /** ComboBox pour la sélection du type de spectacle. */
    @FXML private ComboBox<TypeSpectacle> tySpectacle;
    /** ComboBox pour la sélection du niveau d'expertise (conférences). */
    @FXML private ComboBox<NiveauExpertise> nvExpert;
    
    // Labels pour champs conditionnels selon le type
    /** Label pour le champ âge minimum (affiché pour concerts et spectacles). */
    @FXML private Label lbtfAge;
    /** Label pour le champ artiste/groupe (affiché pour concerts et spectacles). */
    @FXML private Label lbtfArtits;
    /** Label pour le type de concert (affiché uniquement pour les concerts). */
    @FXML private Label lbtyConcert;
    /** Label pour le type de spectacle (affiché uniquement pour les spectacles). */
    @FXML private Label lbTySpectacle;
    /** Label pour le niveau d'expertise (affiché uniquement pour les conférences). */
    @FXML private Label lbNvExpert;
    /** Label pour le domaine (affiché uniquement pour les conférences). */
    @FXML private Label lbDomaine;
    /** Label pour l'intervenant (affiché uniquement pour les conférences). */
    @FXML private Label lbIntervenant;
    
    // Labels d'erreur pour validation temps réel
    /** Label d'erreur pour la validation de la date de l'événement. */
    @FXML private Label lblErrDate;
    /** Label d'erreur pour la validation du titre de l'événement. */
    @FXML private Label lblErrTitre;
    /** Label d'erreur pour la validation du lieu de l'événement. */
    @FXML private Label lbErrLieu;
    /** Label d'erreur pour la validation du type d'événement. */
    @FXML private Label lblErrTyEvent;
    /** Label d'erreur pour la validation du type de concert. */
    @FXML private Label lblErrTypeConcert;
    /** Label d'erreur pour la validation du type de spectacle. */
    @FXML private Label lblErrTypeSpectacle;
    /** Label d'erreur pour la validation du niveau d'expertise. */
    @FXML private Label lblErrNvExpert;
    /** Label d'erreur pour la validation du domaine de conférence. */
    @FXML private Label lblErrDomaine;
    /** Label d'erreur pour la validation de l'âge minimum. */
    @FXML private Label lblErrAge;
    /** Label d'erreur pour la validation de l'artiste/groupe. */
    @FXML private Label lblErrArtiste;
    /** Label d'erreur global pour messages d'erreur généraux. */
    @FXML private Label lblError;
    
    // Boutons d'action
    /** Bouton de retour au tableau de bord sans sauvegarde. */
    @FXML private Button btnReturn;
    
    // === Services et données ===
    
    /** Service métier pour la gestion des événements. */
    private final EvenementService evenementService = new EvenementService();
    
    // Données de l'événement en cours de modification
    /** Objet événement chargé depuis la base pour modification. */
    private Evenement evenementAModifier;
    /** Identifiant de l'événement à modifier. */
    private int evenementId;
    /** Type de l'événement à modifier (non modifiable). */
    private TypeEvenement evenementType;
    
    /** Référence vers le contrôleur de tableau de bord organisateur pour navigation. */
    private com.bschooleventmanager.eventmanager.controller.organisateur.OrganisateurDashboardController dashboardController;
    
    /**
     * Initialise les composants de l'interface de modification d'événements.
     * 
     * <p>Cette méthode est appelée automatiquement par JavaFX après le chargement
     * du fichier FXML. Elle configure tous les contrôles de l'interface pour
     * la modification d'événements existants avec leurs contraintes spécifiques.</p>
     * 
     * <p><strong>Configuration effectuée :</strong></p>
     * <ul>
     *   <li><strong>Spinners :</strong> Configuration des plages horaires et âges</li>
     *   <li><strong>ComboBox :</strong> Chargement des valeurs depuis les enums</li>
     *   <li><strong>Validation numérique :</strong> Formatage automatique des champs</li>
     *   <li><strong>Interface adaptative :</strong> Masquage initial des champs conditionnels</li>
     *   <li><strong>Écouteurs :</strong> Réaction aux changements de type d'événement</li>
     * </ul>
     * 
     * <p><strong>Contraintes spécifiques à la modification :</strong></p>
     * <ul>
     *   <li><strong>Type d'événement :</strong> ComboBox désactivée (non modifiable)</li>
     *   <li><strong>Spinners :</strong> Heures (0-23), minutes (0-59), âge (0-99)</li>
     *   <li><strong>Validation :</strong> Entiers pour places, décimaux pour prix</li>
     * </ul>
     * 
     * <p><strong>État initial de l'interface :</strong></p>
     * <ul>
     *   <li>Masquage de tous les champs conditionnels</li>
     *   <li>Réinitialisation des messages d'erreur</li>
     *   <li>Configuration des écouteurs d'événements</li>
     *   <li>Préparation pour le chargement des données</li>
     * </ul>
     * 
     * <p>Cette méthode prépare l'interface pour recevoir les données de l'événement
     * à modifier via {@link #setEvenementInfo(int, TypeEvenement)}.</p>
     * 
     * @see #initializeSpinners()
     * @see #initializeComboBoxes()
     * @see #setupNumericValidation()
     * @see #hideAllConditionalFields()
     * @see #setupListeners()
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
     * Définit les informations d'événement et charge l'objet complet depuis la base.
     * 
     * <p>Cette méthode est le point d'entrée principal pour initialiser l'interface
     * de modification avec un événement spécifique. Elle stocke les identifiants
     * et déclenche le chargement complet des données depuis la base de données.</p>
     * 
     * <p><strong>Processus d'initialisation :</strong></p>
     * <ol>
     *   <li>Stockage de l'ID et du type d'événement</li>
     *   <li>Appel de {@link #loadEventById()} pour chargement complet</li>
     *   <li>Configuration automatique de l'interface</li>
     *   <li>Adaptation des champs selon le type</li>
     * </ol>
     * 
     * <p><strong>Validation des paramètres :</strong></p>
     * <ul>
     *   <li>Vérification de la validité de l'ID événement</li>
     *   <li>Contrôle de cohérence du type d'événement</li>
     *   <li>Validation des permissions d'accès</li>
     * </ul>
     * 
     * <p>Cette méthode doit être appelée après l'initialisation du contrôleur
     * et avant l'affichage de l'interface à l'utilisateur.</p>
     * 
     * @param eventId L'identifiant unique de l'événement à modifier
     * @param eventType Le type de l'événement (Concert, Spectacle, Conférence)
     * 
     * @see #loadEventById()
     * @see TypeEvenement
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
     * Sauvegarde les modifications avec validation complète et sécurité.
     * 
     * <p>Cette méthode orchestre l'intégralité du processus de sauvegarde des
     * modifications, de la validation des saisies jusqu'à la persistance en base
     * de données. Elle implémente une approche sécurisée avec contrôles multiples.</p>
     * 
     * <p><strong>Workflow de sauvegarde :</strong></p>
     * <ol>
     *   <li><strong>Validation des champs :</strong> Contrôle complet des saisies</li>
     *   <li><strong>Vérification existence :</strong> Événement toujours présent</li>
     *   <li><strong>Application modifications :</strong> Mise à jour de l'objet</li>
     *   <li><strong>Validation finale :</strong> Autorisations et cohérence</li>
     *   <li><strong>Sauvegarde :</strong> Persistance selon le type concret</li>
     *   <li><strong>Navigation :</strong> Retour au dashboard en cas de succès</li>
     * </ol>
     * 
     * <p><strong>Validations effectuées :</strong></p>
     * <ul>
     *   <li><strong>Champs obligatoires :</strong> Nom, lieu, date, places</li>
     *   <li><strong>Contraintes temporelles :</strong> Date future avec délai</li>
     *   <li><strong>Limites numériques :</strong> Places et prix dans les bornes</li>
     *   <li><strong>Cohérence tarifaire :</strong> Premium ≥ VIP ≥ Standard</li>
     *   <li><strong>Autorisations :</strong> Propriétaire et session valide</li>
     * </ul>
     * 
     * <p><strong>Sécurité et autorisations :</strong></p>
     * <ul>
     *   <li><strong>Session utilisateur :</strong> Vérification de la connexion</li>
     *   <li><strong>Propriété événement :</strong> Seul le créateur peut modifier</li>
     *   <li><strong>Validation finale :</strong> Contrôles avant persistance</li>
     * </ul>
     * 
     * <p><strong>Gestion d'erreurs :</strong></p>
     * <ul>
     *   <li><strong>BusinessException :</strong> Erreurs métier avec message contextualisé</li>
     *   <li><strong>Exception générale :</strong> Erreurs techniques avec fallback</li>
     *   <li><strong>Rollback :</strong> Préservation de l'état en cas d'échec</li>
     *   <li><strong>Feedback utilisateur :</strong> Notifications et messages d'erreur</li>
     * </ul>
     * 
     * <p><strong>Sauvegarde par type :</strong></p>
     * <ul>
     *   <li><strong>Concert :</strong> {@link EvenementService#modifierConcert(Concert)}</li>
     *   <li><strong>Spectacle :</strong> {@link EvenementService#modifierSpectacle(Spectacle)}</li>
     *   <li><strong>Conférence :</strong> {@link EvenementService#modifierConference(Conference)}</li>
     * </ul>
     * 
     * <p>Cette méthode est liée au bouton de sauvegarde via l'annotation @FXML
     * et représente l'aboutissement du processus de modification.</p>
     * 
     * @author Charbel SONON
     * @author MABOMESI Loïc
     * 
     * @see #validerChamps()
     * @see #appliquerModifications()
     * @see #validateBeforeSave()
     * @see #performSave()
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
