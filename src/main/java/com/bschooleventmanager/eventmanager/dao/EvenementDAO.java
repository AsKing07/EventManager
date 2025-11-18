package com.bschooleventmanager.eventmanager.dao;

import com.bschooleventmanager.eventmanager.exception.DatabaseException;
import com.bschooleventmanager.eventmanager.model.Concert;
import com.bschooleventmanager.eventmanager.model.Conference;
import com.bschooleventmanager.eventmanager.model.Evenement;
import com.bschooleventmanager.eventmanager.model.Spectacle;
import com.bschooleventmanager.eventmanager.model.enums.EtatEvent;
import com.bschooleventmanager.eventmanager.model.enums.StatutEvenement;
import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvenementDAO extends BaseDAO<Evenement> {
    private static final Logger logger = LoggerFactory.getLogger(EvenementDAO.class);

    //Méthodes de BaseDAO à implémenter
     @Override
    public Evenement creer(Evenement evenement) throws DatabaseException {
        return createEvent(evenement);
    }

     @Override
    public List<Evenement> listerTous() throws DatabaseException {
       return getAllEvents();
   
    }

    @Override
    public Evenement mettreAJour(Evenement evenement) throws DatabaseException {
        updateEvent(evenement);
        return evenement;
    }

    @Override
    public void supprimer(int id) throws DatabaseException {
deleteEventById(id);
 
    }

    @Override
    public Evenement chercher(int id) throws DatabaseException {
        return getEventById(id);
    }
    public List<Evenement> getEventByType(TypeEvenement type) throws DatabaseException {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT id_evenement, organisateur_id, nom, date_evenement, lieu, type_evenement, " +
                       "description, places_standard_disponibles, places_vip_disponibles, " +
                       "places_premium_disponibles, prix_standard, prix_vip, prix_premium, " +
                       "date_creation, statut FROM Evenements WHERE type_evenement = ?";

        try (Connection connection = getConnection();PreparedStatement pstmt = connection.prepareStatement(query)) {
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

    /**
     * Marque un événement comme supprimé (mise à jour de la colonne `etat_event` dans la base de données).
     *
     * Effets et logique:
     *  - exécute une mise à jour sur la table `evenements`
     *  - ferme correctement la connexion et le PreparedStatement grâce à try-with-resources
     *
     * Remarques :
     *  - le premier paramètre SQL correspond à la valeur d'état (ici `EtatEvent.SUPPRIME.getCode()`),
     *    le second à l'identifiant de l'événement.
     *  - on capture et re-propage l'exception SQL en RuntimeException
     */
    public void suppEvent(int id){
        String query = "UPDATE evenements SET etat_event=? WHERE id_evenement=?;";
        /*try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String query = "UPDATE evenements SET etat_event=? WHERE id_evenement=?;";
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, EtatEvent.SUPPRIME.getCode());
            st.setInt(2,id);
            st.executeUpdate();
            conn.close();*/
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement st = conn.prepareStatement(query)) {

            // 1er paramètre : code de l'état SUPPRIME
            st.setInt(1, EtatEvent.SUPPRIME.getCode());
            // 2e paramètre : id de l'événement
            st.setInt(2, id);

            int rows = st.executeUpdate();
            if (rows == 0) {
                logger.warn("Aucun événement trouvé pour l'ID {} lors de la suppression logique.", id);
            } else {
                logger.info("Événement ID {} marqué comme supprimé.", id);
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
   

    public static List<Evenement> getAllEvents() {
        List<Evenement> evenements = new ArrayList<>();

        String sql = "SELECT * FROM evenements ORDER BY date_evenement ASC";
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                evenements.add(mapRowToEvenement(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evenements;
    }

    public static Evenement createEvent(Evenement evenement) throws DatabaseException {
     
         String query = "INSERT INTO Evenements (organisateur_id, nom, date_evenement, lieu, type_evenement, " +
                       "description, places_standard_disponibles, places_vip_disponibles, " +
                       "places_premium_disponibles, prix_standard, prix_vip, prix_premium, statut, " +
                       "place_standard_vendues, place_p_vendu, place_vip_vendu, etat_event) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


        try (Connection connection = DatabaseConnection.getInstance().getConnection();PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
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
            pstmt.setInt(14, evenement.getPlaceStandardVendues());
            pstmt.setInt(15, evenement.getPlacePremiumVendues());
            pstmt.setInt(16, evenement.getPlaceVipVendues());
            pstmt.setBoolean(17, evenement.isEtatEvent());

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

    public static Evenement updateEvent(Evenement evenement) throws DatabaseException {
        String sql = "UPDATE evenements SET " +
                "organisateur_id = ?, nom = ?, date_evenement = ?, lieu = ?, " +
                "type_evenement = ?, description = ?, places_standard_disponibles = ?, " +
                "places_vip_disponibles = ?, places_premium_disponibles = ?, " +
                "prix_standard = ?, prix_vip = ?, prix_premium = ?, statut = ?, " +
                "place_standard_vendues = ?, place_p_vendu = ?, place_vip_vendu = ?, etat_event = ? " +
                "WHERE id_evenement = ?";

        try ( Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);) {
           

            stmt.setInt(1, evenement.getOrganisateurId());
            stmt.setString(2, evenement.getNom());
            stmt.setTimestamp(3, Timestamp.valueOf(evenement.getDateEvenement()));
            stmt.setString(4, evenement.getLieu());
            stmt.setString(5, evenement.getTypeEvenement().name());
            stmt.setString(6, evenement.getDescription());
            stmt.setInt(7, evenement.getPlacesStandardDisponibles());
            stmt.setInt(8, evenement.getPlacesVipDisponibles());
            stmt.setInt(9, evenement.getPlacesPremiumDisponibles());
            stmt.setBigDecimal(10, evenement.getPrixStandard());
            stmt.setBigDecimal(11, evenement.getPrixVip());
            stmt.setBigDecimal(12, evenement.getPrixPremium());
            stmt.setString(13, evenement.getStatut().name());
            stmt.setInt(14, evenement.getPlaceStandardVendues());
            stmt.setInt(15, evenement.getPlacePremiumVendues());
            stmt.setInt(16, evenement.getPlaceVipVendues());
            stmt.setBoolean(17, evenement.isEtatEvent());
            stmt.setInt(18, evenement.getIdEvenement());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Aucun événement trouvé avec l'ID: " + evenement.getIdEvenement());
            }

            logger.info("Événement mis à jour avec succès: {}", evenement.getNom());
            return evenement;

        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de l'événement", e);
            throw new DatabaseException("Erreur lors de la mise à jour de l'événement", e);
        }
    }

    /**
     * Met à jour les places vendues pour un événement
     */
    public static void mettreAJourPlacesVendues(int idEvenement, int placesStandardVendues, 
                                               int placesVipVendues, int placesPremiumVendues) throws DatabaseException {
        String sql = "UPDATE evenements SET place_standard_vendues = ?, place_vip_vendu = ?, place_p_vendu = ? " +
                    "WHERE id_evenement = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, placesStandardVendues);
            stmt.setInt(2, placesVipVendues);
            stmt.setInt(3, placesPremiumVendues);
            stmt.setInt(4, idEvenement);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Aucun événement trouvé avec l'ID: " + idEvenement);
            }
            
            logger.info("Places vendues mises à jour pour l'événement ID: {}", idEvenement);
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour des places vendues", e);
            throw new DatabaseException("Erreur lors de la mise à jour des places vendues", e);
        }
    }

    /**
     * Active ou désactive un événement
     */
    public static void changerEtatEvenement(int idEvenement, boolean actif) throws DatabaseException {
        String sql = "UPDATE evenements SET etat_event = ? WHERE id_evenement = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, actif);
            stmt.setInt(2, idEvenement);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Aucun événement trouvé avec l'ID: " + idEvenement);
            }
            
            logger.info("État de l'événement {} changé à: {}", idEvenement, actif ? "actif" : "inactif");
        } catch (SQLException e) {
            logger.error("Erreur lors du changement d'état de l'événement", e);
            throw new DatabaseException("Erreur lors du changement d'état de l'événement", e);
        }

    }

    public static Evenement getEventById(int id) throws DatabaseException {
        String sql = "SELECT * FROM evenements WHERE id_evenement = ?";

        try( Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRowToEvenement(rs);
            } else {
                throw new DatabaseException("Aucun événement trouvé avec l'ID: " + id);
            }

        

        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération de l'événement", e);
            throw new DatabaseException("Erreur lors de la récupération de l'événement", e);
        }
    }

    public static List<Evenement> getEventsByOrganizerId(int organizerId) throws DatabaseException {
        List<Evenement> evenements = new ArrayList<>();
        String sql = "SELECT * FROM evenements WHERE organisateur_id = ? ORDER BY date_evenement ASC";

        try( Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, organizerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                evenements.add(mapRowToEvenement(rs));
            }

            return evenements;

        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des événements de l'organisateur", e);
            throw new DatabaseException("Erreur lors de la récupération des événements de l'organisateur", e);
        }
    }

    public static boolean deleteEventById(int id) throws DatabaseException {
        String sql = "DELETE FROM evenements WHERE id_evenement = ?";

        try ( Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);) {
            
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Aucun événement trouvé avec l'ID: " + id);
            }

            logger.info("Événement supprimé avec succès, ID: {}", id);
            return true;

        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression de l'événement", e);
            throw new DatabaseException("Erreur lors de la suppression de l'événement", e);
        }
    }
        

    private static Evenement mapRowToEvenement(ResultSet rs) throws SQLException {
        // Gestion sécurisée du type d'événement
        String typeStr = rs.getString("type_evenement");
        TypeEvenement type;
        if (typeStr != null && !typeStr.trim().isEmpty()) {
            type = TypeEvenement.valueOf(typeStr.trim());
        } else {
            type = TypeEvenement.CONFERENCE; // Valeur par défaut
        }
        
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
        
        // Nouvelles colonnes pour les ventes
        evenement.setPlaceStandardVendues(rs.getInt("place_standard_vendues"));
        evenement.setPlacePremiumVendues(rs.getInt("place_p_vendu"));
        evenement.setPlaceVipVendues(rs.getInt("place_vip_vendu"));
        evenement.setEtatEvent(rs.getBoolean("etat_event"));
        
        // Gestion sécurisée du statut avec valeur par défaut
        String statutStr = rs.getString("statut");
        if (statutStr != null && !statutStr.trim().isEmpty()) {
            evenement.setStatut(StatutEvenement.valueOf(statutStr.trim()));
        } else {
            evenement.setStatut(StatutEvenement.A_VENIR); // Valeur par défaut
        }

        return evenement;
    }
}
