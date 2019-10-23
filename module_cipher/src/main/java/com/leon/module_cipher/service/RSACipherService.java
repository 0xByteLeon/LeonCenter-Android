package com.leon.module_cipher.service;

import android.text.TextUtils;

import com.leon.module_cipher.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

/**
 * @time:2019/10/22 16:43
 * @author:Leon
 * @description:
 */
public class RSACipherService implements ICipherService {

    private String privateKey;
    private String publicKey;

    public RSACipherService(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    /**
     * 加密算法RSA
     */
    protected final String KEY_ALGORITHM = "RSA";
    protected final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";

    /**
     * 签名算法
     */
    protected final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    /**
     * RSA最大加密明文大小
     */
    protected final int MAX_ENCRYPT_BLOCK = 117;
    /**
     * RSA最大解密密文大小
     */
    protected final int MAX_DECRYPT_BLOCK = 256;


    /**
     * 对已加密数据进行签名
     *
     * @param data       已加密的数据
     * @param privateKey 私钥
     * @return 对已加密数据生成的签名
     * @throws Exception
     */

    public String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return Base64.encode(signature.sign());
    }

    /**
     * 验签
     *
     * @param data 签名之前的数据
     * @param sign 签名之后的数据
     * @return 验签是否成功
     * @throws Exception
     */
    public boolean verify(byte[] data, String sign) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify((PublicKey) generatePublicKey(KEY_ALGORITHM));
        signature.update(data);
        return signature.verify(Base64.decode(sign));
    }

    /**
     * 用私钥对数据进行解密
     *
     * @param encryptedData 使用公钥加密过的数据
     * @return 解密后的数据
     * @throws Exception
     */
    public byte[] decryptByPrivateKey(String encryptedData) throws Exception {
        if (TextUtils.isEmpty(encryptedData))
            return new byte[]{};
        return decryptByPrivateKey(Base64.decode(encryptedData));
    }
    /**
     * 用私钥对数据进行解密
     *
     * @param encryptedData 使用公钥加密过的数据
     * @return 解密后的数据
     * @throws Exception
     */
    public byte[] decryptByPrivateKey(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, generatePrivateKey(KEY_ALGORITHM));
        return exeDataOperate(cipher, encryptedData, MAX_DECRYPT_BLOCK);
    }

    private byte[] exeDataOperate(Cipher cipher, byte[] data, int maxBlockSize) throws IOException, BadPaddingException, IllegalBlockSizeException {
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段处理
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxBlockSize) {
                cache = cipher.doFinal(data, offSet, maxBlockSize);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * maxBlockSize;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 公钥解密
     *
     * @param encryptedData 使用私钥加密过的数据
     * @return 解密后的数据
     * @throws Exception
     */
    public byte[] decryptByPublicKey(String encryptedData) throws Exception {
        if (TextUtils.isEmpty(encryptedData)){
            return new byte[]{};
        }
        return decryptByPublicKey(Base64.decode(encryptedData));
    }
    /**
     * 公钥解密
     *
     * @param encryptedData 使用私钥加密过的数据
     * @return 解密后的数据
     * @throws Exception
     */
    public byte[] decryptByPublicKey(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, generatePublicKey(KEY_ALGORITHM));
        return exeDataOperate(cipher, encryptedData, MAX_DECRYPT_BLOCK);
    }

    public byte[] encryptByPublicKey(String data) throws Exception {
        if (TextUtils.isEmpty(data)) {
            return new byte[]{};
        }
        return encryptByPublicKey(Base64.decode(data));
    }

    /**
     * 公钥加密
     *
     * @param data 需要加密的数据
     * @return 使用公钥加密后的数据
     * @throws Exception
     */
    public byte[] encryptByPublicKey(byte[] data) throws Exception {
        // 对数据加密
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, generatePublicKey(KEY_ALGORITHM));
        return exeDataOperate(cipher, data, MAX_ENCRYPT_BLOCK);
    }

    public byte[] encryptByPrivateKey(String data) throws Exception {
        if (TextUtils.isEmpty(data)) {
            return new byte[]{};
        }
        return encryptByPrivateKey(Base64.decode(data));
    }

    /**
     * 私钥加密
     *
     * @param data 待加密的数据
     * @return 使用私钥加密后的数据
     * @throws Exception
     */
    public byte[] encryptByPrivateKey(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, generatePrivateKey(KEY_ALGORITHM));
        return exeDataOperate(cipher, data, MAX_ENCRYPT_BLOCK);
    }

    @Override
    public String getPublicKey() {
        return publicKey;
    }

    @Override
    public String getPrivateKey() {
        return privateKey;
    }
}
