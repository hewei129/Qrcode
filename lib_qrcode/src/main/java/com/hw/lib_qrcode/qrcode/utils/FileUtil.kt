package com.hw.lib_qrcode.qrcode.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.random.Random.Default.nextInt


/**
 * @author hewei(David)
 * @date 2020/12/16  2:01 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

//fun getRealPathFromURI(context: Context, contentUri: Uri?): File? {
//    var file: File? = null
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        if (contentUri?.scheme == ContentResolver.SCHEME_FILE)
//            file = File(requireNotNull(contentUri.path))
//        else if (contentUri?.scheme == ContentResolver.SCHEME_CONTENT) {
//            //把文件保存到沙盒
//            val contentResolver = context.contentResolver
//            val displayName = run {
//                val cursor = contentResolver.query(contentUri, null, null, null, null)
//                cursor?.let {
//                    if (it.moveToFirst())
//                        it.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
//                    else null
//                }
//            } ?: "${System.currentTimeMillis()}${nextInt(0, 9999)}.${MimeTypeMap.getSingleton()
//                .getExtensionFromMimeType(contentResolver.getType(contentUri))}"
//
//            val ios = contentResolver.openInputStream(contentUri)
//            if (ios != null) {
//                file = File("${context.externalCacheDir!!.absolutePath}/$displayName")
//                    .apply {
//                        val fos = FileOutputStream(this)
//                        FileUtils.copy(ios, fos)
//                        fos.close()
//                        ios.close()
//                    }
//            }
//        }
//    } else {
//        var res: String? = null
//        val proj = arrayOf<String>(MediaStore.Images.Media.DATA)
//        val cursor: Cursor? =
//            contentUri?.let { context.contentResolver.query(it, proj, null, null, null) }
//        if (cursor?.moveToFirst() == true) {
//            val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            res = cursor.getString(columnIndex)
//        }
//        cursor?.close()
//        file = File(res)
//    }
//    return file
//
//}

/**
 * android7.0以上处理方法
 */
private fun getFilePathForN(
    context: Context,
    uri: Uri
): String? {
    try {
        val returnCursor =
            context.contentResolver.query(uri, null, null, null, null)
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val file = File(context.filesDir, name)
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        var read = 0
        val maxBufferSize = 1 * 1024 * 1024
        val bytesAvailable: Int? = inputStream?.available()
        val bufferSize = bytesAvailable?.coerceAtMost(maxBufferSize)
        val buffers = bufferSize?.let { ByteArray(it) }
        while (inputStream?.read(buffers).also {
                if (it != null) {
                    read = it
                }
            } != -1) {
            outputStream.write(buffers, 0, read)
        }
        returnCursor.close()
        inputStream?.close()
        outputStream.close()
        return file.path
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

/**
 * 全平台处理方法
 */
@Throws(Exception::class)
fun getPathByUri(context: Context, uri: Uri): String? {
    val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    val isN = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    if (isN) {
        return getFilePathForN(context, uri)
    }

    // DocumentProvider
    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
        } else if (isDownloadsDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), id.toLong()
            )
            return getDataColumn(context, contentUri, null, null)
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(
                split[1]
            )
            return getDataColumn(context, contentUri, selection, selectionArgs)
        }
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {
        return getDataColumn(context, uri, null, null)
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}

/**
 * 获取此Uri的数据列的值。这对于MediaStore uri和其他基于文件的内容提供程序非常有用。
 */
fun getDataColumn(
    context: Context, uri: Uri?, selection: String?,
    selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val projection =
        arrayOf(MediaStore.Images.ImageColumns.DATA)
    try {
        cursor = context.contentResolver.query(
            uri!!, projection, selection, selectionArgs,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)
            return cursor.getString(column_index)
        }
    } catch (e: IllegalArgumentException) {
        //do nothing
    } finally {
        cursor?.close()
    }
    return null
}

fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}
