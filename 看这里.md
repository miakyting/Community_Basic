# Community
我的首个正儿八经的Web项目，用到的组件也比较少，前端也是静态页面，不过数据库文件没了，也懒得截演示图了。

[开发环境] ：spirngboot2.x + maven3.3.7 + jdk8 + Tomcat7

[技术选型] ：Mybatis + MySQL + Redis + Thymeleaf + aliyunOOS

该项目是一个简易版的中文社区网站，采用了GitHub的OAuth2授权登录，图片内容存储在阿里云OOS远程对象数据库中，Redis存储热点数据。

通过定时任务并利用小顶堆随时获取最热门的前十个问题
