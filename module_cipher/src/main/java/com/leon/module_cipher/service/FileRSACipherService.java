package com.leon.module_cipher.service;

import com.apkfuns.logutils.LogUtils;
import com.leon.module_cipher.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * @time:2019/10/18 14:14
 * @author:Leon
 * @description:
 */
public class FileRSACipherService implements ICipherService {

    protected String privateKey;
    protected String publicKey;

    public FileRSACipherService(String privateKey, String publicKey) {
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
     * 获取公钥的key
     */
    protected final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * 读写缓冲大小
     */
    protected int bufferSize = 8096;
    /**
     * 获取私钥的key
     */
    protected final String PRIVATE_KEY = "RSAPrivateKey";


    /**
     * RSA最大加密明文大小
     */
    protected final int MAX_ENCRYPT_BLOCK = 117;


    /**
     * RSA最大解密密文大小
     */
    protected final int MAX_DECRYPT_BLOCK = 256;


    /**
     * @param keySize 生成的秘钥长度  一般为1024或2048
     * @return
     * @throws Exception
     */
    public Map<String, Object> genKeyPair(int keySize) throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(keySize);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);

        return keyMap;
    }

    /**
     * 对已加密数据进行签名
     *
     * @param path 文件路径
     * @return 对已加密数据生成的签名
     */
    public String sign(String path) {
        try (
                RandomAccessFile randomAccessFile = new RandomAccessFile(path, "r");
                FileInputStream fileInputStream = new FileInputStream(path);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, bufferSize);
        ) {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign((PrivateKey) generatePrivateKey(KEY_ALGORITHM));

            long totalLength = randomAccessFile.length();
            randomAccessFile.close();

            byte[] bufferedBytes = new byte[bufferSize];
            for (int offset = 0; offset < totalLength; offset += bufferSize) {
                if (totalLength - offset >= bufferSize) {
                    if (bufferedInputStream.read(bufferedBytes) != -1) {
                        signature.update(bufferedBytes);
                    }
                } else {
                    byte[] dst = new byte[(int) (totalLength - offset)];
                    if (bufferedInputStream.read(dst) != -1) {
                        signature.update(dst);
                    }
                }

            }
            return Base64.encode(signature.sign());
        } catch (Exception e) {

        }
        return null;
    }


    /**
     * 验签
     *
     * @param path 需要验签的文件
     * @param sign 文件签名
     * @return 验签是否成功
     * @throws Exception
     */
    public boolean verify(String path, String sign) {
        try (
                RandomAccessFile randomAccessFile = new RandomAccessFile(path, "r");
                FileInputStream fileInputStream = new FileInputStream(path);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, bufferSize);
        ) {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify((PublicKey) generatePublicKey(KEY_ALGORITHM));
            long totalLength = randomAccessFile.length();
            randomAccessFile.close();

            byte[] bufferedBytes = new byte[bufferSize];
            for (int offset = 0; offset < totalLength; offset += bufferSize) {
                if (totalLength - offset >= bufferSize) {
                    if (bufferedInputStream.read(bufferedBytes) != -1) {
                        signature.update(bufferedBytes);
                    }
                } else {
                    byte[] dst = new byte[(int) (totalLength - offset)];
                    if (bufferedInputStream.read(dst) != -1) {
                        signature.update(dst);
                    }
                }
            }
            return signature.verify(Base64.decode(sign));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 公钥解密
     *
     * @param srcFile 使用私钥加密过的数据
     * @param dstFile 解密后文件存放位置
     * @return 解密后的数据
     * @throws Exception
     */
    public void decryptByPublicKey(String srcFile, String dstFile) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, generatePublicKey(KEY_ALGORITHM));
        exeFileOperate(cipher, srcFile, dstFile, MAX_DECRYPT_BLOCK);
    }

    /**
     * 用私钥对数据进行解密
     *
     * @param srcFile 使用公钥加密过的数据
     * @param dstFile 解密后文件路径
     * @return 解密后的数据
     * @throws Exception
     */
    public void decryptByPrivateKey(String srcFile, String dstFile) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, generatePrivateKey(KEY_ALGORITHM));
        LogUtils.d(cipher.getAlgorithm());
        exeFileOperate(cipher, srcFile, dstFile, MAX_DECRYPT_BLOCK);
    }

    /**
     * 公钥加密
     *
     * @param srcFile 需要加密的数据
     * @param dstFile 加密后文件
     * @return 使用公钥加密后的数据
     * @throws Exception
     */
    public void encryptByPublicKey(String srcFile, String dstFile) throws Exception {
        // 对数据加密
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, generatePublicKey(KEY_ALGORITHM));
        exeFileOperate(cipher, srcFile, dstFile, MAX_ENCRYPT_BLOCK);
    }

    /**
     * 私钥加密
     *
     * @param srcFile 待加密的数据
     * @return 使用私钥加密后的数据
     * @throws Exception
     */
    public void encryptByPrivateKey(String srcFile, String dstFile) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, generatePrivateKey(KEY_ALGORITHM));
        exeFileOperate(cipher, srcFile, dstFile, MAX_ENCRYPT_BLOCK);
    }


    /**
     * 执行文件操作
     */
    protected void exeFileOperate(Cipher cipher, String srcFile, String dstFile, int maxBlockSize) {
        try (
                FileOutputStream dstFileOutputStream = new FileOutputStream(dstFile);
                BufferedOutputStream dstBufferedOutputStream = new BufferedOutputStream(dstFileOutputStream, 8096);
                RandomAccessFile srcRaf = new RandomAccessFile(srcFile, "r");
                FileChannel srcChannel = srcRaf.getChannel();
        ) {

            long srcTotalLenght = srcRaf.length();

            long multiples = srcTotalLenght / maxBlockSize;
            long remainder = srcTotalLenght % maxBlockSize;
            LogUtils.d("multiples : " + multiples + " remainder : " + remainder + " maxBlockSize : " + maxBlockSize + " srcTotalLenght : " + srcTotalLenght);
            MappedByteBuffer srcBuffer = srcChannel.map(
                    FileChannel.MapMode.READ_ONLY, 0, srcTotalLenght);
            byte[] bytes = new byte[maxBlockSize];
            //先对整除部分解密
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
            srcBuffer.clear();
        } catch (Exception e) {

        }
    }

    @Override
    public String getPublicKey() {
        return publicKey;
    }


    /**
     * 获取私钥
     *
     * @param
     * @return
     * @throws Exception
     */
    @Override
    public String getPrivateKey() {
        return privateKey;
    }

}
