package com.bschooleventmanager.eventmanager.model;

import com.bschooleventmanager.eventmanager.model.enums.NiveauExpertise;
import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;
import com.bschooleventmanager.eventmanager.model.enums.StatutEvenement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Conference extends Evenement {
    protected String theme;
    protected String intervenants;
    protected String domaine;
    protected NiveauExpertise niveauExpertise;

    public Conference(String titre, LocalDate dateEvenement, String lieu, TypeEvenement typeEvenement, String description, int placesStandardDisponibles, int placesVipDisponibles, int placesPremiumDisponibles, BigDecimal prixStandard, BigDecimal prixVip, BigDecimal prixPremium, LocalDateTime dateCreation, String intervenants, String domaine, NiveauExpertise niveauExpertise) {
        super(titre, dateEvenement, lieu, description, placesStandardDisponibles, placesVipDisponibles, placesPremiumDisponibles, prixStandard, prixVip, prixPremium, dateCreation);
        //this.theme = theme;
        this.typeEvenement = TypeEvenement.CONFERENCE;
        this.intervenants = intervenants;
        this.domaine = domaine;
        this.niveauExpertise = niveauExpertise;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getIntervenants() {
        return intervenants;
    }

    public void setIntervenants(String intervenants) {
        this.intervenants = intervenants;
    }

    public String getDomaine() {
        return domaine;
    }

    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }

    public NiveauExpertise getNiveauExpertise() {
        return niveauExpertise;
    }

    public void setNiveauExpertise(NiveauExpertise niveauExpertise) {
        this.niveauExpertise = niveauExpertise;
    }

    // Constructeur vide
    public Conference() {
        super();
        this.typeEvenement = TypeEvenement.CONFERENCE;
    }

    // Constructeur de base
    public Conference(int organisateurId, String nom, LocalDate dateEvenement, String lieu) {
        super(organisateurId, nom, dateEvenement, lieu, TypeEvenement.CONFERENCE);
    }

    // Constructeur avec description
    public Conference(int organisateurId, String nom, LocalDate dateEvenement, String lieu, String description) {
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
        System.out.println("Nom: " + titre);
        System.out.println("Date: " + dateEvenement);
        System.out.println("Lieu: " + lieu);
        System.out.println("Description: " + description);
        System.out.println("Statut: " + statut.getLabel());
        System.out.println("Capacité totale: " + getCapaciteTotale());
        System.out.println("Recette maximale: " + calculerRecetteMaximale() + "€");
    }

    @Override
    /*public boolean peutEtreAnnule() {
        // Une conférence peut être annulée jusqu'à 2 heures avant le début
        return statut == StatutEvenement.A_VENIR && 
               dateEvenement.isAfter(LocalDateTime.now().plusHours(2));
    }*/
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
                ", nom='" + titre + '\'' +
                ", date=" + dateEvenement +
                ", lieu='" + lieu + '\'' +
                ", statut=" + statut +
                ", capacité=" + getCapaciteTotale() +
                '}';
    }
}
