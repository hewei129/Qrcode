package com.hw.lib_qrcode.photo

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaCodec.MetricsConstants.MIME_TYPE
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
import com.hw.lib_qrcode.qrcode.ScanCodeModel
import com.hw.lib_qrcode.qrcode.view.BaseScanView
import com.kevin.crop.UCrop
import com.yxing.BaseScanActivity
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_camera.pvCamera
import kotlinx.android.synthetic.main.activity_scancode.*
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


/**
 * @author hewei(David)
 * @date 2020/12/15  10:39 AM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

open class CameraActivity : BaseScanActivity() {
    override fun getLayoutId(): Int = R.layout.activity_camera

    override fun initData() {
        mDestinationUri =
            Uri.fromFile(File(cacheDir, "cropImage.jpeg"))
        cameraExecutor = Executors.newSingleThreadExecutor()
        //权限申请
        //判断用户是否已经授权，未授权则向用户申请授权，已授权则直接进行呼叫操作
        if(ContextCompat.checkSelfPermission(this,"Manifest.permission.CAMERA")
            != PackageManager.PERMISSION_GRANTED)
        {
            //注意第二个参数没有双引号
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(
                    this@CameraActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    1001
                )
            }
        }
        else
        {
            if(ContextCompat.checkSelfPermission(this,"Manifest.permission.WRITE_EXTERNAL_STORAGE")
                != PackageManager.PERMISSION_GRANTED)
            {
                //注意第二个参数没有双引号
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(
                        this@CameraActivity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1002
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

    }
    protected var mOnPictureListener: OnPictureListener? = null
    protected var lensFacing: Int = CameraSelector.LENS_FACING_FRONT
    private var camera: Camera? = null
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private lateinit var cameraExecutor: ExecutorService
    private var baseScanView: BaseScanView? = null
    private var rlParentContent: RelativeLayout? = null
    private lateinit var scModel: ScanCodeModel

    fun setCameraFacing(facing: Int){
        lensFacing = facing
    }
    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(
                baseFolder, SimpleDateFormat(format, Locale.US)
                    .format(System.currentTimeMillis()) + extension
            )
    }



    /*切换闪光灯*/
    open fun switchFlashLight(flag: Boolean) {
        //  Log.i("打开闪光灯", "openFlashLight");
        camera?.cameraControl?.enableTorch(flag)
    }


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
            //拍摄图像的配置

            imageCapture = ImageCapture.Builder() //CAPTURE_MODE_MAXIMIZE_QUALITY 拍摄高质量图片，图像质量优先于延迟，可能需要更长的时间
                //CAPTURE_MODE_MINIMIZE_LATENCY
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setTargetAspectRatio(screenAspectRatio) //设置宽高比
                .setTargetRotation(rotation) // 设置旋转角度
                .build()
            // 图像分析用例
            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(size)
                .setTargetRotation(rotation)
                .build()
                .apply {
                    setAnalyzer(
                        cameraExecutor,
                        LuminosityAnalyzer()
                    )
                }

            // 必须在重新绑定用例之前取消之前绑定
            cameraProvider.unbindAll()
            try {
                //获取相机实例
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, imageCapture, preview, imageAnalyzer
                )

                //设置预览的view
                preview?.setSurfaceProvider(pvCamera.surfaceProvider)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (null != grantResults && grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                val b = shouldShowRequestPermissionRationale(permissions[0])
                if (!b) {
                    // 提示用户去应用设置界面手动开启权限
                    if (requestCode == 1001 || requestCode == 1002) {
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
                if(requestCode == 1001){
                    if(ContextCompat.checkSelfPermission(this,"Manifest.permission.WRITE_EXTERNAL_STORAGE")
                        != PackageManager.PERMISSION_GRANTED)
                    {
                        //注意第二个参数没有双引号
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ActivityCompat.requestPermissions(
                                this@CameraActivity,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                1002
                            )
                        }
                    }
                }else if (requestCode == 1002) {
                    // surface准备监听
                    pvCamera.post {
                        //设置需要实现的用例（预览，拍照，图片数据解析等等）
                        bindCameraUseCases()
                    }
                }
                //                ToastUtil.showToast(appData, "权限获取成功");
            }
        }
    }


    private var imageCapture: ImageCapture? = null


    /**
     * 根据传入的值获取相机应该设置的分辨率比例
     */
    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = Math.max(width, height).toDouble() / Math.min(width, height)
        if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }


    private class LuminosityAnalyzer : ImageAnalysis.Analyzer {
        private var lastAnalyzedTimestamp = 0L

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // 将缓冲区倒回零
            val data = ByteArray(remaining())
            get(data)   // 将缓冲区复制到字节数组中
            return data // 返回字节数组
        }


        override fun analyze(image: ImageProxy) {
            val currentTimestamp = System.currentTimeMillis()
            // 计算平均流明的频率不超过每秒一次
            if (currentTimestamp - lastAnalyzedTimestamp >=
                TimeUnit.SECONDS.toMillis(1)) {
                val buffer = image.planes[0].buffer
                // 从回调对象中提取图像数据
                val data = buffer.toByteArray()
                // 将数据转换为像素值数组
                val pixels = data.map { it.toInt() and 0xFF }
                // 计算图像的平均亮度
                val luma = pixels.average()
                // Log the new luma value
//                Log.d("CameraXApp", "平均亮度: $luma")
                // 更新最后分析帧的时间戳
                lastAnalyzedTimestamp = currentTimestamp
            }
        }
    }
    /**
     * 拍照按钮点击监听
     */
    protected fun takePhoto(){

        val metadata = ImageCapture.Metadata()
            metadata.isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT

        val outputFileOptions: ImageCapture.OutputFileOptions = ImageCapture.OutputFileOptions.Builder(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, getContentValues()).setMetadata(metadata).build()

        imageCapture!!.takePicture(outputFileOptions, cameraExecutor, object :
            ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.e("outputFileResults", "outputFileResults:" + outputFileResults.savedUri)
                    outputFileResults.savedUri?.let { startCropActivity(it) }
//                    mOnPictureListener?.onPicture(outputFileResults.savedUri)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("outputFileResults", "exception:" + exception.localizedMessage)
                }
             })

    }
    protected fun takePhoto(isNeedCrop: Boolean){

        val metadata = ImageCapture.Metadata()
        metadata.isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT

        val outputFileOptions: ImageCapture.OutputFileOptions = ImageCapture.OutputFileOptions.Builder(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, getContentValues()).setMetadata(metadata).build()

        imageCapture!!.takePicture(outputFileOptions, cameraExecutor, object :
            ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Log.e("outputFileResults", "outputFileResults:" + outputFileResults.savedUri)
                if(isNeedCrop)
                    outputFileResults.savedUri?.let { startCropActivity(it) }
                else
                    mOnPictureListener?.onPicture(outputFileResults.savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("outputFileResults", "exception:" + exception.localizedMessage)
            }
        })

    }

    // 剪切后图像文件
    var mDestinationUri: Uri? = null
    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    open fun startCropActivity(uri: Uri) {
        mDestinationUri?.let {
            UCrop.of(uri, it)
                .withAspectRatio(432f, 432f)
                .withMaxResultSize(432, 432)
                .withTargetActivity(CropActivity::class.java)
                .start(this)
        }
    }

    open fun startCropActivity(uri: Uri, w: Float, h: Float) {
        mDestinationUri?.let {
            UCrop.of(uri, it)
                .withAspectRatio(w, h)
                .withMaxResultSize(w.toInt(), h.toInt())
                .withTargetActivity(CropActivity::class.java)
                .start(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == UCrop.REQUEST_CROP){
                handleCropResult(data)
            }
        }
    }
    /**
     * 处理剪切成功的返回值
     *
     * @param result
     */
    open fun handleCropResult(result: Intent?) {
        val resultUri = result?.let { UCrop.getOutput(it) }
        Log.e("image", "resultUri==$resultUri")
        if (null != resultUri) {
            mOnPictureListener?.onPicture(resultUri)
        } else {
            Toast.makeText(this, "Crop failed", Toast.LENGTH_SHORT)
                .show()
            //            ScToast.getInstance(mContext).showToast( "无法剪切选择图片");
        }
    }

    /**
     * 处理剪切失败的返回值
     *
     * @param result
     */
//    open fun handleCropError(result: Intent?) {
//        val cropError = UCrop.getError(result!!)
//        if (cropError != null) {
//            Log.e(
//                TAG,
//                "handleCropError: ",
//                cropError
//            )
//            Toast.makeText(
//                this,
//                cropError.message,
//                Toast.LENGTH_LONG
//            ).show()
//        } else {
//            Toast.makeText(
//                this,
//                "无法剪切选择图片,请重试！",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }

    /**
    * 使用ContentValues存储图片输出信息
    */
private fun getContentValues(): ContentValues {
    // 创建拍照后输出的图片文件名
    val fileName = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg"
    return ContentValues().apply {
        put(MediaStore.MediaColumns.MIME_TYPE, MIME_TYPE)
        // 适配Android Q版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/*")
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/"
            )
        } else {
            val fileDir = File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES).also { if (!it.exists()) it.mkdir() }
            val filePath = fileDir.absolutePath + File.separator + fileName
            put(MediaStore.Images.Media.DATA, filePath)
        }
    }
}
override fun onDestroy() {
    super.onDestroy()
    cameraExecutor.shutdown()
    baseScanView?.cancelAnim()
}

/**
* 图片选择的回调接口
*/
interface OnPictureListener {
    /**
     * 图片选择的监听回调
     *
     * @param url
     */
    fun onPicture(uri: Uri?)
    }

}