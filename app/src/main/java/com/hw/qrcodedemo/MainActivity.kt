package com.hw.qrcodedemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hw.lib_qrcode.qrcode.ScanCodeConfig
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
//        Log.e("David", "time="+sdf.format(Date()))
        val qrUtil = QRScanUtil()
        qrUtil.setCallBack(scanCallBack)
        tv_sao.setOnClickListener {

            qrUtil.scanQRCode(this, QRScanUtil.ScanToolMode.CZXING_MODE)

        }
    }

    // 扫描结果回调
    private val scanCallBack: QRScanUtil.QRScanCallBack = object : QRScanUtil.QRScanCallBack {
        override fun onFail() {
        }

        override fun onSuccess(result: String?) {
            Toast.makeText(this@MainActivity, "result=$result", Toast.LENGTH_LONG).show()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //接收扫码结果
        if (resultCode === Activity.RESULT_OK && requestCode === ScanCodeConfig.QUESTCODE && data != null) {
            val extras: Bundle? = data.extras
            if (extras != null) {
                val code = extras.getString(ScanCodeConfig.CODE_KEY)
                code?.let {
                    Toast.makeText(this, "result=$code", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}