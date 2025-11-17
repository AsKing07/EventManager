package com.bschooleventmanager.eventmanager.model.enums;

public enum TypeConcert {
    LIVE("Live"),
    ACOUSTIQUE("Acoustique");

    private final String label;

    TypeConcert(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}