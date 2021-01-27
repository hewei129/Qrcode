package com.hw.lib_qrcode.scan.thread


/**
 * @author hewei(David)
 * @date 2021/1/27  5:49 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

class FrameData(
    data: ByteArray,
    left: Int,
    top: Int,
    width: Int,
    height: Int,
    rowWidth: Int,
    rowHeight: Int
) {
    var data: ByteArray
    var left: Int
    var top: Int
    var width: Int
    var height: Int
    var rowWidth: Int
    var rowHeight: Int

    init {
        this.data = data
        this.left = left
        this.top = top
        this.width = width
        this.height = height
        this.rowWidth = rowWidth
        this.rowHeight = rowHeight
    }
}
