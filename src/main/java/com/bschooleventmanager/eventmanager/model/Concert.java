package com.bschooleventmanager.eventmanager.model;

import com.bschooleventmanager.eventmanager.model.enums.TypeConcert;
import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;
import com.bschooleventmanager.eventmanager.model.enums.StatutEvenement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Concert extends Evenement {
    protected String artiste_groupe;
    protected TypeConcert type;
    protected Integer ageMin;


    // Constructeur vide
    public Concert() {
        super();
        this.typeEvenement = TypeEvenement.CONCERT;
    }

    public Concert(String titre, LocalDate dateEvenement, String lieu, String description, int placesStandardDisponibles, int placesVipDisponibles, int placesPremiumDisponibles, BigDecimal prixStandard, BigDecimal prixVip, BigDecimal prixPremium, LocalDateTime dateCreation, String artiste_groupe, TypeConcert type, Integer ageMin) {
        super(titre, dateEvenement, lieu, description, placesStandardDisponibles, placesVipDisponibles, placesPremiumDisponibles, prixStandard, prixVip, prixPremium, dateCreation);
        this.typeEvenement = TypeEvenement.CONCERT;
        this.artiste_groupe = artiste_groupe;
        this.type = type;
        this.ageMin = ageMin;
    }

    public String getArtiste_groupe() {
        return artiste_groupe;
    }

    public void setArtiste_groupe(String artiste_groupe) {
        this.artiste_groupe = artiste_groupe;
    }

    public TypeConcert getType() {
        return type;
    }

    public void setType(TypeConcert type) {
        this.type = type;
    }

    public Integer getAgeMin() {
        return ageMin;
    }

    public void setAgeMin(Integer ageMin) {
        this.ageMin = ageMin;
    }

    // Constructeur de base
    public Concert(int organisateurId, String nom, LocalDate dateEvenement, String lieu) {
        super(organisateurId, nom, dateEvenement, lieu, TypeEvenement.CONCERT);
    }

    // Constructeur avec description
    public Concert(int organisateurId, String nom, LocalDate dateEvenement, String lieu, String description) {
        super(organisateurId, nom, dateEvenement, lieu, TypeEvenement.CONCERT, description);
    }


    // Implémentation des méthodes abstraites
    @Override
    public String getCategorie() {
        return typeEvenement.getLabel();
    }

    @Override
    public void afficherInformations() {
        System.out.println("=== Concert ===");
        System.out.println("ID: " + idEvenement);
        System.out.println("titre: " + titre);
        System.out.println("Date: " + dateEvenement);
        System.out.println("Lieu: " + lieu);
        System.out.println("Description: " + description);
        System.out.println("Statut: " + statut.getLabel());
        System.out.println("Capacité totale: " + getCapaciteTotale());
        System.out.println("Recette maximale: " + calculerRecetteMaximale() + "€");
    }

    @Override
    public boolean peutEtreAnnule() {
        LocalDate jour = LocalDate.now();
        LocalDate jour24 = jour.plusDays(1);
        // Un concert peut être annulé s'il n'a pas encore commencé
        return statut == StatutEvenement.A_VENIR &&
                //dateEvenement.isAfter(LocalDateTime.now().plusHours(24));
                dateEvenement.isAfter(jour24);
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
                ", titre='" + titre + '\'' +
                ", date=" + dateEvenement +
                ", lieu='" + lieu + '\'' +
                ", statut=" + statut +
                ", capacité=" + getCapaciteTotale() +
                '}';
    }
}
