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

/**
 * Contrôleur pour la création d'événements dans EventManager.
 * 
 * <p>Cette classe gère l'intégralité du processus de création d'événements
 * avec support des trois types d'événements : concerts, spectacles et conférences.
 * Elle fournit une interface utilisateur dynamique qui s'adapte au type d'événement
 * sélectionné et applique une validation complète des données.</p>
 * 
 * <p><strong>Types d'événements supportés :</strong></p>
 * <ul>
 *   <li><strong>Concert :</strong> Avec artiste/groupe, type musical, âge minimum</li>
 *   <li><strong>Spectacle :</strong> Avec troupe/artiste, type de spectacle, âge minimum</li>
 *   <li><strong>Conférence :</strong> Avec domaine, intervenant, niveau d'expertise</li>
 * </ul>
 * 
 * <p><strong>Fonctionnalités principales :</strong></p>
 * <ul>
 *   <li>Interface adaptative selon le type d'événement</li>
 *   <li>Validation en temps réel des champs numériques</li>
 *   <li>Gestion des trois catégories de places (Standard, VIP, Premium)</li>
 *   <li>Validation complète avec messages d'erreur contextuels</li>
 *   <li>Intégration avec les services métier EventManager</li>
 * </ul>
 * 
 * <p><strong>Validation et contraintes :</strong></p>
 * <ul>
 *   <li><strong>Champs obligatoires :</strong> Nom, lieu, date, type, au moins une place</li>
 *   <li><strong>Contraintes temporelles :</strong> Événements futurs uniquement (min 1h)</li>
 *   <li><strong>Limites numériques :</strong> Places (0-50000), Prix (0-10000€)</li>
 *   <li><strong>Cohérence tarifaire :</strong> Premium ≥ VIP ≥ Standard</li>
 *   <li><strong>Validation spécifique :</strong> Selon le type d'événement</li>
 * </ul>
 * 
 * <p><strong>Interface utilisateur dynamique :</strong></p>
 * <ul>
 *   <li>Champs conditionnels affichés selon le type sélectionné</li>
 *   <li>Spinners pour date/heure avec contraintes automatiques</li>
 *   <li>ComboBox alimentées dynamiquement depuis les enums</li>
 *   <li>Validation numérique avec formatage automatique</li>
 *   <li>Messages d'erreur spécifiques par champ</li>
 * </ul>
 * 
 * <p><strong>Architecture de validation :</strong></p>
 * <ol>
 *   <li><strong>Validation de base :</strong> Champs obligatoires et formats</li>
 *   <li><strong>Validation temporelle :</strong> Date et heure cohérentes</li>
 *   <li><strong>Validation numérique :</strong> Places et prix dans les limites</li>
 *   <li><strong>Validation spécifique :</strong> Selon le type d'événement</li>
 *   <li><strong>Validation finale :</strong> Cohérence globale des données</li>
 * </ol>
 * 
 * <p><strong>Gestion d'erreurs :</strong></p>
 * <ul>
 *   <li>Messages contextuels par champ avec labels dédiés</li>
 *   <li>Validation progressive avec arrêt sur première erreur</li>
 *   <li>Logging détaillé pour traçage et débogage</li>
 *   <li>Notifications utilisateur avec NotificationUtils</li>
 * </ul>
 * 
 * <p><strong>Intégration services :</strong></p>
 * <ul>
 *   <li><strong>EvenementService :</strong> Création et gestion des événements</li>
 *   <li><strong>SessionManager :</strong> Récupération de l'organisateur connecté</li>
 *   <li><strong>NotificationUtils :</strong> Feedback utilisateur temps réel</li>
 * </ul>
 * 
 * <p><strong>Workflow de création :</strong></p>
 * <ol>
 *   <li>Sélection du type d'événement (affichage conditionnel)</li>
 *   <li>Saisie des informations générales (nom, lieu, date, description)</li>
 *   <li>Configuration des places et tarifs par catégorie</li>
 *   <li>Saisie des informations spécifiques au type</li>
 *   <li>Validation complète avec feedback temps réel</li>
 *   <li>Création via service métier et redirection</li>
 * </ol>
 * 
 * <p><strong>Exemple d'utilisation :</strong></p>
 * <pre>{@code
 * CreateEventController controller = loader.getController();
 * controller.setDashboardController(dashboardController);
 * // L'interface s'initialise automatiquement avec les contraintes
 * }</pre>
 * 
 * @author Charbel SONON
 * @author MABOMESI Loïc
 * @version 1.0
 * @since 1.0
 * 
 * @see ModifyEventController
 * @see com.bschooleventmanager.eventmanager.service.EvenementService
 * @see com.bschooleventmanager.eventmanager.model.Concert
 * @see com.bschooleventmanager.eventmanager.model.Spectacle
 * @see com.bschooleventmanager.eventmanager.model.Conference
 * @see com.bschooleventmanager.eventmanager.controller.organisateur.OrganisateurDashboardController
 */
public class CreateEventController {
    // Labels pour champs conditionnels selon le type d'événement
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
    
    // Champs de saisie principaux
    /** Champ de saisie pour le nom de l'événement. */
    @FXML private TextField tfNom;
    /** Champ de saisie pour le lieu de l'événement. */
    @FXML private TextField tfLieu;
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
    /** Champ de saisie pour le nombre de places standard. */
    @FXML private TextField nbPlacesStandard;
    /** Champ de saisie pour l'artiste ou le groupe (concerts/spectacles). */
    @FXML private TextField tfArtits;
    /** Champ de saisie pour le domaine de la conférence. */
    @FXML private TextField Domaine;
    /** Champ de saisie pour l'intervenant de la conférence. */
    @FXML private TextField Intervenant;
    /** Champ d'affichage de l'ID d'événement (utilisé en modification). */
    @FXML private TextField tfIdEvent;
    
    /** Zone de texte pour la description détaillée de l'événement. */
    @FXML private TextArea Description;
    
    /** Sélecteur de date pour la date de l'événement. */
    @FXML private DatePicker dpDate;
    
    // Spinners pour heure et âge
    /** Spinner pour la sélection de l'heure (0-23). */
    @FXML private Spinner<Integer> spHour;
    /** Spinner pour la sélection des minutes (0-59). */
    @FXML private Spinner<Integer> spMinute;
    
    // ComboBox pour les sélections
    /** ComboBox pour la sélection du type d'événement. */
    @FXML private ComboBox<String> evType;
    /** ComboBox pour la sélection du niveau d'expertise (conférences). */
    @FXML private ComboBox<String> nvExpert;
    /** ComboBox pour la sélection du type de concert. */
    @FXML private ComboBox<String> tyConcert;
    /** ComboBox pour la sélection du type de spectacle. */
    @FXML private ComboBox<String> tySpectacle;
    /** ComboBox pour la sélection de l'âge minimum. */
    @FXML private ComboBox<Integer> tfAge;
    
    /** Label d'erreur global pour messages d'erreur généraux. */
    @FXML private Label lblError;
    /** Image de prévisualisation (fonctionnalité future). */
    @FXML private ImageView imgPreview;
    
    /** Logger pour traçage des opérations de création d'événements. */
    private static final Logger logger = LoggerFactory.getLogger(CreateEventController.class);

    /** Service métier pour la gestion des événements. */
    private EvenementService evenementService = new EvenementService();

    /** Référence vers le contrôleur de tableau de bord organisateur pour navigation. */
    private com.bschooleventmanager.eventmanager.controller.organisateur.OrganisateurDashboardController dashboardController;

    /**
     * Constructeur par défaut requis par JavaFX.
     * 
     * <p>Ce constructeur est automatiquement appelé par JavaFX lors du chargement
     * du fichier FXML. L'initialisation des composants est effectuée dans la
     * méthode {@link #initialize()}.</p>
     */
    public CreateEventController() {
        // Constructeur requis par JavaFX
    }

    /**
     * Initialise les composants de l'interface de création d'événements.
     * 
     * <p>Cette méthode est appelée automatiquement par JavaFX après le chargement
     * du fichier FXML. Elle configure tous les contrôles de l'interface avec leurs
     * valeurs par défaut et établit les mécanismes de validation.</p>
     * 
     * <p><strong>Configuration effectuée :</strong></p>
     * <ul>
     *   <li><strong>Spinners :</strong> Configuration des plages horaires (0-23h, 0-59min)</li>
     *   <li><strong>ComboBox :</strong> Chargement dynamique depuis les enums</li>
     *   <li><strong>Validation numérique :</strong> Formatage automatique des champs</li>
     *   <li><strong>Interface adaptative :</strong> Configuration initiale des champs conditionnels</li>
     * </ul>
     * 
     * <p><strong>Sources de données :</strong></p>
     * <ul>
     *   <li>TypeEvenement.values() → ComboBox types d'événements</li>
     *   <li>TypeConcert.values() → ComboBox types de concert</li>
     *   <li>TypeSpectacle.values() → ComboBox types de spectacle</li>
     *   <li>NiveauExpertise.values() → ComboBox niveaux d'expertise</li>
     *   <li>Plage 5-18 ans → ComboBox âges minimum</li>
     * </ul>
     * 
     * <p><strong>Validation configurée :</strong></p>
     * <ul>
     *   <li>Champs places : entiers uniquement (TextFormatter)</li>
     *   <li>Champs prix : décimaux avec 2 décimales max</li>
     *   <li>Formatage automatique avec validation temps réel</li>
     * </ul>
     * 
     * @see #loadComboBoxValues()
     * @see #setupNumericValidation()
     * @see javafx.fxml.Initializable
     */
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
     * Charge dynamiquement les valeurs des ComboBox depuis les énumérations.
     * 
     * <p>Cette méthode initialise toutes les ComboBox de l'interface avec les valeurs
     * appropriées extraites des énumérations du modèle. Elle garantit la cohérence
     * entre l'interface utilisateur et les valeurs métier définies dans le code.</p>
     * 
     * <p><strong>ComboBox configurées :</strong></p>
     * <ul>
     *   <li><strong>evType :</strong> Types d'événements depuis {@link TypeEvenement}</li>
     *   <li><strong>tyConcert :</strong> Types de concerts depuis {@link TypeConcert}</li>
     *   <li><strong>tySpectacle :</strong> Types de spectacles depuis {@link TypeSpectacle}</li>
     *   <li><strong>nvExpert :</strong> Niveaux d'expertise depuis {@link NiveauExpertise}</li>
     *   <li><strong>tfAge :</strong> Âges minimum de 5 à 18 ans</li>
     * </ul>
     * 
     * <p><strong>Avantages du chargement dynamique :</strong></p>
     * <ul>
     *   <li>Synchronisation automatique avec les énumérations</li>
     *   <li>Évite la duplication de données</li>
     *   <li>Facilite la maintenance et l'évolution</li>
     *   <li>Garantit la cohérence des valeurs</li>
     * </ul>
     * 
     * <p>Cette méthode est appelée automatiquement lors de l'initialisation
     * de l'interface dans {@link #initialize()}.</p>
     * 
     * @see TypeEvenement
     * @see TypeConcert
     * @see TypeSpectacle
     * @see NiveauExpertise
     * @see #initialize()
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
     * Configure la validation numérique pour les champs de places et prix.
     * 
     * <p>Cette méthode applique des {@link TextFormatter} aux champs numériques
     * pour garantir la saisie de valeurs correctes en temps réel. Elle empêche
     * la saisie de caractères invalides et formate automatiquement les valeurs.</p>
     * 
     * <p><strong>Types de validation appliqués :</strong></p>
     * <ul>
     *   <li><strong>Champs de places :</strong> Entiers uniquement (0-9)</li>
     *   <li><strong>Champs de prix :</strong> Décimaux avec point et max 2 décimales</li>
     * </ul>
     * 
     * <p><strong>Champs configurés :</strong></p>
     * <ul>
     *   <li><em>Places :</em> nbPlacesStandard, nbPlacesVip, nbPlacesPremium</li>
     *   <li><em>Prix :</em> prixPlaceStandard, prixPlaceVip, prixPlacePremium</li>
     * </ul>
     * 
     * <p><strong>Validation en temps réel :</strong></p>
     * <ul>
     *   <li>Rejet automatique des caractères invalides</li>
     *   <li>Respect des formats numériques attendus</li>
     *   <li>Limitation des décimales pour les prix</li>
     *   <li>Amélioration de l'expérience utilisateur</li>
     * </ul>
     * 
     * <p>Cette méthode est appelée lors de l'initialisation de l'interface
     * dans {@link #initialize()} pour configurer tous les champs numériques.</p>
     * 
     * @see #setupIntegerField(TextField)
     * @see #setupDecimalField(TextField)
     * @see #initialize()
     * @see javafx.scene.control.TextFormatter
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
     * Configure un champ TextField pour accepter uniquement des entiers.
     * 
     * <p>Cette méthode applique un {@link TextFormatter} à un champ de texte
     * pour limiter la saisie aux chiffres uniquement. Elle utilise une expression
     * régulière pour valider en temps réel les modifications de texte.</p>
     * 
     * <p><strong>Comportement :</strong></p>
     * <ul>
     *   <li><strong>Caractères acceptés :</strong> 0-9 uniquement</li>
     *   <li><strong>Caractères rejetés :</strong> Lettres, symboles, espaces</li>
     *   <li><strong>Validation :</strong> Expression régulière "\\d*"</li>
     *   <li><strong>Feedback :</strong> Rejet silencieux des caractères invalides</li>
     * </ul>
     * 
     * <p><strong>Utilisation typique :</strong></p>
     * <ul>
     *   <li>Champs de quantité de places</li>
     *   <li>Identifiants numériques</li>
     *   <li>Compteurs et index</li>
     * </ul>
     * 
     * @param field Le champ TextField à configurer, peut être null (pas d'effet)
     * 
     * @see #setupDecimalField(TextField)
     * @see #setupNumericValidation()
     * @see javafx.scene.control.TextFormatter
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
     * Configure un champ TextField pour accepter uniquement des nombres décimaux.
     * 
     * <p>Cette méthode applique un {@link TextFormatter} à un champ de texte
     * pour permettre la saisie de nombres décimaux avec au maximum deux chiffres
     * après la virgule. Elle utilise une expression régulière sophistiquée
     * pour valider le format en temps réel.</p>
     * 
     * <p><strong>Format accepté :</strong></p>
     * <ul>
     *   <li><strong>Entiers :</strong> 123, 0, 9999</li>
     *   <li><strong>Décimaux :</strong> 25.50, 0.99, 100.00</li>
     *   <li><strong>Partie entière :</strong> Illimitée</li>
     *   <li><strong>Partie décimale :</strong> Maximum 2 chiffres</li>
     * </ul>
     * 
     * <p><strong>Validation :</strong></p>
     * <ul>
     *   <li><strong>Expression régulière :</strong> "\\d*\\.?\\d{0,2}"</li>
     *   <li><strong>Séparateur décimal :</strong> Point (.) uniquement</li>
     *   <li><strong>Rejet automatique :</strong> Formats invalides</li>
     * </ul>
     * 
     * <p><strong>Utilisation typique :</strong></p>
     * <ul>
     *   <li>Champs de prix et tarifs</li>
     *   <li>Montants monétaires</li>
     *   <li>Pourcentages et ratios</li>
     * </ul>
     * 
     * @param field Le champ TextField à configurer, peut être null (pas d'effet)
     * 
     * @see #setupIntegerField(TextField)
     * @see #setupNumericValidation()
     * @see javafx.scene.control.TextFormatter
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
     * Définit la référence vers le contrôleur de tableau de bord organisateur.
     * 
     * <p>Cette méthode est appelée lors de l'initialisation du contrôleur pour
     * établir la liaison avec le contrôleur parent, permettant ainsi la navigation
     * de retour vers le tableau de bord après création d'un événement ou annulation.</p>
     * 
     * <p><strong>Utilisation :</strong></p>
     * <ul>
     *   <li>Navigation de retour après création réussie</li>
     *   <li>Retour en cas d'annulation utilisateur</li>
     *   <li>Gestion des transitions entre vues</li>
     *   <li>Préservation du contexte organisateur</li>
     * </ul>
     * 
     * @param dashboardController Le contrôleur de tableau de bord organisateur
     * 
     * @see #returnToDashboard()
     * @see com.bschooleventmanager.eventmanager.controller.organisateur.OrganisateurDashboardController
     */
    public void setDashboardController(com.bschooleventmanager.eventmanager.controller.organisateur.OrganisateurDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    /**
     * Effectue le retour vers le tableau de bord organisateur.
     * 
     * <p>Cette méthode gère la navigation de retour vers le tableau de bord principal
     * de l'organisateur. Elle est appelée après une création réussie d'événement
     * ou lors d'une annulation par l'utilisateur.</p>
     * 
     * <p><strong>Comportement :</strong></p>
     * <ul>
     *   <li>Vérification de la validité de la référence dashboard</li>
     *   <li>Appel de la méthode showDashboard() du contrôleur parent</li>
     *   <li>Transition fluide entre les vues</li>
     *   <li>Préservation du contexte utilisateur</li>
     * </ul>
     * 
     * <p>Cette méthode est liée au bouton de retour via l'annotation @FXML.</p>
     * 
     * @see #setDashboardController(com.bschooleventmanager.eventmanager.controller.organisateur.OrganisateurDashboardController)
     * @see com.bschooleventmanager.eventmanager.controller.organisateur.OrganisateurDashboardController#showDashboard()
     */
    @FXML
    private void returnToDashboard() {
        if (dashboardController != null) {
            dashboardController.showDashboard();
        }
    }



    /**
     * Gère l'adaptation de l'interface selon le type d'événement sélectionné.
     * 
     * <p>Cette méthode est automatiquement appelée lors du changement de sélection
     * dans la ComboBox du type d'événement. Elle modifie dynamiquement l'interface
     * pour afficher uniquement les champs pertinents au type sélectionné.</p>
     * 
     * <p><strong>Comportement par type d'événement :</strong></p>
     * <ul>
     *   <li><strong>CONCERT :</strong>
     *     <ul>
     *       <li>Affichage : âge minimum, artiste/groupe, type de concert</li>
     *       <li>Masquage : type spectacle, niveau expertise, domaine, intervenant</li>
     *     </ul>
     *   </li>
     *   <li><strong>SPECTACLE :</strong>
     *     <ul>
     *       <li>Affichage : âge minimum, artiste/troupe, type de spectacle</li>
     *       <li>Masquage : type concert, niveau expertise, domaine, intervenant</li>
     *     </ul>
     *   </li>
     *   <li><strong>CONFERENCE :</strong>
     *     <ul>
     *       <li>Affichage : niveau expertise, domaine, intervenant</li>
     *       <li>Masquage : âge minimum, artiste, types concert/spectacle</li>
     *     </ul>
     *   </li>
     * </ul>
     * 
     * <p><strong>Interface adaptative :</strong></p>
     * <ul>
     *   <li>Gestion de la visibilité (visible/invisible)</li>
     *   <li>Gestion du layout (managed/unmanaged)</li>
     *   <li>Optimisation de l'espace d'affichage</li>
     *   <li>Expérience utilisateur simplifiée</li>
     * </ul>
     * 
     * <p>Cette méthode améliore l'utilisabilité en ne présentant que les champs
     * nécessaires selon le contexte, réduisant la complexité perçue de l'interface.</p>
     * 
     * @see TypeEvenement
     * @see javafx.scene.Node#setVisible(boolean)
     * @see javafx.scene.Node#setManaged(boolean)
     */
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
     * Supprime un événement par son identifiant.
     * 
     * <p>Cette méthode utilise le service EvenementService pour supprimer
     * définitivement un événement de la base de données. Elle inclut une
     * gestion d'erreurs complète avec logging approprié.</p>
     * 
     * <p><strong>Processus de suppression :</strong></p>
     * <ol>
     *   <li>Validation de l'ID événement</li>
     *   <li>Appel au service métier pour suppression</li>
     *   <li>Gestion des exceptions et logging</li>
     *   <li>Retour d'information à l'utilisateur si nécessaire</li>
     * </ol>
     * 
     * <p><strong>Sécurité et validation :</strong></p>
     * <ul>
     *   <li>Vérification des permissions organisateur</li>
     *   <li>Validation de l'existence de l'événement</li>
     *   <li>Gestion des contraintes référentielles</li>
     *   <li>Logging des opérations pour audit</li>
     * </ul>
     * 
     * <p><strong>Gestion d'erreurs :</strong></p>
     * <ul>
     *   <li>BusinessException : erreurs métier (permissions, contraintes)</li>
     *   <li>Exception générale : erreurs techniques inattendues</li>
     *   <li>Logging détaillé pour diagnostic</li>
     * </ul>
     * 
     * @param idEvent L'identifiant unique de l'événement à supprimer
     * @throws BusinessException Si une règle métier empêche la suppression
     * @author MABOMESI Loïc
     * 
     * @see EvenementService#suppEvent(int)
     * @see com.bschooleventmanager.eventmanager.exception.BusinessException
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
     * Méthode principale de création d'événement avec validation complète.
     * 
     * <p>Cette méthode orchestre l'intégralité du processus de création d'un événement,
     * de la validation des données jusqu'à la création effective en base de données.
     * Elle implémente une validation en plusieurs étapes et une gestion d'erreurs robuste.</p>
     * 
     * <p><strong>Workflow de création :</strong></p>
     * <ol>
     *   <li><strong>Effacement des erreurs :</strong> Reset des messages précédents</li>
     *   <li><strong>Validation formulaire :</strong> Contrôle complet des saisies</li>
     *   <li><strong>Chargement données :</strong> Extraction des valeurs communes</li>
     *   <li><strong>Validation finale :</strong> Cohérence globale des données</li>
     *   <li><strong>Création spécialisée :</strong> Selon le type d'événement</li>
     *   <li><strong>Navigation :</strong> Retour au dashboard en cas de succès</li>
     * </ol>
     * 
     * <p><strong>Types de validation effectués :</strong></p>
     * <ul>
     *   <li><strong>Champs obligatoires :</strong> Nom, lieu, date, type</li>
     *   <li><strong>Formats numériques :</strong> Places et prix valides</li>
     *   <li><strong>Contraintes temporelles :</strong> Date future (min 1h)</li>
     *   <li><strong>Cohérence tarifaire :</strong> Premium ≥ VIP ≥ Standard</li>
     *   <li><strong>Validation spécifique :</strong> Selon le type d'événement</li>
     * </ul>
     * 
     * <p><strong>Création par type :</strong></p>
     * <ul>
     *   <li><strong>Concert :</strong> Validation artiste, type musical, âge</li>
     *   <li><strong>Spectacle :</strong> Validation troupe, type spectacle, âge</li>
     *   <li><strong>Conférence :</strong> Validation domaine, expertise, intervenant</li>
     * </ul>
     * 
     * <p><strong>Gestion d'erreurs :</strong></p>
     * <ul>
     *   <li><strong>BusinessException :</strong> Erreurs métier avec message contextualisé</li>
     *   <li><strong>Exception générale :</strong> Erreurs techniques avec fallback</li>
     *   <li><strong>Feedback utilisateur :</strong> Notifications et labels d'erreur</li>
     *   <li><strong>Logging :</strong> Traçage complet pour diagnostic</li>
     * </ul>
     * 
     * <p><strong>Intégrations :</strong></p>
     * <ul>
     *   <li><strong>SessionManager :</strong> Récupération organisateur connecté</li>
     *   <li><strong>EvenementService :</strong> Création en base de données</li>
     *   <li><strong>NotificationUtils :</strong> Feedback utilisateur temps réel</li>
     * </ul>
     * 
     * <p>Cette méthode est le point d'entrée principal du processus de création
     * et garantit une expérience utilisateur fluide avec validation robuste.</p>
     * 
     * @throws BusinessException Si une règle métier empêche la création
     * 
     * @author MABOMESI Loïc
     * @author Charbel SONON
     * 
     * @see #validateForm()
     * @see #loadCommonValues()
     * @see #performFinalValidation(EventBaseData)
     * @see #createConcert(int, EventBaseData)
     * @see #createSpectacle(int, EventBaseData)
     * @see #createConference(int, EventBaseData)
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
