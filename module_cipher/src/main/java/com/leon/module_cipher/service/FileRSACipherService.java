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
    // public final String SIGNATURE_ALGORITHM = "MD5withRSA";
    protected final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    /**
     * 获取公钥的key
     */
    protected final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * 读写缓冲大小
     * */
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

        System.out.println("publicKey：" + Base64.encode(publicKey.getEncoded()));
        System.out.println("privateKey：" + Base64.encode(privateKey.getEncoded()));

        return keyMap;
    }


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
     * 对已加密数据进行签名
     *
     * @param path       文件路径
     * @return 对已加密数据生成的签名
     * @throws Exception
     */

    public String sign(String path) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign((PrivateKey) generatePrivateKey(KEY_ALGORITHM));
        RandomAccessFile randomAccessFile = new RandomAccessFile(path,"r");
        long totalLength = randomAccessFile.length();
        randomAccessFile.close();
        FileInputStream fileInputStream = new FileInputStream(path);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream,bufferSize);
        byte[] bufferedBytes = new byte[bufferSize];
        for (int offset = 0; offset < totalLength; offset += bufferSize) {
            if (totalLength - offset >= bufferSize) {
                if (bufferedInputStream.read(bufferedBytes) != -1){
                    signature.update(bufferedBytes);
                }
            } else {
                byte[] dst = new byte[(int) (totalLength - offset)] ;
                if (bufferedInputStream.read(dst) != -1){
                    signature.update(dst);
                }
            }

        }
        return Base64.encode(signature.sign());
    }


    /**
     * 验签
     *
     * @param path      需要验签的文件
     * @param sign      文件签名
     * @return 验签是否成功
     * @throws Exception
     */
    public boolean verify(String path, String sign) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify((PublicKey) generatePublicKey(KEY_ALGORITHM));
        RandomAccessFile randomAccessFile = new RandomAccessFile(path,"r");
        long totalLength = randomAccessFile.length();
        randomAccessFile.close();
        FileInputStream fileInputStream = new FileInputStream(path);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream,bufferSize);
        byte[] bufferedBytes = new byte[bufferSize];
        for (int offset = 0; offset < totalLength; offset += bufferSize) {
            if (totalLength - offset >= bufferSize) {
                if (bufferedInputStream.read(bufferedBytes) != -1){
                    signature.update(bufferedBytes);
                }
            } else {
                byte[] dst = new byte[(int) (totalLength - offset)] ;
                if (bufferedInputStream.read(dst) != -1){
                    signature.update(dst);
                }
            }

        }
        return signature.verify(Base64.decode(sign));
    }


    /**
     * 公钥解密
     *
     * @param srcFile   使用私钥加密过的数据
     * @param dstFile   解密后文件存放位置
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
     * @param srcFile    使用公钥加密过的数据
     * @param dstFile    解密后文件路径
     * @return 解密后的数据
     * @throws Exception
     */
    public void decryptByPrivateKey(String srcFile, String dstFile) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, generatePrivateKey(KEY_ALGORITHM));
        LogUtils.d(cipher.getAlgorithm());
        exeFileOperate(cipher, srcFile, dstFile, MAX_DECRYPT_BLOCK);
    }

//
//    public void decryptByPrivateKey() throws Exception {
//        decryptByPrivateKey(srcPath,dstPath);
//    }
//
//
//
//    public void encryptByPrivateKey() throws Exception {
//        encryptByPrivateKey(srcPath,dstPath);
//    }


    /**
     * 公钥加密
     *
     * @param srcFile      需要加密的数据
     * @param dstFile      加密后文件
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
     * @param srcFile       待加密的数据
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
    protected void exeFileOperate(Cipher cipher, String srcFile, String dstFile, int maxBlockSize) throws Exception {
        FileOutputStream dstFileOutputStream = new FileOutputStream(dstFile);
        BufferedOutputStream dstBufferedOutputStream = new BufferedOutputStream(dstFileOutputStream,8096);

        RandomAccessFile srcRaf = new RandomAccessFile(srcFile, "r");
        long srcTotalLenght = srcRaf.length();
        FileChannel srcChannel = srcRaf.getChannel();

        long multiples = srcTotalLenght / maxBlockSize;
        long remainder = srcTotalLenght % maxBlockSize;
        LogUtils.d("multiples : " + multiples + " remainder : " + remainder + " maxBlockSize : " + maxBlockSize + " srcTotalLenght : " + srcTotalLenght);
        MappedByteBuffer srcBuffer = srcChannel.map(
                FileChannel.MapMode.READ_ONLY, 0, srcTotalLenght);
        byte[] bytes = new byte[maxBlockSize];
        //先对整除部分解密
        for (int offset = 0; offset < srcTotalLenght; offset += maxBlockSize) {
            if (srcTotalLenght - offset >= maxBlockSize) {
                for (int i = 0; i < maxBlockSize; i++){
                    bytes[i] = srcBuffer.get(offset + i);
                }
                dstBufferedOutputStream.write(cipher.doFinal(bytes));
            } else {
                byte[] dst = new byte[(int) (srcTotalLenght - offset)] ;
                for (int i = 0; i < srcTotalLenght - offset; i++){
                    dst[i] = srcBuffer.get(offset + i);
                }
                dstBufferedOutputStream.write(cipher.doFinal(dst));
            }

        }
//        if (remainder > 0) {
//            byte[] lastBytes = new byte[(int) remainder];
//            srcBuffer.get(lastBytes, (int) multiples * maxBlockSize, (int) srcTotalLenght);
//            dstOutputStream.write(lastBytes);
//        }
//        dstOutputStream.close();
        dstBufferedOutputStream.flush();
        dstFileOutputStream.flush();
        dstBufferedOutputStream.close();
        dstFileOutputStream.close();
        srcBuffer.clear();
        srcChannel.close();
        srcRaf.close();
    }

    @Override
    public String getPublicKey() {
        return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp/oT0nmIotDHnGxQTW9LwqEROTaGzzGb6u1ky4/WlIcQhtwiNvRdFHOyPSdys+I5oLDkCb4TxcTSKfu6cCSHw3qCHAOjBNj/STx6tH+oqRvW5uOWI1Z8m4RvWDCPiH9jkl0sC/x7HxrwBjcqhj8xqdcTTTcCOloUDfiDIWCLlLAM58GtuEzJrcRq7zusKFbp2R5OnH56MrY1sXBr6yKtHvWlTwpyUEUK6MpA2xILojC1aaOXCvQxdrVXrR0wJjKvqnXMTm5cg/2wNqmnpF5bYZ3UctEa+/ESQOK971ocnfWbEQa/rKQ2vbj421PRTDu0Zsy2krkegcpgr20tG7W6uwIDAQAB";
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
        return "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCn+hPSeYii0MecbFBNb0vCoRE5NobPMZvq7WTLj9aUhxCG3CI29F0Uc7I9J3Kz4jmgsOQJvhPFxNIp+7pwJIfDeoIcA6ME2P9JPHq0f6ipG9bm45YjVnybhG9YMI+If2OSXSwL/HsfGvAGNyqGPzGp1xNNNwI6WhQN+IMhYIuUsAznwa24TMmtxGrvO6woVunZHk6cfnoytjWxcGvrIq0e9aVPCnJQRQroykDbEguiMLVpo5cK9DF2tVetHTAmMq+qdcxOblyD/bA2qaekXlthndRy0Rr78RJA4r3vWhyd9ZsRBr+spDa9uPjbU9FMO7RmzLaSuR6BymCvbS0btbq7AgMBAAECggEAJefL81IToFImyUiz8lVsLjZ0kYmZcEYMnj4F4d3KmWfFNfmVCvunyYa2MRCm1Kr3QCjKvNwU0CszFeBFWSsVK+qIU9QKRHzHVvp74iFwqQCsRRwUdFL++YUwLyF+Mlo02QGHgAjwq7tDcNOgF/1Isr/jDiicqxhKsEBlLvH/bYTR3cb2mXias+jf13p/R9oQaoD2wJMgE5whkJRO/EFdWovVjVNWAxhxihVnVthbWyvKkJ4iggxPI5W5GhGMMtSjQUanKReiwxYZfJ9LctOL2oDMrKpdJCfejrCGm3kHbgf1Qk9HfgWUYMI4gndM2jU2g96ilvx3hZtrJE1BkL/L8QKBgQD2EqBZ5nBfjSeFwMIXknDWIpLENqibj959kEBaYAS7IUWhlwld9q8Wl+buNYlg6avO7+upnBibme1I4O7U47fpw/ERV0iwBBIXxcD14DDG6bsHsZGiqNp9v00b/nAnHxyeGNPqjiRoMpjPq0u9KSqg71mGCkrEoqkn1cJvehyCMwKBgQCuwOXGIOm5GtI/3QZVYU3zXiuNJAqnhm7eWwGDAHTdyqL6cBCri/e+7Bv0Ubkqnc6YH2HFyiMtT5W5q3iPfsYSHlzXFN9LEMqcFLxyyjO7CNR2xGo/LLqMA7iKw0X208yWzOEUfIbcyKuSghFVhqakAUnGlX/O54JI1RcWXPCtWQKBgGRDHKDkut7D9dmSu8K9AUrO1XwrRTH1QdNphj8MpoDUwrlfgAqQK7EJ/acLm0kojL+JlD0MhB5KbptmQwntKw+NrdjB9rT5Q6H6CnBkg5tYusMcv9lF8ZiQ7Ms+NdZK3+8u8JG+jukBzK+4/Og7MMpYG+L4qJRMLp4zg8DGQX1vAoGAC5arJhpkpWvL6YREuUC8pJjoFnH/auCVVZV/YiqTlWmsqSLpWBbuFPEfYXJR7yuL2barCyy+bHDMowVl904W4Hi2+2Rc8mNorDUL0ijqiUqqnD2mNg5HSKWuhTUx3I5rEM0BWBRx9Q4xooORlrRZNKMJO2wKj7P2hdHruAXtx/ECgYEApQ+kSkr45hbbmTn4bxYy2VM7WFiUigGLT/5K8q6IEL3GZ/ovzVW4VxGxoz7rBtJ0lT1uNw3ZkJ9vXHpdUy//+Hl0nnFNG/j/o0QZ/HXQh70dhRW0WUGF/ksBwPQ7EH5gIRB+Qk2UAibp8tinuVIDrOokwlEQ88yocHdzWdXHRgQ=";
    }


    /**
     * 用于加解密进度的监听器
     */
    public interface CipherListener {
        void onProgress(long current, long total);
    }
}
