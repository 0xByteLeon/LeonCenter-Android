package com.leon.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.lang.IllegalArgumentException
/**
 * @time:2019/7/24 11:01
 * @author:Leon
 * @description:
 */
object FileUtils {
    private val TAG = javaClass.name
    /**
     * 分享文件至其他App
     * @param file 要分享的文件
     * @param authority Android N 以上系统分享文件需要FileProvider
     * @return 分享成功返回true 失败false
     * */
    @JvmStatic
    fun shareFile(context: Context, file: File, authority: String?): Boolean {
        val shareIntent = Intent(Intent.ACTION_SEND)
        var uri: Uri

        if (!file.exists()) {
            Log.e(TAG,"file not exists!")
            return false
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file)
        } else {
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            uri = FileProvider.getUriForFile(
                context,
                authority ?: throw IllegalArgumentException("请输入正确的 FileProvider authority : $authority"),
                file
            )
            context.grantUriPermission(
                context.packageName, uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            context.grantUriPermission(
                context.packageName, uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        val contentResolver = context.contentResolver
        val fileMimeType = contentResolver.getType(uri)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = fileMimeType
        context.startActivity(shareIntent)
        return true
    }
}