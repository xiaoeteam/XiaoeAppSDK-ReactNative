package com.xiaoe.shop.sdk.rn.internal;

import com.xiaoe.shop.webcore.XEToken;

public class XEShopModel {
    public XELiveData<XEToken> token = new XELiveData<>();
    public XELiveData<String> title = new XELiveData<>();

    private static volatile XEShopModel sInstance = new XEShopModel();

    private XEShopModel() {
    }

    public static XEShopModel getInstance() {
        return sInstance;
    }
}
