package com.bschooleventmanager.eventmanager.dao;


import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import com.bschooleventmanager.eventmanager.model.Utilisateur;
import com.bschooleventmanager.eventmanager.model.Client;
import com.bschooleventmanager.eventmanager.model.Organisateur;
import com.bschooleventmanager.eventmanager.model.enums.TypeUtilisateur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO extends BaseDAO<Utilisateur> {
    private static final Logger logger = LoggerFactory.getLogger(UtilisateurDAO.class);

    @Override
    public Utilisateur creer(Utilisateur user) throws DatabaseException {
        String query = "INSERT INTO utilisateurs (nom, email, mot_de_passe, type_utilisateur) VALUES (?, ?, ?, ?)";
Connection connection = getConnection();
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getMotDePasse());
            pstmt.setString(4, user.getTypeUtilisateur().name());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setIdUtilisateur(rs.getInt(1));
                        return user;
                    }
                }
            }
            connection.close();
        } catch (SQLException e) {
            logger.error("Erreur création utilisateur", e);
            throw new DatabaseException("Erreur création utilisateur", e);
        }

        return null;
    }

    @Override
    public Utilisateur chercher(int id) throws DatabaseException {
        String query = "SELECT * FROM Utilisateurs WHERE id_utilisateur = ?";
Connection connection = getConnection();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUtilisateur(rs);
                }
            }
            connection.close();
        } catch (SQLException e) {
            logger.error("Erreur recherche utilisateur", e);
            throw new DatabaseException("Erreur recherche utilisateur", e);
        }

        return null;
    }

    public Utilisateur chercherParEmail(String email) throws DatabaseException {
        String query = "SELECT * FROM utilisateurs WHERE email = ?";
Connection connection = getConnection();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUtilisateur(rs);
                }
            }
            connection.close();
        } catch (SQLException e) {
            logger.error("Erreur recherche utilisateur par email", e);
            throw new DatabaseException("Erreur recherche utilisateur par email", e);
        }

        return null;
    }

    public boolean emailExiste(String email) throws DatabaseException {
        String query = "SELECT COUNT(*) FROM utilisateurs WHERE email = ?";
Connection connection = getConnection();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            connection.close();
        } catch (SQLException e) {
            logger.error("Erreur vérification existence email", e);
            throw new DatabaseException("Erreur vérification existence email", e);
        }

        return false;
    }

    @Override
    public List<Utilisateur> listerTous() throws DatabaseException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String query = "SELECT * FROM utilisateurs";
Connection connection = getConnection();
       try (Statement stmt = connection.createStatement();
           ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                utilisateurs.add(mapRowToUtilisateur(rs));
            }
            connection.close();
        } catch (SQLException e) {
            logger.error("Erreur listage utilisateurs", e);
            throw new DatabaseException("Erreur listage utilisateurs", e);
        }

        return utilisateurs;
    }

        /**
     * Modifie les données d'un utilisateur
     * @param user L'utilisateur à modifier
     * @return true si la modification a réussi, false sinon
     * @throws DatabaseException En cas d'erreur de base de données
     */
    @Override
    public Utilisateur mettreAJour(Utilisateur user) throws DatabaseException {
 String query = "UPDATE utilisateurs SET nom = ?, email = ? WHERE id_utilisateur = ?";
        Connection connection = getConnection();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getEmail());
            pstmt.setInt(3, user.getIdUtilisateur());

            int rowsAffected = pstmt.executeUpdate();
            connection.close();
            
        } catch (SQLException e) {
            logger.error("Erreur modification utilisateur", e);
            throw new DatabaseException("Erreur modification utilisateur", e);
        }
        return user;
    }

    @Override
    public void supprimer(int id) throws DatabaseException {
        String query = "DELETE FROM utilisateurs WHERE id_utilisateur = ?";
Connection connection = getConnection();
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            connection.close();
          
        } catch (SQLException e) {
            logger.error("Erreur suppression utilisateur", e);
            throw new DatabaseException("Erreur suppression utilisateur", e);
        }
    }



    /**
     * Change le mot de passe d'un utilisateur
     * @param userId L'ID de l'utilisateur
     * @param hashedPassword Le nouveau mot de passe hashé
     * @return true si le changement a réussi, false sinon
     * @throws DatabaseException En cas d'erreur de base de données
     */
    public boolean changerMotDePasse(int userId, String hashedPassword) throws DatabaseException {
        String query = "UPDATE utilisateurs SET mot_de_passe = ? WHERE id_utilisateur = ?";
        Connection connection = getConnection();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            connection.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Erreur changement mot de passe", e);
            throw new DatabaseException("Erreur changement mot de passe", e);
        }
    }

    private Utilisateur mapRowToUtilisateur(ResultSet rs) throws SQLException {
        TypeUtilisateur type = TypeUtilisateur.valueOf(rs.getString("type_utilisateur"));
        Utilisateur user;
        
        // Création de l'instance appropriée selon le type
        if (type == TypeUtilisateur.CLIENT) {
            user = new Client();
        } else {
            user = new Organisateur();
        }
        
        user.setIdUtilisateur(rs.getInt("id_utilisateur"));
        user.setNom(rs.getString("nom"));
        user.setEmail(rs.getString("email"));
        user.setMotDePasse(rs.getString("mot_de_passe"));
        user.setTypeUtilisateur(type);
        user.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        return user;
    }
}