INSERT t_operator(id, name, address, create_time, storehouse_id, region_id, type) SELECT 1,'华强','深圳', now(), 1, 440300, 1 FROM dual WHERE not EXISTS (select 1 from t_operator where t_operator.id = 1);
INSERT t_device_storehouse(id,name,create_time, store_level,operator_id, region_id, address) SELECT 1,'华强仓库', now(), 1, 1, 440300,'陕西西安' FROM dual WHERE not EXISTS (select 1 from t_device_storehouse where t_device_storehouse.id = 1);
INSERT t_device_manufactor(id,name) SELECT 1,'华强技术' FROM dual WHERE not EXISTS (select 1 from t_device_manufactor where t_device_manufactor.id = 1);
INSERT t_iot_type(id,name) SELECT 1,'onenet' FROM dual WHERE not EXISTS (select 1 from t_iot_type where t_iot_type.id = 1);
INSERT t_iot_type(id,name) SELECT 2,'iot' FROM dual WHERE not EXISTS (select 1 from t_iot_type where t_iot_type.id = 2);
INSERT t_iot_type(id,name) SELECT 3,'cucc' FROM dual WHERE not EXISTS (select 1 from t_iot_type where t_iot_type.id = 3);


INSERT INTO `t_electrmobile_type` VALUES (1,NULL,'2018-10-17 14:45:44',NULL,'2018-10-17 14:45:44','电动自行车'),
                                         (2,NULL,'2018-10-17 14:45:52',NULL,'2018-10-17 14:45:52','踏板自行车'),
                                         (3,NULL,'2018-10-17 14:46:09',NULL,'2018-10-17 14:46:09','电动三轮车'),
                                         (4,NULL,'2018-10-17 14:46:25',NULL,'2018-10-17 14:46:25','踏板摩托车'),
                                         (5,NULL,'2018-10-17 14:46:45',NULL,'2018-10-17 14:46:45','摩托车'),
                                         (6,NULL,'2018-10-31 11:27:32',NULL,'2018-10-31 11:27:32','电动三摩'),
                                         (7,NULL,'2018-10-31 11:28:06',NULL,'2018-10-31 11:28:06','折叠电动车'),
                                         (8,NULL,'2018-11-01 11:30:59',NULL,'2018-11-01 11:30:59','电动四轮车'),
                                         (9,NULL,'2018-11-01 11:30:45',NULL,'2018-11-15 18:14:40','其他'),
                                         (10,NULL,'2019-08-14 10:22:20',NULL,'2019-08-14 10:22:20','巴伦');
