CREATE TABLE if not exists extended_data_{appName} (
  call_id integer NOT NULL COMMENT '方法调用序号',
  data_type varchar(30) NOT NULL COMMENT '数据类型，MB_SQL: Mybatis的Mapper的数据库操作及表名',
  data_value text NOT NULL COMMENT '数据内容，JSON字符串格式',
  PRIMARY KEY (call_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='自定义数据表';