### ITELLYOU API
https://www.itellyou.com 后端服务API

### 本地安装运行

1. 安装 mysql8.0 以上版本，jdk 1.8 版本
2. 安装 idea ide 工具
3. 使用 idea 以Maven的方式导入 itellyou-api 项目
4. 根目录中 sql 文件夹 itellyou.sql 文件为本服务的数据库结构文件，data 目录里面是所需要的基础数据
5. 本项目为 SpringBoot2 MVC 多模块项目
    * .ansj 项目搜索使用 lucene 做为搜索引擎，AnsjAnalyzer 作为分词,分词配置在 api/src/main/java/resources/ansj_library.properties 文件中
    * api - 控制器，View 层相关
    * dao - 数据库访问层，数据库配置在该模块 resources 中 dev 为开发模式下的数据库链接，prod 为正式环境链接，大部分sql文件在 mapper目录 中
    * model - 数据实体类和相关枚举类型
    * service - 业务处理模块
    * util - 工具模块
 6. 配置数据库连接 dao/src/main/resources/application-dev.yml 默认本地开发地址，生产环境下添加 application-prod.yml 配置数据库链接
 7. 使用 idea 配置Main Class: com.itellyou.api.Application 就可以启动了，默认端口为 8082 ，配置在 api/src/main/resources/application.yml 中
 
 ### 事件驱动
 
用户在 com.itellyou.model.sys.EntityAction com.itellyou.model.sys.EntityType 中的所有操作都会通过
com.itellyou.service.event.OperationalPublisher 发布基于 OperationalEvent 的事件

OperationalModel 操作 Model 
```
new OperationalModel(EntityAction action, EntityType type, Long targetId, Long targetUserId, Long createdUserId, Long createdTime, Long createdIp);
```
action:操作
type:操作类型
targetId:操作对象的唯一id
targetUserId:操作对象的创作者
createdUserId:创建操作的用户
createdTime:创建操作的时间
createdIp:创建操作的ip

发布操作
```
operationalPublisher.publish(new OperationalEvent(this, operationalModel));
``` 
监听事件：
```
...

@EventListener
@Async
public void globalEvent(OperationalEvent event){
    OperationalModel model = event.getOperationalModel();
    // 更新积分
    bankService.updateByOperational(UserBankType.CREDIT,model);
    // 更新权限分
    bankService.updateByOperational(UserBankType.SCORE,model);
    // 设置消息
    notificationManagerService.put(model);
}
```
### 权限
sys_permission 权限表
sys_role 角色表
sys_role_permission 角色权限对应表

user_role 用户角色表对应对应 sys_role 角色
user_rank 用户等级表
user_rank_role 用户等级对应 sys_role 角色

默认系统内置角色
root 超级管理员
guest 游客
user 登录用户

需要设置 sys_role 中的created_user_id的用户编码设置为你的超级账号id
首次设置超级权限需要在 user_role 中添加root角色
```
insert into user_role(user_id,role_id,created_user_id,created_time,created_ip) values(用户编号,3,0,0,0)
```

### redis 缓存
reids 官网下载安装
```
https://redis.io/
```
在 api/src/main/resources/application.yml 中配置 redis 相关参数
在 api/src/main/java/com.itellyou.api/config/RedisConfig 中可以调节相关缓存参数
