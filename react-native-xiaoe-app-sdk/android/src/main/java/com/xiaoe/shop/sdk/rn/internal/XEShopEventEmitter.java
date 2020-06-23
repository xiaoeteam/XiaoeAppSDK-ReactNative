package com.xiaoe.shop.sdk.rn.internal;


import com.xiaoe.shop.webcore.core.bridge.JsBridgeListener;
import com.xiaoe.shop.webcore.core.bridge.JsCallbackResponse;

public class XEShopEventEmitter implements JsBridgeListener {
    private static volatile XEShopEventEmitter sInstance = new XEShopEventEmitter();

    private JsBridgeListener mListener;

    private XEShopEventEmitter() {
    }

    public static XEShopEventEmitter getInstance() {
        return sInstance;
    }

    public void observe(JsBridgeListener listener) {
        mListener = listener;
    }

    public void release() {
        mListener = null;
    }

    @Override
    public void onJsInteract(int actionType, JsCallbackResponse response) {
        mListener.onJsInteract(actionType, response);
    }
}
