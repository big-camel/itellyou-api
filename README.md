### ITELLYOU API
https://www.itellyou.com 后端服务API

### 本地安装运行

1. 安装 mysql5.6 以上版本，jdk 1.8 版本
2. 安装 idea ide 工具
3. 使用 idea 以Maven的方式导入 itellyou-api 项目
4. 根目录中 sql 文件夹 itellyou.sql 文件为本服务的数据库结构文件，u-*****.sql 在每次新增或修改功能后数据库变更文件。以上文件需要依次执行才能得到最新的数据库结构
5. 本项目为 SpringBoot2 MVC 多模块项目
    * .ansj 项目搜索使用 lucene 做为搜索引擎，AnsjAnalyzer 作为分词,分词配置在 api/src/main/java/resources/ansj_library.properties 文件中
    * api - 控制器，View 层相关
    * dao - 数据库访问层，数据库配置在该模块 resources 中 dev 为开发模式下的数据库链接，prod 为正式环境链接，大部分sql文件在 mapper目录 中
    * model - 数据实体类和相关枚举类型
    * service - 业务处理模块
    * util - 工具模块
 6. 配置数据库连接 dao/src/main/resources/application-dev.yml 默认本地开发地址，生产环境下添加 application-prod.yml 配置数据库链接
 7. 使用 idea 配置Main Class: com.itellyou.api.Application 就可以启动了，默认端口为 8082 ，配置在 api/src/main/resources/application.yml 中