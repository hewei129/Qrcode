package com.hw.lib_qrcode.qrcode.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View


/**
 * @author hewei(David)
 * @date 2020/12/28  5:31 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */
open class BaseScanView : View {
    protected var valueAnimator: ValueAnimator? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
    }

    open fun startAnim() {}
    open fun cancelAnim() {}
}