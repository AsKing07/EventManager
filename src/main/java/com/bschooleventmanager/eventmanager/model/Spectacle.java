package com.bschooleventmanager.eventmanager.model;

import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;
import com.bschooleventmanager.eventmanager.model.enums.StatutEvenement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Spectacle extends Evenement {

    // Constructeur vide
    public Spectacle() {
        super();
        this.typeEvenement = TypeEvenement.SPECTACLE;
    }

    // Constructeur de base
    public Spectacle(int organisateurId, String nom, LocalDateTime dateEvenement, String lieu) {
        super(organisateurId, nom, dateEvenement, lieu, TypeEvenement.SPECTACLE);
    }

    // Constructeur avec description
    public Spectacle(int organisateurId, String nom, LocalDateTime dateEvenement, String lieu, String description) {
        super(organisateurId, nom, dateEvenement, lieu, TypeEvenement.SPECTACLE, description);
    }

    // Implémentation des méthodes abstraites
    @Override
    public String getCategorie() {
        return typeEvenement.getLabel();
    }

    @Override
    public void afficherInformations() {
        System.out.println("=== Spectacle ===");
        System.out.println("ID: " + idEvenement);
        System.out.println("Nom: " + nom);
        System.out.println("Date: " + dateEvenement);
        System.out.println("Lieu: " + lieu);
        System.out.println("Description: " + description);
        System.out.println("Statut: " + statut.getLabel());
        System.out.println("Capacité totale: " + getCapaciteTotale());
        System.out.println("Recette maximale: " + calculerRecetteMaximale() + "€");
    }

    @Override
    public boolean peutEtreAnnule() {
        // Un spectacle peut être annulé jusqu'à 4 heures avant le début
        return statut == StatutEvenement.A_VENIR && 
               dateEvenement.isAfter(LocalDateTime.now().plusHours(4));
    }

    @Override
    public int getCapaciteTotale() {
        return placesStandardDisponibles + placesVipDisponibles + placesPremiumDisponibles;
    }

    @Override
    public BigDecimal calculerRecetteMaximale() {
        BigDecimal recetteStandard = prixStandard != null ? 
            prixStandard.multiply(BigDecimal.valueOf(placesStandardDisponibles)) : BigDecimal.ZERO;
        BigDecimal recetteVip = prixVip != null ? 
            prixVip.multiply(BigDecimal.valueOf(placesVipDisponibles)) : BigDecimal.ZERO;
        BigDecimal recettePremium = prixPremium != null ? 
            prixPremium.multiply(BigDecimal.valueOf(placesPremiumDisponibles)) : BigDecimal.ZERO;
        
        return recetteStandard.add(recetteVip).add(recettePremium);
    }

    // Méthodes spécifiques au spectacle
    public boolean necessiteEclairage() {
        return true; // Un spectacle nécessite un éclairage spécialisé
    }

    public boolean aCostumes() {
        return true; // Un spectacle a généralement des costumes
    }

    public boolean aEntracte() {
        return getCapaciteTotale() > 200; // Entracte pour les grands spectacles
    }

    public int getDureeEstimeeMinutes() {
        return 150; // Durée standard d'un spectacle : 2h30
    }

    @Override
    public String toString() {
        return "Spectacle{" +
                "id=" + idEvenement +
                ", nom='" + nom + '\'' +
                ", date=" + dateEvenement +
                ", lieu='" + lieu + '\'' +
                ", statut=" + statut +
                ", capacité=" + getCapaciteTotale() +
                '}';
    }
}
