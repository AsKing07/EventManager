package com.bschooleventmanager.eventmanager.service;

import com.bschooleventmanager.eventmanager.dao.EvenementDAO;
import com.bschooleventmanager.eventmanager.dao.ReservationDAO;
import com.bschooleventmanager.eventmanager.dao.ReservationDetailsDAO;
import com.bschooleventmanager.eventmanager.exception.BusinessException;
import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import com.bschooleventmanager.eventmanager.exception.PlacesInsuffisantesException;
import com.bschooleventmanager.eventmanager.model.Evenement;
import com.bschooleventmanager.eventmanager.model.Reservation;
import com.bschooleventmanager.eventmanager.model.ReservationDetail;
import com.bschooleventmanager.eventmanager.model.Utilisateur;
import com.bschooleventmanager.eventmanager.model.enums.CategorieTicket;
import com.bschooleventmanager.eventmanager.model.enums.StatutReservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service pour la gestion des réservations
 * Couche métier entre les contrôleurs et les DAO
 */
public class ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
    
    private final ReservationDAO reservationDAO;
    private final ReservationDetailsDAO detailsDAO;
    private final EvenementDAO evenementDAO;
    
    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
        this.detailsDAO = new ReservationDetailsDAO();
        this.evenementDAO = new EvenementDAO();
    }

    /**
     * Crée une nouvelle réservation avec validation complète
     * @param utilisateur L'utilisateur qui fait la réservation
     * @param evenement L'événement pour lequel réserver
     * @param quantiteStandard Nombre de places standard
     * @param quantiteVip Nombre de places VIP  
     * @param quantitePremium Nombre de places Premium
     * @param payerMaintenant Si le paiement est immédiat
     * @return La réservation créée
     * @throws PlacesInsuffisantesException Si pas assez de places disponibles
     * @throws BusinessException Pour les autres erreurs métier
     */
    public Reservation creerReservation(Utilisateur utilisateur, Evenement evenement,
                                      int quantiteStandard, int quantiteVip, int quantitePremium,
                                      boolean payerMaintenant) 
            throws PlacesInsuffisantesException, BusinessException {
        
        logger.info("Création réservation pour utilisateur {} sur événement {}", 
                   utilisateur.getIdUtilisateur(), evenement.getIdEvenement());
        
        try {
            // 1. Validations métier
            validateReservationRequest(utilisateur, evenement, quantiteStandard, quantiteVip, quantitePremium);
            
            // 2. Vérifier la disponibilité en temps réel (re-fetch de la DB)
            Evenement evenementActuel = evenementDAO.chercher(evenement.getIdEvenement());
            if (evenementActuel == null) {
                throw new BusinessException("Événement introuvable");
            }
            
            validateDisponibilite(evenementActuel, quantiteStandard, quantiteVip, quantitePremium);
            
            // 3. Calculer le total
            double total = calculerTotal(evenementActuel, quantiteStandard, quantiteVip, quantitePremium);
            
            // 4. Créer la réservation
            StatutReservation statut = payerMaintenant ? StatutReservation.CONFIRMEE : StatutReservation.EN_ATTENTE;
            
            Reservation reservation = new Reservation(
                utilisateur.getIdUtilisateur(),
                evenement.getIdEvenement(),
                LocalDateTime.now().toString(),
                statut,
                total
            );
            
            // 5. Transaction : créer réservation + détails + mettre à jour places
            reservation = reservationDAO.creer(reservation);
            
            // 6. Créer les détails
            creerDetailsReservation(reservation.getIdReservation(), evenementActuel,
                                  quantiteStandard, quantiteVip, quantitePremium);
            
            // 7. Mettre à jour les places vendues
            mettreAJourPlacesVendues(evenementActuel, quantiteStandard, quantiteVip, quantitePremium);
            
            logger.info("✓ Réservation créée avec succès: ID {}, Total: {}€", 
                       reservation.getIdReservation(), total);
            
            return reservation;
            
        } catch (PlacesInsuffisantesException e) {
            logger.warn("Places insuffisantes pour la réservation: {}", e.getMessage());
            throw e; // Re-throw exception métier
        } catch (DatabaseException e) {
            logger.error("Erreur base de données lors de la création de réservation", e);
            throw new BusinessException("Erreur technique lors de la réservation. Veuillez réessayer.", e);
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la création de réservation", e);
            throw new BusinessException("Une erreur inattendue s'est produite. Veuillez contacter le support.", e);
        }
    }

    /**
     * Récupère les réservations d'un client
     */
    public List<Reservation> getReservationsClient(int clientId) throws BusinessException {
        try {
            return reservationDAO.getReservationsParClient(clientId);
        } catch (DatabaseException e) {
            logger.error("Erreur récupération réservations client {}", clientId, e);
            throw new BusinessException("Erreur lors de la récupération de l'historique des réservations", e);
        }
    }

    /**
     * Récupère les détails d'une réservation
     */
    public List<ReservationDetail> getDetailsReservation(int reservationId) throws BusinessException {
        try {
            return detailsDAO.getDetailsParReservation(reservationId);
        } catch (DatabaseException e) {
            logger.error("Erreur récupération détails réservation {}", reservationId, e);
            throw new BusinessException("Erreur lors de la récupération des détails de réservation", e);
        }
    }

    /**
     * Annule une réservation
     */
    public void annulerReservation(int reservationId, Utilisateur utilisateur) throws BusinessException {
        try {
            Reservation reservation = reservationDAO.chercher(reservationId);
            if (reservation == null) {
                throw new BusinessException("Réservation introuvable");
            }
            
            // Vérifier que l'utilisateur peut annuler cette réservation
            if (reservation.getClientId() != utilisateur.getIdUtilisateur()) {
                throw new BusinessException("Vous n'êtes pas autorisé à annuler cette réservation");
            }
            
            // Vérifier le statut
            if (reservation.getStatut() == StatutReservation.ANNULEE) {
                throw new BusinessException("Cette réservation est déjà annulée");
            }
            
            // Mettre à jour le statut
            reservation.setStatut(StatutReservation.ANNULEE);
            reservation.setDateAnnulation(LocalDateTime.now());
            
            reservationDAO.mettreAJour(reservation);
            
            // Remettre les places disponibles dans l'événement
            List<ReservationDetail> details = detailsDAO.getDetailsParReservation(reservationId);
            for (ReservationDetail detail : details) {
                // Récupérer l'événement et remettre les places
                Evenement evenement = evenementDAO.chercher(reservation.getIdEvenement());
                if (evenement != null) {
                    int nouvellesPlacesStandard = evenement.getPlaceStandardVendues();
                    int nouvellesPlacesVip = evenement.getPlaceVipVendues();
                    int nouvellesPlacesPremium = evenement.getPlacePremiumVendues();
                    
                    // Réduire le nombre de places vendues selon la catégorie
                    switch (detail.getCategoriePlace()) {
                        case STANDARD:
                            nouvellesPlacesStandard = Math.max(0, nouvellesPlacesStandard - detail.getNombreTickets());
                            break;
                        case VIP:
                            nouvellesPlacesVip = Math.max(0, nouvellesPlacesVip - detail.getNombreTickets());
                            break;
                        case PREMIUM:
                            nouvellesPlacesPremium = Math.max(0, nouvellesPlacesPremium - detail.getNombreTickets());
                            break;
                    }
                    
                    // Mettre à jour l'événement
                    EvenementDAO.mettreAJourPlacesVendues(
                        evenement.getIdEvenement(),
                        nouvellesPlacesStandard,
                        nouvellesPlacesVip,
                        nouvellesPlacesPremium
                    );
                }
            }
            
            logger.info("✓ Réservation {} annulée par utilisateur {}", reservationId, utilisateur.getIdUtilisateur());
            
        } catch (DatabaseException e) {
            logger.error("Erreur annulation réservation {}", reservationId, e);
            throw new BusinessException("Erreur lors de l'annulation de la réservation", e);
        }
    }

    // === MÉTHODES PRIVÉES DE VALIDATION ===

    private void validateReservationRequest(Utilisateur utilisateur, Evenement evenement,
                                          int quantiteStandard, int quantiteVip, int quantitePremium) 
            throws BusinessException {
        
        if (utilisateur == null) {
            throw new BusinessException("Utilisateur non connecté");
        }
        
        if (evenement == null) {
            throw new BusinessException("Événement non spécifié");
        }
        
        int totalQuantite = quantiteStandard + quantiteVip + quantitePremium;
        if (totalQuantite <= 0) {
            throw new BusinessException("Veuillez sélectionner au moins une place");
        }
        
        if (totalQuantite > 10) {
            throw new BusinessException("Maximum 10 places par réservation");
        }
        
        // Vérifier la date de l'événement (avec marge de 30 minutes)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventDateTime = evenement.getDateEvenement();
        LocalDateTime cutoffTime = eventDateTime.minusMinutes(30);
        
        if (now.isAfter(eventDateTime)) {
            throw new BusinessException("Impossible de réserver pour un événement passé");
        } else if (now.isAfter(cutoffTime)) {
            throw new BusinessException("Les réservations pour cet événement sont fermées (fermeture 30 minutes avant le début)");
        }
    }

    private void validateDisponibilite(Evenement evenement, int quantiteStandard, 
                                     int quantiteVip, int quantitePremium) 
            throws PlacesInsuffisantesException {
        
        if (quantiteStandard > evenement.getPlacesStandardRestantes()) {
            throw new PlacesInsuffisantesException(
                String.format("Places Standard insuffisantes. Disponibles: %d, Demandées: %d", 
                            evenement.getPlacesStandardRestantes(), quantiteStandard));
        }
        
        if (quantiteVip > evenement.getPlacesVipRestantes()) {
            throw new PlacesInsuffisantesException(
                String.format("Places VIP insuffisantes. Disponibles: %d, Demandées: %d",
                            evenement.getPlacesVipRestantes(), quantiteVip));
        }
        
        if (quantitePremium > evenement.getPlacesPremiumRestantes()) {
            throw new PlacesInsuffisantesException(
                String.format("Places Premium insuffisantes. Disponibles: %d, Demandées: %d",
                            evenement.getPlacesPremiumRestantes(), quantitePremium));
        }
    }

    private double calculerTotal(Evenement evenement, int quantiteStandard, 
                               int quantiteVip, int quantitePremium) {
        double total = 0.0;
        
        if (quantiteStandard > 0 && evenement.getPrixStandard() != null) {
            total += quantiteStandard * evenement.getPrixStandard().doubleValue();
        }
        
        if (quantiteVip > 0 && evenement.getPrixVip() != null) {
            total += quantiteVip * evenement.getPrixVip().doubleValue();
        }
        
        if (quantitePremium > 0 && evenement.getPrixPremium() != null) {
            total += quantitePremium * evenement.getPrixPremium().doubleValue();
        }
        
        return total;
    }

    private void creerDetailsReservation(int reservationId, Evenement evenement,
                                       int quantiteStandard, int quantiteVip, int quantitePremium) 
            throws DatabaseException {
        
        if (quantiteStandard > 0 && evenement.getPrixStandard() != null) {
            ReservationDetail detail = new ReservationDetail(
                reservationId,
                CategorieTicket.STANDARD,
                quantiteStandard,
                evenement.getPrixStandard().doubleValue()
            );
            detailsDAO.creer(detail);
        }
        
        if (quantiteVip > 0 && evenement.getPrixVip() != null) {
            ReservationDetail detail = new ReservationDetail(
                reservationId,
                CategorieTicket.VIP,
                quantiteVip,
                evenement.getPrixVip().doubleValue()
            );
            detailsDAO.creer(detail);
        }
        
        if (quantitePremium > 0 && evenement.getPrixPremium() != null) {
            ReservationDetail detail = new ReservationDetail(
                reservationId,
                CategorieTicket.PREMIUM,
                quantitePremium,
                evenement.getPrixPremium().doubleValue()
            );
            detailsDAO.creer(detail);
        }
    }

    private void mettreAJourPlacesVendues(Evenement evenement, int quantiteStandard, 
                                        int quantiteVip, int quantitePremium) 
            throws DatabaseException {
        
        EvenementDAO.mettreAJourPlacesVendues(
            evenement.getIdEvenement(),
            evenement.getPlaceStandardVendues() + quantiteStandard,
            evenement.getPlaceVipVendues() + quantiteVip,
            evenement.getPlacePremiumVendues() + quantitePremium
        );
    }
}