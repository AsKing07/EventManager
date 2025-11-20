package com.bschooleventmanager.eventmanager.model.enums;

// Enumération pour les types d'utilisateurs
public enum TypeUtilisateur {
    CLIENT("Client"),
    ORGANISATEUR("Organisateur");

    private final String label;

    TypeUtilisateur(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

