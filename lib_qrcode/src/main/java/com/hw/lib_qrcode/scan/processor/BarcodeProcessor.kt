package com.hw.lib_qrcode.scan.processor

import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.hw.lib_qrcode.qrcode.DecodeFormatManager
import java.util.*


/**
 * @author hewei(David)
 * @date 2021/1/27  5:45 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description 二维码处理模块
 */

class BarcodeProcessor {

    private var cancel = false

    private var reader: MultiFormatReader? = null

    private fun initReader(): MultiFormatReader? {
        val formatReader = MultiFormatReader()
        val hints =
            Hashtable<DecodeHintType, Any?>()
        val decodeFormats = Vector<BarcodeFormat>()
        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS)
        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS)
        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS)
        hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
        hints[DecodeHintType.CHARACTER_SET] = "UTF-8"
        formatReader.setHints(hints)
        return formatReader
    }

    fun BarcodeProcessor() {
        reader = initReader()
    }

    fun process(
        data: ByteArray?,
        left: Int,
        top: Int,
        width: Int,
        height: Int
    ): Result? {
        if (cancel || data == null || data?.size <= 0) {
            return null
        }
        try {

            //将图片旋转
//            LogUtil.e("result", "Result=Decoding,1111,,,");
            val source =
                PlanarYUVLuminanceSource(data, width, height, left, top, width, height, false)
            if (source != null) {
                val bitmap = BinaryBitmap(HybridBinarizer(source))
                //                LogUtil.e("result", "Result=Decoding,2222,,,");
                try {
                    val result = reader!!.decodeWithState(bitmap)
                    if (result != null) {
//                        LogUtil.e("result", "Result=Decoding,3333,,,");
//                        LogUtil.e("result", "Result="+result.getText());
                        return result
                    }
                } catch (re: ReaderException) {
                    // continue
                } finally {
                    reader!!.reset()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    /**
     * 分析亮度
     *
     * @param data        摄像头数据
     * @param imageWidth  图像宽度
     * @param imageHeight 图像高度
     * @return 是否过暗
     */
    fun analysisBrightness(
        data: ByteArray?,
        imageWidth: Int,
        imageHeight: Int
    ): Boolean {
        return if (cancel) {
            false
        } else false
    }


    fun cancel() {
        cancel = true
    }

}