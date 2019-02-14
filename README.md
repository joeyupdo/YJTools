# JoeyTools

#### 介绍
个人工具库，主要是个人在开发过程中的部分工具类

#### 软件架构
软件架构说明


#### 使用方法
1. Add it in your root build.gradle at the end of repositories:
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
2. Add the dependency:
	dependencies {
		implementation 'com.github.joeyupdo:YJTools:v1.0.2'
	}

#### 使用说明
1. AidlUtils：AIDL客户端，方便连接一个AIDL接口调用
2. UiToast：封装了Toast的一个工具类，方便显示Toast信息
3. MainThreadKit：主线程工具，在任意位置可使代码在主线程中运行，分为同步/异步方式
4. CrashHelper：抓取应用崩溃信息，可将信息记录到文件，供后期进行问题排查
5. DelayTimer：使用Handler实现的一个定时器，可以执行定时任务
6. ResourceContentType：给定一个文件，判断是图片还是GIF动图
7. YJEmail: 发送邮件工具类，可方便发送邮件

#### 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request

