// java
package com.bschooleventmanager.eventmanager.model.enums;

/**
 * Représente l'état logique d'un événement.
 * Utilisé pour obtenir un code entier stocké en base (ex: 1 = ACTIF, 0 = SUPPRIME).
 */
public enum EtatEvent {
    ACTIF(1),
    SUPPRIME(0);

    private final int code;

    EtatEvent(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static EtatEvent fromCode(int code) {
        for (EtatEvent e : values()) {
            if (e.code == code) return e;
        }
        throw new IllegalArgumentException("Code EtatEvent inconnu: " + code);
    }
}
