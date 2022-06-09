create table file
(
    fileId    bigint auto_increment comment '文件id',
    fileName  varchar(100)     not null comment '文件名',
    filePath  varchar(15000)   not null,
    username  varchar(45)      not null comment '创建用户',
    fileSize  bigint default 0 not null comment '文件大小',
    creatDate varchar(45)      not null comment '创建时间',
    type      bigint default 0 not null,
    constraint file_fileId_uindex
        unique (fileId)
)
    comment '文件列表';

alter table file
    add primary key (fileId);

create table user
(
    uid      bigint auto_increment,
    username varchar(45)              not null,
    level    int unsigned default '1' not null,
    password varchar(45)              not null,
    constraint uid_UNIQUE
        unique (uid),
    constraint username_UNIQUE
        unique (username)
);

alter table user
    add primary key (uid);

