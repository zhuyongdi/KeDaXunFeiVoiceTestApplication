package com.zyd.kedaxunfeivoicetestapplication.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class GsonUtil {

    public static <T> T toFromJson(Object src, Type type) {
        return fromJson(toJson(src, type), type);
    }

    public static <T> T fromJson(String json, Type type) {
        try {
            return new Gson().fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toJson(Object src, Type type) {
        try {
            return new Gson().toJson(src, type);
        } catch (Exception e) {
            //to do nothing
        }
        return null;
    }

}
