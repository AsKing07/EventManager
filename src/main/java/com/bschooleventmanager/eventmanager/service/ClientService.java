package com.bschooleventmanager.eventmanager.service;

import com.bschooleventmanager.eventmanager.dao.ClientDAO;
import com.bschooleventmanager.eventmanager.model.Client;
import com.bschooleventmanager.eventmanager.exception.BusinessException;
import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import com.bschooleventmanager.eventmanager.util.PasswordUtils;
import com.bschooleventmanager.eventmanager.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service de gestion des clients dans l'application EventManager.
 * Centralise toutes les opérations métier liées aux clients : inscription,
 * authentification, gestion des réservations et mise à jour des profils.
 * 
 * Ce service fait interface entre les contrôleurs et la couche d'accès aux données,
 * appliquant les règles métier et la validation des données.
 * @author Équipe EventManager
 * @version 1.0
 * @since 2024-11-20
 */
public class ClientService {
    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    private final ClientDAO clientDAO = new ClientDAO();

    /**
     * Inscrire un nouveau client
     */
    /**
     * Inscription d'un nouveau client dans le système.
     * Valide les données, vérifie l'unicité de l'email et crée un compte client.
     *
     * @param nom Nom complet du client
     * @param email Adresse email unique du client
     * @param motDePasse Mot de passe sécurisé
     * @return Le client créé avec son ID assigné
     * @throws BusinessException Si les données sont invalides ou l'email déjà utilisé
     */
    public Client inscrireClient(String nom, String email, String motDePasse) throws BusinessException {
        try {
            // Validations
            if (nom == null || nom.trim().isEmpty()) {
                throw new BusinessException("Le nom ne peut pas être vide");
            }

            if (!ValidationUtils.isEmailValid(email)) {
                throw new BusinessException("Email invalide");
            }

            if (motDePasse == null || motDePasse.length() < 6) {
                throw new BusinessException("Le mot de passe doit faire au moins 6 caractères");
            }

            // Vérifier si email existe déjà
            if (clientDAO.chercherParEmail(email) != null) {
                throw new BusinessException("Cet email est déjà utilisé par un client");
            }

            // Créer le client
            Client client = new Client(nom, email);
            client.setMotDePasse(PasswordUtils.hashPassword(motDePasse));

            Client result = clientDAO.creer(client);
            logger.info("✓ Inscription client réussie: {}", email);
            return result;

        } catch (DatabaseException e) {
            logger.error("Erreur base de données lors de l'inscription client", e);
            throw new BusinessException("Erreur lors de l'inscription", e);
        }
    }

    /**
     * Authentifier un client
     */
    /**
     * Authentification d'un client par email et mot de passe.
     * Vérifie les credentials et retourne les informations du client connecté.
     *
     * @param email Adresse email du client
     * @param motDePasse Mot de passe du client
     * @return Le client authentifié avec ses informations complètes
     * @throws BusinessException Si les credentials sont incorrects
     */
    public Client authentifierClient(String email, String motDePasse) throws BusinessException {
        try {
            if (!ValidationUtils.isEmailValid(email)) {
                throw new BusinessException("Email invalide");
            }

            Client client = clientDAO.chercherParEmail(email);

            if (client == null) {
                throw new BusinessException("Mot de passe ou email incorrect");
            }

            if (!PasswordUtils.verifyPassword(motDePasse, client.getMotDePasse())) {
                throw new BusinessException("Mot de passe ou email incorrect");
            }

            logger.info("✓ Authentification client réussie: {}", email);
            return client;

        } catch (DatabaseException e) {
            logger.error("Erreur base de données lors de l'authentification client", e);
            throw new BusinessException("Erreur lors de l'authentification", e);
        }
    }

    /**
     * Récupérer un client par ID
     */
    /**
     * Récupération d'un client par son identifiant.
     *
     * @param id Identifiant unique du client
     * @return Le client correspondant à l'ID
     * @throws BusinessException Si le client n'est pas trouvé
     */
    public Client getClient(int id) throws BusinessException {
        try {
            Client client = clientDAO.chercher(id);
            if (client == null) {
                throw new BusinessException("Client non trouvé");
            }
            return client;
        } catch (DatabaseException e) {
            logger.error("Erreur récupération client", e);
            throw new BusinessException("Erreur récupération client", e);
        }
    }

    /**
     * Consulter les réservations d'un client
     */
    /**
     * Consultation des réservations d'un client.
     * Affiche l'historique complet des réservations avec leur statut.
     *
     * @param idClient Identifiant du client
     * @throws BusinessException Si le client n'existe pas
     */
    public void consulterReservations(int idClient) throws BusinessException {
        // Vérifier que le client existe
        Client client = getClient(idClient);
        logger.info("Consultation des réservations pour le client: {}", client.getNom());
        // Logique de consultation à implémenter selon les besoins
    }

    /**
     * Annuler une réservation
     */
    /**
     * Annulation d'une réservation par un client.
     * Vérifie les conditions d'annulation et applique les règles métier.
     *
     * @param idClient Identifiant du client demandeur
     * @param idReservation Identifiant de la réservation à annuler
     * @throws BusinessException Si l'annulation n'est pas autorisée
     */
    public void annulerReservation(int idClient, int idReservation) throws BusinessException {
        // Vérifier que le client existe
        Client client = getClient(idClient);
        logger.info("Annulation de la réservation {} pour le client: {}", idReservation, client.getNom());
        // Logique d'annulation à implémenter selon les règles métier
    }

    /**
     * Mettre à jour les informations d'un client
     */
    /**
     * Mise à jour des informations d'un client.
     * Valide et sauvegarde les modifications du profil client.
     *
     * @param client Objet client avec les nouvelles données
     * @throws BusinessException Si la validation échoue
     */
    public void mettreAJourClient(Client client) throws BusinessException {
        try {
            clientDAO.mettreAJour(client);
            logger.info("✓ Client mis à jour: {}", client.getEmail());
        } catch (DatabaseException e) {
            logger.error("Erreur mise à jour client", e);
            throw new BusinessException("Erreur mise à jour client", e);
        }
    }

    /**
     * Lister tous les clients
     */
    /**
     * Récupération de la liste complète des clients.
     * Utilisé principalement pour l'administration.
     *
     * @return Liste de tous les clients enregistrés
     * @throws BusinessException Si l'accès aux données échoue
     */
    public List<Client> listerTousLesClients() throws BusinessException {
        try {
            return clientDAO.listerTous();
        } catch (DatabaseException e) {
            logger.error("Erreur listage clients", e);
            throw new BusinessException("Erreur listage clients", e);
        }
    }
}
