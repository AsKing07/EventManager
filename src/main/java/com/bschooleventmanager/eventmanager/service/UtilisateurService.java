package com.bschooleventmanager.eventmanager.service;

import com.bschooleventmanager.eventmanager.dao.UtilisateurDAO;
import com.bschooleventmanager.eventmanager.model.Utilisateur;
import com.bschooleventmanager.eventmanager.model.Client;
import com.bschooleventmanager.eventmanager.model.Organisateur;
import com.bschooleventmanager.eventmanager.model.enums.TypeUtilisateur;
import com.bschooleventmanager.eventmanager.exception.BusinessException;
import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import com.bschooleventmanager.eventmanager.util.PasswordUtils;
import com.bschooleventmanager.eventmanager.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilisateurService {
    private static final Logger logger = LoggerFactory.getLogger(UtilisateurService.class);
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    /**
     * Inscrire un nouvel utilisateur
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
     * Authentifier un utilisateur
     */
    public Utilisateur authentifier(String email, String motDePasse)
            throws BusinessException {
        try {
            if (!ValidationUtils.isEmailValid(email)) {
                throw new BusinessException("Email invalide");
            }

            Utilisateur user = utilisateurDAO.chercherParEmail(email);

            if (user == null) {
                throw new BusinessException("Utilisateur non trouvé");
            }

            if (!PasswordUtils.verifyPassword(motDePasse, user.getMotDePasse())) {
                throw new BusinessException("Mot de passe incorrect");
            }

            logger.info("✓ Authentification réussie: {}", email);
            return user;

        } catch (DatabaseException e) {
            logger.error("Erreur base de données lors de l'authentification", e);
            throw new BusinessException("Erreur lors de l'authentification", e);
        }
    }

    /**
     * Récupérer un utilisateur par ID
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
}