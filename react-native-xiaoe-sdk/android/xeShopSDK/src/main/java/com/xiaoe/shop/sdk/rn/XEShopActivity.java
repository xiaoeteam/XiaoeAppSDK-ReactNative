package com.xiaoe.shop.sdk.rn;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xiaoe.shop.sdk.rn.internal.RNUtils;
import com.xiaoe.shop.sdk.rn.internal.XELiveData;
import com.xiaoe.shop.sdk.rn.internal.XEShopDecoration;
import com.xiaoe.shop.sdk.rn.internal.XEShopEventEmitter;
import com.xiaoe.shop.sdk.rn.internal.XEShopModel;
import com.xiaoe.shop.webcore.XEToken;
import com.xiaoe.shop.webcore.XiaoEWeb;

public class XEShopActivity extends AppCompatActivity {
    public static String EXTRA_SHOP_URL = "shop_url";

    private ViewGroup mWebLayout;
    private TextView mTitleView;

    private XiaoEWeb mXiaoEWeb;

    public static boolean launch(Context context, String shopUrl) {
        if (context == null || TextUtils.isEmpty(shopUrl)) {
            return false;
        }

        Intent intent = new Intent(context, XEShopActivity.class);
        intent.putExtra(EXTRA_SHOP_URL, shopUrl);

        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        setContentView(R.layout.mo_xeshopsdk_activity_xe_shop);

        final Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        final String shopUrl = intent.getStringExtra(EXTRA_SHOP_URL);
        if (TextUtils.isEmpty(shopUrl)) {
            finish();
            return;
        }

        initView();

        mXiaoEWeb = XiaoEWeb.with(this)
                .setWebParent(mWebLayout)
                .useDefaultUI()
                .useDefaultTopIndicator()
                .buildWeb()
                .loadUrl(shopUrl);

        mXiaoEWeb.setJsBridgeListener(XEShopEventEmitter.getInstance());

        // 启动时同步登录态
        XEShopModel.getInstance().token.observe(new XELiveData.Observer<XEToken>() {
            @Override
            public void onChanged(XEToken token) {
                syncToken(token);
            }
        });
    }

    private void initView() {
        mWebLayout = (ViewGroup) findViewById(R.id.web_layout);
        ImageView backView = (ImageView) findViewById(R.id.xe_sdk_back_img);
        ImageView closeView = (ImageView) findViewById(R.id.xe_sdk_close_img);
        ImageView shareView = (ImageView) findViewById(R.id.xe_sdk_share_img);
        ViewGroup titleLayout = (ViewGroup) findViewById(R.id.xe_sdk_title_layout);
        mTitleView = (TextView) findViewById(R.id.xe_sdk_title_tv);

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mXiaoEWeb == null || !mXiaoEWeb.handlerBack()) {
                    finish();
                }
            }
        });

        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mXiaoEWeb != null) {
                    mXiaoEWeb.share();
                }
            }
        });

        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Title
        XEShopModel.getInstance().title.observe(new XELiveData.Observer<String>() {
            @Override
            public void onChanged(String title) {
                mTitleView.setText(title);
            }
        });

        // NavStyle
        final NavStyle style = XEShopDecoration.sNavStyle;
        if (style.titleFontSize > 0) {
            mTitleView.setTextSize(style.titleFontSize);
        }
        if (style.titleColor != null) {
            mTitleView.setTextColor(Color.parseColor(style.titleColor));
        }
        if (style.backgroundColor != null) {
            titleLayout.setBackgroundColor(Color.parseColor(style.backgroundColor));
        }
        setImage(this, backView, style.backIconImageName, R.mipmap.mo_xeshopsdk_back_icon);
        setImage(this, closeView, style.closeIconImageName, R.mipmap.mo_xeshopsdk_close_icon);
        setImage(this, shareView, style.shareIconImageName, R.mipmap.mo_xeshopsdk_share_icon);
    }

    private void setImage(Context context, ImageView imageView, String imageName, int fallback) {
        if (imageName != null) {
            Drawable drawable = RNUtils.getDrawable(context, imageName);
            if (drawable != null) {
                imageView.setImageDrawable(drawable);
                return;
            }
        }

        imageView.setImageResource(fallback);
    }

    private void syncToken(XEToken token) {
        if (token == null) {
            return;
        }
        if (TextUtils.isEmpty(token.getTokenKey()) || TextUtils.isEmpty(token.getTokenValue())) {
            return;
        }

        if (mXiaoEWeb != null) {
            mXiaoEWeb.sync(token);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mXiaoEWeb != null) {
            mXiaoEWeb.webLifeCycle().onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mXiaoEWeb != null) {
            mXiaoEWeb.webLifeCycle().onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XEShopModel.getInstance().token.removeObserver();
        XEShopModel.getInstance().title.removeObserver();

        if (mXiaoEWeb != null) {
            mXiaoEWeb.webLifeCycle().onDestroy();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mXiaoEWeb != null && mXiaoEWeb.handlerKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static class NavStyle {
        String titleColor;
        int titleFontSize;
        String backgroundColor;
        String backIconImageName;
        String closeIconImageName;
        String shareIconImageName;

        public NavStyle titleColor(String color) {
            if (checkColor(color)) {
                this.titleColor = color;
            }
            return this;
        }

        public NavStyle titleFontSize(int fontSize) {
            if (checkFontSize(fontSize)) {
                this.titleFontSize = fontSize;
            }
            return this;
        }

        public NavStyle backgroundColor(String color) {
            if (checkColor(color)) {
                this.backgroundColor = color;
            }
            return this;
        }

        public NavStyle backIconImageName(String imageName) {
            if (checkImageName(imageName)) {
                this.backIconImageName = imageName;
            }
            return this;
        }

        public NavStyle closeIconImageName(String imageName) {
            if (checkImageName(imageName)) {
                this.closeIconImageName = imageName;
            }
            return this;
        }

        public NavStyle shareIconImageName(String imageName) {
            if (checkImageName(imageName)) {
                this.shareIconImageName = imageName;
            }
            return this;
        }

        private boolean checkColor(String color) {
            if (TextUtils.isEmpty(color)) return false;
            try {
                Color.parseColor(color);
                return true;
            } catch (Exception ignored) {
                return false;
            }
        }

        private boolean checkFontSize(int fontSize) {
            return fontSize > 0;
        }

        private boolean checkImageName(String name) {
            return !TextUtils.isEmpty(name);
        }
    }
}
