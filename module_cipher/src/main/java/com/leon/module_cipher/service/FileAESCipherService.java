package com.leon.module_cipher.service;

import com.apkfuns.logutils.LogUtils;
import com.leon.module_cipher.Base64;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
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

    public void encrypt(String srcPath, String dstPath) throws Exception {
        // 对数据加密
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.decode(getIv()));
        cipher.init(Cipher.ENCRYPT_MODE, generatePublicKey(KEY_ALGORITHM), ivParameterSpec);
        try (FileInputStream file = new FileInputStream(srcPath);
             FileOutputStream outStream = new FileOutputStream(dstPath);
             CipherOutputStream cos = new CipherOutputStream(outStream, cipher);) {

            byte[] buf = new byte[1024];
            int read;
            while ((read = file.read(buf)) != -1) {
                cos.write(buf, 0, read);
            }
            outStream.flush();
        } catch (Exception e) {
            LogUtils.e(e);
            e.printStackTrace();
        }
    }

    /**
     * 执行文件操作
     */
    protected void exeFileOperate(Cipher cipher, String srcFile, String dstFile, int maxBlockSize) throws Exception {
        FileOutputStream dstFileOutputStream = new FileOutputStream(dstFile);
        BufferedOutputStream dstBufferedOutputStream = new BufferedOutputStream(dstFileOutputStream, maxBlockSize);

        RandomAccessFile srcRaf = new RandomAccessFile(srcFile, "r");
        long srcTotalLenght = srcRaf.length();
        FileChannel srcChannel = srcRaf.getChannel();

        long multiples = srcTotalLenght / maxBlockSize;
        long remainder = srcTotalLenght % maxBlockSize;
        LogUtils.d("multiples : " + multiples + " remainder : " + remainder + " maxBlockSize : " + maxBlockSize + " srcTotalLenght : " + srcTotalLenght);
        MappedByteBuffer srcBuffer = srcChannel.map(
                FileChannel.MapMode.READ_ONLY, 0, srcTotalLenght);
        byte[] bytes = new byte[maxBlockSize];
        //先对整除部分
        for (int offset = 0; offset < srcTotalLenght; offset += maxBlockSize) {
            if (srcTotalLenght - offset >= maxBlockSize) {
                for (int i = 0; i < maxBlockSize; i++) {
                    bytes[i] = srcBuffer.get(offset + i);
                }
                dstBufferedOutputStream.write(cipher.doFinal(bytes));
            } else {
                byte[] dst = new byte[(int) (srcTotalLenght - offset)];
                for (int i = 0; i < srcTotalLenght - offset; i++) {
                    dst[i] = srcBuffer.get(offset + i);
                }
                dstBufferedOutputStream.write(cipher.doFinal(dst));
            }

        }

        dstBufferedOutputStream.flush();
        dstFileOutputStream.flush();
        dstBufferedOutputStream.close();
        dstFileOutputStream.close();
        srcBuffer.clear();
        srcChannel.close();
        srcRaf.close();
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
