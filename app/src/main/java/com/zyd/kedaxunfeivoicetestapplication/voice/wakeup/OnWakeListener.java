package com.zyd.kedaxunfeivoicetestapplication.voice.wakeup;

public interface OnWakeListener {

    void onError(String msg);

    void onStart();

    void onWakeUp();

}