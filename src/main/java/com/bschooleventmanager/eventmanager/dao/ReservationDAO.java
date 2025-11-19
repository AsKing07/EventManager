package com.bschooleventmanager.eventmanager.dao;

import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import com.bschooleventmanager.eventmanager.model.Reservation;
import com.bschooleventmanager.eventmanager.model.enums.StatutReservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO extends BaseDAO<Reservation> {
    private static final Logger logger = LoggerFactory.getLogger(ReservationDAO.class);

    @Override
    public Reservation creer(Reservation reservation) throws DatabaseException {
        String query = "INSERT INTO reservations (client_id, id_evenement, date_reservation, statut, total_paye) " +
                       "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, reservation.getClientId());
            pstmt.setInt(2, reservation.getIdEvenement());
            pstmt.setString(3, reservation.getDateReservation());
            pstmt.setString(4, reservation.getStatut().name());
            pstmt.setDouble(5, reservation.getTotalPaye());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        reservation.setIdReservation(rs.getInt(1));
                        logger.info("✓ Réservation créée: ID {}", reservation.getIdReservation());
                        return reservation;
                    }
                }
            }
            throw new DatabaseException("Erreur lors de la création de la réservation.");
        } catch (SQLException e) {
            logger.error("Erreur création réservation", e);
            throw new DatabaseException("Erreur création réservation", e);
        }
    }

    @Override
    public Reservation chercher(int id) throws DatabaseException {
        String query = "SELECT id_reservation, client_id, id_evenement, date_reservation, statut, " +
                       "total_paye, date_annulation FROM reservations WHERE id_reservation = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToReservation(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            logger.error("Erreur recherche réservation", e);
            throw new DatabaseException("Erreur recherche réservation", e);
        }
    }

    @Override
    public List<Reservation> listerTous() throws DatabaseException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT id_reservation, client_id, id_evenement, date_reservation, statut, " +
                       "total_paye, date_annulation FROM reservations ORDER BY date_reservation DESC";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                reservations.add(mapRowToReservation(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur listage réservations", e);
            throw new DatabaseException("Erreur listage réservations", e);
        }
        return reservations;
    }

    @Override
    public Reservation mettreAJour(Reservation reservation) throws DatabaseException {
        String query = "UPDATE reservations SET client_id = ?, id_evenement = ?, date_reservation = ?, " +
                       "statut = ?, total_paye = ?, date_annulation = ? WHERE id_reservation = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, reservation.getClientId());
            pstmt.setInt(2, reservation.getIdEvenement());
            pstmt.setString(3, reservation.getDateReservation());
            pstmt.setString(4, reservation.getStatut().name());
            pstmt.setDouble(5, reservation.getTotalPaye());
            pstmt.setTimestamp(6, reservation.getDateAnnulation() != null ? 
                    Timestamp.valueOf(reservation.getDateAnnulation()) : null);
            pstmt.setInt(7, reservation.getIdReservation());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Aucune réservation trouvée avec l'ID: " + reservation.getIdReservation());
            }
            logger.info("✓ Réservation mise à jour: ID {}", reservation.getIdReservation());
        } catch (SQLException e) {
            logger.error("Erreur mise à jour réservation", e);
            throw new DatabaseException("Erreur mise à jour réservation", e);
        }
        return reservation;
    }

    @Override
    public void mettreAJourC(Reservation entity) throws DatabaseException {

    }

    @Override
    public void supprimer(int id) throws DatabaseException {
        String query = "DELETE FROM reservations WHERE id_reservation = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Aucune réservation trouvée avec l'ID: " + id);
            }
            logger.info("✓ Réservation supprimée: ID {}", id);
        } catch (SQLException e) {
            logger.error("Erreur suppression réservation", e);
            throw new DatabaseException("Erreur suppression réservation", e);
        }
    }

    /**
     * Récupère les réservations d'un client spécifique
     */
    public List<Reservation> getReservationsParClient(int clientId) throws DatabaseException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT id_reservation, client_id, id_evenement, date_reservation, statut, " +
                       "total_paye, date_annulation FROM reservations WHERE client_id = ? ORDER BY date_reservation DESC";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, clientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapRowToReservation(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur récupération réservations client", e);
            throw new DatabaseException("Erreur récupération réservations client", e);
        }
        return reservations;
    }

    /**
     * Récupère les réservations pour un événement spécifique
     */
    public List<Reservation> getReservationsParEvenement(int eventId) throws DatabaseException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT id_reservation, client_id, id_evenement, date_reservation, statut, " +
                       "total_paye, date_annulation FROM reservations WHERE id_evenement = ? ORDER BY date_reservation DESC";
        
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, eventId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapRowToReservation(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur récupération réservations événement", e);
            throw new DatabaseException("Erreur récupération réservations événement", e);
        }
        return reservations;
    }

    /**
     * Mappe une ligne de ResultSet vers un objet Reservation
     */
    private Reservation mapRowToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setIdReservation(rs.getInt("id_reservation"));
        reservation.setClientId(rs.getInt("client_id"));
        reservation.setIdEvenement(rs.getInt("id_evenement"));
        reservation.setDateReservation(rs.getString("date_reservation"));
        
        String statutStr = rs.getString("statut");
        if (statutStr != null && !statutStr.trim().isEmpty()) {
            reservation.setStatut(StatutReservation.valueOf(statutStr));
        } else {
            reservation.setStatut(StatutReservation.EN_ATTENTE);
        }
        
        reservation.setTotalPaye(rs.getDouble("total_paye"));
        
        Timestamp dateAnnulation = rs.getTimestamp("date_annulation");
        if (dateAnnulation != null) {
            reservation.setDateAnnulation(dateAnnulation.toLocalDateTime());
        }
        
        return reservation;
    }
}
