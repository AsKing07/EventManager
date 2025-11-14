package com.bschooleventmanager.eventmanager.util;

import com.bschooleventmanager.eventmanager.model.Utilisateur;

public class SessionManager {
    private static Utilisateur utilisateurConnecte;
    private static long dateConnexion;

    public static void setUtilisateurConnecte(Utilisateur user) {
        utilisateurConnecte = user;
        dateConnexion = System.currentTimeMillis();
    }

    public static Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

//    public static int getIdUtilisateurConnecte() {
//        return utilisateurConnecte != null ? utilisateurConnecte.getIdUtilisateur() : -1;
//    }

    public static boolean isConnecte() {
        return utilisateurConnecte != null;
    }

    public static void deconnecter() {
        utilisateurConnecte = null;
        dateConnexion = 0;
    }

    public static long getDateConnexion() {
        return dateConnexion;
    }
}
