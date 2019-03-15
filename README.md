# toutiao

* 使用Spring boot编写的咨询网站
* 采用MVC模式，并实现前后端分离
* 通过myBatis和MySQL实现数据库部分功能
* 接入七牛云SDK实现图片存储
* 使用Git进行项目管理

### 目录结构

![](/presentation/structure.png)

### 功能实现

目前网站部署于阿里云服务器[http://59.110.152.215/](http://59.110.152.215/)上。

网站目前已实现的功能有：
1. 登录注册，注册需要邮箱验证通过(通过异步事件队列内邮箱发送部分实现)
2. 图片上传及写推文(图片通过七牛云存储而存放)
3. 资讯赞踩功能(通过Redis实现)
4. 站内信互发功能(也在异步事件队列中，与MySQL配合)
5. 资讯评论功能(MySQL实现)
6. 登录拦截，站内信查看的身份验证(interceptor拦截)

### 效果展示
![主页](/presentation/home_page.png)

<center>主页赞踩<center>

![新闻细节页](/presentation/detail.png)

<center>新闻细节页与评论<center>

![上传页](/presentation/upload.png)

<center>ajax上传<center>

![站内信页](/presentation/message.png)

<center>站内信-点赞提示<center>
