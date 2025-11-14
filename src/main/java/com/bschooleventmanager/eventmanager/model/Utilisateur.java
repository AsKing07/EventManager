package com.bschooleventmanager.eventmanager.model;

import com.bschooleventmanager.eventmanager.model.enums.TypeUtilisateur;

import java.time.LocalDateTime;

public class Utilisateur {
    private int idUtilisateur;
    private String nom;
    private String email;
    private String motDePasse;
    private TypeUtilisateur typeUtilisateur;
    private LocalDateTime dateCreation;

    // Constructeur vide
    public Utilisateur() {
    }

    // Constructeur complet
    public Utilisateur(int idUtilisateur, String nom, String email,
                       TypeUtilisateur typeUtilisateur, LocalDateTime dateCreation) {
        this.idUtilisateur = idUtilisateur;
        this.nom = nom;
        this.email = email;
        this.typeUtilisateur = typeUtilisateur;
        this.dateCreation = dateCreation;
    }

    // Constructeur pour création
    public Utilisateur(String nom, String email, TypeUtilisateur typeUtilisateur) {
        this.nom = nom;
        this.email = email;
        this.typeUtilisateur = typeUtilisateur;
    }

    // Getters et Setters
    public int getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(int id) { this.idUtilisateur = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String mdp) { this.motDePasse = mdp; }

    public TypeUtilisateur getTypeUtilisateur() { return typeUtilisateur; }
    public void setTypeUtilisateur(TypeUtilisateur type) { this.typeUtilisateur = type; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime date) { this.dateCreation = date; }

    // Méthode toString
    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + idUtilisateur +
                ", nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", type=" + typeUtilisateur +
                ", dateCreation=" + dateCreation +
                '}';
    }
}

