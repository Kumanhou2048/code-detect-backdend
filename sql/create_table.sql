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

DROP TABLE IF EXISTS optimize_record;


CREATE TABLE optimize_record (
                                 id                   INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                                 userAccount          VARCHAR(256) NOT NULL COMMENT '用户账号，外键，引用自user表',
                                 originalCodeFileName VARCHAR(512) NOT NULL COMMENT '原始代码文件的文件名',
                                 originalCodeFileUrl  VARCHAR(1024) NOT NULL COMMENT '用户原始上传的文件的 URL',
                                 optimizedCodeFileUrl VARCHAR(1024) NOT NULL COMMENT '优化后的代码文件的 URL',
                                 reportUrl            VARCHAR(1024) NOT NULL COMMENT '代码优化报告的 URL',
                                 createTime           DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
                                 updateTime           DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
) COMMENT '代码优化记录表';

