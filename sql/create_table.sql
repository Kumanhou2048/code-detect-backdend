# 数据库初始化
-- 创建库
create database if not exists codeDetect_db;

-- 切换库
use codeDetect_db;

# 用户表
create table user
(
    username     varchar(256)                       null comment '用户昵称',
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别',
    userPassword varchar(512)                       not null comment '密码',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '状态 0 - 正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '用户角色 0 - 普通用户 1 - 管理员',
    webCode      varchar(512)                       null comment '网站账户编号'
)
    comment '用户';

# 导入示例用户
INSERT INTO codeDetect_db.user (username, userAccount, avatarUrl, gender, userPassword, phone, email, userStatus, createTime, updateTime, isDelete, userRole, webCode) VALUES ('cyw', '22331009', 'https://i2.hdslb.com/bfs/archive/e52d668edcd1aa953c3137f1e27470b23ae5a306.jpg', 0, '12345678', '13380042848', 'chenyw95@mail2.sysu.edu.cn', 0, '2025-04-23 14:14:22', '2025-04-23 14:39:37', 0, 1, '1');

