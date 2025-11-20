package com.bschooleventmanager.eventmanager.model;

import com.bschooleventmanager.eventmanager.model.enums.TypeUtilisateur;
import java.time.LocalDateTime;

/**
 * Représente un client dans l'application EventManager.
 * Hérite de Utilisateur et implémente les fonctionnalités spécifiques aux clients,
 * notamment la réservation d'événements et la gestion des réservations.
 * @version 1.0
 * @since 2024-11-20
 * @see Utilisateur
 */
public class Client extends Utilisateur {
    
    /**
     * Constructeur par défaut.
     * Initialise un client vide pour les opérations de mapping.
     */
    public Client() {
        super();
        this.typeUtilisateur = TypeUtilisateur.CLIENT;
    }
    
    /**
     * Constructeur complet pour créer un client avec toutes ses informations.
     *
     * @param idUtilisateur Identifiant unique du client
     * @param nom Nom complet du client  
     * @param email Adresse email unique
     * @param dateCreation Date de création du compte
     */
    public Client(int idUtilisateur, String nom, String email, LocalDateTime dateCreation) {
        super(idUtilisateur, nom, email, TypeUtilisateur.CLIENT, dateCreation);
    }
    
    /**
     * Constructeur pour l'inscription d'un nouveau client.
     *
     * @param nom Nom complet du client
     * @param email Adresse email unique
     */
    public Client(String nom, String email) {
        super(nom, email, TypeUtilisateur.CLIENT);
    }
    
    // Implémentation des méthodes abstraites
    @Override
    public String getRole() {
        return typeUtilisateur.getLabel();
    }
    
    @Override
    public void afficherInformations() {
        System.out.println("=== Informations Client ===");
        System.out.println("ID: " + idUtilisateur);
        System.out.println("Nom: " + nom);
        System.out.println("Email: " + email);
        System.out.println("Role: " + getRole());
        System.out.println("Date de création: " + dateCreation);
    }
    
    @Override
    public boolean peutReserverEvenement() {
        return true; // Un client peut réserver des événements
    }
    
    @Override
    public String toString() {
        return "Client{" +
                "id=" + idUtilisateur +
                ", nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
