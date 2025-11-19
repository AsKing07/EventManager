package com.bschooleventmanager.eventmanager.service;

import com.bschooleventmanager.eventmanager.dao.EvenementDAO;
import com.bschooleventmanager.eventmanager.exception.BusinessException;
import com.bschooleventmanager.eventmanager.model.Evenement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class EvenementService {

    private static final Logger logger = LoggerFactory.getLogger(EvenementService.class);

    private final EvenementDAO evenementDAO = new EvenementDAO();

    public List<Evenement> getAllEvents() {
        logger.info("Service: Fetching all events...");
        List<Evenement> list = evenementDAO.getAllEvents();
        logger.info("Service: {} events retrieved", list.size());
        return list;
    }

    public List<Evenement> listerEvenementsParOrganisateur(int organisateurId) throws BusinessException {
        if (organisateurId <= 0) {
            throw new BusinessException("ID organisateur invalide.");
        }

        List<Evenement> evenements = EvenementDAO.getEventsByOrganisateur(organisateurId);

        if (evenements.isEmpty()) {
            logger.warn("No events found for organiser {}", organisateurId);
        }

        return evenements;
    }

    public void supprimerEvenement(int eventId) throws BusinessException {
        boolean ok = evenementDAO.deleteEvent(eventId);

        if (!ok) {
            throw new BusinessException("La suppression a échoué.");
        }
    }

    public void updateEvent(Evenement evenement) throws BusinessException, SQLException {
        if (evenement == null || evenement.getIdEvenement() <= 0) {
            throw new BusinessException("Événement invalide.");
        }

        boolean success = evenementDAO.updateEvent(evenement); // make sure your DAO has this method
        if (!success) {
            throw new BusinessException("La mise à jour de l'événement a échoué.");
        }
    }


}
