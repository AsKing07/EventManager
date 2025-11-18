package com.bschooleventmanager.eventmanager.model.enums;

public enum MethodePaiement {
    CARTE_CREDIT("Carte de crédit"),
    CARTE_DEBIT("Carte de débit"),
    PAYPAL("PayPal"),
    STRIPE("Stripe"),
    VIREMENT("Virement bancaire"),
    ESPECES("Espèces");

    private final String label;

    MethodePaiement(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static MethodePaiement fromLabel(String label) {
        for (MethodePaiement methode : values()) {
            if (methode.label.equals(label)) {
                return methode;
            }
        }
        throw new IllegalArgumentException("Méthode de paiement inconnue: " + label);
    }
}