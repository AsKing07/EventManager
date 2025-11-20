package com.bschooleventmanager.eventmanager.model;


import com.bschooleventmanager.eventmanager.model.enums.StatutReservation;

import java.time.LocalDateTime;
import java.util.List;
// Classe représentant une réservation
public class Reservation {

    private int idReservation;
    private int clientId;
    private int idEvenement;
    private String dateReservation;
    private StatutReservation statut;
    private double totalPaye;
    private List<ReservationDetail> details;
    private LocalDateTime dateAnnulation;

    // Constructeur vide
    public Reservation() {
    }

    // Constructeur complet
    public Reservation(int idReservation, int clientId, int idEvenement,
                       String dateReservation, StatutReservation statut, double totalPaye, List<ReservationDetail> details, LocalDateTime dateAnnulation) {
        this.idReservation = idReservation;
        this.clientId = clientId;
        this.idEvenement = idEvenement;
        this.dateReservation = dateReservation;
        this.statut = statut;
        this.totalPaye = totalPaye;
        this.details = details;
        this.dateAnnulation = dateAnnulation;

    }

    // Constructeur pour création
    public Reservation(int clientId, int idEvenement,
                       String dateReservation, StatutReservation statut, double totalPaye) {
        this.clientId = clientId;
        this.idEvenement = idEvenement;
        this.dateReservation = dateReservation;
        this.statut = statut;
        this.totalPaye = totalPaye;
    }

    // Getters et Setters
    public int getIdReservation() { return idReservation; }
    public void setIdReservation(int idReservation) { this.idReservation = idReservation; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public int getIdEvenement() { return idEvenement; }
    public void setIdEvenement(int idEvenement) { this.idEvenement = idEvenement; }

    public String getDateReservation() { return dateReservation; }
    public void setDateReservation(String dateReservation) { this.dateReservation = dateReservation; }

    public StatutReservation getStatut() { return statut; }
    public void setStatut(StatutReservation statut) { this.statut = statut; }

    public double getTotalPaye() { return totalPaye; }
    public void setTotalPaye(double totalPaye) { this.totalPaye = totalPaye; }

    public List<ReservationDetail> getDetails() { return details; }
    public void setDetails(List<ReservationDetail> details) { this.details = details; }

    public LocalDateTime getDateAnnulation() { return dateAnnulation; }
    public void setDateAnnulation(LocalDateTime dateAnnulation) { this.dateAnnulation = dateAnnulation; }

    // Méthode toString
    @Override
    public String toString() {
        return "Reservation{" +
                "idReservation=" + idReservation +
                ", clientId=" + clientId +
                ", idEvenement=" + idEvenement +
                ", dateReservation='" + dateReservation + '\'' +
                ", statut=" + statut +
                ", totalPaye=" + totalPaye +
                '}';
    }
}
