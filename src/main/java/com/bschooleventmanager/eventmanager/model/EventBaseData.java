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
}

