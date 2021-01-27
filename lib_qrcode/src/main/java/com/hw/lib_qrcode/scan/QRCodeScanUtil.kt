package com.hw.lib_qrcode.scan

import android.app.Activity
import com.google.zxing.Result
import com.hw.lib_qrcode.R
import com.hw.lib_qrcode.qrcode.utils.AudioUtil
import com.hw.lib_qrcode.scan.thread.Callback
import com.hw.lib_qrcode.scan.thread.Dispatcher


/**
 * @author hewei(David)
 * @date 2021/1/27  5:44 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

class QRCodeScanUtil(activity: Activity) {
    private var audioId = R.raw.beep
    private var audioUtil: AudioUtil? = null
    private var dispatcher: Dispatcher? = null
    init {
        this.audioUtil = AudioUtil(activity, audioId)
        this.dispatcher = Dispatcher()
    }


    fun setAudioId(audioId: Int) {
        this.audioId = audioId
    }

    private var isPlayAudio = false
    fun setPlayAudio(flag: Boolean) {
        isPlayAudio = flag
    }




    @Volatile
    private var isDecodingSuccess = false

    fun setIsDecodingSuccess(flag: Boolean) {
        isDecodingSuccess = flag
    }

    fun decodeQrcode(
        data: ByteArray,
        left: Int,
        top: Int,
        width: Int,
        height: Int,
        callback: Callback
    ) {
        try {
            if (dispatcher != null && !isDecodingSuccess) dispatcher!!.newRunnable(
                data,
                left,
                top,
                width,
                height,
                width,
                width,
                object : Callback {
                    override fun onDecodeComplete(result: Result?) {
                        if (isPlayAudio) audioUtil?.playBeepSoundAndVibrate()
                        callback.onDecodeComplete(result)
                    }

                    override fun onDarkBrightness(isDark: Boolean) {
                        callback.onDarkBrightness(isDark)
                    }
                })?.enqueue()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        setIsDecodingSuccess(true)
        if (dispatcher != null) dispatcher!!.cancelAll()
    }
}