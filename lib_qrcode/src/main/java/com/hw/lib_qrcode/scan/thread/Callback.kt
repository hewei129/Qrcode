package com.hw.lib_qrcode.scan.thread

import com.google.zxing.Result


/**
 * @author hewei(David)
 * @date 2021/1/27  5:47 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

interface Callback {
    fun onDecodeComplete(result: Result?)

    fun onDarkBrightness(isDark: Boolean)
}