package com.bschooleventmanager.eventmanager.util;

import java.util.regex.Pattern;

/**
 * Utilitaires de validation pour les données utilisateur
 * Contient les méthodes de validation pour les emails, mots de passe, etc.
 */
public class ValidationUtils {
    // Expression régulière pour la validation des emails
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    
    // Expression régulière pour un mot de passe fort (au moins une lettre et un chiffre)
    private static final String STRONG_PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d).{6,}$";
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(STRONG_PASSWORD_REGEX);

    /**
     * Valide le format d'une adresse email
     * @param email L'adresse email à valider
     * @return true si l'email est valide, false sinon
     */
    public static boolean isEmailValid(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Valide la longueur minimale d'un mot de passe
     * @param password Le mot de passe à valider
     * @return true si le mot de passe fait au moins 6 caractères, false sinon
     */
    public static boolean isPasswordValid(String password) {
        return password != null && password.length() >= 6;
    }
    
    /**
     * Valide qu'un mot de passe est fort (contient lettres et chiffres)
     * @param password Le mot de passe à valider
     * @return true si le mot de passe est fort, false sinon
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null) {
            return false;
        }
        return STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Valide qu'un mot de passe est très fort (lettres, chiffres et caractères spéciaux)
     * @param password Le mot de passe à valider
     * @return true si le mot de passe est très fort, false sinon
     */
    public static boolean isPasswordVeryStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>\\/?].*");
        
        return hasLower && hasUpper && hasDigit && hasSpecial;
    }
    
    /**
     * Retourne le niveau de force d'un mot de passe
     * @param password Le mot de passe à analyser
     * @return Niveau de force : WEAK, MEDIUM, STRONG, VERY_STRONG
     */
    public static PasswordStrength getPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return PasswordStrength.WEAK;
        }
        
        if (isPasswordVeryStrong(password)) {
            return PasswordStrength.VERY_STRONG;
        }
        
        if (isPasswordStrong(password)) {
            return PasswordStrength.STRONG;
        }
        
        if (password.length() >= 6) {
            return PasswordStrength.MEDIUM;
        }
        
        return PasswordStrength.WEAK;
    }

    /**
     * Valide qu'une chaîne n'est ni nulle ni vide
     * @param value La chaîne à valider
     * @return true si la chaîne contient du texte, false sinon
     */
    public static boolean isNonNull(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Valide qu'un nom contient uniquement des lettres et espaces
     * @param nom Le nom à valider
     * @return true si le nom est valide, false sinon
     */
    public static boolean isNomValid(String nom) {
        if (!isNonNull(nom)) {
            return false;
        }
        
        // Autoriser les lettres, espaces, tirets et apostrophes
        return nom.matches("^[a-zA-ZÀ-ÿ\\s'-]+$") && nom.length() >= 2;
    }
    
    /**
     * Énumération pour les niveaux de force des mots de passe
     */
    public enum PasswordStrength {
        WEAK("Faible", "#e74c3c"),
        MEDIUM("Moyen", "#f39c12"), 
        STRONG("Fort", "#2ecc71"),
        VERY_STRONG("Très fort", "#27ae60");
        
        private final String label;
        private final String color;
        
        PasswordStrength(String label, String color) {
            this.label = label;
            this.color = color;
        }
        
        public String getLabel() {
            return label;
        }
        
        public String getColor() {
            return color;
        }
    }
}
