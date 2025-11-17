package com.bschooleventmanager.eventmanager.model.enums;

public enum NiveauExpertise {
    DEBUTANT("Debutant"),
    INTERMEDIAIRE("Intermediaire"),
    PROFESSIONNEL("Professionnel");


    private final String label;

    NiveauExpertise(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
