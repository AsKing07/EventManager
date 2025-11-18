package com.bschooleventmanager.eventmanager.dao;

import com.bschooleventmanager.eventmanager.exception.DatabaseException;
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

public class OrganisateurDAO extends BaseDAO<Organisateur> {
    private static final Logger logger = LoggerFactory.getLogger(OrganisateurDAO.class);

    @Override
    public Organisateur creer(Organisateur organisateur) throws DatabaseException {
        String query = "INSERT INTO Utilisateurs (nom, email, mot_de_passe, type_utilisateur) VALUES (?, ?, ?, ?)";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, organisateur.getNom());
            pstmt.setString(2, organisateur.getEmail());
            pstmt.setString(3, organisateur.getMotDePasse());
            pstmt.setString(4, TypeUtilisateur.ORGANISATEUR.name());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        organisateur.setIdUtilisateur(rs.getInt(1));
                        logger.info("✓ Organisateur créé: {}", organisateur.getEmail());
                        return organisateur;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur création organisateur", e);
            throw new DatabaseException("Erreur création organisateur", e);
        }

        return null;
    }

    @Override
    public Organisateur chercher(int id) throws DatabaseException {
        String query = "SELECT id_utilisateur, nom, email, mot_de_passe, type_utilisateur, date_creation " +
                       "FROM Utilisateurs WHERE id_utilisateur = ? AND type_utilisateur = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            pstmt.setString(2, TypeUtilisateur.ORGANISATEUR.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToOrganisateur(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur recherche organisateur", e);
            throw new DatabaseException("Erreur recherche organisateur", e);
        }

        return null;
    }

    public Organisateur chercherParEmail(String email) throws DatabaseException {
        String query = "SELECT id_utilisateur, nom, email, mot_de_passe, type_utilisateur, date_creation " +
                       "FROM Utilisateurs WHERE email = ? AND type_utilisateur = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, TypeUtilisateur.ORGANISATEUR.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToOrganisateur(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur recherche organisateur par email", e);
            throw new DatabaseException("Erreur recherche organisateur par email", e);
        }

        return null;
    }

    @Override
    public List<Organisateur> listerTous() throws DatabaseException {
        List<Organisateur> organisateurs = new ArrayList<>();
        String query = "SELECT id_utilisateur, nom, email, mot_de_passe, type_utilisateur, date_creation " +
                       "FROM Utilisateurs WHERE type_utilisateur = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, TypeUtilisateur.ORGANISATEUR.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    organisateurs.add(mapRowToOrganisateur(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur listage organisateurs", e);
            throw new DatabaseException("Erreur listage organisateurs", e);
        }

        return organisateurs;
    }

    @Override
    public Organisateur mettreAJour(Organisateur organisateur) throws DatabaseException {
        String query = "UPDATE Utilisateurs SET nom = ?, email = ? WHERE id_utilisateur = ? AND type_utilisateur = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, organisateur.getNom());
            pstmt.setString(2, organisateur.getEmail());
            pstmt.setInt(3, organisateur.getIdUtilisateur());
            pstmt.setString(4, TypeUtilisateur.ORGANISATEUR.name());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("✓ Organisateur mis à jour: {}", organisateur.getIdUtilisateur());
                return organisateur;
            }
        } catch (SQLException e) {
            logger.error("Erreur mise à jour organisateur", e);
            throw new DatabaseException("Erreur mise à jour organisateur", e);
        }
        return null;
    }

    @Override
    public void supprimer(int id) throws DatabaseException {
        String query = "DELETE FROM Utilisateurs WHERE id_utilisateur = ? AND type_utilisateur = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            pstmt.setString(2, TypeUtilisateur.ORGANISATEUR.name());
            pstmt.executeUpdate();
            logger.info("✓ Organisateur supprimé: {}", id);
        } catch (SQLException e) {
            logger.error("Erreur suppression organisateur", e);
            throw new DatabaseException("Erreur suppression organisateur", e);
        }
    }

    private Organisateur mapRowToOrganisateur(ResultSet rs) throws SQLException {
        Organisateur organisateur = new Organisateur();
        organisateur.setIdUtilisateur(rs.getInt("id_utilisateur"));
        organisateur.setNom(rs.getString("nom"));
        organisateur.setEmail(rs.getString("email"));
        organisateur.setMotDePasse(rs.getString("mot_de_passe"));
        organisateur.setTypeUtilisateur(TypeUtilisateur.valueOf(rs.getString("type_utilisateur")));
        organisateur.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        return organisateur;
    }
}
