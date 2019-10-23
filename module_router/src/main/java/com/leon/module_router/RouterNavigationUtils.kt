package com.leon.module_router

import com.alibaba.android.arouter.launcher.ARouter

/**
 * @time:2019/9/6 15:42
 * @author:Leon
 * @description:
 */
object RouterNavigationUtils {
    /**
     * 去往module_a
     */
    fun goModuleAMain() {
        ARouter.getInstance().build(RouterUrls.MODULE_A_MAIN).navigation()
    }

    /**
     * 去往首页
     */
    fun goMainActivity() {
        ARouter.getInstance().build(RouterUrls.MAIN_HOME).navigation()
    }

    fun goChiper(){
        ARouter.getInstance().build(RouterUrls.MODULE_CIPHER_MAIN)
    }

    fun goRSACipher(){
        ARouter.getInstance().build(RouterUrls.MODULE_CIPHER_RSA).navigation()
    }
}