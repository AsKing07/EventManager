package com.bschooleventmanager.eventmanager.dao;

import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import com.bschooleventmanager.eventmanager.model.Paiement;
import com.bschooleventmanager.eventmanager.model.enums.StatutPaiement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO pour la gestion des paiements
 */
public class PaiementDAO extends BaseDAO<Paiement> {
    private static final Logger logger = LoggerFactory.getLogger(PaiementDAO.class);

    /**
     * Crée un nouveau paiement
     */
    @Override
    public Paiement creer(Paiement paiement) throws DatabaseException {
        String sql = """
            INSERT INTO paiements (id_reservation, montant, date_paiement, statut, methode_paiement, numero_transaction)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
            
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, paiement.getIdReservation());
            stmt.setBigDecimal(2, paiement.getMontant());
            stmt.setTimestamp(3, Timestamp.valueOf(paiement.getDatePaiement()));
            stmt.setString(4, paiement.getStatut().name());
            stmt.setString(5, paiement.getMethodePaiement());
            stmt.setString(6, paiement.getNumeroTransaction());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Échec de la création du paiement");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int idPaiement = generatedKeys.getInt(1);
                    paiement.setIdPaiement(idPaiement);
                    logger.info("✓ Paiement créé avec l'ID: {}", idPaiement);
                    return paiement;
                } else {
                    throw new DatabaseException("Impossible de récupérer l'ID du paiement créé");
                }
            }
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la création du paiement", e);
            throw new DatabaseException("Erreur lors de la création du paiement: " + e.getMessage());
        }
    }

    /**
     * Trouve un paiement par son ID
     */
    @Override
    public Paiement chercher(int idPaiement) throws DatabaseException {
        String sql = """
            SELECT id_paiement, id_reservation, montant, date_paiement, statut, methode_paiement, numero_transaction
            FROM paiements 
            WHERE id_paiement = ?
            """;
            
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPaiement);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPaiement(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du paiement par ID: {}", idPaiement, e);
            throw new DatabaseException("Erreur lors de la recherche du paiement: " + e.getMessage());
        }
    }

    /**
     * Liste tous les paiements
     */
    @Override
    public List<Paiement> listerTous() throws DatabaseException {
        String sql = """
            SELECT id_paiement, id_reservation, montant, date_paiement, statut, methode_paiement, numero_transaction
            FROM paiements 
            ORDER BY date_paiement DESC
            """;
            
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            List<Paiement> paiements = new ArrayList<>();
            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
            
            return paiements;
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération de tous les paiements", e);
            throw new DatabaseException("Erreur lors de la récupération des paiements: " + e.getMessage());
        }
    }

    /**
     * Met à jour un paiement
     */
    @Override
    public void mettreAJour(Paiement paiement) throws DatabaseException {
        String sql = """
            UPDATE paiements 
            SET id_reservation = ?, montant = ?, date_paiement = ?, statut = ?, methode_paiement = ?, numero_transaction = ?
            WHERE id_paiement = ?
            """;
            
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, paiement.getIdReservation());
            stmt.setBigDecimal(2, paiement.getMontant());
            stmt.setTimestamp(3, Timestamp.valueOf(paiement.getDatePaiement()));
            stmt.setString(4, paiement.getStatut().name());
            stmt.setString(5, paiement.getMethodePaiement());
            stmt.setString(6, paiement.getNumeroTransaction());
            stmt.setInt(7, paiement.getIdPaiement());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Aucun paiement trouvé avec l'ID: " + paiement.getIdPaiement());
            }
            
            logger.info("✓ Paiement {} mis à jour", paiement.getIdPaiement());
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour du paiement: {}", paiement.getIdPaiement(), e);
            throw new DatabaseException("Erreur lors de la mise à jour du paiement: " + e.getMessage());
        }
    }

    /**
     * Supprime un paiement (soft delete - change statut vers ANNULE)
     */
    @Override
    public void supprimer(int idPaiement) throws DatabaseException {
        String sql = """
            UPDATE paiements 
            SET statut = 'ANNULE'
            WHERE id_paiement = ?
            """;
            
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPaiement);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Aucun paiement trouvé avec l'ID: " + idPaiement);
            }
            
            logger.info("✓ Paiement {} supprimé (annulé)", idPaiement);
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression du paiement: {}", idPaiement, e);
            throw new DatabaseException("Erreur lors de la suppression du paiement: " + e.getMessage());
        }
    }

    /**
     * Trouve un paiement par son ID
     */
    public Optional<Paiement> trouverParId(int idPaiement) throws DatabaseException {
        String sql = """
            SELECT id_paiement, id_reservation, montant, date_paiement, statut, methode_paiement, numero_transaction
            FROM paiements 
            WHERE id_paiement = ?
            """;
            
        try (
            Connection conn = getConnection();
       
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPaiement);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Paiement paiement = mapResultSetToPaiement(rs);
                return Optional.of(paiement);
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du paiement par ID: {}", idPaiement, e);
            throw new DatabaseException("Erreur lors de la recherche du paiement: " + e.getMessage());
        }
    }

    /**
     * Trouve tous les paiements d'une réservation
     */
    public List<Paiement> trouverParReservation(int idReservation) throws DatabaseException {
        String sql = """
            SELECT id_paiement, id_reservation, montant, date_paiement, statut, methode_paiement, numero_transaction
            FROM paiements 
            WHERE id_reservation = ?
            ORDER BY date_paiement DESC
            """;
            
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idReservation);
            ResultSet rs = stmt.executeQuery();
            
            List<Paiement> paiements = new ArrayList<>();
            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
            
            return paiements;
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche des paiements pour la réservation: {}", idReservation, e);
            throw new DatabaseException("Erreur lors de la recherche des paiements: " + e.getMessage());
        }
    }

    /**
     * Met à jour le statut d'un paiement
     */
    public boolean mettreAJourStatut(int idPaiement, StatutPaiement nouveauStatut, String numeroTransaction) throws DatabaseException {
        String sql = """
            UPDATE paiements 
            SET statut = ?, numero_transaction = ?, date_paiement = ?
            WHERE id_paiement = ?
            """;
            
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nouveauStatut.name());
            stmt.setString(2, numeroTransaction);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(4, idPaiement);
            
            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;
            
            if (success) {
                logger.info("✓ Statut du paiement {} mis à jour vers: {}", idPaiement, nouveauStatut);
            }
            
            return success;
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour du paiement: {}", idPaiement, e);
            throw new DatabaseException("Erreur lors de la mise à jour du paiement: " + e.getMessage());
        }
    }

    /**
     * Trouve un paiement par numéro de transaction
     */
    public Optional<Paiement> trouverParTransaction(String numeroTransaction) throws DatabaseException {
        String sql = """
            SELECT id_paiement, id_reservation, montant, date_paiement, statut, methode_paiement, numero_transaction
            FROM paiements 
            WHERE numero_transaction = ?
            """;
            
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, numeroTransaction);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Paiement paiement = mapResultSetToPaiement(rs);
                return Optional.of(paiement);
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du paiement par transaction: {}", numeroTransaction, e);
            throw new DatabaseException("Erreur lors de la recherche du paiement: " + e.getMessage());
        }
    }

    /**
     * Calcule le montant total payé pour une réservation
     */
    // public BigDecimal calculerMontantTotalPaye(int idReservation) throws DatabaseException {
    //     String sql = """
    //         SELECT COALESCE(SUM(montant), 0) as total_paye
    //         FROM paiement 
    //         WHERE id_reservation = ? AND statut = 'REUSSI'
    //         """;           
    //     try (Connection conn = getConnection();
    //          PreparedStatement stmt = conn.prepareStatement(sql)) {            
    //         stmt.setInt(1, idReservation);
    //         ResultSet rs = stmt.executeQuery();          
    //         if (rs.next()) {
    //             return rs.getBigDecimal("total_paye");
    //         }         
    //         return BigDecimal.ZERO;          
    //     } catch (SQLException e) {
    //         logger.error("Erreur lors du calcul du montant payé pour la réservation: {}", idReservation, e);
    //         throw new DatabaseException("Erreur lors du calcul du montant payé: " + e.getMessage());
    //     }
    // }

    /**
     * Mappe un ResultSet vers un objet Paiement
     */
    private Paiement mapResultSetToPaiement(ResultSet rs) throws SQLException {
        return new Paiement(
            rs.getInt("id_paiement"),
            rs.getInt("id_reservation"),
            rs.getBigDecimal("montant"),
            rs.getTimestamp("date_paiement").toLocalDateTime(),
            StatutPaiement.valueOf(rs.getString("statut")),
            rs.getString("methode_paiement"),
            rs.getString("numero_transaction")
        );
    }
}
