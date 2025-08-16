# 项目总结

## 1. 项目结构

该项目是一个Android应用程序，主要使用Kotlin语言和Jetpack Compose进行UI开发。项目结构遵循标准的Android项目布局。

*   `app/src/main/`: 包含应用程序的主要源代码和资源。
    *   `AndroidManifest.xml`: Android应用程序的清单文件，定义了应用程序的组件（活动、服务、广播接收器、内容提供者）、权限和元数据。
    *   `kotlin/`: 存放Kotlin源代码。根据文件列表，这里包含了UI组件、ViewModel、数据仓库等模块，遵循MVVM（Model-View-ViewModel）架构模式。
        *   `com/temp/mail/`: 应用程序的根包。
            *   `App.kt`: 应用程序的入口点，用于Koin依赖注入的初始化和TokenRepository的启动。
            *   `MainActivity.kt`: 主Activity，应用程序的启动入口，使用Jetpack Compose构建UI，并根据用户设置的主题进行显示。
            *   `data/`: 数据层。
                *   `datastore/SettingsDataStore.kt`: 用于持久化存储用户设置（如主题、动态颜色、JavaScript启用状态）。
                *   `model/`: 数据模型定义。
                    *   `AuthToken.kt`: 认证令牌的数据类。
                    *   `Email.kt`: 邮件列表项的数据类。
                    *   `EmailAddress.kt`: 邮箱地址的数据类。
                    *   `EmailDetails.kt`: 邮件详情的数据类，包含 `EmailBody`。
                *   `network/`: 网络请求相关。
                    *   `MailCxApiService.kt`: 用于获取原始认证令牌的API服务。
                    *   `MailService.kt`: 定义邮件服务接口，用于获取邮件列表和邮件详情。
                    *   `MailServiceImpl.kt`: `MailService` 的实现，使用OkHttp进行网络请求。
                *   `repository/`: 数据仓库层。
                    *   `EmailRepository.kt`: 定义邮件数据仓库接口。
                    *   `EmailRepositoryImpl.kt`: `EmailRepository` 的实现，负责邮件数据的加载、缓存和历史记录管理。
                    *   `TokenRepository.kt`: 负责管理认证令牌的获取和刷新。
            *   `di/AppModule.kt`: Koin依赖注入模块，定义了应用程序中所有单例和ViewModel的提供方式。
            *   `ui/`: 用户界面相关。
                *   `EmailDetailActivity.kt`: 邮件详情Activity，显示邮件内容，支持复制验证码和WebView渲染。
                *   `components/`: 可重用的Compose UI组件。
                    *   `AppDrawer.kt`: 应用程序的抽屉导航，显示邮箱列表、添加新邮箱、导航到历史邮件和设置。
                    *   `EmailItem.kt`: 邮件列表中的单个邮件项的Composeable。
                    *   `CommonDialog.kt`: 一个可复用的通用对话框组件，用于显示通知和错误信息。
                *   `history/`: 历史邮件功能。
                    *   `HistoryActivity.kt`: 历史邮件Activity。
                    *   `HistoryScreen.kt`: 历史邮件列表的Composeable屏幕。
                    *   `HistoryViewModel.kt`: 历史邮件的ViewModel。
                *   `screens/`: 主要的Composeable屏幕。
                    *   `EmailListScreen.kt`: 邮件列表屏幕，支持下拉刷新、错误处理和邮件详情跳转。
                    *   `MainScreen.kt`: 主屏幕，集成了抽屉导航和邮件列表。
                *   `settings/`: 设置功能。
                    *   `SettingsActivity.kt`: 设置Activity。
                    *   `SettingsViewModel.kt`: 设置的ViewModel。
                *   `viewmodel/`: ViewModel层。
                    *   `EmailDetailViewModel.kt`: 邮件详情的ViewModel。
                    *   `EmailListViewModel.kt`: 邮件列表的ViewModel，负责加载和刷新邮件。
                    *   `MainViewModel.kt`: 主界面的ViewModel，管理邮箱地址和选中状态。
            *   `util/`: 工具类。
                *   `EmailGenerator.kt`: 用于生成随机邮箱地址。
                *   `FileLogger.kt`: 用于文件日志记录。
                *   `NetworkUtils.kt`: 网络工具类，用于检查网络连接。
    *   `res/`: 应用程序的资源文件。
        *   `mipmap-anydpi-v26/`: 应用程序的自适应启动器图标。
        *   `mipmap-hdpi/`, `mipmap-mdpi/`, `mipmap-xhdpi/`, `mipmap-xxhdpi/`, `mipmap-xxxhdpi/`: 不同DPI的应用程序图标。
        *   `values/`: 默认资源，如`arrays.xml` (数组资源), `strings.xml` (字符串资源), `themes.xml` (主题定义)。
        *   `values-zh-rCN/`: 中文（简体）的本地化字符串资源。
        *   `xml/`: XML配置文件，如`backup_rules.xml` (备份规则), `data_extraction_rules.xml` (数据提取规则)。
*   `app/build.gradle.kts`: 应用程序模块的Gradle构建脚本，定义了应用程序的构建配置、依赖项、签名信息等。
*   `gradle/libs.versions.toml`: Gradle版本目录文件，用于集中管理项目中的依赖版本和插件版本。
*   `build.gradle.kts` (根目录): 整个项目的Gradle构建脚本，通常用于定义子项目的配置和全局依赖。
*   `settings.gradle.kts`: Gradle设置文件，用于声明项目中的模块。

## 2. 项目文件的作用

*   [`AndroidManifest.xml`](app/src/main/AndroidManifest.xml): 应用程序的清单文件，声明了应用程序的组件、权限和元数据。
*   `app/src/main/kotlin/com/temp/mail/App.kt`: 应用程序的入口点，负责初始化Koin依赖注入和启动TokenRepository。
*   `app/src/main/kotlin/com/temp/mail/MainActivity.kt`: 应用程序的主Activity，使用Jetpack Compose构建UI，并根据用户设置的主题进行显示。
*   `app/src/main/kotlin/com/temp/mail/data/datastore/SettingsDataStore.kt`: 负责应用程序设置的持久化存储，包括主题、动态颜色和JavaScript启用状态。
*   `app/src/main/kotlin/com/temp/mail/data/model/AuthToken.kt`: 定义了认证令牌的数据结构及其过期判断逻辑。
*   `app/src/main/kotlin/com/temp/mail/data/model/Email.kt`: 定义了邮件列表项的数据结构。
*   `app/src/main/kotlin/com/temp/mail/data/model/EmailAddress.kt`: 定义了邮箱地址的数据结构。
*   `app/src/main/kotlin/com/temp/mail/data/model/EmailDetails.kt`: 定义了邮件详情的数据结构，包含邮件正文（HTML和纯文本）。
*   `app/src/main/kotlin/com/temp/mail/data/network/MailCxApiService.kt`: 负责与Mail.Cx API进行交互，特别是获取原始认证令牌。
*   `app/src/main/kotlin/com/temp/mail/data/network/MailService.kt`: 定义了获取邮件列表和邮件详情的网络服务接口。
*   `app/src/main/kotlin/com/temp/mail/data/network/MailServiceImpl.kt`: `MailService` 接口的实现，使用OkHttp库处理HTTP请求和响应。
*   `app/src/main/kotlin/com/temp/mail/data/repository/EmailRepository.kt`: 定义了邮件数据仓库的接口，用于抽象数据源。
*   `app/src/main/kotlin/com/temp/mail/data/repository/EmailRepositoryImpl.kt`: `EmailRepository` 接口的实现，负责从网络获取邮件、缓存邮件详情到本地文件，并管理历史邮件。
*   `app/src/main/kotlin/com/temp/mail/data/repository/TokenRepository.kt`: 负责管理应用程序的认证令牌，包括获取、刷新和自动刷新机制。
*   `app/src/main/kotlin/com/temp/mail/di/AppModule.kt`: Koin依赖注入模块，集中管理应用程序的依赖关系，包括DataStore、网络服务、数据仓库和ViewModel。
*   `app/src/main/kotlin/com/temp/mail/ui/EmailDetailActivity.kt`: 显示单个邮件的详细内容，支持WebView渲染HTML邮件体，并提供复制验证码的功能。
*   `app/src/main/kotlin/com/temp/mail/ui/components/AppDrawer.kt`: 应用程序的侧边抽屉导航，显示已有的邮箱地址列表，提供添加新邮箱、查看历史邮件和进入设置界面的入口。
*   `app/src/main/kotlin/com/temp/mail/ui/components/EmailItem.kt`: 用于在邮件列表中显示单个邮件的Composeable组件，包括发件人、主题、日期和时间。
*   `app/src/main/kotlin/com/temp/mail/ui/components/CommonDialog.kt`: 一个可复用的通用对话框组件，旨在统一应用内的对话框样式。它支持两种模式：1. **普通通知模式**，显示标题、文本和一个“确定”按钮。 2. **错误信息模式**，会额外显示一个警告图标，并提供“复制”按钮，方便用户复制错误信息进行反馈。
*   `app/src/main/kotlin/com/temp/mail/ui/history/HistoryActivity.kt`: 历史邮件界面的Activity。
*   `app/src/main/kotlin/com/temp/mail/ui/history/HistoryScreen.kt`: 显示历史邮件列表的Composeable屏幕，提供清空历史记录的功能。
*   `app/src/main/kotlin/com/temp/mail/ui/history/HistoryViewModel.kt`: 历史邮件界面的ViewModel，负责加载和管理历史邮件数据。
*   `app/src/main/kotlin/com/temp/mail/ui/screens/EmailListScreen.kt`: 显示当前选中邮箱的邮件列表的Composeable屏幕，支持下拉刷新和错误信息显示。
*   `app/src/main/kotlin/com/temp/mail/ui/screens/MainScreen.kt`: 应用程序的主屏幕，集成了侧边抽屉导航和邮件列表，并处理邮箱地址的选中和添加。
*   `app/src/main/kotlin/com/temp/mail/ui/settings/SettingsActivity.kt`: 应用程序设置界面的Activity。
*   `app/src/main/kotlin/com/temp/mail/ui/settings/SettingsViewModel.kt`: 设置界面的ViewModel，负责管理和更新用户设置。
*   `app/src/main/kotlin/com/temp/mail/ui/viewmodel/EmailListViewModel.kt`: 负责管理邮件列表的ViewModel，包括加载、刷新和错误处理。
*   `app/src/main/kotlin/com/temp/mail/ui/viewmodel/MainViewModel.kt`: 负责管理主界面的ViewModel，包括邮箱地址的生成、选择和管理。
*   `app/src/main/kotlin/com/temp/mail/util/EmailGenerator.kt`: 提供了生成随机邮箱地址的工具方法。
*   `app/src/main/kotlin/com/temp/mail/util/FileLogger.kt`: 提供了将日志信息写入文件的功能。
*   `app/src/main/kotlin/com/temp/mail/util/NetworkUtils.kt`: 提供了检查设备网络连接状态的工具方法。
*   `app/src/main/res/`: 包含应用程序的各种资源。
    *   `mipmap-anydpi-v26/ic_launcher.xml`: 自适应启动器图标的XML定义。
    *   `mipmap-hdpi/`, `mipmap-mdpi/`, `mipmap-xhdpi/`, `mipmap-xxhdpi/`, `mipmap-xxxhdpi/`: 包含不同DPI的应用程序图标文件（`ic_launcher.png`, `ic_launcher_background.png`, `ic_launcher_foreground.png`, `ic_launcher_monochrome.png`）。
    *   `values/arrays.xml`: 定义了字符串数组资源。
    *   `values/strings.xml`: 应用程序的默认字符串资源。
    *   `values/themes.xml`: 定义了应用程序的默认主题。
    *   `values-zh-rCN/strings.xml`: 应用程序的中文（简体）字符串资源，用于国际化。
    *   `xml/backup_rules.xml`: 定义了应用程序的备份规则。
    *   `xml/data_extraction_rules.xml`: 定义了应用程序的数据提取规则。

## 3. 项目依赖

根据[`app/build.gradle.kts`](app/build.gradle.kts)和[`gradle/libs.versions.toml`](gradle/libs.versions.toml)文件，该项目主要依赖以下库：

*   **AndroidX Core**: 提供了核心的Android系统功能。
*   **AndroidX Compose BOM**: Jetpack Compose的材料清单，用于管理Compose库的版本兼容性。
*   **AndroidX Activity Compose**: 用于在Compose中集成Activity。
*   **AndroidX Compose UI**: Compose UI工具包的核心库。
*   **AndroidX Compose UI Graphics**: Compose UI的图形库。
*   **AndroidX Compose UI Tooling Preview**: 用于Compose UI的预览功能。
*   **AndroidX Compose Material3**: 实现了Material Design 3的Compose组件。
*   **AndroidX Compose Material Icons Extended**: 提供了扩展的Material Design图标。
*   **AndroidX Lifecycle ViewModel Compose**: 用于在Compose中集成ViewModel。
*   **AndroidX DataStore Preferences**: 用于持久化存储键值对数据。
*   **OkHttp**: 一个高效的HTTP客户端，用于网络请求。
*   **OkHttp Logging Interceptor**: OkHttp的日志拦截器，用于网络请求的日志记录。
*   **Kotlinx Serialization JSON**: Kotlin的序列化库，用于JSON数据的序列化和反序列化。
*   **Koin Android**: Koin依赖注入框架的Android集成。
*   **Koin AndroidX Compose**: Koin依赖注入框架的Compose集成。
*   **JUnit**: 单元测试框架。
*   **AndroidX JUnit**: AndroidX的JUnit测试库。
*   **AndroidX Espresso Core**: Android UI测试框架。
*   **AndroidX Compose UI Tooling**: 用于Compose UI的工具。
*   **AndroidX Compose UI Test Manifest**: 用于Compose UI测试的清单。

这些依赖表明该项目是一个现代的Android应用程序，使用了Jetpack Compose进行UI开发，并集成了网络请求、数据持久化和依赖注入等功能。