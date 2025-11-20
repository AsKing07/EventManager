package com.bschooleventmanager.eventmanager.model.enums;
/*  Enumération représentant les différentes catégories de tickets disponibles pour un événement.
  Chaque catégorie a un label descriptif et un ordre pour le tri ou l'affichage.
*/
public enum CategorieTicket {
    STANDARD("Standard", 0),
    VIP("VIP", 1),
    PREMIUM("Premium", 2);

    private final String label;
    private final int ordre;

    CategorieTicket(String label, int ordre) {
        this.label = label;
        this.ordre = ordre;
    }

    public String getLabel() {
        return label;
    }

    public int getOrdre() {
        return ordre;
    }
}