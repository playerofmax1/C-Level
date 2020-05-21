--
-- Table structure for table `wrk_performance_year`
--

DROP TABLE IF EXISTS `wrk_performance_year`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `wrk_performance_year`
(
    `id`         bigint(20)  NOT NULL AUTO_INCREMENT,
    `year`       int(4)      NOT NULL,
    `startDate`  datetime(6) NOT NULL,
    `endDate`    datetime(6) NOT NULL,

    `createDate` datetime(6) DEFAULT NULL,
    `modifyDate` datetime(6) DEFAULT NULL,
    `version`    bigint(20)  NOT NULL,
    `createBy`   bigint(20)  NOT NULL,
    `modifyBy`   bigint(20)  NOT NULL,

    PRIMARY KEY (`id`),
    CONSTRAINT `FKPFYEAR_CREATE` FOREIGN KEY (`createBy`) REFERENCES `wrk_user` (`id`),
    CONSTRAINT `FKPFYEAR_MODIFY` FOREIGN KEY (`modifyBy`) REFERENCES `wrk_user` (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wrk_performance_year`
--
INSERT INTO wrk_performance_year (id, year, startDate, endDate, createDate, modifyDate, version, createBy, modifyBy) VALUES (1, 2020, '2019-11-01 00:00:00.000000', '2020-10-31 00:00:00.000000', '2020-05-20 09:46:45.000000', '2020-05-20 09:46:45.000000', 1, 15, 15);
INSERT INTO wrk_performance_year (id, year, startDate, endDate, createDate, modifyDate, version, createBy, modifyBy) VALUES (2, 2021, '2020-11-01 00:00:00.000000', '2021-10-31 00:00:00.000000', '2020-05-20 09:46:45.000000', '2020-05-20 09:46:45.000000', 1, 15, 15);
INSERT INTO wrk_performance_year (id, year, startDate, endDate, createDate, modifyDate, version, createBy, modifyBy) VALUES (3, 2022, '2021-11-01 00:00:00.000000', '2022-10-31 00:00:00.000000', '2020-05-20 09:46:45.000000', '2020-05-20 09:46:45.000000', 1, 15, 15);
INSERT INTO wrk_performance_year (id, year, startDate, endDate, createDate, modifyDate, version, createBy, modifyBy) VALUES (4, 2023, '2022-11-01 00:00:00.000000', '2023-10-31 00:00:00.000000', '2020-05-20 09:46:45.000000', '2020-05-20 09:46:45.000000', 1, 15, 15);
INSERT INTO wrk_performance_year (id, year, startDate, endDate, createDate, modifyDate, version, createBy, modifyBy) VALUES (5, 2024, '2023-11-01 00:00:00.000000', '2024-10-31 00:00:00.000000', '2020-05-20 09:46:45.000000', '2020-05-20 09:46:45.000000', 1, 15, 15);
INSERT INTO wrk_performance_year (id, year, startDate, endDate, createDate, modifyDate, version, createBy, modifyBy) VALUES (6, 2025, '2024-11-01 00:00:00.000000', '2025-10-31 00:00:00.000000', '2020-05-20 09:46:45.000000', '2020-05-20 09:46:45.000000', 1, 15, 15);
INSERT INTO wrk_performance_year (id, year, startDate, endDate, createDate, modifyDate, version, createBy, modifyBy) VALUES (7, 2026, '2025-11-01 00:00:00.000000', '2026-10-31 00:00:00.000000', '2020-05-20 09:46:45.000000', '2020-05-20 09:46:45.000000', 1, 15, 15);
INSERT INTO wrk_performance_year (id, year, startDate, endDate, createDate, modifyDate, version, createBy, modifyBy) VALUES (8, 2027, '2026-11-01 00:00:00.000000', '2027-10-31 00:00:00.000000', '2020-05-20 09:46:45.000000', '2020-05-20 09:46:45.000000', 1, 15, 15);
INSERT INTO wrk_performance_year (id, year, startDate, endDate, createDate, modifyDate, version, createBy, modifyBy) VALUES (9, 2028, '2027-11-01 00:00:00.000000', '2028-10-31 00:00:00.000000', '2020-05-20 09:46:45.000000', '2020-05-20 09:46:45.000000', 1, 15, 15);
INSERT INTO wrk_performance_year (id, year, startDate, endDate, createDate, modifyDate, version, createBy, modifyBy) VALUES (10, 2029, '2028-11-01 00:00:00.000000', '2029-10-31 00:00:00.000000', '2020-05-20 09:46:45.000000', '2020-05-20 09:46:45.000000', 1, 15, 15);
INSERT INTO wrk_performance_year (id, year, startDate, endDate, createDate, modifyDate, version, createBy, modifyBy) VALUES (11, 2030, '2029-11-01 00:00:00.000000', '2030-10-31 00:00:00.000000', '2020-05-20 09:46:45.000000', '2020-05-20 09:46:45.000000', 1, 15, 15);

/*Current year need table to store it, now can use SystemConfig table as temporary, when has EOD/EOM/EOY processes will be need table look like EOD table*/
INSERT INTO sys_config (id, description, name, value) VALUES (11, 'Current Performance Year (Christian)', 'app.pf.year', '2020');

--
-- Table structure for table `vw_rpt_mandays`
--
CREATE OR REPLACE VIEW vw_rpt_mandays AS (
    select ts.userId,

           pjt.id                                           as projectId,
           pfy.year                                         as workYear,

           sum(ts.chargeMinute)                             as chargeMinutes,
           sum(ts.chargeMinute) / 60                        as chargeHours,
           sum(ts.chargeMinute) / 480                       as chargeDays,
           count(ts.workDate)                               as workDays,

           min(ts.workDate)                                 as firstChargeDate,
           max(ts.workDate)                                 as lastChargeDate,
           DATEDIFF(max(ts.workDate), min(ts.workDate)) + 1 as firstToLastDays

    from wrk_timesheet ts
             left join wrk_project pjt on (ts.projectId = pjt.id)
             left join wrk_performance_year pfy on (ts.workDate between pfy.startDate and pfy.endDate)

    where chargeMinute > 0

    group by userId, workYear, projectId
);
