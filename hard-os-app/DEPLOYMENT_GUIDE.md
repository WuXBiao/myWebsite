# Hard OS App - 部署指南

## 📦 Android 部署

### 1. 构建 APK

```bash
# 构建调试 APK
flutter build apk --debug

# 构建发布 APK
flutter build apk --release

# 输出位置
# build/app/outputs/flutter-apk/app-release.apk
```

### 2. 构建 AAB（Google Play 格式）

```bash
# 构建 AAB
flutter build appbundle --release

# 输出位置
# build/app/outputs/bundle/release/app-release.aab
```

### 3. 签名配置

#### 创建密钥库

```bash
keytool -genkey -v -keystore ~/key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias key
```

#### 配置 gradle.properties

编辑 `android/key.properties`：

```properties
storePassword=<your_store_password>
keyPassword=<your_key_password>
keyAlias=key
storeFile=<path_to_key.jks>
```

#### 配置 build.gradle

编辑 `android/app/build.gradle.kts`：

```kotlin
android {
    signingConfigs {
        release {
            keyAlias = keystoreProperties['keyAlias']
            keyPassword = keystoreProperties['keyPassword']
            storeFile = file(keystoreProperties['storeFile'])
            storePassword = keystoreProperties['storePassword']
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.release
        }
    }
}
```

### 4. 安装到设备

```bash
# 安装 APK
flutter install

# 或使用 adb
adb install build/app/outputs/flutter-apk/app-release.apk

# 卸载应用
adb uninstall com.example.hard_os_app
```

### 5. 上传到 Google Play

1. 创建 Google Play 开发者账户
2. 创建应用
3. 填写应用信息
4. 上传 AAB 文件
5. 配置发布选项
6. 提交审核

## 🍎 iOS 部署

### 1. 构建 iOS 应用

```bash
# 构建 iOS 应用
flutter build ios --release

# 输出位置
# build/ios/iphoneos/Runner.app
```

### 2. 使用 Xcode 构建

```bash
# 打开 Xcode 项目
open ios/Runner.xcworkspace

# 或使用命令行构建
xcodebuild -workspace ios/Runner.xcworkspace \
  -scheme Runner \
  -configuration Release \
  -derivedDataPath build/ios_build
```

### 3. 配置签名

1. 打开 `ios/Runner.xcworkspace`
2. 选择 Runner 项目
3. 选择 Runner target
4. 进入 Signing & Capabilities 标签
5. 选择开发团队
6. 配置签名证书

### 4. 创建 IPA

```bash
# 使用 Xcode 创建 IPA
xcodebuild -workspace ios/Runner.xcworkspace \
  -scheme Runner \
  -configuration Release \
  -archivePath build/Runner.xcarchive \
  archive

xcodebuild -exportArchive \
  -archivePath build/Runner.xcarchive \
  -exportOptionsPlist ios/ExportOptions.plist \
  -exportPath build/ipa
```

### 5. 上传到 App Store

1. 创建 App Store Connect 账户
2. 创建应用
3. 填写应用信息
4. 上传 IPA 文件
5. 配置发布选项
6. 提交审核

## 🖥️ Windows 部署

### 1. 构建 Windows 应用

```bash
# 构建 Windows 应用
flutter build windows --release

# 输出位置
# build/windows/runner/Release/
```

### 2. 创建安装程序

使用 MSIX 打包：

```bash
# 生成 MSIX
flutter pub run msix:create

# 输出位置
# build/windows/runner/Release/hard_os_app.msix
```

### 3. 配置 MSIX

编辑 `pubspec.yaml`：

```yaml
msix_config:
  display_name: Hard OS App
  publisher_display_name: Your Company
  identity_name: com.example.hard_os_app
  logo_path: assets/images/logo.png
```

## 🌐 Web 部署

### 1. 构建 Web 应用

```bash
# 构建 Web 应用
flutter build web --release

# 输出位置
# build/web/
```

### 2. 部署到 Firebase Hosting

```bash
# 安装 Firebase CLI
npm install -g firebase-tools

# 初始化 Firebase 项目
firebase init hosting

# 配置 public 目录为 build/web

# 部署
firebase deploy
```

### 3. 部署到其他服务

#### Netlify

```bash
# 安装 Netlify CLI
npm install -g netlify-cli

# 部署
netlify deploy --prod --dir=build/web
```

#### Vercel

```bash
# 安装 Vercel CLI
npm install -g vercel

# 部署
vercel --prod
```

## 📋 预发布检查清单

### 代码质量
- [ ] 运行 `flutter analyze` 无警告
- [ ] 运行 `flutter test` 所有测试通过
- [ ] 代码覆盖率 > 70%
- [ ] 没有 TODO 或 FIXME 注释

### 功能测试
- [ ] 所有功能在真机上测试通过
- [ ] 在多个 Android 版本上测试
- [ ] 在多个 iOS 版本上测试
- [ ] 网络不稳定环境下测试
- [ ] 权限请求正常工作

### 性能优化
- [ ] 应用启动时间 < 3 秒
- [ ] 内存使用 < 100 MB
- [ ] 没有内存泄漏
- [ ] 没有 jank（帧率不稳定）

### 安全检查
- [ ] 没有硬编码的密钥或密码
- [ ] API 端点使用 HTTPS
- [ ] 敏感数据加密存储
- [ ] 权限最小化原则

### 文档完整性
- [ ] README.md 完整
- [ ] IMPLEMENTATION_GUIDE.md 完整
- [ ] TEST_GUIDE.md 完整
- [ ] DEPLOYMENT_GUIDE.md 完整
- [ ] 代码注释完整

### 应用信息
- [ ] 应用名称正确
- [ ] 应用版本号正确
- [ ] 应用图标正确
- [ ] 应用描述完整
- [ ] 隐私政策链接正确

## 🔄 持续集成/持续部署 (CI/CD)

### GitHub Actions 配置

创建 `.github/workflows/build.yml`：

```yaml
name: Build and Deploy

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Setup Flutter
      uses: subosito/flutter-action@v2
      with:
        flutter-version: '3.13.0'
    
    - name: Get dependencies
      run: flutter pub get
    
    - name: Run tests
      run: flutter test
    
    - name: Build APK
      run: flutter build apk --release
    
    - name: Upload APK
      uses: actions/upload-artifact@v2
      with:
        name: app-release.apk
        path: build/app/outputs/flutter-apk/app-release.apk
```

## 📊 版本管理

### 版本号格式

遵循 Semantic Versioning (SemVer)：

```
major.minor.patch+build
例如：1.0.0+1
```

### 更新版本

编辑 `pubspec.yaml`：

```yaml
version: 1.0.0+1
```

编辑 `android/app/build.gradle.kts`：

```kotlin
android {
    defaultConfig {
        versionCode = 1
        versionName = "1.0.0"
    }
}
```

编辑 `ios/Runner/Info.plist`：

```xml
<key>CFBundleShortVersionString</key>
<string>1.0.0</string>
<key>CFBundleVersion</key>
<string>1</string>
```

## 🔍 发布后监控

### 崩溃报告

集成 Firebase Crashlytics：

```dart
import 'package:firebase_crashlytics/firebase_crashlytics.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  
  // 初始化 Firebase
  await Firebase.initializeApp();
  
  // 启用 Crashlytics
  FlutterError.onError = FirebaseCrashlytics.instance.recordFlutterError;
  
  runApp(const MyApp());
}
```

### 用户反馈

集成 Firebase 用户反馈：

```dart
// 在应用中添加反馈按钮
ElevatedButton(
  onPressed: () {
    // 发送反馈
  },
  child: const Text('反馈'),
)
```

### 分析数据

集成 Firebase Analytics：

```dart
import 'package:firebase_analytics/firebase_analytics.dart';

final analytics = FirebaseAnalytics.instance;

// 记录事件
await analytics.logEvent(
  name: 'device_connected',
  parameters: {
    'device_type': 'ble',
    'timestamp': DateTime.now().toString(),
  },
);
```

## 📝 发布说明模板

```markdown
# 版本 1.0.0

## 新功能
- 支持 BLE 连接
- 支持经典蓝牙连接
- 支持 USB 串口连接

## 改进
- 改进了连接稳定性
- 优化了数据传输性能
- 改进了用户界面

## 修复
- 修复了数据粘包问题
- 修复了内存泄漏
- 修复了权限请求问题

## 已知问题
- 在某些设备上 BLE 连接可能不稳定
- USB 连接需要启用 OTG

## 升级建议
建议所有用户升级到此版本。
```

## 🚀 快速部署命令

```bash
# 完整的发布流程
flutter clean
flutter pub get
flutter test
flutter analyze
flutter build apk --release
flutter build appbundle --release
flutter build ios --release
flutter build windows --release
flutter build web --release
```

---

**祝部署顺利！** 🎉
