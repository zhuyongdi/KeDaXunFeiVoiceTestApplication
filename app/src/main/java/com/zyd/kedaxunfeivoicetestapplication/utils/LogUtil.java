package com.zyd.kedaxunfeivoicetestapplication.utils;

import android.util.Log;

import com.zyd.kedaxunfeivoicetestapplication.Configuration;

/**
 * 项目的日志工具类
 */
public class LogUtil {

    private static final String TAG = "zzzzzzzzzz";

    private LogUtil() {
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void w(String msg) {
        w(TAG, msg);
    }

    public static void e(String tag, String msg) {
        if (Configuration.LOG_DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (Configuration.LOG_DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (Configuration.LOG_DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (Configuration.LOG_DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (Configuration.LOG_DEBUG) {
            Log.w(tag, msg);
        }
    }
}
