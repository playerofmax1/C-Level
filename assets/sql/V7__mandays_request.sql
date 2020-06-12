--
-- Table structure for table `wrk_mandays_request`
--

DROP TABLE IF EXISTS `wrk_mandays_request`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `wrk_mandays_request`
(
    `id`               bigint(20)     NOT NULL AUTO_INCREMENT,
    `prjTaskId`        bigint(20)     NULL,
    `status`           varchar(10)    NOT NULL,
    `projectId`        bigint(20)     NULL,
    `taskId`           bigint(20)     NULL,
    `userId`           bigint         NOT NULL,

    `extendMD`         decimal(19, 2) NULL,
    `extendMDDuration` varchar(255)   NULL,
    `extendMDMinute`   bigint         NULL,
    `description`      varchar(255)   NULL,
    `comment`          varchar(1000)  NULL,
    `amdCalculation`   bit(1)         NULL,

    `createDate`       datetime(6) DEFAULT NULL,
    `modifyDate`       datetime(6) DEFAULT NULL,
    `version`          bigint(20)     NOT NULL,
    `createBy`         bigint(20)     NOT NULL,
    `modifyBy`         bigint(20)     NOT NULL,

    PRIMARY KEY (`id`),
    CONSTRAINT `FKMDRQ_CREATE` FOREIGN KEY (`createBy`) REFERENCES `wrk_user` (`id`),
    CONSTRAINT `FKMDRQ_MODIFY` FOREIGN KEY (`modifyBy`) REFERENCES `wrk_user` (`id`),
    CONSTRAINT `FKMDRQ_PROJECT` FOREIGN KEY (`projectId`) REFERENCES `wrk_project` (`id`),
    CONSTRAINT `FKMDRQ_TASK` FOREIGN KEY (`taskId`) REFERENCES `wrk_task` (`id`),
    CONSTRAINT `FKMDRQ_PRJTASK` FOREIGN KEY (`prjTaskId`) REFERENCES `wrk_prj_task` (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wrk_mandays_request`
--
