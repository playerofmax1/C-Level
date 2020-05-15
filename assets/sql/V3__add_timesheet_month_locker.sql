--
-- Table structure for table `wrk_timesheet_lock_month_lock`
--

DROP TABLE IF EXISTS `wrk_timesheet_lock_month_lock`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `wrk_timesheet_lock`
(
    `id`         bigint(20)  NOT NULL AUTO_INCREMENT,
    `userId`     bigint(20)  NOT NULL,
    `startDate`  datetime(6) NOT NULL,
    `endDate`    datetime(6) NOT NULL,

    `createDate` datetime(6) DEFAULT NULL,
    `modifyDate` datetime(6) DEFAULT NULL,
    `version`    bigint(20)  NOT NULL,
    `createBy`   bigint(20)  NOT NULL,
    `modifyBy`   bigint(20)  NOT NULL,

    PRIMARY KEY (`id`),
    CONSTRAINT `FKTSLOCK_USER` FOREIGN KEY (`userId`) REFERENCES `wrk_user` (`id`),
    CONSTRAINT `FKTSLOCK_CREATE` FOREIGN KEY (`createBy`) REFERENCES `wrk_user` (`id`),
    CONSTRAINT `FKTSLOCK_MODIFY` FOREIGN KEY (`modifyBy`) REFERENCES `wrk_user` (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wrk_timesheet_lock`
--
INSERT INTO wrk_timesheet_lock (id, userId, startDate, endDate, createDate, modifyDate, version, createBy, modifyBy)
VALUES (1, 15, '2020-02-01 00:00:00.000000', '2020-02-29 00:00:00.000000', '2020-02-15 00:00:00.000000', '2020-02-15 00:00:00.000000', 1, 15, 15);
