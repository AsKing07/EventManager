package com.bschooleventmanager.eventmanager.exception;

//pour les annulations de réservation hors délai
public class AnnulationTardiveException extends RuntimeException {
    public AnnulationTardiveException(String message) {
        super(message);
    }
}
