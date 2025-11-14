package com.bschooleventmanager.eventmanager.dao;

import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import com.bschooleventmanager.eventmanager.model.Spectacle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

public class SpectacleDAO extends BaseDAO<Spectacle> {
    private static final Logger logger = LoggerFactory.getLogger(EvenementDAO.class);

    @Override
    public Spectacle creer(Spectacle spectacle) throws DatabaseException {
        Integer idOrg = 1;
        String query = "INSERT INTO evenements (organisateur_id, nom, date_evenement, lieu, type_evenement, " +
                "description, places_standard_disponibles, places_vip_disponibles, " +
                "places_premium_disponibles, prix_standard, prix_vip, prix_premium," +
                "artiste_groupe,age_min,type_spectacle) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, idOrg);
            pstmt.setString(2, spectacle.getTitre());
            pstmt.setDate(3, Date.valueOf(spectacle.getDateEvenement()));
            pstmt.setString(4, spectacle.getLieu());
            pstmt.setString(5, spectacle.getTypeEvenement().getLabel());
            pstmt.setString(6, spectacle.getDescription());
            pstmt.setInt(7, spectacle.getPlacesStandardDisponibles());
            pstmt.setInt(8, spectacle.getPlacesVipDisponibles());
            pstmt.setInt(9, spectacle.getPlacesPremiumDisponibles());
            pstmt.setBigDecimal(10, spectacle.getPrixStandard());
            pstmt.setBigDecimal(11, spectacle.getPrixVip());
            pstmt.setBigDecimal(12, spectacle.getPrixPremium());
            //pstmt.setString(13, spectacle.getStatut().getLabel());
            pstmt.setString(13, spectacle.getTroupe_artistes());
            pstmt.setInt(14, spectacle.getAgeMin());
            pstmt.setString(15, spectacle.getTypeSpectacle().getLabel());

            int affectedRows = pstmt.executeUpdate();
            //pstmt.close();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        spectacle.setIdEvenement(rs.getInt(1));
                        logger.info("✓ Événement SPECTACLE créé: {}", spectacle.getTitre());
                        return spectacle;
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Spectacle chercher(int id) throws DatabaseException {
        return null;
    }

    @Override
    public List<Spectacle> listerTous() throws DatabaseException {
        return List.of();
    }

    @Override
    public void mettreAJour(Spectacle entity) throws DatabaseException {

    }

    @Override
    public void supprimer(int id) throws DatabaseException {

    }
}
