package com.daniel.util;

public class Validate {
    public static boolean password(String textPw){
        return textPw.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{9,}$");
    }

    public static boolean esVacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean puntuacion(Double s){
        return s == null || s > 10.0;
    }
}