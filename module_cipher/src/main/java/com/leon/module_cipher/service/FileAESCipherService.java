package com.leon.module_cipher.service;

import com.apkfuns.logutils.LogUtils;
import com.leon.module_cipher.Base64;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.AlgorithmParameters;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @time:2019/10/22 15:02
 * @author:Leon
 * @description:
 */
public class FileAESCipherService implements ICipherService {

    private String aesKey;
    private String ivParameter;

    public FileAESCipherService(String aesKey, String ivParameter) {
        this.aesKey = aesKey;
        this.ivParameter = ivParameter;
    }

    /**
     * 加密算法AES
     */
    protected final String KEY_ALGORITHM = "AES";
    protected final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private int bufferSize = 1024;
    protected final int KEY_SIZE = 256;

    public void decrypt(String srcPath, String dstPath) throws Exception {
        // 对数据解密
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, generatePublicKey(KEY_ALGORITHM), generateIV(Base64.decode(getIv())));
        try (FileInputStream file = new FileInputStream(srcPath);
             FileOutputStream outStream = new FileOutputStream(dstPath);
             CipherOutputStream cos = new CipherOutputStream(outStream, cipher)) {
            byte[] buf = new byte[1024];
            int read;
            while ((read = file.read(buf)) != -1) {
                cos.write(buf, 0, read);
            }
            outStream.flush();
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * 初始化向量参数
     *
     * @param iv
     * @return
     * @throws Exception
     */
    private AlgorithmParameters generateIV(byte[] iv) throws Exception {
        AlgorithmParameters params = AlgorithmParameters.getInstance(KEY_ALGORITHM);
        params.init(new IvParameterSpec(iv));
        return params;
    }

    public boolean encrypt(String srcPath, String dstPath) throws Exception {
        // 对数据加密
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.decode(getIv()));
        cipher.init(Cipher.ENCRYPT_MODE, generatePublicKey(KEY_ALGORITHM), ivParameterSpec);
        return exeFileOperate(cipher, srcPath, dstPath, bufferSize);
    }

    /**
     * 执行文件操作
     */
    protected boolean exeFileOperate(Cipher cipher, String srcPath, String dstPath, int maxBlockSize) {
        try (FileInputStream file = new FileInputStream(srcPath);
             FileOutputStream outStream = new FileOutputStream(dstPath);
             CipherOutputStream cos = new CipherOutputStream(outStream, cipher)) {

            byte[] buf = new byte[maxBlockSize];
            int read;
            while ((read = file.read(buf)) != -1) {
                cos.write(buf, 0, read);
            }
            cos.flush();
            outStream.flush();
            return true;
        } catch (Exception e) {
            LogUtils.e(e);
            e.printStackTrace();
        }
        return false;
    }


    private String getIv() {
        return ivParameter;
//        return "BEz2ffkQadq1VQD6HTUJAw==";
    }

    @Override
    public String getPublicKey() {
//        return "zYdyxm0Xar5WOHq4laacDlXUU+F+FWZ5gHqVg5v6mx4=";
        return aesKey;
    }

    @Override
    public String getPrivateKey() {
//        return "zYdyxm0Xar5WOHq4laacDlXUU+F+FWZ5gHqVg5v6mx4=";
        return aesKey;
    }

    @Override
    public Key generatePrivateKey(String keyAlogrithm) throws Exception {
        return new SecretKeySpec(Base64.decode(getPrivateKey()), keyAlogrithm);
    }

    @Override
    public Key generatePublicKey(String keyAlogrithm) throws Exception {
        return new SecretKeySpec(Base64.decode(getPrivateKey()), keyAlogrithm);
    }
}
