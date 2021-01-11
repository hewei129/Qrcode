package com.hw.lib_qrcode.photo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.hw.lib_qrcode.R
import com.kevin.crop.UCrop
import com.kevin.crop.util.BitmapLoadUtils
import com.kevin.crop.view.CropImageView
import com.kevin.crop.view.GestureCropImageView
import com.kevin.crop.view.OverlayView
import com.kevin.crop.view.TransformImageView.TransformImageListener
import com.yxing.BaseScanActivity
import kotlinx.android.synthetic.main.activity_crop.*
import java.io.OutputStream


/**
 * @author hewei(David)
 * @date 2020/12/28  2:53 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

class CropActivity : BaseScanActivity() {
    private val TAG = "CropActivity"
    var mGestureCropImageView: GestureCropImageView? = null
    var mOverlayView: OverlayView? = null
    private var mOutputUri: Uri? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_crop
    }

    override fun initData() {
        mGestureCropImageView = weixin_act_ucrop.cropImageView
        mOverlayView = weixin_act_ucrop.overlayView
        mOverlayView!!.setPadding(0, 0, 0, 0)
        mGestureCropImageView!!.setPadding(0, 0, 0, 0)
        initCropView()
        initEvents()
    }

    /**
     * 初始化裁剪View
     */
    private fun initCropView() {
        // 设置允许缩放
        mGestureCropImageView!!.isScaleEnabled = true
        // 设置禁止旋转
        mGestureCropImageView!!.isRotateEnabled = false
        // 设置外部阴影颜色
        mOverlayView!!.setDimmedColor(Color.parseColor("#AA000000"))
        // 设置周围阴影是否为椭圆(如果false则为矩形)
        mOverlayView!!.setOvalDimmedLayer(false)
        // 设置显示裁剪边框
        mOverlayView!!.setShowCropFrame(true)
        // 设置不显示裁剪网格
        mOverlayView!!.setShowCropGrid(false)
        val intent = intent
        setImageData(intent)
    }

    private fun setImageData(intent: Intent) {
        val inputUri =
            intent.getParcelableExtra<Uri>(UCrop.EXTRA_INPUT_URI)
        mOutputUri = intent.getParcelableExtra(UCrop.EXTRA_OUTPUT_URI)
        if (inputUri != null && mOutputUri != null) {
            try {
                mGestureCropImageView!!.setImageUri(inputUri)
            } catch (e: java.lang.Exception) {
                setResultException(e)
                finish()
            }
        } else {
            setResultException(java.lang.NullPointerException("Both input and output Uri must be specified"))
            finish()
        }

        // 设置裁剪宽高比
        if (intent.getBooleanExtra(UCrop.EXTRA_ASPECT_RATIO_SET, false)) {
            val aspectRatioX = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_X, 0f)
            val aspectRatioY = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_Y, 0f)
            if (aspectRatioX > 0 && aspectRatioY > 0) {
                mGestureCropImageView!!.targetAspectRatio = (aspectRatioX - 5) / aspectRatioY
            } else {
                mGestureCropImageView!!.targetAspectRatio = CropImageView.SOURCE_IMAGE_ASPECT_RATIO
            }
            //            mGestureCropImageView.setTargetAspectRatio(targetAspectRatio);
        }

        // 设置裁剪的最大宽高
        if (intent.getBooleanExtra(UCrop.EXTRA_MAX_SIZE_SET, false)) {
            val maxSizeX = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_X, 0)
            val maxSizeY = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_Y, 0)
            if (maxSizeX > 0 && maxSizeY > 0) {
//                Log.d(
//                    "crop",
//                    "width==" + DeviceUtils.getWindowWidth(this)
//                        .toString() + "height==" + DeviceUtils.getWindowHeight(this)
//                )
                mGestureCropImageView!!.setMaxResultImageSizeX(maxSizeX)
                mGestureCropImageView!!.setMaxResultImageSizeY(maxSizeY)
            } else {
                Log.w(
                    TAG,
                    "EXTRA_MAX_SIZE_X and EXTRA_MAX_SIZE_Y must be greater than 0"
                )
            }
        }
    }

    fun initEvents() {
        mGestureCropImageView?.setTransformImageListener(mImageListener)
        crop_act_save_fab.setOnClickListener({ cropAndSaveImage() })
        crop_cancel.setOnClickListener { finish() }
        tv_rotation.setOnClickListener({ rotateByAngle(90) })
    }

    private val mImageListener: TransformImageListener = object : TransformImageListener {
        override fun onRotate(currentAngle: Float) {
        }

        override fun onScale(currentScale: Float) {
        }

        override fun onLoadComplete() {
            val fadeInAnimation =
                AnimationUtils.loadAnimation(
                    applicationContext,
                    R.anim.crop_fade_in
                )
            fadeInAnimation.setAnimationListener(object :
                Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    weixin_act_ucrop.setVisibility(View.VISIBLE)
                    mGestureCropImageView!!.setImageToWrapCropBounds()
                }

                override fun onAnimationEnd(animation: Animation) {}
                override fun onAnimationRepeat(animation: Animation) {}
            })
            weixin_act_ucrop.startAnimation(fadeInAnimation)
        }

        override fun onLoadFailure(e: Exception) {
            setResultException(e)
            finish()
        }
    }

    private fun rotateByAngle(angle: Int) {
        mGestureCropImageView!!.postRotate(angle.toFloat())
        mGestureCropImageView!!.setImageToWrapCropBounds()
    }

    private fun cropAndSaveImage() {
        var outputStream: OutputStream? = null
        try {
            val croppedBitmap = mGestureCropImageView!!.cropImage()
            if (croppedBitmap != null) {
                outputStream = contentResolver.openOutputStream(mOutputUri!!)
                croppedBitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    outputStream
                )
                croppedBitmap.recycle()
                setResultUri(mOutputUri!!, mGestureCropImageView!!.targetAspectRatio)
                finish()
            } else {
                setResultException(NullPointerException("CropImageView.cropImage() returned null."))
            }
        } catch (e: java.lang.Exception) {
            setResultException(e)
            finish()
        } finally {
            BitmapLoadUtils.close(outputStream)
        }
    }

    private fun setResultUri(
        uri: Uri,
        resultAspectRatio: Float
    ) {
        setResult(
            Activity.RESULT_OK, Intent()
                .putExtra(UCrop.EXTRA_OUTPUT_URI, uri)
                .putExtra(UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO, resultAspectRatio)
        )
    }

    private fun setResultException(throwable: Throwable) {
        setResult(
            UCrop.RESULT_ERROR,
            Intent().putExtra(UCrop.EXTRA_ERROR, throwable)
        )
    }
}