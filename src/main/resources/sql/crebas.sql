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
   product_id           int(11) DEFAULT NULL comment '��Ʒid',
   quantity             int(11) DEFAULT NULL comment '����',
   checked              int(11) DEFAULT NULL comment '�Ƿ�ѡ��1=��ѡ��0=δѡ��',
   create_time          datetime DEFAULT NULL comment '����ʱ��',
   update_time          datetime DEFAULT NULL comment '����ʱ��',
   primary key (id),
   KEY `user_id_index` (user_id) USING BTREE
)ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=UTF8;

alter table mmall_cart comment '���ﳵ��';

/*==============================================================*/
/* Table: mmall_category                                        */
/*==============================================================*/
create table mmall_category
(
   id                   int(11) not null AUTO_INCREMENT comment '���id',
   parent_id            int(11) DEFAULT null comment '�����id',
   name                 varchar(50) DEFAULT NULL comment '�������',
   status               tinyint(1) DEFAULT 1 comment '���״̬1-������2-�ѷ���',
   sort_order           int(4) DEFAULT NULL comment '�����ţ�ͬ��չʾ˳����ֵ�������Ȼ����',
   create_time          datetime DEFAULT NULL comment '����ʱ��',
   update_time          datetime DEFAULT NULL comment '����ʱ��',
   primary key (id)
)ENGINE=InnoDB AUTO_INCREMENT=100032 DEFAULT CHARSET=UTF8;

alter table mmall_category comment '�����';

/*==============================================================*/
/* Table: mmall_order                                           */
/*==============================================================*/
create table mmall_order
(
   id                   int(11) not null AUTO_INCREMENT comment '����id',
   order_no             bigint(20) DEFAULT NULL comment '������',
   user_id              int(11) DEFAULT NULL comment '�û�id',
   shipping_id          int(11) DEFAULT null comment '�ջ���ַid',
   payment              decimal(20,2) DEFAULT NULL comment 'ʵ��֧�����',
   payment_type         int(4) DEFAULT NULL comment '֧������ 1-����֧��',
   postage              int(10) DEFAULT NULL comment '�˷� ��λԪ',
   status               int(10) DEFAULT NULL comment '����״̬:0-��ȡ�� 10-δ֧�� 20-�Ѹ��� 40-�ѷ��� 50-���׳ɹ� 60-���׹ر�',
   payment_time         datetime DEFAULT NULL comment '֧��ʱ��',
   send_time            datetime DEFAULT NULL comment '����ʱ��',
   end_time             datetime DEFAULT NULL comment '�������ʱ��',
   close_time           datetime DEFAULT NULL comment '���׹ر�ʱ��',
   create_time          datetime,
   update_time          datetime,
   primary key (id),
   UNIQUE KEY `order_no_index` (order_no) USING BTREE
)ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=UTF8;

alter table mmall_order comment '������';

/*==============================================================*/
/* Table: mmall_order_item                                      */
/*==============================================================*/
create table mmall_order_item
(
   id                   int(11) not null AUTO_INCREMENT comment '�����ӱ�id',
   user_id              int(11) DEFAULT NULL,
   order_no             bigint(20) DEFAULT NULL,
   product_id           int(11) DEFAULT NULL,
   product_name         varchar(100) DEFAULT NULL,
   product_image        varchar(500) DEFAULT NULL,
   current_unit_price   decimal(20,2) DEFAULT null comment '���ɶ���ʱ����Ʒ�۸�',
   quantity             int(10) DEFAULT NULL comment '��Ʒ����',
   total_price          decimal(20,2) DEFAULT NULL,
   create_time          datetime DEFAULT NULL,
   update_time          datetime DEFAULT NULL,
   primary key (id),
   KEY `order_no_index` (order_no) USING BTREE,
   KEY `order_no_user_id_index` (order_no,user_id) USING BTREE
)ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=UTF8;

alter table mmall_order_item comment '������ϸ��';

/*==============================================================*/
/* Table: mmall_pay_info                                        */
/*==============================================================*/
create table mmall_pay_info
(
   id                   int(11) not null AUTO_INCREMENT,
   user_id              int(11) DEFAULT NULL comment '�û�id',
   order_no             bigint(20) DEFAULT NULL comment '������',
   pay_platform         int(10) DEFAULT NULL comment '֧��ƽ̨��1-֧������2-΢��',
   platform_mumber      varchar(200) DEFAULT NULL comment '֧����֧����ˮ��',
   platform_status      varchar(20) DEFAULT NULL comment '֧����֧��״̬',
   create_time          datetime DEFAULT NULL comment '����ʱ��',
   update_time          datetime DEFAULT NULL comment '����ʱ��',
   primary key (id)
)ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=UTF8;

alter table mmall_pay_info comment '֧����Ϣ��';
/*==============================================================*/
/* Table: mmall_product                                         */
/*==============================================================*/
create table mmall_product
(
   id                   int(11) not null AUTO_INCREMENT comment '��Ʒid',
   category_id          int(11) not null comment '����id',
   name                 varchar(50) not null comment '��Ʒ����',
   subtitle             varchar(200) DEFAULT null comment '��Ʒ������',
   main_image           varchar(500) DEFAULT NULL comment '��Ʒ��ͼ��url��Ե�ַ',
   sub_images           text comment 'ͼƬ��ַ��json��ʽ����չ��',
   detail               text comment '��Ʒ����',
   price                decimal(20,2) not null comment '�۸񣬵�λԪ������λС��',
   stock                int(11) not null comment '�������',
   status               int(6) DEFAULT 1 comment '��Ʒ״̬,1-���� 2-�¼� 3-ɾ��',
   create_time          datetime DEFAULT NULL comment '����ʱ��',
   update_time             datetime DEFAULT NULL comment '����ʱ��',
   primary key (id)
)ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=UTF8;

alter table mmall_product comment '��Ʒ��';

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
   receiver_zip        varchar(6) DEFAULT NULL comment '�ʱ�',
   create_time          datetime DEFAULT NULL,
   update_time          datetime DEFAULT NULL,
   primary key (id)
)ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=UTF8;

alter table mmall_shipping comment '�ջ���ַ��';

/*==============================================================*/
/* Table: mmall_user                                            */
/*==============================================================*/
create table mmall_user
(
   id                   int(11) not null AUTO_INCREMENT comment '�û���id',
   username             varchar(50) not null comment '�û���',
   password             varchar(50) not null comment '�û����룬MD5����',
   email                varchar(50) DEFAULT NULL,
   phone                varchar(20) DEFAULT NULL,
   question             varchar(100) DEFAULT NULL comment '�һ���������',
   answer               varchar(100) DEFAULT NULL comment '�һ������',
   role                 int(4) not null comment '��ɫ0-����Ա��1-��ͨ�û�',
   create_time          datetime not null comment '����ʱ��',
   update_time          datetime not null comment '���һ�θ���ʱ��',
   primary key (id),
   UNIQUE KEY `user_name_unique` (username) USING BTREE
)ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=UTF8;

alter table mmall_user comment '�û���';
