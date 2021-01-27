package com.hw.qrcodedemo

import android.app.Activity
import android.content.Intent
import com.hw.lib_qrcode.qrcode.ScanCodeConfig
import com.hw.lib_qrcode.qrcode.def.ScanStyle
import com.hw.lib_qrcode.scan.QRCodeScanUtil
import java.util.*


/**
 * @author hewei(David)
 * @date 2021/1/26  5:38 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

class QRScanUtil {
    private val TAG = "QRScanUtil"

    private val DEFAULT_MODE = ScanToolMode.NORMAL_MODE


    enum class ScanToolMode {
        NORMAL_MODE,  // zxing 二维码扫描库
        CAMERA_MODE //  CAMERA流传入 二维码扫描库
    }


    fun scanQRCode(activity: Activity) {
        scanQRCode(activity, DEFAULT_MODE)
    }

    fun scanQRCode(activity: Activity, mode: ScanToolMode?) {
        val intent: Intent? = null
        when (mode) {
            ScanToolMode.NORMAL_MODE -> {
                ScanCodeConfig.create(activity) //设置扫码页样式 ScanStyle.NONE：无  ScanStyle.QQ ：仿QQ样式   ScanStyle.WECHAT ：仿微信样式
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
            ScanToolMode.CAMERA_MODE -> {
//                val qrScanUtil = QRCodeScanUtil(activity)
//                qrScanUtil.setPlayAudio(true)
//                qrScanUtil.decodeQrcode()

            }
        }
    }




}