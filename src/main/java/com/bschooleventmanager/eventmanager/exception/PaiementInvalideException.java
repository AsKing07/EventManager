package com.bschooleventmanager.eventmanager.exception;


//pour les erreurs de paiement
public class PaiementInvalideException extends RuntimeException {
    public PaiementInvalideException(String message) {
        super(message);
    }
}
