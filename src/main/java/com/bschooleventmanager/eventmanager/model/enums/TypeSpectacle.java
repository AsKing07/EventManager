package com.bschooleventmanager.eventmanager.model.enums;

public enum TypeSpectacle {
    THEATRE("Théatre"),
    CIRQUE("Cirque"),
    HUMOUR("Humour");

    private final String label;

    TypeSpectacle(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

