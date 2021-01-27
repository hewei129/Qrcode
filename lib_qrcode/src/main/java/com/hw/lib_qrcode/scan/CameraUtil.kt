package com.hw.lib_qrcode.scan

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.util.TypedValue
import android.view.MotionEvent
import android.view.WindowManager


/**
 * @author hewei(David)
 * @date 2021/1/27  6:23 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

/**
 * 是否为竖屏
 */
fun isPortrait(context: Context): Boolean {
    val screenResolution = getScreenResolution(context)
    return screenResolution.y > screenResolution.x
}

fun getScreenResolution(context: Context): Point {
    val wm =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val screenResolution = Point()
    display.getSize(screenResolution)
    return screenResolution
}

/**
 * 计算手指间距
 */
fun calculateFingerSpacing(event: MotionEvent): Float {
    val x = event.getX(0) - event.getX(1)
    val y = event.getY(0) - event.getY(1)
    return Math.sqrt(x * x + y * y.toDouble()).toFloat()
}

/**
 * 计算对焦和测光区域
 *
 * @param coefficient        比率
 * @param originFocusCenterX 对焦中心点X
 * @param originFocusCenterY 对焦中心点Y
 * @param originFocusWidth   对焦宽度
 * @param originFocusHeight  对焦高度
 * @param previewViewWidth   预览宽度
 * @param previewViewHeight  预览高度
 *
 *
 * https://www.cnblogs.com/panxiaochun/p/5802814.html
 */
fun calculateFocusMeteringArea(
    coefficient: Float,
    originFocusCenterX: Float, originFocusCenterY: Float,
    originFocusWidth: Int, originFocusHeight: Int,
    previewViewWidth: Int, previewViewHeight: Int
): Rect? {
    val halfFocusAreaWidth = (originFocusWidth * coefficient / 2).toInt()
    val halfFocusAreaHeight = (originFocusHeight * coefficient / 2).toInt()
    val centerX = (originFocusCenterX / previewViewWidth * 2000 - 1000).toInt()
    val centerY = (originFocusCenterY / previewViewHeight * 2000 - 1000).toInt()
    val rectF = RectF(
        clamp(centerX - halfFocusAreaWidth, -1000, 1000).toFloat(),
        clamp(centerY - halfFocusAreaHeight, -1000, 1000).toFloat(),
        clamp(centerX + halfFocusAreaWidth, -1000, 1000).toFloat(),
        clamp(centerY + halfFocusAreaHeight, -1000, 1000).toFloat()
    )
    return Rect(
        Math.round(rectF.left), Math.round(rectF.top),
        Math.round(rectF.right), Math.round(rectF.bottom)
    )
}


fun clamp(value: Int, min: Int, max: Int): Int {
    return Math.min(Math.max(value, min), max)
}

fun dp2px(context: Context, dpValue: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpValue,
        context.resources.displayMetrics
    ).toInt()
}

fun sp2px(context: Context, spValue: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        spValue,
        context.resources.displayMetrics
    ).toInt()
}
