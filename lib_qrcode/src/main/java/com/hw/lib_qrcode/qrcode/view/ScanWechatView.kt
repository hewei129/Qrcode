package com.hw.lib_qrcode.qrcode.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import com.hw.lib_qrcode.R


/**
 * @author hewei(David)
 * @date 2020/12/29  10:11 AM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */
class ScanWechatView : BaseScanView {
    private var scanMaginWith = 0
    private var scanMaginheight = 0
    private var paint: Paint? = null
    private var scanLine: Bitmap? = null
    private var scanRect: Rect? = null
    private var lineRect: Rect? = null

    //扫描线位置
    private var scanLineTop = 0

    //透明度
    private var alpha = 100
    private var bitmapHigh = 0

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        scanLine = BitmapFactory.decodeResource(
            resources,
            R.drawable.scan_wechatline
        )
        bitmapHigh = scanLine!!.height
        scanRect = Rect()
        lineRect = Rect()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        scanMaginWith = measuredWidth / 10
        scanMaginheight = measuredHeight shr 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        scanRect!![scanMaginWith, scanMaginheight, width - scanMaginWith] = height - scanMaginheight
        startAnim()
        paint!!.alpha = alpha
        lineRect!![scanMaginWith, scanLineTop, width - scanMaginWith] = scanLineTop + bitmapHigh
        canvas.drawBitmap(scanLine!!, null, lineRect!!, paint)
    }

    override fun startAnim() {
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofInt(scanRect!!.top, scanRect!!.bottom)
            valueAnimator?.repeatCount = ValueAnimator.INFINITE
            valueAnimator?.repeatMode = ValueAnimator.RESTART
            valueAnimator?.duration = 4000
            valueAnimator?.interpolator = LinearInterpolator()
            valueAnimator?.addUpdateListener { animation ->
                scanLineTop = animation.animatedValue as Int
                val startHideHeight = (scanRect!!.bottom - scanRect!!.top) / 6
                alpha =
                    if (scanRect!!.bottom - scanLineTop <= startHideHeight) ((scanRect!!.bottom - scanLineTop).toDouble() / startHideHeight * 100).toInt() else 100
                postInvalidate()
            }
            valueAnimator?.start()
        }
    }

    override fun cancelAnim() {
        valueAnimator?.cancel()
    }
}
