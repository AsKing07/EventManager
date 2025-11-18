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
        String query = "INSERT INTO evenements (organisateur_id, nom, date_evenement, lieu, type_evenement, " +
                "description, places_standard_disponibles, places_vip_disponibles, " +
                "places_premium_disponibles, prix_standard, prix_vip, prix_premium," +
                "artiste_groupe,age_min,type_spectacle, place_standard_vendues, place_p_vendu, place_vip_vendu, etat_event) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


        try ( Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = connection.prepareStatement(query); PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, spectacle.getOrganisateurId());
            pstmt.setString(2, spectacle.getNom());
            pstmt.setTimestamp(3, Timestamp.valueOf(spectacle.getDateEvenement()));
            pstmt.setString(4, spectacle.getLieu());
            pstmt.setString(5, spectacle.getTypeEvenement().getLabel());
            pstmt.setString(6, spectacle.getDescription());
            pstmt.setInt(7, spectacle.getPlacesStandardDisponibles());
            pstmt.setInt(8, spectacle.getPlacesVipDisponibles());
            pstmt.setInt(9, spectacle.getPlacesPremiumDisponibles());
            pstmt.setBigDecimal(10, spectacle.getPrixStandard());
            pstmt.setBigDecimal(11, spectacle.getPrixVip());
            pstmt.setBigDecimal(12, spectacle.getPrixPremium());
            pstmt.setString(13, spectacle.getTroupe_artistes());
            pstmt.setInt(14, spectacle.getAgeMin());
            pstmt.setString(15, spectacle.getTypeSpectacle().getLabel());
            pstmt.setInt(16, spectacle.getPlaceStandardVendues());
            pstmt.setInt(17, spectacle.getPlacePremiumVendues());
            pstmt.setInt(18, spectacle.getPlaceVipVendues());
            pstmt.setBoolean(19, spectacle.isEtatEvent());

            int affectedRows = pstmt.executeUpdate();
           

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        spectacle.setIdEvenement(rs.getInt(1));
                        logger.info("✓ Événement SPECTACLE créé: {}", spectacle.getNom());
                        return spectacle;
                    }
                }
            }
           
            throw new DatabaseException("Erreur lors de la création de l'événement.");
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

    /**
     * Mettre à jour un spectacle existant
     * @param spectacle
     * @return
     * @throws DatabaseException
     */
    @Override
    public Spectacle mettreAJour(Spectacle spectacle) throws DatabaseException {
        String query = "UPDATE evenements SET organisateur_id = ?, nom = ?, date_evenement = ?, lieu = ?, type_evenement = ?," +
                "    description = ?, places_standard_disponibles = ?, places_vip_disponibles = ?, places_premium_disponibles = ?," +
                "    prix_standard = ?, prix_vip = ?, prix_premium = ?, artiste_groupe = ?, age_min = ?, domaine = ?, " +
                "    intervenant = ?, type_concert = ?, type_spectacle = ?, niveau_expertise = ? WHERE id_evenement = ?;";

        

        try ( Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, spectacle.getOrganisateurId());
            pstmt.setString(2, spectacle.getNom());
            pstmt.setTimestamp(3, Timestamp.valueOf(spectacle.getDateEvenement()));
            pstmt.setString(4, spectacle.getLieu());
            pstmt.setString(5, spectacle.getTypeEvenement().getLabel());
            pstmt.setString(6, spectacle.getDescription());
            pstmt.setInt(7, spectacle.getPlacesStandardDisponibles());
            pstmt.setInt(8, spectacle.getPlacesVipDisponibles());
            pstmt.setInt(9, spectacle.getPlacesPremiumDisponibles());
            pstmt.setBigDecimal(10, spectacle.getPrixStandard());
            pstmt.setBigDecimal(11, spectacle.getPrixVip());
            pstmt.setBigDecimal(12, spectacle.getPrixPremium());
            pstmt.setString(13, spectacle.getTroupe_artistes());
            pstmt.setInt(14, spectacle.getAgeMin());
            pstmt.setNull(15,java.sql.Types.VARCHAR); // Domaine n'est pas necessaire pour un spectacle
            pstmt.setNull(16,java.sql.Types.VARCHAR); // Intervenants n'est pas necessaire pour un spectacle
            pstmt.setNull(17, java.sql.Types.VARCHAR); // Type concert n'est pas necessaire pour un spectacle
            pstmt.setString(18, spectacle.getTypeSpectacle().getLabel());
            pstmt.setInt(19, java.sql.Types.NULL); // Niveau expertise n'est pas necessaire pour un concert
            pstmt.setInt(20, spectacle.getIdEvenement());

            int affectedRows = pstmt.executeUpdate();


            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        spectacle.setIdEvenement(rs.getInt(1));
                        logger.info("✓ spectacle modifié: {}", spectacle.getNom());
                       
                        return spectacle;
                    }
                }
            }
            
            throw new DatabaseException("Erreur lors de la mise a jour du du spectacle.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void supprimer(int id) throws DatabaseException {

    }
}
