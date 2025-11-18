package com.bschooleventmanager.eventmanager.service;

import com.bschooleventmanager.eventmanager.config.StripeConfig;
import com.bschooleventmanager.eventmanager.exception.PaiementInvalideException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Service d'intégration avec l'API Stripe
 * Gère les paiements via Stripe en mode test
 */
public class StripePaymentService {
    private static final Logger logger = LoggerFactory.getLogger(StripePaymentService.class);
    
    private final StripeConfig config;
    private boolean initialized = false;
    
    public StripePaymentService() {
        this.config = StripeConfig.getInstance();
        initializeStripe();
    }
    
    /**
     * Initialise la configuration Stripe
     */
    private void initializeStripe() {
        try {
            if (config.isConfigured()) {
                Stripe.apiKey = config.getSecretKey();
                initialized = true;
                logger.info("✓ Stripe initialisé en mode {}", config.isTestMode() ? "TEST" : "PRODUCTION");
            } else {
                logger.warn("⚠️ Stripe non configuré - utilisation de la simulation");
            }
        } catch (Exception e) {
            logger.error("❌ Erreur lors de l'initialisation de Stripe", e);
        }
    }
    
    /**
     * Crée un PaymentIntent Stripe
     * @param montant Montant en euros
     * @param currency Devise (EUR par défaut)
     * @param description Description du paiement
     * @return L'ID du PaymentIntent ou un ID simulé
     */
    public String creerPaymentIntent(BigDecimal montant, String currency, String description) 
            throws PaiementInvalideException {
        
        if (!initialized) {
            // Mode simulation si Stripe n'est pas configuré
            return simulatePaymentIntent(montant, description);
        }
        
        try {
            // Convertir le montant en centimes (Stripe utilise les centimes)
            long montantEnCentimes = montant.multiply(BigDecimal.valueOf(100)).longValue();
            
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(montantEnCentimes)
                .setCurrency(currency.toLowerCase())
                .setDescription(description)
                .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                .setConfirm(true)
                .setReturnUrl("https://votre-site.com/return") // URL de retour (optionnel en test)
                .build();
            
            PaymentIntent intent = PaymentIntent.create(params);
            
            logger.info("✓ PaymentIntent créé: {} pour {} centimes", intent.getId(), montantEnCentimes);
            return intent.getId();
            
        } catch (StripeException e) {
            logger.error("❌ Erreur Stripe lors de la création du PaymentIntent", e);
            throw new PaiementInvalideException("Erreur Stripe: " + e.getMessage());
        }
    }
    
    /**
     * Confirme un paiement avec un token de carte test
     * @param paymentIntentId ID du PaymentIntent
     * @param testCardToken Token de carte test Stripe
     * @return Le statut du paiement
     */
    public PaymentResult confirmerPaiement(String paymentIntentId, String testCardToken) 
            throws PaiementInvalideException {
        
        if (!initialized) {
            // Mode simulation
            return simulatePaymentConfirmation(paymentIntentId, testCardToken);
        }
        
        try {
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
            
            if ("succeeded".equals(intent.getStatus())) {
                return new PaymentResult(true, intent.getId(), "Payment succeeded");
            } else if ("requires_action".equals(intent.getStatus())) {
                // Certains paiements peuvent nécessiter une action supplémentaire
                logger.info("⚠️ Paiement nécessite une action: {}", intent.getNextAction());
                return new PaymentResult(false, intent.getId(), "Payment requires additional action");
            } else {
                return new PaymentResult(false, intent.getId(), "Payment failed: " + intent.getStatus());
            }
            
        } catch (StripeException e) {
            logger.error("❌ Erreur lors de la confirmation du paiement", e);
            throw new PaiementInvalideException("Erreur de confirmation Stripe: " + e.getMessage());
        }
    }
    
    /**
     * Traite un paiement complet avec une carte de test Stripe
     * Utilise des tokens de test Stripe au lieu de numéros de carte bruts
     */
    public PaymentResult traiterPaiementComplet(BigDecimal montant, String nomPorteur, 
                                               String numeroCarteTest, String description) 
            throws PaiementInvalideException {
        
        if (!initialized) {
            // Mode simulation si Stripe n'est pas configuré
            return simulatePaymentConfirmation("pi_sim_" + System.currentTimeMillis(), numeroCarteTest);
        }
        
        try {
            // Convertir le montant en centimes
            long montantEnCentimes = montant.multiply(BigDecimal.valueOf(100)).longValue();
            
            // Obtenir le token de test correspondant à la carte
            String testToken = getTestTokenForCard(numeroCarteTest);
            
            if (testToken != null) {
                // Utiliser un token de test prédéfini
                PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(montantEnCentimes)
                    .setCurrency("eur")
                    .setDescription(description)
                    .setPaymentMethod(testToken)
                    .setConfirm(true)
                    .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                            .setEnabled(true)
                            .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                            .build()
                    )
                    .build();
                
                PaymentIntent intent = PaymentIntent.create(params);
                
                // Vérifier le statut
                String status = intent.getStatus();
                boolean success = "succeeded".equals(status);
                String message = switch (status) {
                    case "succeeded" -> "Payment completed successfully";
                    case "requires_payment_method" -> "Payment method was declined";
                    case "requires_action" -> "Payment requires additional authentication";
                    default -> "Payment status: " + status;
                };
                
                logger.info("✓ Paiement Stripe avec token: {} € - Status: {} - Transaction: {}", 
                           montant, status, intent.getId());
                
                return new PaymentResult(success, intent.getId(), message);
            } else {
                // Créer un PaymentIntent simple et simuler le résultat
                PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(montantEnCentimes)
                    .setCurrency("eur")
                    .setDescription(description)
                    .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                            .setEnabled(true)
                            .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                            .build()
                    )
                    .build();
                
                PaymentIntent intent = PaymentIntent.create(params);
                
                // Simuler le résultat basé sur le numéro de carte
                boolean success = !numeroCarteTest.startsWith("4000000000000002");
                String message = success ? "Payment succeeded (simulated with test card)" : "Card declined (test)";
                
                logger.info("✓ Paiement Stripe simulé: {} € - Transaction: {}", montant, intent.getId());
                
                return new PaymentResult(success, intent.getId(), message);
            }
            
        } catch (StripeException e) {
            logger.error("❌ Erreur lors du traitement du paiement Stripe: {}", e.getMessage());
            
            // Gérer les cartes de test qui échouent volontairement
            if (numeroCarteTest.startsWith("4000000000000002")) {
                return new PaymentResult(false, "declined_" + System.currentTimeMillis(), "Card declined (test card)");
            }
            
            throw new PaiementInvalideException("Erreur Stripe: " + e.getMessage());
        }
    }
    
    /**
     * Retourne un token de test Stripe prédéfini selon la carte utilisée
     * En production, ces tokens seraient générés côté client avec Stripe.js
     */
    private String getTestTokenForCard(String numeroCarteTest) {
        // Tokens de test Stripe standard (ces tokens sont publics et sûrs)
        return switch (numeroCarteTest) {
            case "4242424242424242" -> "pm_card_visa"; // Token Visa test
            case "4000000000000002" -> "pm_card_chargeDeclined"; // Token carte déclinée
            case "4000000000000069" -> "pm_card_expired"; // Token carte expirée
            case "5555555555554444" -> "pm_card_mastercard"; // Token Mastercard test
            default -> null; // Utiliser la simulation pour les autres cartes
        };
    }
    
    /**
     * Simule un PaymentIntent quand Stripe n'est pas configuré
     */
    private String simulatePaymentIntent(BigDecimal montant, String description) {
        String simulatedId = "pi_test_" + System.currentTimeMillis();
        logger.info("🎭 PaymentIntent simulé: {} pour {} € - {}", simulatedId, montant, description);
        return simulatedId;
    }
    
    /**
     * Simule une confirmation de paiement
     */
    private PaymentResult simulatePaymentConfirmation(String paymentIntentId, String cardNumber) {
        // Simuler les réponses selon les cartes test Stripe
        boolean success = !cardNumber.startsWith("4000000000000002"); // Carte de déclin
        
        String message = success ? "Simulated payment succeeded" : "Simulated payment declined";
        
        logger.info("🎭 Confirmation simulée: {} - {}", paymentIntentId, message);
        
        return new PaymentResult(success, paymentIntentId, message);
    }
    
    /**
     * Vérifie si Stripe est correctement configuré
     */
    public boolean isConfigured() {
        return initialized && config.isConfigured();
    }
    
    /**
     * Classe pour le résultat d'un paiement
     */
    public static class PaymentResult {
        private final boolean success;
        private final String transactionId;
        private final String message;
        
        public PaymentResult(boolean success, String transactionId, String message) {
            this.success = success;
            this.transactionId = transactionId;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getTransactionId() { return transactionId; }
        public String getMessage() { return message; }
    }
}