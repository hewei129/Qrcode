package com.hw.qrcodedemo

import android.app.Activity
import android.content.Intent
import android.graphics.Color.green
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.hw.lib_qrcode.qrcode.ScanCodeConfig
import com.hw.lib_qrcode.qrcode.bean.ScanRect
import com.hw.lib_qrcode.qrcode.def.ScanStyle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_sao.setOnClickListener {
            ScanCodeConfig.create(this@MainActivity) //设置扫码页样式 ScanStyle.NONE：无  ScanStyle.QQ ：仿QQ样式   ScanStyle.WECHAT ：仿微信样式
                .setStyle(ScanStyle.QQ)//扫码成功是否播放音效  true ： 播放   false ： 不播放
                .setPlayAudio(true)
                //设置音效音频
                .setAudioId(R.raw.beep)
                .setShowFrame(true)
                //设置边框上四个角标颜色
                .setFrameColor(R.color.colorAccent)
                //设置边框上四个角标圆角  单位 /dp
                .setFrameRaduis(5)
                .setFrameWith(4)
                .setFrameLenth(15)
                .setShowShadow(true)
                //设置边框外部阴影颜色
                .setShaowColor(R.color.black_tran30)
                //设置扫码条图片
                .setScanBitmapId(R.drawable.scan_wechatline)
                .buidler() //跳转扫码页   扫码页可自定义样式
                .start(MyScanActivity::class.java)
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