package com.zyd.kedaxunfeivoicetestapplication.voice.speak;

import com.zyd.kedaxunfeivoicetestapplication.bean.VoiceExtraBean;

public interface OnSpeakListener {

    void onError(String msg, VoiceExtraBean voiceExtraBean);

    void onStart(VoiceExtraBean voiceExtraBean);

    void onEnd(VoiceExtraBean voiceExtraBean);
}