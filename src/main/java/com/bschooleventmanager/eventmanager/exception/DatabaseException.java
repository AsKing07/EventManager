package com.bschooleventmanager.eventmanager.exception;

/**
 * Exception levée lors d'erreurs d'accès à la base de données.
 * Encapsule les erreurs SQL et les problèmes de connectivité.
 *
 * @author @AsKing07 Charbel SONON
 * @version 1.0
 * @since 2024-11-20
 */
public class DatabaseException extends Exception {
    /**
     * Constructeur avec message d'erreur.
     *
     * @param message Description de l'erreur de base de données
     */
    public DatabaseException(String message) {
        super(message);
    }

    /**
     * Constructeur avec message et cause racine.
     *
     * @param message Description de l'erreur de base de données
     * @param cause Exception SQL originale
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}