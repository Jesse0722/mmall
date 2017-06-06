/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/5/26 17:06:08                           */
/*==============================================================*/


drop table if exists mmall_cart;

drop table if exists mmall_category;

drop table if exists mmall_order;

drop table if exists mmall_order_item;

drop table if exists mmall_pay_info;

drop table if exists mmall_product;

drop table if exists mmall_shipping;

drop table if exists mmall_user;

/*==============================================================*/
/* Table: mmall_cart                                            */
/*==============================================================*/
create table mmall_cart
(
   id                   int(11) not null AUTO_INCREMENT,
   user_id              int(11) not null,
   product_id           int(11) DEFAULT NULL comment '商品id',
   quantity             int(11) DEFAULT NULL comment '数量',
   checked              int(11) DEFAULT NULL comment '是否选择，1=勾选，0=未选择',
   create_time          datetime DEFAULT NULL comment '创建时间',
   update_time          datetime DEFAULT NULL comment '更新时间',
   primary key (id),
   KEY `user_id_index` (user_id) USING BTREE
)ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=UTF8;

alter table mmall_cart comment '购物车表';

/*==============================================================*/
/* Table: mmall_category                                        */
/*==============================================================*/
create table mmall_category
(
   id                   int(11) not null AUTO_INCREMENT comment '类别id',
   parent_id            int(11) DEFAULT null comment '父类别id',
   name                 varchar(50) DEFAULT NULL comment '类别名称',
   status               tinyint(1) DEFAULT 1 comment '类别状态1-正常，2-已废弃',
   sort_order           int(4) DEFAULT NULL comment '排序编号，同类展示顺序，数值相等则自然排序',
   create_time          datetime DEFAULT NULL comment '创建时间',
   update_time          datetime DEFAULT NULL comment '更新时间',
   primary key (id)
)ENGINE=InnoDB AUTO_INCREMENT=100032 DEFAULT CHARSET=UTF8;

alter table mmall_category comment '分类表';

/*==============================================================*/
/* Table: mmall_order                                           */
/*==============================================================*/
create table mmall_order
(
   id                   int(11) not null AUTO_INCREMENT comment '订单id',
   order_no             bigint(20) DEFAULT NULL comment '订单号',
   user_id              int(11) DEFAULT NULL comment '用户id',
   shipping_id          int(11) DEFAULT null comment '收货地址id',
   payment              decimal(20,2) DEFAULT NULL comment '实际支付金额',
   payment_type         int(4) DEFAULT NULL comment '支付类型 1-在线支付',
   postage              int(10) DEFAULT NULL comment '运费 单位元',
   status               int(10) DEFAULT NULL comment '订单状态:0-已取消 10-未支付 20-已付款 40-已发货 50-交易成功 60-交易关闭',
   payment_time         datetime DEFAULT NULL comment '支付时间',
   send_time            datetime DEFAULT NULL comment '发货时间',
   end_time             datetime DEFAULT NULL comment '交易完成时间',
   close_time           datetime DEFAULT NULL comment '交易关闭时间',
   create_time          datetime,
   update_time          datetime,
   primary key (id),
   UNIQUE KEY `order_no_index` (order_no) USING BTREE
)ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=UTF8;

alter table mmall_order comment '订单表';

/*==============================================================*/
/* Table: mmall_order_item                                      */
/*==============================================================*/
create table mmall_order_item
(
   id                   int(11) not null AUTO_INCREMENT comment '订单子表id',
   user_id              int(11) DEFAULT NULL,
   order_no             bigint(20) DEFAULT NULL,
   product_id           int(11) DEFAULT NULL,
   product_name         varchar(100) DEFAULT NULL,
   product_image        varchar(500) DEFAULT NULL,
   current_unit_price   decimal(20,2) DEFAULT null comment '生成订单时的商品价格',
   quantity             int(10) DEFAULT NULL comment '商品数量',
   total_price          decimal(20,2) DEFAULT NULL,
   create_time          datetime DEFAULT NULL,
   update_time          datetime DEFAULT NULL,
   primary key (id),
   KEY `order_no_index` (order_no) USING BTREE,
   KEY `order_no_user_id_index` (order_no,user_id) USING BTREE
)ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=UTF8;

alter table mmall_order_item comment '订单明细表';

/*==============================================================*/
/* Table: mmall_pay_info                                        */
/*==============================================================*/
create table mmall_pay_info
(
   id                   int(11) not null AUTO_INCREMENT,
   user_id              int(11) DEFAULT NULL comment '用户id',
   order_no             bigint(20) DEFAULT NULL comment '订单号',
   pay_platform         int(10) DEFAULT NULL comment '支付平台：1-支付宝，2-微信',
   platform_mumber      varchar(200) DEFAULT NULL comment '支付宝支付流水号',
   platform_status      varchar(20) DEFAULT NULL comment '支付宝支付状态',
   create_time          datetime DEFAULT NULL comment '创建时间',
   update_time          datetime DEFAULT NULL comment '更新时间',
   primary key (id)
)ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=UTF8;

alter table mmall_pay_info comment '支付信息表';
/*==============================================================*/
/* Table: mmall_product                                         */
/*==============================================================*/
create table mmall_product
(
   id                   int(11) not null AUTO_INCREMENT comment '商品id',
   category_id          int(11) not null comment '分类id',
   name                 varchar(50) not null comment '商品名称',
   subtitle             varchar(200) DEFAULT null comment '商品副标题',
   main_image           varchar(500) DEFAULT NULL comment '商品主图，url相对地址',
   sub_images           text comment '图片地址，json格式，扩展用',
   detail               text comment '商品详情',
   price                decimal(20,2) not null comment '价格，单位元保留两位小数',
   stock                int(11) not null comment '库存数量',
   status               int(6) DEFAULT 1 comment '商品状态,1-在售 2-下架 3-删除',
   create_time          datetime DEFAULT NULL comment '创建时间',
   update_time             datetime DEFAULT NULL comment '更新时间',
   primary key (id)
)ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=UTF8;

alter table mmall_product comment '产品表';

/*==============================================================*/
/* Table: mmall_shipping                                        */
/*==============================================================*/
create table mmall_shipping
(
   id                   int(11) not null AUTO_INCREMENT,
   user_id              int(11) DEFAULT NULL,
   receiver_name        varchar(20) DEFAULT NULL,
   receiver_phone       varchar(20) DEFAULT NULL,
   receiver_mobile      varchar(20) DEFAULT NULL,
   receiver_province    varchar(20) DEFAULT NULL,
   receiver_city        varchar(20) DEFAULT NULL,
   receiver_district    varchar(20) DEFAULT NULL,
   receiver_address     varchar(200) DEFAULT NULL,
   receiver_zip        varchar(6) DEFAULT NULL comment '邮编',
   create_time          datetime DEFAULT NULL,
   update_time          datetime DEFAULT NULL,
   primary key (id)
)ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=UTF8;

alter table mmall_shipping comment '收货地址表';

/*==============================================================*/
/* Table: mmall_user                                            */
/*==============================================================*/
create table mmall_user
(
   id                   int(11) not null AUTO_INCREMENT comment '用户表id',
   username             varchar(50) not null comment '用户名',
   password             varchar(50) not null comment '用户密码，MD5加密',
   email                varchar(50) DEFAULT NULL,
   phone                varchar(20) DEFAULT NULL,
   question             varchar(100) DEFAULT NULL comment '找回密码问题',
   answer               varchar(100) DEFAULT NULL comment '找回密码答案',
   role                 int(4) not null comment '角色0-管理员，1-普通用户',
   create_time          datetime not null comment '创建时间',
   update_time          datetime not null comment '最后一次更新时间',
   primary key (id),
   UNIQUE KEY `user_name_unique` (username) USING BTREE
)ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=UTF8;

alter table mmall_user comment '用户表';
