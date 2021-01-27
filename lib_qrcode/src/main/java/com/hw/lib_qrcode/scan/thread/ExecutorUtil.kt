package com.hw.lib_qrcode.scan.thread

import android.os.Handler
import android.os.Looper
import java.util.concurrent.*


/**
 * @author hewei(David)
 * @date 2021/1/27  5:48 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

    private var sMainExecutor: Executor? = null
    private var sMainHandler: Handler? = null
    private var sIOExecutor: Executor? = null


    @Synchronized
    private fun getMainExecutor(): Executor {
        if (sMainExecutor == null) {
            sMainHandler = Handler(Looper.getMainLooper())
            sMainExecutor = Executor { command -> sMainHandler!!.post(command) }
        }
        return sMainExecutor!!
    }

    @Synchronized
    fun getIOExecutor(): Executor? {
        if (sIOExecutor == null) {
            sIOExecutor = ThreadPoolExecutor(
                1,
                1,
                5,
                TimeUnit.SECONDS,
                LinkedBlockingQueue()
            )
            (sIOExecutor as ThreadPoolExecutor).allowCoreThreadTimeOut(true)
        }
        return sIOExecutor
    }

    fun runOnUiThread(runnable: Runnable?) {
        if (runnable == null) {
            return
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            getMainExecutor().execute(runnable)
        } else {
            runnable.run()
        }
    }

    fun threadFactory(
        name: String?,
        daemon: Boolean
    ): ThreadFactory? {
        return ThreadFactory { runnable ->
            val result = Thread(runnable, name)
            result.isDaemon = daemon
            result
        }
    }