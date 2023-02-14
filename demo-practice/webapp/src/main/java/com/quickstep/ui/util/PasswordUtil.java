package com.quickstep.ui.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.util.UUID;

/**
 * User: Ben
 * Date: 29/08/11
 * Time: 2:31 PM
 */
public class PasswordUtil {

    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static String generateRandomPassword() {
        return hashPassword(UUID.randomUUID().toString());
    }

    public static boolean passwordsMatch(String plaintextPassword, String hashedPassword) {
        final var result = BCrypt.verifyer().verify(plaintextPassword.toCharArray(), hashedPassword);
        return result.verified;
    }
}
