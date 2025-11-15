package com.bschooleventmanager.eventmanager.util;

import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitaire pour gérer les dimensions et le positionnement des fenêtres
 * Permet d'adapter automatiquement la taille de l'application à l'écran
 */
public class WindowUtils {
    private static final Logger logger = LoggerFactory.getLogger(WindowUtils.class);
    
    // Marges par défaut pour éviter que la fenêtre couvre complètement l'écran
    private static final double DEFAULT_MARGIN_RATIO = 0.05; // 5% de marge
    
    // Constructeur privé pour empêcher l'instanciation
    private WindowUtils() {
        throw new UnsupportedOperationException("Classe utilitaire - ne peut pas être instanciée");
    }
    
    /**
     * Configure une fenêtre selon les paramètres de l'application
     * @param stage La fenêtre à configurer
     */
    public static void configureStage(Stage stage) {
        try {
            if (AppConfig.isWindowAdaptive()) {
                adaptToScreen(stage);
            } else {
                setFixedSize(stage);
            }
            
            if (AppConfig.isWindowMaximized()) {
                stage.setMaximized(true);
            }
            
            // Centrer la fenêtre si elle n'est pas maximisée
            if (!stage.isMaximized()) {
                stage.centerOnScreen();
            }
            
            logger.info("✓ Fenêtre configurée - Adaptive: {}, Maximized: {}", 
                       AppConfig.isWindowAdaptive(), AppConfig.isWindowMaximized());
            
        } catch (Exception e) {
            logger.error("Erreur lors de la configuration de la fenêtre", e);
            // En cas d'erreur, utiliser la taille par défaut
            setFixedSize(stage);
        }
    }
    
    /**
     * Adapte la taille de la fenêtre à l'écran principal
     * @param stage La fenêtre à adapter
     */
    private static void adaptToScreen(Stage stage) {
        try {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Calculer les dimensions avec marge
            double margin = Math.min(screenBounds.getWidth(), screenBounds.getHeight()) * DEFAULT_MARGIN_RATIO;
            double width = screenBounds.getWidth() - (margin * 2);
            double height = screenBounds.getHeight() - (margin * 2);
            
            // Appliquer les dimensions
            stage.setWidth(width);
            stage.setHeight(height);
            
            // Positionner avec la marge
            stage.setX(screenBounds.getMinX() + margin);
            stage.setY(screenBounds.getMinY() + margin);
            
            logger.info("Fenêtre adaptée à l'écran - Taille: {}x{}, Position: [{}, {}]", 
                       (int)width, (int)height, (int)margin, (int)margin);
                       
        } catch (Exception e) {
            logger.error("Erreur lors de l'adaptation à l'écran", e);
            setFixedSize(stage);
        }
    }
    
    /**
     * Utilise la taille fixe définie dans la configuration
     * @param stage La fenêtre à configurer
     */
    private static void setFixedSize(Stage stage) {
        int width = AppConfig.getWindowWidth();
        int height = AppConfig.getWindowHeight();
        
        stage.setWidth(width);
        stage.setHeight(height);
        
        logger.info("Taille fixe appliquée - Dimensions: {}x{}", width, height);
    }
    
    /**
     * Obtient les dimensions optimales pour une fenêtre
     * @return Tableau contenant [largeur, hauteur]
     */
    public static double[] getOptimalDimensions() {
        if (AppConfig.isWindowAdaptive()) {
            try {
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                double margin = Math.min(screenBounds.getWidth(), screenBounds.getHeight()) * DEFAULT_MARGIN_RATIO;
                
                return new double[]{
                    screenBounds.getWidth() - (margin * 2),
                    screenBounds.getHeight() - (margin * 2)
                };
            } catch (Exception e) {
                logger.warn("Erreur lors du calcul des dimensions optimales", e);
            }
        }
        
        // Retourner les dimensions par défaut
        return new double[]{
            AppConfig.getWindowWidth(),
            AppConfig.getWindowHeight()
        };
    }
    
    /**
     * Vérifie si l'écran est suffisamment grand pour les dimensions minimales
     * @param minWidth Largeur minimale requise
     * @param minHeight Hauteur minimale requise
     * @return true si l'écran peut accueillir les dimensions
     */
    public static boolean canAccommodateSize(double minWidth, double minHeight) {
        try {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            return screenBounds.getWidth() >= minWidth && screenBounds.getHeight() >= minHeight;
        } catch (Exception e) {
            logger.error("Erreur lors de la vérification des dimensions", e);
            return false;
        }
    }
}