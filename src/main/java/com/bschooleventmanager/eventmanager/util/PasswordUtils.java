package com.bschooleventmanager.eventmanager.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    /**
     * Hasher un mot de passe
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    /**
     * Vérifier un mot de passe
     */
    public static boolean verifyPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
