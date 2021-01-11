package com.hw.lib_qrcode.qrcode.view

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.hw.lib_qrcode.R
import com.hw.lib_qrcode.qrcode.utils.dp2px


/**
 * @author hewei(David)
 * @date 2020/12/28  5:33 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

class ScanQQView : BaseScanView {
    private var scanMaginWith = 0
    private var scanMaginheight = 0
    private var paint: Paint? = null
    private var scanLine: Bitmap? = null
    private var scanRect: Rect? = null
    private var lineRect: Rect? = null

    //画布截取
    private var interceptiRect: Rect? = null

    //扫描线位置
    private var scanLineTop = 0

    //扫描框大小
    private var scanWith = 0
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
            R.drawable.scanqq
        )
        bitmapHigh = scanLine!!.getHeight()
        interceptiRect = Rect()
        scanRect = Rect()
        lineRect = Rect()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        scanMaginWith = measuredWidth / 10
        scanMaginheight = measuredHeight shr 2
        scanWith = measuredWidth - 2 * scanMaginWith
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        scanRect!![scanMaginWith, scanMaginheight, width - scanMaginWith] =
            scanMaginheight + scanWith
        startAnim()
        drawFrameBounds(canvas, scanRect)
        lineRect!![scanMaginWith, scanLineTop, width - scanMaginWith] = scanLineTop + bitmapHigh
        canvas.drawBitmap(scanLine!!, null, lineRect!!, paint)
    }

    /**
     * 绘制取景框边框
     *
     * @param canvas
     * @param frame
     */
    private fun drawFrameBounds(
        canvas: Canvas,
        frame: Rect?
    ) {
        paint!!.color = ContextCompat.getColor(context, R.color.qqscan)
        paint!!.strokeWidth = 2f
        paint!!.style = Paint.Style.FILL
        val corWidth: Int = dp2px(context, 4f)
        val corLength: Int = dp2px(context, 15f)
        val radius: Int = dp2px(context, 2f)
        interceptiRect!![scanRect!!.left - corWidth, scanRect!!.top - corWidth, scanRect!!.right + corWidth] =
            scanRect!!.bottom + corWidth

        // 左上角
        canvas.drawRoundRect(
            frame!!.left - corWidth.toFloat(),
            frame.top - corWidth.toFloat(),
            frame.left.toFloat(),
            (frame.top
                    + corLength).toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            paint!!
        )
        canvas.drawRoundRect(
            frame.left - corWidth.toFloat(),
            frame.top - corWidth.toFloat(),
            (frame.left
                    + corLength).toFloat(),
            frame.top.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            paint!!
        )
        // 右上角
        canvas.drawRoundRect(
            frame.right.toFloat(), frame.top - corWidth.toFloat(), frame.right + corWidth.toFloat(),
            frame.top + corLength.toFloat(), radius.toFloat(), radius.toFloat(), paint!!
        )
        canvas.drawRoundRect(
            frame.right - corLength.toFloat(),
            frame.top - corWidth.toFloat(),
            frame.right + corWidth.toFloat(),
            frame.top.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            paint!!
        )
        // 左下角
        canvas.drawRoundRect(
            frame.left - corWidth.toFloat(),
            frame.bottom - corLength.toFloat(),
            frame.left.toFloat(),
            frame.bottom + corWidth.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            paint!!
        )
        canvas.drawRoundRect(
            frame.left - corWidth.toFloat(),
            frame.bottom.toFloat(),
            (frame.left
                    + corLength).toFloat(),
            frame.bottom + corWidth.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            paint!!
        )
        // 右下角
        canvas.drawRoundRect(
            frame.right.toFloat(),
            frame.bottom - corLength.toFloat(),
            (frame.right
                    + corWidth).toFloat(),
            frame.bottom + corWidth.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            paint!!
        )
        canvas.drawRoundRect(
            frame.right - corLength.toFloat(),
            frame.bottom.toFloat(),
            (frame.right
                    + corWidth).toFloat(),
            frame.bottom + corWidth.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            paint!!
        )
        canvas.clipRect(interceptiRect!!)
    }

    override fun startAnim() {
        if (valueAnimator == null) {
            valueAnimator =
                ValueAnimator.ofInt(scanRect!!.top - bitmapHigh, scanRect!!.bottom - bitmapHigh)
            valueAnimator?.setRepeatCount(ValueAnimator.INFINITE)
            valueAnimator?.setRepeatMode(ValueAnimator.RESTART)
            valueAnimator?.setDuration(3000)
            valueAnimator?.setInterpolator(LinearInterpolator())
            valueAnimator?.addUpdateListener(AnimatorUpdateListener { animation ->
                scanLineTop = animation.animatedValue as Int
                postInvalidate()
            })
            valueAnimator?.start()
        }
    }

    override fun cancelAnim() {
        valueAnimator?.cancel()
    }
}
