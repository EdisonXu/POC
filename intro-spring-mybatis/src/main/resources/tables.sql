--
--数据库 tb_user
--

drop table if exists tb_user;

create table tb_user(
    id int primary key auto_increment comment '主键',
    name varchar(40) not null unique comment '用户名',
    password varchar(40) not null comment '密码',
    age int  comment '年龄',
    sex char(2) not null comment '性别'
);