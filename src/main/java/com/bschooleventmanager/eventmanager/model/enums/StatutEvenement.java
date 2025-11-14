package com.bschooleventmanager.eventmanager.model.enums;

public enum StatutEvenement {
    A_VENIR("À venir"),
    EN_COURS("En cours"),
    TERMINE("Terminé"),
    ANNULE("Annulé");

    private final String label;

    StatutEvenement(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}