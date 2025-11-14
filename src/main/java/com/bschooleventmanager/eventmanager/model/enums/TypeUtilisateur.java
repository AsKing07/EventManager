package com.bschooleventmanager.eventmanager.model.enums;

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

