package com.bschooleventmanager.eventmanager.model;

import com.bschooleventmanager.eventmanager.model.enums.TypeConcert;
import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;
import com.bschooleventmanager.eventmanager.model.enums.StatutEvenement;
import com.bschooleventmanager.eventmanager.model.enums.TypeSpectacle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Spectacle extends Evenement {
    protected TypeSpectacle typeSpectacle;
    protected String troupe_artistes;
    protected Integer ageMin;

    // Constructeur vide
    public Spectacle() {
        super();
        this.typeEvenement = TypeEvenement.SPECTACLE;
    }

    // Constructeur de base
    public Spectacle(int organisateurId, String nom, LocalDateTime dateEvenement, String lieu, String description, int placesStandardDisponibles, int placesVipDisponibles, int placesPremiumDisponibles, BigDecimal prixStandard, BigDecimal prixVip, BigDecimal prixPremium, LocalDateTime dateCreation, String artiste_groupe, TypeSpectacle type, Integer ageMin) {
        super(organisateurId,nom, dateEvenement, lieu, TypeEvenement.SPECTACLE, description, placesStandardDisponibles, placesVipDisponibles, placesPremiumDisponibles, prixStandard, prixVip, prixPremium);
        this.typeEvenement = TypeEvenement.CONCERT;
        this.troupe_artistes = artiste_groupe;
        this.typeSpectacle = type;
        this.ageMin = ageMin;
    }

     //Constructeur de création de spectacle (avec prix et places)


    public Spectacle(int organisateurId, 
                        String nom, 
                        LocalDateTime dateEvenement,
                        String lieu, 
                       
                        String description,
                        int placesStandard,
                        int placesVip,
                        int placesPremium,
                        BigDecimal prixStandard,
                        BigDecimal prixVip,
                        BigDecimal prixPremium, TypeSpectacle typeSpectacle, String troupe_artistes, Integer ageMin) {
                             super( organisateurId, 
               nom, 
               dateEvenement, 
               lieu, 
               TypeEvenement.SPECTACLE, 
               description, 
               placesStandard, 
               placesVip, 
               placesPremium, 
               prixStandard, 
               prixVip, 
               prixPremium);
        this.typeSpectacle = typeSpectacle;
        this.troupe_artistes = troupe_artistes;
        this.ageMin = ageMin;
    }
   

    public TypeSpectacle getTypeSpectacle() {
        return typeSpectacle;
    }

    public void setTypeSpectacle(TypeSpectacle typeSpectacle) {
        this.typeSpectacle = typeSpectacle;
    }

    public String getTroupe_artistes() {
        return troupe_artistes;
    }

    public void setTroupe_artistes(String troupe_artistes) {
        this.troupe_artistes = troupe_artistes;
    }

    public Integer getAgeMin() {
        return ageMin;
    }

    public void setAgeMin(Integer ageMin) {
        this.ageMin = ageMin;
    }

    

    // Implémentation des méthodes abstraites
    @Override
    public String getCategorie() {
        return typeEvenement.getLabel();
    }

    
    
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
        LocalDateTime jour = LocalDateTime.now();
        LocalDateTime jour24 = jour.plusDays(1);
        // Un spectacle peut être annulé
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
