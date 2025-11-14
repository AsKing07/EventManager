package com.bschooleventmanager.eventmanager.exception;

//pour les réservations impossibles
public class PlacesInsuffisantesException extends RuntimeException {
    public PlacesInsuffisantesException(String message) {
        super(message);
    }
}
