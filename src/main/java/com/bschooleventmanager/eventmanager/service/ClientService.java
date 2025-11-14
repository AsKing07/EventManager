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

public class ClientService {
    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    private final ClientDAO clientDAO = new ClientDAO();

    /**
     * Inscrire un nouveau client
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
    public Client authentifierClient(String email, String motDePasse) throws BusinessException {
        try {
            if (!ValidationUtils.isEmailValid(email)) {
                throw new BusinessException("Email invalide");
            }

            Client client = clientDAO.chercherParEmail(email);

            if (client == null) {
                throw new BusinessException("Client non trouvé");
            }

            if (!PasswordUtils.verifyPassword(motDePasse, client.getMotDePasse())) {
                throw new BusinessException("Mot de passe incorrect");
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
    public void consulterReservations(int idClient) throws BusinessException {
        // Vérifier que le client existe
        Client client = getClient(idClient);
        logger.info("Consultation des réservations pour le client: {}", client.getNom());
        // Logique de consultation à implémenter selon les besoins
    }

    /**
     * Annuler une réservation
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
    public List<Client> listerTousLesClients() throws BusinessException {
        try {
            return clientDAO.listerTous();
        } catch (DatabaseException e) {
            logger.error("Erreur listage clients", e);
            throw new BusinessException("Erreur listage clients", e);
        }
    }
}
