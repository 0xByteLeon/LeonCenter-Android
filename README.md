# LeonCenter-Android
### Android 组件化MVVM快速开发模板
  Arouter路由，LiveBus主线事件，谷歌Jetpack组件实现mvvm，异步请求使用Kotlin协程实现，剥离Rxjava
完善中 

**项目结构简介**
> 1. App为项目壳工程
> 2. common组件为架构组件包含mvvm基础架构，其他子组件全部依赖于此
> 3. module a为测试组件
> 4. module router 路由组件，组件间路由管理封装

**第三方SDK处理（待实现）**

第三方SDK（推送、支付、分享、登录等）可以作为一个独立的Module被依赖，appid可以设置为和主工程一致