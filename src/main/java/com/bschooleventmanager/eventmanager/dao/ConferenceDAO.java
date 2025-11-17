package com.bschooleventmanager.eventmanager.dao;

import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import com.bschooleventmanager.eventmanager.model.Conference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

public class ConferenceDAO extends BaseDAO<Conference> {
    private static final Logger logger = LoggerFactory.getLogger(EvenementDAO.class);

    @Override
    public Conference creer(Conference conference) throws DatabaseException {
       
        String query = "INSERT INTO evenements (organisateur_id, nom, date_evenement, lieu, type_evenement, " +
                "description, places_standard_disponibles, places_vip_disponibles, " +
                "places_premium_disponibles, prix_standard, prix_vip, prix_premium," +
                "Domaine,intervenant,niveau_expertise) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
Connection connection = getConnection();

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, conference.getOrganisateurId());
            pstmt.setString(2, conference.getNom());
            pstmt.setTimestamp(3, Timestamp.valueOf(conference.getDateEvenement()));
            pstmt.setString(4, conference.getLieu());
            pstmt.setString(5, conference.getTypeEvenement().getLabel());
            pstmt.setString(6, conference.getDescription());
            pstmt.setInt(7, conference.getPlacesStandardDisponibles());
            pstmt.setInt(8, conference.getPlacesVipDisponibles());
            pstmt.setInt(9, conference.getPlacesPremiumDisponibles());
            pstmt.setBigDecimal(10, conference.getPrixStandard());
            pstmt.setBigDecimal(11, conference.getPrixVip());
            pstmt.setBigDecimal(12, conference.getPrixPremium());
            //pstmt.setString(13, spectacle.getStatut().getLabel());
            pstmt.setString(13, conference.getDomaine());
            pstmt.setString(14, conference.getIntervenants());
            pstmt.setString(15, conference.getNiveauExpertise().getLabel());

            int affectedRows = pstmt.executeUpdate();
            //pstmt.close();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        conference.setIdEvenement(rs.getInt(1));
                        logger.info("✓ Événement SPECTACLE créé: {}", conference.getNom());
                        return conference;
                    }
                }
            }
            connection.close();
            throw new DatabaseException("Erreur lors de la création de l'événement.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Conference chercher(int id) throws DatabaseException {
        return null;
    }

    @Override
    public List<Conference> listerTous() throws DatabaseException {
        return List.of();
    }

    @Override
    public void mettreAJour(Conference entity) throws DatabaseException {

    }

    @Override
    public void supprimer(int id) throws DatabaseException {

    }
}
