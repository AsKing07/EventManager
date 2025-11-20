package com.bschooleventmanager.eventmanager.model;

import com.bschooleventmanager.eventmanager.model.enums.TypeUtilisateur;
import java.time.LocalDateTime;

/**
 * Classe représentant un Organisateur, qui hérite de la classe Utilisateur.
 * Un organisateur peut créer et gérer des événements.
 */
public class Organisateur extends Utilisateur {
    
    // Constructeur vide
    public Organisateur() {
        super();
        this.typeUtilisateur = TypeUtilisateur.ORGANISATEUR;
    }
    
    // Constructeur complet
    public Organisateur(int idUtilisateur, String nom, String email, LocalDateTime dateCreation) {
        super(idUtilisateur, nom, email, TypeUtilisateur.ORGANISATEUR, dateCreation);
    }
    
    // Constructeur pour création
    public Organisateur(String nom, String email) {
        super(nom, email, TypeUtilisateur.ORGANISATEUR);
    }
    
    // Implémentation des méthodes abstraites
    @Override
    public String getRole() {
        return typeUtilisateur.getLabel();
    }
    
    @Override
    public void afficherInformations() {
        System.out.println("=== Informations Organisateur ===");
        System.out.println("ID: " + idUtilisateur);
        System.out.println("Nom: " + nom);
        System.out.println("Email: " + email);
        System.out.println("Role: " + getRole());
        System.out.println("Date de création: " + dateCreation);
    }
    
    @Override
    public boolean peutReserverEvenement() {
        return false; // Un organisateur ne peut pas réserver d'événements (il les crée)
    }
    
    @Override
    public String toString() {
        return "Organisateur{" +
                "id=" + idUtilisateur +
                ", nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
