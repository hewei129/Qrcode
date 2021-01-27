package com.hw.lib_qrcode.scan.thread

import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


/**
 * @author hewei(David)
 * @date 2021/1/27  5:47 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description 任务分发器
 */

class Dispatcher {
    private val TAG: String =Dispatcher::class.java.simpleName

    private val MAX_RUNNABLE = 10
    private var executorService: ExecutorService? = null

    private var blockingDeque: LinkedBlockingDeque<Runnable>? = null

    init {
        blockingDeque = LinkedBlockingDeque()
        executorService = ThreadPoolExecutor(
            1,
            5,
            5,
            TimeUnit.SECONDS,
            blockingDeque,
            threadFactory("decode dispatcher", false)
        )
    }

    fun newRunnable(frameData: FrameData?,  isPortrait: Boolean, callback: Callback?): ProcessRunnable? {
        return ProcessRunnable(this, frameData, isPortrait, callback)
    }

    fun newRunnable(
        data: ByteArray,
        left: Int,
        top: Int,
        width: Int,
        height: Int,
        rowWidth: Int,
        rowHeight: Int,
        isPortrait: Boolean,
        callback: Callback?
    ): ProcessRunnable? {
        return newRunnable(FrameData(data, left, top, width, height, rowWidth, rowHeight), isPortrait, callback)
    }

    @Synchronized
    fun enqueue(runnable: ProcessRunnable): Int {
        if (blockingDeque!!.size > MAX_RUNNABLE) {
            blockingDeque!!.remove()
        }
        execute(runnable)
//        LogUtil.d("blockingDeque: " + blockingDeque!!.size)
        return blockingDeque?.size ?: 0
    }

    @Synchronized
    private fun execute(runnable: Runnable) {
        executorService?.execute(runnable)
    }

    fun finished(runnable: ProcessRunnable) {
        finish(blockingDeque, runnable)
    }

    private fun finish(
        decodeDeque: Deque<Runnable>?,
        runnable: ProcessRunnable
    ) {
        synchronized(this) {
            if (decodeDeque!!.size > 0) {
                decodeDeque.remove(runnable)
                promoteCalls()
            }
        }
    }

    @Synchronized
    private fun promoteCalls() {
        blockingDeque?.let {
            val first = it.first
            execute(first)
        }

    }

    @Synchronized
    fun cancelAll() {
        blockingDeque?.let {
            for (runnable in it) {
                (runnable as ProcessRunnable).cancel()
            }
            it.clear()
        }

    }


}