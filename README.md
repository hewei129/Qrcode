# Qrcode
集成封装了二维码扫码、生成二维码等，运用了camerax库，可自定义扫码页面；还封装了自定义camerax拍照功能等等

使用说明 
Step 1. Add it in your root build.gradle at the end of repositories:

    allprojects {
      repositories {
        ...
        maven { url 'https://jitpack.io' }
      }
    }
Step 2. Add the dependency

    dependencies {
            implementation 'com.github.hewei129:Qrcode:1.0.2'
    }
Step 3. 
    二维码扫码：
     ScanCodeConfig.create(activity) //设置扫码页样式 ScanStyle.NONE：无  ScanStyle.QQ ：仿QQ样式   ScanStyle.WECHAT ：仿微信样式
                                            .setStyle(ScanStyle.CUSTOMIZE)//扫码成功是否播放音效  true ： 播放   false ： 不播放
                                            .setPlayAudio(true)//设置音效音频
                                            .setAudioId(R.raw.beep)
                                            .setScanRect(ScanRect(left, top, right, bottom))//设置扫描框剧屏幕的边距
                                            .setShowFrame(true)//设置边框上四个角标颜色
                                            .setFrameColor(R.color.green)//设置边框上四个角标圆角  单位 /dp
                                            .setFrameRaduis(5)//设置边框的圆角
                                            .setFrameWith(4)//设置边框的四个边的宽度
                                            .setFrameLenth(15)//设置边框的四个边的长度
                                            .setShowShadow(true)//设置边框外部阴影颜色
                                            .setShaowColor(R.color.black_tran30)//设置扫码条图片
                                            .setScanBitmapId(R.drawable.scan_wechatline)
                                            .buidler() //跳转扫码页   扫码页可自定义样式
                                            .start(MyScanActivity::class.java)
                                            
                                            //MyScanActivity自定义activity ,可继承至ScanCodeActivity重写layout

Step 4. 生成二维码:
       /**
     * 生成二维码
     *
     * @param text 需要生成二维码的文字、网址等
     * @param size 需要生成二维码的大小 1:1 500px
     * @param color 需要生成二维码的颜色 类型：int， 0xffffffff(白色)， 0xff000000(黑色)
     * @param logo 需要生成二维码的logo 类型：bitmap
     * @return bitmap
     */
    /**
     * 生成二维码，默认大小为50500
     *
     * @param text 需要生成二维码的文字、网址等
     * @return bitmap
     */
    createQRCode(text)//default size:500px
    createQRCode(text, 500)
    createQRCode(text, 500, color)
    createQRcodeWithLogo(text, logo)
    createQRcodeWithLogo(text, size, logo, logoWith, logoHigh, logoRaduisX, logoRaduisY)
    
    
    Step 5. 可调用自定义camera：
        例如：自定义activity 继承CameraActivity，重写layout
    


