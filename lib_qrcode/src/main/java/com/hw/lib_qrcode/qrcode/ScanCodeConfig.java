package com.hw.lib_qrcode.qrcode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.hw.lib_qrcode.qrcode.utils.QrCodeUtil;


public class ScanCodeConfig {
    public static final int QUESTCODE = 0x001;
    public static final String CODE_KEY = "code";
    public static final String MODEL_KEY = "model";

    private  Activity mActivity;
    private ScanCodeModel model;

    public ScanCodeConfig (ScanCodeModel model){
        this.mActivity = model.mActivity;
        this.model = model;
    }

    public static ScanCodeModel create(Activity mActivity){
        return new ScanCodeModel(mActivity);
    }

    public void start(Class mClass){
        Intent intent = new Intent(mActivity, mClass);
        intent.putExtra(MODEL_KEY, model);
        mActivity.startActivityForResult(intent, QUESTCODE);
    }

    public static Bitmap createQRCode(String text){
        return createQRCode(text);
    }

    public static Bitmap createQRCode(String text, int size) {
        return createQRCode(text, size);
    }

    public static Bitmap createQRcodeWithLogo(String text, Bitmap logo){
        return createQRcodeWithLogo(text, logo);
    }

    public static Bitmap createQRcodeWithLogo(String text, int size, Bitmap logo, int logoWith, int logoHigh, float logoRaduisX, float logoRaduisY){
        return createQRcodeWithLogo(text, size, logo, logoWith, logoHigh, logoRaduisX, logoRaduisY);
    }

    public static String scanningImage(Activity mActivity, Uri uri) {
        return scanningImage(mActivity, uri);
    }
}
