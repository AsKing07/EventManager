package com.bschooleventmanager.eventmanager.model.enums;

public enum TypeConcert {
    LIVE("Live"),
    ACOUSTIQUE("Acoustique"),
    JAZZ("Jazz"),
    ROCK("Rock"),
    CLASSIQUE("Classique");

    private final String label;

    TypeConcert(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static TypeConcert fromLabel(String label) {
        for (TypeConcert type : TypeConcert.values()) {
            if (type.getLabel().equalsIgnoreCase(label)) {
                return type;
            }
        }
        return null; // ou lever une exception si le label n'est pas valide
    }
}