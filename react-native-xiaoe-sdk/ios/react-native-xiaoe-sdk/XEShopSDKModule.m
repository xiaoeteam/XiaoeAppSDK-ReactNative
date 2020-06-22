//
//  XEShopSDKModule.m
//  XiaoeAppSDK_ReactNative
//
//  Created by 小鹅通 on 2020/6/19.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import "XEShopSDKModule.h"
#import "XEWebViewController.h"
#import <XEShopSDK/XEShopSDK.h>

NSString * const EVENT_LISTENER = @"XEShopSDKEvents";

@interface XEShopSDKModule()

@property(nonatomic, copy) NSString *titles;
@property(nonatomic, strong) UIColor *titleColor;
@property(nonatomic, strong) UIColor *navViewColor;
@property(nonatomic, assign) CGFloat titleFontSize;

// 图标
@property(nonatomic, copy) NSString *backImageName;
@property(nonatomic, copy) NSString *shareImageName;
@property(nonatomic, copy) NSString *closeImageName;

@end


@implementation XEShopSDKModule

RCT_EXPORT_MODULE(XEShopSDK)

RCT_EXPORT_METHOD(initConfig:(id)params) {
  
  // MARK: 初始化
  // 参数
  NSLog(@"%@",(NSDictionary*)params);
  NSDictionary *dict = (NSDictionary*)params;
  NSString *clientId = dict[@"clientId"];
  NSString *appId = dict[@"appId"];
  NSString *scheme = dict[@"scheme"];
  BOOL isOpenLog = dict[@"isOpenLog"];
  
  dispatch_async(dispatch_get_main_queue(), ^{
    XEConfig *config = [[XEConfig alloc] initWithClientId: clientId appId: appId];
    config.scheme = scheme;
    [XESDK.shared initializeSDKWithConfig:config];
  });
  
}

RCT_EXPORT_METHOD(setNavStyle:(id)params) {
  
  NSDictionary * dict = (NSDictionary*)params;
  
  NSString *titleColor = dict[@"titleColor"];
  NSNumber *titleFontSize = dict[@"titleFontSize"];
  NSString *backgroundColor = dict[@"backgroundColor"];
  
  NSString *backIconImageName = dict[@"backIconImageName"];
  NSString *closeIconImageName = dict[@"closeIconImageName"];
  NSString *shareIconImageName = dict[@"shareIconImageName"];
  
  
  self.titleColor = [self colorWithHexString:titleColor alpha:1.0];
  if (titleFontSize) {
    self.titleFontSize = [titleFontSize floatValue];
  }
  self.navViewColor = [self colorWithHexString:backgroundColor alpha:1.0];
  
  _backImageName = backIconImageName;
  _closeImageName = closeIconImageName;
  _shareImageName = shareIconImageName;
  
}

RCT_EXPORT_METHOD(setTitle:(id)params) {
  if ([params isKindOfClass:[NSString class]]) {
    self.titles = params;
    return;
  }
  NSDictionary * dict = (NSDictionary*)params;
  self.titles = [NSString stringWithString:dict[@"title"]];
}

RCT_EXPORT_METHOD(open:(NSString* )url) {
  
  __weak typeof(self) weakSelf = self;
  dispatch_async(dispatch_get_main_queue(), ^{
    
    // 初始化 webview
    XEWebViewController * _XEWebViewVC = [[XEWebViewController alloc] init];
    _XEWebViewVC.url = url;
    _XEWebViewVC.callback = ^(NSDictionary *dict){
      [weakSelf sendEventWithName:dict];
    };
    _XEWebViewVC.navTitle = self.titles;
    _XEWebViewVC.titleFontSize = self.titleFontSize;
    _XEWebViewVC.titleColor = self.titleColor;
    _XEWebViewVC.navViewColor = self.navViewColor;
    _XEWebViewVC.backImageName = self.backImageName;
    _XEWebViewVC.shareImageName = self.shareImageName;
    _XEWebViewVC.closeImageName = self.closeImageName;
    
    _XEWebViewVC.modalPresentationStyle = UIModalPresentationFullScreen;
    
    UIViewController *vc = [self frontWindow].rootViewController;
    [vc presentViewController:_XEWebViewVC animated: YES completion:nil];
    
  });
}

RCT_EXPORT_METHOD(synchronizeToken:(NSString*)tokenKey tokenValue:(NSString*)tokenValue) {
  // MARK: 同步登录态
  NSString *token_key = tokenKey;
  NSString *token_value = tokenValue;
  dispatch_async(dispatch_get_main_queue(), ^{
    [XESDK.shared synchronizeCookieKey:token_key cookieValue:token_value];
  });
  
}

RCT_EXPORT_METHOD(isLog:(id)log) {
  BOOL isLog = (BOOL)log;
  XESDK.shared.config.enableLog = isLog;
}

RCT_EXPORT_METHOD(logoutSDK) {
  [XESDK.shared logout];
}

RCT_REMAP_METHOD(getSdkVersion,
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject){
  NSString *version = XESDK.shared.version;
  resolve(version);
}


- (NSArray<NSString *> *)supportedEvents {
  return @[EVENT_LISTENER]; //这里返回的将是你要发送的消息名的数组。
}

- (void)sendEventWithName:(NSDictionary*)dict {
  [self sendEventWithName:EVENT_LISTENER
                     body:dict];
}


- (NSDictionary *)constantsToExport
{
  NSDictionary  * dic = @{@"EVENT_LISTENER":EVENT_LISTENER,@"EVENT_LOGIN":@501,@"EVENT_SHARE":@503,@"EVENT_TITLE_RECEIVE":@504,@"EVENT_NOTICE_OUT_LINK":@505};
  
  return dic;
}


/**
 16进制颜色转换为UIColor
 
 @param hexColor 16进制字符串（可以以0x开头，可以以#开头，也可以就是6位的16进制）
 @param opacity 透明度
 @return 16进制字符串对应的颜色
 */
- (UIColor *)colorWithHexString:(NSString *)hexColor alpha:(float)opacity{
  
  if (hexColor == nil) return nil;
  
  NSString * cString = [[hexColor stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] uppercaseString];
  
  // String should be 6 or 8 characters
  if ([cString length] < 6) return nil;
  
  // strip 0X if it appears
  if ([cString hasPrefix:@"0X"]) cString = [cString substringFromIndex:2];
  if ([cString hasPrefix:@"#"]) cString = [cString substringFromIndex:1];
  
  if ([cString length] != 6) return nil;
  
  // Separate into r, g, b substrings
  NSRange range;
  range.location = 0;
  range.length = 2;
  NSString * rString = [cString substringWithRange:range];
  
  range.location = 2;
  NSString * gString = [cString substringWithRange:range];
  
  range.location = 4;
  NSString * bString = [cString substringWithRange:range];
  
  // Scan values
  unsigned int r, g, b;
  [[NSScanner scannerWithString:rString] scanHexInt:&r];
  [[NSScanner scannerWithString:gString] scanHexInt:&g];
  [[NSScanner scannerWithString:bString] scanHexInt:&b];
  
  return [UIColor colorWithRed:((float)r / 255.0f)
                         green:((float)g / 255.0f)
                          blue:((float)b / 255.0f)
                         alpha:opacity];
}


// 获取 window
- (UIWindow *)frontWindow {
  NSEnumerator *frontToBackWindows = [UIApplication.sharedApplication.windows reverseObjectEnumerator];
  for (UIWindow *window in frontToBackWindows) {
    BOOL windowOnMainScreen = window.screen == UIScreen.mainScreen;
    BOOL windowIsVisible = !window.hidden && window.alpha > 0;
    BOOL windowLevelSupported = window.windowLevel >= UIWindowLevelNormal;
    BOOL windowKeyWindow = window.isKeyWindow;
    
    if(windowOnMainScreen && windowIsVisible && windowLevelSupported && windowKeyWindow) {
      return window;
    }
  }
  return UIApplication.sharedApplication.keyWindow;
}

@end
