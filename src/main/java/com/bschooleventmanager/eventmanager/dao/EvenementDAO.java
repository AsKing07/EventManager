package com.bschooleventmanager.eventmanager.dao;

import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import com.bschooleventmanager.eventmanager.model.Evenement;
import com.bschooleventmanager.eventmanager.model.Concert;
import com.bschooleventmanager.eventmanager.model.Conference;
import com.bschooleventmanager.eventmanager.model.Spectacle;
import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;
import com.bschooleventmanager.eventmanager.model.enums.StatutEvenement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvenementDAO extends BaseDAO<Evenement> {
    private static final Logger logger = LoggerFactory.getLogger(EvenementDAO.class);

    @Override
    public Evenement creer(Evenement evenement) throws DatabaseException {
        String query = "INSERT INTO Evenements (organisateur_id, nom, date_evenement, lieu, type_evenement, " +
                       "description, places_standard_disponibles, places_vip_disponibles, " +
                       "places_premium_disponibles, prix_standard, prix_vip, prix_premium, statut) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, evenement.getOrganisateurId());
            pstmt.setString(2, evenement.getNom());
            pstmt.setTimestamp(3, Timestamp.valueOf(evenement.getDateEvenement()));
            pstmt.setString(4, evenement.getLieu());
            pstmt.setString(5, evenement.getTypeEvenement().name());
            pstmt.setString(6, evenement.getDescription());
            pstmt.setInt(7, evenement.getPlacesStandardDisponibles());
            pstmt.setInt(8, evenement.getPlacesVipDisponibles());
            pstmt.setInt(9, evenement.getPlacesPremiumDisponibles());
            pstmt.setBigDecimal(10, evenement.getPrixStandard());
            pstmt.setBigDecimal(11, evenement.getPrixVip());
            pstmt.setBigDecimal(12, evenement.getPrixPremium());
            pstmt.setString(13, evenement.getStatut().name());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        evenement.setIdEvenement(rs.getInt(1));
                        logger.info("✓ Événement créé: {}", evenement.getNom());
                        return evenement;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur création événement", e);
            throw new DatabaseException("Erreur création événement", e);
        }

        return null;
    }

    @Override
    public Evenement chercher(int id) throws DatabaseException {
        String query = "SELECT id_evenement, organisateur_id, nom, date_evenement, lieu, type_evenement, " +
                       "description, places_standard_disponibles, places_vip_disponibles, " +
                       "places_premium_disponibles, prix_standard, prix_vip, prix_premium, " +
                       "date_creation, statut FROM Evenements WHERE id_evenement = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEvenement(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur recherche événement", e);
            throw new DatabaseException("Erreur recherche événement", e);
        }

        return null;
    }

    public List<Evenement> chercherParOrganisateur(int organisateurId) throws DatabaseException {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT id_evenement, organisateur_id, nom, date_evenement, lieu, type_evenement, " +
                       "description, places_standard_disponibles, places_vip_disponibles, " +
                       "places_premium_disponibles, prix_standard, prix_vip, prix_premium, " +
                       "date_creation, statut FROM Evenements WHERE organisateur_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, organisateurId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    evenements.add(mapRowToEvenement(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur recherche événements par organisateur", e);
            throw new DatabaseException("Erreur recherche événements par organisateur", e);
        }

        return evenements;
    }

    public List<Evenement> chercherParType(TypeEvenement type) throws DatabaseException {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT id_evenement, organisateur_id, nom, date_evenement, lieu, type_evenement, " +
                       "description, places_standard_disponibles, places_vip_disponibles, " +
                       "places_premium_disponibles, prix_standard, prix_vip, prix_premium, " +
                       "date_creation, statut FROM Evenements WHERE type_evenement = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, type.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    evenements.add(mapRowToEvenement(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur recherche événements par type", e);
            throw new DatabaseException("Erreur recherche événements par type", e);
        }

        return evenements;
    }

    @Override
    public List<Evenement> listerTous() throws DatabaseException {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT id_evenement, organisateur_id, nom, date_evenement, lieu, type_evenement, " +
                       "description, places_standard_disponibles, places_vip_disponibles, " +
                       "places_premium_disponibles, prix_standard, prix_vip, prix_premium, " +
                       "date_creation, statut FROM Evenements";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                evenements.add(mapRowToEvenement(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur listage événements", e);
            throw new DatabaseException("Erreur listage événements", e);
        }

        return evenements;
    }

    @Override
    public void mettreAJour(Evenement evenement) throws DatabaseException {
        String query = "UPDATE Evenements SET nom = ?, date_evenement = ?, lieu = ?, description = ?, " +
                       "places_standard_disponibles = ?, places_vip_disponibles = ?, " +
                       "places_premium_disponibles = ?, prix_standard = ?, prix_vip = ?, " +
                       "prix_premium = ?, statut = ? WHERE id_evenement = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, evenement.getNom());
            pstmt.setTimestamp(2, Timestamp.valueOf(evenement.getDateEvenement()));
            pstmt.setString(3, evenement.getLieu());
            pstmt.setString(4, evenement.getDescription());
            pstmt.setInt(5, evenement.getPlacesStandardDisponibles());
            pstmt.setInt(6, evenement.getPlacesVipDisponibles());
            pstmt.setInt(7, evenement.getPlacesPremiumDisponibles());
            pstmt.setBigDecimal(8, evenement.getPrixStandard());
            pstmt.setBigDecimal(9, evenement.getPrixVip());
            pstmt.setBigDecimal(10, evenement.getPrixPremium());
            pstmt.setString(11, evenement.getStatut().name());
            pstmt.setInt(12, evenement.getIdEvenement());

            pstmt.executeUpdate();
            logger.info("✓ Événement mis à jour: {}", evenement.getIdEvenement());
        } catch (SQLException e) {
            logger.error("Erreur mise à jour événement", e);
            throw new DatabaseException("Erreur mise à jour événement", e);
        }
    }

    @Override
    public void supprimer(int id) throws DatabaseException {
        String query = "DELETE FROM Evenements WHERE id_evenement = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            logger.info("✓ Événement supprimé: {}", id);
        } catch (SQLException e) {
            logger.error("Erreur suppression événement", e);
            throw new DatabaseException("Erreur suppression événement", e);
        }
    }

    private Evenement mapRowToEvenement(ResultSet rs) throws SQLException {
        TypeEvenement type = TypeEvenement.valueOf(rs.getString("type_evenement"));
        Evenement evenement;

        // Création de l'instance appropriée selon le type
        switch (type) {
            case CONCERT:
                evenement = new Concert();
                break;
            case CONFERENCE:
                evenement = new Conference();
                break;
            case SPECTACLE:
                evenement = new Spectacle();
                break;
            default:
                throw new SQLException("Type d'événement non supporté: " + type);
        }

        // Mapping des propriétés
        evenement.setIdEvenement(rs.getInt("id_evenement"));
        evenement.setOrganisateurId(rs.getInt("organisateur_id"));
        evenement.setNom(rs.getString("nom"));
        evenement.setDateEvenement(rs.getTimestamp("date_evenement").toLocalDateTime());
        evenement.setLieu(rs.getString("lieu"));
        evenement.setTypeEvenement(type);
        evenement.setDescription(rs.getString("description"));
        evenement.setPlacesStandardDisponibles(rs.getInt("places_standard_disponibles"));
        evenement.setPlacesVipDisponibles(rs.getInt("places_vip_disponibles"));
        evenement.setPlacesPremiumDisponibles(rs.getInt("places_premium_disponibles"));
        evenement.setPrixStandard(rs.getBigDecimal("prix_standard"));
        evenement.setPrixVip(rs.getBigDecimal("prix_vip"));
        evenement.setPrixPremium(rs.getBigDecimal("prix_premium"));
        evenement.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        evenement.setStatut(StatutEvenement.valueOf(rs.getString("statut")));

        return evenement;
    }
}
