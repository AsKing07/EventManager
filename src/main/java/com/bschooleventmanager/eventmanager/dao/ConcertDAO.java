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
      
        String query = "INSERT INTO evenements (organisateur_id, nom, date_evenement, lieu, type_evenement, " +
                "description, places_standard_disponibles, places_vip_disponibles, " +
                "places_premium_disponibles, prix_standard, prix_vip, prix_premium," +
                "artiste_groupe,age_min,type_concert, place_standard_vendues, place_p_vendu, place_vip_vendu, etat_event) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
Connection connection = getConnection();

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, concert.getOrganisateurId());
            pstmt.setString(2, concert.getNom());
            pstmt.setTimestamp(3, Timestamp.valueOf(concert.getDateEvenement()));
            pstmt.setString(4, concert.getLieu());
            pstmt.setString(5, concert.getTypeEvenement().getLabel());
            pstmt.setString(6, concert.getDescription());
            pstmt.setInt(7, concert.getPlacesStandardDisponibles());
            pstmt.setInt(8, concert.getPlacesVipDisponibles());
            pstmt.setInt(9, concert.getPlacesPremiumDisponibles());
            pstmt.setBigDecimal(10, concert.getPrixStandard());
            pstmt.setBigDecimal(11, concert.getPrixVip());
            pstmt.setBigDecimal(12, concert.getPrixPremium());
            pstmt.setString(13, concert.getArtiste_groupe());
            pstmt.setInt(14, concert.getAgeMin());
            pstmt.setString(15, concert.getType().getLabel());
            pstmt.setInt(16, concert.getPlaceStandardVendues());
            pstmt.setInt(17, concert.getPlacePremiumVendues());
            pstmt.setInt(18, concert.getPlaceVipVendues());
            pstmt.setBoolean(19, concert.isEtatEvent());

            int affectedRows = pstmt.executeUpdate();
    

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        concert.setIdEvenement(rs.getInt(1));
                        logger.info("✓ Événement créé: {}", concert.getNom());
                        connection.close();
                        return concert;
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
