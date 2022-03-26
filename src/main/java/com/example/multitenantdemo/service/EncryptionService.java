package com.example.multitenantdemo.service;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface EncryptionService {
    /**
     * @param stringToEncrypt
     * @param secret secret key
     * @param salt   salt key
     * @return String encrypted password or null
     * */
    String encrypt(String stringToEncrypt, String secret, String salt);

    /**
     * @param stringToDecrypt
     * @param secret secret key
     * @param salt   salt key
     * @return String decrypted password or null
     * */
    String decrypt(String stringToDecrypt, String secret, String salt);
}
