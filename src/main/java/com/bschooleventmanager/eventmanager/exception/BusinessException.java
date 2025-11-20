package com.bschooleventmanager.eventmanager.exception;

/**
 * Exception générique pour les erreurs métier de l'application EventManager.
 * Centralise la gestion des erreurs liées aux règles métier et à la validation.
 * @author Équipe EventManager
 * @version 1.0
 * @since 2024-11-20
 */
public class BusinessException extends Exception {
    /**
     * Constructeur avec message d'erreur.
     *
     * @param message Description de l'erreur métier
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Constructeur avec message et cause racine.
     *
     * @param message Description de l'erreur métier
     * @param cause Exception originale ayant causé cette erreur
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}