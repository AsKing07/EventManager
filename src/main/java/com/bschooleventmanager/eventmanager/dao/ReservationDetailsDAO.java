package com.bschooleventmanager.eventmanager.dao;

import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import com.bschooleventmanager.eventmanager.model.ReservationDetail;
import com.bschooleventmanager.eventmanager.model.enums.CategorieTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDetailsDAO extends BaseDAO<ReservationDetail> {
    private static final Logger logger = LoggerFactory.getLogger(ReservationDetailsDAO.class);

    @Override
    public ReservationDetail creer(ReservationDetail detail) throws DatabaseException {
        String query = "INSERT INTO reservationdetails (id_reservation, categorie_place, nombre_tickets, prix_unitaire, sous_total) " +
                       "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, detail.getIdReservation());
            pstmt.setString(2, detail.getCategoriePlace().name());
            pstmt.setInt(3, detail.getNombreTickets());
            pstmt.setDouble(4, detail.getPrixUnitaire());
            pstmt.setDouble(5, detail.getSousTotal());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        detail.setIdDetail(rs.getInt(1));
                        logger.info("✓ Détail de réservation créé: ID {}", detail.getIdDetail());
                        return detail;
                    }
                }
            }
            throw new DatabaseException("Erreur lors de la création du détail de réservation.");
        } catch (SQLException e) {
            logger.error("Erreur création détail réservation", e);
            throw new DatabaseException("Erreur création détail réservation", e);
        }
    }

    @Override
    public ReservationDetail chercher(int id) throws DatabaseException {
        String query = "SELECT id_detail, id_reservation, categorie_place, nombre_tickets, prix_unitaire, sous_total " +
                       "FROM reservation_details WHERE id_detail = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToReservationDetail(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            logger.error("Erreur recherche détail réservation", e);
            throw new DatabaseException("Erreur recherche détail réservation", e);
        }
    }

    @Override
    public List<ReservationDetail> listerTous() throws DatabaseException {
        List<ReservationDetail> details = new ArrayList<>();
        String query = "SELECT id_detail, id_reservation, categorie_place, nombre_tickets, prix_unitaire, sous_total " +
                       "FROM reservation_details ORDER BY id_detail";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                details.add(mapRowToReservationDetail(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur listage détails réservation", e);
            throw new DatabaseException("Erreur listage détails réservation", e);
        }
        return details;
    }

    @Override
    public ReservationDetail mettreAJour(ReservationDetail detail) throws DatabaseException {
        String query = "UPDATE reservation_details SET id_reservation = ?, categorie_place = ?, nombre_tickets = ?, " +
                       "prix_unitaire = ?, sous_total = ? WHERE id_detail = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, detail.getIdReservation());
            pstmt.setString(2, detail.getCategoriePlace().name());
            pstmt.setInt(3, detail.getNombreTickets());
            pstmt.setDouble(4, detail.getPrixUnitaire());
            pstmt.setDouble(5, detail.getSousTotal());
            pstmt.setInt(6, detail.getIdDetail());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Aucun détail de réservation trouvé avec l'ID: " + detail.getIdDetail());
            }
            logger.info("✓ Détail de réservation mis à jour: ID {}", detail.getIdDetail());
        } catch (SQLException e) {
            logger.error("Erreur mise à jour détail réservation", e);
            throw new DatabaseException("Erreur mise à jour détail réservation", e);
        }
        return detail;
    }

    @Override
    public void mettreAJourC(ReservationDetail entity) throws DatabaseException {

    }

    @Override
    public void supprimer(int id) throws DatabaseException {
        String query = "DELETE FROM reservation_details WHERE id_detail = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Aucun détail de réservation trouvé avec l'ID: " + id);
            }
            logger.info("✓ Détail de réservation supprimé: ID {}", id);
        } catch (SQLException e) {
            logger.error("Erreur suppression détail réservation", e);
            throw new DatabaseException("Erreur suppression détail réservation", e);
        }
    }

    /**
     * Récupère tous les détails d'une réservation spécifique
     */
    public List<ReservationDetail> getDetailsParReservation(int reservationId) throws DatabaseException {
        List<ReservationDetail> details = new ArrayList<>();
        String query = "SELECT id_detail, id_reservation, categorie_place, nombre_tickets, prix_unitaire, sous_total " +
                       "FROM reservation_details WHERE id_reservation = ? ORDER BY categorie_place";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, reservationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    details.add(mapRowToReservationDetail(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur récupération détails par réservation", e);
            throw new DatabaseException("Erreur récupération détails par réservation", e);
        }
        return details;
    }

    /**
     * Supprime tous les détails d'une réservation
     */
    public void supprimerParReservation(int reservationId) throws DatabaseException {
        String query = "DELETE FROM reservation_details WHERE id_reservation = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, reservationId);
            int rowsAffected = pstmt.executeUpdate();
            logger.info("✓ {} détails de réservation supprimés pour la réservation: {}", rowsAffected, reservationId);
        } catch (SQLException e) {
            logger.error("Erreur suppression détails par réservation", e);
            throw new DatabaseException("Erreur suppression détails par réservation", e);
        }
    }

    /**
     * Mappe une ligne de ResultSet vers un objet ReservationDetail
     */
    private ReservationDetail mapRowToReservationDetail(ResultSet rs) throws SQLException {
        ReservationDetail detail = new ReservationDetail();
        detail.setIdDetail(rs.getInt("id_detail"));
        detail.setIdReservation(rs.getInt("id_reservation"));
        
        String categorieStr = rs.getString("categorie_place");
        if (categorieStr != null && !categorieStr.trim().isEmpty()) {
            detail.setCategoriePlace(CategorieTicket.valueOf(categorieStr));
        } else {
            detail.setCategoriePlace(CategorieTicket.STANDARD);
        }
        
        detail.setNombreTickets(rs.getInt("nombre_tickets"));
        detail.setPrixUnitaire(rs.getDouble("prix_unitaire"));
        detail.setSousTotal(rs.getDouble("sous_total"));
        
        return detail;
    }
}
