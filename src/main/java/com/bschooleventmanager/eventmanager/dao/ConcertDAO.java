package com.bschooleventmanager.eventmanager.dao;

import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import com.bschooleventmanager.eventmanager.model.Concert;

import java.sql.*;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConcertDAO extends BaseDAO<Concert> {

    private static final Logger logger = LoggerFactory.getLogger(EvenementDAO.class);
    @Override
    public Concert creer(Concert concert) throws DatabaseException {
        Integer idOrg = 1;
        String query = "INSERT INTO evenements (organisateur_id, nom, date_evenement, lieu, type_evenement, " +
                "description, places_standard_disponibles, places_vip_disponibles, " +
                "places_premium_disponibles, prix_standard, prix_vip, prix_premium," +
                "artiste_groupe,age_min,type_concert) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, idOrg);
            pstmt.setString(2, concert.getTitre());
            pstmt.setDate(3, Date.valueOf(concert.getDateEvenement()));
            pstmt.setString(4, concert.getLieu());
            pstmt.setString(5, concert.getTypeEvenement().getLabel());
            pstmt.setString(6, concert.getDescription());
            pstmt.setInt(7, concert.getPlacesStandardDisponibles());
            pstmt.setInt(8, concert.getPlacesVipDisponibles());
            pstmt.setInt(9, concert.getPlacesPremiumDisponibles());
            pstmt.setBigDecimal(10, concert.getPrixStandard());
            pstmt.setBigDecimal(11, concert.getPrixVip());
            pstmt.setBigDecimal(12, concert.getPrixPremium());
            //pstmt.setString(13, concert.getStatut().getLabel());
            pstmt.setString(13, concert.getArtiste_groupe());
            pstmt.setInt(14, concert.getAgeMin());
            pstmt.setString(15, concert.getType().getLabel());

            int affectedRows = pstmt.executeUpdate();
            //pstmt.close();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        concert.setIdEvenement(rs.getInt(1));
                        logger.info("✓ Événement créé: {}", concert.getTitre());
                        return concert;
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Concert chercher(int id) throws DatabaseException{
        return null;
    }

    @Override
    public List<Concert> listerTous() throws DatabaseException {
        return List.of();
    }

    @Override
    public void mettreAJour(Concert concert) throws DatabaseException {

    }

    @Override
    public void supprimer(int id) throws DatabaseException {

    }
}
