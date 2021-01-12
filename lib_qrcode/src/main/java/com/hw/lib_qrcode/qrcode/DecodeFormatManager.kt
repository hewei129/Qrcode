package com.hw.lib_qrcode.qrcode

import com.google.zxing.BarcodeFormat
import java.util.*


/**
 * @author hewei(David)
 * @date 2020/12/30  3:55 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

//用于存放解码类型分类
class DecodeFormatManager {
    companion object {
        val PRODUCT_FORMATS: MutableSet<BarcodeFormat> = EnumSet.of(
            BarcodeFormat.UPC_A,
            BarcodeFormat.UPC_E,
            BarcodeFormat.EAN_13,
            BarcodeFormat.EAN_8,
            BarcodeFormat.RSS_14,
            BarcodeFormat.RSS_EXPANDED
        )
        val INDUSTRIAL_FORMATS: Set<BarcodeFormat> = EnumSet.of(
            BarcodeFormat.CODE_39,
            BarcodeFormat.CODE_93,
            BarcodeFormat.CODE_128,
            BarcodeFormat.ITF,
            BarcodeFormat.CODABAR
        )
        val ONE_D_FORMATS: MutableSet<BarcodeFormat> =
            EnumSet.copyOf(PRODUCT_FORMATS)
        val QR_CODE_FORMATS: Set<BarcodeFormat> =
            EnumSet.of(BarcodeFormat.QR_CODE)
        val DATA_MATRIX_FORMATS: Set<BarcodeFormat> =
            EnumSet.of(BarcodeFormat.DATA_MATRIX)

        init {
            ONE_D_FORMATS.addAll(INDUSTRIAL_FORMATS)
        }

    }

}
