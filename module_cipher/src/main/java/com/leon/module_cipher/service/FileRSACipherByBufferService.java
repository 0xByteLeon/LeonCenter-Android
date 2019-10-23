//package com.leon.module_cipher.service;
//
//import com.apkfuns.logutils.LogUtils;
//import com.leon.module_cipher.Base64;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.RandomAccessFile;
//import java.security.KeyFactory;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.security.Signature;
//import java.security.spec.PKCS8EncodedKeySpec;
//
//import javax.crypto.Cipher;
//
///**
// * @time:2019/10/18 14:14
// * @author:Leon
// * @description:
// */
//public class FileRSACipherByBufferService extends FileRSACipherService {
//
//    public FileRSACipherByBufferService(String srcFile, String dstFile) {
//        super(srcFile, dstFile);
//    }
//
//    /**
//     * 加密算法RSA
//     */
//    private final String KEY_ALGORITHM = "RSA";
//    private final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
//
//    /**
//     * 签名算法
//     */
//    // public final String SIGNATURE_ALGORITHM = "MD5withRSA";
//    private final String SIGNATURE_ALGORITHM = "SHA256withRSA";
//
//    /**
//     * 获取公钥的key
//     */
//    private final String PUBLIC_KEY = "RSAPublicKey";
//
//    /**
//     * 读写缓冲大小
//     * */
//    private int bufferSize = 8096;
//    /**
//     * 获取私钥的key
//     */
//    private final String PRIVATE_KEY = "RSAPrivateKey";
//
//
//    /**
//     * RSA最大加密明文大小
//     */
//    private final int MAX_ENCRYPT_BLOCK = 117;
//
//
//    /**
//     * RSA最大解密密文大小
//     */
//    private final int MAX_DECRYPT_BLOCK = 256;
//
//
//
//    /**
//     * 对已加密数据进行签名
//     *
//     * @param data       已加密的数据
//     * @param privateKey 私钥
//     * @return 对已加密数据生成的签名
//     * @throws Exception
//     */
//
//    public String sign(byte[] data, String privateKey) throws Exception {
//        byte[] keyBytes = Base64.decode(privateKey);
//        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
//        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
//        signature.initSign(privateK);
//        signature.update(data);
//        return Base64.encode(signature.sign());
//    }
//
//    /**
//     * 对已加密数据进行签名
//     *
//     * @param path       文件路径
//     * @return 对已加密数据生成的签名
//     * @throws Exception
//     */
//
//    public String sign(String path) throws Exception {
//        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
//        signature.initSign((PrivateKey) generatePrivateKey(KEY_ALGORITHM));
//        RandomAccessFile randomAccessFile = new RandomAccessFile(path,"r");
//        long totalLength = randomAccessFile.length();
//        randomAccessFile.close();
//        FileInputStream fileInputStream = new FileInputStream(path);
//        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream,bufferSize);
//        byte[] bufferedBytes = new byte[bufferSize];
//        for (int offset = 0; offset < totalLength; offset += bufferSize) {
//            if (totalLength - offset >= bufferSize) {
//                if (bufferedInputStream.read(bufferedBytes) != -1){
//                    signature.update(bufferedBytes);
//                }
//            } else {
//                byte[] dst = new byte[(int) (totalLength - offset)] ;
//                if (bufferedInputStream.read(dst) != -1){
//                    signature.update(dst);
//                }
//            }
//
//        }
//        return Base64.encode(signature.sign());
//    }
//
//
//    /**
//     * 验签
//     *
//     * @param path      需要验签的文件
//     * @param sign      文件签名
//     * @return 验签是否成功
//     * @throws Exception
//     */
//    public boolean verify(String path, String sign) throws Exception {
//        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
//        signature.initVerify((PublicKey) generatePublicKey(KEY_ALGORITHM));
//        RandomAccessFile randomAccessFile = new RandomAccessFile(path,"r");
//        long totalLength = randomAccessFile.length();
//        randomAccessFile.close();
//        FileInputStream fileInputStream = new FileInputStream(path);
//        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream,bufferSize);
//        byte[] bufferedBytes = new byte[bufferSize];
//        for (int offset = 0; offset < totalLength; offset += bufferSize) {
//            if (totalLength - offset >= bufferSize) {
//                if (bufferedInputStream.read(bufferedBytes) != -1){
//                    signature.update(bufferedBytes);
//                }
//            } else {
//                byte[] dst = new byte[(int) (totalLength - offset)] ;
//                if (bufferedInputStream.read(dst) != -1){
//                    signature.update(dst);
//                }
//            }
//
//        }
//        return signature.verify(Base64.decode(sign));
//    }
//
//
//    /**
//     * 公钥解密
//     *
//     * @param srcFile   使用私钥加密过的数据
//     * @param dstFile   解密后文件存放位置
//     * @return 解密后的数据
//     * @throws Exception
//     */
//    public void decryptByPublicKey(String srcFile, String dstFile) throws Exception {
//        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
//        cipher.init(Cipher.DECRYPT_MODE, generatePublicKey(KEY_ALGORITHM));
//        exeFileOperate(cipher, srcFile, dstFile, MAX_DECRYPT_BLOCK);
//    }
//
//    /**
//     * 用私钥对数据进行解密
//     *
//     * @param srcFile    使用公钥加密过的数据
//     * @param dstFile    解密后文件路径
//     * @return 解密后的数据
//     * @throws Exception
//     */
//    public void decryptByPrivateKey(String srcFile, String dstFile) throws Exception {
//        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
//        cipher.init(Cipher.DECRYPT_MODE, generatePrivateKey(KEY_ALGORITHM));
//        LogUtils.d(cipher.getAlgorithm());
//        exeFileOperate(cipher, srcFile, dstFile, MAX_DECRYPT_BLOCK);
//    }
//
//
////    public void decryptByPrivateKey() throws Exception {
////        decryptByPrivateKey(srcPath,dstPath);
////    }
////
////
////
////    public void encryptByPrivateKey() throws Exception {
////        encryptByPrivateKey(srcPath,dstPath);
////    }
//
//    /**
//     * 执行文件操作
//     */
//    @Override
//    protected void exeFileOperate(Cipher cipher, String srcFile, String dstFile, int maxBlockSize) throws Exception {
//        FileOutputStream dstFileOutputStream = new FileOutputStream(dstFile);
//        BufferedOutputStream dstBufferedOutputStream = new BufferedOutputStream(dstFileOutputStream,8096);
//        RandomAccessFile srcRaf = new RandomAccessFile(srcFile, "r");
//        long srcTotalLenght = srcRaf.length();
//        FileInputStream srcFileInputStream = new FileInputStream(srcFile);
//        BufferedInputStream srcBufferedInputStream = new BufferedInputStream(srcFileInputStream,bufferSize);
//
//        byte[] bytes = new byte[maxBlockSize];
//        //先对整除部分解密
//        for (int offset = 0; offset < srcTotalLenght; offset += maxBlockSize) {
//            if (srcTotalLenght - offset >= maxBlockSize) {
//                if (srcBufferedInputStream.read(bytes) != -1){
//                    dstBufferedOutputStream.write(cipher.doFinal(bytes));
//                }
//            } else {
//                byte[] dst = new byte[(int) (srcTotalLenght - offset)] ;
//                if (srcBufferedInputStream.read(dst) != -1){
//                    dstBufferedOutputStream.write(cipher.doFinal(dst));
//                }
//            }
//
//        }
//
//        dstBufferedOutputStream.flush();
//        dstFileOutputStream.flush();
//        dstBufferedOutputStream.close();
//        dstFileOutputStream.close();
//        srcBufferedInputStream.close();
//        srcFileInputStream.close();
//        srcRaf.close();
//    }
//
//    /**
//     * 用于加解密进度的监听器
//     */
//    public interface CipherListener {
//        void onProgress(long current, long total);
//    }
//}
