package com.hw.qrcodedemo

import android.app.Activity
import android.content.Intent
import com.hw.lib_qrcode.qrcode.ScanCodeActivity
import com.hw.lib_qrcode.qrcode.ScanCodeConfig


/**
 * @author hewei(David)
 * @date 2021/1/4  2:11 PM
 * @Copyright ©  Shanghai Xinke Digital Technology Co., Ltd.
 * @description
 */

class MyScanActivity: ScanCodeActivity(){


    override fun getLayoutId(): Int {
        return super.getLayoutId()

    }

    override fun success(code: String) {
        val intent = Intent()
        intent.putExtra(ScanCodeConfig.CODE_KEY, code)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun initData() {
        super.initData()
    }






}