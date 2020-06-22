package com.xiaoe.shop.sdk.rn.internal;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class RNUtils {

    private RNUtils() {
    }

    public static Drawable getDrawable(Context context, String imageName) {
        final Resources res = context.getResources();
        final int id = res.getIdentifier(imageName, "drawable", context.getPackageName());
        final Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = context.getDrawable(id);
        } else {
            drawable = res.getDrawable(id);
        }
        return drawable;
    }

    public static void sendEvent(ReactContext reactContext,
                                 String eventName,
                                 WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    public static boolean getBoolean(ReadableMap map, String key, boolean fallback) {
        return map.hasKey(key) ? map.getBoolean(key) : fallback;
    }

    public static int getInt(ReadableMap map, String key, int fallback) {
        return map.hasKey(key) ? map.getInt(key) : fallback;
    }

    public static String getString(ReadableMap map, String key) {
        return getString(map, key, null);
    }

    public static String getString(ReadableMap map, String key, String fallback) {
        return map.hasKey(key) ? map.getString(key) : fallback;
    }
}
