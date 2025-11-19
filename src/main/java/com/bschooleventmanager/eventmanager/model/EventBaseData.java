package com.bschooleventmanager.eventmanager.model;

import java.time.LocalDateTime;

/**
 * Classe représentant les données de base d'un événement lors de sa création ou mise à jour.
 * @author loic Vanel
 */
public class EventBaseData {

    public String titre;
    public String description;
    public LocalDateTime dateEvent;
    public String lieu;
    public String typeEvent;

    public Integer nbreStandard;
    public Integer nbreVip;
    public Integer nbrePremium;

    public Integer prixStand;
    public Integer prixVip;
    public Integer prixPremium;

    public EventBaseData() {}

    public EventBaseData(String titre, String description, LocalDateTime dateEvent,
                         String lieu, String typeEvent,
                         Integer nbreStandard, Integer nbreVip, Integer nbrePremium,
                         Integer prixStand, Integer prixVip, Integer prixPremium) {

        this.titre = titre;
        this.description = description;
        this.dateEvent = dateEvent;
        this.lieu = lieu;
        this.typeEvent = typeEvent;

        this.nbreStandard = nbreStandard;
        this.nbreVip = nbreVip;
        this.nbrePremium = nbrePremium;

        this.prixStand = prixStand;
        this.prixVip = prixVip;
        this.prixPremium = prixPremium;
    }

    
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateEvent() {
        return dateEvent;
    }

    public void setDateEvent(LocalDateTime dateEvent) {
        this.dateEvent = dateEvent;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getTypeEvent() {
        return typeEvent;
    }

    public void setTypeEvent(String typeEvent) {
        this.typeEvent = typeEvent;
    }

    public Integer getNbreStandard() {
        return nbreStandard;
    }

    public void setNbreStandard(Integer nbreStandard) {
        this.nbreStandard = nbreStandard;
    }

    public Integer getNbreVip() {
        return nbreVip;
    }

    public void setNbreVip(Integer nbreVip) {
        this.nbreVip = nbreVip;
    }

    public Integer getNbrePremium() {
        return nbrePremium;
    }

    public void setNbrePremium(Integer nbrePremium) {
        this.nbrePremium = nbrePremium;
    }

    public Integer getPrixStand() {
        return prixStand;
    }

    public void setPrixStand(Integer prixStand) {
        this.prixStand = prixStand;
    }

    public Integer getPrixVip() {
        return prixVip;
    }

    public void setPrixVip(Integer prixVip) {
        this.prixVip = prixVip;
    }

    public Integer getPrixPremium() {
        return prixPremium;
    }

    public void setPrixPremium(Integer prixPremium) {
        this.prixPremium = prixPremium;
    }
}

