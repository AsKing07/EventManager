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
    
    // Nouvelles colonnes pour le suivi des ventes
    protected int placeStandardVendues;
    protected int placePremiumVendues;
    protected int placeVipVendues;
    protected boolean etatEvent;

    // Constructeur vide
    protected Evenement() {
        this.statut = StatutEvenement.A_VENIR;
        this.placeStandardVendues = 0;
        this.placePremiumVendues = 0;
        this.placeVipVendues = 0;
        this.etatEvent = true; // Par défaut, l'événement est actif
    }

    // Constructeur complet (pour la base de données)
    public Evenement(int idEvenement,
                     int organisateurId,
                     String nom,
                     LocalDateTime dateEvenement,
                     String lieu,
                     TypeEvenement typeEvenement,
                     String description,
                     int placesStandard,
                     int placesVip,
                     int placesPremium,
                     BigDecimal prixStandard,
                     BigDecimal prixVip,
                     BigDecimal prixPremium,
                     LocalDateTime dateCreation,
                     StatutEvenement statut) {

        this.idEvenement = idEvenement;
        this.organisateurId = organisateurId;
        this.nom = nom;
        this.dateEvenement = dateEvenement;
        this.lieu = lieu;
        this.typeEvenement = typeEvenement;
        this.description = description;

        this.placesStandardDisponibles = placesStandard;
        this.placesVipDisponibles = placesVip;
        this.placesPremiumDisponibles = placesPremium;

        this.prixStandard = prixStandard;
        this.prixVip = prixVip;
        this.prixPremium = prixPremium;

        this.dateCreation = dateCreation;
        this.statut = statut;
    }

    // Constructeur de création (avec places et prix)
    protected Evenement(int organisateurId, 
                        String nom, 
                        LocalDateTime dateEvenement,
                        String lieu, 
                        TypeEvenement typeEvenement, 
                        String description,
                        int placesStandard,
                        int placesVip,
                        int placesPremium,
                        BigDecimal prixStandard,
                        BigDecimal prixVip,
                        BigDecimal prixPremium) {
        this.organisateurId = organisateurId;
        this.nom = nom;
        this.dateEvenement = dateEvenement;
        this.lieu = lieu;
        this.typeEvenement = typeEvenement;
        this.description = description;
        this.placesStandardDisponibles = placesStandard;
        this.placesVipDisponibles = placesVip;
        this.placesPremiumDisponibles = placesPremium;
        this.prixStandard = prixStandard;
        this.prixVip = prixVip;
        this.prixPremium = prixPremium;
        this.statut = StatutEvenement.A_VENIR;
        this.dateCreation = LocalDateTime.now();
        // Initialisation des nouvelles colonnes
        this.placeStandardVendues = 0;
        this.placePremiumVendues = 0;
        this.placeVipVendues = 0;
        this.etatEvent = true; // Par défaut, l'événement est actif
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

    // Getters et Setters pour les nouvelles colonnes
    public int getPlaceStandardVendues() { return placeStandardVendues; }
    public void setPlaceStandardVendues(int placeStandardVendues) { this.placeStandardVendues = placeStandardVendues; }

    public int getPlacePremiumVendues() { return placePremiumVendues; }
    public void setPlacePremiumVendues(int placePremiumVendues) { this.placePremiumVendues = placePremiumVendues; }

    public int getPlaceVipVendues() { return placeVipVendues; }
    public void setPlaceVipVendues(int placeVipVendues) { this.placeVipVendues = placeVipVendues; }

    public boolean isEtatEvent() { return etatEvent; }
    public void setEtatEvent(boolean etatEvent) { this.etatEvent = etatEvent; }

    // Méthodes utilitaires pour les ventes
    public int getPlacesStandardRestantes() {
        return Math.max(0, placesStandardDisponibles - placeStandardVendues);
    }

    public int getPlacesVipRestantes() {
        return Math.max(0, placesVipDisponibles - placeVipVendues);
    }

    public int getPlacesPremiumRestantes() {
        return Math.max(0, placesPremiumDisponibles - placePremiumVendues);
    }

    public int getTotalPlacesVendues() {
        return placeStandardVendues + placeVipVendues + placePremiumVendues;
    }

    public double getTauxRemplissage() {
        int totalPlaces = getCapaciteTotale();
        if (totalPlaces == 0) return 0.0;
        return (double) getTotalPlacesVendues() / totalPlaces * 100.0;
    }

    // Méthodes abstraites à implémenter par les classes filles
    public abstract String getCategorie(); 
    public abstract int getCapaciteTotale();
    public abstract BigDecimal calculerRecetteMaximale();
    public abstract boolean peutEtreAnnule();

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
