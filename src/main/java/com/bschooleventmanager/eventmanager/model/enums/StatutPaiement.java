package com.bschooleventmanager.eventmanager.model.enums;
// Enum représentant les différents statuts de paiement pour un événement
public enum StatutPaiement {
    EN_ATTENTE("En attente"),
    REUSSI("Réussi"),
    ECHOUE("Échoué"),
    REMBOURSE("Remboursé");

    private final String label;

    StatutPaiement(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
    
}