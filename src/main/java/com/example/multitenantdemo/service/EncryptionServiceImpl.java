package com.example.multitenantdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Service
@Slf4j
public class EncryptionServiceImpl implements EncryptionService{
    public static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA256";
    public static final String CIPHER = "AES/CBC/PKCS5Padding";
    public static final String KEY_ALGORITHM = "AES";
    public static final int ITERATION_COUNT = 65536;
    public static final int KEY_LENGTH = 256;

    /**
     * {@inheritDoc}
     * */
    @Override
    public String encrypt(String stringToEncrypt, String secret, String salt) {
        try{
            log.info("Started encryption ...........");
            Assert.notNull(stringToEncrypt, "string to encrypt can not be null");
            IvParameterSpec ivParameterSpec = getIvParameterSpec();

            SecretKeySpec secretKey = getSecretKeySpec(secret, salt);
            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            log.info("Encryption almost done ...........");
            return Base64.getEncoder().encodeToString(cipher.doFinal(stringToEncrypt.getBytes("UTF-8")));
        }catch (Exception e){
            log.error("Error occur during encryption: ", e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public String decrypt(String stringToDecrypt, String secret, String salt) {
        try{
            log.info("Started decryption ......");
            Assert.notNull(stringToDecrypt, "string to decrypt can not be null");
            IvParameterSpec ivParameterSpec = getIvParameterSpec();

            SecretKeySpec secretKey = getSecretKeySpec(secret, salt);
            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            log.info("Decryption almost done ...........");
            return new String(cipher.doFinal(Base64.getDecoder().decode(stringToDecrypt)));
        }catch (Exception e){
            log.error("Error occur during decryption: ", e);
            return null;
        }
    }

    private IvParameterSpec getIvParameterSpec(){
        byte[] iv = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        return new IvParameterSpec(iv);
    }

    private SecretKeySpec getSecretKeySpec(String secret, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(HASH_ALGORITHM);
        KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_LENGTH);
        SecretKey tmp = keyFactory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), KEY_ALGORITHM);
    }
}
