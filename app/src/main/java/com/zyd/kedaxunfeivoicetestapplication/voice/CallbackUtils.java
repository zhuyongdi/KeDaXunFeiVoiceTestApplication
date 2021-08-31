package com.zyd.kedaxunfeivoicetestapplication.voice;

import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.RecognizerResult;
import com.zyd.kedaxunfeivoicetestapplication.bean.VoiceExtraBean;
import com.zyd.kedaxunfeivoicetestapplication.utils.GsonUtil;
import com.zyd.kedaxunfeivoicetestapplication.voice.recog.OnRecognizeListener;
import com.zyd.kedaxunfeivoicetestapplication.voice.recog.RecognizeBean;
import com.zyd.kedaxunfeivoicetestapplication.voice.speak.OnSpeakListener;
import com.zyd.kedaxunfeivoicetestapplication.voice.wakeup.OnWakeListener;

import java.util.List;

public class CallbackUtils {

    public static void notifyOnRecognizeListener_onStart(List<OnRecognizeListener> onRecognizeListenerList, VoiceExtraBean extraBean) {
        for (OnRecognizeListener listener : onRecognizeListenerList) {
            if (listener != null) {
                listener.onStart(extraBean);
            }
        }
    }

    public static void notifyOnRecognizeListener_onError(List<OnRecognizeListener> onRecognizeListenerList, String errorMsg, VoiceExtraBean voiceExtraBean) {
        for (OnRecognizeListener listener : onRecognizeListenerList) {
            if (listener != null) {
                listener.onError(errorMsg, voiceExtraBean);
            }
        }
    }

    public static void notifyOnRecognizeListener_onEnd(List<OnRecognizeListener> onRecognizeListenerList, VoiceExtraBean voiceExtraBean) {
        for (OnRecognizeListener listener : onRecognizeListenerList) {
            if (listener != null) {
                listener.onEnd(voiceExtraBean);
            }
        }
    }

    public static void notifyOnRecognizeListener_onStop(List<OnRecognizeListener> onRecognizeListenerList, VoiceExtraBean voiceExtraBean) {
        for (OnRecognizeListener listener : onRecognizeListenerList) {
            if (listener != null) {
                listener.onStop(voiceExtraBean);
            }
        }
    }

    public static void notifyOnRecognizeListener_onResult_ThisCalledOnce(List<OnRecognizeListener> onRecognizeListenerList,
                                                                         List<RecognizerResult> resultList,
                                                                         VoiceExtraBean voiceExtraBean) {
        StringBuilder sb = new StringBuilder();
        for (RecognizerResult r : resultList) {
            if (r != null) {
                RecognizeBean bean = GsonUtil.fromJson(r.getResultString(), new TypeToken<RecognizeBean>() {
                }.getType());
                if (bean != null) {
                    List<RecognizeBean.WS> ws = bean.getWs();
                    if (ws != null && !ws.isEmpty()) {
                        for (RecognizeBean.WS w : ws) {
                            List<RecognizeBean.CW> cw = w.getCw();
                            if (cw != null && !cw.isEmpty()) {
                                RecognizeBean.CW c = cw.get(0);
                                sb.append(c.getW());
                            }
                        }
                    }
                }
            }
        }
        for (OnRecognizeListener listener : onRecognizeListenerList) {
            if (listener != null) {
                listener.onResult_ThisCalledOnce(format(sb.toString().trim()), voiceExtraBean);
            }
        }
    }

    /**
     * 去除字符串中所有的" "和"。"，
     * 去除字符串末尾所有的"."
     */
    private static String format(String s) {
        if (s == null) {
            return "";
        }
        int l = s.length();
        StringBuilder sb = new StringBuilder();
        for (int i = l - 1; i >= 0; i--) {
            String c = s.charAt(i) + "";
            if (!" ".equals(c)
                    && !"。".equals(c)) {
                sb.insert(0, s.charAt(i));
            }
        }
        System.out.println(sb.toString());
        int newL = sb.length();
        for (int i = newL - 1; i >= 0; i--) {
            String c = sb.charAt(i) + "";
            if (".".equals(c)) {
                sb.deleteCharAt(i);
                continue;
            }
            break;
        }
        return sb.toString();
    }

    public static void notifyOnRecognizeListener_onResult_ThisCalledMany(List<OnRecognizeListener> onRecognizeListenerList,
                                                                         List<RecognizerResult> resultList,
                                                                         VoiceExtraBean voiceExtraBean) {
        StringBuilder sb = new StringBuilder();
        for (RecognizerResult r : resultList) {
            if (r != null) {
                RecognizeBean bean = GsonUtil.fromJson(r.getResultString(), new TypeToken<RecognizeBean>() {
                }.getType());
                if (bean != null) {
                    List<RecognizeBean.WS> ws = bean.getWs();
                    if (ws != null && !ws.isEmpty()) {
                        for (RecognizeBean.WS w : ws) {
                            List<RecognizeBean.CW> cw = w.getCw();
                            if (cw != null && !cw.isEmpty()) {
                                RecognizeBean.CW c = cw.get(0);
                                sb.append(c.getW());
                            }
                        }
                    }
                }
            }
        }
        for (OnRecognizeListener listener : onRecognizeListenerList) {
            if (listener != null) {
                listener.onResult_ThisCalledMany(sb.toString(), voiceExtraBean);
            }
        }
    }

    public static void notifyOnWakeListener_onError(List<OnWakeListener> onWakeListenerList, String errorMsg) {
        for (OnWakeListener listener : onWakeListenerList) {
            if (listener != null) {
                listener.onError(errorMsg);
            }
        }
    }

    public static void notifyOnWakeListener_onStart(List<OnWakeListener> onWakeListenerList) {
        for (OnWakeListener listener : onWakeListenerList) {
            if (listener != null) {
                listener.onStart();
            }
        }
    }

    public static void notifyOnWakeListener_onWakeUp(List<OnWakeListener> onWakeListenerList) {
        for (OnWakeListener listener : onWakeListenerList) {
            if (listener != null) {
                listener.onWakeUp();
            }
        }
    }

    public static void notifyOnSpeakListener_onError(List<OnSpeakListener> onSpeakListenerList, String errorMsg, VoiceExtraBean voiceExtraBean) {
        for (OnSpeakListener listener : onSpeakListenerList) {
            if (listener != null) {
                listener.onError(errorMsg, voiceExtraBean);
            }
        }
    }

    public static void notifyOnSpeakListener_onStart(List<OnSpeakListener> onSpeakListenerList, VoiceExtraBean voiceExtraBean) {
        for (OnSpeakListener listener : onSpeakListenerList) {
            if (listener != null) {
                listener.onStart(voiceExtraBean);
            }
        }
    }

    public static void notifyOnSpeakListener_onEnd(List<OnSpeakListener> onSpeakListenerList, VoiceExtraBean voiceExtraBean) {
        for (OnSpeakListener listener : onSpeakListenerList) {
            if (listener != null) {
                listener.onEnd(voiceExtraBean);
            }
        }
    }

}
