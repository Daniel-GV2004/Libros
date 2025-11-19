package com.daniel.util;

public class Validate {
    public static boolean password(String textPw){
        return textPw.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{9,}$");
    }
}