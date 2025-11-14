package com.bschooleventmanager.eventmanager.model;

import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;
import com.bschooleventmanager.eventmanager.model.enums.StatutEvenement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class Evenement {
    protected int idEvenement;
    protected int organisateurId;
    protected String nom;
    protected LocalDateTime dateEvenement;
    protected String lieu;
    protected TypeEvenement typeEvenement;
    protected String description;
    protected int placesStandardDisponibles;
    protected int placesVipDisponibles;
    protected int placesPremiumDisponibles;
    protected BigDecimal prixStandard;
    protected BigDecimal prixVip;
    protected BigDecimal prixPremium;
    protected LocalDateTime dateCreation;
    protected StatutEvenement statut;

    // Constructeur vide
    protected Evenement() {
        this.statut = StatutEvenement.A_VENIR;
    }

    // Constructeur de base avec les informations essentielles
    protected Evenement(int organisateurId, String nom, LocalDateTime dateEvenement,
                        String lieu, TypeEvenement typeEvenement) {
        this.organisateurId = organisateurId;
        this.nom = nom;
        this.dateEvenement = dateEvenement;
        this.lieu = lieu;
        this.typeEvenement = typeEvenement;
        this.statut = StatutEvenement.A_VENIR;
    }

    // Constructeur avec places et prix
    protected Evenement(int organisateurId, String nom, LocalDateTime dateEvenement,
                        String lieu, TypeEvenement typeEvenement, String description) {
        this(organisateurId, nom, dateEvenement, lieu, typeEvenement);
        this.description = description;
    }

    // Getters et Setters
    public int getIdEvenement() { return idEvenement; }
    public void setIdEvenement(int idEvenement) { this.idEvenement = idEvenement; }

    public int getOrganisateurId() { return organisateurId; }
    public void setOrganisateurId(int organisateurId) { this.organisateurId = organisateurId; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public LocalDateTime getDateEvenement() { return dateEvenement; }
    public void setDateEvenement(LocalDateTime dateEvenement) { this.dateEvenement = dateEvenement; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public TypeEvenement getTypeEvenement() { return typeEvenement; }
    public void setTypeEvenement(TypeEvenement typeEvenement) { this.typeEvenement = typeEvenement; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPlacesStandardDisponibles() { return placesStandardDisponibles; }
    public void setPlacesStandardDisponibles(int placesStandardDisponibles) { 
        this.placesStandardDisponibles = placesStandardDisponibles; 
    }

    public int getPlacesVipDisponibles() { return placesVipDisponibles; }
    public void setPlacesVipDisponibles(int placesVipDisponibles) { 
        this.placesVipDisponibles = placesVipDisponibles; 
    }

    public int getPlacesPremiumDisponibles() { return placesPremiumDisponibles; }
    public void setPlacesPremiumDisponibles(int placesPremiumDisponibles) { 
        this.placesPremiumDisponibles = placesPremiumDisponibles; 
    }

    public BigDecimal getPrixStandard() { return prixStandard; }
    public void setPrixStandard(BigDecimal prixStandard) { this.prixStandard = prixStandard; }

    public BigDecimal getPrixVip() { return prixVip; }
    public void setPrixVip(BigDecimal prixVip) { this.prixVip = prixVip; }

    public BigDecimal getPrixPremium() { return prixPremium; }
    public void setPrixPremium(BigDecimal prixPremium) { this.prixPremium = prixPremium; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public StatutEvenement getStatut() { return statut; }
    public void setStatut(StatutEvenement statut) { this.statut = statut; }

    // Méthodes abstraites à implémenter par les classes filles
    public abstract String getCategorie();
    public abstract void afficherInformations();
    public abstract boolean peutEtreAnnule();
    public abstract int getCapaciteTotale();
    public abstract BigDecimal calculerRecetteMaximale();

    @Override
    public String toString() {
        return "Evenement{" +
                "id=" + idEvenement +
                ", nom='" + nom + '\'' +
                ", type=" + typeEvenement +
                ", date=" + dateEvenement +
                ", lieu='" + lieu + '\'' +
                ", statut=" + statut +
                '}';
    }
}
