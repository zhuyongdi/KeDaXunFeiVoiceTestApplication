package com.zyd.kedaxunfeivoicetestapplication.voice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.zyd.kedaxunfeivoicetestapplication.bean.VoiceExtraBean;
import com.zyd.kedaxunfeivoicetestapplication.utils.LogUtil;
import com.zyd.kedaxunfeivoicetestapplication.voice.recog.OnRecognizeListener;
import com.zyd.kedaxunfeivoicetestapplication.voice.speak.OnSpeakListener;
import com.zyd.kedaxunfeivoicetestapplication.voice.wakeup.OnWakeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VoiceManager {

    public static final String APP_ID = "550a414a";

    public static final List<Object[]> SPEAKERS = Arrays.asList(
            new Object[]{0, "小燕—女青、中英、普通话", "xiaoyan"},
            new Object[]{1, "小宇—男青、中英、普通话", "xiaoyu"},
            new Object[]{2, "凯瑟琳—女青、英", "catherine"},
            new Object[]{3, "亨利—男青、英", "henry"},
            new Object[]{4, "玛丽—女青、英", "vimary"},
            new Object[]{5, "小研—女青、中英、普通话", "vixy"},
            new Object[]{6, "小琪—女青、中英、普通话", "xiaoqi"},
            new Object[]{7, "小峰—男青、中英、普通话", "vixf"},
            new Object[]{8, "小梅—女青、中英、粤语", "xiaomei"},
            new Object[]{9, "小莉—女青、中英、台湾普通话", "xiaolin"},
            new Object[]{10, "小蓉—女青、中、四川话", "xiaorong"},
            new Object[]{11, "小芸—女青、中、东北话", "xiaoqian"},
            new Object[]{12, "小坤—男青、中、河南话", "xiaokun"},
            new Object[]{13, "小强—男青、中、湖南话", "xiaoqiang"},
            new Object[]{14, "小莹—女青、中、陕西话", "vixying"},
            new Object[]{15, "小新—男童、中、普通话", "xiaoxin"},
            new Object[]{16, "楠楠—女童、中、普通话", "nannan"},
            new Object[]{17, "老孙—男老、中、普通话", "vils"}
    );

    private static final String TAG = "VoiceManager";

    private static SpeechRecognizer mRecognizer;
    private static VoiceWakeuper mWakeuper;
    private static SpeechSynthesizer mSpeaker;

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private Context mContext;

    private List<OnRecognizeListener> mOnRecognizeListenerList;
    private List<OnWakeListener> mOnWakeListenerList;
    private List<OnSpeakListener> mOnSpeakListenerList;

    private static final int INIT_RECOGNIZER = 1;
    private static final int INIT_WAKEUPER = 2;
    private static final int INIT_SPEAKER = 3;

    private static final List<RecognizerResult> resultList = new ArrayList<>();

    private static VoiceExtraBean mRecognizeVoiceExtraBean;
    private static VoiceExtraBean mSpeakVoiceExtraBean;

    public static VoiceManager getInstance() {
        return CH.INST;
    }

    private VoiceManager() {
        initInternal();
    }

    private static class CH {
        @SuppressLint("StaticFieldLeak")
        private static final VoiceManager INST = new VoiceManager();
    }

    private void initInternal() {
        mOnRecognizeListenerList = new ArrayList<>();
        mOnWakeListenerList = new ArrayList<>();
        mOnSpeakListenerList = new ArrayList<>();

        mHandlerThread = new HandlerThread("Thread-VoiceManager");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case INIT_RECOGNIZER:
                        initRecognizer();
                        break;
                    case INIT_WAKEUPER:
                        initWakeuper();
                        break;
                    case INIT_SPEAKER:
                        initSpeaker();
                        break;
                }
            }
        };
    }

    public void initEngine(Context context) {
        if (mContext == null) {
            mContext = context;
        }
        if (mRecognizer == null) {
            mHandler.sendEmptyMessage(INIT_RECOGNIZER);
        }
        if (mWakeuper == null) {
            mHandler.sendEmptyMessage(INIT_WAKEUPER);
        }
        if (mSpeaker == null) {
            mHandler.sendEmptyMessage(INIT_SPEAKER);
        }
    }

    private void initRecognizer() {
        mRecognizer = SpeechRecognizer.createRecognizer(mContext, code -> {
            if (code == 0) {
                LogUtil.e("初始化Recognizer成功");

                //设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；或直接清空所有参数，具体可参考 DEMO 的示例。
                mRecognizer.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);
                mRecognizer.setParameter(SpeechConstant.SUBJECT, null);
                //设置返回结果格式，目前支持json,xml以及plain 三种格式，其中plain为纯听写文本内容
                mRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");
                //此处engineType为“cloud”
                mRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");
                //设置语音输入语言，zh_cn为简体中文
                mRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                //设置结果返回语言
                mRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin");
                //设置语音前端点:静音超时时间，单位ms，即用户多长时间不说话则当做超时处理
                //取值范围{1000～10000}
                mRecognizer.setParameter(SpeechConstant.VAD_BOS, "4000");
                //设置语音后端点:后端点静音检测时间，单位ms，即用户停止说话多长时间内即认为不再输入，
                //自动停止录音，范围{0~10000}
                mRecognizer.setParameter(SpeechConstant.VAD_EOS, "2000");
                //设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
                mRecognizer.setParameter(SpeechConstant.ASR_PTT, "1");
            } else {
                LogUtil.e("初始化Recognizer失败，错误码=" + code);
            }
        });
    }

    private void initWakeuper() {
        mWakeuper = VoiceWakeuper.createWakeuper(mContext, null);
        if (mWakeuper != null) {
            LogUtil.e("初始化Wakeuper成功");
            //清空参数
            mWakeuper.setParameter(SpeechConstant.PARAMS, null);
            //唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
            mWakeuper.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + 1450);
            //设置唤醒模式
            mWakeuper.setParameter(SpeechConstant.IVW_SST, "wakeup");
            //设置持续进行唤醒
            mWakeuper.setParameter(SpeechConstant.KEEP_ALIVE, "1");
            //设置闭环优化网络模式
            mWakeuper.setParameter(SpeechConstant.IVW_NET_MODE, "0");
            //设置唤醒资源路径
            mWakeuper.setParameter(SpeechConstant.IVW_RES_PATH, getIVWResource());
        } else {
            LogUtil.e("初始化Wakeuper失败");
        }
    }

    private void initSpeaker() {
        mSpeaker = SpeechSynthesizer.createSynthesizer(mContext, code -> {
            if (code == 0) {
                LogUtil.e("初始化Speaker成功");

                //清空参数
                mSpeaker.setParameter(SpeechConstant.PARAMS, null);
                //设置引擎类型为"在线合成"
                mSpeaker.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
                //支持实时音频返回，仅在synthesizeToUri条件下支持
                mSpeaker.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1");
                //设置在线合成发音人
                mSpeaker.setParameter(SpeechConstant.VOICE_NAME, SPEAKERS.get(0)[2] + "");
                //设置合成语速
                mSpeaker.setParameter(SpeechConstant.SPEED, "50");
                //设置合成音调
                mSpeaker.setParameter(SpeechConstant.PITCH, "50");
                //设置合成音量
                mSpeaker.setParameter(SpeechConstant.VOLUME, "100");

                //设置播放器音频流类型
                mSpeaker.setParameter(SpeechConstant.STREAM_TYPE, "3");
                // 设置播放合成音频打断音乐播放，默认为true
                mSpeaker.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false");
                // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
                mSpeaker.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
                mSpeaker.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
            } else {
                LogUtil.e("初始化Speaker失败，错误码=" + code);
            }
        });
    }

    private String getIVWResource() {
        return ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "ivw/" + APP_ID + ".jet");
    }

    public void startRecognize(VoiceExtraBean voiceExtraBean) {
        mRecognizeVoiceExtraBean = voiceExtraBean;
        if (mRecognizer == null) {
            CallbackUtils.notifyOnRecognizeListener_onError(mOnRecognizeListenerList, "recognizer未初始化", mSpeakVoiceExtraBean);
            return;
        }
        int code = mRecognizer.startListening(new RecognizerListener() {
            @Override
            public void onVolumeChanged(int i, byte[] bytes) {
//                LogUtil.e("recognizer--检测到声音变化");
            }

            @Override
            public void onBeginOfSpeech() {
                LogUtil.e("recognizer--开始识别");
                resultList.clear();
                CallbackUtils.notifyOnRecognizeListener_onStart(mOnRecognizeListenerList, mRecognizeVoiceExtraBean);
            }

            @Override
            public void onEndOfSpeech() {
                LogUtil.e("recognizer--结束识别");
                CallbackUtils.notifyOnRecognizeListener_onEnd(mOnRecognizeListenerList, mRecognizeVoiceExtraBean);
            }

            @Override
            public void onResult(RecognizerResult result, boolean b) {
                String resultString = null;
                if (result != null) {
                    resultString = result.getResultString();
                }
                if (b) {
                    LogUtil.e("recognizer--识别到结果（最终）：" + resultString);
                } else {
                    LogUtil.e("recognizer--识别到结果（临时）：" + resultString);
                }
                resultList.add(result);
                CallbackUtils.notifyOnRecognizeListener_onResult_ThisCalledMany(mOnRecognizeListenerList, resultList, mRecognizeVoiceExtraBean);
                if (b) {
                    CallbackUtils.notifyOnRecognizeListener_onResult_ThisCalledOnce(mOnRecognizeListenerList, resultList, mRecognizeVoiceExtraBean);
                }
            }

            @Override
            public void onError(SpeechError speechError) {
                String errorStr = null;
                if (speechError != null) {
                    errorStr = speechError.toString();
                }
                LogUtil.e("recognizer--识别出错：" + errorStr);
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {
                String bundleString = null;
                if (bundle != null) {
                    bundleString = bundle.toString();
                }
                LogUtil.e("RecognizerListener--onEvent：i=" + i + "，i1=" + i1 + "，i2=" + i2 + "，bundle=" + bundleString);
            }
        });
        if (code != 0) {
            LogUtil.e("recognizer--唤醒失败，错误码=" + code);
            CallbackUtils.notifyOnRecognizeListener_onError(mOnRecognizeListenerList, "识别失败，错误码=" + code, mRecognizeVoiceExtraBean);
        }
    }

    public void startWakeup() {
        if (mWakeuper == null) {
            LogUtil.e("wakeuper唤醒失败，wakeuper未初始化");
            CallbackUtils.notifyOnWakeListener_onError(mOnWakeListenerList, "Wakeuper未初始化");
            return;
        }
        int code = mWakeuper.startListening(new WakeuperListener() {
            @Override
            public void onBeginOfSpeech() {
                LogUtil.e("wakeuper--开始唤醒");
                CallbackUtils.notifyOnWakeListener_onStart(mOnWakeListenerList);
            }

            @Override
            public void onResult(WakeuperResult result) {
                String resultString;
                try {
                    String text = result.getResultString();
                    JSONObject object;
                    object = new JSONObject(text);
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("【RAW】 " + text);
                    buffer.append("\n");
                    buffer.append("【操作类型】" + object.optString("sst"));
                    buffer.append("\n");
                    buffer.append("【唤醒词id】" + object.optString("id"));
                    buffer.append("\n");
                    buffer.append("【得分】" + object.optString("score"));
                    buffer.append("\n");
                    buffer.append("【前端点】" + object.optString("bos"));
                    buffer.append("\n");
                    buffer.append("【尾端点】" + object.optString("eos"));
                    resultString = buffer.toString();
                } catch (JSONException e) {
                    resultString = "结果解析出错";
                    e.printStackTrace();
                }
                LogUtil.e("wakeuper--唤醒成功：" + resultString);
                CallbackUtils.notifyOnWakeListener_onWakeUp(mOnWakeListenerList);
            }

            @Override
            public void onError(SpeechError speechError) {
                LogUtil.e("wakeuper--唤醒出错：" + speechError.getPlainDescription(true));
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {
                LogUtil.e("wakeuper--onEvent");
            }

            @Override
            public void onVolumeChanged(int i) {
//                LogUtil.e("wakeuper--检测到声音变化：" + i);
            }
        });
        if (code != 0) {
            LogUtil.e("wakeuper--唤醒失败，错误码=" + code);
            CallbackUtils.notifyOnWakeListener_onError(mOnWakeListenerList, "播放失败，错误码=" + code);
        }
    }

    public void stopRecognize() {
        if (mRecognizer == null) {
            LogUtil.e("Recognizer取消成功，Recognizer未初始化");
            return;
        }
        mRecognizer.stopListening();
    }

    public void cancelRecognize() {
        if (mRecognizer == null) {
            LogUtil.e("Recognizer取消成功，Recognizer未初始化");
            return;
        }
        mRecognizer.cancel();
    }

    public void stopWakeup() {
        if (mWakeuper == null) {
            LogUtil.e("wakeuper停止成功，wakeuper未初始化");
            return;
        }
        mWakeuper.stopListening();
    }

    public void startSpeak(String text, VoiceExtraBean voiceExtraBean) {
        mSpeakVoiceExtraBean = voiceExtraBean;
        if (TextUtils.isEmpty(text)) {
            CallbackUtils.notifyOnSpeakListener_onError(mOnSpeakListenerList, "播放内容为空", mSpeakVoiceExtraBean);
            return;
        }
        if (mSpeaker == null) {
            CallbackUtils.notifyOnSpeakListener_onError(mOnSpeakListenerList, "Speaker未初始化", mSpeakVoiceExtraBean);
            return;
        }
        int code = mSpeaker.startSpeaking(text, new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {
                LogUtil.e("speaker--开始播放");
            }

            @Override
            public void onBufferProgress(int percent, int i1, int i2, String s) {
                LogUtil.e("speaker--合成进度=" + percent);
            }

            @Override
            public void onSpeakPaused() {
                LogUtil.e("speaker--暂停播放");
            }

            @Override
            public void onSpeakResumed() {
                LogUtil.e("speaker--继续播放");
            }

            @Override
            public void onSpeakProgress(int percent, int i1, int i2) {
                LogUtil.e("speaker--播放进度=" + percent);
            }

            @Override
            public void onCompleted(SpeechError error) {
                if (error != null) {
                    LogUtil.e("speaker--播放完成（有异常）" + error.getPlainDescription(true));
                    CallbackUtils.notifyOnSpeakListener_onError(mOnSpeakListenerList, "播放出错：" + error.getPlainDescription(true), mSpeakVoiceExtraBean);
                } else {
                    CallbackUtils.notifyOnSpeakListener_onEnd(mOnSpeakListenerList, mSpeakVoiceExtraBean);
                    LogUtil.e("speaker--播放完成（无异常）");
                }
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {
                LogUtil.e("speaker--onEvent");
            }
        });
        if (code != 0) {
            LogUtil.e("speaker--播放失败，错误码=" + code);
            CallbackUtils.notifyOnSpeakListener_onError(mOnSpeakListenerList, "播放失败，错误码=" + code, mSpeakVoiceExtraBean);
        }
    }

    public void pauseSpeak() {
        if (mSpeaker == null) {
            LogUtil.e("Speaker暂停失败，Speaker未初始化");
            return;
        }
        mSpeaker.pauseSpeaking();
    }

    public void resumeSpeak() {
        if (mSpeaker == null) {
            LogUtil.e("Speaker恢复失败，Speaker未初始化");
            return;
        }
        mSpeaker.resumeSpeaking();
    }

    public void stopSpeak() {
        if (mSpeaker == null) {
            LogUtil.e("Speaker停止失败，Speaker未初始化");
            return;
        }
        mSpeaker.stopSpeaking();
    }
}
