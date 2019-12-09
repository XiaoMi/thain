-- Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
-- This source code is licensed under the Apache License Version 2.0, which
-- can be found in the LICENSE file in the root directory of this source tree.

create table thain_flow
(
    id                       int unsigned auto_increment primary key comment 'flow id，需要唯一，后面对任务的一系列操作都基于它',
    name                     varchar(100)     default ''                    not null comment 'Flow名称，显示用',
    cron                     varchar(100)     default ''                    not null comment 'cron 表达式',
    modify_callback_url      varchar(512)     default ''                    not null comment '修改回调地址',
    pause_continuous_failure int unsigned     default 0                     not null comment '连续失败导致暂停的次数，0表示不暂停',
    email_continuous_failure varchar(512)     default ''                    not null comment '连续失败pause_continuous_failures次数后，发送邮件的邮箱',
    create_user              varchar(100)     default ''                    not null comment '创建人',
    callback_url             varchar(512)     default ''                    not null comment '状态回调地址',
    callback_email           varchar(512)     default ''                    not null comment '状态回调邮箱',
    create_app_id            varchar(128)     default ''                    not null comment '创建app id, 0为网页上创建',
    sla_duration             int unsigned     default 0                     not null comment '期待执行的时间（秒）',
    sla_email                varchar(512)     default ''                    not null comment '超过期望时间发通知的收件人，多个用逗号隔开',
    sla_kill                 tinyint(1)       default 0                     not null comment '超过期望时间 是否kill',
    last_run_status          tinyint unsigned default 0                     not null comment '最后一次运行状态：
	1 未运行、2 运行成功、3 运行异常、4 正在运行、5 手动杀死、6 暂停运行（运行了一半，点了暂停）',
    scheduling_status        tinyint unsigned default 0                     not null comment '调度状态：
	1 未设置调度，
	2 调度中，
	3 暂停调度',
    retry_number             int unsigned     default 0                     not null comment '重试次数',
    time_interval            int unsigned     default 0                     not null comment '每次重试的间隔，单位秒',
    create_time              timestamp        default '2019-01-01 00:00:00' not null comment '创建时间',
    update_time              timestamp        default '2019-01-01 00:00:00' not null comment '更新时间',
    status_update_time       timestamp        default '2019-01-01 00:00:00' not null on update CURRENT_TIMESTAMP comment '状态更新时间',
    deleted                  tinyint(1)       default 0                     not null comment '标记是否删除'
)
    ENGINE = InnoDB
    comment 'flow表';

create table thain_flow_execution
(
    id           int unsigned auto_increment primary key comment '自增id',
    flow_id      int unsigned     default 0                     not null comment '所属flow id',
    status       tinyint unsigned default 0                     not null comment '流程执行状态，0 排队中，1 执行中，2 执行结束，3执行异常,4 手动kill, 5 禁止同时运行',
    host_info    varchar(128)     default ''                    not null comment '机器信息',
    trigger_type tinyint unsigned default 1                     not null comment '触发类型 1手动 2自动',
    logs         mediumtext                                     null comment '日志',
    create_time  timestamp        default '2019-01-01 00:00:00' not null comment '创建时间',
    update_time  timestamp        default '2019-01-01 00:00:00' not null on update CURRENT_TIMESTAMP comment '更新时间',
    heartbeat    timestamp        default '2019-01-01 00:00:00' not null comment '最近一次心跳时间'
)
    ENGINE = InnoDB;

alter table thain_flow_execution
    add index thain_flow_execution_heartbeat_index (heartbeat);

create table thain_job
(
    id           int unsigned auto_increment primary key comment 'id',
    flow_id      int unsigned default 0                 not null,
    name         varchar(128) default '0'               not null comment 'flow id 对应的name 不能重复',
    `condition`  varchar(256) default ''                not null comment '触发条件',
    component    varchar(128) default ''                not null comment 'job 所用组件名称',
    callback_url text                                   null comment '状态回调地址',
    properties   json                                   not null comment '组件属性,json表示',
    x_axis       int          default 0                 not null comment '横坐标',
    y_axis       int          default 0                 not null comment '纵坐标',
    create_time  timestamp    default CURRENT_TIMESTAMP not null comment 'create time',
    deleted      tinyint(1)   default 0                 not null comment 'deleted'
)
    ENGINE = InnoDB;

create table thain_job_execution
(
    id                int unsigned auto_increment primary key comment 'id',
    flow_execution_id int unsigned     default 0                     not null comment '关联的flow_execution',
    job_id            int unsigned     default 0                     not null comment 'job id',
    status            tinyint unsigned default 0                     not null comment '节点执行状态：1未执行，2执行中，3执行结束，4执行异常',
    logs              mediumtext                                     null comment 'job running logs',
    create_time       timestamp        default '2019-01-01 00:00:00' not null comment 'create time',
    update_time       timestamp        default '2019-01-01 00:00:00' not null on update CURRENT_TIMESTAMP comment 'update time'
)
    ENGINE = InnoDB
    comment '节点运行表';


create table thain_user
(
    id            int unsigned auto_increment primary key comment '自增id',
    user_id       varchar(100) default '' not null comment '用户id',
    user_name     varchar(100) default '' not null comment '用户名',
    password_hash varchar(100) default '' not null comment '密码',
    email         varchar(300) default '' not null comment 'email',
    admin         tinyint(1)   default 0  not null comment '是否管理员',
    constraint thain_user_user_id_uindex
        unique (user_id)
)
    ENGINE = InnoDB
    comment '用户表';


create table thain_x5_config
(
    id              int auto_increment primary key comment 'id',
    app_id          varchar(128)  default ''                not null comment 'app id',
    app_key         varchar(128)  default ''                not null comment 'app key',
    app_name        varchar(128)  default ''                not null comment 'app名称',
    principal       varchar(1024) default ''                not null comment '负责人',
    app_description varchar(512)  default ''                not null comment '描述',
    create_time     timestamp     default CURRENT_TIMESTAMP not null comment 'create time',
    constraint sch_x5_config_app_id_uindex
        unique (app_id)
)
    ENGINE = InnoDB;
