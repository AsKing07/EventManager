package com.bschooleventmanager.eventmanager.model;

import com.bschooleventmanager.eventmanager.model.enums.StatutPaiement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modèle représentant un paiement effectué pour une réservation
 */
public class Paiement {
    private int idPaiement;
    private int idReservation;
    private BigDecimal montant;
    private LocalDateTime datePaiement;
    private StatutPaiement statut;
    private String methodePaiement;
    private String numeroTransaction;

    // Constructeurs
    public Paiement() {}

    public Paiement(int idReservation, BigDecimal montant, StatutPaiement statut, String methodePaiement) {
        this.idReservation = idReservation;
        this.montant = montant;
        this.datePaiement = LocalDateTime.now();
        this.statut = statut;
        this.methodePaiement = methodePaiement;
    }

    public Paiement(int idPaiement, int idReservation, BigDecimal montant, LocalDateTime datePaiement, 
                   StatutPaiement statut, String methodePaiement, String numeroTransaction) {
        this.idPaiement = idPaiement;
        this.idReservation = idReservation;
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.statut = statut;
        this.methodePaiement = methodePaiement;
        this.numeroTransaction = numeroTransaction;
    }

    // Getters et Setters
    public int getIdPaiement() {
        return idPaiement;
    }

    public void setIdPaiement(int idPaiement) {
        this.idPaiement = idPaiement;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }

    public StatutPaiement getStatut() {
        return statut;
    }

    public void setStatut(StatutPaiement statut) {
        this.statut = statut;
    }

    public String getMethodePaiement() {
        return methodePaiement;
    }

    public void setMethodePaiement(String methodePaiement) {
        this.methodePaiement = methodePaiement;
    }

    public String getNumeroTransaction() {
        return numeroTransaction;
    }

    public void setNumeroTransaction(String numeroTransaction) {
        this.numeroTransaction = numeroTransaction;
    }

    @Override
    public String toString() {
        return String.format("Paiement{id=%d, reservation=%d, montant=%s, date=%s, statut=%s, methode='%s', transaction='%s'}", 
                idPaiement, idReservation, montant, datePaiement, statut, methodePaiement, numeroTransaction);
    }
}
