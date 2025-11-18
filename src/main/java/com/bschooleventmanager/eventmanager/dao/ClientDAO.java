package com.bschooleventmanager.eventmanager.dao;

import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import com.bschooleventmanager.eventmanager.model.Client;
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

public class ClientDAO extends BaseDAO<Client> {
    private static final Logger logger = LoggerFactory.getLogger(ClientDAO.class);

    @Override
    public Client creer(Client client) throws DatabaseException {
        String query = "INSERT INTO Utilisateurs (nom, email, mot_de_passe, type_utilisateur) VALUES (?, ?, ?, ?)";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getEmail());
            pstmt.setString(3, client.getMotDePasse());
            pstmt.setString(4, TypeUtilisateur.CLIENT.name());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        client.setIdUtilisateur(rs.getInt(1));
                        logger.info("✓ Client créé: {}", client.getEmail());
                        return client;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur création client", e);
            throw new DatabaseException("Erreur création client", e);
        }

        return null;
    }

    @Override
    public Client chercher(int id) throws DatabaseException {
        String query = "SELECT id_utilisateur, nom, email, mot_de_passe, type_utilisateur, date_creation " +
                       "FROM Utilisateurs WHERE id_utilisateur = ? AND type_utilisateur = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            pstmt.setString(2, TypeUtilisateur.CLIENT.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToClient(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur recherche client", e);
            throw new DatabaseException("Erreur recherche client", e);
        }

        return null;
    }

    public Client chercherParEmail(String email) throws DatabaseException {
        String query = "SELECT id_utilisateur, nom, email, mot_de_passe, type_utilisateur, date_creation " +
                       "FROM Utilisateurs WHERE email = ? AND type_utilisateur = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, TypeUtilisateur.CLIENT.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToClient(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur recherche client par email", e);
            throw new DatabaseException("Erreur recherche client par email", e);
        }

        return null;
    }

    @Override
    public List<Client> listerTous() throws DatabaseException {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT id_utilisateur, nom, email, mot_de_passe, type_utilisateur, date_creation " +
                       "FROM Utilisateurs WHERE type_utilisateur = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, TypeUtilisateur.CLIENT.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    clients.add(mapRowToClient(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur listage clients", e);
            throw new DatabaseException("Erreur listage clients", e);
        }
        

        return clients;
    }

    @Override
    public Client mettreAJour(Client client) throws DatabaseException {
        String query = "UPDATE Utilisateurs SET nom = ?, email = ? WHERE id_utilisateur = ? AND type_utilisateur = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getEmail());
            pstmt.setInt(3, client.getIdUtilisateur());
            pstmt.setString(4, TypeUtilisateur.CLIENT.name());

            pstmt.executeUpdate();
            logger.info("✓ Client mis à jour: {}", client.getIdUtilisateur());
        } catch (SQLException e) {
            logger.error("Erreur mise à jour client", e);
            throw new DatabaseException("Erreur mise à jour client", e);
        }
        return client;
    }

    @Override
    public void supprimer(int id) throws DatabaseException {
        String query = "DELETE FROM Utilisateurs WHERE id_utilisateur = ? AND type_utilisateur = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            pstmt.setString(2, TypeUtilisateur.CLIENT.name());
            pstmt.executeUpdate();
            logger.info("✓ Client supprimé: {}", id);
        } catch (SQLException e) {
            logger.error("Erreur suppression client", e);
            throw new DatabaseException("Erreur suppression client", e);
        }

    }

    private Client mapRowToClient(ResultSet rs) throws SQLException {
        Client client = new Client();
        client.setIdUtilisateur(rs.getInt("id_utilisateur"));
        client.setNom(rs.getString("nom"));
        client.setEmail(rs.getString("email"));
        client.setMotDePasse(rs.getString("mot_de_passe"));
        client.setTypeUtilisateur(TypeUtilisateur.valueOf(rs.getString("type_utilisateur")));
        client.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        return client;
    }
}
