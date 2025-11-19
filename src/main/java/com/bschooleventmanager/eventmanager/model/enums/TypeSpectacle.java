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
    public static TypeSpectacle fromLabel(String label) {
        for (TypeSpectacle type : TypeSpectacle.values()) {
            if (type.getLabel().equalsIgnoreCase(label)) {
                return type;
            }
        }
        return null; // ou lever une exception si le label n'est pas valide
    }
}

