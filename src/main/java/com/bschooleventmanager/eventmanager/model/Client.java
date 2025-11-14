package com.bschooleventmanager.eventmanager.model;

import com.bschooleventmanager.eventmanager.model.enums.TypeUtilisateur;
import java.time.LocalDateTime;

public class Client extends Utilisateur {
    
    // Constructeur vide
    public Client() {
        super();
        this.typeUtilisateur = TypeUtilisateur.CLIENT;
    }
    
    // Constructeur complet
    public Client(int idUtilisateur, String nom, String email, LocalDateTime dateCreation) {
        super(idUtilisateur, nom, email, TypeUtilisateur.CLIENT, dateCreation);
    }
    
    // Constructeur pour création
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
