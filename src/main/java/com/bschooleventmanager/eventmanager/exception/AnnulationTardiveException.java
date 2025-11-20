package com.bschooleventmanager.eventmanager.exception;

//pour les annulations de réservation hors délai
/**
 * Exception levée lors d'une tentative d'annulation tardive d'une réservation.
 * Cette exception indique que l'annulation n'est plus autorisée selon les règles métier.
 * @version 1.0
 * @since 2024-11-20
 */
public class AnnulationTardiveException extends RuntimeException {
    /**
     * Constructeur avec message d'erreur.
     *
     * @param message Détails de l'erreur d'annulation tardive
     */
    public AnnulationTardiveException(String message) {
        super(message);
    }
}
