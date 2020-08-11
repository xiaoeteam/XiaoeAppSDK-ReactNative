require "json"
package = JSON.parse(File.read(File.join(__dir__, '../', 'package.json')))

Pod::Spec.new do |s|
  s.name          = package['name']
  s.version       = package["version"]
  s.summary       = package['description']
  s.requires_arc  = true
  s.author        = { 'david' => '337581468@qq.com' }
  s.license       = package["license"]
  s.homepage      = 'https://www.npmjs.com/package/react-native-xiaoe-app-sdk'
  s.source        = { :git => '' }
  s.platform      = :ios, '7.0'
  s.dependency    'React'
  s.dependency    'XEShopSDK', '2.0.9'
  s.source_files = 'react-native-xiaoe-sdk/*.{h,m}'
  s.resources    = 'react-native-xiaoe-sdk/*.{bundle}'



end
