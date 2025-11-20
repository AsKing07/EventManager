package com.bschooleventmanager.eventmanager.service;

import com.bschooleventmanager.eventmanager.dao.UtilisateurDAO;
import com.bschooleventmanager.eventmanager.model.Utilisateur;
import com.bschooleventmanager.eventmanager.model.Client;
import com.bschooleventmanager.eventmanager.model.Organisateur;
import com.bschooleventmanager.eventmanager.model.enums.TypeUtilisateur;
import com.bschooleventmanager.eventmanager.exception.BusinessException;
import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import com.bschooleventmanager.eventmanager.util.PasswordUtils;
import com.bschooleventmanager.eventmanager.util.SessionManager;
import com.bschooleventmanager.eventmanager.util.ValidationUtils;
import com.mysql.cj.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service métier pour la gestion complète des utilisateurs (Clients et Organisateurs).
 * 
 * <p>Fournit les opérations d'inscription, authentification, mise à jour et gestion
 * des mots de passe avec validation complète et gestion de session.</p>
 * 
 * @author Équipe EventManager
 * @version 1.0
 * @since 1.0
 */
public class UtilisateurService {
    private static final Logger logger = LoggerFactory.getLogger(UtilisateurService.class);
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    /**
     * Inscrit un nouvel utilisateur avec validation complète.
     * 
     * @param nom Le nom complet de l'utilisateur
     * @param email L'adresse email unique
     * @param motDePasse Le mot de passe en clair (sera hashé)
     * @param type Le type d'utilisateur (CLIENT ou ORGANISATEUR)
     * @return L'utilisateur créé avec son ID généré
     * @throws BusinessException Si validation échoue ou email existe déjà
     */
    public Utilisateur inscrire(String nom, String email, String motDePasse, String type)
            throws BusinessException {
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

            // Vérifier si email existe
            if (utilisateurDAO.emailExiste(email)) {
                throw new BusinessException("Cet email est déjà utilisé");
            }

            // Créer l'utilisateur
            TypeUtilisateur typeUtilisateur = TypeUtilisateur.valueOf(type);
            Utilisateur user;
            
            // Création de l'instance appropriée selon le type
            if (typeUtilisateur == TypeUtilisateur.CLIENT) {
                user = new Client(nom, email);
            } else {
                user = new Organisateur(nom, email);
            }
            
            user.setMotDePasse(PasswordUtils.hashPassword(motDePasse));

            Utilisateur result = utilisateurDAO.creer(user);
            logger.info("✓ Inscription réussie: {}", email);
            return result;

        } catch (DatabaseException e) {
            logger.error("Erreur base de données lors de l'inscription", e);
            throw new BusinessException("Erreur lors de l'inscription", e);
        }
    }

    /**
     * Authentifie un utilisateur avec email et mot de passe.
     * 
     * @param email L'adresse email de connexion
     * @param motDePasse Le mot de passe en clair
     * @return L'utilisateur authentifié
     * @throws BusinessException Si authentification échoue
     */
    public Utilisateur authentifier(String email, String motDePasse)
            throws BusinessException {
        try {
            if (!ValidationUtils.isEmailValid(email)) {
                throw new BusinessException("Email invalide");
            }

            Utilisateur user = utilisateurDAO.chercherParEmail(email);

            if (user == null) {
                throw new BusinessException("Mot de passe ou email incorrect");
            }

            if (!PasswordUtils.verifyPassword(motDePasse, user.getMotDePasse())) {
                throw new BusinessException("Mot de passe ou email incorrect");
            }

            logger.info("✓ Authentification réussie: {}", email);
            return user;

        } catch (DatabaseException e) {
            logger.error("Erreur base de données lors de l'authentification", e);
            throw new BusinessException("Erreur lors de l'authentification", e);
        }
    }

    /**
     * Récupère un utilisateur par son identifiant.
     * 
     * @param id L'identifiant unique de l'utilisateur
     * @return L'utilisateur trouvé
     * @throws BusinessException Si utilisateur non trouvé
     */
    public Utilisateur getUtilisateur(int id) throws BusinessException {
        try {
            Utilisateur user = utilisateurDAO.chercher(id);
            if (user == null) {
                throw new BusinessException("Utilisateur non trouvé");
            }
            return user;
        } catch (DatabaseException e) {
            logger.error("Erreur récupération utilisateur", e);
            throw new BusinessException("Erreur récupération utilisateur", e);
        }
    }

    /**
     * Met à jour les informations d'un utilisateur et synchronise la session.
     * 
     * @param utilisateur L'utilisateur avec les nouvelles données
     * @return true si mise à jour réussie, false sinon
     */
    public boolean updateUtilisateur(Utilisateur utilisateur) {
        try {
            // Validation des données
            if (!ValidationUtils.isValidName(utilisateur.getNom())) {
                logger.error("Nom invalide lors de la mise à jour");
                return false;
            }

            if (!ValidationUtils.isValidEmail(utilisateur.getEmail())) {
                logger.error("Email invalide lors de la mise à jour");
                return false;
            }

            // Mettre à jour dans la base
           utilisateurDAO.mettreAJour(utilisateur);
           SessionManager.setUtilisateurConnecte(utilisateur);
           
                logger.info("✓ Utilisateur mis à jour: {}", utilisateur.getEmail());
            return true;
       
        } catch (DatabaseException e) {
              logger.error("Échec de la mise à jour de l'utilisateur: {}", utilisateur.getEmail());
            logger.error("Erreur base de données lors de la mise à jour", e);
            return false;
        }
    }

    /**
     * Change le mot de passe d'un utilisateur avec validation sécurisée.
     * 
     * @param userId L'identifiant de l'utilisateur
     * @param nouveauMotDePasse Le nouveau mot de passe en clair
     * @return true si changement réussi, false sinon
     */
    public boolean changePassword(int userId, String nouveauMotDePasse) {
        try {
            // Validation du mot de passe
            if (nouveauMotDePasse == null || nouveauMotDePasse.length() < 8) {
                logger.error("Mot de passe trop court lors du changement");
                return false;
            }

            // Hasher le nouveau mot de passe
            String hashedPassword = PasswordUtils.hashPassword(nouveauMotDePasse);
            
            // Mettre à jour en base
            boolean success = utilisateurDAO.changerMotDePasse(userId, hashedPassword);
            
            if (success) {
                logger.info("✓ Mot de passe changé pour l'utilisateur ID: {}", userId);
            } else {
                logger.error("Échec du changement de mot de passe pour l'utilisateur ID: {}", userId);
            }
            
            return success;

        } catch (Exception e) {
            logger.error("Erreur lors du changement de mot de passe", e);
            return false;
        }
    }

    /**
     * Vérifie la validité d'un mot de passe pour un utilisateur.
     * 
     * @param email L'adresse email de l'utilisateur
     * @param motDePasse Le mot de passe à vérifier
     * @return true si mot de passe valide, false sinon
     */
    public boolean verifyPassword(String email, String motDePasse) {
        try {
            Utilisateur user = utilisateurDAO.chercherParEmail(email);
            if (user == null) {
                return false;
            }

            return PasswordUtils.verifyPassword(motDePasse, user.getMotDePasse());

        } catch (DatabaseException e) {
            logger.error("Erreur lors de la vérification du mot de passe", e);
            return false;
        }
    }
}