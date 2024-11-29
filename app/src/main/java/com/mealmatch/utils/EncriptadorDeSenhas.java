package com.mealmatch.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncriptadorDeSenhas {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Gera o hash da senha
    public static String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    // Verifica se a senha corresponde ao hash
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }
  }