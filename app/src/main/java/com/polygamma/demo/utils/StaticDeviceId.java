package com.polygamma.demo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class StaticDeviceId {

    private static final String PREF = "origin_device";
    private static final String KEY = "static_id";

    public static String get(Context ctx) {
        SharedPreferences sp =
                ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        String id = sp.getString(KEY, null);
        if (id == null) {
            id = UUID.randomUUID().toString();
            sp.edit().putString(KEY, id).apply();
        }
        return id;
    }
}
