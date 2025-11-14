package com.bschooleventmanager.eventmanager.service;

import com.bschooleventmanager.eventmanager.dao.OrganisateurDAO;
import com.bschooleventmanager.eventmanager.model.Organisateur;
import com.bschooleventmanager.eventmanager.exception.BusinessException;
import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import com.bschooleventmanager.eventmanager.util.PasswordUtils;
import com.bschooleventmanager.eventmanager.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OrganisateurService {
    private static final Logger logger = LoggerFactory.getLogger(OrganisateurService.class);
    private final OrganisateurDAO organisateurDAO = new OrganisateurDAO();

    /**
     * Inscrire un nouvel organisateur
     */
    public Organisateur inscrireOrganisateur(String nom, String email, String motDePasse) throws BusinessException {
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
            if (organisateurDAO.chercherParEmail(email) != null) {
                throw new BusinessException("Cet email est déjà utilisé par un organisateur");
            }

            // Créer l'organisateur
            Organisateur organisateur = new Organisateur(nom, email);
            organisateur.setMotDePasse(PasswordUtils.hashPassword(motDePasse));

            Organisateur result = organisateurDAO.creer(organisateur);
            logger.info("✓ Inscription organisateur réussie: {}", email);
            return result;

        } catch (DatabaseException e) {
            logger.error("Erreur base de données lors de l'inscription organisateur", e);
            throw new BusinessException("Erreur lors de l'inscription", e);
        }
    }

    /**
     * Authentifier un organisateur
     */
    public Organisateur authentifierOrganisateur(String email, String motDePasse) throws BusinessException {
        try {
            if (!ValidationUtils.isEmailValid(email)) {
                throw new BusinessException("Email invalide");
            }

            Organisateur organisateur = organisateurDAO.chercherParEmail(email);

            if (organisateur == null) {
                throw new BusinessException("Organisateur non trouvé");
            }

            if (!PasswordUtils.verifyPassword(motDePasse, organisateur.getMotDePasse())) {
                throw new BusinessException("Mot de passe incorrect");
            }

            logger.info("✓ Authentification organisateur réussie: {}", email);
            return organisateur;

        } catch (DatabaseException e) {
            logger.error("Erreur base de données lors de l'authentification organisateur", e);
            throw new BusinessException("Erreur lors de l'authentification", e);
        }
    }

    /**
     * Récupérer un organisateur par ID
     */
    public Organisateur getOrganisateur(int id) throws BusinessException {
        try {
            Organisateur organisateur = organisateurDAO.chercher(id);
            if (organisateur == null) {
                throw new BusinessException("Organisateur non trouvé");
            }
            return organisateur;
        } catch (DatabaseException e) {
            logger.error("Erreur récupération organisateur", e);
            throw new BusinessException("Erreur récupération organisateur", e);
        }
    }

    /**
     * Créer un événement
     */
    public void creerEvenement(int idOrganisateur, String nomEvenement) throws BusinessException {
        // Vérifier que l'organisateur existe
        Organisateur organisateur = getOrganisateur(idOrganisateur);
        logger.info("Création de l'événement '{}' par l'organisateur: {}", nomEvenement, organisateur.getNom());
        // Logique de création d'événement à implémenter
    }

    /**
     * Modifier un événement
     */
    public void modifierEvenement(int idOrganisateur, int idEvenement) throws BusinessException {
        // Vérifier que l'organisateur existe
        Organisateur organisateur = getOrganisateur(idOrganisateur);
        logger.info("Modification de l'événement {} par l'organisateur: {}", idEvenement, organisateur.getNom());
        // Logique de modification d'événement à implémenter
    }

    /**
     * Consulter les statistiques
     */
    public void consulterStatistiques(int idOrganisateur) throws BusinessException {
        // Vérifier que l'organisateur existe
        Organisateur organisateur = getOrganisateur(idOrganisateur);
        logger.info("Consultation des statistiques pour l'organisateur: {}", organisateur.getNom());
        // Logique de consultation des statistiques à implémenter
    }

    /**
     * Mettre à jour les informations d'un organisateur
     */
    public void mettreAJourOrganisateur(Organisateur organisateur) throws BusinessException {
        try {
            organisateurDAO.mettreAJour(organisateur);
            logger.info("✓ Organisateur mis à jour: {}", organisateur.getEmail());
        } catch (DatabaseException e) {
            logger.error("Erreur mise à jour organisateur", e);
            throw new BusinessException("Erreur mise à jour organisateur", e);
        }
    }

    /**
     * Lister tous les organisateurs
     */
    public List<Organisateur> listerTousLesOrganisateurs() throws BusinessException {
        try {
            return organisateurDAO.listerTous();
        } catch (DatabaseException e) {
            logger.error("Erreur listage organisateurs", e);
            throw new BusinessException("Erreur listage organisateurs", e);
        }
    }
}
