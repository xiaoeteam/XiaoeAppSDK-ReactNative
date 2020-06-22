//
//  XEWebViewController.h
//  Pods-Runner
//
//  Created by page on 2019/12/16.
//

#import <UIKit/UIKit.h>
#import <XEShopSDK/XEShopSDK.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, XEShopEvent){
  EVENT_LOGIN = 501,
  EVENT_SHARE = 503,
  EVENT_TITLE_RECEIVE = 504,
  EVENT_NOTICE_OUT_LINK = 505,
};

@interface XEWebViewController : UIViewController

@property(nonatomic, copy) NSString *url;
@property(nonatomic, copy) void(^callback)(NSDictionary* notice);

@property(nonatomic, copy) NSString *navTitle;
@property(nonatomic, strong) UIColor *titleColor;
@property(nonatomic, strong) UIColor *navViewColor;
@property(nonatomic, assign) CGFloat titleFontSize;

// 图标
@property(nonatomic, copy) NSString *backImageName;
@property(nonatomic, copy) NSString *shareImageName;
@property(nonatomic, copy) NSString *closeImageName;

@end

NS_ASSUME_NONNULL_END
