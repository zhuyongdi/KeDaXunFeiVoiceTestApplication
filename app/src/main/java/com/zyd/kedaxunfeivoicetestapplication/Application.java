package com.zyd.kedaxunfeivoicetestapplication;

import android.content.Context;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.zyd.kedaxunfeivoicetestapplication.utils.LogUtil;
import com.zyd.kedaxunfeivoicetestapplication.voice.VoiceManager;

public class Application extends android.app.Application {

    public static Context context;
    public static SpeechUtility speechUtility;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        context = base;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initKeDaXunFeiSDK();
    }

    /**
     * 初始化科大讯飞语音听写SDK
     */
    public static void initKeDaXunFeiSDK() {
        speechUtility = SpeechUtility.createUtility(context, SpeechConstant.APPID + "=" + VoiceManager.APP_ID);
        if (speechUtility == null) {
            LogUtil.e("初始化语音识别SDK失败");
        } else {
            LogUtil.e("初始化语音识别SDK成功");
            VoiceManager.getInstance().initEngine(context);
        }
    }

}
