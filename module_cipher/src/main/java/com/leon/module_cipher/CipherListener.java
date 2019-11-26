package com.leon.module_cipher;

/**
 * @time:2019/10/30 11:01
 * @author:Leon
 * @description: 用于加解密进度的监听器
 */
interface CipherListener {
    /**
     * @param opmode  cipher 工作模式，加密或解密
     * @param total   文件总大小
     * @param current 进度
     */
    void onProgress(int opmode, String cipherAlgorithm, long current, long total);
}
