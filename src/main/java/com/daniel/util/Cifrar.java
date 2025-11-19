package com.daniel.util;

import org.apache.commons.codec.digest.DigestUtils;

public class Cifrar {
    public static String cifrar(String input) {
        return DigestUtils.sha256Hex(input);
    }
}