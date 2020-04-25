### ITELLYOU API
https://www.itellyou.com 后端服务API

### 本地安装运行

1. 安装 mysql5.6 以上版本，jdk 1.8 版本
2. 安装 idea ide 工具
3. 根目录中 itellyou.sql 文件为本服务的数据库结构文件，u-*****.sql 在每次新增或修改功能后数据库变更文件。以上文件需要依次执行才能得到最新的数据库结构
4. 本项目为 SpringBoot2 MVC 多模块项目
    * .ansj 项目搜索使用 lucene 做为搜索引擎，
    * api - 控制器，View 层相关
    * dao - 数据库访问层，数据库配置在该模块 resources 中 dev 为开发模式下的数据库链接，prod 为正式环境链接，大部分sql文件在 mapper目录 中
    * model - 数据实体类和相关枚举类型
    * service - 业务处理模块
    * util - 工具模块