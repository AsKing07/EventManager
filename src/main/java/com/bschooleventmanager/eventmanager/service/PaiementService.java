package com.bschooleventmanager.eventmanager.service;

import com.bschooleventmanager.eventmanager.dao.PaiementDAO;
import com.bschooleventmanager.eventmanager.dao.ReservationDAO;
import com.bschooleventmanager.eventmanager.exception.BusinessException;
import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import com.bschooleventmanager.eventmanager.exception.PaiementInvalideException;
import com.bschooleventmanager.eventmanager.model.Paiement;
import com.bschooleventmanager.eventmanager.model.Reservation;
import com.bschooleventmanager.eventmanager.model.enums.MethodePaiement;
import com.bschooleventmanager.eventmanager.model.enums.StatutPaiement;
import com.bschooleventmanager.eventmanager.model.enums.StatutReservation;
import com.bschooleventmanager.eventmanager.service.StripePaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Service de gestion des paiements
 * Gère la logique métier liée aux transactions de paiement
 */
public class PaiementService {
    private static final Logger logger = LoggerFactory.getLogger(PaiementService.class);

    private final PaiementDAO paiementDAO;
    private final ReservationDAO reservationDAO;
    private final StripePaymentService stripeService;

    // Patterns de validation
    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("^\\d{13,19}$");
    private static final Pattern CVV_PATTERN = Pattern.compile("^\\d{3,4}$");

    public PaiementService() {
        this.paiementDAO = new PaiementDAO();
        this.reservationDAO = new ReservationDAO();
        this.stripeService = new StripePaymentService();
    }

    /**
     * Traite un paiement pour une réservation donnée
     */
    public Paiement traiterPaiement(int idReservation, String nomPorteur, String numeroCarteOuToken, 
                                   String cvv, String moisExpiration, String anneeExpiration, 
                                   MethodePaiement methode) throws PaiementInvalideException, BusinessException {
        try {
            logger.info("Début du traitement de paiement pour la réservation: {}", idReservation);

            // 1. Valider les données de paiement
            validerDonneesPaiement(nomPorteur, numeroCarteOuToken, cvv, moisExpiration, anneeExpiration, methode);

            // 2. Récupérer la réservation
            Reservation reservation = reservationDAO.chercher(idReservation);
            if (reservation == null) {
                throw new BusinessException("Réservation non trouvée avec l'ID: " + idReservation);
            }

            // 3. Vérifier si la réservation nécessite un paiement
            BigDecimal totalAPayerReservation = BigDecimal.valueOf(reservation.getTotalPaye());
            if (totalAPayerReservation.compareTo(BigDecimal.ZERO) == 0) {
                throw new BusinessException("Cette réservation ne nécessite pas de paiement");
            }

            // 4. Vérifier si la réservation n'est pas déjà payée
            // BigDecimal montantDejaPaye = paiementDAO.calculerMontantTotalPaye(idReservation);
            // BigDecimal montantRestant = totalPayeReservation.subtract(montantDejaPaye);

            // if (montantRestant.compareTo(BigDecimal.ZERO) <= 0) {
            //     throw new BusinessException("Cette réservation est déjà entièrement payée");
            // }

            // 5. Créer le paiement avec statut EN_ATTENTE
            Paiement paiement = new Paiement(
                idReservation,
                totalAPayerReservation,
                StatutPaiement.EN_ATTENTE,
                methode.getLabel()
            );

            // 6. Traitement selon la méthode de paiement
            String numeroTransaction;
            try {
                switch (methode) {
                    case STRIPE -> {
                        numeroTransaction = traiterPaiementStripe(numeroCarteOuToken, totalAPayerReservation);
                    }
                    case CARTE_CREDIT, CARTE_DEBIT -> {
                        numeroTransaction = traiterPaiementCarte(numeroCarteOuToken, cvv, moisExpiration, anneeExpiration, totalAPayerReservation);
                    }
                    default -> {
                        numeroTransaction = genererNumeroTransaction();
                    }
                }
                
                // 7. Mettre à jour le paiement avec succès
                paiement.setStatut(StatutPaiement.REUSSI);
                paiement.setNumeroTransaction(numeroTransaction);
                paiement.setDatePaiement(LocalDateTime.now());

            } catch (Exception e) {
                // 8. En cas d'échec, marquer le paiement comme échoué
                paiement.setStatut(StatutPaiement.ECHOUE);
                paiement.setNumeroTransaction("FAILED_" + UUID.randomUUID());
                logger.error("Échec du traitement de paiement pour la réservation: {}", idReservation, e);
                throw new PaiementInvalideException("Échec du traitement du paiement: " + e.getMessage());
            }

            // 9. Enregistrer le paiement en base
            Paiement paiementCree = paiementDAO.creer(paiement);

            // 10. Mettre à jour le statut de la réservation 
            if (paiement.getStatut() == StatutPaiement.REUSSI) {
             
                    reservation.setStatut(StatutReservation.CONFIRMEE);
                    reservationDAO.mettreAJour(reservation);
                    logger.info("✓ Réservation {} confirmée après paiement complet", idReservation);
                
            }

            logger.info("✓ Paiement traité avec succès - Transaction: {}", numeroTransaction);
            return paiementCree;

        } catch (DatabaseException e) {
            logger.error("Erreur de base de données lors du traitement du paiement", e);
            throw new BusinessException("Erreur technique lors du paiement: " + e.getMessage());
        }
    }

    /**
     * Valide les données de paiement
     */
    private void validerDonneesPaiement(String nomPorteur, String numeroCarteOuToken, String cvv, 
                                      String moisExpiration, String anneeExpiration, MethodePaiement methode) 
                                      throws PaiementInvalideException {
        
        if (nomPorteur == null || nomPorteur.trim().isEmpty()) {
            throw new PaiementInvalideException("Le nom du porteur est obligatoire");
        }

        if (nomPorteur.trim().length() < 2) {
            throw new PaiementInvalideException("Le nom du porteur doit contenir au moins 2 caractères");
        }

        if (methode == MethodePaiement.STRIPE) {
            // Pour Stripe, numeroCarteOuToken est un token, validation différente
            if (numeroCarteOuToken == null || numeroCarteOuToken.trim().isEmpty()) {
                throw new PaiementInvalideException("Token de paiement manquant");
            }
            return; // Skip autres validations pour Stripe
        }

        // Validation pour carte classique
        if (numeroCarteOuToken == null || !CARD_NUMBER_PATTERN.matcher(numeroCarteOuToken.replaceAll("\\s", "")).matches()) {
            throw new PaiementInvalideException("Numéro de carte invalide (13-19 chiffres requis)");
        }

        if (cvv == null || !CVV_PATTERN.matcher(cvv).matches()) {
            throw new PaiementInvalideException("Code CVV invalide (3-4 chiffres requis)");
        }

        // Validation date d'expiration
        try {
            int mois = Integer.parseInt(moisExpiration);
            int annee = Integer.parseInt(anneeExpiration);
            
            if (mois < 1 || mois > 12) {
                throw new PaiementInvalideException("Mois d'expiration invalide (01-12)");
            }

            LocalDateTime maintenant = LocalDateTime.now();
            LocalDateTime expiration = LocalDateTime.of(2000 + annee, mois, 1, 0, 0).plusMonths(1).minusDays(1);
            
            if (expiration.isBefore(maintenant)) {
                throw new PaiementInvalideException("Carte expirée");
            }

        } catch (NumberFormatException e) {
            throw new PaiementInvalideException("Format de date d'expiration invalide");
        }
    }

    /**
     * Traite un paiement Stripe avec l'API réelle
     */
    private String traiterPaiementStripe(String numeroCarteTest, BigDecimal montant) throws Exception {
        logger.info("Traitement paiement Stripe - Carte test: {}, Montant: {}", 
                   numeroCarteTest.substring(0, 4) + "****", montant);
        
        try {
            // Utiliser le service Stripe pour traiter le paiement
            StripePaymentService.PaymentResult result = stripeService.traiterPaiementComplet(
                montant, 
                "Client EventManager", 
                numeroCarteTest,
                "Réservation événement - EventManager"
            );
            
            if (result.isSuccess()) {
                logger.info("✓ Paiement Stripe réussi - Transaction: {}", result.getTransactionId());
                return result.getTransactionId();
            } else {
                throw new Exception("Paiement Stripe échoué: " + result.getMessage());
            }
            
        } catch (PaiementInvalideException e) {
            logger.error("❌ Erreur de validation Stripe: {}", e.getMessage());
            throw new Exception("Erreur de validation Stripe: " + e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Erreur inattendue Stripe", e);
            throw new Exception("Erreur Stripe: " + e.getMessage());
        }
    }

    /**
     * Simule le traitement d'un paiement carte classique
     */
    private String traiterPaiementCarte(String numeroCarteChiffre, String cvv, String mois, String annee, BigDecimal montant) throws Exception {
        logger.info("Traitement paiement carte - Montant: {}", montant);
        
        // Simulation d'un appel à un processeur de paiement
        String numeroTransaction = "card_" + UUID.randomUUID().toString().substring(0, 12);
        
        // Simulation de délai de traitement
        Thread.sleep(1000);
        
        // Simulation d'échec pour certains numéros (tests)
        if (numeroCarteChiffre.startsWith("4000000000000002")) {
            throw new Exception("Card declined");
        }
        
        logger.info("✓ Paiement carte simulé avec succès - Transaction: {}", numeroTransaction);
        return numeroTransaction;
    }

    /**
     * Génère un numéro de transaction unique
     */
    private String genererNumeroTransaction() {
        return "pay_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * Récupère l'historique des paiements d'une réservation
     */
    public List<Paiement> obtenirHistoriquePaiements(int idReservation) throws BusinessException {
        try {
            return paiementDAO.trouverParReservation(idReservation);
        } catch (DatabaseException e) {
            logger.error("Erreur lors de la récupération de l'historique de paiements", e);
            throw new BusinessException("Impossible de récupérer l'historique des paiements");
        }
    }


}