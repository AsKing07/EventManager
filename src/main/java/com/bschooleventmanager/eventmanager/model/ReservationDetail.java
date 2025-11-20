package com.bschooleventmanager.eventmanager.model;


import com.bschooleventmanager.eventmanager.model.enums.CategorieTicket;
// Classe représentant le détail d'une réservation
public class ReservationDetail {



    private int idDetail;
    private int idReservation;
    private CategorieTicket categoriePlace;
    private int nombreTickets;
    private double prixUnitaire;
    private double sousTotal;


    // Constructeur vide
    public ReservationDetail() {
    }

    // Constructeur complet
    public ReservationDetail(int idDetail, int idReservation, CategorieTicket categoriePlace,
                             int nombreTickets, double prixUnitaire, double sousTotal) {
        this.idDetail = idDetail;
        this.idReservation = idReservation;
        this.categoriePlace = categoriePlace;
        this.nombreTickets = nombreTickets;
        this.prixUnitaire = prixUnitaire;
        this.sousTotal = sousTotal;
    }

    // Constructeur pour création
    public ReservationDetail(int idReservation, CategorieTicket categoriePlace,
                             int nombreTickets, double prixUnitaire) {
        this.idReservation = idReservation;
        this.categoriePlace = categoriePlace;
        this.nombreTickets = nombreTickets;
        this.prixUnitaire = prixUnitaire;
        this.sousTotal = prixUnitaire * nombreTickets;
    }

    // Getters et Setters
    public int getIdDetail() { return idDetail; }
    public void setIdDetail(int idDetail) { this.idDetail = idDetail; }

    public int getIdReservation() { return idReservation; }
    public void setIdReservation(int idReservation) { this.idReservation = idReservation; }

    public CategorieTicket getCategoriePlace() { return categoriePlace; }
    public void setCategoriePlace(CategorieTicket categoriePlace) { this.categoriePlace = categoriePlace; }

    public int getNombreTickets() { return nombreTickets; }
    public void setNombreTickets(int nombreTickets) { this.nombreTickets = nombreTickets; }

    public double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public double getSousTotal() { return sousTotal; }
    public void setSousTotal(double sousTotal) { this.sousTotal = sousTotal; }

    // Méthode toString
    @Override
    public String toString() {
        return "ReservationDetail{" +
                "idDetail=" + idDetail +
                ", idReservation=" + idReservation +
                ", categoriePlace=" + categoriePlace +
                ", nombreTickets=" + nombreTickets +
                ", prixUnitaire=" + prixUnitaire +
                ", sousTotal=" + getSousTotal() +
                '}';
    }
}

