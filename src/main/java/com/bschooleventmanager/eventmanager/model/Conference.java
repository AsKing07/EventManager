package com.bschooleventmanager.eventmanager.model;

import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;
import com.bschooleventmanager.eventmanager.model.enums.StatutEvenement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Conference extends Evenement {

    // Constructeur vide
    public Conference() {
        super();
        this.typeEvenement = TypeEvenement.CONFERENCE;
    }

    // Constructeur de base
    public Conference(int organisateurId, String nom, LocalDateTime dateEvenement, String lieu) {
        super(organisateurId, nom, dateEvenement, lieu, TypeEvenement.CONFERENCE);
    }

    // Constructeur avec description
    public Conference(int organisateurId, String nom, LocalDateTime dateEvenement, String lieu, String description) {
        super(organisateurId, nom, dateEvenement, lieu, TypeEvenement.CONFERENCE, description);
    }

    // Implémentation des méthodes abstraites
    @Override
    public String getCategorie() {
        return typeEvenement.getLabel();
    }

    @Override
    public void afficherInformations() {
        System.out.println("=== Conférence ===");
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
        // Une conférence peut être annulée jusqu'à 2 heures avant le début
        return statut == StatutEvenement.A_VENIR && 
               dateEvenement.isAfter(LocalDateTime.now().plusHours(2));
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

    // Méthodes spécifiques à la conférence
    public boolean necessiteMaterielPresentation() {
        return true; // Une conférence nécessite du matériel de présentation
    }

    public boolean estEducative() {
        return true; // Une conférence est généralement éducative
    }

    public int getDureeEstimeeMinutes() {
        return 120; // Durée standard d'une conférence : 2 heures
    }

    @Override
    public String toString() {
        return "Conference{" +
                "id=" + idEvenement +
                ", nom='" + nom + '\'' +
                ", date=" + dateEvenement +
                ", lieu='" + lieu + '\'' +
                ", statut=" + statut +
                ", capacité=" + getCapaciteTotale() +
                '}';
    }
}
