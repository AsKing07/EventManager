package com.bschooleventmanager.eventmanager.model.enums;
// Enum pour les niveaux d'expertise dans un spectacle
public enum NiveauExpertise {
    DEBUTANT("Debutant"),
    INTERMEDIAIRE("Intermediaire"),
    PROFESSIONNEL("Professionnel"),
    EXPERT("Expert"),
    MASTER("Master");


    private final String label;

    NiveauExpertise(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
    
    //Récupérer l'enum à partir du label
    public static NiveauExpertise fromLabel(String label) {
        for (NiveauExpertise niveau : NiveauExpertise.values()) {
            if (niveau.getLabel().equalsIgnoreCase(label)) {
                return niveau;
            }
        }
        return null; // ou lever une exception si le label n'est pas valide
    }
}
