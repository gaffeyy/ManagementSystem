# 资料文库

## 需求分析

* 用户部分

1. 注册/登录/注销 ： 使用Redis 利用 `Session` + `Cookie`实现分布式登录
2. 用户管理（管理员/普通用户）
3. 点赞资料、收藏资料/取消点赞、取消收藏
4. 增加、获取浏览记录

* 文件资料部分

1. 文件上传
2. 给文件增加标签
3. 文件删除
4. 文件查询
   1. 通过文件名模糊查询
   2. 通过文件ID查询
   3. 通过文件标签查询
5. 根据用户/管理员对文件的操作（上传、删除）记录操作日志



## 技术栈

1. Java编程语言
2. SpringBoot框架
3. SpringMVC + Mybatis/MybatisPlus
4. Mysql数据库、Redis数据库
5. Swagger + Knife4j接口文档



## 数据库建表

1. 用户表user

> ```sql
> create table if not exists wenku_user.user
> (
>     userName     varchar(256)                       null comment '用户名称',
>     id           bigint auto_increment comment 'id(主键)'
>         primary key,
>     userAccount  varchar(256)                       null comment '账号',
>     gender       tinyint                            null comment '性别',
>     userPassword varchar(512)                       not null comment '密码',
>     email        varchar(512)                       null comment '邮箱',
>     createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
>     isDelete     tinyint  default 0                 not null comment '是否删除',
>     userRole     int      default 0                 not null comment '用户身份 0 - 普通用户; 1 - 管理员',
>     userTags     varchar(1024)                      null comment '标签 json 列表'
> )
>     comment '用户';
> ```

2. 资料表document

> ```sql
> create table if not exists wenku_user.document
> (
>     documentName varchar(256)                       not null comment '资料名称',
>     documentId   bigint auto_increment comment '资料id(主键)'
>         primary key,
>     category     varchar(256)                       not null comment '资料类型',
>     uploadUserId bigint                             not null comment '上传用户ID',
>     uploadTime   datetime default CURRENT_TIMESTAMP null comment '上传时间',
>     isDelete     tinyint  default 0                 not null comment '是否删除',
>     ducomentUrl  varchar(256)                       not null comment '文档URL',
>     tags         varchar(1024)                      null comment '文档标签（JSON列表）',
>     constraint document_user_id_fk
>         foreign key (uploadUserId) references wenku_user.user (id)
> );
> ```

[在使用springboot时，有哪些类需要注入，哪些类不需要呢？ - 知乎 (zhihu.com)](https://www.zhihu.com/question/439481519)
