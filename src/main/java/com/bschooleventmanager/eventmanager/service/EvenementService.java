package com.bschooleventmanager.eventmanager.service;

import com.bschooleventmanager.eventmanager.dao.ConcertDAO;
import com.bschooleventmanager.eventmanager.dao.ConferenceDAO;
import com.bschooleventmanager.eventmanager.dao.EvenementDAO;
import com.bschooleventmanager.eventmanager.dao.SpectacleDAO;
import com.bschooleventmanager.eventmanager.exception.BusinessException;
import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import com.bschooleventmanager.eventmanager.model.*;
import com.bschooleventmanager.eventmanager.model.enums.StatutEvenement;
import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;
import com.bschooleventmanager.eventmanager.util.NotificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class EvenementService {
    private static final Logger logger = LoggerFactory.getLogger(EvenementService.class);
    private static final EvenementDAO evenementDAO = new EvenementDAO();
    private static final ConcertDAO concertDAO = new ConcertDAO();
    private static final SpectacleDAO spectacleDao = new SpectacleDAO();
    private static final ConferenceDAO conferenceDAO = new ConferenceDAO();

    public List<Evenement> getAllEvents() {
        logger.info("Service: Fetching all events...");
        List<Evenement> list = evenementDAO.getAllEvents();
        logger.info("Service: {} events retrieved : " + list.size());
        return list;
    }

       /**
     * Supprime un événement identifié par son identifiant.
     *
     * <p>Cette méthode loggue l'appel puis délègue la suppression au DAO
     * `EvenementDAO`. L'exception SQLException par le DAO
     * est propagée au caller.</p>
     *
     * @param idEvent identifiant de l'événement à supprimer
     * @throws RuntimeException si le DAO lève une exception non contrôlée
     */
    public void suppEvent(int idEvent){
        logger.info("Fonction suppEven dans le service EvenementService");
        evenementDAO.suppEvent(idEvent);

    }


    /**
     * Créer un nouveau concert 
     */
    public Concert creerConcert(Concert concert) throws BusinessException {
        try {
            // Validation des données
            validerDonneesEvenement(concert.getNom(), concert.getDateEvenement(), concert.getLieu(), concert.getPlacesStandardDisponibles(), concert.getPlacesVipDisponibles(), concert.getPlacesPremiumDisponibles());



            Concert result = concertDAO.creer(concert);
            
            logger.info("✓ Concert créé: {}", concert.getNom());
            return result;

        } catch (DatabaseException e) {
            logger.error("Erreur création concert", e);
            throw new BusinessException("Erreur lors de la création du concert", e);
        }
    }

    /**
     * Modifier un concert
     * @param concert
     */
    public void modifierConcert(Concert concert) throws BusinessException {
        try {
            // Validation des données
            validerDonneesEvenement(concert.getNom(), concert.getDateEvenement(), concert.getLieu(), concert.getPlacesStandardDisponibles(), concert.getPlacesVipDisponibles(), concert.getPlacesPremiumDisponibles());
            concertDAO.mettreAJourC(concert);

            logger.info("✓ Concert modifé: {}", concert.getNom());
            NotificationUtils.showSuccess("Modification du concert réussie !");
            //return result;

        } catch (DatabaseException e) {
            logger.error("Erreur modification concert", e);
            throw new BusinessException("Erreur lors de la modif du concert", e);
        }
    }

    /**
     * Modifier une conférence
     * @param conference
     */
    public void modifierConference(Conference conference) throws BusinessException {
        try {
            // Validation des données
            validerDonneesEvenement(conference.getNom(), conference.getDateEvenement(), conference.getLieu(), conference.getPlacesStandardDisponibles(), conference.getPlacesVipDisponibles(), conference.getPlacesPremiumDisponibles());
            conferenceDAO.mettreAJourC(conference);

            logger.info("✓ Concert modifé: {}", conference.getNom());
            NotificationUtils.showSuccess("Modification de la conférence réussie !");

        } catch (DatabaseException e) {
            logger.error("Erreur modification concert", e);
            throw new BusinessException("Erreur lors de la modif du concert", e);
        }
    }

    /**
     * Modifier un spectacle
     * @param spectacle
     */
    public void modifierSpectacle(Spectacle spectacle) throws BusinessException {
        try {
            // Validation des données
            validerDonneesEvenement(spectacle.getNom(), spectacle.getDateEvenement(), spectacle.getLieu(), spectacle.getPlacesStandardDisponibles(), spectacle.getPlacesVipDisponibles(), spectacle.getPlacesPremiumDisponibles());
            spectacleDao.mettreAJourC(spectacle);

            logger.info("✓ Concert modifé: {}", spectacle.getNom());
            NotificationUtils.showSuccess("Modification du spectacle réussie !");
            //return result;

        } catch (DatabaseException e) {
            logger.error("Erreur modification concert", e);
            throw new BusinessException("Erreur lors de la modif du concert", e);
        }
    }


    /**
     * Créer une nouvelle conférence
     */
    public Conference creerConference(Conference conference) throws BusinessException {
        try {
            // Validation des données
            validerDonneesEvenement(conference.getNom(), conference.getDateEvenement(), conference.getLieu(), conference.getPlacesStandardDisponibles(), conference.getPlacesVipDisponibles(), conference.getPlacesPremiumDisponibles());


            Conference result = (Conference) conferenceDAO.creer(conference);
            logger.info("✓ Conférence créée: {}", conference.getNom());
            return result;

        } catch (DatabaseException e) {
            logger.error("Erreur création conférence", e);
            throw new BusinessException("Erreur lors de la création de la conférence", e);
        }
    }



    /**
    /**
     * Créer un nouveau spectacle
     */
    public Spectacle creerSpectacle(Spectacle spectacle) throws BusinessException {
        try {
            // Validation des données
            validerDonneesEvenement(spectacle.getNom(), spectacle.getDateEvenement(), spectacle.getLieu(), spectacle.getPlacesStandardDisponibles(), spectacle.getPlacesVipDisponibles(), spectacle.getPlacesPremiumDisponibles());

            Spectacle result = (Spectacle) spectacleDao.creer(spectacle);
            logger.info("✓ Spectacle créé: {}", spectacle.getNom());
            return result;

        } catch (DatabaseException e) {
            logger.error("Erreur création spectacle", e);
            throw new BusinessException("Erreur lors de la création du spectacle", e);
        }
    }



    /**
     * Récupérer un événement par ID
     */
    public Evenement getEvenement(int id) throws BusinessException {
        try {
            Evenement evenement = evenementDAO.chercher(id);
            if (evenement == null) {
                throw new BusinessException("Événement non trouvé");
            }
            return evenement;
        } catch (DatabaseException e) {
            logger.error("Erreur récupération événement", e);
            throw new BusinessException("Erreur récupération événement", e);
        }
    }
    /**
     * Récupérer un événement par ID
     */
    public EventTotal getEvenementT(int id) throws BusinessException {
        try {
            EventTotal evenement = evenementDAO.chercherTotal(id);
            if (evenement == null) {
                throw new BusinessException("Événement non trouvé");
            }
            return evenement;
        } catch (DatabaseException e) {
            logger.error("Erreur récupération événement", e);
            throw new BusinessException("Erreur récupération événement", e);
        }
    }

    /**
     * Récupérer un concert par ID
     */
    public Concert getConcertById(int id) throws BusinessException {
        try {
            Concert concert = concertDAO.chercher(id);
            if (concert == null) {
                throw new BusinessException("Concert non trouvé");
            }
            return concert;
        } catch (DatabaseException e) {
            logger.error("Erreur récupération événement", e);
            throw new BusinessException("Erreur récupération événement", e);
        }
    }

    /**
     * Lister les événements d'un organisateur
     */
    public List<Evenement> getEvenementsParOrganisateur(int organisateurId) throws BusinessException {
        try {
            return evenementDAO.getEventsByOrganizerId(organisateurId);
        } catch (DatabaseException e) {
            logger.error("Erreur récupération événements organisateur", e);
            throw new BusinessException("Erreur récupération événements organisateur", e);
        }
       
    }

    /**
     * Lister les événements par type
     */
    public List<Evenement> getEvenementsParType(TypeEvenement type) throws BusinessException {
        try {
            return evenementDAO.getEventByType(type);
        } catch (DatabaseException e) {
            logger.error("Erreur récupération événements par type", e);
            throw new BusinessException("Erreur récupération événements par type", e);
        }
       
    }

    /**
     * Annuler un événement
     */
    public void annulerEvenement(int idEvenement) throws BusinessException {
        try {
            Evenement evenement = getEvenement(idEvenement);
            
            if (!evenement.peutEtreAnnule()) {
                throw new BusinessException("Cet événement ne peut plus être annulé");
            }

            evenement.setStatut(StatutEvenement.ANNULE);
            evenementDAO.mettreAJour(evenement);
            
            logger.info("✓ Événement annulé: {}", evenement.getNom());

        } catch (DatabaseException e) {
            logger.error("Erreur annulation événement", e);
            throw new BusinessException("Erreur lors de l'annulation", e);
        }
        catch (BusinessException e) {
            NotificationUtils.showError(e.getMessage());
        }
    }

    /**
     * Modifier un événement
     */
    public void updateEvent(Evenement evenement) throws BusinessException {
        try {
            if (evenement.getStatut() == StatutEvenement.TERMINE) {
                throw new BusinessException("Impossible de modifier un événement terminé");
            }

            evenementDAO.mettreAJour(evenement);
            logger.info("✓ Événement modifié: {}", evenement.getNom());

        } catch (DatabaseException e) {
            logger.error("Erreur modification événement", e);
            throw new BusinessException("Erreur modification événement", e);
        }
        catch (BusinessException e) {
            NotificationUtils.showError(e.getMessage());
        }
    }


    // Méthodes privées pour la validation

    /**
     * Validation simple des données d'événement
     */
    private static void validerDonneesEvenement(String nom, LocalDateTime dateEvenement, String lieu,
                                         int placesStandard, int placesVip, int placesPremium) throws BusinessException {
        if (nom == null || nom.trim().isEmpty()) {
            throw new BusinessException("Le nom de l'événement ne peut pas être vide");
        }

        if (dateEvenement == null) {
            throw new BusinessException("La date de l'événement est obligatoire");
        }

        if (dateEvenement.isBefore(LocalDateTime.now())) {
            throw new BusinessException("La date de l'événement ne peut pas être dans le passé");
        }

        if (lieu == null || lieu.trim().isEmpty()) {
            throw new BusinessException("Le lieu de l'événement ne peut pas être vide");
        }

        if (placesStandard < 0 || placesVip < 0 || placesPremium < 0) {
            throw new BusinessException("Le nombre de places ne peut pas être négatif");
        }

        if (placesStandard + placesVip + placesPremium == 0) {
            throw new BusinessException("L'événement doit avoir au moins une place disponible");
        }
    }
}

