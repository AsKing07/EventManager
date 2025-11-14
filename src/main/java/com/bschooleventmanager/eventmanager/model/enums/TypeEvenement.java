package com.bschooleventmanager.eventmanager.model.enums;

public enum TypeEvenement {
    CONCERT("Concert"),
    SPECTACLE("Spectacle"), 
    CONFERENCE("Conférence");

    private final String label;

    TypeEvenement(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
