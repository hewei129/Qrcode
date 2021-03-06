package com.hw.lib_qrcode.qrcode.utils

import android.content.Context


/**
 * @author hewei(David)
 * @date 2020/12/28  5:30 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

/**
 * 将px值转换为dip或dp值，保证尺寸大小不变
 * DisplayMetrics类中属性density
 */
fun px2dp(context: Context, pxValue: Float): Int {
    val scale = context.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

/**
 * 将dip或dp值转换为px值，保证尺寸大小不变
 * DisplayMetrics类中属性density
 */
fun dp2px(context: Context, dipValue: Float): Int {
    val scale = context.resources.displayMetrics.density
    return (dipValue * scale + 0.5f).toInt()
}

/**
 * 将px值转换为sp值，保证文字大小不变
 * DisplayMetrics类中属性scaledDensity
 */
fun px2sp(context: Context, pxValue: Float): Int {
    val fontScale = context.resources.displayMetrics.scaledDensity
    return (pxValue / fontScale + 0.5f).toInt()
}

/**
 * 将sp值转换为px值，保证文字大小不变
 * DisplayMetrics类中属性scaledDensity
 */
fun sp2px(context: Context, spValue: Float): Int {
    val fontScale = context.resources.displayMetrics.scaledDensity
    return (spValue * fontScale + 0.5f).toInt()
}