package com.bschooleventmanager.eventmanager.model;

import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;
import com.bschooleventmanager.eventmanager.model.enums.StatutEvenement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Concert extends Evenement {

    // Constructeur vide
    public Concert() {
        super();
        this.typeEvenement = TypeEvenement.CONCERT;
    }
    // Constructeur de création complet
    public Concert(int organisateurId, 
                     String nom, 
                     LocalDateTime dateEvenement, 
                     String lieu,
                     String description,
                     int placesStandard,
                     int placesVip,
                     int placesPremium,
                     BigDecimal prixStandard,
                     BigDecimal prixVip,
                     BigDecimal prixPremium) {
        super(organisateurId, nom, dateEvenement, lieu, TypeEvenement.CONCERT, description,
              placesStandard, placesVip, placesPremium, prixStandard, prixVip, prixPremium);
    }

    // Implémentation des méthodes abstraites
    @Override
    public String getCategorie() {
        return typeEvenement.getLabel();
    }

 
    @Override
    public boolean peutEtreAnnule() {
        // Un concert peut être annulé si sa date est dans plus de 7 jours
        return dateEvenement.isAfter(LocalDateTime.now().plusDays(7));
    }


    public void afficherInformations() {
        System.out.println("=== Concert ===");
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

    // Méthodes spécifiques au concert
    public boolean necessiteSonorisation() {
        return true; // Un concert nécessite toujours une sonorisation
    }

    public boolean aUneDureeFixe() {
        return false; // La durée d'un concert peut varier
    }

    @Override
    public String toString() {
        return "Concert{" +
                "id=" + idEvenement +
                ", nom='" + nom + '\'' +
                ", date=" + dateEvenement +
                ", lieu='" + lieu + '\'' +
                ", statut=" + statut +
                ", capacité=" + getCapaciteTotale() +
                '}';
    }
}
