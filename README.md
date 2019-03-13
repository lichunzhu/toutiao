# toutiao

* 使用Spring boot编写的咨询网站
* 采用MVC模式，并实现前后端分离
* 通过myBatis和MySQL实现数据库部分功能
* 接入七牛云SDK实现图片存储
* 使用Git进行项目管理

### 目录结构

├── README.md               	 	   // help
├── pom.xml                 	 	   // mvn依赖包
├── src                     			// 源码目录
│   ├── main
│	│	├── java.com.baine.toutiao		// 主代码目录
│	│	│	├── aspect					// AOP试用
│	│	│	├── async					// 异步事件队列
│	│	│	├── configuration			// freemarker和interceptor的配置
│	│	│	├── controller				// 各网页的controller
│	│	│	├── dao						// DAO层
│	│	│	├── interceptor				// 网站拦截器实现
│	│	│	├── model					// 各数据模型
│	│	│	├── service					// 服务层
│	│	│	├── util					// 工具函数
│	│	│	└── ToutiaoApplication.java
│	│	└── resources					// 资源目录
│	│		├── com.baine.toutiao.dao	// 逻辑复杂的dao
│	│		├── static					// 静态文件js,font,img等
│	│		├── templates				// freemarker模板
│	│		├── application.properties	
│	│		└── mybatis-config.xml		
│   └── test							// 一些测试代码               
├── .gitignore
├── .mvn/wrapper
├── mvnw                         
└── mvnw.cmd

### 实现的功能

目前网站部署于阿里云服务器[http://59.110.152.215/](http://59.110.152.215/)上。

网站目前已实现的功能有：
1. 登录注册，注册需要邮箱验证通过(通过异步事件队列内邮箱发送部分实现)
2. 图片上传及写推文(图片通过七牛云存储而存放)
3. 资讯赞踩功能(通过Redis实现)
4. 站内信互发功能(也在异步事件队列中，与MySQL配合)
5. 资讯评论功能(MySQL实现)
6. 登录拦截，站内信查看的身份验证(interceptor拦截)

