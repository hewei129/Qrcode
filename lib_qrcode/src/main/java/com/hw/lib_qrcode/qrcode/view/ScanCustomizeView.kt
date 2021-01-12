package com.hw.lib_qrcode.qrcode.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.hw.lib_qrcode.R
import com.hw.lib_qrcode.qrcode.ScanCodeModel
import com.hw.lib_qrcode.qrcode.bean.ScanRect
import com.hw.lib_qrcode.qrcode.utils.dp2px


/**
 * @author hewei(David)
 * @date 2020/12/28  5:31 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

class ScanCustomizeView : BaseScanView {
    private var paint: Paint? = null
    private var scanLine: Bitmap? = null
    private var scanRect: Rect? = null
    private var lineRect: Rect? = null

    //扫描线位置
    private var scanLineTop = 0
    private var bitmapHigh = 0
    private var scanCodeModel: ScanCodeModel? = null
    private var sRect: ScanRect? = null

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

    fun setScanCodeModel(scanCodeModel: ScanCodeModel) {
        this.scanCodeModel = scanCodeModel
        scanLine = BitmapFactory.decodeResource(
            resources,
            scanCodeModel.scanBitmapId
        )
        bitmapHigh = if (scanLine == null) 0 else scanLine!!.height
        sRect = scanCodeModel.scanRect
        postInvalidate()
    }

    private fun init() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint!!.style = Paint.Style.FILL
        scanRect = Rect()
        lineRect = Rect()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (sRect != null) {
            scanRect!![dp2px(context, sRect!!.left.toFloat()), dp2px(
                context,
                sRect!!.top.toFloat()
            ), dp2px(context, sRect!!.right.toFloat())] =
                dp2px(context, sRect!!.bottom.toFloat())
            if (scanCodeModel?.isShowFrame == true) {
                drawFrameBounds(canvas, scanRect)
            }
            if (scanCodeModel?.isShowShadow == true) {
                drawShadow(canvas, scanRect)
            }
            if (scanLine != null) {
                startAnim()
                lineRect!![dp2px(context, sRect!!.left.toFloat()), scanLineTop, dp2px(
                    context,
                    sRect!!.right.toFloat()
                )] =
                    scanLineTop + bitmapHigh
                canvas.drawBitmap(scanLine!!, null, lineRect!!, paint)
            }
        }
    }

    /** 绘制阴影
     * @param canvas
     * @param frame
     */
    private fun drawShadow(
        canvas: Canvas,
        frame: Rect?
    ) {
        paint!!.color = ContextCompat.getColor(
            context,
            if (scanCodeModel?.shaowColor === 0)
                R.color.black_tran30 else scanCodeModel!!.shaowColor
        )
        val frameWith: Int = dp2px(
            context,
            if (scanCodeModel!!.frameWith === 0) DEFALUTE_WITH.toFloat() else scanCodeModel!!.frameWith
                .toFloat()
        )
        canvas.drawRect(0f, 0f, width.toFloat(), frame!!.top - frameWith.toFloat(), paint!!)
        canvas.drawRect(
            0f,
            frame.top - frameWith.toFloat(),
            frame.left - frameWith.toFloat(),
            frame.bottom + frameWith.toFloat(),
            paint!!
        )
        canvas.drawRect(
            frame.right + frameWith.toFloat(),
            frame.top - frameWith.toFloat(),
            width.toFloat(),
            frame.bottom + frameWith.toFloat(),
            paint!!
        )
        canvas.drawRect(
            0f, frame.bottom + frameWith.toFloat(),
            width.toFloat(),
            height.toFloat(),
            paint!!
        )
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
        if (scanCodeModel == null) return
        paint!!.color = ContextCompat.getColor(
            context,
            if (scanCodeModel!!.frameColor === 0) R.color.qqscan else scanCodeModel!!.frameColor
        )
        val corWidth: Int = dp2px(
            context,
            if (scanCodeModel!!.frameWith === 0) DEFALUTE_WITH.toFloat() else scanCodeModel!!.frameWith
                .toFloat()
        )
        val corLength: Int = dp2px(
            context,
            if (scanCodeModel!!.frameLenth === 0) DEFAULTE_LENGTH.toFloat() else scanCodeModel!!.frameLenth
                .toFloat()
        )
        val radius: Int = dp2px(context, scanCodeModel!!.frameRaduis.toFloat())

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
    }

    override fun startAnim() {
        if (valueAnimator == null) {
            valueAnimator =
                ValueAnimator.ofInt(scanRect!!.top - bitmapHigh, scanRect!!.bottom - bitmapHigh)
            valueAnimator?.repeatCount = ValueAnimator.INFINITE
            valueAnimator?.repeatMode = ValueAnimator.RESTART
            valueAnimator?.duration = 3000
            valueAnimator?.interpolator = LinearInterpolator()
            valueAnimator?.addUpdateListener { animation ->
                scanLineTop = animation.animatedValue as Int
                postInvalidate()
            }
            valueAnimator?.start()
        }
    }

    override fun cancelAnim() {
        valueAnimator?.cancel()
    }

    companion object {
        //边框角默认宽度
        const val DEFALUTE_WITH = 4

        //边框角默认长度
        const val DEFAULTE_LENGTH = 15
    }
}
