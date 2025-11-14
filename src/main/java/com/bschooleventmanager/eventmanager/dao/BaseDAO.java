package com.bschooleventmanager.eventmanager.dao;


import java.sql.Connection;
import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import java.util.List;

public abstract class BaseDAO<T> {
 
    protected BaseDAO() {
        // constructeur vide intentionnel
    }

    /**
     * Récupère une connexion valide depuis le gestionnaire de connexion.
     * Les DAO doivent appeler cette méthode avant d'effectuer des opérations SQL.
     */
    protected Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Créer une entité
     */
    public abstract T creer(T entity) throws DatabaseException;

    /**
     * Trouver par ID
     */
    public abstract T chercher(int id) throws DatabaseException;

    /**
     * Trouver tous
     */
    public abstract List<T> listerTous() throws DatabaseException;

    /**
     * Mettre à jour
     */
    public abstract void mettreAJour(T entity) throws DatabaseException;

    /**
     * Supprimer
     */
    public abstract void supprimer(int id) throws DatabaseException;
}