package com.hw.lib_qrcode.scan.thread

import com.hw.lib_qrcode.scan.processor.BarcodeProcessor
import com.hw.lib_qrcode.scan.rotateYUV420Degree90


/**
 * @author hewei(David)
 * @date 2021/1/27  5:51 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description  二维码处理线程
 */

class ProcessRunnable(dispatcher: Dispatcher?,
                      frameData: FrameData?,
                      callback: Callback?) : Runnable{
    private var dispatcher: Dispatcher? = null
    private var frameData: FrameData? = null
    private var mBarcodeProcessor: BarcodeProcessor? = null
    private var mDecodeCallback: Callback? = null

   init{
        this.dispatcher = dispatcher
        this.frameData = frameData
        mDecodeCallback = callback
        mBarcodeProcessor = BarcodeProcessor()
    }

    override fun run() {
        try {
            frameData?.let {
                if (it.left !== 0 && it.top !== 0) {
                    val isDark = mBarcodeProcessor!!.analysisBrightness(
                        it.data,
                        it.width,
                        it.height
                    )
                    if (mDecodeCallback != null) {
                        mDecodeCallback!!.onDarkBrightness(isDark)
                    }
                    if (isDark) {
                        return
                    }
                }
//            val start = System.currentTimeMillis()
                val new_data: ByteArray? = rotateYUV420Degree90(it.data, it.width, it.height)
                val result = mBarcodeProcessor!!.process(
                    new_data,
                    it.left,
                    it.top,
                    it.height,
                    it.width
                )
                if (result != null && mDecodeCallback != null) {
                    mDecodeCallback!!.onDecodeComplete(result)
                }

            }

//            LogUtil.d("reader time: " + (System.currentTimeMillis() - start));

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            dispatcher?.finished(this)
        }
    }

    fun cancel() {
        mBarcodeProcessor?.cancel()
    }

    fun enqueue(): Int? {
        return dispatcher?.enqueue(this)
    }
}