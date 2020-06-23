package com.xiaoe.shop.sdk.rn;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.xiaoe.shop.sdk.rn.internal.RNUtils;
import com.xiaoe.shop.sdk.rn.internal.XEShopDecoration;
import com.xiaoe.shop.sdk.rn.internal.XEShopEventEmitter;
import com.xiaoe.shop.sdk.rn.internal.XEShopModel;
import com.xiaoe.shop.webcore.core.XEToken;
import com.xiaoe.shop.webcore.core.XiaoEWeb;
import com.xiaoe.shop.webcore.core.bridge.JsBridgeListener;
import com.xiaoe.shop.webcore.core.bridge.JsCallbackResponse;
import com.xiaoe.shop.webcore.core.bridge.JsInteractType;

import java.util.HashMap;
import java.util.Map;

import static com.xiaoe.shop.sdk.rn.internal.RNUtils.getBoolean;
import static com.xiaoe.shop.sdk.rn.internal.RNUtils.getInt;
import static com.xiaoe.shop.sdk.rn.internal.RNUtils.getString;

public class XEShopSDKModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
    private static final String MODULE_NAME = "XEShopSDK";

    private static final String EVENT_LISTENER_NAME = "XEShopSDKEvents";

    public enum XEShopEvent {
        EVENT_LOGIN(501),
        EVENT_SHARE(503),
        EVENT_TITLE_RECEIVE(504),
        EVENT_NOTICE_OUT_LINK(505),
        ;

        int code;

        XEShopEvent(int code) {
            this.code = code;
        }
    }

    XEShopSDKModule(@NonNull final ReactApplicationContext reactContext) {
        super(reactContext);

        reactContext.addLifecycleEventListener(this);

        // 初始化SDK回调
        XEShopEventEmitter.getInstance().observe(new JsBridgeListener() {
            @Override
            public void onJsInteract(int action, JsCallbackResponse jsCallbackResponse) {
                WritableMap event = new Event(action, jsCallbackResponse).asWritableMap();
                if (event != null) {
                    RNUtils.sendEvent(reactContext, EVENT_LISTENER_NAME, event);
                }
            }
        });
    }

    @NonNull
    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        Map<String, Object> constants = new HashMap<>();
        constants.put("EVENT_LISTENER", EVENT_LISTENER_NAME);
        for (XEShopEvent event : XEShopEvent.values()) {
            constants.put(event.name(), event.code);
        }
        return constants;
    }

    @ReactMethod
    public void initConfig(ReadableMap params, Promise promise) {
        final String appId = getString(params, "appId");
        final String clientId = getString(params, "clientId");
        final boolean isOpenLog = getBoolean(params, "isOpenLog", false);
        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(clientId)) {
            promise.reject(new IllegalArgumentException("Missing appId or clientId"));
            return;
        }

        XiaoEWeb.init(getReactApplicationContext(), appId, clientId, XiaoEWeb.WebViewType.X5);
        XiaoEWeb.isOpenLog(isOpenLog);
        promise.resolve(null);
    }

    @ReactMethod
    public void setTitle(String title) {
        XEShopModel.getInstance().title.postValue(title);
    }

    @ReactMethod
    public void setNavStyle(ReadableMap params) {
        XEShopDecoration.sNavStyle.titleColor(getString(params, "titleColor"));
        XEShopDecoration.sNavStyle.titleFontSize(getInt(params, "titleFontSize", -1));
        XEShopDecoration.sNavStyle.backgroundColor(getString(params, "backgroundColor"));
        XEShopDecoration.sNavStyle.backIconImageName(getString(params, "backIconImageName"));
        XEShopDecoration.sNavStyle.closeIconImageName(getString(params, "closeIconImageName"));
        XEShopDecoration.sNavStyle.shareIconImageName(getString(params, "shareIconImageName"));
    }

    @ReactMethod
    public void open(String shopUrl) {
        if (TextUtils.isEmpty(shopUrl)) {
            return;
        }

        XEShopActivity.launch(context(), shopUrl);
    }

    @ReactMethod
    public void synchronizeToken(String tokenKey, String tokenValue) {
        final XEToken token = new XEToken(tokenKey, tokenValue);
        XEShopModel.getInstance().token.postValue(token);
    }

    @ReactMethod
    public void logoutSDK() {
        XiaoEWeb.userLogout(getReactApplicationContext());
    }

    @ReactMethod
    public void isLog(boolean isOpenLog) {
        XiaoEWeb.isOpenLog(isOpenLog);
    }

    @ReactMethod
    public void getSdkVersion(Promise promise) {
        final String sdkVersion = XiaoEWeb.getSdkVersion();

        if (promise != null) {
            promise.resolve(sdkVersion);
        }
    }

    @Override
    public void onHostResume() {
    }

    @Override
    public void onHostPause() {
    }

    @Override
    public void onHostDestroy() {
        release();
    }

    private void release() {
        XEShopEventEmitter.getInstance().release();
    }

    private Context context() {
        Context context = getCurrentActivity();
        return context != null ? context : getReactApplicationContext();
    }

    private static class Event {
        private int mCode;
        private String mMessage;
        private String mData;

        Event(int action, JsCallbackResponse response) {
            switch (action) {
                case JsInteractType.LOGIN_ACTION:
                    mCode = XEShopEvent.EVENT_LOGIN.code;
                    mMessage = "登录通知";
                    break;
                case JsInteractType.SHARE_ACTION:
                    mCode = XEShopEvent.EVENT_SHARE.code;
                    mMessage = "分享通知";
                    break;
                case JsInteractType.TITLE_RECEIVE:
                    mCode = XEShopEvent.EVENT_TITLE_RECEIVE.code;
                    mMessage = "标题通知";
                    break;
                case JsInteractType.NOTICE_OUT_LINK_ACTION:
                    mCode = XEShopEvent.EVENT_NOTICE_OUT_LINK.code;
                    mMessage = "外链通知";
                    break;
                default:
                    mCode = action;
                    mMessage = "";
                    break;
            }
            mData = response.getResponseData();
        }

        WritableMap asWritableMap() {
            WritableMap result = Arguments.createMap();
            result.putInt("code", mCode);
            result.putString("message", mMessage);
            result.putString("data", mData);
            return result;
        }
    }
}
