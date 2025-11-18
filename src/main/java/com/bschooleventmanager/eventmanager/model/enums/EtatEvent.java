package com.bschooleventmanager.eventmanager.model.enums;

public enum EtatEvent {
    SUPPRIME(0, "Supprimé"),
    ACTIF(1, "Actif");

    private final int code;
    private final String label;

    EtatEvent(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static EtatEvent fromCode(int code) {
        for (EtatEvent etat : values()) {
            if (etat.code == code) {
                return etat;
            }
        }
        throw new IllegalArgumentException("Code d'état de compte inconnu : " + code);
    }
}
