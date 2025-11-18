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
                "Domaine,intervenant,niveau_expertise, place_standard_vendues, place_p_vendu, place_vip_vendu, etat_event) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
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
            pstmt.setInt(16, conference.getPlaceStandardVendues());
            pstmt.setInt(17, conference.getPlacePremiumVendues());
            pstmt.setInt(18, conference.getPlaceVipVendues());
            pstmt.setBoolean(19, conference.isEtatEvent());

            int affectedRows = pstmt.executeUpdate();
         

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        conference.setIdEvenement(rs.getInt(1));
                        logger.info("✓ Événement SPECTACLE créé: {}", conference.getNom());
                        return conference;
                    }
                }
            }
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


    /**
     * Mettre à jour une conférence
     * @param conference
     * @return
     * @throws DatabaseException
     */
    @Override
    public Conference mettreAJour(Conference conference) throws DatabaseException {
        String query = "UPDATE evenements SET organisateur_id = ?, nom = ?, date_evenement = ?, lieu = ?, type_evenement = ?," +
                "    description = ?, places_standard_disponibles = ?, places_vip_disponibles = ?, places_premium_disponibles = ?," +
                "    prix_standard = ?, prix_vip = ?, prix_premium = ?, artiste_groupe = ?, age_min = ?, domaine = ?, " +
                "    intervenant = ?, type_concert = ?, type_spectacle = ?, niveau_expertise = ? WHERE id_evenement = ?;";

        

        try (Connection connection = getConnection(); PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
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
            pstmt.setNull(13, java.sql.Types.VARCHAR); // Artiste/Groupe n'est pas necessaire pour une conference
            pstmt.setInt(14, java.sql.Types.NULL); // Age min n'est pas necessaire pour une conference
            pstmt.setString(15,conference.getDomaine());
            pstmt.setString(16,conference.getIntervenants());
            pstmt.setNull(17, Types.NULL);// Type concert n'est pas necessaire pour une conference
            pstmt.setNull(18, Types.NULL);// Type spectacle n'est pas necessaire pour une conference
            pstmt.setString(19,conference.getNiveauExpertise().getLabel());
            pstmt.setInt(20, conference.getIdEvenement());

            int affectedRows = pstmt.executeUpdate();


            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        conference.setIdEvenement(rs.getInt(1));
                        logger.info("✓ Conference modifié: {}", conference.getNom());
                        return conference;
                    }
                }
            }
            
            throw new DatabaseException("Erreur lors de la mise a jour de la conference.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void supprimer(int id) throws DatabaseException {

    }
}
