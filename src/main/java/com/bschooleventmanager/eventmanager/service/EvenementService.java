package com.bschooleventmanager.eventmanager.service;

import com.bschooleventmanager.eventmanager.dao.EvenementDAO;
import com.bschooleventmanager.eventmanager.exception.BusinessException;
import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import com.bschooleventmanager.eventmanager.model.*;
import com.bschooleventmanager.eventmanager.model.enums.StatutEvenement;
import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class EvenementService {
    private static final Logger logger = LoggerFactory.getLogger(EvenementService.class);

    private final EvenementDAO evenementDAO = new EvenementDAO();

    public List<Evenement> getAllEvents() {
        logger.info("Service: Fetching all events...");
        List<Evenement> list = evenementDAO.getAllEvents();
        logger.info("Service: {} events retrieved : " + list.size());
        return list;
    }


    /**
     * Créer un nouveau concert 
     */
    public Concert creerConcert(int organisateurId, String nom, LocalDateTime dateEvenement, 
                               String lieu, String description, int placesStandard, 
                               int placesVip, int placesPremium, BigDecimal prixStandard,
                               BigDecimal prixVip, BigDecimal prixPremium) throws BusinessException {
        try {
            // Validation des données
            validerDonneesEvenement(nom, dateEvenement, lieu, placesStandard, placesVip, placesPremium);

            // Création du concert
            Concert concert = new Concert(organisateurId, nom, dateEvenement, lieu, description, placesStandard, placesVip, placesPremium, prixStandard, prixVip, prixPremium);
        

            Concert result = (Concert) evenementDAO.creer(concert);
            logger.info("✓ Concert créé: {}", nom);
            return result;

        } catch (DatabaseException e) {
            logger.error("Erreur création concert", e);
            throw new BusinessException("Erreur lors de la création du concert", e);
        }
    }


    /**
     * Créer une nouvelle conférence
     */
    public Conference creerConference(int organisateurId, String nom, LocalDateTime dateEvenement, 
                                     String lieu, String description, int placesStandard, 
                                     int placesVip, int placesPremium, BigDecimal prixStandard, BigDecimal prixPremium, BigDecimal prixVip) throws BusinessException {
        try {
            // Validation des données
            validerDonneesEvenement(nom, dateEvenement, lieu, placesStandard, placesVip, placesPremium);

            // Création de la conférence
            Conference conference = new Conference(organisateurId, 
                     nom, 
                     dateEvenement, 
                     lieu,
                     description,
                     placesStandard,
                     placesVip,
                     placesPremium,
                     prixStandard,
                     prixVip,
                     prixPremium);
            

            Conference result = (Conference) evenementDAO.creer(conference);
            logger.info("✓ Conférence créée: {}", nom);
            return result;

        } catch (DatabaseException e) {
            logger.error("Erreur création conférence", e);
            throw new BusinessException("Erreur lors de la création de la conférence", e);
        }
    }



    /**
     * Créer un nouveau spectacle 
     */
    public Spectacle creerSpectacle(int organisateurId, String nom, LocalDateTime dateEvenement, 
                                   String lieu, String description, int placesStandard, 
                                   int placesVip, int placesPremium, BigDecimal prixPremium, BigDecimal prixStandard, BigDecimal prixVip) throws BusinessException {
        try {
            // Validation des données
            validerDonneesEvenement(nom, dateEvenement, lieu, placesStandard, placesVip, placesPremium);

          
            // Création du spectacle
            Spectacle spectacle = new Spectacle (organisateurId, nom, dateEvenement, lieu, description, placesStandard, placesVip, placesPremium, prixStandard, prixVip, prixPremium);
            

            Spectacle result = (Spectacle) evenementDAO.creer(spectacle);
            logger.info("✓ Spectacle créé: {}", nom);
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
    }

    /**
     * Modifier un événement
     */
    public void updateEvent(Evenement evenement) throws BusinessException {
        try {
            if (evenement.getStatut() == StatutEvenement.TERMINE) {
                throw new BusinessException("Impossible de modifier un événement terminé");
            }

            evenementDAO.updateEvent(evenement);
            logger.info("✓ Événement modifié: {}", evenement.getNom());

        } catch (DatabaseException e) {
            logger.error("Erreur modification événement", e);
            throw new BusinessException("Erreur modification événement", e);
        }
    }


    // Méthodes privées pour la validation

    /**
     * Validation simple des données d'événement
     */
    private void validerDonneesEvenement(String nom, LocalDateTime dateEvenement, String lieu,
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


