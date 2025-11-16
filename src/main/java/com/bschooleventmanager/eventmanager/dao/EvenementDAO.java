package com.bschooleventmanager.eventmanager.dao;

import com.bschooleventmanager.eventmanager.model.Evenement;
import com.bschooleventmanager.eventmanager.model.enums.StatutEvenement;
import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvenementDAO  {
    private static final Logger logger = LoggerFactory.getLogger(EvenementDAO.class);

    public static List<Evenement> getAllEvents() {
        List<Evenement> evenements = new ArrayList<>();

        String sql = "SELECT * FROM evenements ORDER BY date_evenement ASC";
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                evenements.add(new Evenement(
                        rs.getInt("id_evenement"),
                        rs.getInt("organisateur_id"),
                        rs.getString("nom"),
                        rs.getTimestamp("date_evenement").toLocalDateTime(),
                        rs.getString("lieu"),
                        TypeEvenement.valueOf(rs.getString("type_evenement").toUpperCase()),
                        rs.getString("description"),
                        rs.getInt("places_standard_disponibles"),
                        rs.getInt("places_vip_disponibles"),
                        rs.getInt("places_premium_disponibles"),
                        rs.getBigDecimal("prix_standard"),
                        rs.getBigDecimal("prix_vip"),
                        rs.getBigDecimal("prix_premium"),
                        rs.getTimestamp("date_creation").toLocalDateTime(),
                        StatutEvenement.valueOf(rs.getString("statut").toUpperCase())
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evenements;
    }
}
