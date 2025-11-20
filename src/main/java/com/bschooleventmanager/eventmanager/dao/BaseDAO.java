package com.bschooleventmanager.eventmanager.dao;

import com.bschooleventmanager.eventmanager.exception.DatabaseException;

import java.sql.Connection;
import java.util.List;

/**
 * Classe abstraite de base pour tous les DAO avec opérations CRUD standards.
 * 
 * <p>Fournit la structure commune et les opérations de base pour l'accès aux données,
 * ainsi qu'une méthode utilitaire pour obtenir une connexion à la base de données.</p>
 * 
 * @param <T> Le type d'entité géré par le DAO
 * @author Charbel SONON (@AsKing07) & Loïc MABOMESI (@LoicVanelMABO)
 * @version 1.0
 * @since 1.0
 */
public abstract class BaseDAO<T> {
 
    /**
     * Constructeur protégé pour empêcher l'instanciation directe.
     */
    protected BaseDAO() {
        // constructeur vide intentionnel
    }

    /**
     * Récupère une connexion valide depuis le gestionnaire de connexion singleton.
     * 
     * @return La connexion à la base de données
     * @see DatabaseConnection#getInstance()
     */
    protected Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Crée une nouvelle entité en base de données.
     * 
     * @param entity L'entité à créer
     * @return L'entité créée avec son ID généré
     * @throws DatabaseException En cas d'erreur de création
     */
    public abstract T creer(T entity) throws DatabaseException;

    /**
     * Recherche une entité par son identifiant unique.
     * 
     * @param id L'identifiant de l'entité
     * @return L'entité trouvée ou null si non trouvée
     * @throws DatabaseException En cas d'erreur de recherche
     */
    public abstract T chercher(int id) throws DatabaseException;

    /**
     * Récupère toutes les entités de ce type.
     * 
     * @return Liste de toutes les entités
     * @throws DatabaseException En cas d'erreur de récupération
     */
    public abstract List<T> listerTous() throws DatabaseException;

    /**
     * Met à jour une entité existante en base de données.
     * 
     * @param entity L'entité à mettre à jour
     * @return L'entité mise à jour
     * @throws DatabaseException En cas d'erreur de mise à jour
     */
    public abstract T mettreAJour(T entity) throws DatabaseException;

    /**
     * Supprime une entité par son identifiant.
     * 
     * @param id L'identifiant de l'entité à supprimer
     * @throws DatabaseException En cas d'erreur de suppression
     */
    public abstract void supprimer(int id) throws DatabaseException;
}