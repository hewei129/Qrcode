package com.hw.lib_qrcode.qrcode

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hw.lib_qrcode.R
import com.hw.lib_qrcode.qrcode.def.ScanStyle
import com.hw.lib_qrcode.qrcode.iface.OnScancodeListenner
import com.hw.lib_qrcode.qrcode.view.BaseScanView
import com.hw.lib_qrcode.qrcode.view.ScanCustomizeView
import com.hw.lib_qrcode.qrcode.view.ScanQQView
import com.hw.lib_qrcode.qrcode.view.ScanWechatView
import com.yxing.BaseScanActivity
import kotlinx.android.synthetic.main.activity_scancode.*
import java.io.File
import java.lang.Math.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class ScanCodeActivity : BaseScanActivity() {

    private var lensFacing : Int = CameraSelector.LENS_FACING_BACK
    private var camera : Camera? = null
    private var preview : Preview? = null
    private var imageAnalyzer : ImageAnalysis? = null
    private lateinit var cameraExecutor : ExecutorService
    private var baseScanView : BaseScanView? = null
    private var rlParentContent : RelativeLayout? = null
    private lateinit var scModel : ScanCodeModel

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(baseFolder, SimpleDateFormat(format, Locale.US)
                .format(System.currentTimeMillis()) + extension)
    }

    override fun getLayoutId() : Int = R.layout.activity_scancode

    override fun initData() {
        scModel = intent?.extras?.getParcelable(ScanCodeConfig.MODEL_KEY)!!
        addScanView(scModel.getStyle())
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()
        //权限申请
        //判断用户是否已经授权，未授权则向用户申请授权，已授权则直接进行呼叫操作
        if(ContextCompat.checkSelfPermission(this,"Manifest.permission.CAMERA")
            != PackageManager.PERMISSION_GRANTED)
        {
            //注意第二个参数没有双引号
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(
                    this@ScanCodeActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    1001
                )
            }
        }
        else
        {
            // surface准备监听
            pvCamera.post {
                //设置需要实现的用例（预览，拍照，图片数据解析等等）
                bindCameraUseCases()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (null != grantResults && grantResults.size > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                val b = shouldShowRequestPermissionRationale(permissions[0])
                if (!b) {
                    // 提示用户去应用设置界面手动开启权限
                    if (requestCode == 1001) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri =
                            Uri.fromParts("package", this.packageName, null)
                        intent.data = uri
                        startActivityForResult(
                            intent,
                           10000)
                    }
                } else {
                    Toast.makeText(this, "请先开启相关权限", Toast.LENGTH_LONG).show()
                }
            } else {
                // surface准备监听
                pvCamera.post {
                    //设置需要实现的用例（预览，拍照，图片数据解析等等）
                    bindCameraUseCases()
                }
                //                ToastUtil.showToast(appData, "权限获取成功");
            }
        }
    }

    /*切换闪光灯*/
    open fun switchFlashLight(flag: Boolean) {
        //  Log.i("打开闪光灯", "openFlashLight");
        camera?.cameraControl?.enableTorch(flag)
    }

    private fun addScanView(style: Int?) {
        rlParentContent = findViewById(R.id.rlparent)
        val lp : RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        when (style){
            ScanStyle.QQ -> {
                baseScanView = ScanQQView(this)
            }
            ScanStyle.WECHAT -> {
                baseScanView = ScanWechatView(this)
            }
            ScanStyle.CUSTOMIZE -> {
                baseScanView = ScanCustomizeView(this).apply {
                    setScanCodeModel(scModel)
                }
            }
        }
        baseScanView?.let {
            it.layoutParams = lp
            rlParentContent?.addView(it)
        }
    }

    protected abstract fun success(code: String)

    private fun bindCameraUseCases() {

        // 获取用于设置全屏分辨率相机的屏幕值
        val metrics = DisplayMetrics().also { pvCamera.display.getRealMetrics(it) }

        //获取使用的屏幕比例分辨率属性
        val screenAspectRatio = aspectRatio(metrics.widthPixels / 2, metrics.heightPixels / 2)

        val width = pvCamera.measuredWidth
        val height = if (screenAspectRatio == AspectRatio.RATIO_16_9) {
            (width * RATIO_16_9_VALUE).toInt()
        } else {
            (width * RATIO_4_3_VALUE).toInt()
        }
        val size = Size(width, height)

        //获取旋转角度
        val rotation = pvCamera.display.rotation

        //生命周期绑定
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()//设置所选相机
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            // 预览用例
            preview = Preview.Builder()
                .setTargetResolution(size)
                .setTargetRotation(rotation)
                .build()

            // 图像分析用例
            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(size)
                .setTargetRotation(rotation)
                .build()
                .apply {
                    setAnalyzer(cameraExecutor, ScanCodeAnalyzer(this@ScanCodeActivity, scModel, object :
                        OnScancodeListenner {
                        override fun onBackCode(code: String) {
                            success(code)
                        }
                    }))
                }

            // 必须在重新绑定用例之前取消之前绑定
            cameraProvider.unbindAll()
            try {
                //获取相机实例
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer)

                //设置预览的view
                preview?.setSurfaceProvider(pvCamera.surfaceProvider)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * 根据传入的值获取相机应该设置的分辨率比例
     */
    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        baseScanView?.cancelAnim()
    }
}