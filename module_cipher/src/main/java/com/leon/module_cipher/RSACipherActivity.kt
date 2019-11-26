package com.leon.module_cipher

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.apkfuns.logutils.LogUtils
import com.blankj.utilcode.util.FileUtils
import com.leon.common.base.BaseActivity
import com.leon.common.utils.SpUtils
import com.leon.common.utils.Uri2PathUtil
import com.leon.module_cipher.service.FileAESCipherService
import com.leon.module_cipher.service.FileRSACipherService
import com.leon.module_cipher.service.RSACipherService
import com.leon.module_router.RouterUrls
import kotlinx.android.synthetic.main.module_cipher_activity_rsa_cipher.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.uiThread
import java.lang.Exception
import java.security.KeyPairGenerator

@Route(path = RouterUrls.MODULE_CIPHER_RSA)
class RSACipherActivity : BaseActivity() {

    private val REQUEST_FILE_CODE: Int = 1000

    override val layoutId: Int = R.layout.module_cipher_activity_rsa_cipher

    private var currentFilePath = ""
    lateinit var fileRSACipherServer: FileRSACipherService
    lateinit var rsaCipherService: RSACipherService
    lateinit var fileAESCipherService: FileAESCipherService
    override fun initData(savedInstanceState: Bundle?) {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(1024)
        val keyPair = keyPairGenerator.generateKeyPair()
    }

    override fun initView(savedInstanceState: Bundle?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE
                ), 1000
            )
        }
        SpUtils.defaultSpName = "test"

        fileRSACipherServer =
            FileRSACipherService(etPrivateKey.text.toString(), etPrivateKey.text.toString())
        rsaCipherService =
            RSACipherService(etPrivateKey.text.toString(), etPublicKey.text.toString())

        chooseFileBtn.onClick {
            SpUtils.getInstance(application).put("test","ajghaughuahgiufguhgu")

            //            test()
            //val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("*/*")
            //val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_FILE_CODE)
            fileRSACipherServer =
                FileRSACipherService(etPrivateKey.text.toString(), etPrivateKey.text.toString())
            rsaCipherService =
                RSACipherService(etPrivateKey.text.toString(), etPublicKey.text.toString())
        }

        btnEncryptTest.onClick {
            LogUtils.d(etRsaTest.text.toString())
            LogUtils.d(Base64.encode(rsaCipherService.encryptByPrivateKey(etRsaTest.text.toString())))
        }

        btnEnByRsaPrivate.onClick {
            doAsync {
                val startTime = System.currentTimeMillis();
                fileRSACipherServer.encryptByPrivateKey(
                    currentFilePath,
                    currentFilePath + ".EnByRsaPrivate." + FileUtils.getFileExtension(currentFilePath)
                )
                val endTime = System.currentTimeMillis()
                uiThread {
                    log.append(
                        "私钥加密后文件路径(耗时${endTime - startTime})：${currentFilePath + ".EnByRsaPrivate." + FileUtils.getFileExtension(
                            currentFilePath
                        )} \n"
                    )
                }
            }
        }

        signByRsaPrivate.onClick {
            doAsync {
                val startTime = System.currentTimeMillis()
                val sign = fileRSACipherServer.sign(currentFilePath)
                val endTime = System.currentTimeMillis()
                uiThread {
                    log.append("原始文件私钥签名(耗时${endTime - startTime})：$sign  \n")
                }
            }
        }

        btnDeByRsaPublic.onClick {
            doAsync {
                val startTime = System.currentTimeMillis()
                fileRSACipherServer.decryptByPublicKey(
                    currentFilePath + ".EnByRsaPrivate." + FileUtils.getFileExtension(currentFilePath),
                    currentFilePath + ".DeByRsaPublic." + FileUtils.getFileExtension(currentFilePath)
                )
                val endTime = System.currentTimeMillis()
                uiThread {
                    log.append(
                        "私钥加密后公钥解密文件路径(耗时${endTime - startTime})：${currentFilePath + ".DeByRsaPublic." + FileUtils.getFileExtension(
                            currentFilePath
                        )} \n"
                    )
                }
            }
        }
        btnEnByRsaPublic.onClick {
            doAsync {
                val startTime = System.currentTimeMillis()
                fileRSACipherServer.encryptByPublicKey(
                    currentFilePath,
                    currentFilePath + ".EnByRsaPublic." + FileUtils.getFileExtension(currentFilePath)
                )
                val endTime = System.currentTimeMillis()
                uiThread {
                    log.append(
                        "公钥加密文件路径(耗时${endTime - startTime})：${currentFilePath + ".EnByRsaPublic." + FileUtils.getFileExtension(
                            currentFilePath
                        )} \n"
                    )
                }
            }
        }

        btnDeByRsaPrivate.onClick {
            doAsync {
                val startTime = System.currentTimeMillis()
                fileRSACipherServer.decryptByPrivateKey(
                    currentFilePath + ".EnByRsaPublic." + FileUtils.getFileExtension(currentFilePath),
                    currentFilePath + ".DeByRsaPrivate." + FileUtils.getFileExtension(currentFilePath)
                )
                val endTime = System.currentTimeMillis()
                uiThread {
                    log.append(
                        "公钥加密后私钥解密文件路径(耗时${endTime - startTime})：${currentFilePath + ".DeByRsaPrivate" + FileUtils.getFileExtension(
                            currentFilePath
                        )} \n"
                    )
                }
            }

        }
        verifyByRsa.onClick {
            doAsync {
                val startTime = System.currentTimeMillis()
                val sign = fileRSACipherServer.sign(currentFilePath)
                val prvSign = fileRSACipherServer.verify(
                    currentFilePath + ".DeByRsaPrivate." + FileUtils.getFileExtension(currentFilePath),
                    sign
                )
                val pubSign = fileRSACipherServer.verify(
                    currentFilePath + ".DeByRsaPublic." + FileUtils.getFileExtension(currentFilePath),
                    sign
                )
                val endTime = System.currentTimeMillis()
                uiThread {
                    log.append("私钥解密后签名验证(耗时${endTime - startTime})：$prvSign  公钥解密后文件签名验证：${pubSign} \n")
                }
            }
        }

        btnGetAesByRsaPublic.onClick {
            val startTime = System.currentTimeMillis()
            val aesKey = rsaCipherService.decryptByPublicKey(etAesEncryptByRsaKey.text.toString())
            val ivParameter =
                rsaCipherService.decryptByPublicKey(etAesKeyIvEncryptByRsa.text.toString())
            fileAESCipherService =
                FileAESCipherService(Base64.encode(aesKey), Base64.encode(ivParameter))
            val endTime = System.currentTimeMillis()
            log.append("Rsa公钥解密Aes密钥(耗时${endTime - startTime}) \n")
            LogUtils.d("AES Key : ${Base64.encode(aesKey)}")
            LogUtils.d("Iv parameter : ${Base64.encode(ivParameter)}")
        }

        btnAesEncryptFile.onClick {
            doAsync {
                val startTime = System.currentTimeMillis()
                fileAESCipherService.encrypt(
                    currentFilePath,
                    currentFilePath + ".EnByAes." + FileUtils.getFileExtension(currentFilePath)
                )
                val endTime = System.currentTimeMillis()
                uiThread {
                    log.append(
                        "Aes密钥加密文件路径(耗时${endTime - startTime})：${currentFilePath + ".EnByAes." + FileUtils.getFileExtension(
                            currentFilePath
                        )} \n"
                    )
                }
            }
        }

        btnAesDecryptFile.onClick {
            doAsync {
                val startTime = System.currentTimeMillis()
                fileAESCipherService.decrypt(
                    currentFilePath + ".EnByAes." + FileUtils.getFileExtension(currentFilePath),
                    currentFilePath + ".DeByAes." + FileUtils.getFileExtension(currentFilePath)
                )
                val endTime = System.currentTimeMillis()
                uiThread {
                    log.append(
                        "Aes密钥解密文件路径(耗时${endTime - startTime})：${currentFilePath + ".DeByAes." + FileUtils.getFileExtension(
                            currentFilePath
                        )} \n"
                    )
                }
            }
        }

        btnAesDecryptFileSignVerify.onClick {
            doAsync {
                val startTime = System.currentTimeMillis()
                val sign = fileRSACipherServer.sign(currentFilePath)
                val prvSign = fileRSACipherServer.verify(
                    currentFilePath + ".DeByAes." + FileUtils.getFileExtension(currentFilePath),
                    sign
                )
                val endTime = System.currentTimeMillis()

                uiThread {
                    log.append("AES解密后文件RSA签名校验(耗时${endTime - startTime})：$prvSign \n")
                }
            }
        }
    }

    override fun bindModel() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri = data?.getData()
            uri?.let {
                LogUtils.d(it)
                val path = Uri2PathUtil.getRealPathFromUri(this, it)
                currentFilePath = path
            }
        }
    }

    /*RSA 2048*/

    val publicKey =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAichGTEP0QFswnvn+ZAQrgGHM8VeDZLJuezGhgxh4d9SyRUfnIW/zefT71rwS4bZUs1MPxJwavOyxABJOHLuckdHXknCsGEWz78gsA6D0+O+9dl1gCZR29nnN/NlzmNbSjFnzvsTJYBlS88qSr35RXFE+6DM7uPsS8Fm2I+65FteJ8p2yMvpSg72QkIX8xvI1F1uwXrciIB+4u7uTozxIplMOo4a6uhAm3W+Kjpz3ni2btjGqHRbqb3ebSZyl+nFfnjQaBe3XyVxAWDSanjgFj/wbqbeug9FBs+nQFVPIZR9z0aE5Ndi5o3eSkV7HFmWpkxaiPZ0BLRK3XHMaBtuSpwIDAQAB"
    val privateKey =
        "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCJyEZMQ/RAWzCe+f5kBCuAYczxV4Nksm57MaGDGHh31LJFR+chb/N59PvWvBLhtlSzUw/EnBq87LEAEk4cu5yR0deScKwYRbPvyCwDoPT47712XWAJlHb2ec382XOY1tKMWfO+xMlgGVLzypKvflFcUT7oMzu4+xLwWbYj7rkW14nynbIy+lKDvZCQhfzG8jUXW7BetyIgH7i7u5OjPEimUw6jhrq6ECbdb4qOnPeeLZu2MaodFupvd5tJnKX6cV+eNBoF7dfJXEBYNJqeOAWP/Bupt66D0UGz6dAVU8hlH3PRoTk12Lmjd5KRXscWZamTFqI9nQEtErdccxoG25KnAgMBAAECggEBAIPz1b88ZTMtIgdejA7lH3Q4Nbn8gc1yRPSet3uBd/3rKT/IeMZBHQBzaqxgOgUIRV3n8nXsun6sf2b+IOjLlErimH2agnZMauL85YokH/g4QU6WZl9GXBf41xmMd3SsZ8AadaEBfYoXNqZcHtcLNogfFwvx5QRnD+A3SoRnH8OLBeVvOEe4AqHLT2xEZ9TeCf3fJe0Rf0fUIbw7I5ioiRZV/ir0L1VM7+1k2JODUkdC2Luj5Tl3nl1Eg6EmkYCmGE1bip1NAatsfjPBLMF7XdPNjLboiffjgKVBOjb7Y9vL18BCoLtWeTT2GkMpi5Sr94T1te1Ox77dF4BP33Xn7eECgYEA1TNUrAQsh14NbbkwFtUHXS8/YXt81p9wbSpFBymIawF2Lkk0913TB4CHSun45LhYXjdZZxK/TgqC5EIq5v2RA0jY3cSxoqVe6RZKB04E8wszeJHiEJPdu2vFnpZh9iAyhswiM5FmuKZKoWsVc2SZrBXAI02smSn3lXYok1VBS3sCgYEApXEZS6gjUu4o7ZL53Ur1HDfi/nxpkxqrPh+D1HVYjzjT+4vTeZwtLXt2VCInPWNXH+f11mzhxIrLkI0jMcSCah81DuU8aFXnqvPuyFvt9uaQBYlVWBtkcGZyeaxHFrbfCyeu0jm7SfwmiIg12hKlIHtPTjEZQUX+kkWr8cdaZ8UCgYEAh0Pl+K09QzVc97yC0jmeTnTnlYWvksvdnKUw3nZvYtSukndH75nLhfr524HOs+5xwnUDd+3hCjaJDSEd7yf5lUfmr+1XdoXNTb0igrfxU/JLWbfU4geuqnaaDyACTxHmfLePC4C413ZJ61fxaCDvjsrN+JgTZanGt0EcRT3WC3kCgYEAgf5/GMJxlw0JXbs515a5R8Xl9358Whj/at3KcRsPTeIiNqnkrc54dR9ol60KViMDZ0+VDDobn5pLXzZ26/jzXD1PLHgU4gp18Q6glhAdx/3cNm11gLhtUCA/XLlwVjm0wggZRpgUQIr/IBKe9c3mr8IUS2Uq6e38nKRf+adhst0CgYAM4tvl+U1MPbbz3YzDv8QPepZ7Pglgdfxqfr5OkXA7jNhqTZjSq10B6oClGvirBo1m6f26F02iUKk1n67AuiLlTP/RRZHi1cfq6P9IaXl23PcxJfUMvIxQDS0U+UTFpNXryTw/qNAkSfufN48YzKdGvc8vHrYJyaeemaVlbdJOCw=="


    fun test(){

        /*RSA  1024 */
//        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIarYvrIMZGHKa8f2E6ubg0//28R1zJ4ArD+XELXYvDrM8UBR42PqJCpjPN3hC91YAnnk2Y9U+X5o/rGxH5ZTZzYy+rkAmZFJa1fK2mWDxPYJoxH+DGHQc+h8t83BMB4pKqVPhcJVF6Ie+qpD5RFUU/e5iEz8ZZFDroVE3ubKaKwIDAQAB";
//        String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIhqti+sgxkYcprx/YTq5uDT//bxHXMngCsP5cQtdi8OszxQFHjY+okKmM83eEL3VgCeeTZj1T5fmj+sbEfllNnNjL6uQCZkUlrV8raZYPE9gmjEf4MYdBz6Hy3zcEwHikqpU+FwlUXoh76qkPlEVRT97mITPxlkUOuhUTe5sporAgMBAAECgYA0aSND37iifKUTaKOpXIKFoI23910EMAnrAXmaTIkafUBZjL7Ay0Q+QIcDHeGjgNlW9YvGXMbB5wMhMYKMgOUV1FpeqQdDslO4Z7zynRjkDJkjOKkE2/j10CvmNO8e2uCWKsYYUE9IyTkxcypjBCv16ifT0qmdxb7uKLccYI16eQJBANMutfNO/q7kUKiYvilBLN9+pZOg6eTmKmV0Xygoa3ClpQTfurwLA8W/Fv3oXnjHXTryNVHeoxSH69imo0RZ9kcCQQClXhMbXlfvl5iInmwziFhtYBztvkLuyQ084FgszR7iR0nuOWoURLQa5O7sLL724FNRlSvOCmmmWguh2vmQgRr9AkBDS5tHkWCvMqpRT3spgk9eWOlChgCCpKXV9qNsFJVILEDNsM28pnXpSd91wdp4+m7HHe/Hyv6EyFtrio50dYZ5AkAODVVwUO8GBArJKTUml+JzwOQUa8OCSQFf9+xmOjPypH4qySQzfrcTRfrrhM3haqSJ3TQwuP/LTAGLCnGEjwP9AkBqFFyrrQviPOhwel3NWjRv8mftOFgnm0Isk/NQJ4JtoahYvPDeUyP80WSuVWnPyV4zHz9Kw7BggYCPc4xZDACV";

        try {
            log.text = ""
            val data = etRsaTest.text.toString()

            val publicEncryptBytes = RSAUtils.encryptByPublicKey(data.toByteArray(), publicKey)
            println("公钥加密后的数据：" + Base64.encode(publicEncryptBytes))
            log.append("公钥加密后的数据：" + Base64.encode(publicEncryptBytes) + "\n")
            val privatDecryptBytes = RSAUtils.decryptByPrivateKey(publicEncryptBytes, privateKey)
            println("私钥解密后的数据：" + String(privatDecryptBytes))
            log.append("私钥解密后的数据：" + String(privatDecryptBytes) + "\n")


            println("--------------------")
            log.append("--------------------\n")

            val privateKeyEncryptBytes =
                RSAUtils.encryptByPrivateKey(data.toByteArray(), privateKey)
            println("私钥加密后的数据：" + Base64.encode(privateKeyEncryptBytes))
            log.append("私钥加密后的数据：" + Base64.encode(privateKeyEncryptBytes) + "\n")

            val singnData = RSAUtils.sign(data.toByteArray(), privateKey)
            println("私钥签名后的数据：" + singnData!!)
            log.append("私钥签名后的数据：" + singnData + "\n")


            val publicDecryptBytes = RSAUtils.decryptByPublicKey(privateKeyEncryptBytes, publicKey)
            println("公钥解密后的数据：" + String(publicDecryptBytes))
            log.append("公钥解密后的数据：" + String(publicDecryptBytes) + "\n")

            val isSign = RSAUtils.verify(data.toByteArray(), publicKey, singnData)
            println("签名是否正确：$isSign")
            log.append("签名是否正确：$isSign")

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}

