package com.bschooleventmanager.eventmanager.model;

import com.bschooleventmanager.eventmanager.model.enums.NiveauExpertise;
import com.bschooleventmanager.eventmanager.model.enums.StatutEvenement;
import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Classe Conference qui hérite de Evenement
public class Conference extends Evenement {
    protected String theme;
    protected String intervenants;
    protected String domaine;
    protected NiveauExpertise niveauExpertise;

         // Constructeur vide
    public Conference() {
        super();
        this.typeEvenement = TypeEvenement.CONFERENCE;
    }

    public Conference(int organisateurId, String nom, LocalDateTime dateEvenement, String lieu, TypeEvenement typeEvenement, String description, int placesStandardDisponibles, int placesVipDisponibles, int placesPremiumDisponibles, BigDecimal prixStandard, BigDecimal prixVip, BigDecimal prixPremium, String intervenants, String domaine, NiveauExpertise niveauExpertise) {
       super(organisateurId, nom, dateEvenement, lieu, TypeEvenement.CONFERENCE, description, placesStandardDisponibles, placesVipDisponibles, placesPremiumDisponibles, prixStandard, prixVip, prixPremium);
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

   
    // Implémentation des méthodes abstraites
    @Override
    public String getCategorie() {
        return typeEvenement.getLabel();
    }

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
        LocalDateTime jour = LocalDateTime.now();
        LocalDateTime jour24 = jour.plusDays(1);
        // Une conférence peut être annulé que si l'annulation est faite au moins 24 heures avant la date de l'événement
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
                ", nom='" + nom + '\'' +
                ", date=" + dateEvenement +
                ", lieu='" + lieu + '\'' +
                ", statut=" + statut +
                ", capacité=" + getCapaciteTotale() +
                '}';
    }
}
