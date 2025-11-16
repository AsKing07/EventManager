package com.bschooleventmanager.eventmanager.service;

import com.bschooleventmanager.eventmanager.dao.EvenementDAO;
import com.bschooleventmanager.eventmanager.model.*;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.logging.Logger;

public class EvenementService {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(EvenementService.class);

    private EvenementDAO evenementDAO = new EvenementDAO();

    public List<Evenement> getAllEvents() {
        logger.info("Service: Fetching all events...");
        List<Evenement> list = evenementDAO.getAllEvents();
        logger.info("Service: {} events retrieved : " + list.size());
        return list;
    }

}
