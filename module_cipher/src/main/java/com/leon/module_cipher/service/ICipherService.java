package com.leon.module_cipher.service;

import com.leon.module_cipher.Base64;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @time:2019/10/18 11:48
 * @author:Leon
 * @description:
 */
public interface ICipherService {
    String getPublicKey();

    String getPrivateKey();

    default Key generatePrivateKey(String keyAlogrithm) throws Exception {
        byte[] keyBytes = Base64.decode(getPrivateKey());
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(keyAlogrithm);
        return keyFactory.generatePrivate(pkcs8KeySpec);
    }

    default Key generatePublicKey(String keyAlogrithm) throws Exception {
        byte[] keyBytes = Base64.decode(getPublicKey());
//        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(keyAlogrithm);
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

}
