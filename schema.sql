-- ============================================================
-- CMP「码农天堂」数据库脚本
-- 依据：src/mvc/persistence/Impl/*.java 中的 SQL 语句
--      + ResultSet 读取的列名反推（非原始建表语句，属逆向还原）
--
-- 用法一（本地）：DBeaver 连接 MySQL → 打开本文件 → 全选执行
-- 用法二（Docker）：由 MySQL 容器在首次启动时自动执行
--   （挂载到 /docker-entrypoint-initdb.d/，仅在数据目录为空时运行）
--
-- 注意：库名 cmp_2，与 DBUtil.java 中的连接串一致。
-- 本脚本不删库，只删表重建；如需彻底重来请手动 DROP DATABASE。
-- ============================================================

CREATE DATABASE IF NOT EXISTS cmp_2 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE cmp_2;

-- 1. 商品分类
CREATE TABLE category (
  catid   VARCHAR(10)  NOT NULL COMMENT '分类ID',
  name    VARCHAR(80)  NOT NULL COMMENT '分类名称',
  descn   VARCHAR(255)          COMMENT '分类描述',
  PRIMARY KEY (catid)
) COMMENT='商品分类';

-- 2. 商品（分类下的具体商品）
CREATE TABLE product (
  productid VARCHAR(10)  NOT NULL COMMENT '商品ID',
  category  VARCHAR(10)  NOT NULL COMMENT '所属分类ID',
  name      VARCHAR(80)  NOT NULL COMMENT '商品名称',
  descn     VARCHAR(255)          COMMENT '商品描述',
  PRIMARY KEY (productid),
  KEY idx_product_category (category)
) COMMENT='商品';

-- 3. 商品条目（商品的具体规格/SKU）
CREATE TABLE item (
  itemid    VARCHAR(10)   NOT NULL COMMENT '条目ID',
  productid VARCHAR(10)   NOT NULL COMMENT '所属商品ID',
  listprice DECIMAL(10,2)          COMMENT '售价',
  unitcost  DECIMAL(10,2)          COMMENT '成本价',
  supplier  INT                    COMMENT '供应商编号',
  status    VARCHAR(2)             COMMENT '状态',
  attr1     VARCHAR(80)            COMMENT '属性1',
  attr2     VARCHAR(80)            COMMENT '属性2',
  attr3     VARCHAR(80)            COMMENT '属性3',
  attr4     VARCHAR(80)            COMMENT '属性4',
  attr5     VARCHAR(80)            COMMENT '属性5',
  qty       INT          NOT NULL DEFAULT 100 COMMENT '库存数量',
  PRIMARY KEY (itemid),
  KEY idx_item_product (productid)
) COMMENT='商品条目';

-- 4. 账号资料
CREATE TABLE account (
  userid      VARCHAR(80) NOT NULL COMMENT '用户ID',
  virtualname VARCHAR(80)          COMMENT '昵称',
  birthday    DATE                 COMMENT '生日',
  email       VARCHAR(80)          COMMENT '邮箱',
  phone       VARCHAR(20)          COMMENT '手机号',
  avatar      VARCHAR(255)         COMMENT '头像路径',
  PRIMARY KEY (userid)
) COMMENT='账号资料';

-- 5. 登录凭证
CREATE TABLE signon (
  username VARCHAR(80) NOT NULL COMMENT '用户名',
  password VARCHAR(80) NOT NULL COMMENT '密码',
  PRIMARY KEY (username)
) COMMENT='登录凭证';

-- 6. 收货地址
CREATE TABLE receiver (
  userid         VARCHAR(80) NOT NULL COMMENT '用户ID',
  receiver_name  VARCHAR(50) NOT NULL COMMENT '收货人',
  receiver_phone VARCHAR(20) NOT NULL COMMENT '联系电话',
  receiver_addr  VARCHAR(255) NOT NULL COMMENT '收货地址',
  KEY idx_receiver_user (userid)
) COMMENT='收货地址';

-- 7. 购物车（一个用户一个车）
CREATE TABLE cart (
  cart_id  INT         NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  username VARCHAR(80) NOT NULL COMMENT '所属用户',
  PRIMARY KEY (cart_id),
  KEY idx_cart_username (username)
) COMMENT='购物车';

-- 8. 购物车明细
CREATE TABLE cart_item (
  cart_id    INT           NOT NULL COMMENT '购物车ID',
  item_id    VARCHAR(10)            COMMENT '条目ID',
  item_name  VARCHAR(80)            COMMENT '条目名称（冗余）',
  item_price DECIMAL(10,2)          COMMENT '单价',
  quantity   INT           NOT NULL DEFAULT 1 COMMENT '数量',
  KEY idx_cartitem_cart (cart_id),
  KEY idx_cartitem_item (item_id)
) COMMENT='购物车明细';

-- 9. 订单主表
CREATE TABLE orders (
  orderId      INT          NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  userId       VARCHAR(80)  NOT NULL COMMENT '下单用户',
  orderName    VARCHAR(50)           COMMENT '收货人',
  orderDate    DATETIME              COMMENT '下单时间',
  orderAddress VARCHAR(255)          COMMENT '收货地址',
  orderTel     VARCHAR(20)           COMMENT '联系电话',
  PRIMARY KEY (orderId),
  KEY idx_orders_user (userId)
) COMMENT='订单主表';

-- 10. 订单明细
CREATE TABLE order_items (
  orderId  INT           NOT NULL COMMENT '订单ID',
  itemId   VARCHAR(10)   NOT NULL COMMENT '条目ID',
  quantity INT           NOT NULL COMMENT '数量',
  price    DECIMAL(10,2)          COMMENT '成交单价',
  username VARCHAR(80)            COMMENT '所属用户',
  KEY idx_orderitems_order (orderId)
) COMMENT='订单明细';

-- 11. 操作日志
CREATE TABLE user_log (
  logId       INT          NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  user_id     VARCHAR(80)           COMMENT '操作用户',
  action      VARCHAR(50)           COMMENT '操作类型',
  action_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  item_id     VARCHAR(10)           COMMENT '关联条目（可空）',
  cart_id     INT                   COMMENT '关联购物车（可空）',
  order_id    INT                   COMMENT '关联订单（可空）',
  details     VARCHAR(500)          COMMENT '操作详情',
  PRIMARY KEY (logId),
  KEY idx_log_user (user_id)
) COMMENT='操作日志';

-- ============================================================
-- 最小可用种子数据（跑通浏览→加购→下单链路）
-- ============================================================

INSERT INTO category VALUES
('FISH','鱼类','各种观赏鱼'),
('DOGS','狗类','各种犬类'),
('CATS','猫类','各种猫类'),
('BIRDS','鸟类','各种鸣禽与观赏鸟'),
('REPTILES','爬行类','蜥蜴、蛇等爬行动物');

INSERT INTO product VALUES
('FI-SW-01','FISH','天使鱼','来自深海的观赏鱼'),
('FI-SW-02','FISH','虎皮鱼','活泼好动'),
('K9-DL-01','DOGS','斑点狗','忠诚热情'),
('K9-RT-01','DOGS','金毛','性格温和'),
('FL-DSH-01','CATS','短毛猫','安静亲人'),
('AV-CB-01','BIRDS','亚马逊鹦鹉','善于模仿人声'),
('AV-SB-02','BIRDS','黄喉雀','叫声清脆'),
('RP-LI-02','REPTILES','绿鬣蜥','需要较大饲养箱'),
('RP-SN-01','REPTILES','玉米蛇','温顺易饲养');

INSERT INTO item
(itemid, productid, listprice, unitcost, supplier, status, attr1, attr2, attr3, attr4, attr5, qty) VALUES
('EST-1','FI-SW-01',16.50,10.00,1,'P','大型','水族箱','天使鱼 大型',NULL,NULL,120),
('EST-2','FI-SW-01',16.50,10.00,1,'P','小型','水族箱','天使鱼 小型',NULL,NULL,150),
('EST-3','FI-SW-02',18.50,12.00,1,'P','大型','水族箱','虎皮鱼 大型',NULL,NULL,80),
('EST-4','K9-DL-01',18.50,12.00,1,'P','成年','雄性','斑点狗 成年雄性',NULL,NULL,60),
('EST-5','K9-RT-01',18.50,12.00,2,'P','幼年','雌性','金毛 幼年雌性',NULL,NULL,45),
('EST-6','FL-DSH-01',18.50,12.00,3,'P','成年','雄性','短毛猫 成年雄性',NULL,NULL,70),
('EST-7','FL-DSH-01',20.00,13.00,3,'P','幼年','雌性','短毛猫 幼年雌性',NULL,NULL,55),
('EST-8','AV-CB-01',193.50,92.00,4,'P','成年','雄性','亚马逊鹦鹉 成年雄性',NULL,NULL,20),
('EST-9','AV-SB-02',193.50,92.00,4,'P','成年','雌性','黄喉雀 成年雌性',NULL,NULL,35),
('EST-10','RP-LI-02',22.00,14.00,5,'P','成年','雄性','绿鬣蜥 成年雄性',NULL,NULL,25),
('EST-11','RP-SN-01',18.50,12.00,5,'P','成年','雌性','玉米蛇 成年雌性',NULL,NULL,30);

-- 测试账号：test / 123456
INSERT INTO signon VALUES ('test','123456');
INSERT INTO account VALUES ('test','测试用户','2004-11-06','test@example.com','13800000000',NULL);
INSERT INTO receiver VALUES ('test','张三','13800000000','湖南省长沙市岳麓区中南大学');
