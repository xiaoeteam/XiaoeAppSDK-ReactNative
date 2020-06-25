/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {Component} from 'react';

import {
  NativeEventEmitter,
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
  TouchableOpacity,
} from 'react-native';

import {Colors} from 'react-native/Libraries/NewAppScreen';

import XEShopSDK from 'react-native-xiaoe-app-sdk';

// SDK参数
const XEShopSDKParams = {
  appId: 'app38itOR341547',
  clientId: '883pzzGyzynE72G',
  shopUrl: 'https://apprnDA0ZDw4581.sdk.xiaoe-tech.com',
};

const App = () =>{
  return <View>
  <StatusBar barStyle="dark-content" />
      <SafeAreaView>
        <ScrollView
          contentInsetAdjustmentBehavior="automatic"
          style={styles.scrollView}>
          <View style={styles.body}>
          <View style={styles.headerContainer}>
            <Text style={styles.headerTitle}>小鹅通店铺Demo</Text>
            </View>
            <XEShopSDKDemo />
          </View>
        </ScrollView>
      </SafeAreaView>
      </View>
}

// SDK组件
class XEShopSDKDemo extends Component {

  constructor(props) {
    super(props)
    this.state = {
      sdkVersion:  'unknown'
    }
  }

  render() {
    return (
      <View style={styles.contentContainer}>
        <View style={styles.infoContainer}>
          <Text style={styles.info}>SDK版本：{this.state.sdkVersion}</Text>
        </View>
        <View style={[styles.linkListContainer]}>
          <View style={styles.separator} />
          <TouchableOpacity
            accessibilityRole={'button'}
            style={styles.linkContainer}
            onPress={this._openShop}>
            <Text style={styles.link}>打开店铺</Text>
          </TouchableOpacity>
        </View>
      </View>
    );
  }

  componentDidMount() {
    // 初始化SDK
    XEShopSDK.initConfig({
      appId: XEShopSDKParams.appId,
      clientId: XEShopSDKParams.clientId,
      isOpenLog: true,
    });

    this.bindEvent()

    this.setUI()

  }

  componentWillUnmount() {
    // 取消监听SDK事件回调
    this.eventEmitter.remove();
  }

  setUI() {
    // 设置样式
    XEShopSDK.setNavStyle({
      titleColor: '#333333',
      titleFontSize: 18,
      backgroundColor: '#eeeeee',
      // backIconImageName: '',
      // closeIconImageName: '',
      // shareIconImageName: '',
    });

    // 设置标题
    this._setTitle('小鹅通店铺Demo');

    // 获取SDK版本信息
    XEShopSDK.getSdkVersion().then(version => {
      this.setState({
        sdkVersion: version,
      });
    });

  }

  bindEvent() {
    // 监听SDK事件回调
    const eventEmitter = new NativeEventEmitter(XEShopSDK);
    this.eventEmitter = eventEmitter.addListener(
      XEShopSDK.EVENT_LISTENER,
      event => {
        switch (event.code) {
          case XEShopSDK.EVENT_LOGIN:
            console.log(`Login event: ${JSON.stringify(event)}`);

            // 需要登录时，执行登录流程并将认证数据同步给SDK
            this._login();

            break;
          case XEShopSDK.EVENT_SHARE:
            console.log(`Share event: ${JSON.stringify(event)}`);
            break;
          case XEShopSDK.EVENT_TITLE_RECEIVE:
            console.log(`Title: ${event.data}`);
            this._setTitle(event.data);
            break;
          case XEShopSDK.EVENT_NOTICE_OUT_LINK:
            console.log(`Notice out link: ${event.data}`);
            break;
          default:
            console.log(JSON.stringify(event));
            break;
        }
      },
    );
  }

  // 设置标题
  _setTitle(title) {
    XEShopSDK.setTitle(title);
  }

  _openShop() {
    // 打开店铺
    XEShopSDK.open(XEShopSDKParams.shopUrl);
  }

  _setToken(tokenKey, tokenValue) {
    // 同步认证信息
    XEShopSDK.synchronizeToken(tokenKey, tokenValue);
  }

  _logout() {
    // 清除认证信息
    XEShopSDK.logoutSDK();
  }

  _setLog(isOpenLog) {
    // 调试开关
    XEShopSDK.isLog(isOpenLog);
  }

  // 模拟服务端登录流程，获得认证信息后同步给SDK
  _login() {
    fetch(
      'https://app38itOR341547.sdk.xiaoe-tech.com/sdk_api/xe.account.login.test/1.0.0',
      {
        method: 'POST',
        headers: {
          Accept: 'application/json',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          app_user_id: '123', // APP方用户ID
          secret_key: 'dfomGwT7JRWWnzY3okZ6yTkHtgNPTyhr', // 小鹅通secret_key。正式版本不要放在客户端！！
          sdk_app_id: XEShopSDKParams.clientId, // 小鹅通client_id
          app_id: XEShopSDKParams.appId, // 小鹅通app_id
        }),
      },
    )
      .then(response => response.json())
      .then(responseJson => {
        console.log(`Login Success: ${JSON.stringify(responseJson)}`);
        if (responseJson.code === 0 && responseJson.data) {
          this._setToken(
            responseJson.data.token_key,
            responseJson.data.token_value,
          );
        } else {
          console.log(`Login Error: ${responseJson}`);
        }
      })
      .catch(error => {
        console.log(`Login Error: ${error}`);
      });
  }
}

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  body: {
    backgroundColor: Colors.white,
  },
  headerContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  headerTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  contentContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  infoContainer: {
    marginTop: 32,
  },
  info: {
    paddingVertical: 4,
  },
  linkListContainer: {
    marginTop: 32,
  },
  separator: {
    backgroundColor: Colors.light,
    height: 1,
  },
  linkContainer: {
    alignItems: 'center',
    paddingVertical: 16,
  },
  link: {
    flex: 2,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.primary,
  },
});

export default App;
