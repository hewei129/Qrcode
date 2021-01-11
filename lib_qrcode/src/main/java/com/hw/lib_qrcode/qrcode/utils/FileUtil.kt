package com.hw.lib_qrcode.qrcode.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.FileUtils
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.random.Random.Default.nextInt


/**
 * @author hewei(David)
 * @date 2020/12/16  2:01 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

fun getRealPathFromURI(context: Context, contentUri: Uri?): File? {
    var file : File ?= null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        if (contentUri?.scheme == ContentResolver.SCHEME_FILE)
            file = File(requireNotNull(contentUri.path))
        else if (contentUri?.scheme == ContentResolver.SCHEME_CONTENT) {
            //把文件保存到沙盒
            val contentResolver = context.contentResolver
            val displayName = run {
                val cursor = contentResolver.query(contentUri, null, null, null, null)
                cursor?.let {
                    if (it.moveToFirst())
                        it.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    else null
                }
            } ?: "${System.currentTimeMillis()}${nextInt(0, 9999)}.${MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(contentResolver.getType(contentUri))}"

            val ios = contentResolver.openInputStream(contentUri)
            if (ios != null) {
                file = File("${context.externalCacheDir!!.absolutePath}/$displayName")
                    .apply {
                        val fos = FileOutputStream(this)
                        FileUtils.copy(ios, fos)
                        fos.close()
                        ios.close()
                    }
            }
        }
    }else{
        var res: String? = null
        val proj = arrayOf<String>(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentUri?.let { context.contentResolver.query(it, proj, null, null, null) }
        if (cursor?.moveToFirst() == true) {
            val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(columnIndex)
        }
        cursor?.close()
        file = File(res)
    }
    return file

}