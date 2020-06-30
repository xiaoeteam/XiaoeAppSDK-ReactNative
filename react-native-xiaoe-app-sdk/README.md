# React Native API文档


## Installing
1. Install library from npm or yarn
```
npm install react-native-xiaoe-app-sdk
```
```
yarn add react-native-xiaoe-app-sdk
```

2. link native code
- react-native 0.60+
```
cd ios && pod install
```
- Pre 0.60
```
react-native link react-native-xiaoe-app-sdk
```

## 模块名称

XEShopSDK



## 常量

#### XEShopSDK.EVENT_LISTENER
- 用于设置EventListener
- value: "XEShopSDKEvents"

#### XEShopSDK.EVENT_LOGIN
- 登录回调事件
- code: 501

#### XEShopSDK.EVENT_SHARE
- 分享回调事件
- code: 503

#### XEShopSDK.EVENT_TITLE_RECEIVE
- 标题回调事件
- code: 504

#### XEShopSDK.EVENT_NOTICE_OUT_LINK
- 通知外部打开链接回调事件
- code: 505



## 方法

#### XEShopSDK.initConfig(Object object)
- 初始化SDK
- 参数
	- appId : string  平台App ID
	- clientId : string  平台Client ID
	- isOpenLog : boolean  【可选】是否打开调试日志，默认false

#### XEShopSDK.setTitle(string title)
- 设置标题
- 参数
	- title : string  标题

#### XEShopSDK.setNavStyle(Object object)
- 设置导航栏样式
- 参数 【全都是可选参数】
	- titleColor : string  标题颜色
	- titleFontSize : int  标题字体大小
	- backgroundColor : string  标题栏背景颜色
	- backIconImageName : string  返回按钮图片名称
	- closeIconImageName : string  关闭按钮图片名称
	- shareIconImageName : string  分享按钮图片名称

#### XEShopSDK.open(string shopUrl)
- 打开店铺
- 参数
	- shopUrl : string  店铺链接

#### XEShopSDK.synchronizeToken(string tokenKey, string tokenValue)
- 同步认证信息
- 参数
	- tokenKey : string  服务端登录接口返回的token_key
	- tokenValue: string  服务端登录接口返回的token_value

#### XEShopSDK.logoutSDK()
- 清除认证信息

#### XEShopSDK.isLog(boolean isOpenLog)
- 调试开关
- 参数
	- isOpenLog : boolean  是否打开调试开关

#### Promise XEShopSDK.getSdkVersion()
- 获取SDK版本
- 返回Promise对象用于异步返回SDK版本
- Promise.resolve(string ret)  返回SDK版本

# [详细使用](https://github.com/xiaoeteam/XiaoeAppSDK-ReactNative/wiki)