package com.hw.lib_qrcode.qrcode.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.*
import android.net.Uri
import android.provider.MediaStore
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.IOException
import java.util.*


/**
 * @author hewei(David)
 * @date 2020/12/28  5:21 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */
object QrCodeUtil {
    const val DEFAULTE_SIZE = 500
    /**
     * 生成二维码
     *
     * @param text 需要生成二维码的文字、网址等
     * @param size 需要生成二维码的大小（）
     * @return bitmap
     */
    /**
     * 生成二维码，默认大小为50500
     *
     * @param text 需要生成二维码的文字、网址等
     * @return bitmap
     */
    @JvmOverloads
    fun createQRCode(text: String?, size: Int = DEFAULTE_SIZE): Bitmap? {
        return try {
            val hints =
                Hashtable<EncodeHintType, Any?>()
            hints[EncodeHintType.CHARACTER_SET] = "utf-8"
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            hints[EncodeHintType.MARGIN] = 1
            val bitMatrix = QRCodeWriter().encode(
                text,
                BarcodeFormat.QR_CODE, size, size, hints
            )
            val pixels = IntArray(size * size)
            for (y in 0 until size) {
                for (x in 0 until size) {
                    if (bitMatrix[x, y]) {
                        pixels[y * size + x] = -0x1000000
                    } else {
                        pixels[y * size + x] = -0x1
                    }
                }
            }
            val bitmap = Bitmap.createBitmap(
                size, size,
                Bitmap.Config.ARGB_8888
            )
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    fun createQRCode(text: String?, size: Int = DEFAULTE_SIZE, color: Int): Bitmap? {
        return try {
            val hints =
                Hashtable<EncodeHintType, Any?>()
            hints[EncodeHintType.CHARACTER_SET] = "utf-8"
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            hints[EncodeHintType.MARGIN] = 1
            val bitMatrix = QRCodeWriter().encode(
                text,
                BarcodeFormat.QR_CODE, size, size, hints
            )
            val pixels = IntArray(size * size)
            for (y in 0 until size) {
                for (x in 0 until size) {
                    if (bitMatrix[x, y]) {
                        pixels[y * size + x] = color
                    } else {
                        pixels[y * size + x] = -0x1
                    }
                }
            }
            val bitmap = Bitmap.createBitmap(
                size, size,
                Bitmap.Config.ARGB_8888
            )
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    /**生成带logo 二维码
     * @param text  文字
     * @param logo   logo
     * @return
     */
    fun createQRcodeWithLogo(text: String?, logo: Bitmap?): Bitmap? {
        return createQRcodeWithLogo(
            text,
            DEFAULTE_SIZE,
            logo,
            DEFAULTE_SIZE / 5,
            DEFAULTE_SIZE / 5,
            0f,
            0f
        )
    }

    /** 生成带logo 二维码
     * @param text  文字
     * @param size   二维码大小 1 ：1
     * @param logo   logo
     * @param logoWith logo宽
     * @param logoHigh  logo高
     * @param logoRaduisX  logo x圆角
     * @param logoRaduisY  logo y圆角
     * @return
     */
    fun createQRcodeWithLogo(
        text: String?,
        size: Int,
        logo: Bitmap?,
        logoWith: Int,
        logoHigh: Int,
        logoRaduisX: Float,
        logoRaduisY: Float
    ): Bitmap? {
        return try {
            val hints =
                Hashtable<EncodeHintType, Any?>()
            hints[EncodeHintType.CHARACTER_SET] = "utf-8"
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            hints[EncodeHintType.MARGIN] = 1
            val bitMatrix = QRCodeWriter().encode(
                text,
                BarcodeFormat.QR_CODE, size, size, hints
            )
            val pixels = IntArray(size * size)
            for (y in 0 until size) {
                for (x in 0 until size) {
                    if (bitMatrix[x, y]) {
                        pixels[y * size + x] = -0x1000000
                    } else {
                        pixels[y * size + x] = -0x1
                    }
                }
            }
            val bitmap = Bitmap.createBitmap(
                size, size,
                Bitmap.Config.ARGB_8888
            )
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
            logo?.let { addLogo(bitmap, it, logoWith, logoHigh, logoRaduisX, logoRaduisY) }
                ?: bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 在二维码中间添加Logo图案
     * @param src         原图
     * @param logo        logo
     * @param logoWith     添加logo的宽度
     * @param logoHigh     添加logo的高度
     * @param logoRaduisX  logo圆角
     * @param logoRaduisY  logo圆角
     * @return
     */
    @SuppressLint("NewApi")
    fun addLogo(
        src: Bitmap?,
        logo: Bitmap?,
        logoWith: Int,
        logoHigh: Int,
        logoRaduisX: Float,
        logoRaduisY: Float
    ): Bitmap? {
        if (src == null) {
            return null
        }
        if (logo == null) {
            return src
        }
        //获取图片的宽高
        val srcWidth = src.width
        val srcHeight = src.height
        val logoW = logo.width
        val logoH = logo.height
        if (srcWidth == 0 || srcHeight == 0) {
            return null
        }
        if (logoW == 0 || logoH == 0) {
            return src
        }
        val scaleW = logoWith / logoW.toFloat()
        val scaleH = logoHigh / logoH.toFloat()
        val matrix = Matrix()
        matrix.postScale(scaleW, scaleH)
        matrix.postTranslate(
            (srcWidth shr 1) - (logoWith shr 1).toFloat(),
            (srcHeight shr 1) - (logoHigh shr 1).toFloat()
        )
        val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)
        val bitmapShader =
            BitmapShader(logo, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        bitmapShader.setLocalMatrix(matrix)
        paint.shader = bitmapShader
        var bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888)
        try {
            val canvas = Canvas(bitmap!!)
            canvas.drawBitmap(src, 0f, 0f, null)
            canvas.drawRoundRect(
                RectF(
                    ((srcWidth shr 1) - (logoWith shr 1)).toFloat(),
                    ((srcHeight shr 1) - (logoHigh shr 1)).toFloat(),
                    ((srcWidth shr 1) + (logoWith shr 1)).toFloat(),
                    ((srcHeight shr 1) + (logoHigh shr 1)).toFloat()
                ), logoRaduisX, logoRaduisY, paint
            )
            canvas.save()
            canvas.restore()
        } catch (e: Exception) {
            bitmap = null
            e.stackTrace
        }
        return bitmap
    }

    /**
     * 解码uri二维码图片
     * @return
     */
    fun scanningImage(mActivity: Activity?, uri: Uri?): String? {
        var scanBitmap: Bitmap? = null
        if (uri == null || mActivity == null) {
            return null
        }
        val hints =
            Hashtable<DecodeHintType, String?>()
        //设置二维码内容的编码
        hints[DecodeHintType.CHARACTER_SET] = "UTF8"
        scanBitmap = getBitmapByUri(mActivity, uri)
        val source: RGBLuminanceSource
        if (scanBitmap != null) {
            source = RGBLuminanceSource(scanBitmap)
        } else {
            return null
        }
        val bitmap1 = BinaryBitmap(HybridBinarizer(source))
        val reader = QRCodeReader()
        return try {
            reader.decode(bitmap1, hints).text
        } catch (e: Exception) {
            null
        }
    }

    private fun getBitmapByUri(mActivity: Activity, uri: Uri): Bitmap? {
        try {
            return MediaStore.Images.Media.getBitmap(mActivity.contentResolver, uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}
